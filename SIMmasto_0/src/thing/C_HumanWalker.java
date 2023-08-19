package thing;

import com.vividsolutions.jts.geom.Coordinate;

import data.C_Parameters;
import simmasto0.protocol.A_Protocol;
import simmasto0.util.C_PathWandererAstar;
import thing.dna.I_DiploidGenome;
import thing.ground.C_LandPlot;
import thing.ground.C_SoilCellUrban;

/** Used for animals within urban network landscapes @see C_LandscapeUrban Le Fur 02,03,04,05.2022 */
public class C_HumanWalker extends A_HumanUrban {
	//
	// FIELDS
	//
	protected Coordinate targetPoint_Umeter; // From PAM, Human_Carrier Caution: disconnected from this.target
	public C_PathWandererAstar pathWanderer;
	/** since WANDER desire leads to random cell, get back to this initial cell when starting a new path */
	private C_SoilCellUrban nextStepStartCell;
	//
	// CONSTRUCTOR
	//
	public C_HumanWalker(I_DiploidGenome genome) {
		super(genome);
	}
	//
	// OVERRIDEN METHODS
	//
	@Override
	public A_Animal giveBirth(I_DiploidGenome genome) {
		return new C_HumanWalker(genome);
	}
	/** Overrides A_Animal since targetPoint_Umeter is coded hard in C_Human that uses pathWander
	 * @see simmasto0.util.C_PathWanderer
	 * @Version J.Le Fur, 04.2017 */
	@Override
	public Coordinate getTargetPoint_Umeter() {
		if (this.targetPoint_Umeter == null) return super.getTargetPoint_Umeter();
		else return this.targetPoint_Umeter;
	}

	/** Activity within one time step : if target is reached: select a new one, elaborates its path. If target not reached : steps
	 * towards its current path. If speed is higher than cell size setNextNode() is repeated several times / authors P.A.Mboup &
	 * J.Le Fur - sept.2012, rev. PAM 03.2014, JLF 10.2015, 01,05.2022 */
	@Override
	public void step_Utick() {
		if (this.nextStepStartCell == null) this.nextStepStartCell = (C_SoilCellUrban) this.currentSoilCell;// init once at start
		// Due to manageActivities() activities chrono may have changed before reaching the preceding target and has to
		// be accounted for
		I_SituatedThing initialTarget = this.target;
		String aim = this.aim;
		this.manageActivities();
		// reset the path in case target has changed meanwhile
		if (this.target != initialTarget && this.pathWanderer.nodesList != null) {
			if (C_Parameters.VERBOSE)
				A_Protocol.event("C_HumanWalker.step_Utick()", this + " could not fullfill " + aim + "; switches to "
						+ this.aim, isNotError);
			this.pathWanderer.nodesList = null;
			this.nextStepStartCell = (C_SoilCellUrban) this.currentSoilCell;
		}
		// already walking a path
		if (this.pathWanderer.nodesList != null) this.ActionWalkOnPath();
		else {
			if (this.target != null && this.target != this.currentSoilCell) {// safety test to remove ?
				// Check if path is requested :
				// 1) If target is in the same concession then no pathwandering, direct move.
				// 2) If within a street has to path wander (and almost all street are the same concession)
				C_LandPlot currentConcession = this.nextStepStartCell.getConcession();
				C_LandPlot targetConcession = ((C_SoilCellUrban) this.target).getConcession();
				if ((targetConcession != currentConcession) || targetConcession.getPlotType() == "STREET"
						|| currentConcession.getPlotType() == "STREET") {
					this.pathWanderer.getPath(this.nextStepStartCell, (C_SoilCellUrban) this.target);
					this.ActionWalkOnPath();
				}
			}
			else super.step_Utick(); // No path wandering
		}
	}

