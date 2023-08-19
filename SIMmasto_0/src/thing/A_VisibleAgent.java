/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing;

import java.util.TreeSet;

import simmasto0.protocol.A_Protocol;
import thing.ground.I_Container;
import thing.ground.landscape.C_Landscape;

import com.vividsolutions.jts.geom.Coordinate;

/** Extend A_NDS, implements basic procedures from I_SituatedThing,<br>
 * Get sensing ability and perception, i.e., sensing for animals, area of influence for things
 * @author jlefur 2010, rev 12.2013, 05.2018 */
public abstract class A_VisibleAgent extends A_NDS implements I_SituatedThing {
	//
	// FIELDS
	//
	public boolean hasToSwitchFace = false;
	public static C_Landscape myLandscape = null;
	protected I_Container currentSoilCell;
	public boolean hasLeftDomain = false; // if the agent reaches an edge, it leaves the simulation
	public boolean hasEnteredDomain = false; // a new agent comes at an edge from outside the exclos
	public Coordinate bornCoord_Umeter;
	protected double sensing_UmeterByTick;
	protected TreeSet<A_Animal> animalsTargetingMe = new TreeSet<A_Animal>();
	//
	// CONSTRUCTOR
	//
	public A_VisibleAgent() {
		super();
		this.hasLeftDomain = false;
		this.hasEnteredDomain = false;
	}
	//
	// METHODS
	//
	/** Test if oneOccupant is closer than sensing. Can be specified in daughter classes / JLF 03.2021 */
	public boolean canInteractWith(I_SituatedThing oneOccupant) {
		if (oneOccupant.getCoordinate_Umeter().distance(this.getCoordinate_Umeter()) <= this.sensing_UmeterByTick) return true;
		return false;
	}
	/** @param m : the ground manager which will look after the Agent action (if the field is concerned) */
	public static void init(C_Landscape m) {
		myLandscape = m;// first, we give the agent a reference to the ground_manager it can use
	}
	/** Remove references to pathwanderer and leave all containers, @rev jlf 2017.12, 2021.02 */
	@Override
	public void discardThis() {
		for (A_Animal oneAnimal : this.animalsTargetingMe)
			oneAnimal.target = null;// cannot use discardTarget due to concurrent modification exception / jlf 12.2017
		this.animalsTargetingMe = null;
		while (this.getCurrentSoilCell() != null) {
			if (!this.getCurrentSoilCell().agentLeaving(this)) // leave the supporting soil cell.
				A_Protocol.event("A_VisibleAgent:discardThis", this + " cannot leave " + this.getCurrentSoilCell(), isError);
		}
		if (this instanceof A_Animal) ((A_Animal) this).lastContainerLeft = null;
		this.bornCoord_Umeter = null;
		super.discardThis();
	}
	/** First stage of the scheme perception, deliberation, decision, action of Jacques Ferber
	 * <p>
	 * Ferber, J., 1995. Les systèmes multi-agents: vers une intelligence collective, InterEditions Paris.</Br>
	 * Available at: http://www.citeulike.org/user/Bc91/article/1464256 [Accessed February 28, 2011].
	 * </p>
	 * Can be used as an indicator of interaction
	 * @author J.E. Longueville 2011 - J.Le Fur 03.2012
	 * @return TreeSet<I_situated_thing> listeVisibleObject<br>
	 */
	protected TreeSet<I_SituatedThing> perception() {
		return myLandscape.findObjectsOncontinuousSpace(this, this.sensing_UmeterByTick);
	}
	//
	// SETTERS AND GETTERS
	//
	public void setCurrentSoilCell(I_Container currentSoilCell) {
		this.currentSoilCell = currentSoilCell;
	}
	public void setHasToSwitchFace(boolean hasToSwitchFace) {
		this.hasToSwitchFace = hasToSwitchFace;
	}
	public TreeSet<A_Animal> getAnimalsTargetingMe() {
		return animalsTargetingMe;
	}
	public Coordinate getCoordinate_Umeter() {
		return A_VisibleAgent.myLandscape.getThingCoord_Umeter(this);
	}
	public Coordinate getCoordinate_Ucs() {
		return A_VisibleAgent.myLandscape.getThingCoord_Ucs(this);
	}
	public I_Container getCurrentSoilCell() {
		return currentSoilCell;
	}
	public double getSensing_UmeterByTick() {
		return sensing_UmeterByTick;
	}
	/** Length (straight line) between this and coordinate / JLF 03.2021 */
	public double getDistance_Umeter(Coordinate oneCoordinate) {
		// // Old procedure
		// Coordinate current = this.getCoords_Umeter();
		// We use * and not Math.pow to improve computing time
		// return Math.sqrt((current.x - relative.x) * (current.x - relative.x) + (current.y - relative.y) * (current.y -
		// relative.y));
		return this.getCoordinate_Umeter().distance(oneCoordinate);
	}
	/** Length (straight line) between this and visible agent / JLF 03.2021 */
	public double getDistance_Umeter(I_SituatedThing agent) {
		return this.getCoordinate_Umeter().distance(agent.getCoordinate_Umeter());
	}
}