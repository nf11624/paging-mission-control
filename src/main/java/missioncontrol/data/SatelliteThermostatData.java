package missioncontrol.data;

public class SatelliteThermostatData extends SatelliteData {
	public SatelliteThermostatData() {
		component = SatelliteComponent.TSTAT; 
	}

	
	/**
	 * If for the same satellite there are three thermostat readings that exceed the red high limit within a five minute interval.
	 */
	@Override
	public boolean outsideBounds() {
		boolean fault = false;
		if (rawValue > redHighLimit) {
			fault = true;
		}
		return fault;
	}
}
