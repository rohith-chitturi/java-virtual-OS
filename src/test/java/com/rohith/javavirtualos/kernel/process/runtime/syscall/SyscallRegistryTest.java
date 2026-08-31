package com.rohith.javavirtualos.kernel.process.runtime.syscall;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SyscallRegistryTest {

    @Test
    public void testSyscallIdsAreUniqueAndStable() throws IllegalAccessException {
        Set<Integer> ids = new HashSet<>();
        
        Field[] fields = SystemCallDispatcher.class.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) && field.getType() == int.class) {
                if (field.getName().startsWith("SYS_")) {
                    int id = field.getInt(null);
                    assertTrue(ids.add(id), "Duplicate Syscall ID found for: " + field.getName() + " (ID: " + id + ")");
                }
            }
        }
        
        // Test stability of specific core syscalls
        assertEquals(1, SystemCallDispatcher.SYS_EXIT);
        assertEquals(4, SystemCallDispatcher.SYS_OPEN);
        assertEquals(5, SystemCallDispatcher.SYS_READ);
        assertEquals(6, SystemCallDispatcher.SYS_WRITE);
        assertEquals(10, SystemCallDispatcher.SYS_CLOSE);
    }
}
