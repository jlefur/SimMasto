/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing;
import data.constants.I_ConstantNumeric;
import data.constants.I_ConstantString;
import data.converters.C_ConvertTimeAndSpace;
import repast.simphony.essentials.RepastEssentials;
import simmasto0.C_ContextCreator;
import simmasto0.util.C_VariousUtilities;

/** The root class of most business objects "A_" means abstract & "NDS" means Nearly Decomposable System (See Simon, 1962)
 * @author Jean Le Fur, kyle wagner, Quentin Baduel, Jean-Emmanuel Longueville Version 2.1, 17.feb.2011, rev. JLF 09-10.2014 */
public abstract class A_NDS implements I_CyberneticThing, I_LivingThing, Comparable<A_NDS>, I_ConstantNumeric,
		I_ConstantString {
	//
	// FIELDS
	//
	/** Used as a unique identifier & used also to compare objects within sorts and treeSets - LeFur 2011 */
	protected String myId = "0";
	public double energy_Ukcal;
	private String myName;
	protected final double birthDate_Utick;
	protected double age_Utick = DEFAULT_AGE0_Utick;
	protected double age_Uday = age_Utick / C_ConvertTimeAndSpace.oneDay_Utick;
	/** Used to avoid concurrent modification exception in inspector's rodent list */
	protected boolean dead = false;
	//
	// CONSTRUCTOR
	//
	/** Has to be initialized by a ground manager @see A_VisibleAgent#init(landscape) */
	public A_NDS() {
		this.myId = String.valueOf(C_ContextCreator.AGENT_NUMBER);
		this.setMyName(C_VariousUtilities.getShortClassName(this.getClass()) + "_" + this.myId);
		this.birthDate_Utick = RepastEssentials.GetTickCount();
		this.dead = false;// used to avoid concurrent modification exception in inspector's rodent list
		this.age_Utick = DEFAULT_AGE0_Utick;
		this.age_Uday = age_Utick / C_ConvertTimeAndSpace.oneDay_Utick;
		this.energy_Ukcal = INITIAL_ENERGY_Ukcal;
		C_ContextCreator.AGENT_NUMBER++;
	}
	//
	// METHODS
	//
	/** Remove references to other objects. Overriden in daughter classes
	 * @revision JLF 2016.05 */
	public void discardThis() {
		this.myId = null;
		this.myName = null;
	}
	/** Increases age.Ensures the validity of the time scales (+ exploratory research on time scales: ticks vs calendars).<br>
	 * Tested with several times scales : System.out.println("A_NDS.getAge_Uday()" + age_Utick + "/" + age_Utick / oneDay_Utick +
	 * "days (1day:" + oneDay_Utick + "ticks)");<br>
	 * Version J.Le Fur 08.2014, rev. 11.2015 */
	public void actionGrowOlder_Utick() {
		this.age_Utick++;
		this.age_Uday += 1 / C_ConvertTimeAndSpace.oneDay_Utick;
	}
	/** Computing time allocated to the agentActivity<br>
	 * Called at each time tick; overriden in successive daughter classes */
	public void step_Utick() {
		this.actionGrowOlder_Utick();
		this.checkDeath(this.getDeathProbability_Utick());
	}
	public int compareTo(A_NDS other) {
		// patch for test TODO JLF 2021.03 to remove
		if ((this.myId == null) || (other.myId == null)) {
			/*
			 * A_Protocol.event("A_NDS.compareTo()", this.myId + " (" + C_VariousUtilities.getShortClassName(this.getClass()) +
			 * ") not comparable to " + other.myId + " (" + C_VariousUtilities.getShortClassName(other.getClass()) + ")",
			 * isError);
			 */
			return 1;
		}
		return other.retrieveId().compareTo(this.myId);
	}
	/** set Dead if random number is lower than death probability passed in arg */
	public void checkDeath(double deathProbability) {
		if (C_ContextCreator.randomGeneratorForDeathProb.nextDouble() <= deathProbability) this.dead = true;
	}
	@Override
	public String toString() {
		return this.myName;
	}
	//
	// SETTERS & GETTERS
	//

	public void setAge_Uday(double age_Uday) {
		this.age_Uday = age_Uday;
		this.age_Utick = Math.round(age_Uday * C_ConvertTimeAndSpace.oneDay_Utick);
	}
	/** Used to toggle death on the GUI, JLF 09.2012 */
	public void setDead(boolean dead) {
		this.dead = dead;
	}
	public void setThisName(String oneName) {
		if (!oneName.equals("")) this.setMyName(oneName);
	}
	public boolean isDead() {
		return this.dead;
	}
	protected double getDeathProbability_Utick() {
		return computeDeathProbability_Uday() / C_ConvertTimeAndSpace.oneDay_Utick;
	}
	/** Retrieve death probability
	 * @return double value */
	protected double computeDeathProbability_Uday() {
		return I_ConstantNumeric.DEFAULT_DEATH_PROBABILITY_UperDay;
	}
	public String retrieveId() {
		if (this.myId == null) {
			return new String();// Patch 03.2021 JLF if dead NDS still not wiped of
		}
		else return this.myId;
	}
	/** Ensures the validity of the time scales (+ exploratory research on time scales: ticks vs calendars).<br>
	 * Tested with several times scales : System.out.println("A_NDS.getAge_Uday()" + age_Utick + "/" + age_Utick / oneDay_Utick +
	 * "days (1day:" + oneDay_Utick + "ticks)");<br>
	 * J.Le Fur 08.2014 */
	public double getAge_Uday() {
		return this.age_Uday;
	}
	public double getBirthDate_Utick() {
		return this.birthDate_Utick;
	}
	public double getAge_Utick() {
		return this.age_Utick;
	}
	public String retrieveMyName() {
		return this.myName;
	}
	public double getEnergy_Ukcal() {// for GUI display
		return energy_Ukcal;
	}
	public void setMyName(String myName) {
		this.myName = myName;
	}
}