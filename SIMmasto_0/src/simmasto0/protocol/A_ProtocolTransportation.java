/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package simmasto0.protocol;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import presentation.display.C_Background;
import presentation.display.C_CustomPanelSet;
import presentation.epiphyte.C_InspectorTransportation;
import repast.simphony.context.Context;
import repast.simphony.essentials.RepastEssentials;
import simmasto0.util.C_Graph;
import thing.C_HumanCarrier;
import thing.C_Rodent;
import thing.C_TaxiMan;
import thing.dna.C_GenomeAmniota;
import thing.ground.C_City;
import thing.ground.C_LandPlot;
import thing.ground.C_Market;
import thing.ground.C_Region;
import thing.ground.C_SoilCell;
import thing.ground.C_SoilCellGraphed;
import thing.ground.landscape.C_LandscapeCountry;
import thing.ground.landscape.C_LandscapeNetwork;
import data.C_Event;
import data.C_Parameters;
import data.constants.I_ConstantTransportation;

/** Initialize the simulation and manage the inputs coming from the events file.csv
 * @author J.Le Fur, P.A.Mboup 07/2012, rev. PAM 08.2013, JLF 09.2014,10.2015 */
public abstract class A_ProtocolTransportation extends A_Protocol implements I_ConstantTransportation {
	//
	// FIELD
	//
	public C_InspectorTransportation inspectorTransportation;// initialized in daughter protocols
	//
	// CONSTRUCTOR
	//
	public A_ProtocolTransportation(Context<Object> ctxt) {
		super(ctxt);
		this.facilityMap = new C_Background(-.46, 44, 31);
		this.inspectorTransportation = new C_InspectorTransportation();
		this.inspectorList.add(inspectorTransportation);
		C_CustomPanelSet.addTransportationInspector(inspectorTransportation);
	}
	//
	// METHODS
	//
	@Override
	/** Breeding season last all year long for commensal rodents (tempered with bioclimates in centenal) */
	protected boolean checkBreedingSeason() {
		return (A_Protocol.breedingSeason = true);
	}
	@Override
	protected void initLandscape(Context<Object> context) {
		this.setLandscape(new C_LandscapeCountry(context, C_Parameters.RASTER_URL, VALUE_LAYER_NAME,
				CONTINUOUS_SPACE_NAME));
	}

