package thing;

/** Any thing that takes input and produces actions is an Agent. Agents usually map input to output, but they may just produce
 * outputs and ignore their inputs.
 * @author kyle wagner, elyk@acm.org / rev. Quentin Baduel, 2009 / Jean Le Fur, 2009, 2014, 2015
 * Version 1.0, Mon Nov 20 14:25:04 2000 */

public interface I_CyberneticThing {

	/** @param in might be a List of ints/floats for a nn or fsm, but it could also be a symbolic structure, a single int, or a
	 *            single token */
	// public void receiveInput(Input in);

	/** The Agent produces some action(s).
	 * @return the action or actions produced */
	// public Object act();
	// public boolean isActive();
	// public void setActive(boolean active);

	// public TreeSet<I_situated_thing> perception();// TODO JLF 2014.10 has to be protected within the NDS hierarchy
}
