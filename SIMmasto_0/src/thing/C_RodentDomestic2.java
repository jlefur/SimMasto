package thing;

import java.util.TreeSet;

import com.vividsolutions.jts.geom.Coordinate;

import data.C_Parameters;
import data.constants.I_ConstantDodel2;
import simmasto0.C_ContextCreator;
import simmasto0.protocol.A_Protocol;
import simmasto0.util.C_VariousUtilities;
import simmasto0.util.C_WallFollower;
import thing.dna.I_DiploidGenome;
import thing.ground.A_Container;
import thing.ground.C_BurrowSystem;
import thing.ground.C_LandPlot;
import thing.ground.C_SoilCell;
import thing.ground.C_SoilCellUrban;
import thing.ground.I_Container;
import thing.ground.landscape.C_Landscape;

/** Rodent agents that are used to live within cities buildings and can interact with different agents like humans, cats,
 * bacterias and ticks <br>
 * created for protocol Dodel 2
 * @author Moussa Sall, July 2018, rev. JLF 03.2021 */
public class C_RodentDomestic2 extends C_RodentCommensal implements I_ConstantDodel2 {
	//
	// FIELDS
	//
	private C_WallFollower wallFollower = new C_WallFollower();
	private boolean hasToCreateHome = false;

	// private boolean hasToStay = false;// keep agent in a favorable land
	//
	// CONSTRUCTOR
	//
	public C_RodentDomestic2(I_DiploidGenome genome) {
		super(genome);
	}
	//
	// OVERRIDEN METHODS
	//
	@Override
	public void step_Utick() {
		if (A_Protocol.protocolCalendar.isDawn() || A_Protocol.protocolCalendar.isDayTime()) {
			if (this.currentSoilCell instanceof C_BurrowSystem) this.setDesire(NONE);// keep quiet
			else if (this.getCurrentSoilCell().getAffinity() == HOUSEDOOR_AFFINITY) this.setDesire("");
			else this.setDesire(HIDE); // search shelter if (this.getCurrentSoilCell().getAffinity() <= CORRIDOR_AFFINITY)
		}
		else {// reset desires
				// if (!this.isSucklingChild()) this.setDesire("");
			if (this.desire == NONE || this.desire == REST || this.desire == HIDE || this.desire == FLEE) {
				this.setDesire("");
			}
		}
		super.step_Utick();
	}
	@Override
	protected void updatePhysiologicStatus() {
		if (hasToCreateHome) this.createNewHome();
		String currentDesire = this.desire;
		super.updatePhysiologicStatus();
		if (currentDesire.equals(FLEE) || currentDesire.equals(HIDE)) this.setDesire(currentDesire);
	}
	/** Remove perceived things beyond obstacles */
	@Override
	protected TreeSet<I_SituatedThing> perception() {
		TreeSet<I_SituatedThing> reallyPerceivedThings = new TreeSet<I_SituatedThing>();
		TreeSet<I_SituatedThing> perceivedThings = super.perception();
		// TODO JLF&MS 2020.09 generalize obstacle perception ?
		for (I_SituatedThing oneThing : perceivedThings) {
			I_SituatedThing sightObstacle = C_VariousUtilities.checkObstacleBefore0(this, oneThing);
			if (!(sightObstacle != null && sightObstacle != oneThing)) reallyPerceivedThings.add(oneThing);
		}
		reallyPerceivedThings.remove(this.currentSoilCell);
		reallyPerceivedThings.remove(this);
		return reallyPerceivedThings;
	}
	/** Author MS 2018, Check if human are perceived */
	@Override
	protected void checkDanger() {
		TreeSet<I_SituatedThing> perceivedThings = this.perception();
		for (I_SituatedThing oneThing : perceivedThings) {
			if (oneThing instanceof A_HumanUrban) {
				this.setDesire(HIDE);
				break;
			}
		}
		super.checkDanger();
	};
	/** (1) If agent has to hide or rest choose myHome, or select shelters or select closest wall */
	@Override
	protected TreeSet<I_SituatedThing> deliberation(TreeSet<I_SituatedThing> perceivedThings) {
		TreeSet<I_SituatedThing> selectedThings = new TreeSet<I_SituatedThing>();
		if ((this.getDesire().equals(REST) || this.getDesire().equals(HIDE))
				&& !(this.currentSoilCell instanceof C_BurrowSystem)) {
			if (perceivedThings.contains(this.myHome) && (this.myHome instanceof C_BurrowSystem)) {
				selectedThings.add(this.myHome);
				return selectedThings;
			}
			selectedThings = this.chooseShelter(perceivedThings);
			if (!selectedThings.isEmpty()) return selectedThings;
			if (this.getDesire().equals(HIDE)) {
				selectedThings = this.chooseDoor(perceivedThings);
				if (!selectedThings.isEmpty()) return selectedThings;
			}
			selectedThings = wallsPerceived(perceivedThings);
			if (!selectedThings.isEmpty()) return selectedThings;
			else this.setDesire(FLEE);
		}
		if (this.getDesire().equals(FLEE)) return selectedThings;// return wall or null
		if (this.getDesire().equals(WANDER)) {
			if (!this.inDangerousArea()) {
				for (C_SoilCell onecell : this.getMyLandPlot().getCells()) selectedThings.add(onecell);
			}
			return selectedThings;
		}
		return super.deliberation(perceivedThings);
	}
	@Override
	public TreeSet<I_SituatedThing> chooseFood(TreeSet<I_SituatedThing> perceivedThings) {
		TreeSet<I_SituatedThing> selectedFoodCells = new TreeSet<I_SituatedThing>();
		for (I_SituatedThing oneThing : perceivedThings) if (oneThing instanceof C_Food)
			selectedFoodCells.add(oneThing);
		return selectedFoodCells;
	}
	@Override
	public TreeSet<I_SituatedThing> chooseShelter(TreeSet<I_SituatedThing> perceivedThings) {
		TreeSet<I_SituatedThing> selectedShelters = new TreeSet<I_SituatedThing>();
		for (I_SituatedThing oneThing : perceivedThings) if (oneThing instanceof C_BurrowSystem)
			selectedShelters.add(oneThing);
		if (selectedShelters.isEmpty() && this.inFavourableLand()) {
			boolean interestLandPlot = false;
			C_LandPlot oneLandPlot = ((C_SoilCell) this.getCurrentSoilCell()).getMyLandPlot();
			TreeSet<I_SituatedThing> foundedThings = new TreeSet<I_SituatedThing>();
			for (C_SoilCell oneCell : oneLandPlot.getCells()) {
				foundedThings.addAll(oneCell.getOccupantList());
			}
			for (I_SituatedThing oneThing : foundedThings) {
				if (oneThing instanceof C_BurrowSystem) selectedShelters.add(oneThing);
				else if (!interestLandPlot && oneThing instanceof C_Food) interestLandPlot = true;
			}
			if (selectedShelters.isEmpty()) {
				if (interestLandPlot) {
					C_BurrowSystem oneBurrow = new C_BurrowSystem(this.getCurrentSoilCell().getAffinity(), this
							.getCurrentSoilCell().getCoordinate_Ucs().X, this.getCurrentSoilCell()
									.getCoordinate_Ucs().Y);
					C_ContextCreator.protocol.contextualizeNewThingInContainer(oneBurrow, this.getCurrentSoilCell());
					selectedShelters.add(oneBurrow);
				}
			}
		}
		return selectedShelters;
	}
	public TreeSet<I_SituatedThing> chooseDoor(TreeSet<I_SituatedThing> perceivedThings) {
		TreeSet<I_SituatedThing> selectedDoors = new TreeSet<I_SituatedThing>();
		for (I_SituatedThing oneThing : perceivedThings) {
			if (((A_Container) oneThing).getAffinity() == HOUSEDOOR_AFFINITY) selectedDoors.add(oneThing);
		}
		return selectedDoors;
	}
	@Override
	/** Position close to the sightObtacle, not within */
	protected void manageArrival() {
		if ((((A_Container) this.target).getAffinity() == HOUSEDOOR_AFFINITY) && this.getDesire().equals(HIDE))
			super.manageArrival();
		else
			if (this.isSightObstacle(this.target)) {// && (this.target != this.myHome)) {
				// if no orientation or orientation outside grid orientate towards target
				if (wallFollower.getOrientationPoint() == null || (!A_VisibleAgent.myLandscape.isPointInGrid(
						wallFollower.getOrientationPoint())))
					wallFollower.setOrientationPoint(this.target.getCoordinate_Umeter());
				// if no wall found, search forward
				if (wallFollower.getNearestWallPosition() == NO_WALL)
					wallFollower.setNearestWallPosition(FORWARD_TO_AGENT);
				// Position close to the sightObtacle, not within
				double distanceToTarget_Umeter = C_VariousUtilities.distance_Umeter(this.target, this);
				if (distanceToTarget_Umeter > (C_Landscape.halfCellDiagonal()
						+ C_Landscape.halfCellDiagonal())) {
					Coordinate newPosition_Umeter = wallFollower.computeNewPositionWithTarget_Umeter(this, this.target);
					if (!this.getCoordinate_Umeter().equals(newPosition_Umeter))
						C_ContextCreator.protocol.contextualizeOldThingInSpace(this, newPosition_Umeter.x,
								newPosition_Umeter.y);
				}
			}
			else super.manageArrival();
	}
	@Override
	protected boolean processTarget() {
		if (this.desire.equals(REST)) this.actionRest();
		if (this.getDesire().equals(FLEE)) this.setDesire("");
		if (this.getDesire().equals(HIDE)) {
			if (((A_Container) this.target).getAffinity() == HOUSEDOOR_AFFINITY) {
				C_ContextCreator.protocol.contextualizeOldThingInCell(this, (I_Container) this.target);
				this.setDesire("");
				return true;
			}
		}
		if (this.isSightObstacle(this.target)) {
			wallFollower.guiding(this);
			return false;
		}
		if (this.target instanceof A_Animal) {
			wallFollower.setOrientationPoint(null);
			wallFollower.setNearestWallPosition(NO_WALL);
		}
		return super.processTarget();
	}

