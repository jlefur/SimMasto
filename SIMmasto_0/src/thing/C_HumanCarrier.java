package thing;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import com.vividsolutions.jts.geom.Coordinate;

import data.constants.I_ConstantString;
import data.constants.I_ConstantTransportation;
import simmasto0.C_ContextCreator;
import simmasto0.util.C_PathWanderer;
import thing.dna.I_DiploidGenome;
import thing.ground.C_City;
import thing.ground.C_SoilCell;
import thing.ground.C_SoilCellGraphed;
import thing.ground.C_Vehicle;
import thing.ground.landscape.C_Landscape;

/** A human carrier owns a C_Vehicle and go from one city to the other.
 * @author Mboup & Le Fur 07-09.2012 */
public class C_HumanCarrier extends A_Human implements I_ConstantString, I_ConstantTransportation {
	//
	// FIELDS
	//
	protected C_Vehicle vehicle;
	protected TreeSet<C_City> cityList;
	protected String citiesToTradeInfo_Ustring;
	protected Coordinate targetPoint_Umeter; // From PAM, Human_Carrier
	public C_PathWanderer pathWanderer;
	//
	// CONSTRUCTOR
	//
	/** used to allow the giveBirth procedure
	 * @see #giveBirth */
	public C_HumanCarrier(I_DiploidGenome genome) {
		super(genome);
		this.cityList = new TreeSet<C_City>();
	}
	//
	// METHODS
	//
	/** Used to toggle death on the GUI, JLF 09.2012 */
	@Override
	public void setDead(boolean dead) {
		this.dead = dead;
	}
	/** If carrier load rodents, reach a city and unload them. Remove references to vehicle and cities<br>
	 * Version rev. JLF 05.2016, 03.2021 */
	@Override
	public void discardThis() {
		if ((this.vehicle != null) && (!this.vehicle.getFullRodentList().isEmpty())) {
			this.actionReachPathEndNow();
			this.vehicle.unloadRodents();
		}
		this.vehicle.setDead(true);
		this.vehicle = null;
		this.cityList = null;
		this.citiesToTradeInfo_Ustring = null;
		super.discardThis();
	}
	/** Get a new vehicle, instantiate the graph (no node in path yet), name the carrier,
	 * @param vehicleType */
	public C_Vehicle ownVehicle(String vehicleType) {
		this.vehicle = new C_Vehicle(vehicleType, this);
		C_ContextCreator.protocol.contextualizeNewThingInContainer(vehicle, currentSoilCell);
		this.pathWanderer = new C_PathWanderer((C_SoilCell) this.currentSoilCell);
		this.vehicle.setParked(true);
		this.setMyName("HC-" + this.vehicle.getType() + NAMES_SEPARATOR + this.myId);
		this.vehicle.initParameters();
		return this.vehicle;
	}
	/** Activity within one time step : if a city is reached: select a new city, elaborates its path, unload rodents. If new city
	 * not reached : steps towards its current path. If vehicle speed is higher than cell size selectNextNode() is repeated
	 * several times / authors P.A.Mboup & J.Le Fur - sept.2012, rev. PAM 03.2014, JLF 10.2015 */
	@Override
	public void step_Utick() {
		double distanceTravelled = 0;
		this.pathWanderer.intermediateDistanceTravelled = 0;
		while (this.pathWanderer.crossingIntermediateDistance(distanceTravelled, this.vehicle
				.getSpeed_UmeterByTick())) {
			distanceTravelled = 0; // it is because continue of the loop.
			// 1) arrived in any path cell towards the targetCell
			if (this.pathWanderer.hasToSetNextNode(this.currentSoilCell)) this.setNextNode();
			// 2) reached a destination city (just before stopped)
			else if (this.pathWanderer.getPathEnd() && !this.vehicle.isParked()) {
				manageArrival();
				break; // pour que parked reste plus de temps true (pendant un step)
			}
			// 3) just before leaving
			else if (this.vehicle.isParked()) {
				// clear animals targeting this's vehicle before leaving / jlf 10.2017
				for (A_Animal oneAnimal : this.vehicle.animalsTargetingMe) oneAnimal.discardTarget();
				this.vehicle.animalsTargetingMe.clear();
				// TODO JLF de MS 2019.07 error caused by HumanCarrier HC-truck_55780 when its current soil cell is
				// road_46903(129,93)!!!
				startTraveling();
				continue;
			}
			if (!this.vehicle.isParked()) { // safety measure
				double maxDistanceToMove_Umeter = (this.vehicle.getSpeed_UmeterByTick()
						- this.pathWanderer.intermediateDistanceTravelled) / this.pathWanderer.track_slow_factor;
				this.computeNextMoveToTarget(maxDistanceToMove_Umeter);
				distanceTravelled = Math.sqrt(this.nextMove_Umeter.x * this.nextMove_Umeter.x + this.nextMove_Umeter.y
						* this.nextMove_Umeter.y) * this.pathWanderer.track_slow_factor;
				this.actionMoveToDestination();
			}
		}
		// super.step_Utick(); // Has to be removed for proper function JLF 01.2017
	}

