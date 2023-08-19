/*This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt*/
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
public class C_GenomeMastoNatalensis extends C_GenomeMastomys {
	//
	// CONSTRUCTORS
	//
	/** Returns a new C_GenomeMastoNatalensis. <br>
	 * Trait values references (see comments in source code):<br>
	 * - Granjon, L. and J.M. Duplantier (2009)Les rongeurs de l'Afrique sahélo-soudanienne. IRD Editions, Publications Scientifiques du Muséum -
	 * Collection Faune et Flore tropicales 43 / / ISBN IRD : 978-2-7099-1675-2 - <br>
	 * - Duplantier, J.M., Granjon, L. and Bouganaly, H. (1996) Reproductive characteristics of three sympatric species of Mastomys in Senegal, as
	 * observed in the field and in captivity. Mammalia, 60(4): 629-638. */
	public C_GenomeMastoNatalensis() {
		super();
		addHybridTestAutosomes();
		alleles.put(SEXUAL_MATURITY_Uday, 84.);//Dimorphisme sexuel -> maturité mâles et femelles différentes, à vérifier (LG)
		alleles.put(LITTER_SIZE, 5.); // source:9 embryos*60% -> Granjon & Duplantier 2009]/ 6.5 (litter size) +60% survival -> 3-4 at age of
										// reproduction (LG) / 6.5 (from which 50 % survive to weaning) [Duplantier et al. 1996]
		alleles.put(MATING_LATENCY_Uday, 39.); // source: 53-22(weaningAgeMastomys) Duplantier et al., 1996 / Sow: 75-22 -> 60-21 (LG)
		alleles.put(GESTATION_LENGTH_Uday, 21.); // source: Duplantier et al., 1996
		makeAmniotaBivalent(this.alleles);

	}
	/** Returns a new GenomeMNalatensis. This method is used for genome mate.
	 * @param microsatXsome
	 * @param gonosome
	 * @param autosomes
	 * @see C_GenomeEucaryote#mate */
	public C_GenomeMastoNatalensis(C_XsomePairMicrosat microsatXsome, C_XsomePairSexual gonosome, ArrayList<C_ChromosomePair> autosomes) {
		super(microsatXsome, gonosome, autosomes);
	}
	public C_GenomeMastoNatalensis(Integer gender) {
		this();
		gonosome = new C_XsomePairSexual(gender);
	}
	//
	// METHOD
	//
	/** For Mastomys Natalensis, we consider that the caryotype is 2n = 8
	 * @author A.Comte & JLF 02.2012, rev. JLF 08.2014 */
	private void addHybridTestAutosomes() {
		double allele2n = 20, allele3n = 30, allele4n = 40, allele5n = 50;
		// locus maplocs of the chromosome Pairs:
		double xsome1q30 = 30, xsome1q70 = 70, xsome2q90 = 90, xsome2q50 = 50;
		int numGenesXsome1 = 2, numGenesXsome2 = 2;
		// two specific autosomes pairs for this species
		C_ChromosomePair xsomePair1 = new C_ChromosomePair(C_Chromosome.MAP_LENGTH, numGenesXsome1, recombinator);
		C_ChromosomePair xsomePair2 = new C_ChromosomePair(C_Chromosome.MAP_LENGTH, numGenesXsome2, recombinator);
		// 2 locus for the chromosome pair 1
		C_Gene gene30 = new C_Gene(allele3n + getTaxonSignature(), xsome1q30, mutator, xsomePair1.xsomeStrands[0].getMyId());
		C_Gene gene31 = new C_Gene(allele3n + getTaxonSignature(), xsome1q30, mutator, xsomePair1.xsomeStrands[1].getMyId());
		C_Gene gene50 = new C_Gene(allele5n + getTaxonSignature(), xsome1q70, mutator, xsomePair1.xsomeStrands[0].getMyId());
		C_Gene gene51 = new C_Gene(allele5n + getTaxonSignature(), xsome1q70, mutator, xsomePair1.xsomeStrands[1].getMyId());
		// 2 locus for the chromosome pair 2
		C_Gene gene20 = new C_Gene(allele2n + getTaxonSignature(), xsome2q90, mutator, xsomePair2.xsomeStrands[0].getMyId());
		C_Gene gene21 = new C_Gene(allele2n + getTaxonSignature(), xsome2q90, mutator, xsomePair2.xsomeStrands[1].getMyId());
		C_Gene gene40 = new C_Gene(allele4n + getTaxonSignature(), xsome2q50, mutator, xsomePair2.xsomeStrands[0].getMyId());
		C_Gene gene41 = new C_Gene(allele4n + getTaxonSignature(), xsome2q50, mutator, xsomePair2.xsomeStrands[1].getMyId());

		xsomePair1.setGenePairAtLocus(0, new C_GenePair(gene30, gene31));
		xsomePair1.setGenePairAtLocus(1, new C_GenePair(gene50, gene51));
		xsomePair2.setGenePairAtLocus(0, new C_GenePair(gene20, gene21));
		xsomePair2.setGenePairAtLocus(1, new C_GenePair(gene40, gene41));
		bivalents.add(bivalents.size(), xsomePair1);
		bivalents.add(bivalents.size(), xsomePair2);
	}
	//
	// GETTER
	//
	@Override
	protected Double getTaxonSignature() {
		return .11;
	}
}
