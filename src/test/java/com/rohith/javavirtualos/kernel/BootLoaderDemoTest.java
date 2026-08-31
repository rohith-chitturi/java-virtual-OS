package com.rohith.javavirtualos.kernel;

import com.rohith.javavirtualos.filesystem.FileSystemManager;
import com.rohith.javavirtualos.filesystem.model.FileNode;
import com.rohith.javavirtualos.filesystem.model.Inode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BootLoaderDemoTest {

    @Test
    public void testDemosAreCreated() {
        BootLoader bootLoader = new BootLoader();
        Kernel kernel = bootLoader.boot();
        
        assertNotNull(kernel);
    }
}
