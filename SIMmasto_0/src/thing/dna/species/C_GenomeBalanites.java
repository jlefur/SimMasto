package thing.dna.species;

/** @author JLF and MS 04.2016 */
public class C_GenomeBalanites extends C_GenomeSpermatophyta {
	//
	// CONSTRUCTOR
	//
	/** Returns a new C_GenomeBalanites (Balanites aegyptiaca)- genome for shrub <br>
	 * Trait values references (see comments in source code):<br>
	 * - Ndiaye O., Diallo A., Sagna M.B. et Guissé A. (2013) Diversité floristique des peuplements ligneux du Ferlo, Sénégal. Vertigo, 13(3), DOI :
	 * 10.4000/vertigo.14352 */
	public C_GenomeBalanites() {
		super();
		alleles.put(GROWTH_RATE_UkcalPerDay, 3.001); // TODO JLF&MS 04.2016 source: junk value, see T.Brevault for better values
		makeSpermatophytaBivalent(this.alleles);
	}
}
