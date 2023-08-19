package thing.dna.phenotyper;

import java.util.ArrayList;

import simmasto0.C_ContextCreator;
import thing.dna.C_GenePair;
import thing.dna.I_MappedDna;


public class C_TraitExpressorAllelesMismatches implements I_MapLocTraitExpressor {
	/**
	 * @return the number of allele mismatches within diploid pairs.
	 */
	public Object evalTrait(I_MappedDna mappedDNA, ArrayList<Double> traitMapLocs) {
		// Determine how many gene pairs there are; this tells me how
		// many genes I need to draw my samples from
		int numMapLocs = traitMapLocs.size();
		int numLocusPairsToCompare = numMapLocs;

		// Select a specified number of pairs of diploid genes to compare.
		// That is, select 2 genes and compare between and within the genes
		// for mismatched alleles.
		int numMismatches = 0;
		for (int i = 0; i < numLocusPairsToCompare; i++) {
			// Select 2 random genePairs by selecting random mapLocs, get
			// their "left"/"right" alleles
			int mapLocIndex1 = (int) (C_ContextCreator.randomGeneratorForDNA.nextDouble() * numMapLocs);
			Number mapLoc1 = (Number) traitMapLocs.get(mapLocIndex1);
			C_GenePair genePair1 = (C_GenePair) mappedDNA.getLocusAllele(mapLoc1.doubleValue());
			Integer allele1L = (Integer) genePair1.getGene(C_GenePair.LEFT).getAllele();
			Integer allele1R = (Integer) genePair1.getGene(C_GenePair.RIGHT).getAllele();

			// Make sure 2nd genePair isn't same as first
			int mapLocIndex2 = (int) (C_ContextCreator.randomGeneratorForDNA.nextDouble() * numMapLocs);
			Number mapLoc2 = (Number) traitMapLocs.get(mapLocIndex2);
			if (numMapLocs > 1) while (mapLocIndex2 == mapLocIndex1)
				mapLocIndex2 = (int) (C_ContextCreator.randomGeneratorForDNA.nextDouble() * numMapLocs);
			C_GenePair genePair2 = (C_GenePair) mappedDNA.getLocusAllele(mapLoc2.doubleValue());
			Integer allele2L = (Integer) genePair2.getGene(C_GenePair.LEFT).getAllele();
			Integer allele2R = (Integer) genePair2.getGene(C_GenePair.RIGHT).getAllele();

			/*
			 * System.out.println("genepair1: " + genePair1); System.out.println("genepair2: " +
			 * genePair2); System.out.println("  1ST: numMismatches: " + numMismatches);
			 */

			// Compare within gene pairs - is each gene heterozygous or
			// homozygous?
			if (allele1L.intValue() != allele1R.intValue()) numMismatches++;
			if (allele2L.intValue() != allele2R.intValue()) numMismatches++;

			// Compare between gene pairs - does 1st pair differ from the other?
			if (allele1L.intValue() != allele2L.intValue()) numMismatches++;
			if (allele1R.intValue() != allele2R.intValue()) numMismatches++;

			System.out.println("  NOW: numMismatches: " + numMismatches);
		}

		return numMismatches;
	}
}
