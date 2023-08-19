package presentation.epiphyte;

import repast.simphony.engine.environment.RunState;
import repast.simphony.essentials.RepastEssentials;
import simmasto0.C_ContextCreator;
import simmasto0.protocol.A_Protocol;
import thing.I_SituatedThing;
import data.C_Parameters;
import data.constants.I_ConstantString;

/** Data inspector: retrieves informations e.g. population sizes and manages lists. stores the values of the indicators to be recorded in the general
 * indicators file.
 * @author A Realini 05.2011 / J.LeFur 09.2011, 07.2012, 01.2013 */

public abstract class A_Inspector implements Comparable<A_Inspector>, I_Inspector, I_ConstantString {

	protected String indicatorsHeader;
	protected String indicatorsValues;
	protected Integer myId;// used to compare objects within sorts and treeSets - LeFur 02.2013

	public A_Inspector() {
	    this.myId = C_ContextCreator.INSPECTOR_NUMBER;
		C_ContextCreator.INSPECTOR_NUMBER++;
//		indicatorsHeader = "numrun;step_mn;Tick;Date;HourDate;objects";
		this.indicatorsHeader = "step_mn;Tick;Date;HourDate;objects";
		indicatorsReset();
	}
	/** also store indicators values for all other inspectors */
	public void step_Utick() {
		indicatorsCompute();
		indicatorsStoreValues();
	}

	/** for compatibility with step (used in daughter classes) */
	public void indicatorsCompute() {}

	/** store all values as a string in the corresponding field */
	public String indicatorsStoreValues() {// Simultech 2018
	    this.indicatorsValues = C_Parameters.TICK_LENGTH_Ucalendar + CSV_FIELD_SEPARATOR + String.valueOf(RepastEssentials.GetTickCount())
				+ CSV_FIELD_SEPARATOR + A_Protocol.protocolCalendar.stringShortDate() + CSV_FIELD_SEPARATOR + A_Protocol.protocolCalendar
						.stringHourDate() + CSV_FIELD_SEPARATOR + RunState.getInstance().getMasterContext().size();
		return this.indicatorsValues;
	}
	/** used for comparison of inspectors within the A_Protocol inspectors field / JLF 02.2013 */
	public int compareTo(A_Inspector other) {
		int nb1 = other.myId;
		int nb2 = this.myId;
		if (nb1 > nb2) return -1;
		else if (nb1 == nb2) return 0;
		else return 1;
	}
	public String getIndicatorsValues() {
		return this.indicatorsValues;
	}
	public String getIndicatorsHeader() {
		return this.indicatorsHeader;
	}
	public void indicatorsReset() {};
	public void closeSimulation() {};
	public void discardDeadThing(I_SituatedThing I_deadThing) {};
}
