package thing.dna;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import simmasto0.C_ContextCreator;
import thing.dna.variator.C_GeneConstraint;
import thing.dna.variator.C_GeneMutatorDouble;
import thing.dna.variator.C_RecombinatorMapGenome;
import thing.dna.variator.I_GeneMutator;
import thing.dna.variator.I_Recombinator;
import data.constants.I_ConstantNumeric;

/** A Chromosome pair is just two chromosomes of a diploid genome.
 * <P>
 * NOTE: map locations are currently not checked for sequential ordering, so each new Gene that is added must be at a higher map
 * location than all of the previously added Genes.
 * <P>
 * @author kyle wagner, elyk@acm.org / JLF 2010 - was formerly class diploidGenome
 * Version 1.1, Aug 28, 2002 / 01.2012 */

public class C_ChromosomePair implements I_MappedDna, I_ConstantNumeric {
	//
	// CONSTANTS
	//
	/** LEFT and RIGHT strand for the chromosome of the mother and the chromosome of the father. The terms are maintained from the
	 * cricketSim version */
	public static final int PARENT_1 = 0;
	public static final int PARENT_2 = 1;
	/* Default values for any type of gene creation */
	protected static final double DEFAULT_CROSSOVER_PROB = .5;
	protected static final double DEFAULT_ALLELE_MIN = 0;
	protected static final double DEFAULT_ALLELE_MAX = Double.MAX_VALUE;
	protected static final int DEFAULT_ALLELE_VAL = 0;
	protected static final C_GeneConstraint DEFAULT_GENE_CONSTRAINT;
	protected static final I_GeneMutator DEFAULT_GENE_MUTATOR;
	// protected static final I_recombinator MAP_GENOME_RECOMBINATOR;
	/** Mean and std of allele mutations on a normal distribution */
	static {
		DEFAULT_GENE_CONSTRAINT = new C_GeneConstraint(DEFAULT_ALLELE_MIN, DEFAULT_ALLELE_MAX);
		DEFAULT_GENE_MUTATOR = new C_GeneMutatorDouble();
	}
	//
	// FIELDS
	//
	/** xsomeStrands stands for the chromosome of the father and the chromosome of the mother. The names strands, LEFT, RIGHT are
	 * maintained from the cricketSim version */
	public C_Chromosome[] xsomeStrands;
	protected I_Recombinator recombinator;
	protected double mapLength;
	protected C_GeneConstraint mappedGeneConstraint;
	protected I_GeneMutator mappedGeneMutator;
	//
	// CONSTRUCTORS
	//
	/** @param recombinator the Recombinator works for HaploidGenomes only, but since the DiploidGenome is just 2 haploids, the
	 *            recombinator is appropriate - it operates on the 2 haploid strands. */
	public C_ChromosomePair(int numGenes, I_Recombinator recombinator) {
		this.mappedGeneMutator = DEFAULT_GENE_MUTATOR;
		this.mappedGeneConstraint = DEFAULT_GENE_CONSTRAINT;
		this.mapLength = C_Chromosome.MAP_LENGTH;
		this.recombinator = recombinator;
		xsomeStrands = new C_Chromosome[2];
		xsomeStrands[PARENT_1] = new C_Chromosome(mapLength, numGenes, recombinator);
		xsomeStrands[PARENT_2] = new C_Chromosome(mapLength, numGenes, recombinator);
	}

	/** @param mapLength the length of this chromosome pair in map units
	 * @param numGenes how many genes will be in ont strand of the final chromosome pair (only for efficiency - if count is
	 *            incorrect, genome will still function properly, it will simply take a longer time to construct).
	 * @param recombinator the Recombinator works for HaploidGenomes only, but since the DiploidGenome is just 2 haploids, the
	 *            recombinator is appropriate - it operates on the 2 haploid strands. */
	public C_ChromosomePair(double mapLength, int numGenes, I_Recombinator recombinator) {
		this.mapLength = mapLength;
		this.recombinator = recombinator;
		xsomeStrands = new C_Chromosome[2];
		xsomeStrands[PARENT_1] = new C_Chromosome(mapLength, numGenes, recombinator);
		xsomeStrands[PARENT_2] = new C_Chromosome(mapLength, numGenes, recombinator);
	}

	/** Used for replication, etc. */
	public C_ChromosomePair(C_Chromosome leftXtid, C_Chromosome rightXtid) {
		this.recombinator = leftXtid.getRecombinator();
		this.mapLength = leftXtid.getMapLength();
		xsomeStrands = new C_Chromosome[2];
		xsomeStrands[PARENT_1] = leftXtid.replicate(DEFAULT_MUT_RATE);
		xsomeStrands[PARENT_2] = rightXtid.replicate(DEFAULT_MUT_RATE);
	}
	//
	// METHODS
	//

