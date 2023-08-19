package thing.dna;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Random;
import thing.dna.variator.C_GeneConstraint;
import thing.dna.variator.C_GeneMutatorDouble;
import thing.dna.variator.I_GeneMutator;

/** A Gene that is part of some chromosome pair. This is the basic component of a HaploidGenome or a DiploidGenome. It has an allele and can mutate
 * itself. Its allele can be anything - character, string, integer, double, whatever. The mutate() method provides an abstract way to change it so
 * that Genomes don't have to know how to mutate - they just tell the Gene to do so. Mutation is done by a Mutator, passed in upon construction. This
 * is a simple interface, providing a way to mutate a gene's allele that is specific to the allele's type (e.g., integer, double, or character).
 * <P>
 * Gene alleles can have constraints. If a GeneConstraint is provided, then the Gene's allele upon construction and after mutation will not violate
 * these constraints. If no constraints are given, the Gene can have any allele (of its type, according to its mutator).
 * <P>
 * @author kyle wagner, elyk@acm.org
 * Version 1.0, Mon Dec 11 23:06:08 2000 */

public class C_Gene implements Comparable<C_Gene> {
	//
	// CONSTANTS
	//
	private static final DecimalFormat decFormat;
	static {
		decFormat = new DecimalFormat();
		decFormat.applyPattern("0.000");
	}
	//
	// FIELDS
	//
	protected Object allele;
	protected double mapLoc;
	protected I_GeneMutator mutator;
	protected C_GeneConstraint geneConstraint;
	protected int ownerXsomeNumber;
	//
	// CONSTRUCTORS
	//
	/** @param mapLoc the location along the chromosome pair (in map units) of this Gene */
	public C_Gene(Object allele, double mapLoc, I_GeneMutator mutator, int ownerXsomeId, C_GeneConstraint geneConstraint) {
		this.mapLoc = mapLoc;
		this.mutator = mutator;
		this.geneConstraint = geneConstraint;
		this.ownerXsomeNumber = ownerXsomeId; // we chose not to transmit the whole chromosome but just an integer representation to maintain
												// encapsulation JLF 2012
		// Constrain gene's value if a constraint was given
		if (geneConstraint == null) this.allele = allele;
		else this.allele = geneConstraint.constrain(allele);
	}
	public C_Gene(Object allele, I_GeneMutator mutator, int ownerXsomeId, C_GeneConstraint geneConstraint) {
		this(allele, 0.0, mutator, ownerXsomeId, geneConstraint);
	}
	public C_Gene(Object allele, double mapLoc, I_GeneMutator mutator, int ownerXsomeId) {
		this(allele, mapLoc, mutator, ownerXsomeId, null);
	}
	public C_Gene(Object allele, I_GeneMutator mutator, int ownerXsomeId) {
		this(allele, mutator, ownerXsomeId, null);
	}
	//
	// METHODS
	//
	//
	/** @param info could be the mutation rate or even more info required by the mutator to do its job. */
	public void mutate(Object info) {
		this.allele = mutator.mutate(info, allele);
		if (geneConstraint != null) allele = geneConstraint.constrain(allele);
	}
	/** Return a copy of this gene. Its allele will be a reference (shallow) copy, since java doesn't provide an adequate clone() interface. */
	public C_Gene copy() {
		return new C_Gene(allele, mapLoc, mutator, ownerXsomeNumber, geneConstraint);
	}
	public String toString() {
		if (allele instanceof Double) return decFormat.format(((Double) allele).doubleValue());// +"/"+ownerXsomeNumber;
		else return allele.toString();// +"/"+ownerXsomeNumber;
	}
	public String toString2() {
		if (allele instanceof Double) return decFormat.format(mapLoc) + " " + decFormat.format(((Double) allele).doubleValue());
		else return decFormat.format(mapLoc) + " " + allele.toString();
	}
	public int compareTo(C_Gene other) {
		int nb1 = (int) other.mapLoc;
		int nb2 = (int) this.mapLoc;
		if (nb1 > nb2) return 1;
		else if (nb1 == nb2) return 0;
		else return -1;
	}
	//
	// SETTERS & GETTERS
	//
	/** @return the taxonSignature (integer number) of the gene. author: Aurore Comte 2012 */
	public double getTaxonSignature() {
		double allele = ((Number) this.getAllele()).doubleValue();
		String s1 = Double.toString(allele);
		int index = s1.indexOf(".");
		if ((index != -1) & (index < s1.length() - 2)) {
			s1 = s1.substring(index + 1, index + 3);
		}
		return Double.parseDouble(s1);
	}
	/** @return the value of the allele carried by the gene. */
	public Object getAllele() {
		return allele;
	}
	public void setAllele(Object allele) {
		this.allele = allele;
	}
	public int getOwnerXsomeNumber() {
		return ownerXsomeNumber;
	}
	public void setOwnerXsomeNumber(int ownerXsomeNumber) {
		this.ownerXsomeNumber = ownerXsomeNumber;
	}
	/** @return the position of the gene on the chromosome */
	public double getMapLoc() {
		return mapLoc;
	}
	/** @return the GeneConstraint of the gene. */
	public C_GeneConstraint getConstraint() {
		return geneConstraint;
	}
	//
	// MAIN
	//
	public static void main(String[] args) {
		System.out.println("Testing Gene");
		double geneLoc = 1.28;
		C_Gene dblGene = new C_Gene(new Double(3), geneLoc, new C_GeneMutatorDouble(-0.5, 0.5), 0, new C_GeneConstraint(2.5, 3.5));
		testGene(dblGene);

		System.out.println("\nOrig gene:   " + dblGene);
		System.out.println("...mutating the copy:");
		C_Gene copyGene = dblGene.copy();
		testGene(copyGene);
		System.out.println("Orig gene:   " + dblGene);
		System.out.println("Copied gene: " + copyGene);
		System.out.println("\n");

		final java.util.Set<String> colorSet = new java.util.TreeSet<String>();
		colorSet.add("Blue");
		colorSet.add("Red");
		colorSet.add("Yellow");
		C_GeneConstraint strGC = new C_GeneConstraint(colorSet, "Blue");
		I_GeneMutator stringMut = new I_GeneMutator() {
			public Object mutate(Object info, Object value) {
				Random randGen = new Random();
				Double mutRate = (Double) info;
				// Random isn't very good - keeps outputting streams of similar values
				// (all 0.9xxxx or 0.5xxxx for example).
				double r = randGen.nextDouble();
				r = r * 100 - (int) (r * 100);
				/*
				 * System.out.println("r=" + r + //"  value=" + value + "  info=" + info);
				 */
				if (r < mutRate.doubleValue())
				// if (randGen.nextDouble() < mutRate.doubleValue())
				{
					int loc = ((int) randGen.nextDouble() * colorSet.size());
					Iterator<String> setIter = colorSet.iterator();
					String val = "";
					int ctr = 0;
					while (setIter.hasNext() && (ctr <= loc)) {
						val = (String) setIter.next();
						ctr++;
					}
					System.out.println("newval: " + val);
					return val;
				}
				else {
					return value;
				}
				/*
				 * StringBuffer strBfr = new StringBuffer((String)value); // If randomly chosen number is below mutation rate, mutate the gene, //
				 * otherwise leave it alone (return its original value). java.util.Random randGen = new java.util.Random(); if (randGen.nextDouble() <
				 * mutRate.doubleValue()) { int loc = ((int)randGen.nextDouble()*strBfr.length())); char ch = (char)(int)(randGen.nextDouble()*26 +
				 * 65); strBfr.setCharAt(loc, ch); return strBfr.toString(); } else { return value; }
				 */
			}
		};
		C_Gene stringGene = new C_Gene("ROSE", stringMut, 0, strGC);
		testGene(stringGene);

		System.out.println("\nOrig gene:   " + stringGene);
		System.out.println("...mutating the copy:");
		copyGene = stringGene.copy();
		testGene(copyGene);
		System.out.println("Orig gene:   " + stringGene);
		System.out.println("Copied gene: " + copyGene);
		System.out.println("\n");
	}
	private static final void testGene(C_Gene gene) {
		System.out.println("gene: " + gene);
		System.out.println("  gene.getAllele(): " + gene.getAllele());
		System.out.println("  gene.getMapLoc(): " + gene.getMapLoc());
		for (int i = 0; i < 5; i++) {
			gene.mutate(new Double(0.5));
			System.out.println("  mutated gene: " + gene);
		}
	}
}
