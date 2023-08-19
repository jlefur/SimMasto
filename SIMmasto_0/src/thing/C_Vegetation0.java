/* This source code is licensed under a BSD license as detailed in file SIMmasto_0.license.txt */
package thing;

import java.util.TreeSet;

import thing.dna.I_DiploidGenome;
import thing.dna.species.C_GenomeBalanites;
import thing.dna.species.C_GenomeFabacea;
import thing.dna.species.C_GenomePoacea;
import thing.dna.species.C_GenomeSpermatophyta;
import thing.ground.C_SoilCell;
import thing.ground.C_SoilCellSavanna;
import data.C_Parameters;
import data.constants.I_ConstantGerbil;
import data.converters.C_ConvertTimeAndSpace;
/** Vegetation objects grow depending on rain intensity.
 * @author M.Sall 10.2015, rev. JLF&MS 03.2016, 05.2017 */
public class C_Vegetation0 extends A_Organism implements I_ConstantGerbil {
	//
	// FIELDS
	//
	private double biomass_Ugram;
	/** Used to speed computation by avoiding access to genome */
	private double growthRate_UgramPerDay;
	/** 6-methoxybenzoquinone is a molecule produced by young shoots that may trigger rodents'reproduction */
	protected boolean methoxybenzoxazolinone;
	//
	// CONSTRUCTOR
	//
	/** Set vegetation type, initial biomass and growth rate */
	public C_Vegetation0(I_DiploidGenome genome) {
		super(genome);
		this.growthRate_UgramPerDay = ((C_GenomeSpermatophyta) this.getGenome()).getGrowthRateValue();
		this.methoxybenzoxazolinone = false;
		if (genome instanceof C_GenomePoacea)
			this.biomass_Ugram = initialGrassBiomass_UgramPerSquareMeter * this.cellArea_UsquareMeter();
		else
			if (genome instanceof C_GenomeFabacea)
				this.biomass_Ugram = initialCropBiomass_UgramPerSquareMeter * this.cellArea_UsquareMeter();
			else
				if (genome instanceof C_GenomeBalanites)
					this.biomass_Ugram = initialCropBiomass_UgramPerSquareMeter * this.cellArea_UsquareMeter();// TODO JLF 2021.02 temp. crop in place of shrub
				else
					this.biomass_Ugram = initialVegetationBiomass_Ugram;
	}
	//
	// METHODS
	//
	protected TreeSet<C_SoilCell> deliberation(TreeSet<I_SituatedThing> perceivedThings) {
		return new TreeSet<C_SoilCell>();
	}
	/** Release seeds at a given threshold */
	public void seedsProductionTODO() {}
	/** Compute the change of biomass given the level of rain in the cell.<br>
	 * The model follow a logistic law : biomass <- biomass(t-1) * (1 + growth_rate * ( 1 - biomass(t-1) / carrying_capacity))
	 * Version J.Le Fur & M.Sall, 02-03 2016 */
	@Override
	public void actionGrowOlder_Utick() {
		super.actionGrowOlder_Utick();
		I_DiploidGenome myGenome = this.genome;
		double previousBiomass_Ugram = this.biomass_Ugram;
		if ((myGenome instanceof C_GenomeFabacea) || (myGenome instanceof C_GenomePoacea)) {// Only crop and grass grow
			double oneDay_Utick = C_ConvertTimeAndSpace.convertTimeDurationToTick(1, "d");
			// Compute carrying capacity given the class of rain intensity
			// Compute vegetation carrying capacity with cell size
			// TODO number in source OK JLF 03.2016 rain classes start at 1
			double carryingCapacityWithRain_Ugram = VegetationCarryingCapacity_UgramPerSquareMeter * this
					.cellArea_UsquareMeter() + (RAIN_VALUE_MULTIPLIER
							* (((C_SoilCellSavanna) this.currentSoilCell).getRainLevel() - 1));
			// Compute biomass change using the logistic law for grass, crop and shrub
			this.biomass_Ugram += (this.growthRate_UgramPerDay * ((C_SoilCellSavanna) this.currentSoilCell)
					.getRainLevel() * SENSITIVITY_TO_RAIN) / oneDay_Utick * (1 - this.biomass_Ugram
							/ carryingCapacityWithRain_Ugram);

			this.biomass_Ugram += (this.growthRate_UgramPerDay * ((C_SoilCellSavanna) this.currentSoilCell)
					.getRainLevel() * SENSITIVITY_TO_RAIN) / oneDay_Utick * (1 - this.biomass_Ugram
							/ carryingCapacityWithRain_Ugram);
		}
		// growth of young shoots triggers the production of methoxybenzoalinone
		if (previousBiomass_Ugram >= this.biomass_Ugram) methoxybenzoxazolinone = false;
		else methoxybenzoxazolinone = true;
	}
	/** Compute the cell area */
	protected double cellArea_UsquareMeter() {
		return C_Parameters.CELL_WIDTH_Umeter * C_Parameters.CELL_WIDTH_Umeter;
	}
	//
	// SETTERS & GETTERS
	//
	public void setBiomass_Ugram(double growBiomass_Ugram) {
		this.biomass_Ugram = growBiomass_Ugram;
	}
	public double getBiomass_Ugram() {
		return biomass_Ugram;
	}
	public boolean getMethoxybenzoxazolinone() {
		return this.methoxybenzoxazolinone;
	}
	/** TODO JLF 2020.11 test bug probe vegetation */
	@Override
	public String getAlleles() {
		return "To come";
	}
	@Override
	public int getCell2Perception() {return 0;}
}
