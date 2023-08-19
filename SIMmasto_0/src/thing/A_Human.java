package thing;
import thing.dna.I_DiploidGenome;
import thing.ground.I_Container;
import data.constants.I_ConstantString;
import data.constants.I_ConstantTransportation;

/** A human owns a room.
 * @author Sall 02-10-2018,rev. J.Le Fur 06.2022 */
public abstract class A_Human extends A_Amniote implements I_ConstantString, I_ConstantTransportation {
	//
	// CONSTRUCTOR
	//
	/** used to allow the giveBirth procedure
	 * @see #giveBirth */
	public A_Human(I_DiploidGenome genome) {
		super(genome);
	}
	//
	// OVERRIDEN METHODS
	//
	/** Do not interact with other animals */
	@Override
	protected boolean actionInteract(A_Animal animal) {
		return false;
	}
	/** Do not check death */
	@Override
	public void checkDeath(double deathProbability) {
		this.dead = false;
	}
	/** Do not check danger (no dangerous area) jlf 02.2020 */
	@Override
	protected void checkDanger() {}
}
