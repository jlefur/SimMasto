package thing.ground;

import java.util.Calendar;

import data.constants.I_ConstantMusTransport;
import simmasto0.protocol.A_Protocol;
import thing.ground.landscape.C_Landscape;

/** A market get its population grow the market day. Implemented for Mus transport protocol */
public class C_Market extends C_City implements I_ConstantMusTransport {
	//
	// FIELD
	//
	private int marketDay_UCalendar = 0;
	//
	// CONSTRUCTOR
	//
	public C_Market(C_Landscape groundManager) {
		super(groundManager);
	}
	//
	// SETTER AND GETTERS
	//
	public void setMarketDay_UCalendar(int marketDay_UCalendar) {
		this.marketDay_UCalendar = marketDay_UCalendar;
	}
	public int getMarketDay_UCalendar() {
		return marketDay_UCalendar;
	}
	public boolean isMarketDay() {
		if (marketDay_UCalendar == 0) return true; // permanent market = city
		else if (A_Protocol.protocolCalendar.get(Calendar.DAY_OF_WEEK) == this.marketDay_UCalendar) return true;
		else return false;
	}
	// GUI STUFF
	/** Change size of the market icon when market day */
	public double getSize() {
		if (this.isMarketDay()) return 100.;
		else return 50.;
	}
	/** Change color of the market icon when market day */
	public double getRedColor() {
		if (this.isMarketDay()) return 1.;
		else return 0.;
	}
}
