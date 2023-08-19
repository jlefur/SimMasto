/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing.dna;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
/** Animals are mobile and speed is their life trait
 * @author J.Le Fur 2017 */
public class C_GenomeAnimalia extends C_GenomeEucaryote implements data.constants.I_ConstantString {
	protected static final ArrayList<Double> SPEED_MAPLOCS = new ArrayList<Double>(Arrays.asList(17.));
	protected static final ArrayList<Double> SENSING_MAPLOCS = new ArrayList<Double>(Arrays.asList(18.));
	protected static final ArrayList<Double> MAX_AGE_MAPLOCS = new ArrayList<Double>(Arrays.asList(19.));
	protected static final int SPEED_LOCUS = 0;
	protected static final int SENSING_LOCUS = 1;
	protected static final int MAX_AGE_LOCUS = 2;
	protected static final int numGenesXsomeAnimalia = 3;
	protected C_ChromosomePair xsomePairAnimalia;
	//
	// CONSTRUCTORS
	//
	/** Return a new GenomeAnimalia, adds an autosome bearing the speed traits of the (daughters) species. TODO JLF 2017.08 change to
	 * this.alleles.put(SPEED_UmeterByDay, randomizeTrait(DEFAULT_SPEED_UmeterByDay); */
	public C_GenomeAnimalia() {
		super();
		this.alleles.put(SPEED_UmeterByDay, DEFAULT_SPEED_UmeterByDay);
		this.alleles.put(SENSING_UmeterByDay, DEFAULT_SENSING_UmeterByDay);
		this.alleles.put(MAX_AGE_Uday, DEFAULT_MAX_AGE_Uday);
		makeAnimaliaBivalent(this.alleles);// values inside the allele map are each time replaced by the daughter class and its
											// intermediates JLF 08.2014
	}
	/** Return a new GenomeAnimalia. This method is used for genome mate. <br>
	 * args: the three different types of chromosomes : microsatXsome, gonosome, autosomes Adds a bivalent bearing the reproductive traits of the
	 * (daughters) species
	 * @see C_GenomeEucaryote#mateGenomes(Object, I_diploid_genome) */
	public C_GenomeAnimalia(C_XsomePairMicrosat microsatXsome, C_XsomePairSexual gonosome, ArrayList<C_ChromosomePair> autosomes) {
		super(microsatXsome, gonosome, autosomes);
		this.alleles.put(SPEED_UmeterByDay, this.getSpeed_UmeterByDay());
		this.alleles.put(SENSING_UmeterByDay, this.getSensing_UmeterByDay());
		this.alleles.put(MAX_AGE_Uday, this.getMaxAge_Uday());
	}
	//
	// METHODS
	//
	/** @param allelesMap the specific allele value from the appropriate daughter genome /author J. Le Fur 08.2014 */
	protected void makeAnimaliaBivalent(Map<String, Double> allelesMap) {
		// Remove obsolete xsomePairAnimalia constructed by mother classes
		this.bivalents.remove(this.xsomePairAnimalia);
		this.xsomePairAnimalia = new C_ChromosomePair(C_Chromosome.MAP_LENGTH, numGenesXsomeAnimalia, recombinator);
		this.xsomePairAnimalia.setGenePairAtLocus(SPEED_LOCUS, //
				new C_Gene(allelesMap.get(SPEED_UmeterByDay) + getTaxonSignature(), SPEED_MAPLOCS.get(0), mutator,
						this.xsomePairAnimalia.xsomeStrands[0].myId), //
				new C_Gene(allelesMap.get(SPEED_UmeterByDay) + getTaxonSignature(), SPEED_MAPLOCS.get(0), mutator,
						this.xsomePairAnimalia.xsomeStrands[1].myId));
		this.xsomePairAnimalia.setGenePairAtLocus(SENSING_LOCUS, //
				new C_Gene(allelesMap.get(SENSING_UmeterByDay) + getTaxonSignature(), SENSING_MAPLOCS.get(0), mutator,
						this.xsomePairAnimalia.xsomeStrands[0].myId), //
				new C_Gene(allelesMap.get(SENSING_UmeterByDay) + getTaxonSignature(), SENSING_MAPLOCS.get(0), mutator,
						this.xsomePairAnimalia.xsomeStrands[1].myId));
		this.xsomePairAnimalia.setGenePairAtLocus(MAX_AGE_LOCUS, //
				new C_Gene(allelesMap.get(MAX_AGE_Uday) + getTaxonSignature(), MAX_AGE_MAPLOCS.get(0), mutator,
						this.xsomePairAnimalia.xsomeStrands[0].myId), //
				new C_Gene(allelesMap.get(MAX_AGE_Uday) + getTaxonSignature(), MAX_AGE_MAPLOCS.get(0), mutator,
						this.xsomePairAnimalia.xsomeStrands[1].myId));
		this.bivalents.add(bivalents.size(), this.xsomePairAnimalia);
	}
	//
	// GETTERS
	//
	// Public direct access to the genes value <br>
	// Reminder: the expressor is an expressor of type average // SUPPOSES ALL MAPLOCS ARE ON THE SAME CHROMOSOME.
	// To account for maplocs on several xsomes: for each map loc find the chromosome, apply the expressor each time, process the
	// list of results (one value per chromosome).<br>
	// author Kyle Wagner 2002, rev.JLF 05.2012, 07.2014, 08.2014, 03.2019 */
	//
	public double getSpeed_UmeterByDay() {
		return ((double) expressor.evalTrait(getChromosomePairFromMaploc(SPEED_MAPLOCS.get(0)), SPEED_MAPLOCS) - getTaxonSignature());
	}
	public double getSensing_UmeterByDay() {
		return ((double) expressor.evalTrait(getChromosomePairFromMaploc(SENSING_MAPLOCS.get(0)), SENSING_MAPLOCS) - getTaxonSignature());
	}
	public double getSensingSurface_USquareMeterByDay() {
		return this.getSensing_UmeterByDay() * this.getSensing_UmeterByDay() * Math.PI;
	}
	@Deprecated
	public double getReachableSurface_USquareMeterByDay() {
		return this.getSpeed_UmeterByDay() * this.getSpeed_UmeterByDay() * Math.PI;
	}
	/** used only for the computation of the mortality table
	 * @see C_Rodent#getDeathProbabilityMicrotusArvalis_Uday */
	public double getMaxAge_Uday() {
		return ((double) expressor.evalTrait(getChromosomePairFromMaploc(MAX_AGE_MAPLOCS.get(0)), MAX_AGE_MAPLOCS) - getTaxonSignature());
	}
}
