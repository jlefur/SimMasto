/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing.ground;

import thing.A_Animal;
import thing.I_SituatedThing;
import thing.ground.landscape.C_Landscape;

/** Crops are specific landplots that can be ploughed
 * @see C_LandPlot
 * @author jlefur 08.2015 */
public class C_CropLandPlot extends C_LandPlot {
	//
	// CONSTANT
	//
	private final double PLOUGHING_MORTALITY_RATE = .8;// induce a mortality when ploughed (=.8 expertise from B.Gauffre, 01.2012)
	//
	// CONSTRUCTOR
	//
	public C_CropLandPlot(C_Landscape groundManager) {
		super(groundManager);
	}
	//
	// METHOD
	//
	/** Induce a mortality when a landPlot is ploughed (value = .8 expertise from B.Gauffre, 01.2012) */
	public void ploughing() {
		for (C_SoilCell oneSoilCell : this.cells) {
			for (I_SituatedThing inhabitant : oneSoilCell.getFullRodentList()) {
				if (inhabitant instanceof A_Animal) ((A_Animal) inhabitant).checkDeath(PLOUGHING_MORTALITY_RATE);
			}
		}
	}
}