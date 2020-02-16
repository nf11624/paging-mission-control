package missioncontrol.data;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AlertData {
	
	
	
	
	/*
	 * The output will specify alert messages. The alert messages should be in JSON format with the following structure:
{
    satelliteId: 1234,
    severity: "severity",
    component: "component",
    timestamp: "timestamp"
}

	 */
	private int satelliteId;
	private String severity;
	private SatelliteComponent component;
	private Date timestamp;
	
	
	
	
}
