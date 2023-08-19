package presentation.epiphyte;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import presentation.dataOutput.C_FileWriter;
import repast.simphony.essentials.RepastEssentials;
import simmasto0.protocol.A_Protocol;
import thing.C_Rodent;
import thing.dna.C_ChromosomePair;
import thing.dna.C_GenePair;
import thing.dna.C_GenomeEucaryote;
import thing.dna.C_XsomePairMicrosat;

import com.vividsolutions.jts.geom.Coordinate;

import data.constants.I_ConstantNumeric;

/** Retrieves information on genes and alleles of the whole population. <br>
 * The rodents' list (listRodents) is obtained from the population inspector and updated when needed. This implementation to avoid permanent calls to
 * C_InspectorPopulation.listRodents static field
 * @see C_InspectorPopulation#rodentList
 * @author A Realini 05.2011 rev. J. Le Fur 03.2013 */
public class C_InspectorGenetic extends A_Inspector implements I_ConstantNumeric {
	//
	// FIELDS
	//
	protected int genePopSaveStep = 0;// steps interval for writing genePop files
	protected C_FileWriter genesFile;
	protected C_FileWriter genePopFile;
	protected double observedHeterozygosityHO = 0, expectedHeterozygosityHE = 0, meanAllelicRichness = 0;
	/** List of list of allelic frequencies for each locus */
	protected ArrayList<Double>[] allelicFrequencies = new ArrayList[NB_MICROSAT_GENES];
	protected double[] expectedHeterozygosityByLocus = new double[NB_MICROSAT_GENES];
	protected int[] richnessByLocus = new int[NB_MICROSAT_GENES];
	//
	// CONSTRUCTOR
	//
	public C_InspectorGenetic() {
		super();
		this.indicatorsHeader = "Tick;ObservedHetero;ExpectedHetero;FIS;MeanAllelicRichness";
		this.genePopFile = new C_FileWriter("GenePop.csv", true);
		initTabGenePopFile();
		/*
		 * genesFile = new C_FileWriter("Genes.csv", true); initTabGenesFile();
		 */
	}
	//
	// METHODS
	//
	/*
	 * @Override public void step_Utick() { super.step_Utick(); // compute and store indicators values recordGenesInFile();// save private files //
	 * write genePop file only at an interval defined in I_numeric_constants if (RepastEssentials.GetTickCount() == INTERVAL_ECRITURE_GENE_POP *
	 * genePopSaveStep) { recordGenePopInFile(); genePopSaveStep++; } }
	 */

	/** Initialize the list of different alleles, for the locus passed in parameter, with the first rodent of the listRodents list (list in
	 * C_InspectorPopulation).
	 * @param locus : locus number for which allelic richness is requested */
	private ArrayList<Object> initDifferingAllelesList(int locus) {
		ArrayList<Object> listDifferingAllelesByLocus = new ArrayList<Object>();
		C_XsomePairMicrosat microsat = ((C_GenomeEucaryote) C_InspectorPopulation.rodentList.first().getGenome()).getMicrosatXsome();
		C_GenePair genesPair = (C_GenePair) microsat.getLocusAllele(locus);
		listDifferingAllelesByLocus.add(genesPair.getGene(C_ChromosomePair.PARENT_1).getAllele());
		if (isHeterozygous(genesPair)) listDifferingAllelesByLocus.add(genesPair.getGene(C_ChromosomePair.PARENT_2).getAllele());
		return listDifferingAllelesByLocus;
	}

