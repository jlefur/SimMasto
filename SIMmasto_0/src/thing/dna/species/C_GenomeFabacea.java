package thing.dna.species;

/** @author JLF and MS 04.2016 */
public class C_GenomeFabacea extends C_GenomeSpermatophyta {
	//
	// CONSTRUCTOR
	//
	/** Returns a new C_GenomeFabacea (e.g., groundnut) - genome for sahelian crops <br>
	 *   Trait values references (see comments in source code):<br>
	 *    - xxx (2014)  */
	public C_GenomeFabacea() {
		super();
		alleles.put(GROWTH_RATE_UkcalPerDay, 5.001); // TODO JLF&MS 04.2016 source: junk value  see T.Brevault for better values
		makeSpermatophytaBivalent(this.alleles);
	}
}
