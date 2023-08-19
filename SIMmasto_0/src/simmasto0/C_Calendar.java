/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package simmasto0;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import data.constants.I_ConstantDodel;
import data.converters.C_ConvertTimeAndSpace;

/** Time manager: in multiscale contexts, several protocols with their own calendar may run concurrently<br>
 * The Gregorian calendar is overloaded with an ability to convert between the protocol <br>
 * WARNING: to access the value of a specific x calendar do not use the static Calendar.MONTH but x.get(MONTH)<br>
 * @see simmasto0.protocol.A_Protocol
 * @author A Realini, rev. Mboup 2013, JLF 01.2014, 07.2014, 08.2014 */
public class C_Calendar extends GregorianCalendar {
	private static final long serialVersionUID = 1L;
	public static DateFormat shortDatePattern, longDatePattern, hourPattern, fullPattern;
	public int tick_Ucalendar; // the time unit (day, month...) within which a simulation tick is expressed
	public int tickAmount_Ucalendar; // the amount of this time unit within one tick
	public int TICK_MAX = 0;// TODO PAM de JLF 2018.06 Redundant with C_Parameters, used only in protocolTransportation
	//
	// CONSTRUCTOR
	//
	public C_Calendar() {
		tick_Ucalendar = C_ConvertTimeAndSpace.getTickUnit_Ucalendar();
		tickAmount_Ucalendar = C_ConvertTimeAndSpace.tick_Ucalendar;
		// Display the date in two possible pattern
		longDatePattern = new SimpleDateFormat("dd MMMM yyyy");
		shortDatePattern = new SimpleDateFormat("dd/MM/yyyy");
		hourPattern = new SimpleDateFormat("dd MMM yyyy - HH:mm:ss");
		fullPattern = new SimpleDateFormat("dd MMM yyyy (EE) - HH:mm:ss", Locale.ENGLISH);
	}
	//
	// METHODS
	//
	/*
	 * public void dayOfWeek() { switch (this.get(Calendar.DAY_OF_WEEK)) { case Calendar.MONDAY : ; case Calendar.TUESDAY : ; } }
	 */
	/** @return string such as 22/08/2014 */
	public String stringShortDate() {
		return C_Calendar.shortDatePattern.format(getTime());
	}
	/** @return string such as 22 août 2014 */
	public String stringLongDate() {
		return C_Calendar.longDatePattern.format(getTime());
	}
	/** @return string such as 22 août 2014 00:00:00 */
	public String stringHourDate() {
		return C_Calendar.hourPattern.format(getTime());
	}
	/** @return string such as tuesday 22 août 2014 00:00:00 */
	public String stringFullDate() {
		return C_Calendar.fullPattern.format(getTime());
	}
	//
	// SETTERS & GETTERS
	//
	/** increment the current with the time corresponding to one tick */
	public void incrementDate() {
		this.add(tick_Ucalendar, tickAmount_Ucalendar);
	}
	public boolean isDawn() {
		if ((this.get(Calendar.HOUR_OF_DAY) >= I_ConstantDodel.DAWN_START_Uhour) && (this.get(Calendar.HOUR_OF_DAY) <= I_ConstantDodel.DAWN_END_Uhour))
			return true;
		else return false;
	}
	public boolean isDayTime() {
		if ((this.get(Calendar.HOUR_OF_DAY) > I_ConstantDodel.DAWN_END_Uhour) && (this.get(
				Calendar.HOUR_OF_DAY) <= I_ConstantDodel.TWILIGHT_START_Uhour)) return true;
		else return false;
	}
	public boolean isTwilight() {
		if ((this.get(Calendar.HOUR_OF_DAY) > I_ConstantDodel.TWILIGHT_START_Uhour) && (this.get(
				Calendar.HOUR_OF_DAY) <= I_ConstantDodel.TWILIGHT_END_Uhour)) return true;
		else return false;
	}
	public boolean isNightTime() {
		if ((this.get(Calendar.HOUR_OF_DAY) > I_ConstantDodel.TWILIGHT_END_Uhour) || (this.get(
				Calendar.HOUR_OF_DAY) < I_ConstantDodel.DAWN_START_Uhour)) return true;
		else return false;
	}
	/** Check the possible use of multiple calendars :<br>
	 * WARNING: to access the value of a specific x calendar do not use the static Calendar.MONTH but x.get(MONTH)<br>
	 * author J. Le Fur, 08.2014 */
	public static void main(String[] args) {
		C_Calendar x = new C_Calendar();
		x.set(2011, Calendar.JANUARY, 11);
		C_Calendar y = new C_Calendar();
		y.set(2016, Calendar.SEPTEMBER, 12);
		System.out.println(C_Calendar.longDatePattern.format(x.getTime()));
		System.out.println(C_Calendar.longDatePattern.format(y.getTime()));
		System.out.println(x.get(YEAR));
		System.out.println(y.get(YEAR));

	}
}