	@Override
	/** Remove dead carriers and rat beyond cities carrying capacities, step the transportation inspector */
	public void step_Utick() {
		// inspectorTransportation.checkLandPlotLists();// TODO JLF Slow simulation however, useful for some undetected ;-) bugs
		removeRatSurplus();
		super.step_Utick(); // has to be after the other inspectors step since it records indicators in file
	}
	/** For each city: remove (kill) rodents until they do not exceed the urban population carryingCapacity <br>
	 * Version JLF 03,04.2015, PAM 02.2016, jlf 01.2018
	 * @see C_City#getCarryingCapacity_Urodent() */
	protected void removeRatSurplus() {
		TreeSet<C_City> cityList = this.inspectorTransportation.getCityList();
		for (C_City oneCity : cityList) {
			// Compute the surplus (included within trucks) and kill rats exceeding
			int surplus = oneCity.getFullLoad_Urodent() - oneCity.getCarryingCapacity_Urodent();
			if (surplus > 0) {
				TreeSet<C_Rodent> oneCityRodentList = oneCity.getFullRodentList();
				Iterator<C_Rodent> iterator = oneCityRodentList.iterator();
				for (int i = 0; i < surplus; i++) {
					C_Rodent x = iterator.next();
					x.checkDeath(1.);
				}
				if (C_Parameters.VERBOSE)
					A_Protocol.event("A_ProtocolTransportation.removeRatSurplus",
							"Transportation protocol step: removed " + surplus + " rodents (" + oneCity
									.getFullLoad_Urodent() + "/" + oneCity.getHumanPopSize_Uindividual() + ") in "
									+ oneCity, false);
			}
		}
	}
	/** Do super, then check superAgents size,check loading probability, reset vehicles if time step has changed<br>
	 * rev. JLF 09.2017 */
	@Override
	public void readUserParameters() {
		String oldTickUnit = C_Parameters.TICK_UNIT_Ucalendar;
		int oldTickLength = C_Parameters.TICK_LENGTH_Ucalendar;
		super.readUserParameters();
		// SUPER AGENTS
		C_Parameters.RODENT_SUPER_AGENT_SIZE = ((Integer) C_Parameters.parameters.getValue("RODENT_SUPER_AGENT_SIZE"))
				.intValue();
		C_Parameters.HUMAN_SUPER_AGENT_SIZE = ((Integer) C_Parameters.parameters.getValue("HUMAN_SUPER_AGENT_SIZE"))
				.intValue();
		// DEFAULT VALUES
		double oldLoadingProb = C_Parameters.VEHICLE_LOADING_PROBA_DIVIDER;// store the old value to detect if it has changed
		C_Parameters.VEHICLE_LOADING_PROBA_DIVIDER = ((Integer) C_Parameters.parameters.getValue(
				"VEHICLE_LOADING_PROBA_DIVIDER")).intValue();
		if (C_Parameters.VEHICLE_LOADING_PROBA_DIVIDER != oldLoadingProb && RepastEssentials.GetTickCount() >= 0) {
			// Check user change of the vehicle loading probability and re-init vehicle if yes
			for (C_HumanCarrier carrier : inspectorTransportation.getCarrierList())
				carrier.getVehicle().initParameters();
			A_Protocol.event("A_Protocol.readUserParameters", "New loading probability rate: " + oldLoadingProb + " -> "
					+ C_Parameters.VEHICLE_LOADING_PROBA_DIVIDER, isNotError);
		}
		// NB: C_Parameters.parameters = RunEnvironment.getInstance().getParameters();
		if ((C_Parameters.TICK_LENGTH_Ucalendar != oldTickLength)
				|| (C_Parameters.TICK_UNIT_Ucalendar != oldTickUnit)) {
			// Re-compute speed of human carriers' vehicles / NB: time converter is already reset in
			// A_Protocol.readUserParameters()
			for (C_HumanCarrier carrier : inspectorTransportation.getCarrierList())
				carrier.getVehicle().initParameters();
		}
	}
	/** Update landplots and graphs if necessary after reading new events
	 * @see A_Protocol#updateUniverseFromChrono()
	 * @see manageEvent */
	@Override
	protected void manageReadEventTypes(TreeSet<String> changedEventTypes) {
		// BELOW: Update landplots _and_ graphs when they have been modified.
		// Snippet placed here since many cells of a given ground type may have been updated at the same time
		// Various types of ground event (city, road, GNT...) are successively dealt with
		for (String eventType : changedEventTypes) {
			// if (eventType.equals(CITY)) inspectorTransportation.addCityToOutputHeader(null);
			// if ((eventType.equals(GOOD_TRACK)) || (eventType.equals(TRACK))) eventType = ROAD;
			// When ground has been modified; if area has been modified, reset all areas
			if (GROUND_TYPE_CODES.containsKey(eventType)) {// TODO JLF 2014.09 patch for centenal GNT (eventTypes are added in
				// protocol), should be revised
				if (((C_LandscapeNetwork) this.landscape).getAreaTypes().contains(eventType)) {
					for (String areaType : ((C_LandscapeNetwork) this.landscape).getAreaTypes()) {
						// 1) sets the landPlot list from the rasterGraph 2) include the changes in the existing landplots
						TreeSet<C_LandPlot> landplotsOfAType = ((C_LandscapeNetwork) this.landscape)
								.identifyTypeLandPlots(areaType);
						updateLandPlots(landplotsOfAType, areaType);
					}
				}
				// 1) sets the landPlot list from the landscapeNetwork 2) include the changes in the existing landplots **
				TreeSet<C_LandPlot> landplotsOfAType = ((C_LandscapeNetwork) this.landscape).identifyTypeLandPlots(
						eventType);
				updateLandPlots(landplotsOfAType, eventType);
				// Update cities for each landplot
				for (C_LandPlot landplot : inspectorTransportation.getLandPlotList()) {
					if (eventType.equals(landplot.getPlotType()) && landplot instanceof C_Region) {
						((C_Region) landplot).computeCityList();
					}
				}
				// Update cities to trade for each human carrier after updating cities for each landplot
				for (C_HumanCarrier oneCarrier : inspectorTransportation.getCarrierList()) {
					if (eventType.equals(oneCarrier.getVehicle().getGraphType())) {
						TreeSet<C_City> citiesToTrade = getCitiesToTrade(oneCarrier.getCitiesToTradeInfo_Ustring(),
								(C_SoilCellGraphed) oneCarrier.getMyHome(), oneCarrier.getVehicle().getGraphType());
						if (!citiesToTrade.isEmpty()) oneCarrier.setCityList(citiesToTrade);
						else
							A_Protocol.event("A_ProtocolTransportation.manageReadEventTypes", oneCarrier + " in "
									+ oneCarrier.getCurrentSoilCell() + " cannot reset city list for " + oneCarrier
											.getCitiesToTradeInfo_Ustring() + ", keeps current: " + oneCarrier
													.getCityList().size() + " cities", isError);
					}
				}
				// inspector renew its cityList and update output Header (for city)
				if (eventType.equalsIgnoreCase(CITY_EVENT)) {
					inspectorTransportation.renewCityList();
				}
				if (((C_LandscapeNetwork) this.landscape).getGraphTypes().contains(eventType)) {
					// Update graph of the type
					((C_LandscapeNetwork) this.landscape).buildGraphsFromLandPlots(landplotsOfAType);
					if (C_Parameters.VERBOSE)
						A_Protocol.event("A_ProtocolTransportation.manageReadEventTypes", landplotsOfAType.size() + " "
								+ eventType + " graph(s) build or updated", isNotError);
					// Update all human carriers graph in this type (eventType) of graph
					for (C_HumanCarrier oneCarrier : inspectorTransportation.getCarrierList()) {
						if (eventType.equals(oneCarrier.getVehicle().getGraphType()))
							oneCarrier.pathWanderer.myGraph = ((C_SoilCellGraphed) oneCarrier.getCurrentSoilCell())
									.getGraph(oneCarrier.getVehicle().getGraphType());
					}
				}
				if (((C_LandscapeNetwork) this.landscape).getAreaTypes().contains(eventType)) {
					// Update graph of the type
					((C_LandscapeNetwork) this.landscape).buildGraphsFromLandPlots(landplotsOfAType, ROAD_EVENT);
					if (C_Parameters.VERBOSE)
						A_Protocol.event("A_ProtocolTransportation.manageReadEventTypes", eventType
								+ " graph build or updated: ", isNotError);
					// Update all human carriers graph in this type (eventType) of graph
					for (C_HumanCarrier oneCarrier : inspectorTransportation.getCarrierList()) {
						if (eventType.equals(oneCarrier.getVehicle().getGraphType()))
							oneCarrier.pathWanderer.myGraph = ((C_SoilCellGraphed) oneCarrier.getCurrentSoilCell())
									.getGraph(oneCarrier.getVehicle().getGraphType());
					}
				}
			}
		}
	}

