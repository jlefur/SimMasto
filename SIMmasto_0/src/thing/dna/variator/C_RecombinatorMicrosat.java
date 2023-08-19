package thing.dna.variator;

import simmasto0.C_ContextCreator;
import thing.dna.C_Chromosome;
import thing.dna.C_ChromosomePair;

/** generalized crossover for chromosome pair dedicated to microsatelites genes
 * @author @author JE.Longueville, 2011 / J.LeFur, 01.2012 */

public class C_RecombinatorMicrosat implements I_Recombinator {

	public C_RecombinatorMicrosat() {}

	/** Performs a full multi-point crossover on left and right strands of the C_XsomeMicrosat Retrieves a composite chromosome
	 * pair containing the value of NUMBER_GENES unlinked microsat locus. This procedure substitutes to extract unbound microsats
	 * positioned on various chromosome pairs (Cheat d'un nombre de 1 locus par chromosome pair).
	 * @return a NEW Chromosome pair which is the recombination of genes from left and right strands of the C_XsomeMicrosat */
	public C_ChromosomePair crossover(C_Chromosome xtid1, C_Chromosome xtid2) {
		if (xtid1.getNumGenes() != xtid2.getNumGenes()) throw new RuntimeException(
				"chromosomes to cross are of different sizes!");
		C_Chromosome crossedXsome1 = new C_Chromosome(xtid1.getNumGenes(), xtid1.getRecombinator());
		C_Chromosome crossedXsome2 = new C_Chromosome(xtid2.getNumGenes(), xtid2.getRecombinator());
		// C_chromosome creation supposes a multipoint recombinator
		for (int locus = 0; locus < xtid1.getNumGenes(); locus++) {
			// randomGenerator below is meant to keep control and reproducibility on random
			// generators - JLF 06-2011
			int randPosition = (int) (C_ContextCreator.randomGeneratorForDNA.nextDouble() * 2);
			if (randPosition == 0) crossedXsome1.setGeneAtLocus(locus, xtid1.getGene(locus));
			else crossedXsome1.setGeneAtLocus(locus, xtid2.getGene(locus));
		}
		// TODO JLF 2012.02 maybe useless to perform the loop two times since all is random; 
		for (int locus = 0; locus < xtid2.getNumGenes(); locus++) {
			// randomGenerator below is meant to keep control and reproducibility on random generators - JLF 06-2011
			int randPosition = (int) (C_ContextCreator.randomGeneratorForDNA.nextDouble() * 2);
			if (randPosition == 0) crossedXsome2.setGeneAtLocus(locus, xtid2.getGene(locus));
			else crossedXsome2.setGeneAtLocus(locus, xtid1.getGene(locus));
		}
		return new C_ChromosomePair(crossedXsome1, crossedXsome2);
	}
}
