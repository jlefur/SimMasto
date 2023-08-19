package thing.ground;

import data.C_Parameters;
import data.constants.I_ConstantGerbil;

/** Soil unit able to support rain
 * @author JLF MS 10-2015 */
public class C_SoilCellSavanna extends C_SoilCell implements I_ConstantGerbil {
	//
	// FIELD
	//
	private int rainLevel;

	//
	// CONSTRUCTOR
	//
	public C_SoilCellSavanna(int aff, int lineNo, int colNo) {
		super(aff, lineNo, colNo);
	}
//	@Override
//	public int getAffinity() {
//		double usefullBiomass = 0.;
//		int cropNumber = 0, grassNumber = 0;
//		for (I_SituatedThing oneThing : this.getOccupantList()) {
//			if (oneThing instanceof C_Vegetation) {
//				if (((C_Vegetation) oneThing).getGenome() instanceof C_GenomeFabacea) {
//					cropNumber++;
//					usefullBiomass += ((C_Vegetation) oneThing).getBiomass_Ugram();
//				}
//				if (((C_Vegetation) oneThing).getGenome() instanceof C_GenomePoacea) {
//					grassNumber++;
//					usefullBiomass += ((C_Vegetation) oneThing).getBiomass_Ugram();
//				}
//			}
//		}
//		return  (this.getCarryingCapacity_Urodent() + (int)Math.round((usefullBiomass / (grassNumber * maxGrassBiomassCarryingCapacityInCell_UgramPerSquareMeter
//				+ cropNumber * maxCropBiomassCarryingCapacityInCell_UgramPerSquareMeter) * C_Parameters.CELL_WIDTH_Umeter * C_Parameters.CELL_WIDTH_Umeter)));
//	}
	//
	// SETTER & GETTER
	//
	public void setRainLevel(int rainLevel) {
		this.rainLevel = rainLevel;
	}
	public int getRainLevel() {
		return this.rainLevel;
	}
	@Override
	public int getCarryingCapacity_Urodent() {
		return (int) Math.ceil(CARRYING_CAPACITY_SAVANNA_UrodentPerSquareMeter * C_Parameters.CELL_WIDTH_Umeter * C_Parameters.CELL_WIDTH_Umeter);
	}
}
