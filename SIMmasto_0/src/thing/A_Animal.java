/* This source code is licensed under a BSD license as detailed in file SIMmasto_0.license.txt */
package thing;

import java.util.ArrayList;
import java.util.TreeSet;

import com.vividsolutions.jts.geom.Coordinate;

import data.C_Parameters;
import data.constants.I_ConstantNumeric;
import data.converters.C_ConvertTimeAndSpace;
import simmasto0.C_ContextCreator;
import simmasto0.protocol.A_Protocol;
import simmasto0.util.C_PathWanderer;
import simmasto0.util.C_VariousUtilities;
import thing.dna.C_GenomeAnimalia;
import thing.dna.C_GenomeEucaryote;
import thing.dna.I_DiploidGenome;
import thing.dna.species.C_GenomeBorrelia;
import thing.ground.A_Container;
import thing.ground.A_SupportedContainer;
import thing.ground.C_SoilCell;
import thing.ground.C_SoilCellGraphed;
import thing.ground.I_Container;
import thing.ground.landscape.C_Landscape;

/** This class accounts for animals as moving agents.
 * @author Q.Baduel, J-E Longueville, J. Le Fur 2011-02, 2013.01, 04.2015, 09.2020 */
public abstract class A_Animal extends A_Organism implements I_ConstantNumeric {
	//
	// FIELDS
	//
	private double maxDispersalDistance_Umeter = 0.0;
	protected String desire = "";// Should never be null or else desire.equals will not work JLF 01.2018
	protected boolean trappedOnBoard = false;
	protected I_SituatedThing target = null; // store the container or agent selected as a destination
	protected double speed_UmeterByTick;
	protected Coordinate nextMove_Umeter = new Coordinate();
	protected boolean hasToLeaveFullContainer = false;// used to avoid return to containers left for fullness
	protected I_Container lastContainerLeft; // Used to compute dispersal
	protected boolean male; // shortcut to avoid fetching each time into gonosomes and so on (see constructor)
	protected I_Container myHome;// JLF 03.2021
	//
	// CONSTRUCTOR
	//
	public A_Animal(I_DiploidGenome genome) {
		super(genome);
		initParameters();
		this.male = ((C_GenomeEucaryote) genome).getGonosome().isMale();
		String sex = "+F:";
		if (testMale()) sex = "-M:";
		this.setMyName(sex + this.retrieveMyName());
	}
	//
	// OVERRIDEN METHOD
	//
	/** The heart of SimMasto agents<br>
	 * Realize actions bound to foraging desire, then super<br>
	 * @version jlf 2017.08 2017.12 2018.01 */
	@Override
	public void step_Utick() {
		if (!this.trappedOnBoard) {// if trapped, do nothing - JLF 07.2013
			//
			// TWO REFLEX ACTIVITIES
			this.checkDanger();
			this.setHasToLeaveFullContainer(this.currentSoilCell.isFull());
			if (this.hasToLeaveFullContainer) this.actionDisperse();
			else {
				//
				// INTENTIONAL ACTIVITIES
				if (!this.getDesire().equals(NONE)) {// NONE when agent do not need to deliberate
					if (this.getDesire() == "") this.setDesire(FEED);
					if (this.target == null) {
						// PDE: perception, (desire), deliberation, decision, execution (viz.action)
						// dedicated to be sophisticated in daughter classes JLF 06.2014, 12.2017 */
						if (setDestination(deliberation(perception())) == null) {
							this.actionNoChoice();// unsatisfied desire
						}
					}
					if (this.target != null) { // NB JLF: not "else if" since situation may have changed in the preceding step
						// If travel is achieved, process target, reset moves and goals
						if (this.isArrived(this.speed_UmeterByTick)) {
							this.setHasToSwitchFace(true);
							this.manageArrival();
							if (this.processTarget()) this.discardTarget();
						}
						else {
							// recompute since animals are moving targets jlf 2017.08
							if (this.target instanceof A_Animal) computeNextMoveToTarget();
							this.actionMoveToDestination();
						}
					}
				}
				computeMaxDispersalDistance_Umeter();
			}
		}
		super.step_Utick();
	}

