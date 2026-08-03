package com.rohith.javavirtualos.kernel.core;

import java.util.List;

/**
 * Represents a processing unit which could be a single core or a multi-core processor.
 */
public interface Processor {
    
    /**
     * Gets all CPU cores managed by this processor.
     * @return A list of CPUs.
     */
    List<CPU> getCores();
    
    /**
     * Returns the total number of cores.
     * @return Number of cores.
     */
    int getCoreCount();
}
