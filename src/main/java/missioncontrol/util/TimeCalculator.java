package missioncontrol.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimeCalculator {

	
	public int intervalLength;
	private static final SimpleDateFormat INPUT_FORMAT = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
	private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	
	public static Date parseDTG(String dateTimeGroup) {
		Date date = null;
		try {
			date = INPUT_FORMAT.parse(dateTimeGroup);
		} catch (ParseException e) {
			// No error handling for now
			e.printStackTrace();
		}
		// 20180101 23:01:05.001
		//System.out.println(date.toString());
		return date;
	}
	
	public static boolean exceedsMinuteInterval(int numberOfMinutes, Date dtg1, Date dtg2) {
		boolean result = false;
		//System.out.println("DIFF: " + Math.abs(dtg1.getTime() - dtg2.getTime()) );
		if (Math.abs(dtg1.getTime() - dtg2.getTime()) > (numberOfMinutes * 60 * 1000)) {
			result = true;
		}
		return result;
	}
	
	public static String formatDate(Date date) {
		// Convert java.util.Date to an Instant for output formatting
		String output = OUTPUT_FORMAT.toFormat().format(date.toInstant().atZone(ZoneId.systemDefault()));
		return output;
	}
	
	
	
}
