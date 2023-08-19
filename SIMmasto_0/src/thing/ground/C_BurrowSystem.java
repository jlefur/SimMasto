/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt  */
package thing.ground;

import data.C_Parameters;
import thing.I_SituatedThing;

public class C_BurrowSystem extends A_SupportedContainer {
	//
	// CONSTRUCTOR
	//
	/** Constructor sets the affinity to 10 */
	public C_BurrowSystem(int affinity, int lineNo, int colNo) {
		super(affinity, lineNo, colNo);
	}
	//
	// METHOD
	//
	/** If burrow is empty then do not exceed EMPTY_BURROW_LIFESPAN_Uday before being removed, reset age when occupied<br>
	 * JLF 03.2018 */
	@Override
	public double computeDeathProbability_Uday() {
		// TODO JLF 2020.04 create getLoad_Uagent, better than getFullLoad_Uagent
		if (!C_Parameters.PERSISTANCE_BURROW && (this.getFullLoad_Uagent() == 0) && this.getAnimalsTargetingMe()
				.isEmpty()) return this.getAge_Uday() / EMPTY_BURROW_LIFESPAN_Uday;
		else {
			this.setAge_Uday(0.);
			return 0.;
		}
	}
	@Override
	/** If currently vanishing and incomer arrive, reactivate JLF 2018 */
	public boolean agentIncoming(I_SituatedThing thing) {
		if (!C_Parameters.PERSISTANCE_BURROW && this.isDead()) this.setDead(false);
		return super.agentIncoming(thing);
	}
	//
	// GETTER
	//
	/** Display/Enlight saturated burrows (= N/K) in other color JLF 2016 */
	public double getRed() {
		if (this.isDead()) return 0;// to avoid probe crash (JLF 03.2021)
		double x = this.getCarryingCapacity_Urodent();
		double y = this.getRodentList().size();
		return y / x;
	}
}