package thing;

import java.util.Calendar;
import java.util.TreeSet;

import simmasto0.protocol.A_Protocol;
import thing.dna.I_DiploidGenome;
import data.constants.I_ConstantDodel;

/** Define rodent agents that account for hourly change of their environment (e.g., suspend activity during daytime)<br>
 * created for protocol Dodel
 * @author Jean Le Fur, march 2015 */
public class C_RodentCircadian extends C_RodentCommensal {
	//
	// FIELD
	//
	double speed_Origin;
	//
	// CONSTRUCTOR
	//
	public C_RodentCircadian(I_DiploidGenome genome) {
		super(genome);
		speed_Origin = speed_UmeterByTick;
	}
	//
	// OVERRIDEN METHODS
	//
	/** generate a new animal, necessary to use spawn
	 * @see A_Amniote#spawn */
	@Override
	public A_Animal giveBirth(I_DiploidGenome genome) {
		return new C_RodentCircadian(genome);
	}
	/** TODO JLF 2020.01 to remove<br>
	 * Do not test death as domestic rodent may often be within a dangerous area JLF&MS 01.2017 */
	@Override
	protected void checkDanger() {}

	/** Transform Perception according time / author: M.Diakhate, rev. J.Le Fur 11.2016 */
	@Override
	protected TreeSet<I_SituatedThing> perception() {
		double sensing = this.sensing_UmeterByTick;
		int hourOfDay = A_Protocol.protocolCalendar.get(Calendar.HOUR_OF_DAY);
		// Low perception during daylight (hidden)
		if ((hourOfDay >= I_ConstantDodel.DAY_START_Uhour) && (hourOfDay <= I_ConstantDodel.DAY_END_Uhour)) {
			sensing = (double) sensing_UmeterByTick / I_ConstantDodel.RODENT_DECREASE_PERCEPTION;
		}
		// greater perception of rodent at dawn and twilight
		else if ((I_ConstantDodel.TWILIGHT_START_Uhour <= hourOfDay && hourOfDay <= I_ConstantDodel.TWILIGHT_END_Uhour)
				|| (I_ConstantDodel.DAWN_START_Uhour <= hourOfDay && hourOfDay <= I_ConstantDodel.DAWN_END_Uhour)) {
			sensing = (double) sensing_UmeterByTick * I_ConstantDodel.RODENT_INCREASE_PERCEPTION;
		}
		return myLandscape.findObjectsOncontinuousSpace(this, sensing);
	}
	/** Transform Speed according time / author: M.Diakhate, rev. J.Le Fur 11.2016 */
	@Override
	public void computeNextMoveToTarget() {
		this.speed_UmeterByTick = speed_Origin;
		int hourOfDay = A_Protocol.protocolCalendar.get(Calendar.HOUR_OF_DAY);
		// No activities between 9 and 18h
		if (I_ConstantDodel.DAY_START_Uhour <= hourOfDay && hourOfDay <= I_ConstantDodel.DAY_END_Uhour) {
			this.speed_UmeterByTick = this.speed_Origin / I_ConstantDodel.SPEED_SMALL_ACTIVITIES;
		}
		// More activities of rodent between these moments
		else if ((I_ConstantDodel.TWILIGHT_START_Uhour <= hourOfDay && hourOfDay <= I_ConstantDodel.TWILIGHT_END_Uhour)
				|| (I_ConstantDodel.DAWN_START_Uhour <= hourOfDay && hourOfDay <= I_ConstantDodel.DAWN_END_Uhour)) {
			this.speed_UmeterByTick = this.speed_Origin * I_ConstantDodel.SPEED_SMALL_ACTIVITIES;
		}
		super.computeNextMoveToTarget(speed_UmeterByTick);
	}
}
