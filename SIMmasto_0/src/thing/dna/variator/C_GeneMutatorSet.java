package thing.dna.variator;

import java.util.*;

import simmasto0.C_ContextCreator;
import thing.dna.C_Chromosome;
import thing.dna.C_Gene;

/** Simple mutator for genes whose values come from a set (actually, a List since random indices into the set must be computed). When
 * this set is provided to the mutator, any call to mutate will produce a value from the set, randomly selected.
 * @author kyle wagner, elyk@acm.org
 * Version 1.0, Sun Jan 28 17:41:21 2001, J.LeFur 09/2011 */

public class C_GeneMutatorSet implements I_GeneMutator {
	// FIELDS
	private List alleleList;
	// METHODS
	public C_GeneMutatorSet(List alleleList) {
		this.alleleList = alleleList;
	}

	/** Determine a new value for the allele, value, and return it. An index into the alleleList is randomly generated (uniform
	 * distribution, java.util.Random is used). value is ignored in this computation. Does no checking on info's or value's type,
	 * for efficiency's sake.
	 * @param info the mutation rate, expressed as a Double in interval [0,1]
	 * @param value the value that is being mutated. A member of the alleleSet, presumably. */
	public Object mutate(Object info, Object value) {
		Double mutRate = (Double) info;

		// If randomly chosen number is below mutation rate, mutate the gene,
		// otherwise leave it alone (return its original value).
		if (C_ContextCreator.randomGeneratorForDNA.nextDouble() < mutRate.doubleValue()) {
			// Generate random index into the set, then get the member at that index.
			int alleleIndex = (int) C_ContextCreator.randomGeneratorForDNA.nextDouble() * alleleList.size();
			System.out.println("Mutation");
			return alleleList.get(alleleIndex);
		}
		else {
			return value;
		}
	}

	public String toString() {
		return "<set mutator:" + alleleList + ">";
	}

	//
	// MAIN
	//
	public static void main(String[] args) {
		System.out.println("Testing SetGeneMutator");

		List<String> alleles = new ArrayList<String>(3);
		alleles.add("A");
		alleles.add("B");
		alleles.add("C");
		C_GeneMutatorSet setMut = new C_GeneMutatorSet(alleles);
		String val = "A";
		Double mutRate = new Double(0.5);

		System.out.println("val = " + val);
		System.out.println("Mutating at this rate: " + mutRate);
		for (int i = 1; i <= 10; i++) {
			System.out.println("mutated val = " + setMut.mutate(mutRate, val));
		}

		List<Integer> bits = new ArrayList<Integer>(2);
		bits.add(new Integer(0));
		bits.add(new Integer(1));
		C_GeneMutatorSet bitMut = new C_GeneMutatorSet(bits);

		int numGenes = 4;
		C_Chromosome bitGenome = new C_Chromosome(numGenes, new C_RecombinatorOnePt());
		Integer bitGenomeId = 0;
		for (int i = 0; i < numGenes; i++)

		{
			C_Gene bitGene = new C_Gene(new Integer(0), bitMut, bitGenomeId);
			bitGenome.setGeneAtLocus(i, bitGene);
		}

		System.out.println("bitGenome = " + bitGenome);
		System.out.println("Mutating at this rate: " + mutRate);
		for (int i = 1; i <= 10; i++) {
			bitGenome.mutate(mutRate);
			System.out.println("mutated genome = " + bitGenome);
		}
	}
}
