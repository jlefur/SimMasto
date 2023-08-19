package thing.dna;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import simmasto0.C_ContextCreator;
import thing.dna.variator.C_GeneMutatorDouble;
import thing.dna.variator.I_Recombinator;
/** A single "strand" of genes, capable of being mutated and crossed over with another single strand (another chromosome)<br>
 * NOTE: map locations are currently not checked for sequential ordering, so each new Gene that is added must be at a higher map location than all of
 * the previously added Genes.<br>
 * @author kyle wagner, elyk@acm.org
 * Version 1.1, aug 28, 2002, jan 2012 J. Le Fur */
public class C_Chromosome implements I_MappedDna, Comparable<C_Chromosome> {
	//
	// CONSTANT
	//
	public static final double MAP_LENGTH = 100.0; // TODO number in source OK xsome map length
	//
	// FIELDS
	//
	protected Integer myId;
	protected List<C_Gene> genes;
	protected I_Recombinator recombinator;
	protected double mapLength;
	private boolean mergedXsome = false; // when an homologous chromosome is composed of several smaller chromosomes, they are merged into a single
											// equivalent homologous chromosome and tagged as a mergedXsome. 03.2012
	//
	// CONSTRUCTORS
	//
	/** Make a chromosome of a specific size (it's more efficient if this size is known since an ArrayList is holding the Genes).
	 * @param numGenes how many genes will be in the final chromosome (only for efficiency - if count is incorrect, chromosome will still function
	 *            properly, it will simply take a longer time to construct).
	 * @param recombinator provides a crossover method Author Kyle Wagner 2002 */
	public C_Chromosome(int numGenes, I_Recombinator recombinator) {
		this.recombinator = recombinator;
		this.mapLength = MAP_LENGTH;
		this.myId = C_ContextCreator.XSOME_NUMBER;
		C_ContextCreator.XSOME_NUMBER++;
		genes = new ArrayList<C_Gene>(numGenes);
		for (int i = 0; i < numGenes; i++)
			// Fill with dummy values (all 0s)
			genes.add(new C_Gene(new Integer(0), new C_GeneMutatorDouble(), this.myId));
	}
	/** Make a chromosome of a specific size (it's more efficient if this size is known since an ArrayList is holding the Genes).
	 * @param mapLength the length of this chromosome in map units
	 * @param numGenes how many genes will be in the final chromosome (only for efficiency - if count is incorrect, chromosome will still function
	 *            properly, it will simply take a longer time to construct).
	 * @param recombinator provides a crossover method */
	public C_Chromosome(double mapLength, int numGenes, I_Recombinator recombinator) {
		this.myId = C_ContextCreator.XSOME_NUMBER;
		C_ContextCreator.XSOME_NUMBER++;
		this.recombinator = recombinator;
		this.mapLength = mapLength;
		genes = new ArrayList<C_Gene>(numGenes);
		// Fill with dummy values (all 0s)
		for (int i = 0; i < numGenes; i++)
			genes.add(new C_Gene(new Integer(0), new C_GeneMutatorDouble(), this.myId));
	}
	//
	// METHODS
	//
	/** Potentially modify all of the Genes (or components) of this Genetic object. Variation may be constrained by a rate (a mutation rate), or other
	 * information provided in the argument, info.
	 * @param info An optional argument (can be null). Often this will be a Double representing a mutation rate. */
	public void mutate(Object info) {
		Iterator<C_Gene> genesIter = genes.iterator();
		C_Gene gene;
		while (genesIter.hasNext()) {
			gene = (C_Gene) genesIter.next();
			gene.mutate(info);
		}
	}
	/** Creates a new chromosome which is copied from startLocus (inclusive) to endLocus (exclusive) in chromosome.
	 * @param startLocus the locus where the duplication of chromosome will begin
	 * @param endLocus the locus where duplication ends (exclusive) */
	public I_DnaThing copySegment(int startLocus, int endLocus) {
		// Make a new chromosome that's big enough to hold the copied genes.
		int numGenesToCopy = endLocus - startLocus;
		C_Chromosome copyChrmosome = new C_Chromosome(mapLength, numGenesToCopy, this.recombinator);
		// Do the copy from my alleles in the specified range to copychromosome.
		for (int locus = startLocus; locus < endLocus; locus++) {
			C_Gene gene = (C_Gene) genes.get(locus);
			copyChrmosome.setGeneAtLocus(locus, gene);
		}
		int lastSegLocus = endLocus - 1;
		int finalLocus = (getNumLoci() - 1);
		// Estimate the map length of this segment ... this isn't terribly accurate. Just finds out the map locations of the loci right around the
		// segment and splits the difference when creating the new segment.
		double copyMapBegin = this.getGene(startLocus).getMapLoc();
		if (startLocus > 0) {
			double copyMapBefore = this.getGene(startLocus - 1).getMapLoc();
			double copyMapAfter = copyMapBegin;
			copyMapBegin = (copyMapBefore + copyMapAfter) / 2;
		}
		else if (startLocus == 0) copyMapBegin = 0;

		double copyMapEnd = this.getGene(lastSegLocus).getMapLoc();
		if (lastSegLocus < finalLocus) {
			double copyMapBefore = copyMapEnd;
			double copyMapAfter = this.getGene(lastSegLocus + 1).getMapLoc();
			copyMapEnd = (copyMapBefore + copyMapAfter) / 2;
		}
		else if (lastSegLocus == finalLocus) copyMapEnd = mapLength;
		double copyMapLength = copyMapEnd - copyMapBegin;
		copyChrmosome.mapLength = copyMapLength;
		return copyChrmosome;
	}
	/** The replicated individual does not have to be asexual, but is most likely to have this property.
	 * @param info Possibly a mutation rate, or other information needed in order to perform the replication.
	 * @return a new Genetic object, a duplicate of this one. Mutations might have occured, however. */
	public C_Chromosome replicate(Object info) {
		C_Chromosome copyGenes = new C_Chromosome(mapLength, genes.size(), recombinator);
		copyGenes.setMergedXsome(mergedXsome);
		C_Gene gene;
		C_Gene geneClone;
		for (int i = 0; i < genes.size(); i++) {
			gene = getGene(i);
			geneClone = gene.copy();
			// geneClone.ownerXsomeNumber = copyGenes.myId; false since it does not account for
			// inbreeding
			copyGenes.setGeneAtLocus(i, geneClone);
		}
		/*
		 * JLF 02.2012 WAS FORMERLY (to account for addGene which is currently deprecated) : Iterator<C_Gene> genesIter = genes.iterator(); while
		 * (genesIter.hasNext()) { gene = (C_Gene) genesIter.next(); // copy gene ... add to copyGenes geneClone = gene.copy();
		 * copyGenes.addGene(geneClone);
		 */
		copyGenes.mutate(info);
		copyGenes.recombinator = this.recombinator;
		return copyGenes;
	}

