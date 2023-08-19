/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing.dna.species;

import java.util.ArrayList;

import thing.dna.C_Chromosome;
import thing.dna.C_ChromosomePair;
import thing.dna.C_Gene;
import thing.dna.C_GenePair;
import thing.dna.C_GenomeEucaryote;
import thing.dna.C_XsomePairMicrosat;
import thing.dna.C_XsomePairSexual;

/** @author Le Fur J. & A.Comte 07.2012, rev. JLF & L.Granjon 08.2014 */
public class C_GenomeMastoErythroleucus extends C_GenomeMastomys {
	//
	// CONSTRUCTORS
	//
	/** Returns a new C_GenomeMastoErythroleucus. <br>
	 * Trait values references (see comments in source code):<br>
	 * - Granjon, L. (1987) Evolution allopatrique chez les Muridés : mécanismes éco-éthologiques liés au syndrome d'insularité
	 * chez Mastomys et Rattus.Montpellier : USTL, 172 p. multigr. Th. : Biol. des Populations et des Ecosystèmes, USTL :
	 * Montpellier. 1987/11/13.<br>
	 * - Granjon, L. and J.M. Duplantier (2009)Les rongeurs de l'Afrique sahélo-soudanienne. IRD Editions, Publications<br>
	 * Scientifiques du Muséum - Collection Faune et Flore tropicales 43 / / ISBN IRD : 978-2-7099-1675-2<br>
	 * - Hubert, B. ; F. Adam, (1975) Reproduction et croissance en élevage de quatre espèces de rongeurs sénégalais.<br>
	 * Mammalia, t. 39(1) : 1-15 - <br>
	 * - Duplantier, J.M., Granjon, L. and Bouganaly, H. (1996) Reproductive characteristics of three sympatric species<br>
	 * of Mastomys in Senegal, as observed in the field and in captivity. Mammalia, 60(4): 629-638.<br>
	 * - Hubert, B. 1982 Dynamique des populations de deux espèces de rongeurs du Sénégal, Mastomys erythroleucus et<br>
	 * Taterillus gracilis (Rodentia, Muridae et Gerbillidae) : l. Etude démographique. Mammalia, t. 46(2) : 37-166<br>
	 * - Poulet A.R. (1972) Recherches écologiques sur une savane sahélienne du Ferlo septentrional, Sénégal : les mammifères.<br>
	 * Terre et Vie-Rev. Ecol. A., 26 : 440-472. */
	public C_GenomeMastoErythroleucus() {
		super();
		addHybridTestAutosomes();
		alleles.put(SEXUAL_MATURITY_Uday, 84.); // source: 12 weeks [Hubert et Adam, 1975 ; Granjon, 1987]
		alleles.put(LITTER_SIZE, 8.); // source: 12 embryos*60% -> Granjon & Duplantier 2009]/7.5*70% (LG), 40d if favourable
										// rainy season, else between 40-60 given resources availability/
										// 7-13, 15 max. [Hubert and Adam, 1975] / 1 to 16, mean 7.1 (dont 66% or 80 % (?) survive
										// to weaning) – in summary: 13 [Duplantier et al., 1996] / 12-16 [Hubert 1982]
		alleles.put(MATING_LATENCY_Uday, 40.); // source: 61-21(weaningAgeMastomys) Duplantier et al., 1996
		alleles.put(GESTATION_LENGTH_Uday, 21.); // source: 3 weeks [Hubert and Adam, 1975; Poulet, 1972; Duplantier et al., 1996]
		makeAmniotaBivalent(this.alleles);

	}
	/** returns a new GenomeMErythroleucus. This method is used for genome mate.
	 * @param microsatXsome
	 * @param gonosome
	 * @param autosomes
	 * @see C_GenomeEucaryote#mate */
	public C_GenomeMastoErythroleucus(C_XsomePairMicrosat microsatXsome, C_XsomePairSexual gonosome,
			ArrayList<C_ChromosomePair> autosomes) {
		super(microsatXsome, gonosome, autosomes);
	}
	public C_GenomeMastoErythroleucus(Integer gender) {
		this();
		gonosome = new C_XsomePairSexual(gender);
	}
	//
	// METHOD
	//
	/** For Mastomys Erythroleucus, we consider that the caryotype is 2n = 14
	 * @author A.Comte and JLF 02.2012, rev. JLF 08.2014 */
	private void addHybridTestAutosomes() {
		double allele2e = 20, allele3e = 30, allele4e = 40, allele5e = 50;
		// locus maplocs of the chromosome Pairs:
		double xsome3q30 = 30, xsome5q70 = 70, xsome2q90 = 90, xsome4q50 = 50;
		int numGenesXsome2 = 1, numGenesXsome3 = 1, numGenesXsome4 = 1, numGenesXsome5 = 1;

		// four specific autosomes pairs for this species
		C_ChromosomePair xsomePair2 = new C_ChromosomePair(C_Chromosome.MAP_LENGTH, numGenesXsome2, recombinator);
		C_ChromosomePair xsomePair3 = new C_ChromosomePair(C_Chromosome.MAP_LENGTH, numGenesXsome3, recombinator);
		C_ChromosomePair xsomePair4 = new C_ChromosomePair(C_Chromosome.MAP_LENGTH, numGenesXsome4, recombinator);
		C_ChromosomePair xsomePair5 = new C_ChromosomePair(C_Chromosome.MAP_LENGTH, numGenesXsome5, recombinator);
		//
		C_Gene gene20 = new C_Gene(allele2e + getTaxonSignature(), xsome2q90, mutator, xsomePair2.xsomeStrands[0].getMyId());
		C_Gene gene21 = new C_Gene(allele2e + getTaxonSignature(), xsome2q90, mutator, xsomePair2.xsomeStrands[1].getMyId());
		C_Gene gene30 = new C_Gene(allele3e + getTaxonSignature(), xsome3q30, mutator, xsomePair3.xsomeStrands[0].getMyId());
		C_Gene gene31 = new C_Gene(allele3e + getTaxonSignature(), xsome3q30, mutator, xsomePair3.xsomeStrands[1].getMyId());
		C_Gene gene40 = new C_Gene(allele4e + getTaxonSignature(), xsome4q50, mutator, xsomePair4.xsomeStrands[0].getMyId());
		C_Gene gene41 = new C_Gene(allele4e + getTaxonSignature(), xsome4q50, mutator, xsomePair4.xsomeStrands[1].getMyId());
		C_Gene gene50 = new C_Gene(allele5e + getTaxonSignature(), xsome5q70, mutator, xsomePair5.xsomeStrands[0].getMyId());
		C_Gene gene51 = new C_Gene(allele5e + getTaxonSignature(), xsome5q70, mutator, xsomePair5.xsomeStrands[1].getMyId());

		xsomePair2.setGenePairAtLocus(0, new C_GenePair(gene20, gene21));
		xsomePair3.setGenePairAtLocus(0, new C_GenePair(gene30, gene31));
		xsomePair4.setGenePairAtLocus(0, new C_GenePair(gene40, gene41));
		xsomePair5.setGenePairAtLocus(0, new C_GenePair(gene50, gene51));
		bivalents.add(bivalents.size(), xsomePair2);
		bivalents.add(bivalents.size(), xsomePair3);
		bivalents.add(bivalents.size(), xsomePair4);
		bivalents.add(bivalents.size(), xsomePair5);
	}
	//
	// GETTER
	//
	@Override
	protected Double getTaxonSignature() {
		return .12;
	}
}
