package data.converters;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import simmasto0.C_Calendar;

/** Time manager / all methods are static
 * @author PAMBOUP, rev. JLF 01.2014, 02.2015 */
public class C_ConvertTimeAndSpace {
	public static int tick_Ucalendar; // 24
	public static String tick_UcalendarUnit; // H
	public static String spaceUnit; // M

	private static double[][] timeMatrixConverter;
	private static double[][] spaceMatrixConverter;
	public static double oneDay_Utick;

	public static void init(int tickUcalendarNumber, String tickUcalendarUnit, String spaceUnit) {
		C_ConvertTimeAndSpace.tick_Ucalendar = tickUcalendarNumber;// ex: 24 for 24h
		C_ConvertTimeAndSpace.tick_UcalendarUnit = tickUcalendarUnit.trim().toUpperCase(); // ex: h for 24h
		C_ConvertTimeAndSpace.spaceUnit = spaceUnit;
		timeMatrixConverter = new double[][]{
				{1, 12.1666667, 365.000001, 8760.000024, 525600.0014, 31536000.09, 31536000086.0, 3.1536E+16}, // a 0
				{0.078947348, 1, 30, 720, 43200, 2592000, 2592000000.0, 2.592E+15}, // m 1
				{0.002631578, 0.033333333, 1, 24, 1440, 86400, 86400000, 8.64E+13}, // j 2
				{0.000109649, 0.001388889, 0.041666667, 1, 60, 3600, 3600000, 3.6E+12}, // h 3
				{1.82748E-06, 2.31481E-05, 0.000694444, 0.016666667, 1, 60, 60000, 60000000000.0}, // min 4
				{3.04581E-08, 3.85802E-07, 1.15741E-05, 0.000277778, 0.016666667, 1, 1000, 1000000000}, // s 5
				{3.04581E-11, 3.85802E-10, 1.15741E-08, 2.77778E-07, 1.66667E-05, 0.001, 1, 1000000}, // ms 6
				{3.04581E-17, 3.85802E-16, 1.15741E-14, 2.77778E-13, 1.66667E-11, 0.000000001, 0.000001, 1} // ns
		// A M J H MIN S MS NS
		};// 0 1 2 3 4 5 6 7
		spaceMatrixConverter = new double[][]{{1, 10, 100, 1000, 10000, 100000, 1000000, 1E+12}, // km 0
				{0.1, 1, 10, 100, 1000, 10000, 100000, 1E+11}, // hm 1
				{0.01, 0.1, 1, 10, 100, 1000, 10000, 10000000000.0}, // dam 2
				{0.001, 0.01, 0.1, 1, 10, 100, 1000, 1000000000}, // m 3
				{0.0001, 0.001, 0.01, 0.1, 1, 10, 100, 100000000}, // dm 4
				{0.00001, 0.0001, 0.001, 0.01, 0.1, 1, 10, 10000000}, // cm 5
				{0.000001, 0.00001, 0.0001, 0.001, 0.01, 0.1, 1, 1000000}, // mm 6
				{1E-12, 1E-11, 1E-10, 0.000000001, 0.00000001, 0.0000001, 0.000001, 1}, // nm 7
		// KM HM DAM M DM CM MM NM
		};// 0 1 2 3 4 5 6 7
		oneDay_Utick = convertTimeDurationToTick(1, "d");
	}
	/** Convert duration with calendar unit TO duration in tick.<br>
	 * For example if the duration is 24H, we have : &nbsp;&nbsp;&nbsp;&nbsp; durationUCalendar : 24 <br>
	 * &nbsp;&nbsp;&nbsp;&nbsp; calendarFieldUnit : "H"
	 * @param duration_UCalendar int duration :
	 * @param calendarUnit : S, M, H, D, MON, Y (sec, min, hour, day, month or year)
	 * @return the number of tick corresponding to that duration */
	public static double convertTimeDurationToTick(double duration_UCalendar, String calendarUnit) {
		return (duration_UCalendar * _UcalendarTo_Utick(calendarUnit)) / (double) tick_Ucalendar;
	}
	/** This procedure is a temporary shortcut which aims is to be generalized with the longer (2nd) alternative:<br>
	 * C_timeAndSpaceConverter.days_Utick(age_Uday);<br>
	 * replaces : <br>
	 * double oneDay_Utick = C_ConvertTimeAndSpace.convertTimeDurationToTick(1, "day");<br>
	 * this.age_Utick = (long) Math.round(age_Uday * oneDay_Utick); <br>
	 * J.Le Fur, 08.2014, rev. 03.2015
	 * @see C_ConvertTimeAndSpace#getTickUnit_Ucalendar for abbreviations */
	public static double days_Utick(long duration_Uday) {
		return convertTimeDurationToTick(duration_Uday, "d");
	}

