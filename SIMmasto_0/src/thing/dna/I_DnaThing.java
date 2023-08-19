package thing.dna;

import java.util.List;

/**
 * Any object that can be subjected to a GA needs to implement these methods.
 * Genes are not included in this interface since it is conceivable that a GA
 * might operate on non-genomic structures (phenomes, for instance). In
 * addition, gene accessors and setters will be different for haploids
 * (single-strand genomes) than for diploids (double-stranded genomes). Specific
 * implementors of I_dna_thing will undoubtedly provide their own accessors, and
 * they may possibly only allow certain implementors of I_dna_thing to crossover
 * with themselves (e.g., Haploids might handle only other Haploids, or might
 * also handle Diploids for haplodiploid organisms, but they may throw a runtime
 * exception for other potential I_genetic_things such as LISP code and neural
 * nets). JLF 2011: was formerly: interface Genetic
 * 
 * @see C_Chromosome
 * @see C_ChromosomePair
 * 
 * @author kyle wagner, elyk@acm.org
 * Version 1.0, Thu Dec 14 00:07:01 2000
 */

public interface I_DnaThing {
	/**
	 * How many loci are in this Genetic object - how many genes (for haploids)
	 * or gene pairs (for diploids).
	 */
	public int getNumLoci();

	/**
	 * A dna_thing object can replicate itself. The copy may be mutated, or
	 * it may be an exact copy. The replicated individual does not have to be
	 * asexual
	 * 
	 * @param info
	 *            Possibly a mutation rate, or other information needed in order
	 *            to perform the replication.
	 * @return a new Genetic object, a duplicate of this one. Mutations might
	 *         have occurred, however.
	 * 
	 * Authors kyle wagner, elyk@acm.org, was formerly in interface I_asexual -
	 *         JLF 02/2011
	 * Version 1.0, Thu Dec 14 00:07:12 2000
	 */

	public I_DnaThing replicate(Object info);

	/**
	 * Potentially modify all of the Genes (or components) of this Genetic
	 * object. Variation may be constrained by a rate (a mutation rate), or
	 * other information provided in the argument, info.
	 * 
	 * @param info An optional argument (can be null). Often this will be a Double
	 *            representing a mutation rate.
	 */
	public void mutate(Object info);

	/**
	 * Takes this Genetic object and mixes it up with another genetic object,
	 * producing a third Genetic object. The usual case is to take 2 haploid
	 * genomes and create a third haploid consisting of various genes taken from
	 * each of the parents. Neither parent is modified in the process.
	 * 
	 * @param parent2
	 *            Another Genetic object with which to mix components (genes or
	 *            other parts).
	 */
	public I_DnaThing crossover(I_DnaThing parent2);

	/**
	 * Creates a new Genetic object which is copied from startLocus (inclusive)
	 * to endLocus (exclusive) in genome.
	 * 
	 * @param startLocus
	 *            the locus where the duplication of genome will begin
	 * @param endLocus
	 *            the locus where duplication ends (exlcusive)
	 */
	public I_DnaThing copySegment(int startLocus, int endLocus);

	/**
	 * @param locus
	 *            the location of the allele in question
	 * @return the allele found at specified locus in this Genetic object
	 */
	public Object getLocusAllele(int locus);

	/**
	 * @return a List of all alleles in this genome, in order.
	 */
	public List<Object> getAlleles();

	/**
	 * Mutates all genes in this genome.
	 */
	public void randomize();
}
