package thing;

import java.util.TreeSet;

import com.vividsolutions.jts.geom.Coordinate;

import data.C_Parameters;
import data.constants.I_ConstantGerbil;
import repast.simphony.engine.environment.RunState;
import simmasto0.C_ContextCreator;
import simmasto0.protocol.A_Protocol;
import thing.dna.I_DiploidGenome;
import thing.dna.species.C_GenomeAcacia;
import thing.ground.C_BurrowSystem;
import thing.ground.C_Nest;
import thing.ground.I_Container;

/** This class accounts for rodent gerbil predator in the sahel zone .
 * @author M. Sall 04.2016 */
public class C_BarnOwl extends A_Amniote implements I_ConstantGerbil {
	//
	// CONSTRUCTOR
	//
	public C_BarnOwl(I_DiploidGenome genome) {
		super(genome);
		// TODO JLF 2018.09 temporary - used to avoid bug with metapopulation
		Object[] list = RunState.getInstance().getMasterContext().toArray();
		for (int i = 0; i < list.length; i++)
			if (list[i] instanceof C_Nest) {
				this.setMyHome((C_Nest) list[i]);
				break;
			}
	}
	//
	// OVERRIDEN METHODS
	//
	/** choose to disperse if no target found.<br>
	 * May be overridden in daughter classes<br>
	 * J.Le Fur, 07.2018 */
	@Override
	protected void actionNoChoice() {
		this.actionDisperse();
	}

	@Override
	protected TreeSet<I_SituatedThing> perception() {
		TreeSet<I_SituatedThing> perceptList = super.perception();
		TreeSet<I_SituatedThing> barnOwlPerceptList = new TreeSet<I_SituatedThing>();
		if (!perceptList.isEmpty()) {
			for (I_SituatedThing oneThing : perceptList) {
				if ((oneThing instanceof C_Rodent) && !this.occupantList.contains(oneThing) && !(oneThing
						.getCurrentSoilCell() instanceof C_BurrowSystem)) barnOwlPerceptList.add(oneThing);
			}
		}
		// JLF 02.2021 go to vegetation in search of rodents
		if (barnOwlPerceptList.isEmpty()) {
			for (I_SituatedThing oneThing : perceptList) {
				if ((oneThing instanceof C_Vegetation)) barnOwlPerceptList.add(oneThing);
			}
		}
		return barnOwlPerceptList;
	}
	/** Get list of perceived things, create list of rodent gerbil and call interact function
	 * @return empty list of soilcell
	 * @param perceivedThings Authors MS 10.2016 */
	@Override
	public TreeSet<I_SituatedThing> deliberation(TreeSet<I_SituatedThing> perceivedThings) {
		if (this.getDesire().equals(HIDE)) {
			perceivedThings.clear();
			perceivedThings.add(this.myHome);
			return perceivedThings;
		}
		return super.deliberation(perceivedThings);
	}
	@Override
	/** Choose the prey if barn owl need to eat and return rodent list perceived */
	public TreeSet<I_SituatedThing> chooseFood(TreeSet<I_SituatedThing> percevingRodent) {
		TreeSet<I_SituatedThing> rodentList = new TreeSet<I_SituatedThing>();
		for (I_SituatedThing oneThing : percevingRodent) {
			if (oneThing instanceof C_Rodent) rodentList.add(oneThing);
		}
		return rodentList;
	}
	@Override
	/** Eat its preys when back to nest, hide at dawn or rest if home, rev JLF 03.2021 */
	public void step_Utick() {
		if (this.currentSoilCell.equals(this.myHome) && !this.occupantList.isEmpty()) {
			C_Rodent prey;
			for (I_SituatedThing occupant : this.getOccupantList())
				if (occupant instanceof C_Rodent) {
					prey = (C_Rodent) occupant;
					this.actionEat(prey);
				}
		}

		// Keep quiet at dawn
		if (A_Protocol.protocolCalendar.isDawn() || A_Protocol.protocolCalendar.isDayTime()) {
			if (this.currentSoilCell instanceof C_Nest) this.setDesire(NONE);
			else {
				this.setDesire(HIDE); // search shelter
			}
		}
		else if (A_Protocol.protocolCalendar.isTwilight() || A_Protocol.protocolCalendar.isNightTime()) {
			// Exit from Nest when night comes
			if (this.currentSoilCell instanceof C_Nest) {
				C_ContextCreator.protocol.contextualizeOldThingInCell(this, this.myHome.getCurrentSoilCell().getCurrentSoilCell());
				this.setDesire("");
			}
		}
		super.step_Utick();
	}
	/**  */
	private boolean actionEat(C_Rodent prey) {
		// this.energy_Ukcal += prey.energy_Ukcal;
		this.energy_Ukcal = 0.;
		prey.setDead(true);
		return true;
	}
	@Override
	/** process target when desire==HIDE */
	protected boolean processTarget() {
		if (this.getDesire().equals(HIDE)) {//
			if (this.target instanceof C_Nest) {
				this.actionEnterContainer((C_Nest) this.target);
				this.setDesire(NONE);
				return true;
			}
		}
		return super.processTarget();
	}
	@Override
	/** Random draw using PREDATION_SUCCESS parameter. If predation succeeds prey dead and energy back to 0. */
	protected boolean actionInteract(A_Animal prey) {
		double x = C_ContextCreator.randomGeneratorForInitialisation.nextDouble() / PREDATION_SUCCESS;
		prey.checkDeath(x);
		// if prey catched (checkDeath) prey enters the owl's belly (dead is used only to benefit from checkDeath).
		if (prey.dead) {
			prey.setDead(false);
			prey.setDesire(NONE);
			prey.setTrappedOnBoard(true);
			A_VisibleAgent.myLandscape.moveToContainer(prey, this);
			this.setDesire(HIDE);// return to nest for eating
			if (C_Parameters.VERBOSE) A_Protocol.event("C_BarnOwl.actionInteract", "PREDATION event: " + this + " catches " + prey, isNotError);
		}
		return true;
	}
	/** Create a tree and a nest at the owl location, put the nest in the tree and the owl in the nest<br>
	 * JLF 02.2021 */
	public void actionSetNest() {
		I_Container thisCell = this.currentSoilCell;
		Coordinate oneCoordinate = new Coordinate(thisCell.retrieveLineNo() + C_ContextCreator.randomGeneratorForInitialisation.nextDouble(), thisCell
				.retrieveColNo() + C_ContextCreator.randomGeneratorForInitialisation.nextDouble());
		// create tree
		C_Vegetation oneTree = new C_Vegetation(new C_GenomeAcacia());
		C_ContextCreator.protocol.contextualizeNewThingInContainer(oneTree, thisCell);
		myLandscape.moveToLocation(oneTree, oneCoordinate);// Place tree at the owl position
		// create nest
		C_Nest oneNest = new C_Nest(thisCell.getAffinity(), thisCell.retrieveColNo(), thisCell.retrieveLineNo());
		C_ContextCreator.protocol.contextualizeNewThingInContainer(oneNest, oneTree);
		myLandscape.moveToLocation(oneNest, oneCoordinate);// Place nest at the tree position
		this.setMyHome(oneNest);
		this.energy_Ukcal--;
		if (C_Parameters.VERBOSE)
			A_Protocol.event("C_BarnOwl.actionSetNest()", "New TREE and NEST for " + this + " at " + this.getCurrentSoilCell(), isNotError);
	}
	@Override
	public A_Animal giveBirth(I_DiploidGenome genome) {
		return new C_BarnOwl(genome);
	}
}
