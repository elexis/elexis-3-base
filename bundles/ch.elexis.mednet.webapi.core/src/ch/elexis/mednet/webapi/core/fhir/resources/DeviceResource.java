package ch.elexis.mednet.webapi.core.fhir.resources;

import java.util.UUID;

import org.hl7.fhir.r4.model.Device;

import ch.elexis.mednet.webapi.core.constants.FHIRConstants;




public class DeviceResource {

	public static Device createDevice() {
		Device device = new Device();
		device.setId(UUID.randomUUID().toString());
		device.setManufacturer(FHIRConstants.DEVICE_MANUFACTURER);
		Device.DeviceDeviceNameComponent deviceName = new Device.DeviceDeviceNameComponent();
		deviceName.setName(FHIRConstants.DEVICE_MANUFACTURER_NAME);
		deviceName.setType(Device.DeviceNameType.USERFRIENDLYNAME);
		device.addDeviceName(deviceName);
		return device;
	}
}

