package presentation.epiphyte;

import java.util.Iterator;
import java.util.TreeSet;
import presentation.dataOutput.C_FileWriter;
import repast.simphony.essentials.RepastEssentials;
import simmasto0.protocol.A_Protocol;
import thing.C_RodentCmr;
import thing.I_SituatedThing;
import thing.ground.C_Trap;
import thing.ground.I_Container;

/** manage traps and tagged rodents list, computes the global indicators from the retrieved individual values indicator
 * definitions
 * @see C_RodentCmr
 * @author Le Fur and Diakhate, sept.2013, rev. JLF 08.2014 */

public class C_InspectorCMR extends A_Inspector implements data.constants.I_ConstantBandia {
	//
	// FIELDS
	//
	private double currentDRS, currentDMR;
	private int currentMNA;
	protected TreeSet<C_Trap> trapList = new TreeSet<C_Trap>();
	private TreeSet<I_Container> trapArea = new TreeSet<I_Container>(); // used to compute the effective number of rodents within
																		// the area.
	public static TreeSet<C_RodentCmr> taggedRodentList = new TreeSet<C_RodentCmr>();
	public int taggedRodentsNumber = 0; // current increment of tagged rodents,
	private C_FileWriter dataSaverCMR;// Writer of an outer csv file
	//
	// CONSTRUCTOR
	//
	public C_InspectorCMR() {
		super();
		this.dataSaverCMR = new C_FileWriter("CMR.csv", true);
		this.dataSaverCMR.writeln("tick;date;popSize;CMR popSize;NbTaggedRodents;DRS;DMR;MNA");
	}
	//
	// METHODS
	//
	/** close private files */
	@Override
	public void closeSimulation() {
	    this.dataSaverCMR.closeFile();
	}

	/** stores the current state of indicators - JLF 10.2013 */
	public void storeCMRIndicators(String date) {
	    this.dataSaverCMR.writeln(RepastEssentials.GetTickCount() + CSV_FIELD_SEPARATOR + date + CSV_FIELD_SEPARATOR + C_InspectorPopulation.rodentList
				.size() + CSV_FIELD_SEPARATOR + getTrapAreaPopulation_Urodent() + CSV_FIELD_SEPARATOR + taggedRodentList.size() + CSV_FIELD_SEPARATOR
				+ getCurrentDRS() + CSV_FIELD_SEPARATOR + getCurrentDMR() + CSV_FIELD_SEPARATOR + getCurrentMNA());
	}
	/** Remove tagged rodents from taggedList or trap from trapList */
	@Override
	public void discardDeadThing(I_SituatedThing thing) {
		boolean test = true;
		if ((thing instanceof C_RodentCmr) && (((C_RodentCmr) thing).getTag() > 0) && !taggedRodentList.remove(thing)) test = false;
		else if ((thing instanceof C_Trap) && !trapList.remove(thing)) test = false;
		if (!test) A_Protocol.event("C_InspectorCMR.discardDeadThing", "Could not remove " + thing + " from list", isError);
	}
	@Override
	public void indicatorsReset() {
	    this.currentDRS = 0.;
	    this.currentDMR = 0.;
	    this.currentMNA = 0;
	}

	/** Mean distance between successive catches within a given session (DRS: "Distance entre Recaptures Successives")
	 * @param session : the session number */
	public void computeDRS(int session) {
		double sumDRS = 0., rodentDRS = 0.;
		int nbTaggedRodents = taggedRodentList.size();
		for (C_RodentCmr rodent : taggedRodentList) {
			rodentDRS = rodent.computeDRS(session);
			if (rodentDRS == 0.) nbTaggedRodents--;// DRS = 0 when rodent has not been recaptured
			else sumDRS += rodentDRS;
		}
		this.currentDRS = sumDRS / nbTaggedRodents;
	}
	/** compute every distances between catches and keep the largest one within a session (DMR: Distance maximum de recapture) */
	public void computeDMR(int session) {
		double dmr = 0., rodentDMR = 0.;
		for (C_RodentCmr rodent : taggedRodentList) {// taggedRodentList have been catched at least 1 time
			rodentDMR = rodent.computeDMR(session);
			if (dmr < rodentDMR) dmr = rodentDMR;
		}
		this.currentDMR = dmr;
	}
	/** MNA : Minimum Number Alive for each session */
	public void computeMNA(int session) {
		int mna = 0;
		for (C_RodentCmr rodent : taggedRodentList) {
			if (rodent.aliveInSession(session)) mna++;
		}
		this.currentMNA = mna;
	}
	/** Give a tag number to rodent */
	public void tag(C_RodentCmr rodent) {
		if (taggedRodentList.add(rodent)) {
		    this.taggedRodentsNumber++;
			rodent.setTag(this.taggedRodentsNumber);
		}
	}
	/** Add trap in trapList */
	public void addTrap(C_Trap oneTrap) {
	    this.trapList.add(oneTrap);
	}
	//
	// SETTERS & GETTERS
	//
	/** Used by protocol to set the initial values
	 * @see simmasto0.protocol.C_ProtocolBandia#setTrapArea_Ucell
	 * @param trapArea the array of cells containing the trap system / Le Fur, 08.2014 */
	public void setTrapArea(TreeSet<I_Container> trapArea) {
		this.trapArea = trapArea;
	}
	public TreeSet<C_Trap> getTrapList() {
		return this.trapList;
	}
	public double getCurrentDRS() {
		return this.currentDRS;
	}
	public double getCurrentDMR() {
		return this.currentDMR;
	}
	public double getCurrentMNA() {
		return this.currentMNA;
	}
	/** value of the population living within the area of the trap system. Value to be compared with MNA value, JLF 08.2014 */
	public double getTrapAreaPopulation_Urodent() {
		double trapSystemPopulation = 0;
		for (Iterator<I_Container> iterator = this.trapArea.iterator(); iterator.hasNext();)
			trapSystemPopulation += iterator.next().getFullRodentList().size();
		return trapSystemPopulation;
	}
}