	/** Compute allelic richness for each locus (nb differing alleles for this locus).<br>
	 * Results are put in the table RichnessByLocus */
	private void computeAllelicRichnessEveryLocus() {
		for (int locus = 0; locus < NB_MICROSAT_GENES; locus++) {
			ArrayList<Object> listDifferingAllelesByLocus = initDifferingAllelesList(locus);
			Iterator<C_Rodent> rodents = C_InspectorPopulation.rodentList.iterator();
			while (rodents.hasNext()) {
				C_XsomePairMicrosat microsat = ((C_GenomeEucaryote) rodents.next().getGenome()).getMicrosatXsome();
				C_GenePair genePair = (C_GenePair) microsat.getLocusAllele(locus);
				Object allele = genePair.getGene(C_ChromosomePair.PARENT_1).getAllele();
				if (!listDifferingAllelesByLocus.contains(allele)) listDifferingAllelesByLocus.add(allele);
				if (isHeterozygous(genePair)) {
					allele = genePair.getGene(C_ChromosomePair.PARENT_2).getAllele();
					if (!listDifferingAllelesByLocus.contains(allele)) listDifferingAllelesByLocus.add(allele);
				}
			}
			richnessByLocus[locus] = listDifferingAllelesByLocus.size();
		}
	}

	/** Compute the mean allelic richness for the whole population <br>
	 * Formula: sum(allelic richness by locus)/nb genes */
	private void computeAllelicRichnessPopMean() {
		double somme = 0;
		for (int i = 0; i < this.richnessByLocus.length; i++)
			somme += this.richnessByLocus[i];
		this.meanAllelicRichness = convertNumber((somme / NB_MICROSAT_GENES), 3);
	}

	/** Compute allelic frequencies for the locus passed in parameter and writes in the allelicFrequencies list<br>
	 * Formula: nb occurences of one allele at locus i / (2 * nb indiv.) */
	private void computeAllelicRichnessOneLocus(int locus) {
		ArrayList<Double> freq = new ArrayList<Double>();
		if (!C_InspectorPopulation.rodentList.isEmpty()) {
			ArrayList<Object> listeAlleles = getAllAllelesOneLocus(locus);
			while (!listeAlleles.isEmpty()) {
				Object allele = listeAlleles.get(0);// retrieve the first allele of the list to compare with the others
				listeAlleles.remove(0); // remove the reference allele
				// Retrieves the index where the first occurence of the allele is found. If not found, index=-1
				int cpt = 1;
				int index = listeAlleles.indexOf(allele);
				while (index != -1) {
					listeAlleles.remove(index);
					cpt++;
					index = listeAlleles.indexOf(allele);
				}
				freq.add(convertNumber((double) cpt / (2 * C_InspectorPopulation.rodentList.size()), 5));
			}
		}
		this.allelicFrequencies[locus] = freq;
	}

	/** Return the list of every alleles existing for the locus passed as a parameter (used to compute the allelic frequency */
	private ArrayList<Object> getAllAllelesOneLocus(int locus) {
		ArrayList<Object> liste = new ArrayList<Object>();
		// add the chromosomes left and right to the list and not the pair per se (for easier computation)
		Iterator<C_Rodent> rodents = C_InspectorPopulation.rodentList.iterator();
		while (rodents.hasNext()) {
			C_Rodent rodent = rodents.next();
			C_XsomePairMicrosat microsat = ((C_GenomeEucaryote) rodent.getGenome()).getMicrosatXsome();
			liste.add(((C_GenePair) microsat.getLocusAllele(locus)).getGene(C_ChromosomePair.PARENT_1).getAllele());
			liste.add(((C_GenePair) microsat.getLocusAllele(locus)).getGene(C_ChromosomePair.PARENT_2).getAllele());
		}
		return liste;
	}

	/** Compute global observed heterozygosity <br>
	 * Formula : sum(nb heterozygotes at locus i/nb indiv) / nb locus */
	private void computeHeterozygosityObservedHO() {
		// for each locus, count the number of heterozygotes then divide by the number of individuals
		double sum = 0;
		for (int locus = 0; locus < NB_MICROSAT_GENES; locus++) {
			int cpt = 0;
			Iterator<C_Rodent> rodents = C_InspectorPopulation.rodentList.iterator();
			while (rodents.hasNext()) {
				C_Rodent rodent = rodents.next();
				C_XsomePairMicrosat microsat = ((C_GenomeEucaryote) rodent.getGenome()).getMicrosatXsome();
				if (isHeterozygous((C_GenePair) microsat.getLocusAllele(locus))) cpt++;
			}
			sum += (double) cpt / (double) C_InspectorPopulation.rodentList.size();
		}
		// divide the sum by the number of loci //
		this.observedHeterozygosityHO = convertNumber((sum / NB_MICROSAT_GENES), 5);
	}