	/** Convert speed ex : 60km/h to X u_meter/tick */
	public static double convertSpeed_UspaceByTick(int speedValue, String spaceUnit, String timeUnit) { // 60, km, h -> 60km/h
		return Math.round((speedValue * convertSpaceUnitToRasterUnit(spaceUnit) * (double) tick_Ucalendar)
				/ _UcalendarTo_Utick(timeUnit));
	}
	/** Convert units ex : km to u_meter (useful to convert speed) */
	public static double convertSpaceUnitToRasterUnit(String spaceUnit) {
		int l = getConverterSpaceMatrixIndex(spaceUnit);
		int c = getConverterSpaceMatrixIndex(C_ConvertTimeAndSpace.spaceUnit);
		return spaceMatrixConverter[l][c];
	}
	public static double _UcalendarTo_Utick(String timeUnit) {
		int l = getConverterTimeMatrixIndex(timeUnit);
		int c = getConverterTimeMatrixIndex(tick_UcalendarUnit);
		return timeMatrixConverter[l][c];
	}
	/** Accepts full string name, 1st character name, except for month (mon) */
	public static int getConverterTimeMatrixIndex(String str) {
		switch (str.toUpperCase()) { // return replace breaks
			case "Y" :
				return 0;
			case "MON" :
				return 1;
			case "D" :
				return 2;
			case "H" :
				return 3;
			case "M" :
				return 4;
			case "S" :
				return 5;
			case "MS" :
				return 6;
				// case "NANOSECOND" :
				// return 7;
				// case "NS" : return 7;
		}
		System.err.println("C_CentenalConverter.getConverterTimeMatrixIndex(): don't accept : " + str);
		return -1;
	}
	public static int getConverterSpaceMatrixIndex(String str) {
		switch (str.toUpperCase()) { // les return remplacent les break
			case "KM" :
				return 0; //
			case "HM" :
				return 1; //
			case "DAM" :
				return 2; //
			case "M" :
				return 3; //
			case "DM" :
				return 4; //
			case "CM" :
				return 5; //
			case "MM" :
				return 6; //
			case "NM" :
				return 7; //
		}
		System.err.println("C_CentenalConverter.getConverterSpaceMatrixIndex(): unknown space unit : " + str);
		return -1;
	}
	/** Must be called just by C_Calendar or after C_Calendar instantiation
	 * @return the calendar field number (e.g., MILLISECOND -> 5)<br>
	 *         author mboup or realini, rev. JLF 08.2014, 03.2015 */
	public static int getTickUnit_Ucalendar() {
		switch (tick_UcalendarUnit.toUpperCase()) {
		// unique abbreviations one(/two/three) letters
			case "MS" :
				return Calendar.MILLISECOND;
			case "S" :
				return Calendar.SECOND;
			case "M" :
				return Calendar.MINUTE;
			case "H" :
				return Calendar.HOUR;
			case "D" :
				return Calendar.DATE;// i.e. day
			case "MON" :
				return Calendar.MONTH;
			case "Y" :
				return Calendar.YEAR;
		}
		System.err.println("C_CentenalConverter.getTickUcalendarField() : bad unit");
		return -1;
	}
	public static double getTimeBetweenDates_Ums(String earlierDate, String laterDate, String returnDateType) {

		Date theEarlierDate = null, theLaterDate = null;
		try {
			theEarlierDate = C_Calendar.shortDatePattern.parse(earlierDate);
			theLaterDate = C_Calendar.shortDatePattern.parse(laterDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		long millisecondsPerTime = getLength_Ums(returnDateType);
		Calendar oneCalendar = Calendar.getInstance();
		oneCalendar.setTime(theEarlierDate);
		long aFromOffset = oneCalendar.get(Calendar.DST_OFFSET);
		oneCalendar.setTime(theLaterDate);
		long aToOffset = oneCalendar.get(Calendar.DST_OFFSET);
		long diffInMiliseconds = (theLaterDate.getTime() + aToOffset) - (theEarlierDate.getTime() + aFromOffset);
		return (double) diffInMiliseconds / millisecondsPerTime;
	}
	public static long getLength_Ums(String dateType) {
		switch (dateType.toUpperCase()) { // les return remplace les break
			case "Y" :
				return (long) (1000 * 60 * 60 * 24 * 12.1666667 * 365); //
			case "MON" :
				return (long) (1000 * 60 * 60 * 24 * 12.1666667); // month ou mois
			case "D" :
				return 1000 * 60 * 60 * 24;
			case "H" :
				return 1000 * 60 * 60; // hour ou heur
			case "M" :
				return 1000 * 60; //
			case "S" :
				return 1000;
			case "MS" :
				return 1; //
		}
		System.err.println("C_ConvertTimeAndSpace.getMilliseconds_per_Time(): do not accept : " + dateType);
		return -1;
	}

}