	/** Remove references to last container left, targeted container and eggs */
	@Override
	public void discardThis() {
		this.lastContainerLeft = null;
		this.desire = null;
		this.nextMove_Umeter = null;
		this.myHome=null;
		this.discardTarget();
		super.discardThis();
	}

	/** If infected, virus or bacteria agent reproduces and its offspring invades the contacting object <br/>
	 * If content more than one virus or bacteria, a randomly chosen bacteria or virus agent has to reproduces! */
	@Override
	public void actionInfect(I_SituatedThing thing) {
		// TODO MS de JLF 02.2021 pas d'arrayList, que des treeSet
		ArrayList<I_SituatedThing> borreliaList = new ArrayList<I_SituatedThing>();
		for (I_SituatedThing oneThing : this.getOccupantList()) if (oneThing instanceof C_BorreliaCrocidurae)
			borreliaList.add(oneThing);
		int chosenPositionNumber = (int) (C_ContextCreator.randomGeneratorForInitialisation.nextDouble() * borreliaList
				.size());
		A_Animal oneBorrelia = ((C_BorreliaCrocidurae) borreliaList.get(chosenPositionNumber)).giveBirth(
				new C_GenomeBorrelia());
		C_ContextCreator.protocol.contextualizeNewThingInContainer(oneBorrelia, (I_Container) thing);
		super.actionInfect(thing);
	}

	@Override
	public void getInfection(I_SituatedThing pathogen) {
		((A_Animal) pathogen).setTrappedOnBoard(true);
		C_ContextCreator.protocol.contextualizeNewThingInContainer(pathogen, this);
		super.getInfection(pathogen);
	}
	/** Test if agent content bacteria or virus */
	@Override
	public boolean isInfected() {
		if (this.isDead()) return false;// to avoid probe crash (JLF 03.2021)
		Boolean isInfected = false;
		for (I_SituatedThing oneThing : this.getOccupantList()) if (oneThing instanceof C_BorreliaCrocidurae) {
			isInfected = true;
			break;
		}
		// If is infected, return true else return the super!
		if (isInfected) return isInfected;
		else return super.isInfected();
	}
	//
	// METHODS
	//
	/** Initialize speed and sensing using time and space conversion<br>
	 * NB: can be used standalone if users parameters are changed during simulation, JLF 2017.08 */
	public void initParameters() {
		this.speed_UmeterByTick = ((C_GenomeAnimalia) this.genome).getSpeed_UmeterByDay()
				/ C_ConvertTimeAndSpace.oneDay_Utick;
		// Get sensing surface
		double sensingSurface_USquareMeterByTick = ((C_GenomeAnimalia) this.genome)
				.getSensingSurface_USquareMeterByDay() / C_ConvertTimeAndSpace.oneDay_Utick;
		this.sensing_UmeterByTick = Math.sqrt(sensingSurface_USquareMeterByTick / Math.PI);
		this.sensing_UmeterByTick = Math.max(this.sensing_UmeterByTick, INSTANTANEOUS_SENSING_Umeter);
	}

