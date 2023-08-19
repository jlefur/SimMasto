package simmasto0.util;

import com.vividsolutions.jts.geom.Coordinate;

import data.C_Parameters;
import data.constants.I_ConstantDodel2;
import thing.A_Animal;
import thing.A_VisibleAgent;
import thing.I_SituatedThing;

/** This allow a agent to follow an direction with obstacle avoidance .
 * @author M. Sall 11.2019, rev. JLF 03.2021 */
public class C_WallFollower implements I_ConstantDodel2 {
	//
	// FIELDS
	//
	private I_SituatedThing myBackCell;
	private I_SituatedThing myRightCell;
	private I_SituatedThing myLeftCell;
	private I_SituatedThing myFrontCell;
	private int nearestWallPosition = NO_WALL;

	private Coordinate orientationPoint_Umeter;
	//
	// CONSTRUCTOR
	//
	public C_WallFollower() {}
	//
	// OVERRIDDEN METHOD
	//
	/** Remove references */
	public void discardThis() {
		this.myBackCell = null;
		this.myRightCell = null;
		this.myFrontCell = null;
		this.myLeftCell = null;
		this.orientationPoint_Umeter = null;
	}
	//
	// METHODS
	//
	/** Verify the next possible displacement and return the selected thing */
	public void guiding(A_Animal oneThing) {
		this.computeOrientations(oneThing, A_VisibleAgent.myLandscape
				.getGrid()[(int) this.orientationPoint_Umeter.x][(int) this.orientationPoint_Umeter.y]);
		switch (nearestWallPosition) {
			case FORWARD_TO_AGENT : {
				// if (C_ContextCreator.randomGeneratorForMovement.nextDouble() > 0.5) {
				if (!oneThing.isSightObstacle(this.myRightCell)) oneThing.setTarget(this.selectRightAim(oneThing));
				else if (!oneThing.isSightObstacle(this.myLeftCell)) oneThing.setTarget(this.selectLeftAim(oneThing));
				else if (!oneThing.isSightObstacle(this.myBackCell)) oneThing.setTarget(this.selectBackAim(oneThing));
				// }
				// else {
				// if (this.isObstacle(this.myLeftCell)) this.target = this.selectLeftAim();
				// else if (this.isObstacle(this.myRightCell)) this.target = this.selectRightAim();
				// else if (this.isObstacle(this.myBackCell)) this.target = this.selectBackAim();
				// }
			}
				break;
			case LEFT_TO_AGENT : {
				if (!oneThing.isSightObstacle(this.myFrontCell)) oneThing.setTarget(this.selectFrontAim(oneThing));
				else if (!oneThing.isSightObstacle(this.myRightCell)) oneThing.setTarget(this.selectRightAim(oneThing));
				else if (!oneThing.isSightObstacle(this.myBackCell)) oneThing.setTarget(this.selectBackAim(oneThing));
				else if (!oneThing.isSightObstacle(this.myLeftCell)) oneThing.setTarget(this.selectLeftAim(oneThing));
			}
				break;
			case RIGHT_TO_AGENT : {
				if (!oneThing.isSightObstacle(this.myFrontCell)) oneThing.setTarget(this.selectFrontAim(oneThing));
				else if (!oneThing.isSightObstacle(this.myLeftCell)) oneThing.setTarget(this.selectLeftAim(oneThing));
				else if (!oneThing.isSightObstacle(this.myBackCell)) oneThing.setTarget(this.selectBackAim(oneThing));
				else if (!oneThing.isSightObstacle(this.myRightCell)) oneThing.setTarget(this.selectRightAim(oneThing));
			}
		}

	}

