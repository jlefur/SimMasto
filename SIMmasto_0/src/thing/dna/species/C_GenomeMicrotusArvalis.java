/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing.dna.species;

import java.util.ArrayList;

import thing.dna.C_ChromosomePair;
import thing.dna.C_GenomeAmniota;
import thing.dna.C_GenomeEucaryote;
import thing.dna.C_XsomePairMicrosat;
import thing.dna.C_XsomePairSexual;

/** @author Le Fur J. 07.2012, rev. JLF 08.2014 */
public class C_GenomeMicrotusArvalis extends C_GenomeAmniota {
	//
	// CONSTRUCTORS
	//
	/** Returns a new GenomeMicrotusArvalis. This method is used for mate. <br>
	 * References (see comments in source code):<br>
	 * - AnAge database (2014) http://genomics.senescence.info/species/entry.php?species=Microtus_arvalis<br>
	 * - Le Louarn, H. and J.P. Quéré (2003) Les rongeurs de France: faunistique et biologie. INRA ed., Paris, ISBN: 2-7380-1091-1,
	 * 256p.<br>
	 * - Quéré, J.P. and H. Le Louarn (2011) Les rongeurs de France: faunistique et biologie. Quae ed., Paris, ISBN:
	 * 978-2-7592-1033-6, 312p. */
	public C_GenomeMicrotusArvalis() {
		super();
		if( this.getGonosome().isFemale())
			alleles.put(SEXUAL_MATURITY_Uday, 39.); 
		else
			alleles.put(SEXUAL_MATURITY_Uday, 45.); 
		alleles.put(LITTER_SIZE, 4.); // source: 3 to 9 [Quéré and Le Louarn, 2011]
		alleles.put(WEANING_AGE_Uday, 18.);// source: Quere comm. Pers. / 20: AnAge database (2014)
		alleles.put(MATING_LATENCY_Uday, 13.); // source: Le Louarn and Quéré, 2003 / (20-21) -1 ! AnAge database (2014)
		alleles.put(GESTATION_LENGTH_Uday, 21.); // source: Quéré and Le Louarn, 2011 / AnAge database (2014)
		makeAmniotaBivalent(this.alleles);
	}
	/** Returns a new GenomeMicrotusArvalis. This method is used for genome mate. <br>
	 * Args: the three different types of chromosomes : microsatXsome, gonosome, autosomes
	 * @see C_GenomeEucaryote#mateGenomes(Object, I_diploid_genome) */
	public C_GenomeMicrotusArvalis(C_XsomePairMicrosat microsatXsome, C_XsomePairSexual gonosome,
			ArrayList<C_ChromosomePair> autosomes) {
		super(microsatXsome, gonosome, autosomes);
	}
	//
	// GETTER
	//
	@Override
	/** this arbitrary taxon signature is specific of the Microtus arvalis species */
	protected Double getTaxonSignature() {
		return .30;
	}
}