	public int getLoad_Urodent() {
		if (this.isDead()) return 0;// to avoid probe crash (JLF 03.2021)
		return this.vehicle.getLoad_Urodent();
	}

	protected void actionMoveToCell(C_SoilCell cell) {
		myLandscape.moveToContainer(this, cell);
		myLandscape.moveToContainer(this.vehicle, cell);
		this.energy_Ukcal--;
	}
	protected void setNextNode() {
		this.targetPoint_Umeter = this.pathWanderer.computeNextNodeAndGetTargetPoint_Umeter(this.targetPoint_Umeter);
		if (this.vehicle.isAccountForTrackCondition()) {
			C_SoilCell currentCell = (C_SoilCell) this.vehicle.currentSoilCell;
			if (currentCell.isOfGroundType(TRACK_EVENT))
				this.pathWanderer.track_slow_factor = TRACK_SLOW_FACTOR.get(TRACK_EVENT);
			else
				if (currentCell.isOfGroundType(GOOD_TRACK_EVENT)) {
					this.pathWanderer.track_slow_factor = TRACK_SLOW_FACTOR.get(GOOD_TRACK_EVENT);
				}
				else
					if (currentCell.isOfGroundType(ROAD_EVENT))
						this.pathWanderer.track_slow_factor = TRACK_SLOW_FACTOR.get(ROAD_EVENT);
		}
		else this.pathWanderer.track_slow_factor = 1.;// TODO number in source 2012 PAM ? pathWanderer.track_slow_factor
	}

	// TODO PAM 2016.10 unifier cette méthode avec goParked reachPathEndNow
	@Override
	protected void manageArrival() {
		C_SoilCellGraphed destination = pathWanderer.targetSoilCell;
		this.actionMoveToCell(destination);
		this.vehicle.unloadRodents();
		this.vehicle.setParked(true); // To allow rodents to load within the vehicle<x
		this.hasToSwitchFace = true;
		this.energy_Ukcal = 0.;
		this.vehicle.hasToSwitchFace = true;
	}
	/** Can be overridden */
	protected void startTraveling() { // just before leaving
		C_SoilCellGraphed finalDestination = (C_SoilCellGraphed) setDestination(deliberation(perception()));
		if (finalDestination != null) { // patch in cases (unexplained, #1/1000) where deliberation does not succeed, JLF 09.2014
			this.targetPoint_Umeter = finalDestination.getCoordinate_Umeter();
			this.setTarget(finalDestination);
			this.pathWanderer.buildPath((C_SoilCell) this.currentSoilCell, finalDestination);
		}
		this.vehicle.setParked(false);
		this.hasToSwitchFace = true;
		this.vehicle.hasToSwitchFace = true;
	}
	@Override
	protected void actionMoveToDestination() {
		((C_Landscape) myLandscape).translate(this, this.nextMove_Umeter);
		((C_Landscape) myLandscape).translate(this.vehicle, this.nextMove_Umeter);
		this.vehicle.carryRodentsToMyLocation_Ucs();
		this.energy_Ukcal--;
	}
	/** Force agent to reach destination (useful just before updating the ground) */

