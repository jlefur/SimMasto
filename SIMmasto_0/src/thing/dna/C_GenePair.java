package thing.dna;

import thing.dna.variator.C_GeneMutatorDouble;


/**
 * A gene pair is a couple of gene on homologous chromosomes. One is on the chromosome from the mother, 
 * and the other on the chromosome from the father. Both are on the same position on the two chromosomes.
 * This position is a "locus". However, their values of alleles are not necessarily the same.
 * @author kyle wagner, elyk@acm.org
 * Version 1.0, Fri Dec 15 11:16:26 2000
 */

public class C_GenePair {

	//
	// CONSTANTS
	//
	public static int LEFT = 0;
	public static int RIGHT = 1;
	//
	// FIELDS
	//
	private C_Gene leftGene;
	private C_Gene rightGene;
	//
	// METHODS
	//
	
	/**
	 * Returns a new gene pair
	 * @param leftGene is the gene on the mother's chromosome and rightGene the gene on the father's chromosome.
	 */
	public C_GenePair(C_Gene leftGene, C_Gene rightGene) {
		this.leftGene = leftGene;
		this.rightGene = rightGene;
	}
	/**
	 * @return the position of the gene pair on the chromosome pair
	 */
	public double getMapLoc() {
		// the position is the same for the two genes.
		return leftGene.getMapLoc();
	}
	/**
	 * @return One of the two genes of the gene pair.
	 */
	public C_Gene getGene(int position) {
		if (position == LEFT)
			return leftGene;
		else
			return rightGene;
	}

	public String toString() {
//		return "<" + leftGene.toString() + ", " + rightGene.toString() + ">";
		return leftGene.toString()+rightGene.toString();
	}

	public String toString2() {
		return "<" + leftGene.toString() + "/" + rightGene.toString() + ">";
	}

	// ////////////////////////////////////////////////////////////////////////
	// PROTECTED/PRIVATE METHODS
	// ////////////////////////////////////////////////////////////////////////

	//
	// MAIN
	//
	public static void main(String[] args) {
		System.out.println("Testing GenePair");

		C_Gene g1 = new C_Gene(new Double(.81), 2.5, new C_GeneMutatorDouble(),LEFT);
		C_Gene g2 = new C_Gene(new Double(.73), 2.5, new C_GeneMutatorDouble(),RIGHT);
		C_GenePair gp = new C_GenePair(g1, g2);
		System.out.println("gp: " + gp);
		System.out.println("gp.getMapLoc(): " + gp.getMapLoc());
		System.out.println("gp.getGene(LEFT): " + gp.getGene(LEFT));
		System.out.println("gp.getGene(RIGHT): " + gp.getGene(RIGHT));
	}
}
