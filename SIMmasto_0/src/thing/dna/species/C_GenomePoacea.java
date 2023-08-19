package thing.dna.species;

/** @author JLF and MS 04.2016 */
public class C_GenomePoacea extends C_GenomeSpermatophyta {
	//
	// CONSTRUCTOR
	//
	/** Returns a new C_GenomePoacea - genome for wild grass <br>
	 *   Trait values references (see comments in source code):<br>
	 *    - xxx (2014)  */
	public C_GenomePoacea() {
		super();
		alleles.put(GROWTH_RATE_UkcalPerDay, 4.001); // TODO JLF&MS 04.2016 source: junk value, see T.Brevault for better values
		makeSpermatophytaBivalent(this.alleles);
	}
}