	// TODO PAM 2016.10 unifier cette méthode avec la méthode goParked()
	public void actionReachPathEndNow() {
		this.pathWanderer.prepareToReachPathEndNow();
		C_ContextCreator.protocol.contextualizeOldThingInCell(this, pathWanderer.targetSoilCell);
		C_ContextCreator.protocol.contextualizeOldThingInCell(this.vehicle, pathWanderer.targetSoilCell);
		this.energy_Ukcal--;
	}
	/** Retrieve this.cityList and convert into I_SituatedThing for compatibility with upper procedure
	 * @see A_VisibleAgent#retrieveCell2Perception Version JLF&PAM 03.2016 */
	@Override
	protected TreeSet<I_SituatedThing> perception() {
		TreeSet<I_SituatedThing> convertedList = new TreeSet<I_SituatedThing>();
		convertedList.addAll(this.cityList);
		return convertedList;
	}
	/** Elaborate carrier known city list with cumulated population divided by distance Select a destination city - build a map
	 * <city,cumulative weights> from carrier known cities, draw a random number.<br>
	 * The number has more chances to be in the interval of a big city, since it gets a great contribution to the total population
	 * / Author Mboup 07.2014, rev. JLF 09.2014 */
	@Override
	protected TreeSet<I_SituatedThing> deliberation(TreeSet<I_SituatedThing> candidateCells) {
		// double cumulatedPopulation_Uindividual = cityChoiceMap_Gravity(cityChoiceMap);
		Map<C_City, Double> cityChoiceMap = new TreeMap<C_City, Double>();
		double cumulatedPopulation_Uindividual = 0;
		double distanceBetweenCities;
		C_City currentCity = (C_City) ((C_SoilCellGraphed) this.currentSoilCell).getLandPlot(CITY_EVENT);
		if (currentCity != null) // TODO Ms to JLF 01.2021 Correction Bug : j'ai ajouté une vérification sur currentCity pour
									// eviter les cas ou le currentSoilCell et un road!
			for (I_SituatedThing oneCity : candidateCells) {

				/*
				 * // Alternative to select cities only with gravity if (oneCity != currentCity) {// Does not choose the city
				 * where it stands cumulatedPopulation_Uindividual += oneCity.getHumanPopSize_Uindividual();
				 * cityChoiceMap.put(oneCity, cumulatedPopulation_Uindividual);}
				 */

				// Alternative to select cities with gravity and distance
				if (oneCity != currentCity) {// Do not choose the city where it stands
					// Taking into account the duration of the journey (only the following line is touched here) PAM 2016.11
					distanceBetweenCities = this.pathWanderer.getPathWeight(((C_City) currentCity).getCells().first(),
							((C_City) oneCity).getCells().first());
					cumulatedPopulation_Uindividual += ((C_City) oneCity).getHumanPopSize_Uindividual()
							/ distanceBetweenCities;
					cityChoiceMap.put((C_City) oneCity, cumulatedPopulation_Uindividual);
				}
			}

		// Draw within the cumulated population distribution: the more interesting a city the more its contribution and chances to
		// be drawn, adapt. Mboup 2014
		TreeSet<I_SituatedThing> selectedCells = new TreeSet<I_SituatedThing>();// result of deliberation (the cells of the chosen
																				// city)
		double previousWeight = 0;
		double randomWeight = (C_ContextCreator.randomGeneratorForDestination.nextDouble()
				* cumulatedPopulation_Uindividual);

		for (C_City oneCity : cityChoiceMap.keySet()) {
			if ((previousWeight < randomWeight) && (randomWeight <= cityChoiceMap.get(oneCity))) {
				for (C_SoilCell cell : oneCity.getCells())
					// Add the cell of the chosen city to selectedCells (those of the correct graphType)
					if (cell.isOfGroundType(this.vehicle.getGraphType())) selectedCells.add(cell);
				return selectedCells;
			}
			else previousWeight = cityChoiceMap.get(oneCity);
		}
		return selectedCells; // if destination city not found, return the original treeSet
	}
	@Override
	/** Generating a new animal is compulsory for each A_Mammal daughter class */
	public A_Animal giveBirth(I_DiploidGenome genome) {
		return new C_HumanCarrier(genome);
	}
	@Override
	/** Do nothing and overrides previous methods */
	protected boolean actionInteract(A_Animal animal) {
		return false;
	}
	//
	// SETTERS & GETTERS
	//
	public void setCityList(TreeSet<C_City> newCityList) {
		this.cityList = newCityList;
	}
	/** Overrides A_Animal since targetPoint_Umeter is coded hard in C_HumanCarrier that uses pathWander
	 * @see simmasto0.util.C_PathWanderer
	 * @Version J.Le Fur, 04.2017 */
	@Override
	public Coordinate getTargetPoint_Umeter() {
		return this.targetPoint_Umeter;
	}
	public C_Vehicle getVehicle() {
		return this.vehicle;
	}
	public TreeSet<C_City> getCityList() {
		return this.cityList;
	}
	public String getCitiesToTradeInfo_Ustring() {
		return citiesToTradeInfo_Ustring;
	}
	public void setCitiesToTradeInfo_Ustring(String citiesToTradeFromEvent) {
		this.citiesToTradeInfo_Ustring = citiesToTradeFromEvent;
	}
	public boolean isParked() {
		if (this.isDead()) return false;// to avoid probe crash (JLF 03.2021)
		return this.vehicle.isParked();
	}
}
