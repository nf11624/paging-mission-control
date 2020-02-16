package missioncontrol;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import missioncontrol.data.AlertData;
import missioncontrol.data.SatelliteBatteryData;
import missioncontrol.data.SatelliteComponent;
import missioncontrol.data.SatelliteData;
import missioncontrol.data.SatelliteThermostatData;
import missioncontrol.util.TimeCalculator;

public class Driver {

	private List<String> input;
	private Map<Integer, List<SatelliteData>> data;
	private List<AlertData> alerts;
	
	// positions inside of line of data
	private static final int TIMESTAMP = 0;
	private static final int SATELLITE_ID = 1;
	private static final int RED_HIGH_LIMIT = 2;
	private static final int YELLOW_HIGH_LIMIT = 3;
	private static final int YELLOW_LOW_LIMIT = 4;
	private static final int RED_LOW_LIMIT = 5;
	private static final int RAW_VALUE = 6;
	private static final int COMPONENT = 7;
	
	
	public Driver() {
		data = new HashMap<>();
		alerts = new LinkedList<>();
	}
	
	public void loadData(String inputFile) {
		try {
			input = Files.readAllLines(Paths.get(inputFile));
			for (String line : input) {
				String[] datapoints = line.split("\\|");
				SatelliteData satData = null;
				/*
				 * 0 -- <timestamp>|
				 * 1 -- <satellite-id>|
				 * 2 -- <red-high-limit>|
				 * 3 -- <yellow-high-limit>|
				 * 4 -- <yellow-low-limit>|
				 * 5 -- <red-low-limit>|
				 * 6 -- <raw-value>|
				 * 7 -- <component>
				 */
				if (datapoints[COMPONENT].equals(SatelliteComponent.BATT.toString())) {
					satData = new SatelliteBatteryData();
				}
				else if (datapoints[COMPONENT].equals(SatelliteComponent.TSTAT.toString())) {
					satData = new SatelliteThermostatData();
				}
				else {
					// Error
				}
				satData.setTimestamp(TimeCalculator.parseDTG(datapoints[TIMESTAMP]));
				satData.setSatelliteId(Integer.parseInt(datapoints[SATELLITE_ID]));
				satData.setRedHighLimit(Integer.parseInt(datapoints[RED_HIGH_LIMIT]));
				satData.setRedLowLimit(Integer.parseInt(datapoints[RED_LOW_LIMIT]));
				satData.setYellowHighLimit(Integer.parseInt(datapoints[YELLOW_HIGH_LIMIT]));
				satData.setYellowLowLimit(Integer.parseInt(datapoints[YELLOW_LOW_LIMIT]));
				satData.setRawValue(Double.parseDouble(datapoints[RAW_VALUE]));
				
				// If there is no list for this satellite, create the list of satelliteData for this id
				if (!data.containsKey(satData.getSatelliteId())) {
					data.put(satData.getSatelliteId(), new LinkedList<SatelliteData>());
				}
				data.get(satData.getSatelliteId()).add(satData);
			}
		} catch (IOException e) {
			// No error handling yet
			e.printStackTrace();
		}
	}

	
	
	public void processData() {
		for (int satelliteId : data.keySet()) {
			processSatelliteData(data.get(satelliteId));
		}
	}
	
	private void processSatelliteData(List<SatelliteData> satelliteData) {
		int numVoltage = 0;
		int numTherm = 0;
		int intervalStartIndex = 0;
		int intervalEndIndex = 1;
		// Handle initial data point being outside of tolerance.
		SatelliteData initialDatum = satelliteData.get(intervalStartIndex);
		if (initialDatum.outsideBounds()) {
			if (initialDatum.getComponent().equals(SatelliteComponent.BATT)) {
				numVoltage++;
			}
			else if (initialDatum.getComponent().equals(SatelliteComponent.TSTAT)) {
				numTherm++;
			}
		}
		
		
		while (intervalEndIndex < satelliteData.size()) {
			SatelliteData datum = satelliteData.get(intervalEndIndex);
			if (datum.outsideBounds()) {
				//System.out.println("OUT OF BOUNDS");
				if (datum.getComponent().equals(SatelliteComponent.BATT)) {
					numVoltage++;
				}
				else if (datum.getComponent().equals(SatelliteComponent.TSTAT)) {
					numTherm++;
				}
			}
			// Raise flag
			if (numVoltage >= 3 || numTherm >= 3) {
				// TODO: create alert
				AlertData alert = new AlertData();
				alert.setComponent(datum.getComponent());
				alert.setSatelliteId(datum.getSatelliteId());
				alert.setTimestamp(datum.getTimestamp());
				if (numVoltage >= 3) {
					alert.setSeverity("RED LOW");
					//TODO: Need to figure out a way to account for already seen alerts.
					numVoltage--;
				}
				else {
					alert.setSeverity("RED HIGH");
					numTherm--;
				}
				alerts.add(alert);
				//break;
			}
			
			if (TimeCalculator.exceedsMinuteInterval(5, initialDatum.getTimestamp(), satelliteData.get(intervalEndIndex).getTimestamp())) {
				// Remove any error from the initial data point
				if (initialDatum.outsideBounds()) {
					if (datum.getComponent().equals(SatelliteComponent.BATT)) {
						numVoltage--;
					}
					else if (datum.getComponent().equals(SatelliteComponent.TSTAT)) {
						numTherm--;
					}
				}
				intervalStartIndex++;
				initialDatum = satelliteData.get(intervalStartIndex); 
			}
			intervalEndIndex++;
		}
			
	}
	
	
	public void printAlerts() {
		StringBuilder output = new StringBuilder();
		
		
		// TODO: Replace w/ Jackson
		output.append("[\n");
		for (int i = 0; i < alerts.size(); i++) {
		//for (AlertData alert: alerts) {
			AlertData alert = alerts.get(i); 
			output.append("{\n");
			output.append("satelliteId: ").append(alert.getSatelliteId()).append("\n");
			output.append("severity: ").append(alert.getSeverity()).append("\n");
			output.append("component: ").append(alert.getComponent()).append("\n");
			output.append("timestamp: ").append(TimeCalculator.formatDate(alert.getTimestamp())).append("\n");
			output.append("}");
			if (i < (alerts.size() - 1)) {
				output.append(",\n");
			}
			else {
				output.append("\n");
			}
				
		}
		output.append("]");
		System.out.println(output.toString());
	}
	
	public static void main(String[] args) {
		Driver driver = new Driver();
		driver.loadData(args[0]);
		driver.processData();	
		driver.printAlerts();
	}
	
}