	/** The replicated individual does not have to be bisexual, but is most likely to have this property. N.B. JLF: could be used for procaryotes ?
	 * @param info Possibly a mutation rate, or other information needed in order to perform the mating.
	 * @param parent2 the other individual involved in this mating.
	 * @return a new Genetic object, a combination of the two parents.. Mutations might have occured, however. Author Kyle Wagner */
	public I_DnaThing mate(Object info, I_DnaThing parent2) {
		C_Chromosome offspring = (C_Chromosome) this.crossover(parent2);
		offspring.mutate(info);
		offspring.recombinator = this.recombinator;
		offspring.mapLength = this.mapLength;
		return offspring;
		// throw new RuntimeException("not implemented yet");
	}
	public void randomize() {
		// Mutation rate is 1 for this operation - we want all genes to
		// be changed.
		Double mutRate = new Double(1.0);
		Iterator<C_Gene> genesIter = genes.iterator();
		C_Gene gene;
		while (genesIter.hasNext()) {
			gene = (C_Gene) genesIter.next();
			gene.mutate(mutRate);
		}
	}
	@Override
	public I_DnaThing crossover(I_DnaThing parent2) {
		return null;
	}
	public String toString() {
		return genes.toString();
	}
	public int compareTo(C_Chromosome other) { // to sort in descending orders from the biggest to the smallest.
		int nb1 = other.getNumGenes();
		int nb2 = this.getNumGenes();
		if (nb1 > nb2) return 1;
		else if (nb1 == nb2) return 0;
		else return -1;
	}
	//
	// SETTERS & GETTERS
	//
	/** @return the length of this chromosome in map units */
	public double getMapLength() {
		return mapLength;
	}
	/** @param locus the index into the chromosome where the gene is
	 * @return the map location of a particular gene in map units */
	public double getGeneMapLoc(int locus) {
		C_Gene gene = getGene(locus);
		return gene.getMapLoc();
	}
	/** Change the recombinator that this chromosome uses.
	 * @param recombinator provides a crossover method */
	public void setRecombinator(I_Recombinator recombinator) {
		this.recombinator = recombinator;
	}
	public int getNumGenes() {
		return genes.size();
	}
	/** How many loci are in this Genetic object - how many genes (for haploids) or gene pairs (for diploids). */
	public int getNumLoci() {
		return genes.size();
	}
	/** Add a gene on this chromosome. */
	public void addAllGenes(List<C_Gene> genes2) {
		genes.addAll(genes2);
	}
	/** Add a gene on this chromosome. */
	public void addGene(C_Gene g) {
		genes.add(g);
	}
	/** Add a gene at a particular locus on this chromosome. If a gene is already on this locus it will be replace by the new one.
	 * @param locus the index into the chromosome where the gene is */
	public void setGeneAtLocus(int locus, C_Gene g) {
		genes.set(locus, g);
	}
	/** All genes are located by indices called "loci".
	 * @param locus the index of the Gene that is to be retrieved. */
	public C_Gene getGene(int locus) {
		return (C_Gene) genes.get(locus);
	}
	/** Give back an iterator that produces the genes in locus order, 0..n-1 */
	public Iterator<C_Gene> getGeneIterator() {
		return genes.iterator();
	}
	/** @param locus the location of the allele in question
	 * @return the allele found at specified locus in this Genetic object */
	public Object getLocusAllele(int locus) {
		C_Gene gene = (C_Gene) genes.get(locus);
		return gene.getAllele();
		// throw new RuntimeException("not implemented yet");
	}
	/** @return the Gene that's stored at the given map location ("empty" if nothing is there) */
	public Object getLocusAllele(double mapLoc) {
		for (int locus = 0; locus < getNumLoci(); locus++) {
			double curMapLoc = getGeneMapLoc(locus);
			if (mapLoc == curMapLoc) return getLocusAllele(locus);
		}
		return "empty";
		// throw new RuntimeException("can't find any map location " + mapLoc);
	}
	/** @return a List of all alleles in this chromosome, in order. */
	public List<Object> getAlleles() {
		List<Object> alleles = new LinkedList<Object>();
		Iterator<C_Gene> geneIter = genes.iterator();
		C_Gene gene;
		while (geneIter.hasNext()) {
			gene = (C_Gene) geneIter.next();
			alleles.add(gene.getAllele());
		}
		return alleles;
	}
	/** @return a List of all genes in this chromosome. */
	public List<C_Gene> getGenes() {
		return genes;
	}
	/** @return the recombinator of this chromosome. */
	public I_Recombinator getRecombinator() {
		return recombinator;
	}
	public Object getGeneWithMaploc(double mapLoc) {
		for (C_Gene gene : genes) {
			if (gene.mapLoc == mapLoc) return gene;
		}
		return "empty";
	}
	public void setMergedXsome(boolean mergedXsome) {
		this.mergedXsome = mergedXsome;
	}
	public boolean isMergedXsome() {
		return mergedXsome;
	}
	public Integer getMyId() {
		return myId;
	}
}