	/** Compute expected heterozygosity for the locus passed as parameter <br>
	 * Formula : 1 - sum((allelic frequency at locus i)^2) */
	private void computeHeterozygosityExpectedHE(int locus) {
		double sum = 0.0;
		computeAllelicRichnessOneLocus(locus);
		ArrayList<Double> freq = this.allelicFrequencies[locus];
		for (int i = 0; i < freq.size(); i++)
			sum += Math.pow(freq.get(i), 2);
		this.expectedHeterozygosityByLocus[locus] = convertNumber(1 - sum, 3);
	}

	/** Compute expected heterozygosity over the whole population <br>
	 * Formula: mean of expected heterozygosities */
	private void computeHeterozygosityExpectedGlobal() {
		double sum = 0;
		for (int locus = 0; locus < NB_MICROSAT_GENES; locus++) {
			computeHeterozygosityExpectedHE(locus);
			sum += this.expectedHeterozygosityByLocus[locus];
		}
		this.expectedHeterozygosityHE = convertNumber((sum / NB_MICROSAT_GENES), 5);
	}

	/** Compute and return the fixation index */
	public double getFixationIndex() {
		double fisValue = 1 - (this.observedHeterozygosityHO / this.expectedHeterozygosityHE);
		// patch: at the end of simulation, FIS drops to -1 which ruins the graph.
		if ((convertNumber(fisValue, 5) == -1.) || (C_InspectorPopulation.rodentList.size() < FIS_COMPUTATION_THRESHOLD_Urodent)) return 0.;
		else return convertNumber(fisValue, 5);
	}

	/** Return true is the gene pair passed as parameter is heterozygous, else return false */
	private boolean isHeterozygous(C_GenePair paire) {
		Object left = paire.getGene(C_ChromosomePair.PARENT_1).getAllele();
		Object right = paire.getGene(C_ChromosomePair.PARENT_2).getAllele();
		if (left.equals(right)) return false;
		else return true;
	}
	@Override
	public void indicatorsCompute() {
		super.indicatorsCompute();
		if (C_InspectorPopulation.rodentList.size() > 1) {// TODO number in source min rodents to compute indicators
			computeAllelicRichnessEveryLocus();
			computeHeterozygosityExpectedGlobal();
			computeHeterozygosityObservedHO();
			computeAllelicRichnessPopMean();
		}
	}

	@Override
	/** Store the current state of indicators in the field including the super ones
	 * @see A_Protocol#recordIndicatorsInFile
	 * @revision JLF 01.2013 */
	public String indicatorsStoreValues() {
	    this.indicatorsValues = RepastEssentials.GetTickCount() + CSV_FIELD_SEPARATOR + this.observedHeterozygosityHO + CSV_FIELD_SEPARATOR
				+ this.expectedHeterozygosityHE + CSV_FIELD_SEPARATOR + getFixationIndex() + CSV_FIELD_SEPARATOR + meanAllelicRichness;
		return this.indicatorsValues;
	}

	// OUTPUT FILES MANAGEMENT //

	/** Initialize the header of the genetic indicators file */
	private void initTabGenesFile() {
	    this.genesFile.write("Tick;");
		loopOnGenesForGenesFileHeader("locus", ';');
		loopOnGenesForGenesFileHeader("LocusFrequency", ';');
		loopOnGenesForGenesFileHeader("expectedHetero", ';');
		this.genesFile.writeln("ObservedHetero;ExpectedHetero;FIS;MeanAllelicRichness");
	}

	/** Write the title with an index at the end as many times as there are requested genes to output
	 * @param title : column title to rewrite NUMBER_GENES times
	 * @param separator : separator between two strings of the headers */
	private void loopOnGenesForGenesFileHeader(String title, char separator) {
		for (int i = 0; i < NB_MICROSAT_GENES; i++)
		    this.genesFile.write(title + i + separator);
	}

