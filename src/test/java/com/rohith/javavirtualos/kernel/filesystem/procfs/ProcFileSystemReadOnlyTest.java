package com.rohith.javavirtualos.kernel.filesystem.procfs;

import com.rohith.javavirtualos.filesystem.model.VirtualFileNode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProcFileSystemReadOnlyTest {

    @Test
    public void testProcFilesAreReadOnly() {
        VirtualFileNode meminfo = new VirtualFileNode("meminfo", "root", null, () -> "data");
        VirtualFileNode uptime = new VirtualFileNode("uptime", "root", null, () -> "data");
        VirtualFileNode status = new VirtualFileNode("status", "root", null, () -> "data");

        assertThrows(UnsupportedOperationException.class, () -> meminfo.setContent("new"));
        assertThrows(UnsupportedOperationException.class, () -> uptime.setContent("new"));
        assertThrows(UnsupportedOperationException.class, () -> status.setContent("new"));
    }
}
