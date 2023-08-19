/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing;// test

import java.util.TreeSet;

import presentation.epiphyte.C_InspectorHybrid;
import simmasto0.C_ContextCreator;
import simmasto0.protocol.A_Protocol;
import thing.dna.C_GenomeAmniota;
import thing.dna.C_GenomeEucaryote;
import thing.dna.I_DiploidGenome;
import thing.ground.I_Container;
import data.C_Parameters;
import data.converters.C_ConvertTimeAndSpace;

/** TODO JLF 2018.02 javadoc */
public abstract class A_Amniote extends A_Animal implements I_ReproducingThing {
	//
	// FIELDS
	//
	/** Reproductive status */
	public boolean preMature;
	protected boolean sexualMature; // i.e., is this gonade able to make gamete
	protected boolean readyToMate; // agent physiologic ability to mate
	protected double curMatingLatency_Uday;
	protected double curGestationLength_Uday;
	protected TreeSet<C_Egg> eggList;// Simple data structure to hold zygote information during matingLatency
	protected int numMatings;
	protected static C_InspectorHybrid hybridInspector = null;
	//
	// CONSTRUCTOR
	//
	public A_Amniote(I_DiploidGenome genome) {
		super(genome);
		preMature = false;
		sexualMature = false;
		readyToMate = false;
		// if (this.testFemale()) {
		curMatingLatency_Uday = -1; // TODO number in source OK 2017.09 JLF curMatingLatency_Uday = -1
		curGestationLength_Uday = -1.; // TODO number in source OK 2015.08 JLF gestation length = -1
		eggList = new TreeSet<C_Egg>();
		numMatings = 0;// TODO number in source OK 2015.08 JLF
	}
	/** Provide a random age to agents at initialization = sexualMaturityAge +/- weaningAge Can be overridden JLF 01.2018 */
	public void setRandomAge() {
		long randAge_Uday = Math.round(((C_GenomeAmniota) this.genome).getWeaningAge_Uday()
				+ (C_ContextCreator.randomGeneratorForInitialisation.nextDouble() * ((C_GenomeAmniota) this.genome)
						.getSexualMaturity_Uday()));
		this.setAge_Uday(randAge_Uday);
	}
	//
	// METHODS
	//
	/** Remove references to last container left, targeted container children and eggs */
	@Override
	public void discardThis() {
		for (C_Egg oneEgg : this.eggList) oneEgg.discardThis();
		this.eggList = null;
		if (!this.getChildren().isEmpty()) for (A_Animal child : this.getChildren()) child.discardTarget();
		super.discardThis();
	}
	//
	/** Activity within one tick (inherit animal's activity also)All methods must be called at each step Special case for adult
	 * female suckling Activity: If foraging, return to sucklings' nest<br>
	 * @version JLF 03/07.2014, rev 2017.09, 2018.03
	 * @see updatePhysiologicStatus */
	@Override
	public void step_Utick() {
		if (!this.isDead()) {

			this.updatePhysiologicStatus();

			if (this.isSexualMature()) {
				TreeSet<A_Amniote> children = this.getChildren();
				// Suckle children if needed
				if (!children.isEmpty()) {
					// if foraging, and not targeting particular food..., return to sucklings' cell
					I_Container nest = this.getChildren().first().getCurrentSoilCell();
					if (this.currentSoilCell != nest) {
						if (this.target == null) this.setTarget(nest);
					}
					else this.actionParentSuckle();
				}
				// else check REPRODUCTION desire
				else if (this.getDesire().equals(FEED) || this.getDesire() == "") {
					if (this.readyToMate && A_Protocol.isBreedingSeason()) this.setDesire(REPRODUCE);
					// End of reproduction season
				}
				if (!A_Protocol.isBreedingSeason() && this.getDesire().equals(REPRODUCE)) this.setDesire("");
			}
			super.step_Utick();
		}
	}
	/** Manage sexual immaturity, gestation. NB: account for tick length <br>
	 * manage {@link #actionSpawn()} when pregnant<br>
	 * Set desire to SUCKLE for newborns<br>
	 * rev.Le Fur 08.2015, 11.2015, 09.2017 */
	protected void updatePhysiologicStatus() {
		double oneTick_Uday = 1. / C_ConvertTimeAndSpace.oneDay_Utick;
		// Sexual mature
		if (this.getAge_Uday() >= ((C_GenomeAmniota) this.genome).getSexualMaturity_Uday()) {
			if (!this.isSexualMature()) {
				this.sexualMature = true;
				this.hasToSwitchFace = true;
			}
			this.curMatingLatency_Uday -= oneTick_Uday;
			if (this.curMatingLatency_Uday <= 0) {
				this.curMatingLatency_Uday = 0;
				this.readyToMate = true;
			}
			// Premature -> sexualMature
			if (this.preMature) {
				this.preMature = false;
				this.hasToSwitchFace = true;
			}
			if (this.isPregnant()) {
				this.curGestationLength_Uday -= oneTick_Uday;
				this.readyToMate = false;
				// End of pregnancy
				if ((this.curGestationLength_Uday <= 0.) && (this.canSpawn())) {
					this.actionSpawn();
					this.setTarget(this.currentSoilCell);// Remain in the place for suckling (children are trapped on board)
					this.curMatingLatency_Uday = ((C_GenomeAmniota) this.genome).getMatingLatency_Uday();
					this.hasToSwitchFace = true;
				}
			}
		}
		// Suckler -> premature
		else if (this.getAge_Uday() >= ((C_GenomeAmniota) this.genome).getWeaningAge_Uday() && !this.preMature) {
			this.preMature = true;
			this.hasToLeaveFullContainer = true;
			this.setDesire("");// Can now deliberate and move JLF 01.2017, 08.2017
			if (this.target != null && this.target instanceof A_Amniote) this.discardTarget();// Do not target mother anymore
		}
	}
	/** Verify if Amniota can spawn in the currentSoilCell. This method must be redefined in daughter protocols */
	public boolean canSpawn() {
		return true;
	}
	/** if desire = reproduction, choosePartners, else return super.deliberation() <br>
	 * @param perceivedThings TreeSet <I_situated_thing> listeVisibleObjects from perception method
	 * @return candidate targets<br>
	 *         Version J.E.Longueville & J.Le Fur 2011 / jlefur 03.2012 / Complete rev. JLF 08.2017 */
	@Override
	protected TreeSet<I_SituatedThing> deliberation(TreeSet<I_SituatedThing> perceivedThings) {
		if (this.getDesire().equals(REPRODUCE)) {
			TreeSet<I_SituatedThing> candidateTargets = this.choosePartner(perceivedThings);
			// If no partner, then forage
			if (candidateTargets.isEmpty()) {
				this.setDesire(FEED);
				return super.deliberation(perceivedThings);
			}
			else return candidateTargets;
		}
		else return super.deliberation(perceivedThings);
	}