	/** Initialize the header for genePop output file */
	private void initTabGenePopFile() {
	    this.genePopFile.writeln(Calendar.getInstance().getTime() + " Run n°" + this.genePopFile.numRun + " | Species: ");
		for (int i = 0; i < NB_MICROSAT_GENES; i++)
		    this.genePopFile.writeln("locus" + i);
	}

	/** Output data in genePop file */
	private void recordGenePopInFile() {
		C_GenePair[] paire = new C_GenePair[NB_MICROSAT_GENES];
		this.genePopFile.writeln("Pop " + RepastEssentials.GetTickCount());
		Iterator<C_Rodent> rodents = C_InspectorPopulation.rodentList.iterator();
		while (rodents.hasNext()) {
			C_Rodent microtus = rodents.next();
			try {
				Coordinate point = microtus.getCoordinate_Umeter();
				this.genePopFile.write(point.x + " " + point.y + " , ");
			} catch (Exception e) {
				A_Protocol.event("C_InspectorGenetic.recordGenePopInFile", "Could not write " + microtus.getAge_Utick() + " / " + microtus.isDead()
						+ " (" + microtus, isError);
			}
			for (int locus = 0; locus < NB_MICROSAT_GENES; locus++) {
				C_XsomePairMicrosat microsat = ((C_GenomeEucaryote) microtus.getGenome()).getMicrosatXsome();
				paire[locus] = (C_GenePair) microsat.getLocusAllele(locus);
			}
			this.genePopFile.writeln(paire[0] + " " + paire[1] + " " + paire[2]);
		}
	}
	/** Output data in genetic indicators file */
	private void recordGenesInFile() {
	    this.genesFile.write(RepastEssentials.GetTickCount() + CSV_FIELD_SEPARATOR);
		for (int i = 0; i < NB_MICROSAT_GENES; i++)
		    this.genesFile.write(this.richnessByLocus[i] + CSV_FIELD_SEPARATOR);
		for (int i = 0; i < NB_MICROSAT_GENES; i++)
		    this.genesFile.write(this.allelicFrequencies[i] + CSV_FIELD_SEPARATOR);
		for (int i = 0; i < NB_MICROSAT_GENES; i++)
		    this.genesFile.write(this.expectedHeterozygosityByLocus[i] + CSV_FIELD_SEPARATOR);
		this.genesFile.writeln(this.observedHeterozygosityHO + CSV_FIELD_SEPARATOR + this.expectedHeterozygosityHE + CSV_FIELD_SEPARATOR + getFixationIndex()
				+ CSV_FIELD_SEPARATOR + this.meanAllelicRichness);
	}

	/** close private files */
	public void closeSimulation() {
	    this.meanAllelicRichness = 0;
	    this.expectedHeterozygosityHE = 0;
	    this.observedHeterozygosityHO = 0;
		// genesFile.writeln("0;0;0;0;0;0;0;0;0;0;0;" + RepastEssentials.GetTickCount() + ";0;0;0;" + genesFile.getNumRun());
		// genesFile.closeFile();
		this.recordGenePopInFile();
		this.genePopFile.closeFile();
	}

	/** Utility: reduce to the requested number of digit after the decimal point(above 5 the double is automatically transformed in scientific format).
	 * @param val : the double to transform
	 * @param n : the number of digits after the decimal point */
	private double convertNumber(double val, int n) {
		final int MAX = n + 2; // n digits after the decimal point + decimal point + 0 before the point
		final int facteur = (int) Math.pow(10, n); // to shift the decimal point of n digits
		// Convert only if the number of digit after the dec. point exceeds n
		if (((Double) val).toString().length() > MAX) {
			int i = (int) (val * facteur); // On supprime les chiffres après n
			return (double) i / facteur; // On replace la virgule
		}
		else return val;
	}

	/** getter<br>
	 * Warning, the field is not updated at the call, this is done in step() procedure JLF 02.2013
	 * @see #step_Utick() */
	public double getMeanAllelicRichness() {
		return this.meanAllelicRichness;
	}

}
