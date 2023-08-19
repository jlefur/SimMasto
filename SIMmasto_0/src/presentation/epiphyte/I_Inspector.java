package presentation.epiphyte;

import thing.I_SituatedThing;

/** inspectors are in charge of gathering and providing indicators value<br>
 * each inspector may be specific of a component of the simulation (population, genetics, ...)<br>
 * inspectors are unknown of the thing objects (epiphyte system approach) <br>
 * within a given protocol, all inspectors are managed by the protocol class<br>
 * most inspectors are bound to the A_Inspector abstract class which gather generic inspector function
 * @author J.Le Fur, 2011, rev. 02.2013 */
public interface I_Inspector {
	/** store indicators value */
	public void step_Utick();
	/** for compatibility with step (used in daughter classes) */
	public void indicatorsCompute();
	/** store all values as a string in the corresponding field */
	public String indicatorsStoreValues();
	/** patch under construction : reset indicators values in case restarting the GUI does not fully reset the system. implemented
	 * partially or not in some inspectors; Le Fur, 01.2014 */
	public void indicatorsReset();
	/** close private files */
	public void closeSimulation();
	/**Remove dead things in corresponding inspectors' lists*/
	public void discardDeadThing(I_SituatedThing deadThing);
}
