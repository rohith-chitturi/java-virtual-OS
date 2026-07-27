package com.rohith.javavirtualos.kernel.device;

public class DeviceValidator {

    public void validateRegistration(DeviceDriver newDriver, DeviceRegistry registry) throws IllegalArgumentException {
        if (newDriver == null || newDriver.getDescriptor() == null) {
            throw new IllegalArgumentException("Driver or descriptor cannot be null");
        }
        
        DeviceDescriptor newDesc = newDriver.getDescriptor();
        
        if (newDesc.getName() == null || newDesc.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Device name cannot be empty");
        }
        
        if (newDesc.getCapabilities() == null || newDesc.getCapabilities().isEmpty()) {
            throw new IllegalArgumentException("Device must have at least one capability");
        }
        
        if (registry.getDriver(newDesc.getName()) != null) {
            throw new IllegalArgumentException("Device name collision: " + newDesc.getName());
        }
    }
}
