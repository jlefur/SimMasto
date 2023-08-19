/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing.ground;

import java.util.TreeSet;

import data.C_Parameters;
import simmasto0.protocol.A_Protocol;
import simmasto0.util.C_VariousUtilities;
import thing.A_Animal;
import thing.A_VisibleAgent;
import thing.C_Rodent;
import thing.I_SituatedThing;

/** Any container able to contain I_situated thing. Provide the mean to manage any I_container thing; that is containers of
 * containers. Basic fields and commands only.
 * @author J. Le Fur, nov.2014, mar.2015, sep.2020 */
public abstract class A_Container extends A_VisibleAgent implements I_Container {
	//
	// FIELDS
	//
	/** Coordinate i or x in the grid or soilCellMatrix */
	protected int lineNo = -1;
	/** Coordinate j or y in the grid or soilCellMatrix */
	protected int colNo = -1;
	/** The agents that have entered the container */
	protected TreeSet<I_SituatedThing> occupantList = new TreeSet<I_SituatedThing>();
	/** The more the affinity is high, the more the dynamics agents are attracted affinity is also used as a proxy for carrying
	 * capacity (the less affinity, the less K) */
	protected int affinity = 0;
	protected int rodentLoad = 0;

	//
	// METHODS
	//
	/** Transmit infection to thing (a container may transmit its infection to its content)<br>
	 * Has to be overridden in daughter classes MS 2020 */
	public void actionInfect(I_SituatedThing thing) {
		this.energy_Ukcal--;
	}
	public void getInfection(I_SituatedThing pathogen) {}

