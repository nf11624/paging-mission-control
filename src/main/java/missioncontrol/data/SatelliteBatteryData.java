package missioncontrol.data;

public class SatelliteBatteryData extends SatelliteData {
	public SatelliteBatteryData() {
		component = SatelliteComponent.BATT; 
	}

	/**
	 * If for the same satellite there are three battery voltage readings that are under the red low limit.
	 */
	@Override
	public boolean outsideBounds() {
		boolean fault = false;
		if (rawValue < redLowLimit) {
			fault = true;
		}
		return fault;
	}
}