	/** Manage the position if thing is arrived */
	@Override
	protected void manageArrival() {
		this.targetPoint_Umeter = null;
		this.pathWanderer.nodesList = null;
		this.nextMove_Umeter.x = 0.0;
		this.nextMove_Umeter.y = 0.0;
		this.nextStepStartCell = (C_SoilCellUrban) this.target;
		this.discardTarget();
		// if (C_Parameters.VERBOSE) A_VisibleAgent.myLandscape.resetCellsColor();// @@TEMPORARY JLF 2022.02
	}

	/** Test graph end if not, super
	 * @param speed the speed of the agent or agent's vehicle
	 * @author LeFur 08.2012, 08.2017, rev. 01.2022 */
	protected boolean isArrived(double speed) {
		if (this.pathWanderer.isPathEnd) return true;
		else return super.isArrived(speed);
	}

	/** Compute next move to target, move on the GUI */
	@Override
	protected void actionMoveToDestination() {
		// arrived in any path cell towards the targetCell
		if (this.pathWanderer.hasToSetNextNode(this.currentSoilCell)) this.setNextNode();
		super.actionMoveToDestination();
		// if (C_Parameters.VERBOSE)
		// A_VisibleAgent.myLandscape.getValueLayer().set(10, ((C_SoilCellUrban) this.currentSoilCell)
		// .retrieveLineNo(), this.currentSoilCell.retrieveColNo());// @@vert boutique
	}

	/** Remove references to targetPoint_Umeter discards pathwanderer, JLF 02.2022 */
	@Override
	public void discardThis() {
		this.targetPoint_Umeter = null;
		this.pathWanderer.discardThis();
		this.pathWanderer = null;
		this.nextStepStartCell = null;
		super.discardThis();
	}

	public void ActionWalkOnPath() {
		double distanceTravelled = 0;
		this.pathWanderer.intermediateDistanceTravelled = 0;
		this.pathWanderer.isPathEnd = false;
		while (this.pathWanderer.crossingIntermediateDistance(distanceTravelled, this.speed_UmeterByTick)) {
			distanceTravelled = 0; // it is because continue of the loop.
			// 1) arrived in any path cell towards the targetCell
			if (this.pathWanderer.hasToSetNextNode(this.currentSoilCell)) this.setNextNode();
			// 2) reached a destination (just before stopped)
			else if (this.pathWanderer.isPathEnd && this.isArrived(this.speed_UmeterByTick)) {
				manageArrival();
				break; // pour que parked reste plus de temps true (pendant un step)
			}
			// 3) just before leaving (if targetPoint is null, then has to define it
			else if (this.targetPoint_Umeter == null) {
				startTraveling((C_SoilCellUrban) this.target);
				continue;
			}
			double maxDistanceToMove_Umeter = this.speed_UmeterByTick - this.pathWanderer.intermediateDistanceTravelled;
			this.computeNextMoveToTarget(maxDistanceToMove_Umeter);
			distanceTravelled = Math.sqrt(this.nextMove_Umeter.x * this.nextMove_Umeter.x + this.nextMove_Umeter.y
					* this.nextMove_Umeter.y);// @@ inutile ?
			this.actionMoveToDestination();
		}
	}
	//
	// METHODS
	//
	// Progress within a graph, JLF 01.2022, from PAM HumanCarrier
	protected void setNextNode() {
		this.targetPoint_Umeter = this.pathWanderer.computeNextNodeAndGetTargetPoint_Umeter(this.targetPoint_Umeter);
	}
	/**   */
	protected void startTraveling(C_SoilCellUrban finalDestination) { // just before leaving
		if (finalDestination != null) { // patch in cases (unexplained, #1/1000) where deliberation does not succeed, JLF 09.2014
			this.targetPoint_Umeter = finalDestination.getCoordinate_Umeter();
			this.setTarget(finalDestination);
			this.pathWanderer.buildBestPath((C_SoilCellUrban) this.currentSoilCell, finalDestination);
		}
		this.hasToSwitchFace = true;
	}
}
