/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing.dna.species;

import java.util.ArrayList;

import thing.dna.C_ChromosomePair;
import thing.dna.C_GenomeAmniota;
import thing.dna.C_GenomeEucaryote;
import thing.dna.C_XsomePairMicrosat;
import thing.dna.C_XsomePairSexual;

/** @author JLF 07.2014, rev. JLF & L.Granjon 08.2014, M.Sall 10.2016 */
public class C_GenomeGerbillusNigeriae extends C_GenomeAmniota {
	//
	// CONSTRUCTORS
	//
	/** Returns a new Gerbillus nigeriae genome. <br>
	 * Trait values references (see comments in source code):<br>
	 * - Sicard and Fuminier 1996// TODO JLF 2015.10 to complete - Nomao, 2001 // TODO JLF ref. Nomao 2001 - Granjon, 2014 comm.
	 * pers. */
	public C_GenomeGerbillusNigeriae() {
		super();
		if (this.getGonosome().isFemale()) alleles.put(SEXUAL_MATURITY_Uday, 60.);// Weaning is three weeks, however young females
																					// are not sexually mature between around
																					// two months Nomao, 2001 (LG comm. Pers.)
		else alleles.put(SEXUAL_MATURITY_Uday, 60.001); // source: junk value TODO JLF 2020.04; MS 2017.05 (nb ask LG)
		this.alleles.put(LITTER_SIZE, 4.); // source:between 2 and 6 (Sicard and Fuminier, 1996)
		this.alleles.put(WEANING_AGE_Uday, 26.);// source: Nomao, 2001
		this.alleles.put(MATING_LATENCY_Uday, 39.); // source: TODO JLF 2020.04; 2015.10 ask LG
		this.alleles.put(GESTATION_LENGTH_Uday, 22.); // source: TODO JLF 2020.04; 2015.10 ask LG
		makeAmniotaBivalent(this.alleles);
	}

	/** Returns a new Gerbillus nigeriae genome. This method is used for genome mate.
	 * @param microsatXsome
	 * @param gonosome
	 * @param autosomes
	 * @see C_GenomeEucaryote#mate */
	public C_GenomeGerbillusNigeriae(C_XsomePairMicrosat microsatXsome, C_XsomePairSexual gonosome,
			ArrayList<C_ChromosomePair> autosomes) {
		super(microsatXsome, gonosome, autosomes);
	}
	//
	// GETTER
	//
	@Override
	protected Double getTaxonSignature() {
		return .20;
	}
}
