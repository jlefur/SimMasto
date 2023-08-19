/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing.ground;

import simmasto0.C_ContextCreator;
import simmasto0.protocol.A_Protocol;
import thing.C_RodentCmr;
import thing.I_SituatedThing;

/** A trap may be opened, closed, full; it can catch rodent
 * @author J.Le Fur and M.Diakhate, july 2013, rev. 27.09.13 */
public class C_Trap extends C_BurrowSystem implements data.constants.I_ConstantBandia {
	//
	// FIELD
	//
	private boolean open = true;
	//
	// CONSTRUCTOR
	//
	public C_Trap(int affinity, int lineNo, int colNo) {
		super(affinity, lineNo, colNo);
	}
	//
	// METHODS
	//
	/** Add rodent in trap's occupantList if : 1° Trap is open, 2° Rodent is not already trapped (check), 3° Trap is not full of rodents, 4°
	 * TRAP_LOADING_PROBA >= randomProbability */
	public void trapRodent(C_RodentCmr rodentBandia) {
		// control
		if (rodentBandia.isTrappedOnBoard()) A_Protocol.event("C_Trap.trapRodent", rodentBandia + " is already trapped", isError);
		if (this.open && !rodentBandia.isTrappedOnBoard() && !this.isFull()
				&& (C_ContextCreator.randomGeneratorForDeathProb.nextDouble() <= TRAP_LOADING_PROBA)) {
			rodentBandia.setTrappedOnBoard(true);
			closeTrap();
		}
		else rodentBandia.actionRandomExitOfContainer();// put rodent besides the trap.
	}
	public void openTrap() {
		this.open = true;
		this.hasToSwitchFace = true;
	}
	public void closeTrap() {
		this.open = false;
		this.hasToSwitchFace = true;
	}
	/** One agent from the list is leaving the area of this container agent. Version author Q.Baduel 2008, rev. JLF 12.2015,01.2016 */
	@Override
	public boolean agentLeaving(I_SituatedThing thing) {
		if (super.agentLeaving(thing)) {
			this.setDead(false);// TODO JLF 2017.03 ajout de setDead
			return true;
		}
		else return false;
	}
	//
	// GETTERS
	//
	public boolean isOpen() {
		return this.open;
	}
	@Override
	public int getCarryingCapacity_Urodent() {
		return TRAP_MAX_LOAD;
	}
	/** If burrow is empty then do not exceed EMPTY_BURROW_LIFESPAN_Uday before beeing removed, reset age when occupied<br>
	 * JLF 03.2018 */
	@Override
	public double computeDeathProbability_Uday() {
		return 0.;
	}
}
