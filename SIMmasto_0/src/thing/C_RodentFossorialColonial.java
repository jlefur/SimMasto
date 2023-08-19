package thing;

import java.util.TreeSet;

import thing.dna.I_DiploidGenome;
import thing.ground.C_BurrowSystem;

public class C_RodentFossorialColonial extends C_RodentFossorial {

	public C_RodentFossorialColonial(I_DiploidGenome genome) {
		super(genome);
	}
	/** generate a new animal, necessary to use spawn
	 * @see A_Amniote#spawn */
	@Override
	public A_Animal giveBirth(I_DiploidGenome genome) {
		return new C_RodentFossorialColonial(genome);
	}
	/** Manage activity when rodent is within a burrow system, mature...<br>
	 * Adapted from JEL 2010, JLF 03,06.2014 full rev. from svn507, JLF 07.2014, 12.2016, 01,09.2017 */
	@Override
	public void step_Utick() {
		// Mature male inside burrow within breeding season
		if (this.getDesire().equals(REPRODUCE) && (this.getCurrentSoilCell() instanceof C_BurrowSystem)) {
			if (this.testMale()) {
				for (C_Rodent acquaintance : this.getCurrentSoilCell().getRodentList())
					this.actionInteract(acquaintance);
				actionRandomExitOfContainer();
			}
			if (this.testFemale()) this.setDesire(NONE);
		}
		super.step_Utick();
	}
	/** Uses A_Animal deliberation (super). If breeding season, the burrow list replaces the soil cells list
	 * @return list of candidate soilCells
	 * @param perceivedThings Authors JE. Longueville 2011 / drastically simplified jlefur 03.2012, rev. jlf 09.2017 */
	@Override
	protected TreeSet<I_SituatedThing> deliberation(TreeSet<I_SituatedThing> perceivedThings) {
		if (this.getDesire().equals(REPRODUCE) && !(this.currentSoilCell instanceof C_BurrowSystem)) {
			// Extract only burrow systems from the list of perceived things JLF 09.2017 */
			TreeSet<I_SituatedThing> candidateBurrows = new TreeSet<I_SituatedThing>();
			if (this.currentSoilCell instanceof C_BurrowSystem) candidateBurrows.add(currentSoilCell);
			else {
				for (I_SituatedThing oneThingPerceived : perceivedThings) {
					if (oneThingPerceived instanceof C_BurrowSystem) {
						candidateBurrows.add((C_BurrowSystem) oneThingPerceived);
					}
				}
				if (this.lastContainerLeft instanceof C_BurrowSystem) candidateBurrows.remove(this.lastContainerLeft);
				if (candidateBurrows.isEmpty() && this.testFemale()) candidateBurrows.add(this.actionDig());
			}
			return candidateBurrows;
		}
		else return super.deliberation(perceivedThings);
	}
}