	/** Manage the position if thing is arrived */
	protected void manageArrival() {
		this.nextMove_Umeter.x = 0.0;
		this.nextMove_Umeter.y = 0.0;
		A_VisibleAgent.myLandscape.moveToLocation(this, this.target.getCoordinate_Ucs());
	}
	/** choose an action (wander, disperse) if no target found.<br>
	 * May be overridden in daughter classes<br>
	 * J.Le Fur, 07.2018 */
	protected void actionNoChoice() {
		if (this.getDesire().equals(FEED)) this.actionEat();
			this.actionForage();
	}
	/** retrieve energy when resting, has to be generalized JLF 07.2021 */
	protected void actionRest() {
		this.energy_Ukcal++;
	}
	/** set new random move then move */
	protected void actionForage() {
		this.setNewRandomMove();
		this.actionMove();
	}
	/** Sucked up by the container */
	protected boolean processTarget() {
		// Sucked up by the container target, when arrived at proximity. Does not work for animal targets
		if ((this.target instanceof I_Container) && !(this.target instanceof A_Animal)) {
			C_ContextCreator.protocol.contextualizeOldThingInCell(this, (I_Container) this.target);
			if (this.getDesire().equals(FEED)) return this.actionEat();
			else if (this.getDesire().equals(WANDER)) this.actionWander();
			return true;
		}
		return false;
	}
	/** Put animal in container and place it in the right location <br>
	 * was formerly actionEnterBurrow<br>
	 * Rev. jlf 11.2017 */
	public void actionEnterContainer(I_Container oneContainer) {
		C_ContextCreator.protocol.contextualizeOldThingInCell(this, oneContainer);// Put animal in container
		myLandscape.moveToLocation(this, myLandscape.getThingCoord_Ucs(oneContainer));// Place agent at the container position
		this.setHasToSwitchFace(true);// JLF 04.2021
		this.energy_Ukcal--;
	}
	/** Quit current burrow system and enter in support soil cell of burrow. Get a newRandom displacement and move */
	public void actionRandomExitOfContainer() {// trucks, burrows, traps,...
		if (this.currentSoilCell instanceof A_SupportedContainer) {
			this.setHasToSwitchFace(true);
			C_ContextCreator.protocol.contextualizeOldThingInCell(this, this.currentSoilCell.getCurrentSoilCell());
			this.setNewRandomMove();
			A_VisibleAgent.myLandscape.translate(this, nextMove_Umeter); // Move agent on the graphics presentation
			this.energy_Ukcal--;
		}
		else
			A_Protocol.event("A_Animal.actionRandomExitOfContainer()", this + " not within a supported container ("
					+ this.currentSoilCell, isError);
	}

