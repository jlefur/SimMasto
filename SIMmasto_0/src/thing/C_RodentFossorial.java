/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing;

import java.util.TreeSet;

import simmasto0.C_ContextCreator;
import simmasto0.protocol.A_Protocol;
import thing.dna.I_DiploidGenome;
import thing.ground.C_BurrowSystem;

/** Rodents bound to burrow systems : female use or dig burrow to spawn and suckle, mating is outside burrow<br>
 * Implement digging, perception, spawning, dispersing, */
public class C_RodentFossorial extends C_Rodent {
	//
	// CONSTRUCTOR
	//
	public C_RodentFossorial(I_DiploidGenome genome) {
		super(genome);
	}
	//
	// METHODS
	//
	/** Generate a new animal, necessary to use spawn
	 * @see A_Amniote#spawn */
	@Override
	public A_Animal giveBirth(I_DiploidGenome genome) {
		return new C_RodentFossorial(genome);
	}

	/** Spawn if within a burrow, else dig a burrow and super.spawn , rev. JLF 01,09.2017 */
	@Override
	protected void actionSpawn() {
		if (!(this.getCurrentSoilCell() instanceof C_BurrowSystem)) { // if within a burrow do not dig
			// Choose burrow not full and stop
			for (I_SituatedThing onePerceivedThing : this.perception())
				if ((onePerceivedThing instanceof C_BurrowSystem) && !(((C_BurrowSystem) onePerceivedThing).isFull())) {
					this.actionEnterBurrow((C_BurrowSystem) onePerceivedThing);
					break;
				}
		}
		// Dig a burrow and enter it.
		if (!(this.getCurrentSoilCell() instanceof C_BurrowSystem)) actionEnterBurrow(actionDig());
		this.setHasToLeaveFullContainer(false);// for children care
		super.actionSpawn(); // use giveBirth() for each egg's genome.
	}
	@Override
	public void setHasToLeaveFullContainer(boolean dispersal) {
		if (this.isSucklingChild()) this.hasToLeaveFullContainer = false;
		else super.setHasToLeaveFullContainer(dispersal);
	}
	@Override
	/** If within burrow exit before super.wander<br>
	 * JLF 01.2018 */
	protected void actionForage() {
		if (this.currentSoilCell instanceof C_BurrowSystem) this.actionRandomExitOfContainer();
		super.actionForage();
	}
	@Override
	/** Exit from burrow before dispersing using super */
	public void actionDisperse() {
		if (this.getCurrentSoilCell() instanceof C_BurrowSystem) actionRandomExitOfContainer();
		super.actionDisperse();
	}
	/** If within a burrow system, only retrieve the burrow's occupants, else super<br>
	 * Version Authors J.E. Longueville 2011 - J.Le Fur 03.2012,04.2015,01.2016
	 * @see A_VisibleAgent#retrieveCell2Perception
	 * @return TreeSet<I_situated_thing> listeVisibleObject */
	@Override
	protected TreeSet<I_SituatedThing> perception() {
		if (this.currentSoilCell instanceof C_BurrowSystem) return this.currentSoilCell.getOccupantList();
		else return super.perception();
	}
	/** Dig a burrow (at the same coordinates), then enter within it <br>
	 * Version Author JE Longueville 2011, rev. jlf 08.2015, 12.2015, 09.2017
	 * @return new C_BurrowSystem
	 * @see C_RodentFossorial#spawn */
	public C_BurrowSystem actionDig() {
		if (this.currentSoilCell instanceof C_BurrowSystem) {
			A_Protocol.event("C_RodentFossorial.actionDig", this + " cannot dig, already in " + this.currentSoilCell, isError);
			return (C_BurrowSystem) this.currentSoilCell;
		}
		else {
			C_BurrowSystem newBurrow = new C_BurrowSystem(currentSoilCell.getAffinity(), currentSoilCell.retrieveLineNo(), currentSoilCell
					.retrieveColNo());
			C_ContextCreator.protocol.contextualizeNewThingInContainer(newBurrow, currentSoilCell);// Put burrow in digger's
																									// current soilCell
			myLandscape.moveToLocation(newBurrow, myLandscape.getThingCoord_Ucs(this));// Place burrow at the rodent position
			this.energy_Ukcal--;
/*			if (C_Parameters.VERBOSE)
				A_Protocol.event("C_RodentFossorial.actionDig()", "BURROW DUG by " + this + " at " + this.getCurrentSoilCell(), isNotError);*/
			return newBurrow;
		}
	}
	/** Enter burrow; if target was the burrow, remove target */
	public void actionEnterBurrow(C_BurrowSystem oneburrow) {
		C_ContextCreator.protocol.contextualizeOldThingInCell(this, oneburrow);// Put rodent in burrow
		myLandscape.moveToLocation(this, myLandscape.getThingCoord_Ucs(oneburrow));// Place rodent at the burrow position
		// Remove any target not belonging to occupant list JLF 01.2018
		if (this.target != null) {
			if (oneburrow.getOccupantList().contains(this.target)) {} // In case target is a thing within the burrow (a priori
																		// never happens)
			else this.discardTarget();
		}
		this.energy_Ukcal--;
	}
	/** Exit from burrow before foraging
	 * @version JLF 09.2017
	 * @return true if success */
	@Override
	protected boolean actionEat() {
		if (this.currentSoilCell instanceof C_BurrowSystem) this.actionRandomExitOfContainer();
		return super.actionEat();
	}
}