	/** Remove agents from occupants list */
	@Override
	public void discardThis() {
		if (this.getOccupantList() == null) {
			A_Protocol.event("A_Container.discardThis()", this + " (" + C_VariousUtilities.getShortClassName(this
					.getClass()).substring(2) + ")", isError);
		}
		else {
			Object[] occupants = this.getOccupantList().toArray();
			for (Object occupant : occupants) {
				if (occupant instanceof I_SituatedThing) {
					this.agentLeaving((I_SituatedThing) occupant);
					if (C_Parameters.VERBOSE)
						A_Protocol.event("A_Container.discardThis", occupant + " was discarded from " + this + " (dead "
								+ this.isDead() + ")", isNotError);
				}
			}
			occupants = null;
			this.occupantList = null;
			super.discardThis();
		}
	}
	/** A thing comes inside the area of this container agent, we register it in the occupant list <br>
	 * NB: use it each time you manually manipulate a thing on space. Version author: Q.Baduel 2008, rev JLF 11.2014, 02.2020
	 * @param thing : the dynamic agent
	 * @return incoming success */
	public boolean agentIncoming(I_SituatedThing thing) {
		thing.assertColNo(this.colNo);
		thing.assertLineNo(this.lineNo);
		boolean entryOK = false;
		if (this.occupantList.add(thing)) entryOK = true;
		else {
			A_Protocol.event("A_Container.agentIncoming", "Cannot add " + thing + " to " + this, isError);
			return false;
		}
		if (entryOK) {
			thing.setCurrentSoilCell(this);
			if (thing instanceof C_Rodent) this.rodentLoad++;
		}
		else A_Protocol.event("A_Container.agentIncoming", "Cannot set " + this + " container to " + thing, isError);
		return entryOK;
	}
	/** One agent from the list is leaving the area of this container agent. Version author Q.Baduel 2008, rev. JLF
	 * 12.2015,01.2016 */
	public boolean agentLeaving(I_SituatedThing thing) {
		if (thing instanceof A_Animal) ((A_Animal) thing).setLastContainerLeft(this);
		boolean exitOK = this.occupantList.remove(thing);
		if (exitOK) {
			thing.setCurrentSoilCell(null);
			if (thing instanceof C_Rodent) this.rodentLoad--;
		}
		else {
			A_Protocol.event("A_Container.agentLeaving", "Could not find " + thing + " in occupantList of " + this,
					isError);
		}
		return exitOK;
	}
	//
	// SETTERS AND GETTERS
	//
	/** Recursively set the value passed to self and contained containers affinity / Author Le Fur 03.2015 */
	public void setAffinity(int a) {
		affinity = a;// TODO JLF 2015.08 probably not generic and functional; has to be checked everywhere for cleaning
		for (I_Container oneContainer : this.getContainerList()) {
			oneContainer.setAffinity(a);
			// 2016.12 JLF affinity to show full cells on GUI
			// if ((oneContainer.getAffinity() > DANGEROUS_AREA_AFFINITY) && oneContainer.isFull())
			// oneContainer.setAffinity(DANGEROUS_AREA_AFFINITY +
			// 1);
		}
	}
	public void assertLineNo(int lineNo) {
		this.lineNo = lineNo;
	}
	public void assertColNo(int colNo) {
		this.colNo = colNo;
	}
	@Override
	public boolean isInfected() {
		return false;
	}
	public boolean isFull() {
		return this.getCarryingCapacity_Urodent() < this.getLoad_Urodent();
	}
	/** @return the i or x coord in the grid (ex-SoilCell) matrix */
	public int retrieveLineNo() {
		return this.lineNo;
	}
	/** @return the j or y coord in the grid (ex-SoilCell) matrix */
	public int retrieveColNo() {
		return this.colNo;
	}
	public int getAffinity() {
		return this.affinity;
	}
	/** @return agent_list, the list not including agents within lower level containers */
	public TreeSet<I_SituatedThing> getOccupantList() {
		return this.occupantList;
	}
	/** Recursively return a treeset of all agents including those in the contained containers/ Author Le Fur 03.2012, rev.
	 * 11.2014
	 * @return agents: a treeset of agents in the container */
	public TreeSet<I_SituatedThing> getFullOccupantList() {
		TreeSet<I_SituatedThing> agents = new TreeSet<I_SituatedThing>();
		TreeSet<I_Container> containers = new TreeSet<I_Container>();
		if (this.isDead() // to avoid probe crash (JLF 03.2021)
				|| this.occupantList.isEmpty()) return agents;// stop condition
		else {
			containers = this.getContainerList();
			agents.addAll(this.occupantList);
			for (I_Container mineContainer : containers)
				// A priori, no infinite loops in case of mutual referencing
				agents.addAll(mineContainer.getFullOccupantList());
			return agents;
		}
	}
	/** @return containerList: the list of I_containers in this container; do not recurse */
	public TreeSet<I_Container> getContainerList() {
		TreeSet<I_Container> containerList = new TreeSet<I_Container>();
		for (I_SituatedThing thing : this.occupantList) if (thing instanceof I_Container)
			containerList.add((I_Container) thing);
		return containerList;
	}
	/** @return rodent_list: the list of rodents in this container; do not recurse */
	public TreeSet<C_Rodent> getRodentList() {
		TreeSet<C_Rodent> rodents = new TreeSet<C_Rodent>();
		for (I_SituatedThing thing : this.occupantList) if (thing instanceof C_Rodent) rodents.add((C_Rodent) thing);
		return rodents;
	}
	/** Recursively return a treeset of all rodents including those in the contained containers/ Author Le Fur 03.2012, rev.
	 * 11.2014
	 * @return rodents: a treeset of rodents in the container */
	public TreeSet<C_Rodent> getFullRodentList() {
		TreeSet<C_Rodent> rodents = new TreeSet<C_Rodent>();
		for (I_SituatedThing agent : getFullOccupantList()) if (agent instanceof C_Rodent) {
			rodents.add((C_Rodent) agent);
		}
		return rodents;
	}
	/** Author Le Fur 03.2015
	 * @return the number of rodents in the container and into its parts
	 * @see getFullRodentList */
	public int getFullLoad_Urodent() {
		return getFullRodentList().size();
	}
	/** Author Le Fur 03.2015
	 * @return the number of rodents in the container only
	 * @see getRodentList */
	public int getLoad_Urodent() {
		return rodentLoad;
	}
	/** @return the number of agents in the container and into its parts
	 * @see getFullAgentList */
	public int getFullLoad_Uagent() {
		return getFullOccupantList().size();
	}
}
