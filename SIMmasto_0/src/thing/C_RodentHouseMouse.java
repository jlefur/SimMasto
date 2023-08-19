package thing;

import java.util.TreeSet;

import thing.dna.I_DiploidGenome;
import thing.ground.C_Vehicle;

/** Used only for display, JLF 01.2017 */
public class C_RodentHouseMouse extends A_RodentCommensalSimplified {

	public C_RodentHouseMouse(I_DiploidGenome genome) {
		super(genome);
	}
	/** generate a new animal, necessary to use spawn
	 * @see A_Amniote#spawn */
	@Override
	public A_Animal giveBirth(I_DiploidGenome genome) {
		return new C_RodentHouseMouse(genome);
	}
	/** Mouse board only in taxis JLF 01,10.2017 */
	protected boolean actionTryBoardingContainer(C_Vehicle vehicle) {
		if (vehicle.getType().equals(TAXI_EVENT)) {
			return super.actionTryBoardingContainer(vehicle);
		}
		else return false;
	}
	/** Select the available taxis in the surrounding
	 * @param perceivedThings TreeSet <I_situated_thing> listeVisibleObjects from perception method
	 * @return candidate taxis<br>
	 *         Version J.Le Fur 2011 / jlefur 03.2012 / Complete rev. JLF 08,10.2017 */
	protected TreeSet<I_SituatedThing> deliberation(TreeSet<I_SituatedThing> perceivedThings) {
		if (this.getDesire().equals(FEED)) {
			TreeSet<I_SituatedThing> candidateTargets = new TreeSet<I_SituatedThing>();// There may be several equivalent targets
			for (I_SituatedThing oneThingPerceived : perceivedThings)
				if ((oneThingPerceived instanceof C_Vehicle) && (((C_Vehicle) oneThingPerceived).getType().equals(TAXI_EVENT))) {
					candidateTargets.add(oneThingPerceived);
				}
			return candidateTargets;
		}
		else return super.deliberation(perceivedThings);
	}
}
