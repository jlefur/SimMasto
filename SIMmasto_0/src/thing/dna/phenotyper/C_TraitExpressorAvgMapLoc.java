package thing.dna.phenotyper;

import java.util.ArrayList;
import java.util.Iterator;

import simmasto0.C_ContextCreator;
import thing.dna.C_Chromosome;
import thing.dna.C_ChromosomePair;
import thing.dna.C_Gene;
import thing.dna.C_GenePair;
import thing.dna.I_MappedDna;
import thing.dna.variator.C_GeneConstraint;
import thing.dna.variator.C_GeneMutatorDouble;
import thing.dna.variator.C_RecombinatorOnePt;
import thing.dna.variator.I_GeneMutator;

/** Provides a simple expressor: adds up all real-valued alleles among the trait's loci, returning the average. Does not use dominance or epistasis.
 * Assumes all alleles are Numbers.
 * <P>
 * @author kyle wagner, elyk@acm.org
 * Version 1.0, Mon Dec 3 14:17:22 2001, rev. JLF 08.2014 */

public class C_TraitExpressorAvgMapLoc implements I_MapLocTraitExpressor {
	/** @param dna the genome which will express this trait (the object holding info about the genetic structure of this trait ?)
	 * @param traitMapLocs the map locations of the loci in the genome which code for this trait
	 * @return a Double representing the value of the trait */
	@Override
	public Object evalTrait(I_MappedDna dna, ArrayList<Double> traitMapLocs) {
		int numAlleles = 0;
		double sum = 0;
		Iterator<Double> mapLocIter = traitMapLocs.iterator();
		while (mapLocIter.hasNext()) {
			Number mapLoc = (Number) mapLocIter.next();
			Object allele = dna.getLocusAllele(mapLoc.doubleValue());
			double alleleVal = 0;
			if (allele instanceof Number) alleleVal = ((Number) allele).doubleValue();
			else if (allele instanceof C_GenePair) {
				C_GenePair gPair = (C_GenePair) allele;
				C_Gene geneL = gPair.getGene(C_GenePair.LEFT);
				C_Gene geneR = gPair.getGene(C_GenePair.RIGHT);
				Number alleleL = (Number) geneL.getAllele();
				Number alleleR = (Number) geneR.getAllele();
				alleleVal = ((alleleL.doubleValue() + alleleR.doubleValue()) / 2);
				// System.out.println("==>DIP:  " + alleleL + " " + alleleR);
			}
			else {
				throw new RuntimeException("bad allele type: " + allele);
			}
			// System.out.println(mapLoc + " " + alleleVal);
			sum += alleleVal;
			numAlleles++;
		}
		return new Double(sum / numAlleles);
	}

	@Override
	public String toString() {
		return "AvgMapLocTraitExpressor";
	}

	public static void main(String[] args) {
		I_MapLocTraitExpressor avgExpr = new C_TraitExpressorAvgMapLoc();

		// Make a haploid and a diploid genome.
		I_GeneMutator gMut = new C_GeneMutatorDouble();
		C_GeneConstraint gConstr = new C_GeneConstraint(-10, 10);
		int numLoci = 5;
		C_Chromosome hapXsome = new C_Chromosome(numLoci, new C_RecombinatorOnePt());
		C_ChromosomePair xsomePair = new C_ChromosomePair(numLoci, new C_RecombinatorOnePt());

		ArrayList<Double> mapLocs = new ArrayList<Double>();
		for (int i = 0; i < numLoci; i++)
			mapLocs.add((C_ContextCreator.randomGeneratorForDNA.nextDouble() * 100));
		for (int i = 0; i < numLoci; i++) {
			Double mapLoc = mapLocs.get(i);
			C_Gene dblGene = new C_Gene(new Double(C_ContextCreator.randomGeneratorForDNA.nextDouble()), mapLoc, gMut, 0, gConstr);

			hapXsome.setGeneAtLocus(i, dblGene);
			xsomePair.setGenePairAtLocus(i, new C_Gene(new Double(C_ContextCreator.randomGeneratorForDNA.nextDouble() * 2), mapLoc, gMut, 0),
					new C_Gene(new Double(C_ContextCreator.randomGeneratorForDNA.nextDouble() * 2), mapLoc, gMut, 0));
		}
		Double value = (Double) avgExpr.evalTrait(hapXsome, mapLocs);
		System.out.println("hapGenome: " + hapXsome);
		System.out.println("value: " + value);

		value = (Double) avgExpr.evalTrait(xsomePair, mapLocs);
		System.out.println("dipGenome: " + xsomePair);
		System.out.println("value: " + value);
	}
}
