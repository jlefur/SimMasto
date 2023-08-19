/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing.dna.species;

import java.util.ArrayList;

import thing.dna.C_ChromosomePair;
import thing.dna.C_GenomeAmniota;
import thing.dna.C_GenomeEucaryote;
import thing.dna.C_XsomePairMicrosat;
import thing.dna.C_XsomePairSexual;

/** @author MS 09.2016 */
public class C_GenomeTytoAlba extends C_GenomeAmniota {
	//
	// CONSTRUCTORS
	//
	/** Returns a new Tyto Alba genome. <br>
	 * Trait values references (see comments in source code):<br>
	 */
	public C_GenomeTytoAlba() {
		super();
		this.alleles.put(SENSING_UmeterByDay, 2500.); // source: Taberlet 1983
		makeAnimaliaBivalent(this.alleles);
		this.alleles.put(LITTER_SIZE, 6.); // source:between 2 and 14 (https://inpn.mnhn.fr/docs/cahab/fiches/Bruant-desroseaux.pdf), number of eggs
		this.alleles.put(WEANING_AGE_Uday, 60.);// 8 to 12 weeks source:between  (http://www.barnowltrust.org.uk/barn-owl-facts/owlets-young-barn-owls/): weaning is two months, period when chick are feeding by parents
		this.alleles.put(MATING_LATENCY_Uday, 56.); // source:between 8 and 10 week (http://www.larousse.fr/encyclopedie/vie-sauvage/effraie_des_clochers/184042)
		this.alleles.put(GESTATION_LENGTH_Uday, 36.); // source:between 30 and 32 more six day of oviposition (https://fr.wikipedia.org/wiki/Chouette_effraie)
		this.alleles.put(SEXUAL_MATURITY_Uday, 386.9);// source :between 1 and 3 years, on average 1.06 year (https://www.registrelep-sararegistry.gc.ca/virtual_sara/files/cosewic/sr_effraie_clochers_0911_fra.pdf)
		this.alleles.put(SPEED_UmeterByDay,this.alleles.get(SENSING_UmeterByDay)); // TODO JLF 2018.01
		makeAnimaliaBivalent(this.alleles);
		makeAmniotaBivalent(this.alleles);
	}

	/** Returns a new Tyto Alba genome. This method is used for genome mate.
	 * @param microsatXsome
	 * @param gonosome
	 * @param autosomes
	 * @see C_GenomeEucaryote#mate */
	public C_GenomeTytoAlba(C_XsomePairMicrosat microsatXsome, C_XsomePairSexual gonosome,
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