	public int numGenes() {
		if (xsomeStrands[PARENT_1].getNumGenes() != xsomeStrands[PARENT_2].getNumGenes()) System.err
				.println("C_ChromosomePair.numGenes()" + "numGenes are different between the two strands: " + getXsomeStrand(0)
						+ "/" + getXsomeStrand(1));
		// Assume that size of LEFT and RIGHT are identical...
		return xsomeStrands[PARENT_1].getNumGenes();
	}

	/** Potentially modify all of the Genes (or components) of this Genetic object. Mutate both strands according to how
	 * HaploidGenome's mutate() works. Variation may be constrained by a rate (a mutation rate), or other information provided in
	 * the argument, info.
	 * @param info An optional argument (can be null). Often this will be a Double representing a mutation rate. */
	public void mutate(Object info) {
		xsomeStrands[PARENT_1].mutate(info);
		xsomeStrands[PARENT_2].mutate(info);
	}

	public I_DnaThing copySegment(int startLocus, int endLocus) {
		// Make a new genome that's big enough to hold the copied genes.
		int numGenesToCopy = endLocus - startLocus;
		C_ChromosomePair copyGenome = new C_ChromosomePair(numGenesToCopy, this.recombinator);
		// Do the copy from my alleles in the specified range to copyGenome.
		for (int locus = startLocus; locus < endLocus; locus++) {
			C_GenePair genePair = this.getGenePair(locus);
			copyGenome.setGenePairAtLocus(locus, genePair);
		}

		int lastSegLocus = endLocus - 1;
		int finalLocus = (getNumLoci() - 1);

		// Estimate the map length of this segment ... this isn't terribly accurate. Just finds out the map locations of the loci
		// right around the segment and splits the difference when creating the new segment.
		double copyMapBegin = this.getGenePair(startLocus).getMapLoc();
		if (startLocus > 0) {
			double copyMapBefore = this.getGenePair(startLocus - 1).getMapLoc();
			double copyMapAfter = copyMapBegin;
			copyMapBegin = (copyMapBefore + copyMapAfter) / 2;
		}
		else if (startLocus == 0) copyMapBegin = 0;

		double copyMapEnd = this.getGenePair(lastSegLocus).getMapLoc();
		if (lastSegLocus < finalLocus) {
			double copyMapBefore = copyMapEnd;
			double copyMapAfter = this.getGenePair(lastSegLocus + 1).getMapLoc();
			copyMapEnd = (copyMapBefore + copyMapAfter) / 2;
		}
		else if (lastSegLocus == finalLocus) copyMapEnd = mapLength;
		double copyMapLength = copyMapEnd - copyMapBegin;
		copyGenome.mapLength = copyMapLength;
		return copyGenome;
	}
	/** The replicated individual does not have to be asexual, but is most likely to have this property.
	 * @param info Possibly a mutation rate, or other information needed in order to perform the replication.
	 * @return a new Genetic object, a duplicate of this one. Mutations might have occured, however. */
	public I_DnaThing replicate(Object info) {
		// Duplicate the two haploid strands, then make a new diploid with the duplicated strands and the source parent's
		// recombinators.
		C_Chromosome leftCopy = (C_Chromosome) xsomeStrands[PARENT_1].replicate(info);
		C_Chromosome rightCopy = (C_Chromosome) xsomeStrands[PARENT_2].replicate(info);
		return new C_ChromosomePair(leftCopy, rightCopy);
	}
	/** The replicated individual does not have to be bisexual, but is most likely to have this property.
	 * @param info Possibly a mutation rate, or other information needed in order to perform the mating.
	 * @param parent2 the other individual involved in this mating.
	 * @return a new Genetic object, a combination of the two parents.. Mutations might have occurred, however. */
	public I_DnaThing mate(Object info, I_DnaThing parent2) {
		I_DnaThing offspring = this.crossover(parent2);
		// offspring.mutate(info);
		return offspring;
	}
	/** Mutates all genes in both strands of this genome. */
	public void randomize() {
		xsomeStrands[PARENT_1].randomize();
		xsomeStrands[PARENT_2].randomize();
	}
	public I_MappedDna crossover() {
		// Crossover both parents' original strands separately -> two haploid strands, or gametes.
		C_ChromosomePair crossedPair = recombinator.crossover(xsomeStrands[PARENT_1], xsomeStrands[PARENT_2]);
		return crossedPair;
	}
	@Override
	public I_DnaThing crossover(I_DnaThing parent2) {
		return null;
	}
	public String toString() {
		// Show both strands in pairwise fashion ... get each GenePair and concat those into a String.
		StringBuffer strBfr = new StringBuffer("{diploid ");
		strBfr.append(mapLength + "mu ");
		for (int i = 0; i < numGenes(); i++) {
			// strBfr.append(getGenePair(i));
			strBfr.append(getGenePair(i).toString2());
			strBfr.append(" ");
		}
		strBfr.append("}");
		return strBfr.toString();
		// Would be better to show both strands pairwise ... get each GenePair // and concat those into a String. String str =
		// "<diploid L: "; str+= xsomeStrands[LEFT].toString(); str += " R:"; str+= xsomeStrands[RIGHT].toString(); str +=
		// ">";return str;
	}
	/** A simpler, slightly more compact String form of the genome */
	public String toString2() {
		// Show both strands in pairwise fashion ... get each GenePair and concat those into a String.
		StringBuffer strBfr = new StringBuffer("");
		for (int i = 0; i < numGenes(); i++) {
			// strBfr.append(getGenePair(i));
			strBfr.append(getGenePair(i).toString2());
			strBfr.append(" ");
		}
		return strBfr.toString();
	}
	//
	// SETTERS & GETTERS
	//
	/*
	 * public void addGenePair(C_Gene leftGene, C_Gene rightGene) { xsomeStrands[PARENT_1].addGene(leftGene);
	 * xsomeStrands[PARENT_2].addGene(rightGene); } public void addGenePair(C_GenePair genePair) {
	 * xsomeStrands[PARENT_1].addGene(genePair.getGene(PARENT_1)); xsomeStrands[PARENT_2].addGene(genePair.getGene(PARENT_2)); }
	 */
	/** Add a single gene pair to the chromosome pair, making each gene pair homozygous for the allele provided */
	private void addMappedGene(int locus, Object allele, double mappedGeneMapLoc) {
		setGenePairAtLocus(locus, new C_Gene(allele, mappedGeneMapLoc, mappedGeneMutator, this.xsomeStrands[PARENT_1].myId,
				mappedGeneConstraint), new C_Gene(allele, mappedGeneMapLoc, mappedGeneMutator, this.xsomeStrands[PARENT_2].myId,
				mappedGeneConstraint));
	}
	/** Add a single gene pair to the chromosome pair, alleles having values from a normal distribution as specified by the given
	 * mean and stdDev. */
	private void addRandomNormalDistributionGene(int locus, double alleleMean, double alleleStdDev, double mapLoc) {
		// Add genes in pairs ("left" and "right" alleles)
		double alleleL;
		double alleleR;
		{
			// Generate values for genes using a normal (gaussian) distribution of mean, alleleMean, and standard deviation,
			// alleleStandDev. Convert any tiny values into the mini allele value, after taking the absolute value of the random #
			alleleL = Math.abs(C_ContextCreator.randomGaussianGenerator.nextGaussian(alleleMean, alleleStdDev));
			alleleL = Math.max(DEFAULT_ALLELE_MIN, alleleL);
			alleleR = Math.abs(C_ContextCreator.randomGaussianGenerator.nextGaussian(alleleMean, alleleStdDev));
			alleleR = Math.max(DEFAULT_ALLELE_MIN, alleleR);

			// Add gene pairs to genome. Constrain future mutations to the specified min/max values.
			setGenePairAtLocus(locus, new C_Gene(new Double(alleleL), mapLoc, DEFAULT_GENE_MUTATOR,
					this.xsomeStrands[PARENT_1].myId, DEFAULT_GENE_CONSTRAINT), new C_Gene(new Double(alleleR), mapLoc,
					DEFAULT_GENE_MUTATOR, this.xsomeStrands[PARENT_2].myId, DEFAULT_GENE_CONSTRAINT));
		}
	}
	/** Add a gene pair at a particular locus on this chromosome pair. If a gene pair is already on this locus it will be replace by
	 * the new one. */
	public void setGenePairAtLocus(int locus, C_Gene leftGene, C_Gene rightGene) {
		xsomeStrands[PARENT_1].setGeneAtLocus(locus, leftGene);
		xsomeStrands[PARENT_2].setGeneAtLocus(locus, rightGene);
	}
	/** Add a gene pair at a particular locus on this chromosome pair. If a gene pair is already on this locus it will be replace by
	 * the new one. */
	public void setGenePairAtLocus(int locus, C_GenePair genePair) {
		xsomeStrands[PARENT_1].setGeneAtLocus(locus, genePair.getGene(PARENT_1));
		xsomeStrands[PARENT_2].setGeneAtLocus(locus, genePair.getGene(PARENT_2));
	}
	/** How many loci (gene pair) are on this chromosome pair. */
	public int getNumLoci() {
		// Assume that size of LEFT and RIGHT are same...
		return xsomeStrands[PARENT_1].getNumGenes();
	}
	/** All genes are located by indices called "loci".
	 * @param locus the index of the Gene that is to be retrieved. */
	public C_Gene getGene(int locus, int strand) {
		return (C_Gene) xsomeStrands[strand].getGene(locus);
	}

