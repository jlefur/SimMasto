/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing;

import java.util.TreeSet;

import simmasto0.C_ContextCreator;
import thing.dna.I_DiploidGenome;
import thing.dna.species.C_GenomeMastoErythroleucus;
import thing.dna.species.C_GenomeMastoNatalensis;
import thing.dna.species.C_GenomeMastomys;
import thing.ground.C_LandPlot;
import thing.ground.C_SoilCell;
import data.constants.I_ConstantCage;

/** Standard rodent only living in cage and not dying
 * @author JLF, 03.2012 rev. jlf 2017-2018 */
public class C_RodentCaged extends C_Rodent implements I_ConstantCage {
	//
	// FIELDS
	//
	public String generation = "F0";
	private C_LandPlot birthCage; // used to identify inbreeding rodents.
	private C_LandPlot currentCage;
	private int currentLine;
	//
	// CONSTRUCTOR
	//
	public C_RodentCaged(I_DiploidGenome genome) {
		super(genome);
	}
	//
	// METHODS
	//
	/** generate a new animal, necessary to use spawn
	 * @see A_Amniote#spawn */
	@Override
	public A_Animal giveBirth(I_DiploidGenome genome) {
		C_RodentCaged rodentChild;
		if (genome instanceof C_GenomeMastoNatalensis) rodentChild = new C_RodentCaged(new C_GenomeMastoNatalensis());
		else if (genome instanceof C_GenomeMastoErythroleucus) rodentChild = new C_RodentCaged(new C_GenomeMastoErythroleucus());
		else {
			rodentChild = new C_RodentCaged(new C_GenomeMastomys());
		}
		rodentChild.birthCage = this.currentCage;
		rodentChild.currentCage = this.currentCage;
		rodentChild.currentLine = this.currentLine;
		rodentChild.initParameters();
		return rodentChild;
	}
	/** can be used standalone if users parameters are changed during simulation, JLF 2017.08 */
	public void initParameters() {
		super.initParameters();
		this.speed_UmeterByTick = this.speed_UmeterByTick / IN_CAGE_SPEED_REDUCER;
	}
	/** Random selection of the target from undecidable alternatives / rev. JLF 02.2019
	 * @param alternatives : the possible alternatives set (any situated thing)<br>
	 *            TODO JLF 2018.01 select min de getDistance_Umeter() should slow simulations */
	@Override
	protected I_SituatedThing setDestination(TreeSet<I_SituatedThing> alternatives) {
		if (!alternatives.isEmpty()) {
			int noTarget = (int) (C_ContextCreator.randomGeneratorForMovement.nextDouble() * alternatives.size());
			I_SituatedThing target = null;
			java.util.Iterator<I_SituatedThing> iterator = alternatives.iterator();
			for (int i = 0; i < alternatives.size(); i++) {
				target = iterator.next();
				if (i == noTarget) this.setTarget(target);
			}
			return this.target;
		}
		else return null;
	}
	/** Remove references to cages */
	@Override
	public void discardThis() {
		this.birthCage = null;
		this.currentCage = null;
		super.discardThis();
	}
	@Override
	/** Do not get stuck to children */
	protected TreeSet<A_Amniote> getChildren() {
		TreeSet<A_Amniote> children = new TreeSet<A_Amniote>();
		return children;
	}
	/** Special procedure for MBour experiment<br>
	 * @version jlefur
	 * @return Perceived cells within the cage
	 * @param perceivedThings */
	@Override
	protected TreeSet<I_SituatedThing> deliberation(TreeSet<I_SituatedThing> perceivedThings) {
		TreeSet<I_SituatedThing> visibleSoilCells = new TreeSet<I_SituatedThing>();
		for (Object oneObject : perceivedThings)
			if (!oneObject.equals(this)) {
				if (oneObject instanceof C_RodentCaged) this.actionInteract((C_RodentCaged) oneObject);
				else if ((oneObject instanceof C_SoilCell) && (oneObject != this.currentSoilCell)) visibleSoilCells.add((C_SoilCell) oneObject);
			}
		return visibleSoilCells;
	}
	/** Special procedure for MBour experiment<br>
	 * @version jlefur
	 * @return cells and agents within the cage */
	@Override
	protected TreeSet<I_SituatedThing> perception() {
		return this.currentCage.getOccupantList();
	}
	/** Remove mother as a target, JLF 02.2019 */
	@Override
	protected void actionSpawn() {
		super.actionSpawn();
		TreeSet<A_Animal> children = (TreeSet<A_Animal>) this.animalsTargetingMe.clone();
		for (A_Animal child : children)
			if (child.target == this) {
				child.setTarget(null);
				child.setDesire(FEED);
			}
	}
	//
	// SETTERS & GETTERS
	//
	public void setBirthCage(C_LandPlot birthLandPlot) {
		this.birthCage = birthLandPlot;
	}
	public void setCurrentCage(C_LandPlot currentCage) {
		this.currentCage = currentCage;
	}
	public void setCurrentLine(int currentLine) {
		this.currentLine = currentLine;
	}
	public String getGeneration() {
		return generation;
	}
	@Override
	/** Either super/10., either 0. (for unbiased outputs) <br>
	 * rev. jlf 06.2018 TODO number in source OK reduce deathProb caged rodents jlf 04.2018 */
	protected double computeDeathProbability_Uday() {
		// return super.getDeathProbability_Uday()/10.;
		return 0.; // (for unbiased outputs)
	}
	public C_LandPlot getBirthCage() {
		return birthCage;
	}
	public C_LandPlot getCurrentCage() {
		return currentCage;
	}
	public int getCurrentLine() {
		return currentLine;
	}
	/** Use for GUI probe rodent display TODO number in source OK */
	public int getDiploidNumber() {
		if (this.genome == null) return -99999999;// in case GUI display a dead rodent
		else return this.genome.getDiploidNumber();

	}
}
