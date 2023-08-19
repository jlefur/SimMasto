/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing.dna;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/** The amniotes are a group of tetrapods that have an adaptation to lay eggs
 * @author J.Le Fur 2012 (source code origin: Kyle Wagner), rev. JLF 08.2014 */
public class C_GenomeAmniota extends C_GenomeAnimalia implements data.constants.I_ConstantString {
	// Maplocs and locus of the chromosome Pair:
	protected static final ArrayList<Double> SEXUAL_MATURITY_MAPLOCS = new ArrayList<Double>(Arrays.asList(11.));
	protected static final ArrayList<Double> LITTER_SIZE_MAPLOCS = new ArrayList<Double>(Arrays.asList(12.));
	protected static final ArrayList<Double> WEANING_AGE_MAPLOCS = new ArrayList<Double>(Arrays.asList(13.));
	protected static final ArrayList<Double> MATING_LATENCY_MAPLOCS = new ArrayList<Double>(Arrays.asList(14.));
	protected static final ArrayList<Double> GESTATION_LENGTH_MAPLOCS = new ArrayList<Double>(Arrays.asList(15.));
	protected static final int SEXUAL_MATURITY_LOCUS = 0;
	protected static final int LITTER_SIZE_LOCUS = 1;
	protected static final int WEANING_AGE_LOCUS = 2;
	protected static final int MATING_LATENCY_LOCUS = 3;
	protected static final int GESTATION_LENGTH_LOCUS = 4;
	protected static final int numGenesXsomeAmniota = 5;
	protected C_ChromosomePair xsomePairAmniota;
	//
	// CONSTRUCTORS
	//
	/** Return a new GenomeAmniota, adds an autosome bearing the reproductive traits of the (daughters) species.<br>
	 * In the case of an agent with C_GenomeAmniota (used when life traits are not important, e.g., human carrier), the agent is provided with
	 * junk(viz. standard) values<br>
	*/
	public C_GenomeAmniota() {
		super();
		this.alleles.put(SEXUAL_MATURITY_Uday, DEFAULT_SEXUAL_MATURITY_Uday); 
		this.alleles.put(LITTER_SIZE, DEFAULT_LITTER_SIZE); 
		this.alleles.put(WEANING_AGE_Uday, DEFAULT_WEANING_AGE_Uday);
		this.alleles.put(MATING_LATENCY_Uday, DEFAULT_MATING_LATENCY_Uday);
		this.alleles.put(GESTATION_LENGTH_Uday, DEFAULT_GESTATION_LENGTH_Uday);
		makeAmniotaBivalent(this.alleles);// values inside the allele map are each time replaced by the daughter class and its
											// intermediates JLF 08.2014, rev. MS 10.2016
	}
	/** Return a new GenomeAmniota. This method is used for genome mate. <br>
	 * args: the three different types of chromosomes : microsatXsome, gonosome, autosomes Adds a bivalent bearing the reproductive traits of the
	 * (daughters) species
	 * @see C_GenomeEucaryote#mateGenomes(Object, I_diploid_genome) */
	public C_GenomeAmniota(C_XsomePairMicrosat microsatXsome, C_XsomePairSexual gonosome, ArrayList<C_ChromosomePair> autosomes) {
		super(microsatXsome, gonosome, autosomes);
		this.alleles.put(SEXUAL_MATURITY_Uday, (double) this.getSexualMaturity_Uday()); 
		this.alleles.put(LITTER_SIZE, (double) this.getLitterSizeValue()); 
		this.alleles.put(WEANING_AGE_Uday, (double) this.getWeaningAge_Uday());
		this.alleles.put(MATING_LATENCY_Uday, (double) this.getMatingLatency_Uday());
		this.alleles.put(GESTATION_LENGTH_Uday, (double) this.getGestationLength_Uday());	
	}
	//
	// METHODS
	//
	/** @param allelesMap the specific allele value from the appropriate daughter genome /author J. Le Fur 08.2014 */
	protected void makeAmniotaBivalent(Map<String, Double> allelesMap) {
		// remove obsolete xsomePairAmniota constructed by mother classes
		this.bivalents.remove(this.xsomePairAmniota);
		this.xsomePairAmniota = new C_ChromosomePair(C_Chromosome.MAP_LENGTH, numGenesXsomeAmniota, recombinator);

		this.xsomePairAmniota.setGenePairAtLocus(SEXUAL_MATURITY_LOCUS, //
				new C_Gene(allelesMap.get(SEXUAL_MATURITY_Uday) + getTaxonSignature(), SEXUAL_MATURITY_MAPLOCS.get(0), mutator,
						this.xsomePairAmniota.xsomeStrands[0].getMyId()), //
				new C_Gene(allelesMap.get(SEXUAL_MATURITY_Uday) + getTaxonSignature(), SEXUAL_MATURITY_MAPLOCS.get(0), mutator,
						this.xsomePairAmniota.xsomeStrands[1].getMyId()));

		this.xsomePairAmniota.setGenePairAtLocus(LITTER_SIZE_LOCUS, //
				new C_Gene(allelesMap.get(LITTER_SIZE) + getTaxonSignature(), LITTER_SIZE_MAPLOCS.get(0), mutator,
						this.xsomePairAmniota.xsomeStrands[0].getMyId()), //
				new C_Gene(allelesMap.get(LITTER_SIZE) + getTaxonSignature(), LITTER_SIZE_MAPLOCS.get(0), mutator,
						this.xsomePairAmniota.xsomeStrands[1].getMyId()));

		this.xsomePairAmniota.setGenePairAtLocus(WEANING_AGE_LOCUS, //
				new C_Gene(allelesMap.get(WEANING_AGE_Uday) + getTaxonSignature(), WEANING_AGE_MAPLOCS.get(0), mutator,
						this.xsomePairAmniota.xsomeStrands[0].getMyId()), //
				new C_Gene(allelesMap.get(WEANING_AGE_Uday) + getTaxonSignature(), WEANING_AGE_MAPLOCS.get(0), mutator,
						this.xsomePairAmniota.xsomeStrands[1].getMyId()));

		this.xsomePairAmniota.setGenePairAtLocus(MATING_LATENCY_LOCUS, //
				new C_Gene(allelesMap.get(MATING_LATENCY_Uday) + getTaxonSignature(), MATING_LATENCY_MAPLOCS.get(0), mutator,
						this.xsomePairAmniota.xsomeStrands[0].getMyId()), //
				new C_Gene(allelesMap.get(MATING_LATENCY_Uday) + getTaxonSignature(), MATING_LATENCY_MAPLOCS.get(0), mutator,
						this.xsomePairAmniota.xsomeStrands[1].getMyId()));

		this.xsomePairAmniota.setGenePairAtLocus(GESTATION_LENGTH_LOCUS, //
				new C_Gene(allelesMap.get(GESTATION_LENGTH_Uday) + getTaxonSignature(), GESTATION_LENGTH_MAPLOCS.get(0), mutator,
						this.xsomePairAmniota.xsomeStrands[0].getMyId()), //
				new C_Gene(allelesMap.get(GESTATION_LENGTH_Uday) + getTaxonSignature(), GESTATION_LENGTH_MAPLOCS.get(0), mutator,
						this.xsomePairAmniota.xsomeStrands[1].getMyId()));

		this.bivalents.add(bivalents.size(), this.xsomePairAmniota);
	}
	//
	// GETTERS
	//
	// Public direct access to the genes value <br>
	// Reminder: the expressor is an expressor of type average // SUPPOSES ALL MAPLOCS ARE ON THE SAME CHROMOSOME.
	// To account for maplocs on several xsomes: for each map loc find the chromosome, apply the expressor each time, process the
	// list of results (one value per chromosome).<br>
	// author Kyle Wagner 2002, rev.JLF 05.2012, 07.2014, 08.2014 */
	//
	public int getLitterSizeValue() {
		return (int) ((double) expressor.evalTrait(getChromosomePairFromMaploc(LITTER_SIZE_MAPLOCS.get(0)), LITTER_SIZE_MAPLOCS)
				- getTaxonSignature());
	}
	public int getSexualMaturity_Uday() {
		return (int) ((double) expressor.evalTrait(getChromosomePairFromMaploc(SEXUAL_MATURITY_MAPLOCS.get(0)), SEXUAL_MATURITY_MAPLOCS)
				- getTaxonSignature());
	}
	public int getWeaningAge_Uday() {
		return (int) ((double) expressor.evalTrait(getChromosomePairFromMaploc(WEANING_AGE_MAPLOCS.get(0)), WEANING_AGE_MAPLOCS)
				- getTaxonSignature());
	}
	public int getMatingLatency_Uday() {
		return (int) ((double) expressor.evalTrait(getChromosomePairFromMaploc(MATING_LATENCY_MAPLOCS.get(0)), MATING_LATENCY_MAPLOCS)
				- getTaxonSignature());
	}
	public int getGestationLength_Uday() {
		return (int) ((double) expressor.evalTrait(getChromosomePairFromMaploc(GESTATION_LENGTH_MAPLOCS.get(0)),
				GESTATION_LENGTH_MAPLOCS) - getTaxonSignature());
	}
}
