/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package presentation.epiphyte;

import java.util.Iterator;
import java.util.TreeSet;

import repast.simphony.engine.environment.RunState;
import presentation.dataOutput.C_FileWriter;

import data.C_Parameters;
import repast.simphony.essentials.RepastEssentials;
import simmasto0.protocol.A_Protocol;
import thing.C_Rodent;
import thing.C_RodentDomestic2;
import thing.I_SituatedThing;
import thing.ground.I_Container;

/** Data inspector: retrieves informations e.g. population sizes and manages lists.
 * @author A Realini 05.2011 / J.Le Fur 09.2011, 07.2012, 01.2013 */
public class C_InspectorPopulation extends A_Inspector {
	//
	// FIELDS
	//
	public static TreeSet<C_Rodent> rodentList = new TreeSet<C_Rodent>();
	protected static TreeSet<C_Rodent> rodentBirthList = new TreeSet<C_Rodent>();
	protected static int nbMales = 0, nbFemales = 0;
	protected int nbDeath = 0; // nbDeath is retrieved from the A_Protocol removeDeadRodents() procedure / JLF 02.2013
	protected int nbBirth = 0;
	protected int dispersedRodents = 0;
	protected double sexRatio = 0., deathRatio = 0., birthRatio = 0.;
	protected double maxDispersal = 0., maxFemaleDispersal = 0., maxMaleDispersal = 0., meanFemaleDispersal = 0., meanMaleDispersal = 0.;
	protected C_FileWriter SpatialDistributionFile;
	//
	// CONSTRUCTOR
	//
	public C_InspectorPopulation() {
		super();
		rodentList.clear();
		rodentBirthList.clear();
		this.indicatorsHeader = this.indicatorsHeader
				+ ";PopSize;SexRatio;meanFemaleDispersal;meanMaleDispersal;maxFemaleDispersal;maxMaleDispersal;dispersedRodents;nbBirth;nbDeath";
		SpatialDistributionFile = new C_FileWriter("SpatialDistribution.csv", true);
	}
	//
	// METHODS
	//
	public void indicatorsCompute() {
		// Population size equal living, newborns and 'dead during this tick' rodents
		double rodentPopSize = (double) (C_InspectorPopulation.rodentList.size() + C_InspectorPopulation.rodentBirthList.size() + this.nbDeath);
		if (rodentPopSize > 0) {
			nbMales = 0;
			nbFemales = 0;
			int nbDispersedMales = 0;
			int nbDispersedFemales = 0;
			dispersedRodents = 0;
			maxDispersal = 0.;
			maxFemaleDispersal = 0.;
			maxMaleDispersal = 0.;
			meanFemaleDispersal = 0.;
			meanMaleDispersal = 0.;
			deathRatio = (double) nbDeath / rodentPopSize;
			birthRatio = (double) rodentBirthList.size() / rodentPopSize;
			nbBirth = rodentBirthList.size();
			// Transfer newborns to rodents list
			for (C_Rodent newBorn : rodentBirthList)
				rodentList.add(newBorn);
			rodentBirthList.clear();
			// Scan the rodents'list to compute indicators
			Iterator<C_Rodent> rodents = rodentList.iterator();
			while (rodents.hasNext()) {
				C_Rodent rodent = rodents.next();
				// double dispersalDistance = rodent.getCurrentDispersalDistance_Umeter();
				double dispersalDistance = rodent.getMaxDispersalDistance_Umeter();
				if (dispersalDistance > maxDispersal) maxDispersal = dispersalDistance;
				if (rodent.testMale()) {
					nbMales++;
					// if (rodent.getMaxDispersalDistance_Umeter() > rodent.getSensing_UmeterByTick()) {
					meanMaleDispersal += dispersalDistance;
					nbDispersedMales++;
					if (dispersalDistance > maxMaleDispersal) maxMaleDispersal = dispersalDistance;
					// }
				}
				else if (rodent.testFemale()) {
					nbFemales++;
					// if (rodent.getMaxDispersalDistance_Umeter() > rodent.getSensing_UmeterByTick()) {
					meanFemaleDispersal += dispersalDistance;
					nbDispersedFemales++;
					if (dispersalDistance > maxFemaleDispersal) maxFemaleDispersal = dispersalDistance;
					// }
				}
				else System.err.println("C_InspectorPopulation.computeRodentIndicators(): neither male or female; hybrid = "
						+ rodent.getGenome().isHybrid());

			}
			meanMaleDispersal = meanMaleDispersal / (double) nbDispersedMales;
			meanFemaleDispersal = meanFemaleDispersal / (double) nbDispersedFemales;
			dispersedRodents = nbDispersedFemales + nbDispersedMales;
			/** Compute sex ratio (Males/Females). simulation will stop if there are no more females */
			if (nbFemales > 0) sexRatio = (double) nbMales / (double) nbFemales;
			/*
			 * else { System.err.println(RepastEssentials.GetTickCount() + " No females: unable to compute sex ratio -> = -1"); sexRatio = -1; }
			 */
		}
	}
	@Override
	/** stores the current state of indicators in the field including the super ones / JLF 01.2013 */
	public String indicatorsStoreValues() {
		indicatorsValues = super.indicatorsStoreValues() + CSV_FIELD_SEPARATOR + rodentList.size() + CSV_FIELD_SEPARATOR + sexRatio
				+ CSV_FIELD_SEPARATOR + meanFemaleDispersal + CSV_FIELD_SEPARATOR + meanMaleDispersal + CSV_FIELD_SEPARATOR + maxFemaleDispersal
				+ CSV_FIELD_SEPARATOR + maxMaleDispersal + CSV_FIELD_SEPARATOR + dispersedRodents + CSV_FIELD_SEPARATOR + nbBirth
				+ CSV_FIELD_SEPARATOR + nbDeath;
		return indicatorsValues;
	}
	/** Output data in genePop file */
	public void recordSpatialDistributionInFile(I_Container[][] grid) {// Simultech 2018
		SpatialDistributionFile.writeln("step;Tick;HourDate;objects");
		SpatialDistributionFile.writeln(C_Parameters.TICK_LENGTH_Ucalendar +" "+C_Parameters.TICK_UNIT_Ucalendar+ CSV_FIELD_SEPARATOR + String.valueOf(RepastEssentials.GetTickCount())
				+ CSV_FIELD_SEPARATOR+ A_Protocol.protocolCalendar.stringHourDate() + CSV_FIELD_SEPARATOR + RunState.getInstance().getMasterContext().size());

		for (int j = 0; j < grid[0].length; j++) {
			SpatialDistributionFile.writeln("");
			SpatialDistributionFile.write(";;;;");
			for (int i = 0; i < grid.length; i++)
				SpatialDistributionFile.write(+grid[i][j].getFullLoad_Urodent() + CSV_FIELD_SEPARATOR);
		}
		SpatialDistributionFile.writeln("");
		SpatialDistributionFile.write(";;;;");
		for (int i = 0; i < grid.length; i++)
			SpatialDistributionFile.write(-4 + CSV_FIELD_SEPARATOR);// TODO JLF 2019.02 Number in source temp
		SpatialDistributionFile.writeln("");
		
/*		Iterator<C_Rodent> rodents = C_InspectorPopulation.rodentList.iterator();
			while (rodents.hasNext()) {
			C_Rodent microtus = rodents.next();
			try {
				Coordinate point = microtus.getCoords_Umeter();
				SpatialDistributionFile.write(point.x + CSV_FIELD_SEPARATOR + point.y);
				SpatialDistributionFile.writeln(CSV_FIELD_SEPARATOR + microtus.retrieveName() + CSV_FIELD_SEPARATOR + microtus.getAge_Uday()
						+ CSV_FIELD_SEPARATOR + microtus.getDesire() + CSV_FIELD_SEPARATOR + microtus.getTargetName());
			} catch (Exception e) {
				A_Protocol.event("C_InspectorGenetic.recordGenePopInFile", "Could not write " + microtus.getAge_Utick() + " / " + microtus.isDead()
						+ " (" + microtus, isError);
			}
		}*/
		
		SpatialDistributionFile.writeln("");
		A_Protocol.event("C_InspectorPopulation.recordSpatialDistributionInFile()", "distribution recorded in File", isNotError);
	}
	/** close private files */
	public void closeSimulation() {
		this.SpatialDistributionFile.closeFile();
	}
	//
	// SETTERS & GETTERS
	//
	public static void addRodentToList(C_Rodent rodent) {
		if (!rodentList.add(rodent)) A_Protocol.event("C_InspectorPopulation.addRodentToList", "Could not add " + rodent, isError);
	}
	public static void addRodentToBirthList(C_Rodent rodent) {
		if (!rodentBirthList.add(rodent)) A_Protocol.event("C_InspectorPopulation.addRodentToBirthList", "Could not add " + rodent, isError);
	}
	@Override
	public void discardDeadThing(I_SituatedThing thing) {
		if ((thing instanceof C_Rodent) && !rodentList.remove(thing)) A_Protocol.event("C_InspectorPopulation.discardDeadThing", "Could not remove "
				+ thing, isError);
	}
	public void setNbDeath_Urodent(int nbDeath) {
		this.nbDeath = nbDeath;
	}
	public double getDeathRatio() {
		return deathRatio;
	}
	public double getBirthRatio() {
		return birthRatio;
	}
	public int getNbDeath() {
		return nbDeath;
	}
	public int getNbBirth() {
		return nbBirth;
	}
	public static int getNbFemales() {
		return nbFemales;
	}
	public static int getNbMales() {
		return nbMales;
	}
	public static int getNbRodents() {
		return C_InspectorPopulation.rodentList.size();
	}
	public double getMaxFemaleDispersal() {
		return maxFemaleDispersal;
	}
	public double getMaxMaleDispersal() {
		return maxMaleDispersal;
	}
	public double getMeanFemaleDispersal() {
		return meanFemaleDispersal;
	}
	public double getMeanMaleDispersal() {
		return meanMaleDispersal;
	}
	//INFECTED RODENTS
	public int getInfectedRodents() {
		int nbInfected = 0;
		for(C_Rodent oneRodent : rodentList) 
			if (oneRodent instanceof C_RodentDomestic2 &&  oneRodent.isInfected()) nbInfected +=1;
		return nbInfected;
	}
	public int getHealthyRodent() {
		return rodentList.size() - getInfectedRodents();
	}
}