	/** Update universe according to oneEventLine from Events <br />
	 * @param event from dataChrono Version rev JLF 12.2015 */
	@Override
	protected void manageOneEvent(C_Event event) {
		super.manageOneEvent(event);
		int x = event.whereX_Ucell, y = event.whereY_Ucell;
		C_SoilCell eventSC = (C_SoilCell) this.landscape.getGrid()[x][y];
		switch (event.type) {
			// OTHER GROUNDS
			case RIVER_EVENT :
			case RAIL_EVENT :
			case GNT_WEAK_EVENT :
			case GNT_MEDIUM_EVENT :
			case GNT_HEAVY_EVENT :
				manageGroundEvent(event);
				break;

			// CITIES
			case CITY_EVENT :
				// eventSC.setAffinity(EVENT_AFFINITY_CODES.get(event.type));//comment 10.2016 JLF
				manageGroundEvent(event);
				break;
			case MARKET_EVENT :
				manageGroundEvent(event);
				break;
			case TOWN_EVENT :
				// eventSC.setAffinity(EVENT_AFFINITY_CODES.get(event.type));//comment 10.2016 JLF
				manageGroundEvent(event);
				break;

			// ROADS
			case TRACK_EVENT :
				eventSC.getGroundTypes().add(event.type);
				if (eventSC.isOfGroundType(GOOD_TRACK_EVENT)) eventSC.getGroundTypes().remove(GOOD_TRACK_EVENT);// Case when a
																												// good track is
																												// abandoned
				if (eventSC.isOfGroundType(ROAD_EVENT)) eventSC.getGroundTypes().remove(ROAD_EVENT);// Case when a good track is
																									// abandoned
				event.type = ROAD_EVENT;
				manageGroundEvent(event);
				break;
			case GOOD_TRACK_EVENT :
				eventSC.getGroundTypes().add(event.type);
				// if track becomes good track then remove track in soil cell ground types
				if (eventSC.isOfGroundType(TRACK_EVENT)) eventSC.getGroundTypes().remove(TRACK_EVENT);
				event.type = ROAD_EVENT;
				manageGroundEvent(event);
				break;
			case ROAD_EVENT :
				// if track becomes road then remove track in soil cell ground types
				if (eventSC.isOfGroundType(TRACK_EVENT)) eventSC.getGroundTypes().remove(TRACK_EVENT);
				else if (eventSC.isOfGroundType(GOOD_TRACK_EVENT)) eventSC.getGroundTypes().remove(GOOD_TRACK_EVENT);
				manageGroundEvent(event);
				break;
			case TRUCK_EVENT :
			case TAXI_EVENT :
			case TRAIN_EVENT :
			case BOAT_EVENT :
				manageVehicleEvent(event);
				break;
			case POPULATION_EVENT :
				managePopulationEvent(event);
				break;
			default :
				break;
		}
	}

