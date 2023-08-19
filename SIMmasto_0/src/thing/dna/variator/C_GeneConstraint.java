package thing.dna.variator;

import java.util.*;

/** @author kyle wagner, elyk@acm.org
 * Version 1.0, Wed Dec 13 20:55:33 2000 */

public class C_GeneConstraint {
	//
	// CONSTANTS
	//
	public static final int MIN_MAX = 0;
	public static final int ALLELE_SET = 1;
	//
	// FIELDS
	//
	private int constraintType;
	@SuppressWarnings("rawtypes")// alleles may be of any type
	private Set legalAlleles;
	private Object defaultAllele;
	private double minAllele, maxAllele;
	private Double minAlleleDbl, maxAlleleDbl;
	//
	// METHODS
	//
	@SuppressWarnings("rawtypes")// alleles may be of any type
	public C_GeneConstraint(Set legalAlleles, Object defaultAllele) {
		this.legalAlleles = legalAlleles;
		this.defaultAllele = defaultAllele;
		constraintType = ALLELE_SET;
	}
	public C_GeneConstraint(double minAllele, double maxAllele) {
		this.minAllele = minAllele;
		this.maxAllele = maxAllele;
		minAlleleDbl = new Double(minAllele);
		maxAlleleDbl = new Double(maxAllele);
		constraintType = MIN_MAX;
	}
	/** Check the given allele. If it's out of bounds or not in the set of allowable alleles, return a new, default allele (either a
	 * default allele or the closest min/max allele allowed).
	 * @param allele the Gene value that must be constrained
	 * @return the constrained allele for the Gene (no change if allele is a legal allele) */
	public Object constrain(Object allele) {
		// Alleles are either Doubles which have max/min values, or they
		// are objects that are members of a set.
		if (constraintType == MIN_MAX) {
			// If the allele is a Double and exceeds the max, return the max.
			// Similar for the min. Otherwise, the allele is fine - return it.
			double alleleDbl = ((Double) allele).doubleValue();
			if (alleleDbl < minAllele) return minAlleleDbl;
			else if (alleleDbl > maxAllele) return maxAlleleDbl;
			else return allele;
		}
		else if (constraintType == ALLELE_SET) {
			// If the allele is in the set of allowed alleles, it's ok - return
			// it.
			// Otherwise, return the default allele value.
			if (legalAlleles.contains(allele)) return allele;
			else return defaultAllele;
		}

		throw new RuntimeException("not implemented yet");
	}
	public String toString() {
		String str = "<constraint ";
		if (constraintType == MIN_MAX) {
			str += minAlleleDbl.toString() + ".." + maxAlleleDbl.toString();
		}
		else if (constraintType == ALLELE_SET) {
			str += legalAlleles + "  " + defaultAllele;
		}
		str += ">";
		return str;
	}
	//
	// MAIN
	//
	public static void main(String[] args) {
		System.out.println("Testing GeneConstraint");
		C_GeneConstraint gcDbl = new C_GeneConstraint(0, 5);
		Object[] dblVals = {new Double(-1), new Double(0), new Double(0.8), new Double(5), new Double(100)};

		Set<String> geneSet = new TreeSet<String>();
		geneSet.add("BLUE");
		geneSet.add("RED");
		geneSet.add("YELLOW");
		C_GeneConstraint gcSet = new C_GeneConstraint(geneSet, "BLUE");
		Object[] setVals = {"GREEN", "BLUE", "RED", "BLACK"};

		testConstraint(gcDbl, dblVals);
		testConstraint(gcSet, setVals);
	}

	private static void testConstraint(C_GeneConstraint gc, Object[] testValues) {
		System.out.println("Testing " + gc);
		for (int i = 0; i < testValues.length; i++)
			System.out.println("constrain(" + testValues[i] + "): " + gc.constrain(testValues[i]));

	}
}