	/** All genes are located by indices called "loci".
	 * @param locus the index of the Gene Pair that is to be retrieved. */
	public C_GenePair getGenePair(int locus) {
		C_Gene gL = (C_Gene) xsomeStrands[PARENT_1].getGene(locus);
		C_Gene gR = (C_Gene) xsomeStrands[PARENT_2].getGene(locus);
		return new C_GenePair(gL, gR);
	}
	/** @param strand Has to be 0 or 1
	 * @return one chromosome among the two chromosomes of the chromosome Pair */
	public C_Chromosome getXsomeStrand(int strand) {
		return xsomeStrands[strand];
	}
	/** Give back an iterator that produces the genes in locus order, 0..n-1 */
	public Iterator<C_Gene> getGeneIterator(int strand) {
		return xsomeStrands[strand].getGeneIterator();
	}
	/** In this case, a special valuation of the GenePair at this locus must be performed. KLUDGE: For now, just returning the
	 * GenePair here. Later - either associate GenePairEvaluator with each gene pair (during construction) or pass back an
	 * AllelePair.
	 * @param locus the location of the allele in question
	 * @return the allele found at specified locus in this Genetic object */
	public Object getLocusAllele(int locus) {
		C_Gene gL = (C_Gene) xsomeStrands[PARENT_1].getGene(locus);
		C_Gene gR = (C_Gene) xsomeStrands[PARENT_2].getGene(locus);
		// System.out.println("gene pair:" + gL + " " + gR);

		// Evaluate alleles stored at gL and gR, return a value representing their collective value (avg, sum, whatever).
		// return genePairEvaluator.express(gL, gR);
		return new C_GenePair(gL, gR);// A KLUDGE...
		// throw new RuntimeException("not implemented yet - use a GenePairEvaluator");
	}
	/** @return the GenePair that's stored at the given map location ("empty" if nothing is there) */
	public Object getLocusAllele(double mapLoc) {
		for (int locus = 0; locus < getNumLoci(); locus++) {
			double curMapLoc = getGeneMapLoc(locus);
			if (mapLoc == curMapLoc) return getLocusAllele(locus);
		}
		return "empty";
		// throw new RuntimeException("can't find any map location " + mapLoc);
	}

