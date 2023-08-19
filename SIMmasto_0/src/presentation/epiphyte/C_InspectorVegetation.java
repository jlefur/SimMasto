package presentation.epiphyte;

import java.util.TreeSet;

import data.constants.I_ConstantGerbil;
import repast.simphony.essentials.RepastEssentials;
import simmasto0.protocol.A_Protocol;
import thing.C_Vegetation;
import thing.dna.species.C_GenomeBalanites;
import thing.dna.species.C_GenomeFabacea;
import thing.dna.species.C_GenomePoacea;
import thing.ground.C_SoilCellSavanna;
import thing.ground.I_Container;

/** Data inspector: retrieves informations e.g. la liste des végétations.
 * @author M SALL 02.2016 */
public class C_InspectorVegetation extends A_Inspector implements I_ConstantGerbil {
	//
	// FIELD
	//
	private TreeSet<C_Vegetation> vegetationList = new TreeSet<C_Vegetation>();
	I_Container[][] landscapeMatrix_Ucell = null;
	//
	// CONSTRUCTOR
	//
	public C_InspectorVegetation() {
		super();
		vegetationList.clear();
		// add to the super class header, this proper header
		indicatorsHeader = "tick;Shrubs Biomass;Crops Biomass;Grasses Biomass;Average Precipitation";
	}
	public C_InspectorVegetation(I_Container[][] soilcellsMatrix_Ucell) {
		super();
		this.landscapeMatrix_Ucell = soilcellsMatrix_Ucell;
		vegetationList.clear();
		// add to the super class header, this proper header
		indicatorsHeader = "tick;Shrubs Biomass;Crops Biomass;Grasses Biomass;Average Precipitation";
	}
	//
	// METHOD
	//
	@Override
	/** Store the current state of indicators in the field including the super ones
	 * @see A_Protocol#recordIndicatorsInFile
	 * @revision MS 2016, JLF&MS 05.2017 */
	public String indicatorsStoreValues() {
		indicatorsValues = RepastEssentials.GetTickCount() + CSV_FIELD_SEPARATOR + this.getShrubEnergy()
				+ CSV_FIELD_SEPARATOR + this.getCropEnergy() + CSV_FIELD_SEPARATOR + this.getGrassEnergy()
				+ CSV_FIELD_SEPARATOR + getAveragePrecipitation_Umm();
		return indicatorsValues;
	}
	public void addVegetationToList(C_Vegetation oneVegetation) {
		if (!this.vegetationList.add(oneVegetation))
			A_Protocol.event("C_InspectorVegetation.addVegetationToList()", "could not add " + oneVegetation,
					isNotError);
	}
	public double getAveragePrecipitation_Umm() {
		int nrow = this.landscapeMatrix_Ucell.length;
		int ncol = this.landscapeMatrix_Ucell[0].length;
		return this.getTotalPrecipitation_Umm() / (nrow * ncol);
	}
	public double getTotalPrecipitation_Umm() {
		double precipitationValue_Umm = .0;
		int nrow = this.landscapeMatrix_Ucell.length;
		int ncol = this.landscapeMatrix_Ucell[0].length;
		for (int i = 0; i < nrow; i++) {
			for (int j = 0; j < ncol; j++) {
				int value = ((C_SoilCellSavanna) this.landscapeMatrix_Ucell[i][j]).getRainLevel();
				switch (value) {
					case 2 :
						precipitationValue_Umm += 10;
						break;
					case 3 :
						precipitationValue_Umm += 25;
						break;
					case 4 :
						precipitationValue_Umm += 60;
						break;
					case 5 :
						precipitationValue_Umm += 115;
						break;
					case 6 :
						precipitationValue_Umm += 175;
						break;
					case 7 :
						precipitationValue_Umm += 235;
						break;
					case 8 :
						precipitationValue_Umm += 270;
						break;
				}
			}
		}
		return precipitationValue_Umm;
	}
	//
	// GETTERS
	//
	public TreeSet<C_Vegetation> getVegetationList() {
		return this.vegetationList;
	}
	public double getShrubEnergy() {
		double shrubEnergy = .0;
		for (C_Vegetation one_Vegetation : this.vegetationList) {
			if (one_Vegetation.getGenome() instanceof C_GenomeBalanites)
				shrubEnergy += one_Vegetation.getEnergy_Ukcal();
		}
		return shrubEnergy;
	}
	public double getGrassEnergy() {
		double grassEnergy = .0;
		for (C_Vegetation one_Vegetation : this.vegetationList) {
			if (one_Vegetation.getGenome() instanceof C_GenomePoacea) grassEnergy += one_Vegetation.getEnergy_Ukcal();
		}
		return grassEnergy;
	}
	public double getCropEnergy() {
		double cropEnergy = .0;
		for (C_Vegetation one_Vegetation : this.vegetationList) {
			if (one_Vegetation.getGenome() instanceof C_GenomeFabacea) cropEnergy += one_Vegetation.getEnergy_Ukcal();
		}
		return cropEnergy;
	}
}