	/** generate a new animal, necessary to use spawn
	 * @see A_Amniote#spawn */
	@Override
	public A_Animal giveBirth(I_DiploidGenome genome) {
		return new C_RodentDomestic2(genome);
	}
	/** Remove references to last container left, targeted container children and eggs */
	@Override
	public void discardThis() {
		this.wallFollower.discardThis();
		this.wallFollower = null;
		if (!this.getOccupantList().isEmpty()) {
			Object[] occupants = this.getOccupantList().toArray();
			for (Object occupant : occupants) {
				if (occupant instanceof C_OrnitodorosSonrai) {
					C_ContextCreator.protocol.contextualizeOldThingInCell((I_SituatedThing) occupant,
							this.currentSoilCell);
					C_ContextCreator.protocol.landscape.moveToLocation((I_SituatedThing) occupant, this
							.getCoordinate_Ucs());
					((A_Animal) occupant).setTrappedOnBoard(false);
					((C_OrnitodorosSonrai) occupant).resetCurMealDuration_Umn();
				}
			}
		}
		super.discardThis();
	}
	/** Allow ticks to move with rodent host */
	@Override
	protected void actionMoveToDestination() {
		super.actionMoveToDestination();
		for (I_SituatedThing oneThing : this.getOccupantList()) myLandscape.moveToLocation(oneThing, myLandscape
				.getThingCoord_Ucs(this));
	}
	@Override
	protected void actionNoChoice() {
		if (this.desire.equals(FLEE)) this.actionFlee();
		else if (this.inDangerousArea() || this.getDesire().equals(HIDE)) this.actionDisperse();
		else if (this.desire.equals(FEED)) this.actionForage();
		else super.actionNoChoice();
	}
	/** Try to reach myHome even if distant. rev. JLF 01.2022 */
	@Override
	protected void actionFlee() {
		if (this.myHome != null && !((A_VisibleAgent) this.myHome).isDead()) {
			this.setTarget(this.myHome);
			this.actionMoveToDestination(this.speed_UmeterByTick * SLOW_FACTOR);
			if (this.isArrived(this.speed_UmeterByTick * SLOW_FACTOR)) this.setDesire("");
		}
		else {
			A_Protocol.event("C_RodentDomestic2.actionFlee()", "myHome of " + this + " is null or dead: " + this.myHome,
					isError);
			super.actionFlee();
		}
	}

