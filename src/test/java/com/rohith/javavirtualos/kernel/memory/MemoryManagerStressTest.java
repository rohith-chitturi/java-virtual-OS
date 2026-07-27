package com.rohith.javavirtualos.kernel.memory;

import com.rohith.javavirtualos.kernel.core.KernelTick;
import com.rohith.javavirtualos.kernel.events.KernelEventBus;
import com.rohith.javavirtualos.kernel.memory.strategy.BestFitStrategy;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MemoryManagerStressTest {

    @Test
    void testMemoryManagerStress() {
        KernelEventBus bus = new KernelEventBus();
        KernelTick tick = new KernelTick();
        
        MemoryManager manager = new MemoryManager(
                MemorySize.ofMB(1024),
                MemorySize.ofMB(128),
                new BestFitStrategy(),
                bus,
                tick
        );

        Random random = new Random(42);
        List<Integer> activeAllocations = new ArrayList<>();

        assertDoesNotThrow(() -> {
            for (int i = 0; i < 10000; i++) {
                if (activeAllocations.size() < 50 && random.nextBoolean()) {
                    try {
                        MemoryBlock block = manager.allocate(MemorySize.ofMB(random.nextInt(16) + 1), 100 + random.nextInt(10));
                        activeAllocations.add(block.getAllocationId());
                    } catch (RuntimeException e) {
                        // OOM is fine
                    }
                } else if (!activeAllocations.isEmpty()) {
                    int index = random.nextInt(activeAllocations.size());
                    int allocId = activeAllocations.remove(index);
                    manager.free(allocId);
                }
                tick.increment();
            }

            for (int allocId : activeAllocations) {
                manager.free(allocId);
            }
        });

        assertEquals(2, manager.getMemoryMap().getBlocks().size());
        assertEquals(MemorySize.ofMB(128), manager.getMemoryMap().getBlocks().get(0).getSize());
        assertEquals(MemoryState.RESERVED, manager.getMemoryMap().getBlocks().get(0).getState());
        
        assertEquals(MemorySize.ofMB(1024 - 128), manager.getMemoryMap().getBlocks().get(1).getSize());
        assertEquals(MemoryState.FREE, manager.getMemoryMap().getBlocks().get(1).getState());
        
        manager.getValidator().validate(manager.getMemoryMap(), MemorySize.ofMB(1024));
    }
}