	protected void managePopulationEvent(C_Event event) {
		C_City city = inspectorTransportation.getCityByName(event.value1);
		if (city == null)
			A_Protocol.event("A_ProtocolTransportation.managePopulationEvent", "City " + event.value1
					+ " not found, hence event " + event + " is not accounted for.", isError);
		else
			city.setHumanPopSize_Uindividual(Integer.parseInt(event.value2));
	}

	/** 1/add the ground type to the event soil cell, 2/sets the ground type value in valuelayer2 (the map of groundtypes)
	 * @see C_LandscapeNetwork#graphedValueLayer author PA Mboup 2014, rev. JLF 09.2014 */
	protected void manageGroundEvent(C_Event event) {
		int x = event.whereX_Ucell, y = event.whereY_Ucell;
		C_SoilCell eventSC = (C_SoilCell) this.landscape.getGrid()[x][y];// SC for SoilCell
		String eventType = event.type;
		eventSC.getGroundTypes().add(eventType);// Assert ground type of the soilCell (one soilCell can have many groundType)
		if (eventType.equals(CITY_EVENT)) eventSC.getGroundTypes().add(TRACK_EVENT);// a market cell is also managed as a city
																					// cell
		if (eventType.equals(CITY_EVENT)) eventSC.getGroundTypes().add(ROAD_EVENT);// a market cell is also managed as a city cell
		if (eventType.equals(CITY_EVENT)) eventSC.getGroundTypes().add(RAIL_EVENT);// a market cell is also managed as a city cell

		// NAME THE CELL: event name or plot type followed with ground name
		// separator followed with cell's unique ID.
		if (eventType.equals(CITY_EVENT) || !eventSC.getGroundTypes().contains(CITY_EVENT)) {
			if (event.value1.length() != 0) {
				if (event.value2.length() != 0)
					eventSC.setThisName(event.value2 + "[" + event.value1 + "]" + NAMES_SEPARATOR + eventSC
							.retrieveId());
				else
					eventSC.setThisName(event.value1 + NAMES_SEPARATOR + eventSC.retrieveId());
			}
			else eventSC.setThisName(eventType + NAMES_SEPARATOR + eventSC.retrieveId());
		}

		// SET VALUE LAYER 2 in position (x,y) according to the following priority (for ground display):
		// city > town > rail > river > road > track > (GNT) weak > (GNT) medium > (GNT) heavy > (border, ocean, foreign
		// countries)
		int eventGroundTypeCode = GROUND_TYPE_CODES.get(eventType);
		// tracks are considered as road for graph computation, however, keep the track code for GUI colors display
		if (eventType.equals(ROAD_EVENT)) {
			if (eventSC.isOfGroundType(TRACK_EVENT)) eventGroundTypeCode = GROUND_TYPE_CODES.get(TRACK_EVENT);
			else
				if (eventSC.isOfGroundType(GOOD_TRACK_EVENT))
					eventGroundTypeCode = GROUND_TYPE_CODES.get(GOOD_TRACK_EVENT);
		}
		double currentGroundTypeColor = ((C_LandscapeNetwork) this.landscape).getGraphedValueLayer().get(x, y);
		// Patch to account for good tracks abandoned and returning to track quality; JLF 12.2015
		if ((currentGroundTypeColor == GROUND_TYPE_CODES.get(GOOD_TRACK_EVENT))
				&& (eventGroundTypeCode == GROUND_TYPE_CODES.get(TRACK_EVENT))) {
			((C_LandscapeNetwork) this.landscape).setGraphedValueLayer(eventGroundTypeCode, x, y);
		}
		else
			if (eventGroundTypeCode < currentGroundTypeColor)
				((C_LandscapeNetwork) this.landscape).setGraphedValueLayer(eventGroundTypeCode, x, y);
	}
	/** Create or remove a population of human carriers with a given vehicle
	 * @param event The event in this case is of the form: type of vehicle/number of human carriers to add/option: cities, area of
	 *            activity / author PA Mboup 2014, rev. JLF 09.2014, 10.2014 */
	protected void manageVehicleEvent(C_Event event) {
		int x = event.whereX_Ucell, y = event.whereY_Ucell;
		C_SoilCellGraphed eventSC = (C_SoilCellGraphed) this.landscape.getGrid()[x][y];// SC for SoilCell
		int nCarriers = Integer.parseInt(event.value1);
		if (Math.abs(nCarriers) > C_Parameters.HUMAN_SUPER_AGENT_SIZE)
			nCarriers = nCarriers / C_Parameters.HUMAN_SUPER_AGENT_SIZE;
		// boat or train which are single units JLF 09.2014
		else {
			if (nCarriers < 0) nCarriers = -1;
			else nCarriers = 1;
		}

		// CHECK compatibility between vehicle and C_SoilCell type
		String vehicleType = event.type;
		if (((vehicleType.equals(TRUCK_EVENT) || vehicleType.equals(TAXI_EVENT)) && !(eventSC.isOfGroundType(
				ROAD_EVENT))) || (vehicleType.equals(TRAIN_EVENT) && !eventSC.isOfGroundType(RAIL_EVENT))
				|| (vehicleType.equals(BOAT_EVENT) && !eventSC.isOfGroundType(RIVER_EVENT)) || !(eventSC.isOfGroundType(
						CITY_EVENT) || eventSC.isOfGroundType(TOWN_EVENT)))
			A_Protocol.event("A_ProtocolTransportation.manageVehicleEvent", eventSC + " is of type" + eventSC
					.getGroundTypes() + ", no " + vehicleType + " can be created there !", isError);
		// CHECK End

		else
			if ((eventSC.getLandPlot(TOWN_EVENT) != null) || (eventSC.getLandPlot(CITY_EVENT) != null)) {// if creation is within
																											// a city
				// PROCESS EVENT VALUE 2 - determine the cities to trade (when the
				// event has a second value (cities, area))
				String graphType = VEHICLE_SPECS.get(vehicleType)[GRAPH_TYPE_COL];
				TreeSet<C_City> citiesToTrade = getCitiesToTrade(event.value2, eventSC, graphType);// the set of
																									// cities(/accointances)
																									// known
				// TODO JLF 2017.01 test restrict trucks only in cities and not in markets
				if (event.type.equals(TRUCK_EVENT)) {
					citiesToTrade = (((C_Region) eventSC.getLandPlot(graphType)).getNoMarkets());
				}
				// PROCESS EVENT VALUE 1 - CARRIERS CREATION/REMOVAL
				// Add n carriers if n >= 0
				C_City targetCity = (C_City) eventSC.getLandPlot(CITY_EVENT);
				C_Graph graph = eventSC.getGraph(graphType);
				if (graph == null)
					System.err.println("A_ProtocolTransportation.manageVehicleEvent(), no " + graphType
							+ " graph for this " + vehicleType + " event in " + eventSC);
				if (nCarriers > 0) { // eventType : road, rail or river ...
					// count the number of cells of this graphType in the target
					// city
					TreeSet<C_SoilCellGraphed> graphCellsOfCity = targetCity.getGraphCells(graphType);
					int citySize_UcellGraphed = graphCellsOfCity.size();
					int nCarriersByCell = (nCarriers / citySize_UcellGraphed) + 1, restOfIntegerDivision = nCarriers
							% citySize_UcellGraphed;
					for (C_SoilCellGraphed cell : graphCellsOfCity) {
						if (restOfIntegerDivision == 0) nCarriersByCell--;
						restOfIntegerDivision--;
						for (int i = 1; i <= nCarriersByCell; i++) {
							createCarrier(cell, vehicleType, graph, citiesToTrade, event.value2);
						}
					}
				}
				// Delete n carriers if n < 0
				else removeCarrierPop(nCarriers, eventSC, vehicleType, citiesToTrade);
			}
			else
				A_Protocol.event("A_ProtocolTransportation.manageVehicleEvent", "No city at: " + eventSC + ", "
						+ nCarriers + " not created", isError);
	}
	/** Determine the list of cities or markets to trade given the content of eventValue2<br>
	 * In the case where there is a specific list of cities to trade, event value2 contains a list of cities in the form tag
	 * 'city' followed with ':city1:city2...' */
	public TreeSet<C_City> getCitiesToTrade(String eventValue2, C_SoilCellGraphed eventSC, String graphType) {
		String areaTypeToTrade = null;// in case of e.g., "ground nut trade area"
		TreeSet<C_City> citiesToTrade = new TreeSet<C_City>();
		if (eventValue2.length() != 0) {
			// Case: list of cities
			String tagOfValue2 = eventValue2.split(EVENT_VALUE2_FIELD_SEPARATOR)[0];// retrieve tag city or areaType
			if ((tagOfValue2.equals(CITY_EVENT) || (tagOfValue2.equals(TOWN_EVENT)))) {// Convert the string into a list of C_City
				String[] stringCities = eventValue2.split(EVENT_VALUE2_FIELD_SEPARATOR);
				for (int i = 1; i < stringCities.length; i++) {// do not count the first value ("city" tag)
					String cityString = stringCities[i];
					citiesToTrade.add(inspectorTransportation.getCityByName(cityString));
				}
			}
			// Case: markets
			else
				if (tagOfValue2.equals(MARKET_EVENT))
					citiesToTrade.addAll(((C_Region) eventSC.getLandPlot(graphType)).getMarkets());
				// Case: a trade area ex GNT
				else
					if (((C_LandscapeNetwork) this.landscape).getAreaTypes().contains(tagOfValue2)) {
						if (eventSC.getLandPlot(tagOfValue2) == null) {
							A_Protocol.event("A_ProtocolTransportation.getCitiesToTrade", "Will not be able to reset "
									+ tagOfValue2 + " city list of hCarrier in " + eventSC + " which is not part of "
									+ tagOfValue2 + " area.", isError);
						}
						else {
							areaTypeToTrade = eventValue2.split(EVENT_VALUE2_FIELD_SEPARATOR)[0];
							for (C_LandPlot oneLandPlotGNT : inspectorTransportation.getLandPlotList(areaTypeToTrade)) {
								citiesToTrade.addAll(((C_Region) oneLandPlotGNT).getCityList());
							}
						}
					}
					else
						A_Protocol.event("A_ProtocolTransportation.getCitiesToTrade",
								" soilCell does not match with event value 2 : " + eventValue2
										+ "; citiesToTrade list is empty. SC : " + eventSC, isError);
		}
		// Simplest case, e.g., event occurs on a road and retrieve all cities within this plot
		else citiesToTrade = ((C_Region) eventSC.getLandPlot(graphType)).getCityList();
		return citiesToTrade;
	}