	/** If not dead and within a dangerous area, test death, get back two steps. Can be overridden by daughter classes<br>
	 * agent'probe on the GUI Version JLF&MS 01.2017, jlf 08.2017 */
	protected void checkDanger() {
		if (this.currentSoilCell == null) {
			A_Protocol.event("A_Animal.checkDanger()", "no soil cell for " + this, isError);
		}
		// If arrived in wrong place, get back two steps
		else if (this.currentSoilCell.getAffinity() <= DANGEROUS_AREA_AFFINITY) {
			if (this.currentSoilCell.getAffinity() < DANGEROUS_AREA_AFFINITY) {
				this.checkDeath(DANGEROUS_AREA_MORTALITY_RATE);
				this.actionFlee();
			}
			else this.checkDeath(DANGEROUS_AREA_MORTALITY_RATE / 4.);// TODO number in source 2019.02 JLF Reduce death probability
																		// for e.g. streets
		}
	}
	/** invert direction of move and move REPEAT_DISPERSAL times<br>
	 * JLF&MS 08.2021 */
	protected void actionFlee() {
		// TODO MS to JLF je ne comprends pas l'utilité de lui faire faire des aller retour
		this.nextMove_Umeter.x = -this.nextMove_Umeter.x;
		this.nextMove_Umeter.y = -this.nextMove_Umeter.y;
		for (int i = 0; i < REPEAT_DISPERSAL; i++) if (!this.hasEnteredDomain) this.actionMove();
	}
	/** Check if can perceive thing */
	public boolean canPerceiveThing(I_SituatedThing oneThing) {
		return (this.getDistance_Umeter(oneThing) - (C_Parameters.CELL_WIDTH_Umeter / 2) < this
				.getSensing_UmeterByTick());
	}
	/** Interact with an animal - Method refined in daughters classes.
	 * @param animal any daughter class of A_Animal / JLF 02.2014
	 * @return true if success */
	protected boolean actionInteract(A_Animal animal) {
		A_Protocol.event("A_VisibleAgent.actionInteract", animal + " do nothing", isError);
		return false;
	}
	/** Method refined in daughters classes. Consume the cell and gain the affinity amount of energy.
	 * @version JLF 09.2017
	 * @return true if success */
	protected boolean actionEat() {
		this.energy_Ukcal += this.currentSoilCell.getAffinity();
		return true;
	}
	/** If no next move, get a random one; then (TODO number in source 2017.09 JLF ) actionMove REPEAT_DISPERSAL times to
	 * accentuate dispersal <br>
	 * revision JLF 12.2016, 05,09.2017 */
	public void actionDisperse() {
		if ((nextMove_Umeter.x == 0) && (nextMove_Umeter.y == 0)) this.setNewRandomMove();
		for (int i = 0; i < REPEAT_DISPERSAL; i++) if (!this.hasEnteredDomain) this.actionMove();
	}
	/** Get a random one; then actionMove, then reset nextMove <br>
	 * JLF 02.2018 */
	public void actionWander() {
		this.setNewRandomMove();
		this.actionMove();
	}
	/***/
	public boolean actionHide() {
		return false;
	}
	/** If desire=FORAGE select the cell set with the best affinity<br>
	 * NB:second stage of the perception-deliberation-action scheme of Ferber 1999 :<br>
	 * @param perceivedThings TreeSet <I_situated_thing> listeVisibleObjects from perception method
	 * @return candidate targets : selected things with best affinities<br>
	 * @version J.E.Longueville & J.Le Fur 2011 / jlefur 03.2012 / Complete rev. JLF 08,10.2017 */
	protected TreeSet<I_SituatedThing> deliberation(TreeSet<I_SituatedThing> perceivedThings) {
		if (this.getDesire().equals(FEED)) return this.chooseFood(perceivedThings);
		else {
			A_Protocol.event("A_Animal.deliberation", this + " does not desire to FEED (" + this.getDesire() + ")",
					isError);
			return perceivedThings;
		}
	}
	public TreeSet<I_SituatedThing> chooseShelter(TreeSet<I_SituatedThing> perceivedThings) {
		TreeSet<I_SituatedThing> shelters = new TreeSet<I_SituatedThing>();
		// if (this.myHome != null) shelters.add(this.myHome);
		return shelters;
	}
	/** Use list of perceived things and choose any container except A_Organism (eggs, relatives, ...) as a forage source<br>
	 * rev.JLF 02.2017, 01.2020 */
	public TreeSet<I_SituatedThing> chooseFood(TreeSet<I_SituatedThing> perceivedThings) {
		TreeSet<I_SituatedThing> candidateTargets = new TreeSet<I_SituatedThing>();// There may be several equivalent targets
		A_Container currentDest = null, testedContainer = null;
		for (I_SituatedThing oneThingPerceived : perceivedThings) {
			// Any container except A_Organism (eggs, relatives, ...)
			if ((oneThingPerceived instanceof A_Container) && !(oneThingPerceived instanceof A_Organism)) {
				testedContainer = (A_Container) oneThingPerceived;
				if (testedContainer.getAffinity() > this.currentSoilCell.getAffinity()) {
					if (currentDest == null || testedContainer.getAffinity() > currentDest.getAffinity()) {
						candidateTargets.clear();
						candidateTargets.add(testedContainer);
						currentDest = testedContainer;
					}
					else
						if (testedContainer.getAffinity() == currentDest.getAffinity())
							candidateTargets.add(testedContainer);
				}
			}
		}
		// Do not reenter the container just left
		if (this.lastContainerLeft != null) candidateTargets.remove(this.lastContainerLeft);
		return candidateTargets;
	}
	/** Compute next move to target, move on the GUI */
	protected void actionMoveToDestination() {
		if (this.target != null) {
			this.computeNextMoveToTarget();
			this.actionMove();
		}
	}
	/** Compute next move to target with a given speed, move on the GUI */
	protected void actionMoveToDestination(double speed) {
		if (this.target != null) {
			this.computeNextMoveToTarget(speed);
			this.actionMove();
		}
	}
	/** Move on the landscape (continuous space, grid and GUI) and moves occupants accordingly<br>
	 * rev. JLF 08.2017, 03.2021
	 * @see C_Landscape#translate */
	public void actionMove() {
		A_VisibleAgent.myLandscape.translate(this, this.nextMove_Umeter); // Move agent on the graphics presentation
		if (!this.occupantList.isEmpty()) {
			TreeSet<A_VisibleAgent> occupants = (TreeSet<A_VisibleAgent>) this.occupantList.clone();
			java.util.Iterator<A_VisibleAgent> it = occupants.iterator();
			while (it.hasNext()) A_VisibleAgent.myLandscape.translate(it.next(), this.nextMove_Umeter);
		}
		this.energy_Ukcal--;
	}
	/** Random selection of the target from undecidable alternatives / rev. JLF 04.2017
	 * @param alternatives : the possible alternatives set (any situated thing) */
	protected I_SituatedThing setDestination(TreeSet<I_SituatedThing> alternatives) {
		if (!alternatives.isEmpty()) {
			this.setTarget(this.chooseClosest(this, alternatives));
			return this.target;
		}
		else return null;
	}
	/** Select among alternatives the nearest from focus<br>
	 * @author MS 2019.03, rev. jlf 10.2019 */
	protected I_SituatedThing chooseClosest(I_SituatedThing focus, TreeSet<I_SituatedThing> alternatives) {
		I_SituatedThing alternative = null;
		I_SituatedThing closest = null;
		double newDistance_Umeter = 1000000000000000.;// TODO number in source OK JLF 2018.06
		double targetDistance_Umeter = 0.;
		java.util.Iterator<I_SituatedThing> iterator = alternatives.iterator();
		for (int i = 0; i < alternatives.size(); i++) {
			alternative = iterator.next();
			targetDistance_Umeter = focus.getDistance_Umeter(alternative);
			if (targetDistance_Umeter < newDistance_Umeter) {
				closest = alternative;
				newDistance_Umeter = targetDistance_Umeter;
			}
		}
		return closest;
	}
	/** Dismiss targetPoint and provide two random coordinates in meters. This redefines the nextMove_Umeter field. */
	public void setNewRandomMove() {
		this.nextMove_Umeter.x = (C_ContextCreator.randomGeneratorForMovement.nextDouble() * speed_UmeterByTick)
				- (C_ContextCreator.randomGeneratorForMovement.nextDouble() * speed_UmeterByTick);
		this.nextMove_Umeter.y = (C_ContextCreator.randomGeneratorForMovement.nextDouble() * speed_UmeterByTick)
				- (C_ContextCreator.randomGeneratorForMovement.nextDouble() * speed_UmeterByTick);
	}
	/** Provide an aim to move toward specific coordinates. We watch if the agent is (or isn't) close to its goal, we have to do
	 * several tests because if the agent is near in the x variable but not in the y, the next step it may be too far on the x
	 * abscissa and near to the y abscissa, we have to make sure that the agent won't go on a perpetual move. author QBaduel */
	public void computeNextMoveToTarget() {
		computeNextMoveToTarget(this.speed_UmeterByTick);
	}
	/** The same function as computeNextMoveToTarget but with a given speed. <br>
	 * @param agentSpeed_UmeterByTick is the max distance of the agent or agent's vehicle to go over (speed_UmeterBy...). if
	 *            speed_Umeter exceeds the targetPoint then the distance to target is just the distance between
	 *            currentPoint_Umeter and targetPoint_Umeter. <br>
	 *            Le déplacement est suivant un vecteur de coordonnées (vectCoordX, vectCoordY) ie targetPoint_Umeter -
	 *            currentPoint_Umeter (sens), de longeur vectNorm = racine(vectCoordX², vectCoordY²) (norme) et faisant un angle
	 *            alpha par rapport à l'abscisse (direction) si vectCoordX == 0, alpha = +PI/2 ou -PI/2 ça dépend du signe de
	 *            vectCoordY sinon alpha = atan(vectCoordY/vectCoordX) Ainsi nous avons la direction et le sens du déplacement. Et
	 *            donc à partir de la distance à parcourir, on peut calculer exactement nextMove_Umeter.x et nextMove_Umeter.y
	 * @see #computeNextMoveToTarget()
	 * @author LeFur 08.2012 rev. Mboup 10.2014, JLF 04.2017 */
	protected void computeNextMoveToTarget(double agentSpeed_UmeterByTick) {
		if (this.target == null) {
			A_Protocol.event("A_Animal.computeNextMoveToTarget", this + " has no target, cannot compute", isError);
		}
		else if (agentSpeed_UmeterByTick > 0 && !trappedOnBoard) {
			// vecteur de déplacement
			double alpha, signeX;
			double distanceToTargetX = this.getTargetPoint_Umeter().x - this.getCoordinate_Umeter().x;
			double distanceToTargetY = this.getTargetPoint_Umeter().y - this.getCoordinate_Umeter().y;
			double distanceToTarget = Math.sqrt(distanceToTargetX * distanceToTargetX + distanceToTargetY
					* distanceToTargetY);
			if (distanceToTargetX == 0) {
				signeX = 1.0; // positif
				alpha = Math.signum(distanceToTargetY) * Math.PI / 2;
			}
			else {
				signeX = Math.signum(distanceToTargetX);
				alpha = Math.atan(distanceToTargetY / distanceToTargetX);
			}
			if (distanceToTarget < agentSpeed_UmeterByTick) { // pour que l'agent ne dépasse pas sa destination
				agentSpeed_UmeterByTick = distanceToTarget;
			}
			this.nextMove_Umeter.x = signeX * agentSpeed_UmeterByTick * Math.cos(alpha);
			this.nextMove_Umeter.y = signeX * agentSpeed_UmeterByTick * Math.sin(alpha);
		}
	}
	/** The same function as computeNext_move but with a given speed. <br>
	 * @param agentSpeed_UmeterByTick is the max distance of the agent or agent's vehicle to go over (speed_UmeterBy...). if
	 *            speed_Umeter exceeds the targetPoint then the distance to target is just the distance between
	 *            currentPoint_Umeter and targetPoint_Umeter.
	 * @see #computeNextMoveToTarget()
	 * @author LeFur 08.2012 rev. Mboup 10.2014, JLF 04.2017, MS 08.2019 */
	protected void computeNextMoveToTarget0(double agentSpeed_UmeterByTick) {
		if (this.target == null)
			A_Protocol.event("A_Animal.computeNextMoveToTarget", this + " has no target, cannot compute", isError);
		else
			if (agentSpeed_UmeterByTick > 0 && !trappedOnBoard) {
				double distanceToTarget = this.getDistance_Umeter(this.getTargetPoint_Umeter());
				if (distanceToTarget < agentSpeed_UmeterByTick) { // For restricting agent in its displacement distance
					agentSpeed_UmeterByTick = distanceToTarget;
				}
				this.nextMove_Umeter = C_VariousUtilities.computeVectorBetweenTwoPoints(this.getCoordinate_Umeter(),
						this.getTargetPoint_Umeter(), agentSpeed_UmeterByTick);
			}
	}

