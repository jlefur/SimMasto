package thing.dna.variator;

import thing.dna.C_Chromosome;
import thing.dna.C_ChromosomePair;



/**
 * Interface for all recombinators of HaploidGenomes.
 * Provides the interface for performing crossover between 2 HaploidGenomes.
 *
 * @author kyle wagner, elyk@cs.indiana.edu
 * Version 1.0, nov 30, 1998
 */


public interface I_Recombinator
{
  /**
   *  Perform some kind of crossover (1-pt, multi-point, probabilistic)
   *  on these two chromosomes.
   *
   *  @param g1 g2 chromosomes to be recombined - parents of a new chromosome pair
   *  @return a NEW chromosome pair which is the recombination of
   *          genes from g1 and g2
   */
  public C_ChromosomePair crossover(C_Chromosome g1, C_Chromosome g2);
}
