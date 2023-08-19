package thing;

import java.util.Calendar;
import java.util.TreeSet;

import simmasto0.protocol.A_Protocol;
import thing.dna.I_DiploidGenome;
import thing.ground.C_Vehicle;

/** JLF 01.2017 */
public class C_RodentBlackRat extends A_RodentCommensalSimplified {

	public C_RodentBlackRat(I_DiploidGenome genome) {
		super(genome);
	}
	/** generate a new animal, necessary to use spawn
	 * @see A_Amniote#spawn */
	@Override
	public A_Animal giveBirth(I_DiploidGenome genome) {
		return new C_RodentBlackRat(genome);
	}
	/** Black rats do not board in taxis JLF 01,10.2017 */
	protected boolean actionTryBoardingContainer(C_Vehicle vehicle) {
		if (vehicle.getType().equals(TAXI_EVENT)) return false;
		else return super.actionTryBoardingContainer(vehicle);
	}
	@Override
	/** For centenal simulation : cell affinity account for bioclimate, from favourable (12) to unfavourable (2) -> this provide
	 * the length of the breeding season for Rattus: if current month < affinity*3 readyToMate=false<br>
	 * JLeFur, 10.2016 */
	protected void updatePhysiologicStatus() {
		super.updatePhysiologicStatus();
		if (this.isSexualMature() && !this.isPregnant())
			// TODO number in source 2016.10 JLF affinity >=4 -> reproduction all year
			if (this.getCurrentSoilCell().getAffinity() * 3 >= A_Protocol.protocolCalendar.get(Calendar.MONTH)) {
				this.setDesire(REPRODUCE);
				this.readyToMate = true;
			}
			else this.readyToMate = false;
	}
	/** Select the available trucks in the surrounding
	 * @param perceivedThings TreeSet <I_situated_thing> listeVisibleObjects from perception method
	 * @return candidate taxis<br>
	 *         Version J.Le Fur 2011 / jlefur 03.2012 / Complete rev. JLF 08,10.2017 */
	protected TreeSet<I_SituatedThing> deliberation(TreeSet<I_SituatedThing> perceivedThings) {
		if (this.getDesire().equals(FEED)) {
			TreeSet<I_SituatedThing> candidateTargets = new TreeSet<I_SituatedThing>();// There may be several equivalent targets
			for (I_SituatedThing oneThingPerceived : perceivedThings)
				if ((oneThingPerceived instanceof C_Vehicle) && (((C_Vehicle) oneThingPerceived).getType().equals(TRUCK_EVENT))) {
					candidateTargets.add(oneThingPerceived);
				}
			return candidateTargets;
		}
		else return super.deliberation(perceivedThings);
	}
}
