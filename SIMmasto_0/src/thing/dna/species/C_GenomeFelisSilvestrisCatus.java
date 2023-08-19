/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing.dna.species;

import java.util.ArrayList;

import thing.dna.C_ChromosomePair;
import thing.dna.C_GenomeAmniota;
import thing.dna.C_GenomeEucaryote;
import thing.dna.C_XsomePairMicrosat;
import thing.dna.C_XsomePairSexual;

/** @author MS 09.2016 */
public class C_GenomeFelisSilvestrisCatus extends C_GenomeAmniota {
	//
	// CONSTRUCTORS
	//
	/** Returns a new Tyto Alba genome. <br>
	 * Trait values references (see comments in source code):<br>
	 */
	public C_GenomeFelisSilvestrisCatus() {
		super();
//		this.alleles.put(SENSING_UmeterByDay, 0.); // source: Taberlet 1983
		makeAnimaliaBivalent(this.alleles);
		this.alleles.put(LITTER_SIZE, 5.); // source:between 4 and 8 (https://en.wikipedia.org/wiki/Cat#cite_note-Jemmett1977-143), number of eggs
		this.alleles.put(WEANING_AGE_Uday, 42.);// source : 42 days  (Ireland, T., & Neilan, R. M. (2016). A spatial agent-based model of feral cats and analysis of population and nuisance controls. Ecological Modelling, 337, 123-136.), period when chick are feeding by parents
		this.alleles.put(MATING_LATENCY_Uday, 56.); // source:between 8 and 10 week (http://www.larousse.fr/encyclopedie/vie-sauvage/effraie_des_clochers/184042)
		this.alleles.put(GESTATION_LENGTH_Uday, 65.); // source:between 64 and 67 day of oviposition (https://en.wikipedia.org/wiki/Cat#cite_note-Jemmett1977-143)
		this.alleles.put(SEXUAL_MATURITY_Uday, 225.);// source :between 5 and 10 months (https://en.wikipedia.org/wiki/Cat#Reproduction)
//		this.alleles.put(SPEED_UmeterByDay,0.); // TODO JLF 2018.01
		this.alleles.put(MAX_AGE_Uday,3467.5);  // source: Life expectancy between 1 and 18 years in the wild (https://doi.org/10.1016/j.gecco.2020.e01198)
		makeAnimaliaBivalent(this.alleles);
		makeAmniotaBivalent(this.alleles);
	}

	/** Returns a new Tyto Alba genome. This method is used for genome mate.
	 * @param microsatXsome
	 * @param gonosome
	 * @param autosomes
	 * @see C_GenomeEucaryote#mate */
	public C_GenomeFelisSilvestrisCatus(C_XsomePairMicrosat microsatXsome, C_XsomePairSexual gonosome,
			ArrayList<C_ChromosomePair> autosomes) {
		super(microsatXsome, gonosome, autosomes);
	}
	//
	// GETTER
	//
	@Override
	protected Double getTaxonSignature() {
		return .71;
	}
}