	/** JLF 03.2021 was formerly included in {@link A_Amniote#deliberation(TreeSet)} */
	protected TreeSet<I_SituatedThing> choosePartner(TreeSet<I_SituatedThing> perceivedThings) {
		TreeSet<I_SituatedThing> candidateTargets = new TreeSet<I_SituatedThing>();// There may be several equivalent targets
		for (I_SituatedThing oneThingPerceived : perceivedThings) {
			// Compatible if same species,ready to mate, other sex (JLF 03.2018)
			if (oneThingPerceived instanceof A_Amniote) {
				A_Amniote other = (A_Amniote) oneThingPerceived;
				if (other.getGenome().getClass() == this.genome.getClass())
					if (!this.sameSex(other) && other.readyToMate) candidateTargets.add(other);
			}
		}
		return candidateTargets;
	}
	@Override
	/** Try to mate with partner, then super */
	protected boolean processTarget() {
		if (this.target instanceof A_Amniote) return this.actionInteract((A_Amniote) this.target);
		else return super.processTarget();
	}

	protected boolean actionParentSuckle() {
		for (A_Amniote child : this.getChildren()) {
			child.energy_Ukcal++;
			this.energy_Ukcal--;
		}
		if (!this.getDesire().equals(NONE)) {
			this.setDesire(FEED);// desire = feed after suckling
			this.actionDisperse();// could be actionWander() also
		}
		return true;
	}

	/** Only female proceed to mate. This may be triggered by the male<br>
	 * the femaleFertilisation method accounts for copulation it produces eggs (litter size) with the new genome (Eucaryotes: 2
	 * gonosomes and microsatXsome)
	 * @see C_Rodent#actionInteract(C_Rodent)
	 * @param male the father with wich to mate */
	public boolean actionMateWithMale(I_ReproducingThing male) {
		C_Egg oneEgg;
		I_DiploidGenome eggGenome;
		C_GenomeAmniota maleParentGenome = (C_GenomeAmniota) ((A_Animal) male).getGenome();
		C_GenomeAmniota thisFemaleParentGenome = (C_GenomeAmniota) this.genome;
		// generate LITTER_SIZE zygotes (fusion, cross-over (& mutation)) from the mother and father
		int litterSize = thisFemaleParentGenome.getLitterSizeValue();
		// Make litter size dependent on ground carrying capacity JLF 03.2019
		litterSize = Math.min(this.getCurrentSoilCell().getCarryingCapacity_Urodent() - this.getCurrentSoilCell()
				.getFullLoad_Urodent(), litterSize);
		litterSize = Math.max(litterSize, 1);
		for (int i = 0; i < litterSize; i++) {
			eggGenome = this.genome.mateGenomes(0, maleParentGenome);
			// If one of the microsatelite of the genome of the egg contains the Lethal_Allele (for a reason or an other). The egg
			// is killed. Then, the size of the litter will be smaller.
			if (((C_GenomeEucaryote) eggGenome).getMicrosatXsome().getAlleles().contains(
					C_GenomeEucaryote.LETHAL_ALLELE)) {
				if (C_Parameters.VERBOSE)
					A_Protocol.event("A_Mammal.actionMateWithMale", "EGG NIPPED IN THE BUD :'(", isNotError);
				hybridInspector.incrPbNippedEgg();
			}
			else {
				oneEgg = new C_Egg(eggGenome);
				C_ContextCreator.protocol.contextualizeNewThingInContainer(oneEgg, this);
				eggList.add(oneEgg);
			}
		}
		this.setPregnant();
		if (C_Parameters.VERBOSE)
			A_Protocol.event("A_Amniote.actionMateWithMale()", "MATE event: " + this + " X " + male + " at "
					+ this.currentSoilCell, isNotError);
		this.energy_Ukcal--;
		return true;
	}

