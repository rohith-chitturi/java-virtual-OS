package com.rohith.javavirtualos.filesystem.storage;

import com.rohith.javavirtualos.filesystem.cache.CacheEntry;
import com.rohith.javavirtualos.filesystem.cache.PageCache;
import com.rohith.javavirtualos.filesystem.cache.PageCacheKey;
import com.rohith.javavirtualos.filesystem.model.Inode;
import com.rohith.javavirtualos.kernel.filesystem.jvfs.BlockAllocator;
import com.rohith.javavirtualos.kernel.filesystem.jvfs.JVFSBlockDevice;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultFileStorage implements FileStorage, PageCache.PageFlusher {

    public static final int PAGE_SIZE = 4096;

    private final PageCache pageCache;
    private final BlockAllocator blockAllocator;
    private final JVFSBlockDevice blockDevice;
    private final Map<Long, Inode> inodeMap;

    public DefaultFileStorage(BlockAllocator blockAllocator, JVFSBlockDevice blockDevice, int maxCachePages) {
        this.blockAllocator = blockAllocator;
        this.blockDevice = blockDevice;
        this.pageCache = new PageCache(this, maxCachePages);
        this.inodeMap = new ConcurrentHashMap<>();
    }

    public PageCache getPageCache() {
        return pageCache;
    }

    @Override
    public byte[] read(Inode inode, long offset, int length) throws Exception {
        inodeMap.putIfAbsent(inode.getInodeId(), inode);
        
        if (inode instanceof com.rohith.javavirtualos.filesystem.model.DeviceNode) {
            byte[] devData = ((com.rohith.javavirtualos.filesystem.model.DeviceNode) inode).readDeviceBytes();
            int readLen = Math.min(length, devData.length);
            byte[] result = new byte[readLen];
            System.arraycopy(devData, 0, result, 0, readLen);
            return result;
        }
        if (inode instanceof com.rohith.javavirtualos.filesystem.model.VirtualFileNode) {
            byte[] vData = ((com.rohith.javavirtualos.filesystem.model.VirtualFileNode) inode).generateContentBytes();
            if (offset >= vData.length) return new byte[0];
            int readLen = (int) Math.min(length, vData.length - offset);
            byte[] result = new byte[readLen];
            System.arraycopy(vData, (int) offset, result, 0, readLen);
            return result;
        }

        if (offset >= inode.calculateSize()) {
            return new byte[0]; // EOF
        }

        // Limit read to the end of the file
        int actualLength = (int) Math.min(length, inode.calculateSize() - offset);
        byte[] result = new byte[actualLength];
        int resultOffset = 0;

        long currentOffset = offset;
        int remainingLength = actualLength;

        while (remainingLength > 0) {
            int pageIndex = (int) (currentOffset / PAGE_SIZE);
            int offsetInPage = (int) (currentOffset % PAGE_SIZE);
            int bytesToReadFromPage = Math.min(PAGE_SIZE - offsetInPage, remainingLength);

            CacheEntry entry = getOrLoadPage(inode, pageIndex);
            
            System.arraycopy(entry.getData(), offsetInPage, result, resultOffset, bytesToReadFromPage);

            currentOffset += bytesToReadFromPage;
            resultOffset += bytesToReadFromPage;
            remainingLength -= bytesToReadFromPage;
        }

        return result;
    }

    @Override
    public void write(Inode inode, long offset, byte[] data) throws Exception {
        inodeMap.putIfAbsent(inode.getInodeId(), inode);
        
        if (inode instanceof com.rohith.javavirtualos.filesystem.model.DeviceNode) {
            ((com.rohith.javavirtualos.filesystem.model.DeviceNode) inode).writeDeviceBytes(data);
            return;
        }
        if (inode instanceof com.rohith.javavirtualos.filesystem.model.VirtualFileNode) {
            throw new Exception("Virtual files are read-only.");
        }

        if (data.length == 0) return;

        long currentOffset = offset;
        int dataOffset = 0;
        int remainingLength = data.length;

        while (remainingLength > 0) {
            int pageIndex = (int) (currentOffset / PAGE_SIZE);
            int offsetInPage = (int) (currentOffset % PAGE_SIZE);
            int bytesToWriteToPage = Math.min(PAGE_SIZE - offsetInPage, remainingLength);

            // Read-Modify-Write
            CacheEntry entry = getOrLoadPage(inode, pageIndex);
            
            System.arraycopy(data, dataOffset, entry.getData(), offsetInPage, bytesToWriteToPage);
            entry.setDirty(true);

            currentOffset += bytesToWriteToPage;
            dataOffset += bytesToWriteToPage;
            remainingLength -= bytesToWriteToPage;
        }

        long newSize = Math.max(inode.calculateSize(), offset + data.length);
        inode.getMetadata().setSize(newSize);
        inode.getMetadata().updateModified();
    }

    @Override
    public void flush(Inode inode) throws Exception {
        // Find all pages for this inode in the cache and flush them.
        // For simplicity, we just flush everything right now, or we could track pages per inode.
        // To be precise, we can iterate max page index based on file size.
        long size = inode.calculateSize();
        if (size == 0) return;
        
        int maxPageIndex = (int) ((size - 1) / PAGE_SIZE);
        for (int i = 0; i <= maxPageIndex; i++) {
            pageCache.flush(new PageCacheKey(inode.getInodeId(), i));
        }
    }

    @Override
    public void flushAll() throws Exception {
        pageCache.flushAll();
    }

    private CacheEntry getOrLoadPage(Inode inode, int pageIndex) throws Exception {
        PageCacheKey key = new PageCacheKey(inode.getInodeId(), pageIndex);
        CacheEntry entry = pageCache.get(key);

        if (entry == null) {
            byte[] data = new byte[PAGE_SIZE];
            Integer physicalBlock = inode.getPhysicalBlock(pageIndex);
            if (physicalBlock != null && physicalBlock >= 0) {
                // Load from disk
                byte[] diskData = blockDevice.readBlock(physicalBlock);
                if (diskData != null && diskData.length > 0) {
                    System.arraycopy(diskData, 0, data, 0, Math.min(PAGE_SIZE, diskData.length));
                }
            } else {
                // Not allocated yet. Zero-filled by default.
                // We don't allocate a physical block until we flush it (write-back delay) or we allocate now?
                // The user said: "Keep responsibilities separate: FileStorage -> BlockAllocator -> block number -> PageCache... Don't put allocation policy inside PageCache."
                // Wait, if we are reading a sparse region, we return zeroes. We don't need to allocate.
                // We will allocate on flush if dirty.
            }
            entry = new CacheEntry(data);
            pageCache.put(key, entry);
        }

        return entry;
    }

    @Override
    public void flushPage(PageCacheKey key, CacheEntry entry) throws Exception {
        Inode inode = inodeMap.get(key.getInodeId());
        if (inode == null) {
            throw new Exception("Cannot flush page for unknown Inode ID: " + key.getInodeId());
        }

        Integer physicalBlock = inode.getPhysicalBlock(key.getPageIndex());
        if (physicalBlock == null || physicalBlock < 0) {
            physicalBlock = blockAllocator.allocateBlock();
            if (physicalBlock < 0) {
                throw new Exception("Out of disk space!");
            }
            inode.setPhysicalBlock(key.getPageIndex(), physicalBlock);
        }

        blockDevice.writeBlock(physicalBlock, entry.getData());
    }
}
