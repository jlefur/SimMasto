package thing;

import java.util.TreeSet;

import com.vividsolutions.jts.geom.Coordinate;

import data.C_Parameters;
import data.constants.I_ConstantDodel2;
import simmasto0.C_ContextCreator;
import simmasto0.protocol.A_Protocol;
import simmasto0.util.C_VariousUtilities;
import thing.dna.I_DiploidGenome;
import thing.ground.C_SoilCellUrban;

public class C_Cat extends A_Amniote implements I_ConstantDodel2 {
	//
	// FIELD
	//
	public Coordinate myHome = new Coordinate();
	//
	// CONSTRUCTOR
	//
	public C_Cat(I_DiploidGenome genome) {
		super(genome);
	}
	//
	// OVERRIDDEN METHODS
	@Override
	public A_Animal giveBirth(I_DiploidGenome genome) {
		return new C_Cat(genome);
	}
	@Override
	protected void updatePhysiologicStatus() {
		this.readyToMate = (!this.isPregnant() && this.isSexualMature() && (this.curMatingLatency_Uday >= MATING_LATENCY_DURATION_Uday));
		super.updatePhysiologicStatus();
	}
	@Override
	protected TreeSet<I_SituatedThing> perception() {
		TreeSet<I_SituatedThing> reallyPerceivedThings = new TreeSet<I_SituatedThing>();
		TreeSet<I_SituatedThing> perceivedThings = super.perception();
		if (!(((C_SoilCellUrban) this.getCurrentSoilCell()).isWall())) {
			for (I_SituatedThing oneThing : perceivedThings) {
				I_SituatedThing sightObstacle = C_VariousUtilities.checkObstacleBefore0(this, oneThing);
				if (!(sightObstacle != null && sightObstacle != oneThing)) reallyPerceivedThings.add(oneThing);
			}
		}
		if (reallyPerceivedThings.isEmpty()) {// can see things in its around
			reallyPerceivedThings = perceivedThings;
		}
		reallyPerceivedThings.remove(this.currentSoilCell);
		reallyPerceivedThings.remove(this);
		return reallyPerceivedThings;
	}
	@Override
	protected TreeSet<I_SituatedThing> deliberation(TreeSet<I_SituatedThing> perceivedThings) {
		if (this.getDesire() == REPRODUCE) return this.choosePartner(perceivedThings);
		if (this.getDesire() == FEED) return this.chooseFood(perceivedThings);
		return super.deliberation(perceivedThings);
	}
	@Override
	public TreeSet<I_SituatedThing> chooseFood(TreeSet<I_SituatedThing> perceivedThings) {
		TreeSet<I_SituatedThing> perceivedRodents = new TreeSet<I_SituatedThing>();
		for (I_SituatedThing oneThing : perceivedThings)
			if (oneThing instanceof C_Rodent) perceivedRodents.add(oneThing);
		return perceivedRodents;
	}
	@Override
	protected TreeSet<I_SituatedThing> choosePartner(TreeSet<I_SituatedThing> perceivedThings) {
		TreeSet<I_SituatedThing> perceivedPartner = new TreeSet<I_SituatedThing>();
		for (I_SituatedThing oneThing : perceivedThings)
			if (oneThing instanceof C_Cat && ((C_Cat) oneThing).readyToMate) perceivedPartner.add(oneThing);
		return perceivedPartner;
	}
	@Override
	protected boolean actionInteract(A_Animal prey) {
		double x = C_ContextCreator.randomGeneratorForInitialisation.nextDouble() / PREDATION_SUCCESS;
		prey.checkDeath(x);
		// if prey catched (checkDeath) prey enters the cat's belly (dead is used only to benefit from checkDeath).
		if (prey.dead) {
			if (C_Parameters.VERBOSE) A_Protocol.event("C_BarnOwl.actionInteract", "PREDATION event: " + this + " catches " + prey, isNotError);
			return this.actionEat();
		}
		return false;
	}
	//
	// GETTERS
	@Override
	/** Check if thing is a wall */
	public boolean isSightObstacle(I_SituatedThing thing) {
		if (thing instanceof C_SoilCellUrban && ((C_SoilCellUrban) thing).isWall()) return true;
		else return super.isSightObstacle(thing);
	}
	public boolean isJuvenile() {
		return (this.getAge_Uday() < JUVENILE_MIN_AGE_Uday) && !this.isSexualMature();
	}
}
