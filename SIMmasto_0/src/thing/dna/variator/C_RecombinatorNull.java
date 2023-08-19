package thing.dna.variator;

import thing.dna.C_Chromosome;
import thing.dna.C_ChromosomePair;

/**
 * Return the same chromosome pair than the parent. The two homologous chromosomes aren't crossed.
 * @author J.LeFur 01/2012
 * 
 */
public class C_RecombinatorNull implements I_Recombinator {
	public C_ChromosomePair crossover(C_Chromosome g1, C_Chromosome g2) {
		return new C_ChromosomePair(g1, g2) ;
	}
}