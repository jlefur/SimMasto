package presentation.epiphyte;

import java.util.Iterator;
import java.util.TreeSet;

import presentation.dataOutput.C_FileWriter;
import repast.simphony.essentials.RepastEssentials;
import simmasto0.C_ContextCreator;
import simmasto0.protocol.C_ProtocolCage;
import thing.C_Rodent;
import thing.C_RodentCaged;
import thing.I_SituatedThing;
import thing.dna.species.C_GenomeMastoErythroleucus;
import thing.dna.species.C_GenomeMastoNatalensis;
import thing.dna.species.C_GenomeMastomys;
import data.C_Parameters;

/** From case study number 2 - MBour M2 A.Comte
 * @author J.Le Fur & A. Comte 03/2012, J.Le Fur 01.2013 */
public class C_InspectorHybrid extends A_Inspector {
	protected TreeSet<C_Rodent> hybridList;
	protected Integer nbEry = 0;
	protected Integer nbNat = 0;
	protected Integer nbHybrids = 0;
	protected Integer nbLazarus = 0;
	protected Double hybridsRate = 0.;
	protected int pbUnbalancedGene = 0;
	protected int pbSynteny = 0;
	protected int pbMatching = 0;
	protected int pbGeneNotFound = 0;
	protected int pbEpistasie = 0;
	protected int pbHaldane = 0;
	protected int pbNippedEgg = 0;

	/** Writer of an outer csv file */
	private C_FileWriter dataSaverHybridsGeneral;
	private C_FileWriter dataSaverHybridsCages;
	private C_FileWriter dataSaverHybridsDiploidNumber;

	public C_InspectorHybrid() {
		super();
		this.indicatorsHeader = "Tick;Nb hybrids";
		this.hybridList = new TreeSet<C_Rodent>();
		this.dataSaverHybridsGeneral = new C_FileWriter("HybGeneralIndicators.csv", true);
		this.dataSaverHybridsGeneral
				.writeln("Tick;TaillePop;NbEry;NbNat;NbLaz;NbHyb;HybridsRate;pbUnbalancedGene;pbSynteny;pbGeneNotFound;pbEspistasie;pbMatching;pbHaldane");
		this.dataSaverHybridsCages = new C_FileWriter("MbourCages.csv", true);
		this.dataSaverHybridsCages.writeln("Tick;Line;Cages");
		this.dataSaverHybridsDiploidNumber = new C_FileWriter("DiploidNumber.csv", true);
		this.dataSaverHybridsDiploidNumber.writeln("Tick;8;9;10;11;12;13;14");
	}

	@Override
	public void step_Utick() {
		TreeSet<C_Rodent> rodentList = C_InspectorPopulation.rodentList;
		super.step_Utick();// compute in cascade and store indicators values
		if (rodentList.isEmpty()) // // close private files
		{
		    this.dataSaverHybridsGeneral.writeln(RepastEssentials.GetTickCount() + ";0;0;0;0;0;0;0;0;0;0;0;0");
		    this.dataSaverHybridsGeneral.closeFile();
		    this.dataSaverHybridsCages.closeFile();
		    this.dataSaverHybridsDiploidNumber.closeFile();
		}
		else { // save private files
			writeDataToFileGeneral();
			writeDataToFileDiploidNumber();
			if (C_Parameters.PROTOCOL.equals("CAGES")) writeDataToFileCages();
		}
		// reset the indicators accounting for problem at zygote formation
		this.pbUnbalancedGene = 0;
		this.pbSynteny = 0;
		this.pbGeneNotFound = 0;
		this.pbEpistasie = 0;
		this.pbMatching = 0;
		this.pbHaldane = 0;
		
	}

	@Override
	/** stores the current state of indicators in the field including the super ones 
	 * has to be conform with indicatorsHeader / JLF 01.2013*/
	public String indicatorsStoreValues() {
	    this.indicatorsValues = RepastEssentials.GetTickCount() + CSV_FIELD_SEPARATOR + this.hybridList.size();
		return (this.indicatorsValues);
	}
	@Override
	public void indicatorsCompute() {
		TreeSet<C_Rodent> listRodents = C_InspectorPopulation.rodentList;
		this.nbEry = 0;
		this.nbNat = 0;
		this.nbLazarus = 0;
		this.nbHybrids = 0;
		this.hybridList.clear();
		Iterator<C_Rodent> rodents = listRodents.iterator();

		while (rodents.hasNext()) {
			C_Rodent rodent = rodents.next();
			if (rodent.getGenome() instanceof C_GenomeMastoErythroleucus) this.nbEry++;
			else if (rodent.getGenome() instanceof C_GenomeMastoNatalensis) this.nbNat++;
			else if (rodent.getGenome().isHybrid()) {
			    this.nbHybrids++;
				this.hybridList.add(rodent);
			}
			else if (rodent.getGenome() instanceof C_GenomeMastomys) if (!rodent.getGenome().isHybrid()) this.nbLazarus++;
			else System.err.println("C_InspectorHybrid.computeRodentIndicators(): neither an ery or nat or masto: " + rodent);
		}
		this.hybridsRate = (double) this.nbHybrids / (double) listRodents.size();
	}

