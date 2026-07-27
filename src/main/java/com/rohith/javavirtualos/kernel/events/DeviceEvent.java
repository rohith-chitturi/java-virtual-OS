package com.rohith.javavirtualos.kernel.events;

import com.rohith.javavirtualos.kernel.device.DeviceDescriptor;

public abstract class DeviceEvent extends KernelEvent {
    
    protected final DeviceDescriptor descriptor;

    public DeviceEvent(DeviceDescriptor descriptor) {
        super();
        this.descriptor = descriptor;
    }
    
    public DeviceDescriptor getDescriptor() { return descriptor; }

    public static class DeviceRegisteredEvent extends DeviceEvent {
        public DeviceRegisteredEvent(DeviceDescriptor descriptor) { super(descriptor); }
        @Override
        public String getMessage() { return "Device registered: " + descriptor.getName(); }
    }

    public static class DeviceUnregisteredEvent extends DeviceEvent {
        public DeviceUnregisteredEvent(DeviceDescriptor descriptor) { super(descriptor); }
        @Override
        public String getMessage() { return "Device unregistered: " + descriptor.getName(); }
    }

    public static class DeviceMountedEvent extends DeviceEvent {
        public DeviceMountedEvent(DeviceDescriptor descriptor) { super(descriptor); }
        @Override
        public String getMessage() { return "Device mounted: " + descriptor.getName() + " at " + descriptor.getMountPath(); }
    }

    public static class DeviceReadEvent extends DeviceEvent {
        private final int bytes;
        public DeviceReadEvent(DeviceDescriptor descriptor, int bytes) { super(descriptor); this.bytes = bytes; }
        @Override
        public String getMessage() { return "Read " + bytes + " bytes from " + descriptor.getName(); }
    }

    public static class DeviceWriteEvent extends DeviceEvent {
        private final int bytes;
        public DeviceWriteEvent(DeviceDescriptor descriptor, int bytes) { super(descriptor); this.bytes = bytes; }
        @Override
        public String getMessage() { return "Wrote " + bytes + " bytes to " + descriptor.getName(); }
    }

    public static class DeviceFailureEvent extends DeviceEvent {
        private final String reason;
        public DeviceFailureEvent(DeviceDescriptor descriptor, String reason) { super(descriptor); this.reason = reason; }
        @Override
        public String getMessage() { return "Device failure: " + descriptor.getName() + " - " + reason; }
    }
}
