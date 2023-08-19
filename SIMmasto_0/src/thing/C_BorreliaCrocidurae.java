package thing;

import data.constants.I_ConstantDodel2;
import thing.dna.I_DiploidGenome;

/** This class accounts for Tick .
 * @author M. Sall 04.2020 */
public class C_BorreliaCrocidurae extends A_Animal implements I_ConstantDodel2 {
	//
	// CONSTRUCTOR
	//
	public C_BorreliaCrocidurae(I_DiploidGenome genome) {
		super(genome);
	}
	//
	// OVERRIDEN METHODS
	//
	public A_Animal giveBirth(I_DiploidGenome genome) {
		C_BorreliaCrocidurae oneBorrelia = new C_BorreliaCrocidurae(genome);
		oneBorrelia.setTrappedOnBoard(true);
		return oneBorrelia;
	}
	@Override
	public void step_Utick() {
	    super.step_Utick();
	}
	public double getEnergy_Ukcal() {// for GUI display
		return energy_Ukcal;
	}
}