	/** @return a List of all alleles in this genome, in order. */
	public List<Object> getAlleles() {
		int numGenePairs = numGenes();
		List<Object> alleles = new LinkedList<Object>();
		for (int locus = 0; locus < numGenePairs; locus++) {
			C_Gene gL = (C_Gene) xsomeStrands[PARENT_1].getGene(locus);
			C_Gene gR = (C_Gene) xsomeStrands[PARENT_2].getGene(locus);
			alleles.add(gL.getAllele());
			alleles.add(gR.getAllele());
			// System.out.println("test allele "+alleles.get(locus));
		}
		return alleles;
	}

	/** @return the length of this genome in map units */
	public double getMapLength() {
		return mapLength;
	}
	/** @param locus the index into the genome where the gene is
	 * @return the map location of a particular gene in map units */
	public double getGeneMapLoc(int locus) {
		C_GenePair genePair = getGenePair(locus);
		return genePair.getMapLoc();
	}
	//
	// MAIN
	//
	public static void main(String[] args) {
		System.out.println("Testing C_Chromosome");
		int numGenes = 4;
		double mapLength = 15.5;
		I_Recombinator recombinator = new C_RecombinatorMapGenome();
		C_ChromosomePair bivalent_1 = new C_ChromosomePair(mapLength, numGenes, recombinator);

		I_GeneMutator gMut = new C_GeneMutatorDouble();
		for (int i = 0; i < numGenes; i++) {
			double mapLoc = i * 4.3;
			// xsome1.setGenePairAtLocus(i, new C_Gene(new Double(new Random().nextDouble() * 2), mapLoc, gMut,
			bivalent_1.setGenePairAtLocus(i, new C_Gene("stringAllele", mapLoc, gMut, bivalent_1.xsomeStrands[PARENT_1].myId),
					new C_Gene(new Double(new Random().nextDouble() * 2), mapLoc, gMut, bivalent_1.xsomeStrands[PARENT_2].myId));
		}
		System.out.println("bivalent_1: " + bivalent_1);
		testMapLocs(bivalent_1);
		System.out.println("numGenes(): " + bivalent_1.numGenes());
		for (int i = 0; i < bivalent_1.numGenes(); i++) {
			System.out.println("getGenePair(" + i + "): " + bivalent_1.getGenePair(i));
			System.out.println("  getGene(" + i + ", LEFT): " + bivalent_1.getGene(i, PARENT_1));
			System.out.println("  getGene(" + i + ", RIGHT): " + bivalent_1.getGene(i, PARENT_2));
		}
		/*
		 * // Add back in when getLocusAllele() and getAlleles() have been // fully implemented. for (int i = 0; i <
		 * genome1.numGenes(); i++) { System.out.println("getGene(" + i + "): " + genome1.getGene(i));
		 * System.out.println("getLocusAllele(" + i + "): " + genome1.getLocusAllele(i)); } System.out.println("getAlleles(): " +
		 * genome1.getAlleles());
		 */
		System.out.println("\nmaking bivalent_2");
		C_ChromosomePair bivalent_2 = new C_ChromosomePair(mapLength, numGenes, recombinator);
		C_Gene g1, g2;
		for (int i = 0; i < numGenes; i++) {
			double mapLoc = i * 4.3;
			g1 = new C_Gene(new Double(i), mapLoc, gMut, bivalent_2.xsomeStrands[PARENT_1].myId);
			g2 = new C_Gene(new Double(i * 2 + 1), mapLoc, gMut, bivalent_2.xsomeStrands[PARENT_2].myId);
			bivalent_2.setGenePairAtLocus(i, new C_GenePair(g1, g2));
		}
		System.out.println("bivalent_2 original: " + bivalent_2);
		testMapLocs(bivalent_2);

		C_ChromosomePair genome2Copy = (C_ChromosomePair) bivalent_2.replicate(new Double(0));
		System.out.println("xsome2's copy:   " + genome2Copy);
		testMapLocs(genome2Copy);
		System.out.println("2's numGenes(): " + bivalent_2.numGenes());
		bivalent_2.mutate(new Double(0.75));
		System.out.println("xsome2 mutated: " + bivalent_2);
		testMapLocs(bivalent_2);
		System.out.println("genome2's copy:  " + genome2Copy);
		testMapLocs(genome2Copy);
		System.out.println("xsome2 left strand: " + bivalent_2.getXsomeStrand(PARENT_1));
		System.out.println("xsome2 right strand: " + bivalent_2.getXsomeStrand(PARENT_2));

		System.out.println("\nCrossover and Mating");
		System.out.println("parent1: " + bivalent_1);
		System.out.println("parent2: " + bivalent_2);
		System.out.println("crossover(): " + bivalent_1.crossover(bivalent_2));
		System.out.println("mate(): " + bivalent_1.mate(new Double(0.75), bivalent_2));

		// Test copySegment()
		I_MappedDna copiedSegment1 = (I_MappedDna) bivalent_2.copySegment(0, 1);
		I_MappedDna copiedSegment2 = (I_MappedDna) bivalent_2.copySegment(1, 3);
		I_MappedDna copiedSegment3 = (I_MappedDna) bivalent_2.copySegment(3, 4);
		System.out.println("\ncopySegment()");
		System.out.println("xsome2: " + bivalent_2);
		System.out.println("xsome2.copySegment(0,1):" + copiedSegment1);
		testMapLocs(copiedSegment1);
		System.out.println("xsome2.copySegment(1,3):" + copiedSegment2);
		testMapLocs(copiedSegment2);
		System.out.println("xsome2.copySegment(3,4):" + copiedSegment3);
		testMapLocs(copiedSegment3);

	}
	private static void testMapLocs(I_MappedDna genome) {
		double mapLoc = 4.3;
		System.out.println("xsome.getMapLength():         " + genome.getMapLength());
		for (int i = 0; i < genome.getNumLoci(); i++) {
			C_GenePair genePair = ((C_ChromosomePair) genome).getGenePair(i);
			System.out.print("  getGenePair(" + i + "): " + genePair);
			System.out.println("  mapLoc: " + genePair.getMapLoc());
			System.out.println("  mapLoc(" + mapLoc + ") allele: " + genome.getLocusAllele(mapLoc));
		}
	}
}
