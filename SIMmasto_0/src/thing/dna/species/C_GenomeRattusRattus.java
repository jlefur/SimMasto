/*This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt*/
package thing.dna.species;

import java.util.ArrayList;

import thing.dna.C_ChromosomePair;
import thing.dna.C_GenomeAmniota;
import thing.dna.C_GenomeEucaryote;
import thing.dna.C_XsomePairMicrosat;
import thing.dna.C_XsomePairSexual;

/** @author JLF and MBOUP 07.2012, rev. JLF & L.Granjon 08.2014 */
public class C_GenomeRattusRattus extends C_GenomeAmniota {
	//
	// CONSTRUCTORS
	//
	/** Returns a new C_GenomeRattusRattus. <br>
	 *   Trait values references (see comments in source code):<br>
	 *    - Kingdon, J. (1974) East African Mammals. An Atlas of Evolution in Africa. 2B (Hares and Rodents). Academic Press. London. 578-581<br>
	 *    - Rosevear, D.R. (1969) Rodents of west Africa: 267-276<br>
	 *    - AnAge database (2014) http://genomics.senescence.info/species/entry.php?species=Rattus_rattus */
	public C_GenomeRattusRattus() {
		super();
		alleles.put(LITTER_SIZE, 4.); // source:~60% survival at maturity => 7->4(LG) / Données Chancira -> 6.5 embryons / 3-4 mois [Kingdon, 1974] / 3
										// mois [Rosevear 1969] / 90 & 112 [AnAge database 2014]
		alleles.put(WEANING_AGE_Uday, 26.);// source: AnAge database 2014
		alleles.put(MATING_LATENCY_Uday, 34.); // source: 5-6 litters/year [Rosevear, 1969] -> 60-26(weaning age) (LG) / 36-22 [AnAge database, 2014]
		alleles.put(GESTATION_LENGTH_Uday, 21.); // source: 21-30d [Kingdon 1974] / 3 weeks or more [Rosevear, 1969] / AnAge database (2014)
		alleles.put(SEXUAL_MATURITY_Uday,75.); // source JMD
		alleles.put(MAX_AGE_Uday,365.); // Peu vivent plus d'un an [Kingdon, 1974] -> age max: 365, age moyen: 6 mois (LG
		makeAmniotaBivalent(this.alleles);
		makeAnimaliaBivalent(this.alleles);
	}
	/** Returns a new GenomeRattusRattus. This method is used for genome mate.
	 * @param microsatXsome
	 * @param gonosome
	 * @param autosomes
	 * @see C_GenomeEucaryote#mate */
	public C_GenomeRattusRattus(C_XsomePairMicrosat microsatXsome, C_XsomePairSexual gonosome, ArrayList<C_ChromosomePair> autosomes) {
		super(microsatXsome, gonosome, autosomes);
	}
	//
	// SETTERS & GETTERS
	//
	@Override
	/** this taxonSignature is specific of the Rattus group and used for individuals which are not
	 * specified. */
	protected Double getTaxonSignature() {
		return .42;
	}
}