	//
	// OTHER METHODS
	//
	/** Receive all perceived things and return walls perceived */
	public TreeSet<I_SituatedThing> wallsPerceived(TreeSet<I_SituatedThing> perceivedThings) {
		TreeSet<I_SituatedThing> perceivedWalls = new TreeSet<I_SituatedThing>();
		for (I_SituatedThing oneThing : perceivedThings) {
			if (this.isSightObstacle(oneThing)) perceivedWalls.add(oneThing);
		}
		return perceivedWalls;
	}
	// TODO MS de JLF 2022.01 redondant avec digBurrow de fossorial ?
	public void createNewHome() {
		if (!this.inDangerousArea()) {
			C_BurrowSystem oneBurrow = new C_BurrowSystem(this.getCurrentSoilCell().getAffinity(), this
					.getCoordinate_Umeter().X, this.getCoordinate_Umeter().Y);
			C_ContextCreator.protocol.contextualizeNewThingInContainer(oneBurrow, this.getCurrentSoilCell());
			this.setMyHome(oneBurrow);
			this.setHasToCreateHome(false);
		}
	}
	//
	// SETTER & GETTERS
	//
	@Override
	/***/
	public void setNewRandomMove() {
		super.setNewRandomMove();
		Coordinate nextPosition_Umeter = new Coordinate();
		nextPosition_Umeter.x = this.getCoordinate_Umeter().x + this.nextMove_Umeter.x;
		nextPosition_Umeter.y = this.getCoordinate_Umeter().y + this.nextMove_Umeter.y;
		if (A_VisibleAgent.myLandscape.isPointInGrid(nextPosition_Umeter)) {
			I_SituatedThing oneCell = A_VisibleAgent.myLandscape
					.getGrid()[(int) nextPosition_Umeter.x][(int) nextPosition_Umeter.y];
			I_SituatedThing seeingFence = C_VariousUtilities.checkObstacleBefore0(this, oneCell);
			if (seeingFence != null) {
				double distanceToFence = C_VariousUtilities.distance_Umeter(this, seeingFence);
				if (distanceToFence > C_Parameters.CELL_WIDTH_Umeter) {
					Coordinate nearestPosition_Umeter = wallFollower.computeNewPositionWithTarget_Umeter(this,
							seeingFence);
					this.nextMove_Umeter.x = nearestPosition_Umeter.x - this.getCoordinate_Umeter().x;
					this.nextMove_Umeter.y = nearestPosition_Umeter.y - this.getCoordinate_Umeter().y;
				}
			}
		}
	}
	/** If target is an obstacle - do not enter in it, is arrived if near (i.e., within reboundZone)
	 * @param speed: the speed of the agent + the target cell half diagonal + MIN_VALUE_OF_REBOUNDZONE_Umeter
	 * @author LeFur 08.2012, 08.2017 rev M.Sall 2019.01 */
	@Override
	protected boolean isArrived(double speed) {
		if (this.isSightObstacle(this.target))
			return super.isArrived(speed + A_VisibleAgent.myLandscape.halfCellDiagonal()
					+ MIN_VALUE_OF_REBOUNDZONE_Umeter);
		else
			return super.isArrived(speed);
	}
	@Override
	/** Check if thing is a wall */
	public boolean isSightObstacle(I_SituatedThing thing) {
		if (thing instanceof C_SoilCellUrban && ((C_SoilCellUrban) thing).isWall()) return true;
		else return super.isSightObstacle(thing);
	}
	private boolean inFavourableLand() {
		int affinity = this.getCurrentSoilCell().getAffinity();
		if (affinity == ROOM_AFFINITY || affinity == ROOMFOOD_AFFINITY || affinity == SHOPFOOD_AFFINITY
				|| affinity == WORKSHOP_AFFINITY || affinity == CORRIDOR_AFFINITY || affinity == CONCESSION_AFFINITY)
			return true;
		return false;
	}
	private boolean inDangerousArea() {
		if (this.getCurrentSoilCell() instanceof C_SoilCellUrban)
			return ((C_SoilCellUrban) this.getCurrentSoilCell()).isDangerousArea();
		return false;
	}
	public void setHasToCreateHome(boolean hasToCreateHome) {
		this.hasToCreateHome = hasToCreateHome;
	}
}
