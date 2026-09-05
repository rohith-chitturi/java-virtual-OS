package com.rohith.javavirtualos.kernel.filesystem.procfs;

import com.rohith.javavirtualos.filesystem.model.VirtualFileNode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProcFileSystemReadOnlyTest {

    @Test
    public void testProcFilesAreReadOnly() {
        VirtualFileNode meminfo = new VirtualFileNode("root", () -> "data");
        VirtualFileNode uptime = new VirtualFileNode("root", () -> "data");
        VirtualFileNode status = new VirtualFileNode("root", () -> "data");

        assertThrows(UnsupportedOperationException.class, () -> meminfo.setContent("new"));
        assertThrows(UnsupportedOperationException.class, () -> uptime.setContent("new"));
        assertThrows(UnsupportedOperationException.class, () -> status.setContent("new"));
    }
}
