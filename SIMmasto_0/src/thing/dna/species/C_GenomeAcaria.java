/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing.dna.species;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import thing.dna.C_Chromosome;
import thing.dna.C_ChromosomePair;
import thing.dna.C_Gene;
import thing.dna.C_GenomeAnimalia;
import thing.dna.C_GenomeEucaryote;
import thing.dna.C_XsomePairMicrosat;
import thing.dna.C_XsomePairSexual;

/** @author M Sall 2019 */
public class C_GenomeAcaria extends C_GenomeAnimalia implements data.constants.I_ConstantString {
	// Maplocs and locus of the chromosome Pair:
	protected static final ArrayList<Double> SEXUAL_MATURITY_MAPLOCS = new ArrayList<Double>(Arrays.asList(20.));
	protected static final ArrayList<Double> LITTER_SIZE_MAPLOCS = new ArrayList<Double>(Arrays.asList(21.));
	protected static final int SEXUAL_MATURITY_LOCUS = 0;
	protected static final int LITTER_SIZE_LOCUS = 1;
	protected static final int numGenesXsomeAmniota = 2;
	protected C_ChromosomePair xsomePairAmniota;
	//
	// CONSTRUCTORS
	//
	/** Return a new GenomeAmniota, adds an autosome bearing the reproductive traits of the (daughters) species.<br>
	 * In the case of an agent with C_GenomeAmniota (used when life traits are not important, e.g., human carrier), the agent is
	 * provided with junk(viz. standard) values<br>
	 */
	public C_GenomeAcaria() {
		super();
		this.alleles.put(SEXUAL_MATURITY_Uday, 26.5);// source : R.Tiwari_2017 TODO MS 2020.09 référence complète
		this.alleles.put(LITTER_SIZE, 2.);//TODO MS 2020.09 référence
		this.alleles.put(MAX_AGE_Uday, 1825.);// ~20 years; Source : Gondard, M. (2017). A la découverte des agents pathogènes et
												// microorganismes des tiques par séquençage de nouvelle génération et QPCR
												// microfluidique à haut débit (Doctoral dissertation, Paris Est).
		this.alleles.put(SENSING_UmeterByDay, 1.001);
		makeAcariaBivalent(this.alleles);
	}
	/** Return a new GenomeAmniota. This method is used for genome mate. <br>
	 * args: the three different types of chromosomes : microsatXsome, gonosome, autosomes Adds a bivalent bearing the
	 * reproductive traits of the (daughters) species
	 * @see C_GenomeEucaryote#mateGenomes(Object, I_diploid_genome) */
	public C_GenomeAcaria(C_XsomePairMicrosat microsatXsome, C_XsomePairSexual gonosome,
			ArrayList<C_ChromosomePair> autosomes) {
		super(microsatXsome, gonosome, autosomes);
		this.alleles.put(SEXUAL_MATURITY_Uday, (double) this.getSexualMaturity_Uday());
		this.alleles.put(LITTER_SIZE, (double) this.getLitterSizeValue());
	}
	//
	// METHODS
	//
	/** @param allelesMap the specific allele value from the appropriate daughter genome /author J. Le Fur 08.2014 */
	protected void makeAcariaBivalent(Map<String, Double> allelesMap) {
		// remove obsolete xsomePairAmniota constructed by mother classes
		this.bivalents.remove(this.xsomePairAmniota);
		this.xsomePairAmniota = new C_ChromosomePair(C_Chromosome.MAP_LENGTH, numGenesXsomeAmniota, recombinator);

		this.xsomePairAmniota.setGenePairAtLocus(SEXUAL_MATURITY_LOCUS, //
				new C_Gene(allelesMap.get(SEXUAL_MATURITY_Uday) + getTaxonSignature(), SEXUAL_MATURITY_MAPLOCS.get(0),
						mutator, this.xsomePairAmniota.xsomeStrands[0].getMyId()), //
				new C_Gene(allelesMap.get(SEXUAL_MATURITY_Uday) + getTaxonSignature(), SEXUAL_MATURITY_MAPLOCS.get(0),
						mutator, this.xsomePairAmniota.xsomeStrands[1].getMyId()));

		this.xsomePairAmniota.setGenePairAtLocus(LITTER_SIZE_LOCUS, //
				new C_Gene(allelesMap.get(LITTER_SIZE) + getTaxonSignature(), LITTER_SIZE_MAPLOCS.get(0), mutator,
						this.xsomePairAmniota.xsomeStrands[0].getMyId()), //
				new C_Gene(allelesMap.get(LITTER_SIZE) + getTaxonSignature(), LITTER_SIZE_MAPLOCS.get(0), mutator,
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
		return (int) ((double) expressor.evalTrait(getChromosomePairFromMaploc(LITTER_SIZE_MAPLOCS.get(0)),
				LITTER_SIZE_MAPLOCS) - getTaxonSignature());
	}
	public int getSexualMaturity_Uday() {
		return (int) ((double) expressor.evalTrait(getChromosomePairFromMaploc(SEXUAL_MATURITY_MAPLOCS.get(0)),
				SEXUAL_MATURITY_MAPLOCS) - getTaxonSignature());
	}
}
