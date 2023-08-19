/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package simmasto0.protocol;


/** Protocols define the specificity of the simulation<br>
 * There have to be only one protocol in the simulation <br>
 * Protocols define the agents currently simulated, the inspectors used, the parameters value<br>
 * Protocols trigger most of the action within the simulation, they are constructed and initialized (initProtocol) by the context
 * creator and triggered by the step_Utick procedure, the unique @ScheduledMethod of the simulator (+ one used for display)<br>
 * Most protocols are bound to the A_Protocol abstract class which gather generic function of the simulation<br>
 * Each protocol is associated with a corresponding parameters set defined as a corresponding xml/txt file in the .rs directory
 * @author J.Le Fur 2012, rev. 02.2013, 07.2014, 08.2014 */

public interface I_Protocol {

	/** Triggered by the context creator at the beginning of the simulation <br>
	 * Specifically Initialize populations and inspectors */
	public void initProtocol();
	/** check the sizes of the rodents'list, proceed to agents' step, updates indicators Author J.Le Fur 2012 */
	public void step_Utick();
	/** reads modifiable parameters value on the GUI and fixed parameters value with initFixedParameters */
	public void readUserParameters();
	/** set the actual start date of the protocol */
	public void initCalendar();
	/** It is important that "each protocol" defines it's condition of end simulation. This abstract requires them to do so*/
	public boolean isSimulationEnd();

}