	/** Writes data in the hybrids indicators csv output file */
	private void writeDataToFileGeneral() {
	    this.dataSaverHybridsGeneral.writeln(RepastEssentials.GetTickCount() + CSV_FIELD_SEPARATOR + C_InspectorPopulation.rodentList.size()
				+ CSV_FIELD_SEPARATOR + this.nbEry + CSV_FIELD_SEPARATOR + this.nbNat + CSV_FIELD_SEPARATOR + this.nbLazarus + CSV_FIELD_SEPARATOR + this.nbHybrids
				+ CSV_FIELD_SEPARATOR + this.hybridsRate + CSV_FIELD_SEPARATOR + this.pbUnbalancedGene + CSV_FIELD_SEPARATOR + this.pbSynteny + CSV_FIELD_SEPARATOR
				+ this.pbGeneNotFound + CSV_FIELD_SEPARATOR + this.pbEpistasie + CSV_FIELD_SEPARATOR + this.pbMatching + CSV_FIELD_SEPARATOR + this.pbHaldane);
	}
	/** Writes data in the DiploidNumber csv output file */
	private void writeDataToFileDiploidNumber() {
		int nb8 = 0, nb9 = 0, nb10 = 0, nb11 = 0, nb12 = 0, nb13 = 0, nb14 = 0;
		for (C_Rodent rodent : C_InspectorPopulation.rodentList) {
			switch (rodent.getGenome().getDiploidNumber()) {
				case 8 :
					nb8++;
					break;
				case 9 :
					nb9++;
					break;
				case 10 :
					nb10++;
					break;
				case 11 :
					nb11++;
					break;
				case 12 :
					nb12++;
					break;
				case 13 :
					nb13++;
					break;
				case 14 :
					nb14++;
					break;
				default :
					break;
			}
		}
		this.dataSaverHybridsDiploidNumber.writeln(RepastEssentials.GetTickCount() + CSV_FIELD_SEPARATOR + nb8 + CSV_FIELD_SEPARATOR + nb9
				+ CSV_FIELD_SEPARATOR + nb10 + CSV_FIELD_SEPARATOR + nb11 + CSV_FIELD_SEPARATOR + nb12 + CSV_FIELD_SEPARATOR + nb13
				+ CSV_FIELD_SEPARATOR + nb14);
	}

	/** Writes data in the cage report csv output file */
	private void writeDataToFileCages() {
		C_ProtocolCage cageProtocol = (C_ProtocolCage) C_ContextCreator.protocol;
		int nbBirth = 0;
		// test if there are any birth before writing the matrix
		nbBirth = 0;
		for (int i = 0; i < cageProtocol.getNB_CAGES_LINES(); i++) {
			for (int j = 0; j < cageProtocol.getNB_CAGES_COLUMNS(); j++) {
				for (I_SituatedThing rodent : cageProtocol.getCagesMatrix()[i][j].getRodentList()) {
					C_RodentCaged encagedRodent = (C_RodentCaged) rodent;
					if (encagedRodent.getAge_Uday() == 0) nbBirth++;
				}
			}
		}
		if (nbBirth != 0) {
			// writes the matrix in the file
			for (int i = 0; i < cageProtocol.getNB_CAGES_LINES(); i++) {
			    this.dataSaverHybridsCages.write(RepastEssentials.GetTickCount() + CSV_FIELD_SEPARATOR + i + CSV_FIELD_SEPARATOR);
				for (int j = 0; j < cageProtocol.getNB_CAGES_COLUMNS(); j++) {
					nbBirth = 0;
					for (I_SituatedThing rodent : cageProtocol.getCagesMatrix()[i][j].getRodentList()) {
						C_RodentCaged encagedRodent = (C_RodentCaged) rodent;
						if (encagedRodent.getAge_Uday() == 0.) nbBirth++;
					}
					this.dataSaverHybridsCages.write(nbBirth + CSV_FIELD_SEPARATOR);
				}
				this.dataSaverHybridsCages.writeln("");
			}
			this.dataSaverHybridsCages.writeln("");
			this.dataSaverHybridsCages.writeln(" ");
		}
	}
	// close private files
	public void closeSimulation() {
	    this.dataSaverHybridsGeneral.closeFile();
	    this.dataSaverHybridsCages.closeFile();
	    this.dataSaverHybridsDiploidNumber.closeFile();
	}
	//
	// SETTERS & GETTERS
	//
	public void incrPbNippedEgg() {
	    this.pbNippedEgg++;
	}
	public void incrPbUnbalancedGene() {
	    this.pbUnbalancedGene++;
	}
	public void incrPbSynteny() {
	    this.pbSynteny++;
	}
	public void incrPbGeneNotFound() {
	    this.pbGeneNotFound++;
	}
	public void incrPbEpistasie() {
	    this.pbEpistasie++;
	}
	public void incrPbMatching() {
	    this.pbMatching++;
	}
	public void incrPbHaldane() {
	    this.pbHaldane++;
	}
	public TreeSet<C_Rodent> getHybridList() {
		return this.hybridList;
	}
	public Integer getNbHybrids() {
		return this.nbHybrids;
	}
	public Double getHybridsRate() {
		return this.hybridsRate;
	}
	public Integer getNbEry() {
		return this.nbEry;
	}
	public Integer getNbNat() {
		return this.nbNat;
	}
	public Integer getNbLazarus() {
		return this.nbLazarus;
	}
	public int getPbUnbalancedGene() {
		return this.pbUnbalancedGene;
	}
	public int getPbAutostopNotFound() {
		return this.pbSynteny;
	}
	public int getPbGeneNotFound() {
		return this.pbGeneNotFound;
	}
	public int getPbNippedEgg() {
		return this.pbNippedEgg;
	}
	/** Used for @see C_UserPanel, JLF 05.2018 */
	public void resetPbNippedEggs() {
	    this.pbNippedEgg = 0;
	}
}