	/** create one humanCarrier of a vehicle type and positions it in a city */
	protected void createCarrier(C_SoilCell cell, String vehicleType, C_Graph graph, TreeSet<C_City> citiesToTrade,
			String citiesToTrade_Ustring) {
		C_HumanCarrier oneCarrier;
		if (vehicleType.equals(TAXI_EVENT)) oneCarrier = new C_TaxiMan(new C_GenomeAmniota());
		else oneCarrier = new C_HumanCarrier(new C_GenomeAmniota());
		// Declares a new object in the context and positions it within the raster ground
		contextualizeNewThingInContainer(oneCarrier, cell);
		oneCarrier.setMyHome(cell);
		oneCarrier.ownVehicle(vehicleType);
		oneCarrier.pathWanderer.targetSoilCell = (C_SoilCellGraphed) oneCarrier.getCurrentSoilCell();
		oneCarrier.pathWanderer.myGraph = graph;
		// TODO JLF 2017.01 patch
		if (vehicleType.equals(TRUCK_EVENT)) for (C_City oneCity : citiesToTrade)
			if (!(oneCity instanceof C_Market)) oneCarrier.getCityList().add(oneCity);
		oneCarrier.setCityList(citiesToTrade);
		oneCarrier.setCitiesToTradeInfo_Ustring(citiesToTrade_Ustring);
		inspectorTransportation.addCarrier(oneCarrier);
	}
	/** Update the list of all landPlots in the groundType in parameter: the code compares the old landplot list and the new
	 * computed landplot list. Can update, shorten or extend landPlot and group several landPlots in one single; can update a
	 * landPlot divided into two or several (divide the road into several roads is to shorten it and add others); but can not yet
	 * destroy a road that does not exist anymore. <br />
	 * This method should only be called after building or rebuilding a groundType landPlots with identifyTypeLandPlots() method
	 * with new landPlots from identifyTypeLandPlots(), this method update the old one and add news. <br />
	 * An old landPlot and a new are the same if 2 conditions : 1- they get a same groundType; 2- they own a cell in common <br/>
	 * After this call we must call updateLandPlotCitiesList() to update for each updated LP the cities list associated
	 * @see thing.ground.landscape.C_LandscapeNetwork#identifyTypeLandPlots <br>
	 * @author P.A. Mboup, 2013, rev. JLF 09.2014 */
	// TODO JLF 2014.09 should be placed in A_Protocol ?
	protected void updateLandPlots(TreeSet<C_LandPlot> newLandPlots, String groundType) {
		// Recover all existing landPlots of this groundType
		TreeSet<C_LandPlot> currentLandPlotsOfAType = inspectorTransportation.getLandPlotList(groundType);
		// If this ground type has no recorded landplot, a type is created and filled with the new land plot list, nothing more.
		if (currentLandPlotsOfAType.size() == 0) {
			for (C_LandPlot aNewLandPlot : newLandPlots)
				createLandPlot(aNewLandPlot);
		}
		// Else update the landplots for the given ground type
		else {
			TreeSet<C_SoilCell> newLandPlotCells;
			// For each new landplot, if it already exist or is an extension of an existing one, update the old one
			for (C_LandPlot oneNewLandPlot : newLandPlots) {
				newLandPlotCells = oneNewLandPlot.getCells();
				C_LandPlot updatedLandPlot = null; //
				// On regarde les old landPlots é qui il correspond
				for (C_LandPlot oneOldLandPlot : currentLandPlotsOfAType) {
					for (C_SoilCell soilCell_1 : oneOldLandPlot.getCells()) {
						// S'il correspond é celui ci (méme groundType et un SC en commun)
						if (oneNewLandPlot.getPlotType().equals(oneOldLandPlot.getPlotType()) && newLandPlotCells
								.contains(soilCell_1)) {
							if (updatedLandPlot == null) {
								// Update this landplot; first remove its
								// soilcells
								for (C_SoilCell soilCell_2 : oneOldLandPlot.getCells()) {
									((C_SoilCellGraphed) soilCell_2).removeLandPlotByGroundType(groundType);
									((C_SoilCellGraphed) soilCell_2).removeGroundType(groundType);
								}
								oneOldLandPlot.getCells().clear();
								// Rebuild the old landplot with the soil cell of the new one (keeping the references agents know)
								for (C_SoilCell soilCell_3 : oneNewLandPlot.getCells()) {
									((C_SoilCellGraphed) soilCell_3).setLandPlotByGroundType(groundType,
											oneOldLandPlot);
									((C_SoilCellGraphed) soilCell_3).addGroundType(groundType);
								}
								updatedLandPlot = oneOldLandPlot;
							}
							// else: one old landplot has already be updated, then soil cells have already been merged. Therefore,
							// remove this one
							else {
								for (C_SoilCell sc : oneOldLandPlot.getCells())
									if (((C_SoilCellGraphed) sc).getLandPlot(groundType) == oneOldLandPlot) {
										((C_SoilCellGraphed) sc).removeLandPlotByGroundType(groundType);
										((C_SoilCellGraphed) sc).removeGroundType(groundType);
									}
								oneOldLandPlot.getCells().clear();
							}
							break; // one soil cell in common is enough for the landplots to be the same
						}
					}
				}
				// If the new lp != an old lp, then add a new lp in the list and the context
				if (updatedLandPlot == null) createLandPlot(oneNewLandPlot);
			}
		}
	}

