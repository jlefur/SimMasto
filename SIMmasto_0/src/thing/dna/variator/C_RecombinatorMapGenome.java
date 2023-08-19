package thing.dna.variator;

import simmasto0.C_ContextCreator;
import thing.dna.C_Chromosome;
import thing.dna.C_ChromosomePair;
import thing.dna.C_Gene;

/**
 * Single-point crossover using map units (on MapGenomes). Crossover point is chosen based on map
 * length (a "virtual" length) of the chromosome pair, not based on how many loci it has.
 * 
 * @author kyle wagner, elyk@cs.indiana.edu
 * Version 1.0, april 25, 2002, modif. J.LeFur 09/2011
 */

public class C_RecombinatorMapGenome implements I_Recombinator {
	public C_RecombinatorMapGenome() {
	}

	/**
	 * Perform 1-pt crossover on these two chromosomes and return the resulting new chromosome. This
	 * is efficient if Chromatids's getGene() is constant or even log(n) access time.
	 * 
	 * @param xsome1
	 *            xsome2 chromosomes to be recombined - parents of a new chromosome
	 * @return a NEW chromosome pair which is the recombination of genes from xsome1 and xsome2
	 *         Author kyle Wagner / rev. J.Le Fur 2011
	 */
	public C_ChromosomePair crossover(C_Chromosome xsome1, C_Chromosome xsome2) {
		if (xsome1.getNumGenes() != xsome2.getNumGenes()) throw new RuntimeException("Parent chromosomes are of different sizes!");
		// Make an offspring genome
		int numGenes = xsome1.getNumGenes();
		double mapLength1 = xsome1.getMapLength();
		I_Recombinator rec1 = xsome1.getRecombinator();
		C_Chromosome offspring1 = new C_Chromosome(mapLength1, numGenes, rec1);

		// Pick a random location along the genome (in map units) to
		// crossover
		double crossPt = xsome1.getMapLength() * C_ContextCreator.randomGeneratorForDNA.nextDouble();
		C_Chromosome curParent1 = xsome1;
		if ((int) (C_ContextCreator.randomGeneratorForDNA.nextDouble() * 2) == 0) curParent1 = xsome2;

		// Start at the beginning of the genome, copying alleles from one
		// parent, until crossPt is reached in curMapLoc. Then switch to
		// the other parent and copy alleles from them all the way to the
		// end of their genome.
		double curMapLoc = curParent1.getGene(0).getMapLoc();
		int locus = 0;
		while ((curMapLoc < crossPt) && (locus < numGenes)) {
			// Get parent's gene at this locus, then copy() it so that
			// it is a distinct object (and thus, mutations to copy won't affect
			// the original gene).
			C_Gene currentGene = curParent1.getGene(locus);
			curMapLoc = currentGene.getMapLoc();
			C_Gene copyGene = currentGene.copy();
			offspring1.setGeneAtLocus(locus, copyGene);
			locus++;// Update current locus
		}

		// Swap which parent is giving the genes...
		C_Chromosome curParent2 = null;
		if (curParent1 == xsome1) curParent2 = xsome2;
		else curParent2 = xsome1;

		while (locus < numGenes) {
			// Get parent's gene at this locus, then copy() it so that
			// it is a distinct object (and thus, mutations to copy won't affect
			// the original gene).
			C_Gene currentGene = curParent2.getGene(locus);
			C_Gene copyGene = currentGene.copy();
			// Indicate that the ownerxsome of the offspring gene is the one of the parent which
			// initiated the copyGene.
			copyGene.setOwnerXsomeNumber(((C_Gene) curParent1.getGeneWithMaploc(curMapLoc)).getOwnerXsomeNumber());
			offspring1.setGeneAtLocus(locus, copyGene);
			locus++;
		}

		// Make the second offspring chromosome of the pair
		double mapLength2 = xsome2.getMapLength();
		I_Recombinator rec2 = xsome2.getRecombinator();
		C_Chromosome offspring2 = new C_Chromosome(mapLength2, numGenes, rec2);

		// Start at the beginning of the genome, copying alleles from one
		// parent, until crossPt is reached in curMapLoc. Then switch to
		// the other parent and copy alleles from them all the way to the
		// end of their genome.
		curMapLoc = curParent2.getGene(0).getMapLoc();
		locus = 0;
		while ((curMapLoc < crossPt) && (locus < numGenes)) {
			// Get parent's gene at this locus, then copy() it so that
			// it is a distinct object (and thus, mutations to copy won't affect
			// the original gene).
			C_Gene currentGene = curParent2.getGene(locus);
			curMapLoc = currentGene.getMapLoc();
			C_Gene copyGene = currentGene.copy();
			offspring2.setGeneAtLocus(locus, copyGene);
			locus++;
		}
		while (locus < numGenes) {
			// Get parent's gene at this locus, then copy() it so that
			// it is a distinct object (and thus, mutations to copy won't affect
			// the original gene).
			C_Gene currentGene = curParent1.getGene(locus);
			C_Gene copyGene = currentGene.copy();
			copyGene.setOwnerXsomeNumber(((C_Gene) curParent2.getGeneWithMaploc(curMapLoc)).getOwnerXsomeNumber());
			offspring2.setGeneAtLocus(locus, copyGene);
			locus++;
		}
		return new C_ChromosomePair(offspring1, offspring2);
	}
}
