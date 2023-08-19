/* This source code is licensed under a BSD license as detailed in file SIMmasto_0.license.txt */
package thing;

import java.util.TreeSet;

import thing.dna.I_DiploidGenome;
import thing.dna.species.C_GenomeBalanites;
import thing.dna.species.C_GenomeFabacea;
import thing.dna.species.C_GenomePoacea;
import thing.dna.species.C_GenomeSpermatophyta;
import thing.ground.A_Container;
import thing.ground.C_SoilCell;
import thing.ground.C_SoilCellSavanna;
import data.C_Parameters;
import data.constants.I_ConstantGerbil;
import data.converters.C_ConvertTimeAndSpace;
import simmasto0.protocol.A_Protocol;
/** Vegetation objects grow depending on rain intensity.
 * @author M.Sall 10.2015, rev. JLF&MS 03.2016, 05.2017 */
public class C_Vegetation extends A_Organism implements I_ConstantGerbil {
	//
	// FIELDS
	//
	/** Used to speed computation by avoiding access to genome */
	private double growthRate_UkcalPerDay;
	/** 6-methoxybenzoquinone is a molecule produced by young shoots that may trigger rodents'reproduction */
	protected boolean methoxybenzoxazolinone;
	//
	// CONSTRUCTOR
	//
	/** Set vegetation type, initial biomass and growth rate */
	public C_Vegetation(I_DiploidGenome genome) {
		super(genome);
		this.growthRate_UkcalPerDay = ((C_GenomeSpermatophyta) this.getGenome()).getGrowthRateValue();
		this.methoxybenzoxazolinone = false;
		// TODO number in source JLF 02.2021 tree grow less than shrub than grass than crop
		if (genome instanceof C_GenomeBalanites) this.energy_Ukcal = INITIAL_VEGET_ENERGY;// shrub
		else if (genome instanceof C_GenomePoacea) this.energy_Ukcal = INITIAL_VEGET_ENERGY * 2.;// wild grass
		else if (genome instanceof C_GenomeFabacea) this.energy_Ukcal = INITIAL_VEGET_ENERGY * 3.;// sahelian crop
		else this.energy_Ukcal = INITIAL_VEGET_ENERGY / 2.;
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

		double carryingCapacity_Ukcal;
		// JLF 03.2016 rain classes start at 1
		int rainLevel = (((C_SoilCellSavanna) this.currentSoilCell).getRainLevel());
		I_DiploidGenome genome = this.genome;
		double previousEnergy_Ukcal = this.energy_Ukcal;

		// Compute carrying capacity given the class of rain level within current cell
		if (genome instanceof C_GenomeBalanites) carryingCapacity_Ukcal = rainLevel * INITIAL_VEGET_ENERGY;// shrub
		else if (genome instanceof C_GenomePoacea) carryingCapacity_Ukcal = rainLevel * INITIAL_VEGET_ENERGY * 2;// wild grass
		else if (genome instanceof C_GenomeFabacea) carryingCapacity_Ukcal = rainLevel * INITIAL_VEGET_ENERGY * 3;// sahelian crop
		else carryingCapacity_Ukcal = rainLevel * INITIAL_VEGET_ENERGY;

		double oneDay_Utick = C_ConvertTimeAndSpace.convertTimeDurationToTick(1, "d");
		// Compute energy change using the logistic law for grass, crop, shrub or tree
		this.energy_Ukcal += (this.growthRate_UkcalPerDay * rainLevel) / oneDay_Utick * (1 - this.energy_Ukcal
				/ carryingCapacity_Ukcal);

		// growth of young shoots triggers the production of methoxybenzoalinone
		if (previousEnergy_Ukcal >= this.energy_Ukcal) methoxybenzoxazolinone = false;
		else methoxybenzoxazolinone = true;
		// If negative energy, remove occupants and set dead
		if (this.energy_Ukcal <= 0) {
			this.setDead(true);
			if (C_Parameters.VERBOSE)
				A_Protocol.event("C_Vegetation.actionGrowOlder_Utick()", "VEGETATION event: " + this
						+ " too eaten and disappear", isNotError);
		}
	}

	@Override
	public void discardThis() {
		Object[] occupants = this.getOccupantList().toArray();
		for (Object occupant : occupants) {
			if (occupant instanceof A_Animal) {
				((A_Animal) occupant).actionRandomExitOfContainer();
				((A_Animal) occupant).actionDisperse();
				((A_Animal) occupant).discardTarget();
			}
			else
				if (occupant instanceof A_Container)
					A_VisibleAgent.myLandscape.moveToContainer((I_SituatedThing) occupant, this.currentSoilCell);
				else
					A_Protocol.event("C_Vegetation:discardThis", "cannot remove " + occupant + " from " + this,
							isError);
		}
		this.getOccupantList().clear();
		super.discardThis();
	}
	/** Compute the cell area */
	protected double cellArea_UsquareMeter() {
		return C_Parameters.CELL_WIDTH_Umeter * C_Parameters.CELL_WIDTH_Umeter;
	}
	//
	// SETTERS & GETTERS
	//
	public boolean getMethoxybenzoxazolinone() {
		return this.methoxybenzoxazolinone;
	}
	/** if animal check danger in vegetation, affinity must not be 0 / JLF 03.2021 */
	public int getAffinity() {
		return (int) Math.abs(this.energy_Ukcal);
	}
	/** TODO JLF 2020.11 test bug probe vegetation */
	@Override
	public String getAlleles() {
		return "To come";
	}
	@Override
	public int getCell2Perception() {
		return 0;
	}

}