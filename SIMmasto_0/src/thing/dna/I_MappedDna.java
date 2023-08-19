package thing.dna;

/**
 * All Genetic objects have a length in the number of loci they've got, but they also can have a
 * "virtual length", as if Genes are located at various points along the genome. For example, a HaploidGenome
 * might have 3 genes, whose loci are 1, 2, 3. However, locus1 may be at 1.4, locus2 at 1.43 and locus3 at
 * 35.1. This is used to help model linkage. Loci 1 and 2 are closely linked, while 3 is far from either.
 * 
 * 
 * was formerly interface MapGenome then I_map_Genome, JLF 01-2011
 * @author kyle wagner, elyk@acm.org
 * Version 1.0, Thu Apr 25 13:17:41 2002
 */

public interface I_MappedDna extends I_DnaThing {
	/**
	 * @return the length of this genome in map units
	 */
	public double getMapLength();

	/**
	 * @param locus
	 *            the index into the genome where the gene is
	 * @return the map location of a particular gene in map units
	 */
	public double getGeneMapLoc(int locus);

	/**
	 * @return the Gene or GenePair that's stored at the given map location
	 */
	public Object getLocusAllele(double mapLoc);
}
