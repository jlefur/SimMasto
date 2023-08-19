package thing.dna.phenotyper;

import java.util.ArrayList;
import thing.dna.I_MappedDna;

/** Provides a callback method used by MapLocPhenotyper to express a trait. A simple TraitExpressor might add up all real-valued
 * alleles among the trait's loci, returning the sum. However, many expressors are likely to take dominance and epistasis into
 * account.
 * @author kyle wagner, elyk@acm.org, modified J.Le Fur, 02.2011, 08.2014
 * Version 1.0, aug 28, 2002 */

public interface I_MapLocTraitExpressor {
	/** @param mappedDNA the DNA which will express this trait
	 * @param traitMapLocs the map locations of the loci in the DNA which code for this trait
	 * @return an Object representing the value of the trait (could be a Double or Integer, a String, or maybe a complex, custom
	 *         object). */
	public Object evalTrait(I_MappedDna mappedDNA, ArrayList<Double> traitMapLocs);
}
