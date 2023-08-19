package thing;

import thing.dna.I_DiploidGenome;
import thing.ground.C_Vehicle;

/** Used only for display, JLF 01.2017 */
public class C_RodentMastoErySimple extends A_RodentCommensalSimplified {

	public C_RodentMastoErySimple(I_DiploidGenome genome) {
		super(genome);
	}
	/** generate a new animal, necessary to use spawn
	 * @see A_Amniote#spawn */
	@Override
	public A_Animal giveBirth(I_DiploidGenome genome) {
		return new C_RodentMastoErySimple(genome);
	}
	/** Mastomys do not board in vehicles JLF 01,10.2017 */
	protected boolean actionTryBoardingContainer(C_Vehicle vehicle) {
		return false;
	}
}