	/** Name the landplot, position it in the context, inform the inspector */
	protected void createLandPlot(C_LandPlot newLandPlot) {
		C_SoilCell firstCell = newLandPlot.getCells().first();
		newLandPlot.setThisName("");
		// landplot situated at its first cell's coordinates
		contextualizeNewThingInContainer(newLandPlot, firstCell);
		// JLF 09.2016 if a city is given coordinates in degrees in the chronogram, stor it into the corresponding cell
		// bornCoordinate and use it to position the landplot
		if (firstCell.bornCoord_Umeter != null) this.landscape.moveToLocation(newLandPlot, firstCell.bornCoord_Umeter);
		// using contextualizeNewAgentInCell adds the landplot in the cell's occupantList, it has to be removed.
		firstCell.agentLeaving(newLandPlot);
		inspectorTransportation.addLandPlot(newLandPlot);
	}

	//
	// SETTERS & GETTERS
	//
	/** Mark carriers to destroy in carriers with a specify citiesList or in a landPlot M
	 * @param nCarriers
	 * @param VehicleType
	 * @param citiesToTrade must be null if xy is given
	 * @return the number of carrier not marked mustDie */
	public int removeCarrierPop(int nCarriers, C_SoilCellGraphed eventCell, String VehicleType,
			Set<C_City> citiesToTrade) {
		if (nCarriers < 0) nCarriers = -nCarriers;
		// Pour détruire des carriers avec une liste de villes spécifiées
		if (citiesToTrade != null) {
			int n = citiesToTrade.size();
			for (C_HumanCarrier oneCarrier : inspectorTransportation.getCarrierList()) {
				if (nCarriers == 0) return nCarriers; // ou break;
				if (!oneCarrier.isDead() // is not must die yet
						&& oneCarrier.getVehicle().getType().equals(VehicleType) // same vehicle type
						&& n == oneCarrier.getCityList().size() // same number of city
						&& oneCarrier.getCityList().containsAll(citiesToTrade)) { // same list of cities
					oneCarrier.setDead(true);
					nCarriers--;
				}
			}
		}
		// Pour détruire des carriers sur un landPlot (de type route, rail, river ...)
		else {// remove carriers on the same landplot, select the landplot corresponding to the vehicleType (which is unique)
			C_Region lp = (C_Region) eventCell.getLandPlot(VEHICLE_SPECS.get(VehicleType)[GRAPH_TYPE_COL]);
			TreeSet<C_HumanCarrier> carrierList = lp.getCarriers();
			for (C_HumanCarrier oneCarrier : carrierList) {
				if (nCarriers == 0) return nCarriers; // ou break;
				if (!oneCarrier.isDead()) {
					oneCarrier.setDead(true);
					nCarriers--;
				}
			}
		}
		return nCarriers;
	}

