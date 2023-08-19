/*This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt*/
package thing.dna.species;

import java.util.ArrayList;

import thing.dna.C_ChromosomePair;
import thing.dna.C_GenomeAmniota;
import thing.dna.C_GenomeEucaryote;
import thing.dna.C_XsomePairMicrosat;
import thing.dna.C_XsomePairSexual;

/** @author JLF and AC 02.2012, rev. JL 08.2017 */
public class C_GenomeMastomys extends C_GenomeAmniota {
	//
	// CONSTRUCTORS
	//
	/** Returns a new C_GenomeMastomys. <br>
	 * Trait values references (see comments in source code):<br>
	 * - Duplantier, J.M., Granjon, L. and Bouganaly, H. (1996) Reproductive characteristics of three sympatric species of Mastomys in Senegal, as
	 * observed in the field and in captivity. Mammalia, 60(4): 629-638. */
	public C_GenomeMastomys() {
		super();
		this.alleles.put(WEANING_AGE_Uday, 21.);// source:3 weeks for genus Mastomys [Duplantier et al. 1996]
		makeAmniotaBivalent(this.alleles);
	}
	/** Returns a new GenomeMNalatensis. This method is used for genome mate.
	 * @param microsatXsome
	 * @param gonosome
	 * @param autosomes
	 * @see C_GenomeEucaryote#mate */
	public C_GenomeMastomys(C_XsomePairMicrosat microsatXsome, C_XsomePairSexual gonosome, ArrayList<C_ChromosomePair> autosomes) {
		super(microsatXsome, gonosome, autosomes);
	}
	//
	// GETTER
	//
	@Override
	/** this taxonSignature is specific of the Mastomys group and used for individuals which are not specified. */
	protected Double getTaxonSignature() {
		return .10;
	}
}