	/** Maximum distance (straight line) from its birth location */
	public void computeMaxDispersalDistance_Umeter() {
		double currentDispersalDistance_Umeter = this.getDistance_Umeter(this.bornCoord_Umeter);
		if (currentDispersalDistance_Umeter > this.maxDispersalDistance_Umeter)
			this.maxDispersalDistance_Umeter = currentDispersalDistance_Umeter;
	}
	/** Remove reference to this in target.animalsTargettingMe<br>
	 * rev. jlf 12.2017, 03.2021 */
	public void discardTarget() {
		if ((this.target != null) && (this.target instanceof A_VisibleAgent)) {
			if (!((A_VisibleAgent) this.target).animalsTargetingMe.remove(this))
				A_Protocol.event("A_Animal.discardTarget()", "could not remove ref. to " + this + " in "
						+ this.target, isError);
			this.target = null;// continue anyway
		} // else target is already null
	}
	//
	// SETTERS AND GETTERS
	//
	/** Restore the speed life trait if false(animal is outside), reduces it by a number if true (animal is inside)<br>
	 * @version J.LeFur&M.Sall, 05.2017, JLF 09,12.2017 */
	public void setTrappedOnBoard(boolean status) {
		this.trappedOnBoard = status;
		this.hasToSwitchFace = true;
		if (status) this.speed_UmeterByTick = this.speed_UmeterByTick / 10.;// TODO number in source 2017.05 JLF & MS
		else
			this.speed_UmeterByTick = ((C_GenomeAnimalia) this.genome).getSpeed_UmeterByDay()
					/ C_ConvertTimeAndSpace.oneDay_Utick;
	}
	/** Tag animal last container when it leaves
	 * @see A_SupportedContainer#agentLeaving */
	public void setLastContainerLeft(I_Container lastContainerLeft) {
		this.lastContainerLeft = lastContainerLeft;
	}
	public void setHasToLeaveFullContainer(boolean dispersal) {
		this.hasToLeaveFullContainer = dispersal;
		if (dispersal) {
			this.discardTarget();
			this.hasToSwitchFace = true;
		}
	}
	public void setMale(boolean male) {
		this.male = male;
	}
	/** Remove and dereference preceding target if needed, reference this in target.animalsTargettingMe<br>
	 * rev. jlf 12.2017, 03.2021, 01.2022 */
	public void setTarget(I_SituatedThing target) {
		this.discardTarget();
		this.target = target;
		if (this.target instanceof A_VisibleAgent) ((A_VisibleAgent) this.target).animalsTargetingMe.add(this);
	}
	public void setMyHome(I_Container myHome) {
		this.myHome = myHome;
	}
	public I_Container getMyHome() {
		return this.myHome;
	}
	/** Test if agent arrives to destination within one tick. If yes next move = (0,0)
	 * @param speed agent or agent's vehicle speed
	 * @author LeFur 08.2012, 08.2017 */
	protected boolean isArrived(double speed) {
		Coordinate currentCoord_Umeter = this.getCoordinate_Umeter();
		if (this.target == null) {
			A_Protocol.event("A_Animal.isArrived", this + " owns no target, returning false", isError);
			return false;
		}
		else {
			boolean reach_x = false, reach_y = false;
			if (Math.abs(currentCoord_Umeter.x - getTargetPoint_Umeter().x) <= speed) reach_x = true;
			if (Math.abs(currentCoord_Umeter.y - getTargetPoint_Umeter().y) <= speed) reach_y = true;
			if (reach_x && reach_y) return true;
			return false;
		}
	}
	public boolean isHasToLeaveFullContainer() {
		return this.hasToLeaveFullContainer;
	}
	public boolean isTrappedOnBoard() {
		return trappedOnBoard;
	}
	/** Tag cell outside the grid as obstacle @author JLF 2019.08 */
	public boolean isSightObstacle(I_SituatedThing currentSoilCell) {
		if (currentSoilCell == null) return true;
		else return false;
	}
	public String getDesire() {
		return desire;
	}
	/** For display purposes - JLF 02.2017 */
	public String getCell1Target() {
		if (this.target != null) {
			// special for visible Agent (viz. NDS)
			if (this.target instanceof A_VisibleAgent) return ((A_VisibleAgent) this.target).toString();
			else return target.toString();
		}
		else return this.getDesire();
	}
	public String getCell0Location() {
		if (this.currentSoilCell != null) return this.currentSoilCell.toString();
		else return NONE;
	}
	public double getMaxDispersalDistance_Umeter() {// used to display on GUI
		return maxDispersalDistance_Umeter;
	}
	public Coordinate getTargetPoint_Umeter() {
		return this.target.getCoordinate_Umeter();
	}
	public I_SituatedThing getTarget() {
		return this.target;
	}
	/** NB: should be isMale() but renamed so as not to be display in agent'probe on the GUI */
	public boolean testMale() {
		return this.male;
	}
	/** NB: should be isFemale() but renamed so as not to be display in agent'probe on the GUI */
	public boolean testFemale() {
		return !this.male;
	}
	public void setDesire(String desire) {
		this.setHasToSwitchFace(true);
		this.desire = desire;
	}
}
