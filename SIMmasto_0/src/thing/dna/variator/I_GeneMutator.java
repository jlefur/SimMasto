package thing.dna.variator;


/**
 * 
 *
 * @author kyle wagner, elyk@acm.org
 * Version 1.0, Wed Dec 13 20:32:02 2000
 */


public interface I_GeneMutator
{
  /**
   *  Determine a new value for the parameter, value, and return it.
   *
   *  @param info is usually mutation rate, but it could be anything
   *         necessary for proper mutation.
   *  @param value the value that is being mutated.
   */
  public Object mutate(Object info, Object value);
}
