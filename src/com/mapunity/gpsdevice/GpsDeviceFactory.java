package com.mapunity.gpsdevice;

import com.mapunity.bluetooth.BluetoothGPSDeviceImpl;
import com.mapunity.bluetooth.Device;
import com.mapunity.tracker.controller.Controller;
import com.mapunity.tracker.view.Logger;

/**
 * Creates either a BluetoothGpsDevice or a JSR179Device
 * 
 * @author gareth
 * 
 */
public class GpsDeviceFactory {
    /**
     * Create one of the Device implementations
     * 
     * @param address
     * @param alias
     * @return The selected Device, or null if other options prevent the chosen
     *         device from being created. Eg jsr179 devices need explicit
     *         permission to run
     */
    public static Device createDevice(String address, String alias) {
        Logger.debug("address is " + address);
        Device dev = null;
        if ("internal".equals(address)) {
            // Jsr179Device requires permission, which it might not get.
            // In that event we need to abort this creation process so a new
            // device can be selected.
            // Create an internal (non bluetooth) gps device
            if (GpsUtilities.checkJsr179IsPresent()
                    && Controller.getController().getUseJsr179()) {
                dev =  Jsr179Device.getDevice(address, alias);
                Logger.debug("dev is "+dev);

            }
        } else if ("Mock".equals(address)) {
            dev = new MockGpsDevice(address, alias);
        } else {

            dev = new BluetoothGPSDeviceImpl(address, alias);
        }
        return dev;
    }
}