	/** Create a set of rodents and place it in a city + distribute equally between city's soil cells
	 * @param nbRodents
	 * @param city */
	protected void addRodentsInCity(int nbRodents, C_City city) {
		// Dans une ville en distribuant le nombre de rats sur le nombre de SC
		TreeSet<C_SoilCell> cellList = city.getCells();
		int cityCellSize = cellList.size();
		int n = (nbRodents / cityCellSize) + 1;
		int r = nbRodents % cityCellSize;
		for (int j = 0; j < cityCellSize; j++) {
			if (r == 0) n--; // done only once
			r--;
			C_SoilCell oneSC = city.retrieveOneCell(j);
			for (int i = 1; i <= n; i++) {
				C_Rodent oneRodent = createRodent();
				oneRodent.setRandomAge();
				contextualizeNewThingInContainer(oneRodent, oneSC);
			}
		}
	}
	/** Mark rodents to destroy in a soilcell or a city
	 * @param x soilcell line
	 * @param y soilcell colomne
	 * @param nbOfRodentsToDelete
	 * @param city optional
	 * @return the number of rodents not marked mustDie */
	public int deleteRodents(int x, int y, int nbOfRodentsToDelete, C_City... city) {
		if (nbOfRodentsToDelete < 0) nbOfRodentsToDelete = -nbOfRodentsToDelete;
		if (city.length != 0) {
			TreeSet<C_SoilCell> cellList = city[0].getCells();
			int cityCellSize = cellList.size(), i = 0, n, r;
			int j = 0, j0 = 0;
			while (nbOfRodentsToDelete > 0) {
				// int division +1, n = n rodents to remove +/-1
				n = (nbOfRodentsToDelete / cityCellSize) + 1;
				r = nbOfRodentsToDelete % cityCellSize; // r = remaining of the integer division
				for (j = j0; j < cityCellSize; j++) { // For each cell
					j0++;
					// parce qu'il avait + 1 Ex: n= 15/4 =3 r=3; r!=0 => n=4. Dans les 3 1eres cell je mets 4, et quand r==0 (4th
					// cell) je met 3(n-1)
					if (r == 0) n = n - 1;
					r--;
					i = 0;
					TreeSet<C_Rodent> rodentList = ((C_SoilCellGraphed) city[0].retrieveOneCell(j)).getFullRodentList();
					for (C_Rodent oneRodent : rodentList) {
						if (!oneRodent.isDead()) {
							oneRodent.setDead(true);
							nbOfRodentsToDelete--;
							i++;
						}
						if (i == n) break; // If we reach the number of rats to destroy (i == n), break for this cell
					}
					// If n rats have not been destroyed in this cell => repeat the calculation pr n and r.
					//j0 will allow us to skip the cells already affected
					if (i < n) break;
				}
				// if nbRats is exhausted or if the city has no more rats to destroy
				if (nbOfRodentsToDelete == 0 || j0 == j) break;
			}
		}
		// To destroy rodents in a soilCell
		else {
			TreeSet<C_Rodent> rodentList = ((C_SoilCellGraphed) this.landscape.getGrid()[x][y]).getFullRodentList();
			if (rodentList != null) for (C_Rodent oneRodent : rodentList) {
				if (!oneRodent.isDead()) {
					oneRodent.setDead(true);
					nbOfRodentsToDelete--;
				}
				if (nbOfRodentsToDelete == 0) break;
			}
		}
		// To return the number of rats that could not be destroyed
		return nbOfRodentsToDelete;
	}

	/** This method must be redefined in daughter protocols */
	protected Map<String, String[]> getVehicleSpecs() {
		return VEHICLE_SPECS;
	}
}