	/** Create eggs number of children; use giveBirth() for each egg's genome. Rev. Pape 2015 */
	protected void actionSpawn() {
		if (!testFemale()) A_Protocol.event("A_Amniote.spawn", this + "is not a female", isError);
		for (C_Egg egg : this.eggList) {
			A_Animal child = this.giveBirth(egg.genome);
			// Remove useless egg
			A_VisibleAgent.myLandscape.moveToContainer(egg, this.currentSoilCell);
			egg.setDead(true);
			// Configure child
			child.trappedOnBoard = this.trappedOnBoard;// e.g., birth within a truck
			if (C_Parameters.VERBOSE && child.trappedOnBoard) {
				A_Protocol.event("A_Amniote.actionSpawn", child + ", is born trapped on board in: " + this
						.getCurrentSoilCell(), isNotError);
			}
			A_Amniote.myLandscape.addChildAgent(this, child);
			child.setDesire(NONE);// used to bypass activity steps
			child.setTarget(this);// used to retrieve its children JLF 09.2017
			child.setHasToSwitchFace(true);
			this.energy_Ukcal--;
		}
		if (C_Parameters.VERBOSE)
			A_Protocol.event("A_Amniote.actionSpawn()", "SPAWN event: " + this + " spawned " + this.eggList.size()
					+ " children at " + this.getCurrentSoilCell(), isNotError);
		this.eggList.clear();
	}
	//
	// SETTER AND GETTERS
	//
	/** Switch the correct counters and tags desire, readyToMate, switchFace, start gestationLength countdown, remove previous
	 * suitors<br>
	 * @version JLF 2017.08 */
	public void setPregnant() {
		this.curGestationLength_Uday = ((C_GenomeAmniota) this.genome).getGestationLength_Uday();// start the countdown
		this.readyToMate = false;
		this.hasToSwitchFace = true;
		// Remove possible suitors
		for (A_Animal oneAnimal : this.animalsTargetingMe) if (oneAnimal.getClass() == this.getClass())
			oneAnimal.target = null;
		this.animalsTargetingMe.clear();
		// Remove own target
		if (this.target != null && this.target.getClass() == this.getClass()) this.discardTarget();
		this.setDesire(FEED);// once pregnant, forages until spawning JLF 2017.08
	}
	protected boolean sameSex(A_Amniote other) {
		if (this.testMale()) {
			if (other.testMale()) return true;
			else return false;
		}
		else {// Female
			if (other.testMale()) return false;
			else return true;
		}
	}
	/** Search for children (viz. targeting this) */
	protected TreeSet<A_Amniote> getChildren() {
		TreeSet<A_Amniote> children = new TreeSet<A_Amniote>();
		for (A_Animal oneAnimal : this.animalsTargetingMe) if (oneAnimal instanceof A_Amniote)
			if (((A_Amniote) oneAnimal).isSucklingChild()) {
				children.add((A_Amniote) oneAnimal);
			}
		return children;
	}
	public double getCurGestationLength_Uday() {
		return curGestationLength_Uday;
	}
	public double getCurMatingLatency_Uday() {
		return curMatingLatency_Uday;
	}
	public boolean isSexualMature() {
		return this.sexualMature;
	}
	/** used also to color the icon when pregnant */
	public boolean isPregnant() {
		if (this.eggList != null)// to avoid probe crash (JLF 03.2021)
			return (eggList.size() != 0);
		else
			return false;
	}
	/** Check if age lower than genomes' weaning age<br>
	 * @author lefurj 2017 */
	public boolean isSucklingChild() {
		if (this.genome != null) return (this.getAge_Uday() < ((C_GenomeAmniota) this.genome).getWeaningAge_Uday());
		else return false;// in case of probing a dead agent on GUI
	}
	/** Declare the hybrid inspector that catches the lethal allele in various situations
	 * @author JLF 02.2013 */
	public static void init(C_InspectorHybrid inspector) {
		hybridInspector = inspector;
	}
}