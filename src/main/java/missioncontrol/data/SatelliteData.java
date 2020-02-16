package missioncontrol.data;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/*
 * The ingest of status telemetry data has the format:
 * 	<timestamp>|<satellite-id>|<red-high-limit>|<yellow-high-limit>|<yellow-low-limit>|<red-low-limit>|<raw-value>|<component>
 */
@Setter
@Getter
public abstract class SatelliteData {
	

	
	protected Date timestamp;
	protected int satelliteId;
	
	// Red High and Low
	protected int redHighLimit;
	protected int redLowLimit;

	// Yellow High and low
	protected int yellowHighLimit;
	protected int yellowLowLimit;
	protected double rawValue;
	protected SatelliteComponent component;
	
	public abstract boolean outsideBounds();
	
}
