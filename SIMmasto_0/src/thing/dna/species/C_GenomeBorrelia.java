/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing.dna.species;

import java.util.ArrayList;
import java.util.Map;

import thing.dna.C_Chromosome;
import thing.dna.C_ChromosomePair;
import thing.dna.C_GenomeAnimalia;
import thing.dna.C_GenomeEucaryote;
import thing.dna.C_XsomePairMicrosat;
import thing.dna.C_XsomePairSexual;

/** @author M Sall 2020 */
public class C_GenomeBorrelia extends C_GenomeAnimalia implements data.constants.I_ConstantString {
	// Maplocs and locus of the chromosome Pair:
	protected static final int numGenesXsomeAmniota = 2;
	protected C_ChromosomePair xsomePairAmniota;
	//
	// CONSTRUCTORS
	//
	/** Return a new GenomeAmniota, adds an autosome bearing the reproductive traits of the (daughters) species.<br>
	 * In the case of an agent with C_GenomeAmniota (used when life traits are not important, e.g., human carrier), the agent is
	 * provided with junk(viz. standard) values<br>
	 */
	public C_GenomeBorrelia() {
		super();
		makeAcariaBivalent(this.alleles);
	}
	/** Return a new GenomeAmniota. This method is used for genome mate. <br>
	 * args: the three different types of chromosomes : microsatXsome, gonosome, autosomes Adds a bivalent bearing the
	 * reproductive traits of the (daughters) species
	 * @see C_GenomeEucaryote#mateGenomes(Object, I_diploid_genome) */
	public C_GenomeBorrelia(C_XsomePairMicrosat microsatXsome, C_XsomePairSexual gonosome,
			ArrayList<C_ChromosomePair> autosomes) {
		super(microsatXsome, gonosome, autosomes);
	}
	//
	// METHODS
	//
	/** @param allelesMap the specific allele value from the appropriate daughter genome /author J. Le Fur 08.2014 */
	protected void makeAcariaBivalent(Map<String, Double> allelesMap) {
		// remove obsolete xsomePairAmniota constructed by mother classes
		this.bivalents.remove(this.xsomePairAmniota);
		this.xsomePairAmniota = new C_ChromosomePair(C_Chromosome.MAP_LENGTH, numGenesXsomeAmniota, recombinator);
		this.bivalents.add(bivalents.size(), this.xsomePairAmniota);
	}
}
