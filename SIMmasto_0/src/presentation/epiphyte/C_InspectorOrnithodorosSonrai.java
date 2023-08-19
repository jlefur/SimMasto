package presentation.epiphyte;

import java.util.TreeSet;

import data.constants.I_ConstantDodel2;
import repast.simphony.essentials.RepastEssentials;
import thing.C_OrnitodorosSonrai;

/** Data inspector: retrieves informations about ticks.
 * @author M SALL 10.2019 */
public class C_InspectorOrnithodorosSonrai extends A_Inspector implements I_ConstantDodel2 {
	//
	// FIELD
	//
	private TreeSet<C_OrnitodorosSonrai> tickList = new TreeSet<C_OrnitodorosSonrai>();
	private static TreeSet<C_OrnitodorosSonrai> tickBirthList = new TreeSet<C_OrnitodorosSonrai>();
	private int nbBirth;
	//
	// CONSTRUCTOR
	//
	public C_InspectorOrnithodorosSonrai() {
		super();
		this.tickList.clear();
		tickBirthList.clear();
		this.nbBirth = 0;
		// add to the super class header, this proper header
		indicatorsHeader = "Tick;Bite Number;Hibernation Average;Birth Number;Population";
	}
	//
	// METHODS
	//
	@Override
	public void indicatorsCompute() {
	    this.nbBirth = tickBirthList.size();
		for (C_OrnitodorosSonrai newBorn : tickBirthList) {
		    this.tickList.add(newBorn);
		}
		tickBirthList.clear();
	}
	@Override
	/** Store the current state of indicators in the field including the super ones
	 * @see A_Protocol#recordIndicatorsInFile
	 * @revision MS 2016, JLF&MS 05.2017 */
	public String indicatorsStoreValues() {
	    this.indicatorsValues = RepastEssentials.GetTickCount() + CSV_FIELD_SEPARATOR + this.getBiteNumber()
				+ CSV_FIELD_SEPARATOR + this.getInfectedNumber() + CSV_FIELD_SEPARATOR + this.getHibernationAverage()
				+ CSV_FIELD_SEPARATOR + this.getBirthNumber() + CSV_FIELD_SEPARATOR + this.getTicksNumber()
				+ CSV_FIELD_SEPARATOR + this.getTickFemaleNumber() + CSV_FIELD_SEPARATOR + this.getTickMaleNumber();
		return this.indicatorsValues;
	}
	public static void addTickBirthList(C_OrnitodorosSonrai oneTick) {
		if (!tickBirthList.add(oneTick))
			System.out.println("C_InspectorOrnithodorosSonrai.addTickBirthList() : : could not add" + oneTick);
	}
	public void addTickToList(C_OrnitodorosSonrai oneTick) {
		if (!this.tickList.add(oneTick))
			System.out.println("C_InspectorOrnithodorosSonrai.addTickToList() : : could not add" + oneTick);
	}
	//
	// SETTER & GETTERS
	//
	public double getHibernationAverage() {
		double hibernationAverage = 0.;
		for (C_OrnitodorosSonrai oneTick : this.tickList) {
			hibernationAverage += oneTick.getHibernationDuration_Uday();
		}
		return (hibernationAverage / (this.tickList.size()));
	}
	public TreeSet<C_OrnitodorosSonrai> getTickList() {
		return this.tickList;
	}
	public int getTicksNumber() {
		return this.tickList.size();
	}
	public int getTickMaleNumber() {
		int maleNumber = 0;
		for (C_OrnitodorosSonrai oneTick : this.tickList) {
			if (oneTick.testMale()) maleNumber++;
		}
		return maleNumber;
	}
	public int getTickFemaleNumber() {
		int femaleNumber = 0;
		for (C_OrnitodorosSonrai oneTick : this.tickList) {
			if (oneTick.testFemale()) femaleNumber++;
		}
		return femaleNumber;
	}
	public double getBiteNumber() {
		int biteNumber = 0;
		for (C_OrnitodorosSonrai oneTick : this.tickList) {
			biteNumber += oneTick.getBiteNumber();
		}
		return biteNumber;
	}
	public double getBirthNumber() {
		return this.nbBirth;
	}
	public int getInfectedNumber() {
		int infectedNumber = 0;
		for (C_OrnitodorosSonrai oneTick : this.tickList)
			if (oneTick.isInfected()) infectedNumber += 1;
		return infectedNumber;
	}
	public int getHealthyNumber() {
		return (this.tickList.size() - getInfectedNumber());
	}
}