	/** Use the selected destination to compute the right, left, forward and backward direction of agent */
	public void computeOrientations(A_Animal oneThing, I_SituatedThing selectedThing) {
		Coordinate selectedThingCoordinate_Umeter = selectedThing.getCoordinate_Umeter();
		Coordinate leftCoordinate_Umeter = new Coordinate();
		Coordinate rightCoordinate_Umeter = new Coordinate();
		Coordinate backwardCoordinate_Umeter = new Coordinate();
		this.myRightCell = null;
		this.myLeftCell = null;
		this.myBackCell = null;
		this.myFrontCell = selectedThing;
		// We consider the same distance between selected thing and right, left or backward formula : see chronoThesisSall
		double distanceToTarget_Umeter = C_VariousUtilities.distance_Umeter(
				selectedThingCoordinate_Umeter, oneThing.getCoordinate_Umeter());

		rightCoordinate_Umeter.x = oneThing.getCoordinate_Umeter().x + (C_Parameters.CELL_WIDTH_Umeter
				/ distanceToTarget_Umeter) * (selectedThingCoordinate_Umeter.y - oneThing.getCoordinate_Umeter().y);
		rightCoordinate_Umeter.y = oneThing.getCoordinate_Umeter().y - (C_Parameters.CELL_WIDTH_Umeter
				/ distanceToTarget_Umeter) * (selectedThingCoordinate_Umeter.x - oneThing.getCoordinate_Umeter().x);

		leftCoordinate_Umeter.x = oneThing.getCoordinate_Umeter().x - (C_Parameters.CELL_WIDTH_Umeter
				/ distanceToTarget_Umeter) * (selectedThingCoordinate_Umeter.y - oneThing.getCoordinate_Umeter().y);
		leftCoordinate_Umeter.y = oneThing.getCoordinate_Umeter().y + (C_Parameters.CELL_WIDTH_Umeter
				/ distanceToTarget_Umeter) * (selectedThingCoordinate_Umeter.x - oneThing.getCoordinate_Umeter().x);

		if (A_VisibleAgent.myLandscape.isPointInGrid(rightCoordinate_Umeter)) {
			this.myRightCell = A_VisibleAgent.myLandscape
					.getGrid()[(int) rightCoordinate_Umeter.x][(int) rightCoordinate_Umeter.y];
			if (C_VariousUtilities.checkObstacleBefore0(oneThing, this.myRightCell) != null) this.myRightCell = null;
		}
		if (A_VisibleAgent.myLandscape.isPointInGrid(leftCoordinate_Umeter)) {
			this.myLeftCell = A_VisibleAgent.myLandscape
					.getGrid()[(int) leftCoordinate_Umeter.x][(int) leftCoordinate_Umeter.y];
			if (C_VariousUtilities.checkObstacleBefore0(oneThing, this.myLeftCell) != null) this.myLeftCell = null;
		}
		backwardCoordinate_Umeter.x = (2 * oneThing.getCoordinate_Umeter().x - selectedThingCoordinate_Umeter.x);
		backwardCoordinate_Umeter.y = (2 * oneThing.getCoordinate_Umeter().y - selectedThingCoordinate_Umeter.y);
		if (A_VisibleAgent.myLandscape.isPointInGrid(backwardCoordinate_Umeter)) {
			this.myBackCell = A_VisibleAgent.myLandscape
					.getGrid()[(int) backwardCoordinate_Umeter.x][(int) backwardCoordinate_Umeter.y];
			if (C_VariousUtilities.checkObstacleBefore0(oneThing, this.myBackCell) != null) this.myBackCell = null;
		}
	}
	/** Allow agent to go right and compute the next displacement sense */
	public I_SituatedThing selectRightAim(A_Animal oneThing) {
		if (nearestWallPosition == RIGHT_TO_AGENT) {
			if (this.myBackCell != null)
				this.orientationPoint_Umeter = C_VariousUtilities.substract(C_VariousUtilities.sum(this.myBackCell
						.getCoordinate_Umeter(), this.myRightCell.getCoordinate_Umeter()), oneThing.getCurrentSoilCell()
								.getCoordinate_Umeter());
			else
				if (this.myFrontCell != null) {
					this.orientationPoint_Umeter = C_VariousUtilities.substract(C_VariousUtilities.sum(oneThing
							.getCurrentSoilCell().getCoordinate_Umeter(), this.myRightCell.getCoordinate_Umeter()),
							this.myFrontCell.getCoordinate_Umeter());
					nearestWallPosition = LEFT_TO_AGENT;
				}
		}
		else {
			if (((A_Animal) oneThing).isSightObstacle(this.myFrontCell))
				this.orientationPoint_Umeter = C_VariousUtilities.substract(C_VariousUtilities.sum(this.myFrontCell
						.getCoordinate_Umeter(), this.myRightCell.getCoordinate_Umeter()), oneThing.getCurrentSoilCell()
								.getCoordinate_Umeter());
			else
				if (this.myLeftCell != null)
					this.orientationPoint_Umeter = C_VariousUtilities.substract(C_VariousUtilities.sum(this.myFrontCell
							.getCoordinate_Umeter(), this.myLeftCell.getCoordinate_Umeter()), oneThing
									.getCurrentSoilCell().getCoordinate_Umeter());
				else
					this.orientationPoint_Umeter = C_VariousUtilities.substract(C_VariousUtilities.sum(this.myFrontCell
							.getCoordinate_Umeter(), oneThing.getCurrentSoilCell().getCoordinate_Umeter()),
							this.myRightCell.getCoordinate_Umeter());

			nearestWallPosition = LEFT_TO_AGENT;
		}
		return myRightCell;
	}
	/** Allow agent to go left and compute the next displacement sense */
	public I_SituatedThing selectLeftAim(A_Animal oneThing) {
		if (nearestWallPosition == LEFT_TO_AGENT) {
			if (this.myBackCell != null)
				this.orientationPoint_Umeter = C_VariousUtilities.substract(C_VariousUtilities.sum(this.myBackCell
						.getCoordinate_Umeter(), this.myLeftCell.getCoordinate_Umeter()), oneThing.getCurrentSoilCell()
								.getCoordinate_Umeter());
			else
				if (this.myFrontCell != null) {
					this.orientationPoint_Umeter = C_VariousUtilities.substract(C_VariousUtilities.sum(oneThing
							.getCurrentSoilCell().getCoordinate_Umeter(), this.myLeftCell.getCoordinate_Umeter()),
							this.myFrontCell.getCoordinate_Umeter());
					nearestWallPosition = RIGHT_TO_AGENT;
				}
		}
		else {
			if (oneThing.isSightObstacle(this.myFrontCell))
				this.orientationPoint_Umeter = C_VariousUtilities.substract(C_VariousUtilities.sum(this.myFrontCell
						.getCoordinate_Umeter(), this.myLeftCell.getCoordinate_Umeter()), oneThing.getCurrentSoilCell()
								.getCoordinate_Umeter());
			else
				if (this.myRightCell != null)
					this.orientationPoint_Umeter = C_VariousUtilities.substract(C_VariousUtilities.sum(this.myFrontCell
							.getCoordinate_Umeter(), this.myRightCell.getCoordinate_Umeter()), oneThing
									.getCurrentSoilCell().getCoordinate_Umeter());
				else
					this.orientationPoint_Umeter = C_VariousUtilities.substract(C_VariousUtilities.sum(this.myFrontCell
							.getCoordinate_Umeter(), oneThing.getCurrentSoilCell().getCoordinate_Umeter()),
							this.myLeftCell.getCoordinate_Umeter());
			nearestWallPosition = RIGHT_TO_AGENT;
		}
		return myLeftCell;
	}
	/** Allow agent to go back and compute the next displacement sense */
	public I_SituatedThing selectBackAim(A_Animal oneThing) {
		if (nearestWallPosition == RIGHT_TO_AGENT) {
			if (this.myLeftCell != null)
				this.orientationPoint_Umeter = C_VariousUtilities.substract(C_VariousUtilities.sum(this.myLeftCell
						.getCoordinate_Umeter(), this.myBackCell.getCoordinate_Umeter()), oneThing.getCurrentSoilCell()
								.getCoordinate_Umeter());
			else {
				this.orientationPoint_Umeter = null;
				this.nearestWallPosition = NO_WALL;
			}
		}
		else if (nearestWallPosition == LEFT_TO_AGENT) {
			if (this.myRightCell != null)
				this.orientationPoint_Umeter = C_VariousUtilities.substract(C_VariousUtilities.sum(this.myRightCell
						.getCoordinate_Umeter(), this.myBackCell.getCoordinate_Umeter()), oneThing.getCurrentSoilCell()
								.getCoordinate_Umeter());
			else {
				this.orientationPoint_Umeter = null;
				this.nearestWallPosition = NO_WALL;
			}
		}
		else
			this.orientationPoint_Umeter = C_VariousUtilities.substract(C_VariousUtilities.sum(this.myBackCell
					.getCoordinate_Umeter(), this.myBackCell.getCoordinate_Umeter()), oneThing.getCurrentSoilCell()
							.getCoordinate_Umeter());
		return this.myBackCell;
	}
	/** Allow agent to go forward and compute the next displacement sense */
	public I_SituatedThing selectFrontAim(A_Animal oneThing) {
		if (nearestWallPosition == RIGHT_TO_AGENT) {
			if (this.myRightCell != null) {
				this.orientationPoint_Umeter = C_VariousUtilities.substract(C_VariousUtilities.sum(this.myFrontCell
						.getCoordinate_Umeter(), this.myRightCell.getCoordinate_Umeter()), oneThing.getCurrentSoilCell()
								.getCoordinate_Umeter());
			}
			else {
				if (this.myLeftCell != null)
					this.orientationPoint_Umeter = C_VariousUtilities.substract(C_VariousUtilities.sum(this.myFrontCell
							.getCoordinate_Umeter(), oneThing.getCurrentSoilCell().getCoordinate_Umeter()),
							this.myLeftCell.getCoordinate_Umeter());
			}
		}
		else if (nearestWallPosition == LEFT_TO_AGENT) {
			if (this.myRightCell != null)
				this.orientationPoint_Umeter = C_VariousUtilities.substract(C_VariousUtilities.sum(this.myFrontCell
						.getCoordinate_Umeter(), oneThing.getCurrentSoilCell().getCoordinate_Umeter()), this.myRightCell
								.getCoordinate_Umeter());
			else
				if (this.myLeftCell != null)
					this.orientationPoint_Umeter = C_VariousUtilities.substract(C_VariousUtilities.sum(this.myLeftCell
							.getCoordinate_Umeter(), this.myFrontCell.getCoordinate_Umeter()), oneThing
									.getCurrentSoilCell().getCoordinate_Umeter());
		}
		else {
			this.orientationPoint_Umeter = C_VariousUtilities.substract(C_VariousUtilities.sum(this.myFrontCell
					.getCoordinate_Umeter(), oneThing.getCurrentSoilCell().getCoordinate_Umeter()), this.myRightCell
							.getCoordinate_Umeter());
		}
		return this.myFrontCell;
	}
	/** Verify if selected point keep the same sense of last agent movement and if it is the best distance */
	/** Compute the new agent position near the wall */
	public Coordinate computeNewPositionWithTarget_Umeter(A_Animal oneAnimal, I_SituatedThing targetingThing) {
		Coordinate newPositionCoordinate_Umeter = targetingThing.getCoordinate_Umeter();
		double verificationDistance_Umeter = MIN_VALUE_OF_REBOUNDZONE_Umeter;
		Coordinate findingPoint_Umeter = new Coordinate();
		double distanceToCurrentPosition = 0.;
		I_SituatedThing newThingContainer = null;
		do {
			distanceToCurrentPosition = C_VariousUtilities.distance_Umeter(oneAnimal
					.getCoordinate_Umeter(), newPositionCoordinate_Umeter);
			findingPoint_Umeter = C_VariousUtilities.computeVectorBetweenTwoPoints(oneAnimal.getCoordinate_Umeter(),
					newPositionCoordinate_Umeter, verificationDistance_Umeter);
			newPositionCoordinate_Umeter.x -= findingPoint_Umeter.x;
			newPositionCoordinate_Umeter.y -= findingPoint_Umeter.y;
			newThingContainer = A_VisibleAgent.myLandscape
					.getGrid()[(int) newPositionCoordinate_Umeter.x][(int) newPositionCoordinate_Umeter.y];
		} while ((distanceToCurrentPosition > verificationDistance_Umeter) && oneAnimal.isSightObstacle(
				newThingContainer));
		return newThingContainer.getCoordinate_Umeter();
	}
	//
	// SETTERS & GETTERS
	//
	public void setOrientationPoint(Coordinate orientationPoint_Umeter) {
		this.orientationPoint_Umeter = orientationPoint_Umeter;
	}
	public void setNearestWallPosition(int nearestWallPosition) {
		this.nearestWallPosition = nearestWallPosition;
	}
	public Coordinate getOrientationPoint() {
		return orientationPoint_Umeter;
	}
	public int getNearestWallPosition() {
		return nearestWallPosition;
	}
}
