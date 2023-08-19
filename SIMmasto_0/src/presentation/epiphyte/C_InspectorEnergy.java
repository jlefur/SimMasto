package presentation.epiphyte;

import java.util.HashMap;
import java.util.Map;

import repast.simphony.engine.environment.RunState;
import repast.simphony.essentials.RepastEssentials;
import simmasto0.util.C_VariousUtilities;
import thing.A_Organism;

/** Retrieves mean energy for each species in the context J.Le Fur 02.2018 */
public class C_InspectorEnergy extends A_Inspector {
	// the map [speciesGenome, cumulated energy]
	protected Map<String, Double> EnergyBySpecies = new HashMap<String, Double>();
	// size is necessary to compute means
	protected Map<String, Integer> sizeBySpecies = new HashMap<String, Integer>();
	/** Writer of an outer csv file */
//	private C_FileWriter dataSaverEnergyGeneral;

	public C_InspectorEnergy() {
		super();
		indicatorsHeader = "Tick;En. gerbils;En. owls;En. shrubs; En. grass; En. crop; En. trees; En. burrows;";
		/*
		 * dataSaverEnergyGeneral = new C_FileWriter("EnergyIndicators.csv", true); dataSaverEnergyGeneral.writeln(
		 * "Tick;TaillePop;NbEry;NbNat;NbLaz;NbHyb;HybridsRate;pbUnbalancedGene;pbSynteny;pbGeneNotFound;pbEspistasie;pbMatching;pbHaldane"
		 * );
		 */ }

	@Override
	public void step_Utick() {
		super.step_Utick();// compute in cascade and store indicators values
		// save private files
		writeDataToFileGeneral();
	}

	@Override
	/** stores the current state of indicators in the field including the super ones has to be conform with indicatorsHeader / JLF
	 * 01.2013 */
	public String indicatorsStoreValues() {
		indicatorsValues = RepastEssentials.GetTickCount() + CSV_FIELD_SEPARATOR;
		return (indicatorsValues);
	}
	/** compute mean energy for each taxon within the context (compute somme and size then divide and replace EnergyBySpecies) */
	@Override
	public void indicatorsCompute() {
		Object[] contextContent = RunState.getInstance().getMasterContext().toArray();
		String speciesName = "";
		this.EnergyBySpecies.clear();
		this.sizeBySpecies.clear();
		for (int i = 0; i < contextContent.length; i++) {
			Object item = contextContent[i];
			if (item instanceof A_Organism) {
				speciesName = C_VariousUtilities.getShortClassName(item.getClass()).substring(2);
				// If key exist, add values
				if (this.EnergyBySpecies.get(speciesName) != null) {
					this.EnergyBySpecies.put(speciesName, this.EnergyBySpecies.get(speciesName) + //
							((A_Organism) item).getEnergy_Ukcal());
					this.sizeBySpecies.put(speciesName, this.sizeBySpecies.get(speciesName) + 1);
				}
				// If not, create an entry and set values
				else {
					this.EnergyBySpecies.put(speciesName, ((A_Organism) item).getEnergy_Ukcal());
					this.sizeBySpecies.put(speciesName, 1);
				}
			}
		}
		// Replace energy values with mean energyvalues
		for (String key : this.EnergyBySpecies.keySet())
			this.EnergyBySpecies.put(key, this.EnergyBySpecies.get(key) / this.sizeBySpecies.get(key));
	}

	/** Writes data in the Energy indicators csv output file */
	private void writeDataToFileGeneral() {
		// dataSaverEnergyGeneral.writeln(RepastEssentials.GetTickCount() + CSV_FIELD_SEPARATOR
		// + C_InspectorPopulation.rodentList.size() + CSV_FIELD_SEPARATOR + nbGerbils + CSV_FIELD_SEPARATOR
		// + nbOwls + CSV_FIELD_SEPARATOR + nbTrees + CSV_FIELD_SEPARATOR + nbShrubs + CSV_FIELD_SEPARATOR
		// + nbGrasses + CSV_FIELD_SEPARATOR + nbCrops + CSV_FIELD_SEPARATOR + nbTrees);
	}
	/** close private files */
	public void closeSimulation() {
		// dataSaverEnergyGeneral.closeFile();
	}
	//
	// GETTER
	//
	public Map<String, Double> getEnergyBySpecies() {
		return EnergyBySpecies;
	}
}