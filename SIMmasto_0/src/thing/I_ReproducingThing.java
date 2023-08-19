package thing;

import thing.dna.I_DiploidGenome;

/** Interface for all agents/things that can mate with one another.
 * @author kyle wagner, elyk@acm.org / Jean Le Fur rev. 04.03.2014 Version 1.0, dec 23, 2000 */

public interface I_ReproducingThing {

	/** @param parent2 an agent/thing capable of mating provides a new agent/thing, created sexually by this agent/thing and parent2 (the offspring
	 *            need not be capable of mating, however it is likely to have this ability, and it will have the Sexual interface).
	 * @return true if success */
	boolean actionMateWithMale(I_ReproducingThing parent2);
	/** @param genome the DNA of the egg generate a new animal compulsory for each C_Rodent daughter class */
	A_Animal giveBirth(I_DiploidGenome genome);

}
