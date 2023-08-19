package thing;

import java.util.Calendar;
import java.util.TreeSet;

import data.constants.I_ConstantNumeric;
import simmasto0.protocol.A_Protocol;
import thing.dna.I_DiploidGenome;
import thing.ground.C_Vehicle;

/** Simplified rodent for transportation protocol: do not move outside this city, perceive only this current cell content
 * @author J.Le Fur 04.2015 */
public abstract class A_RodentCommensalSimplified extends C_RodentCommensal {

	public A_RodentCommensalSimplified(I_DiploidGenome genome) {
		super(genome);
	}
	// @Override
	// public void step_Utick() {
	// if (this.trappedOnBoard || ((C_SoilCellGraphed) currentSoilCell).getGroundTypes().contains(CITY))
	// super.step_Utick();
	// else this.dead = true;
	// }
	// TODO JLF 2022.04 Remove this below; used for test only
	// @Override
	// public boolean actionMateWithMale(I_ReproducingThing male) {this.setDesire("");return true;}
	// @Override
	// protected double computeDeathProbability_Uday() {return I_ConstantNumeric.DEFAULT_DEATH_PROBABILITY_UperDay;}

	/** Cities affinity are not significant for this / JLF 01.2017 */
	@Override
	protected void checkDanger() {};
	/** Test is agent arrives to destination within one tick. If yes next move = (0,0)
	 * @param speed the speed of the agent or agent's vehicle
	 * @author LeFur 08.2012, 08.2017 */
	@Override
	protected boolean isArrived(double speed) {
		if (this.target instanceof C_Vehicle) return true;
		else return super.isArrived(speed);
	}

	/** Do not perform exact perception radius computation: only perceive this current cell objects */
	@Override
	protected TreeSet<I_SituatedThing> perception() {
		return this.currentSoilCell.getOccupantList();
	}
	/** Do not leave the cityCell */
	protected void actionForage() {}
	/***/
	public void actionMove() {}
	/** Select the available vehicle in the surrounding
	 * @param perceivedThings TreeSet <I_situated_thing> listeVisibleObjects from perception method
	 * @return candidate targets<br>
	 *         Version J.Le Fur 2011 / jlefur 03.2012 / Complete rev. JLF 08,10.2017 */
	protected TreeSet<I_SituatedThing> deliberation(TreeSet<I_SituatedThing> perceivedThings) {
		if (this.getDesire().equals(FEED)) {
			TreeSet<I_SituatedThing> candidateTargets = new TreeSet<I_SituatedThing>();// There may be several equivalent targets
			for (I_SituatedThing oneThingPerceived : perceivedThings) if (oneThingPerceived instanceof C_Vehicle) {
				candidateTargets.add(oneThingPerceived);
			}
			return candidateTargets;
		}
		else return super.deliberation(perceivedThings);
	}
}
