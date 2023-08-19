package thing.dna.variator;

import simmasto0.C_ContextCreator;
import thing.dna.C_Chromosome;
import thing.dna.C_ChromosomePair;
import thing.dna.C_Gene;

/**
 * Single-point crossover.
 * 
 * @author kyle wagner, elyk@cs.indiana.edu
 * Version 1.0, nov 30, 1998
 */

public class C_RecombinatorOnePt implements I_Recombinator {
	public C_RecombinatorOnePt() {
	}

	/**
	 * Perform a one point crossover on these two chromosomes homologous and return a new chromosome
	 * pair.
	 * @param xtid1
	 *            , xtid2 chromosomes to be recombined -
	 * @return a NEW Chromosome pair with two new homologous chromosomes which are the recombination
	 *         of xtid1 and xtid2 (contain the same genes, but not necessarily at the same place).
	 */
	public C_ChromosomePair crossover(C_Chromosome xtid1, C_Chromosome xtid2) {
		if (xtid1.getNumGenes() != xtid2.getNumGenes()) {
			System.out.println("C_RecombinatorOnePt.crossover()chromosomes to cross are of different sizes!");
			return new C_ChromosomePair(xtid1, xtid2);
		}
		/*
		 * throw new RuntimeException("chromosomes to cross are of different sizes!");
		 */

		// Make an offspring genome
		C_Chromosome gamete1 = new C_Chromosome(xtid1.getNumGenes(), xtid1.getRecombinator());
		C_Chromosome gamete2 = new C_Chromosome(xtid2.getNumGenes(), xtid2.getRecombinator());
		// Copy parent1's genes to offspring until crossover pt is
		// reached, then switch and copy parent2's genes to offspring.
		// int crossIndex = Math.abs((int) C_ContextCreator.randomGeneratorForDNA.nextDouble()) %
		// xtid1.numGenes(); // From Kyle Wagner, not understood !
		int crossIndex = (int) ((xtid1.getNumGenes() + 1) * C_ContextCreator.randomGeneratorForDNA.nextDouble());
		C_Gene parentGene1 = null, parentGene2 = null, copyGene1 = null, copyGene2 = null;
		for (int locus = 0; locus < crossIndex; locus++) {
			// Get parent's gene at this locus, then copy() it so that
			// it is a distinct object (and thus, mutations to copy won't affect
			// the original gene).
			parentGene1 = xtid1.getGene(locus);
			copyGene1 = parentGene1.copy();
			parentGene2 = xtid2.getGene(locus);
			copyGene2 = parentGene2.copy();
			copyGene1.setOwnerXsomeNumber(parentGene1.getOwnerXsomeNumber());
			copyGene2.setOwnerXsomeNumber(parentGene2.getOwnerXsomeNumber());
			gamete1.setGeneAtLocus(locus, copyGene1);
			gamete2.setGeneAtLocus(locus, copyGene2);
		}
		for (int locus = crossIndex; locus < xtid1.getNumGenes(); locus++) {
			// Get parent's gene at this locus, then copy() it so that
			// it is a distinct object (and thus, mutations to copy won't affect
			// the original gene).
			parentGene1 = xtid2.getGene(locus);
			copyGene1 = parentGene1.copy();
			parentGene2 = xtid1.getGene(locus);
			copyGene2 = parentGene2.copy();
			copyGene1.setOwnerXsomeNumber(parentGene2.getOwnerXsomeNumber());
			copyGene2.setOwnerXsomeNumber(parentGene1.getOwnerXsomeNumber());
			gamete1.setGeneAtLocus(locus, copyGene1);
			gamete2.setGeneAtLocus(locus, copyGene2);
		}
		return new C_ChromosomePair(gamete1, gamete2);
	}
}
