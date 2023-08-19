/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package simmasto0.protocol;
import java.awt.Dimension;
import java.util.Calendar;
import java.util.Date;
import java.util.List; // TODO PAM de JLF 2017.02 pourquoi pas un treeset ?
import java.util.TimeZone;
import java.util.TreeSet;

import presentation.dataOutput.C_FileWriter;
import presentation.display.C_Background;
import presentation.display.C_CustomPanelSet;
import presentation.display.C_StyleAgent;
import presentation.epiphyte.A_Inspector;
import presentation.epiphyte.C_InspectorEnergy;
import presentation.epiphyte.C_InspectorPopulation;
import presentation.epiphyte.I_Inspector;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import repast.simphony.util.collections.IndexedIterable;
import simmasto0.C_Calendar;
import simmasto0.C_ContextCreator;
import simmasto0.util.C_VariousUtilities;
import simmasto0.util.C_sound;
import thing.A_Animal;
import thing.A_NDS;
import thing.A_VisibleAgent;
import thing.C_Rodent;
import thing.I_SituatedThing;
import thing.dna.C_GenomeAmniota;
import thing.ground.I_Container;
import thing.ground.landscape.C_Landscape;

import com.vividsolutions.jts.geom.Coordinate;

import data.C_Chronogram;
import data.C_Event;
import data.C_Parameters;
import data.constants.I_ConstantNumeric;
import data.constants.I_ConstantString;
import data.converters.C_ConvertTimeAndSpace;
/** Master class for the various simmasto0.protocol of the SimMasto platform
 * @author Jean Le Fur & Pape Adama Mboup 07.2012, rev. JLF 02.2013, 08.2014, 10.2014, 03.2021 */
public abstract class A_Protocol implements I_Protocol, I_ConstantString, I_ConstantNumeric {
	//
	// FIELDS
	//
	public static C_InspectorPopulation inspector = null;
	public static C_InspectorEnergy inspectorEnergy = null;
	// TODO JLF 2014.08, 2015.03 Multiscale contexts: several protocols with their own calendar should run concurrently.
	// The field should thus not be static
	public static C_Calendar protocolCalendar = null;
	public C_Landscape landscape = null;// TODO JLF 2015.12 ->I_container landscape
	protected Context<Object> context = null;
	protected TreeSet<A_Inspector> inspectorList = null;
	protected C_FileWriter indicatorsFile = null; // TODO JLF 2014.10 output file must be managed by inspectors (epiphyte system)
	protected static Boolean breedingSeason = null;// TODO JLF 2014.10 put in other place ? (specific to species/environment)
	protected C_Chronogram chronogram = null;// Contains the whole chrono from the csv file
	protected C_Background facilityMap = null;// used for displaying a bitmap over the grid
	protected C_StyleAgent styleAgent;
	//
	// CONSTRUCTOR
	//
	public A_Protocol(Context<Object> ctxt) {
		this.context = ctxt;
		// CALENDAR
		A_Protocol.protocolCalendar = new C_Calendar();
		initCalendar();// sets the date for the beginning of the simulation
		System.out.println("A_Protocol.constructor(): simulation starts on " + protocolCalendar.stringHourDate());
		// INSPECTORS
		A_Protocol.inspector = new C_InspectorPopulation();
		A_Protocol.inspectorEnergy = new C_InspectorEnergy();
		this.inspectorList = new TreeSet<A_Inspector>();
		this.inspectorList.add(inspector);
		this.inspectorList.add(inspectorEnergy);
		this.initFixedParameters();

		this.readUserParameters();
		A_Protocol.breedingSeason = false;// TODO JLF 2015.03 misplaced
		this.indicatorsFile = new C_FileWriter("Indicateurs.csv", true);
		// Initialization of the ground manager Author: LeFur 07.2012, rev Mboup 2013, Diakhate 2014
		A_Protocol.event("A_Protocol()", " raster: " + C_Parameters.RASTER_URL, isNotError);
		this.initLandscape(ctxt);
		A_Protocol.event("A_Protocol()", " identifying land plots ...", isNotError);
		this.landscape.identifyAffinityLandPlots(ctxt);
		A_VisibleAgent.init(this.landscape);
		A_Protocol.event("A_Protocol()", " Initialized visible AGENT class with their landscape ", isNotError);
		C_CustomPanelSet.addEnergyInspector(inspectorEnergy);
	}
	//
	// METHODS
	//
	/** The contact structure (term coined from S.E.Page: Diversity and Complexity) */
	protected void initLandscape(Context<Object> context) {
		this.setLandscape(new C_Landscape(context, C_Parameters.RASTER_URL, VALUE_LAYER_NAME, CONTINUOUS_SPACE_NAME));
	}
	/** In short: make this method first inherit its daughters at startup, then proceed to super (which is not the case within the
	 * constructor (super must be first there)<br>
	 * Longer: Triggered by the context creator at the beginning of the simulation and after constructing the protocol<br>
	 * The procedure can be realized only after all the CASCADING INSPECTORS and headers have been defined in the subclass
	 * hierarchy i.e., contains procedure that cannot be put in the constructor to avoid the overload when subclasses use super in
	 * their constructor */
	public void initProtocol() {
		A_Protocol.event("A_Protocol().initProtocol()", C_ContextCreator.INSPECTOR_NUMBER + " inspectors defined: ",
				isNotError);
		recordHeadersInFile();
		if (C_Parameters.DISPLAY_MAP && this.facilityMap != null)
			this.facilityMap.contextualize(this.context, this.landscape);
	}
	/** The clock of the simulation (live and let die): provide each A_NDS a tick<br>
	 * Check sizes of the rodents'list, manage time stuff, proceed to all NDSs' step, updates indicators, remove dead agents<br>
	 * Version Author J.Le Fur 2012, rev. 02.2013, 08.2014, 07.2015, 12.2015, 05.2016, 01.2018<br>
	 * Tip: Object[] contextContent = RunState.getInstance().getMasterContext().toArray(); */
	@ScheduledMethod(start = 0, interval = 1, shuffle = false)
	public void step_Utick() {
		A_NDS oneAgent;
		// checkRodentLists();// TODO JLF Slow simulation however, useful for some undetected ;-) bugs

		// Manage chrono events if this protocol uses a chrono event file.
		if (chronogram != null) this.updateUniverseFromChrono();
		this.manageTimeLandmarks();
		// LIVE AND LET DIE //
		this.removeDeadThings();// Kind of garbage collector for things tagged dead
		IndexedIterable<Object> it = this.context.getObjects(A_NDS.class);
		for (int i = 0; i < it.size(); i++) {
			oneAgent = (A_NDS) it.get(i);
			oneAgent.step_Utick();
		}

		// MANAGE EPIPHYTE SYSTEM
		for (A_Inspector oneInspector : inspectorList) oneInspector.step_Utick();
		this.recordIndicatorsInFile(); // Manage indicators file

		if (C_Parameters.TERMINATE || isSimulationEnd()) haltSimulation(); // and close files
	}

	/** Update the universe according to oneEventLine from Events <br />
	 * @param event from dataChrono */
	protected void manageOneEvent(C_Event event) {}

	/** Manage time, breeding season, agricultural changes <br>
	 * include read user parameters<br>
	 * Version Authors JEL2011, AR2011, rev. LeFur 2011,2012,04.2014,08.2014,09.2014 */
	public void manageTimeLandmarks() {
		// int currentYear = A_Protocol.protocolCalendar.get(Calendar.YEAR);
		A_Protocol.protocolCalendar.incrementDate();
		// Beep if start or end of reproduction season
		if (A_Protocol.breedingSeason != this.checkBreedingSeason())
			A_Protocol.event("A_Protocol.manageTimeLandmarks", "Start/end of breeding season", isNotError);
		// Check if map has to be switched Version JLF 08.2014, rev.10.2015, 05.2017
		boolean displayMapBefore = C_Parameters.DISPLAY_MAP;
		this.readUserParameters();
		if (displayMapBefore != C_Parameters.DISPLAY_MAP) switchDisplayMap();
		// if (protocolCalendar.get(Calendar.YEAR) != currentYear) {
		// this.landscape.resetCellsColor();
		// if (C_Parameters.VERBOSE) C_sound.sound("tip.wav");
		// }
	}
	/** Update the environment with every events (chrono) occurred at the current date.<br>
	 * Triggers event management for each new event and all event types that occurred
	 * @see #manageOneEvent
	 * @see #manageReadEventTypes */
	protected void updateUniverseFromChrono() {
		// Chronogram tests dates, retrieves genuine events, processes the string stuff, fills in a TreeSet of C_Event.
		List<C_Event> readEvents = chronogram.retrieveCurrentEvents(protocolCalendar.getTime());
		if (readEvents != null) {
			TreeSet<String> eventTypes = new TreeSet<String>();
			int nbEvents = 0;
			Date date = readEvents.get(0).when_Ucalendar;
			for (C_Event oneEvent : readEvents) {
				if (!date.equals(oneEvent.when_Ucalendar)) {
					manageReadEventTypes(eventTypes);
					date = oneEvent.when_Ucalendar;
					eventTypes.clear();
				}
				// oneEvent.type can change after passing through manageOneEvent()
				// that's why "manageOneEvent(oneEvent);" is before "eventTypes.add(oneEvent.type);"
				manageOneEvent(oneEvent); // TODO PAM 2015.12 touché: lignes permutées
				eventTypes.add(oneEvent.type);
				nbEvents++;
			}
			manageReadEventTypes(eventTypes);
			// print only if events occurred
			if (nbEvents > 0) {
				A_Protocol.event("A_Protocol.updateUniverseFromChrono", "CHRONOGRAM event: " + nbEvents + " "
						+ " event(s) read of type(s) " + eventTypes, isNotError);
				this.landscape.resetCellsColor();
			}
		}
	}
	/** This method must be redefined in daughter protocols */
	protected void manageReadEventTypes(TreeSet<String> eventTypes) {}
	/** Breeding season declaration : also manages situation where REPRO_START_Umonth > REPRO_END_Umonth */
	protected boolean checkBreedingSeason() {
		if (C_Parameters.REPRO_START_Umonth < C_Parameters.REPRO_END_Umonth) {
			if (protocolCalendar.get(Calendar.MONTH) >= C_Parameters.REPRO_START_Umonth && protocolCalendar.get(
					Calendar.MONTH) <= C_Parameters.REPRO_END_Umonth) return (A_Protocol.breedingSeason = true);
		}
		return (A_Protocol.breedingSeason = false);
	}
	/** Declare a new object in the context and positions it within the raster ground
	 * @see #contextualizeNewThingInContainer */
	public void contextualizeNewThingInSpace(I_SituatedThing thing, double x, double y) {
		contextualizeNewThingInGrid(thing, (int) x, (int) y);
		this.landscape.moveToLocation(thing, new Coordinate(x, y));
		if (thing instanceof A_VisibleAgent)
			((A_VisibleAgent) thing).bornCoord_Umeter = this.landscape.getThingCoord_Umeter(thing);
	}
	/** Declare a new object in the context and positions it within the raster ground
	 * @see #contextualizeNewThingInContainer */
	protected void contextualizeNewThingInGrid(I_SituatedThing thing, int line_Ucell, int col_Ucell) {
		I_Container cell = this.landscape.getGrid()[line_Ucell][col_Ucell];
		contextualizeNewThingInContainer(thing, cell);
	}

	/** Declare a new object in the context and positions it within the raster ground */
	public void contextualizeNewThingInContainer(I_SituatedThing thing, I_Container container) {
		if (!context.contains(thing)) {
			context.add(thing);
			this.landscape.moveToLocation(thing, container.getCoordinate_Ucs());
			container.agentIncoming(thing);
			if (thing instanceof A_VisibleAgent)
				((A_VisibleAgent) thing).bornCoord_Umeter = this.landscape.getThingCoord_Umeter(thing);
			if (thing instanceof C_Rodent) C_InspectorPopulation.addRodentToList((C_Rodent) thing);
			if (thing instanceof A_Animal) ((A_Animal) thing).setMyHome(container);
		}
		else
			A_Protocol.event("A_Protocol.contextualizeNewAgentInCell", ((A_NDS) thing).retrieveMyName() + "/"
					+ ((A_NDS) thing).retrieveId() + " already exist in context", isError);
	}
	/** @see contextualizeOldThingInCell with x and y as coordinates instead of cell reference<br>
	 *      M. Sall 11.2018 */
	public void contextualizeOldThingInSpace(I_SituatedThing thing, double x, double y) {
		this.landscape.moveToLocation(thing, new Coordinate(x, y));
		this.contextualizeOldThingInCell(thing, this.landscape.getGrid()[(int) x][(int) y]);
	}
	public void contextualizeOldThingInCell(I_SituatedThing thing, I_Container cell) {
		this.landscape.moveToContainer(thing, cell);
	}
	/** LIVE AND LET DIE AGENTS 2/ RODENTS'DEATH PROCEDURE<br>
	 * To avoid concurrent modification exception as well as scrambling the order of rodents, all death procedure mark the rodent
	 * as having to die. The death and remove of dead rodents is then proceeded in one shot <br>
	 * @see A_Protocol#step_Utick
	 * @revision Author J.Le Fur 2012, rev. 02.2013, 12.2015, 04.2016 */
	protected int removeDeadThings() {
		Object[] things = this.context.toArray();// needed to avoid concurrent modification exception
		int nbDeath = 0, nbDeadRodents = 0;
		for (Object oneThing : things) {
			if ((oneThing instanceof A_NDS)) {
				if (((A_NDS) oneThing).isDead()) {
					if (oneThing instanceof C_Rodent) nbDeadRodents++;
					if (wipeOffObject((I_SituatedThing) oneThing)) nbDeath++; // i.e., if remove succeeded
					else A_Protocol.event("A_Protocol.removeDeadThings", "Cannot remove dead " + oneThing, isError);
				}
			}
		}
		A_Protocol.inspector.setNbDeath_Urodent(nbDeadRodents);
		return nbDeath;
	}
	/** Destroy a thing, remove it from context and remove its references to other object (lastContainerLeft, inspectors) so as to
	 * be garbage collected<br>
	 * Version Jean-Emmanuel Longueville 2011-01, rev. JLF 2014, 12.2015, 04.2016 */
	public boolean wipeOffObject(I_SituatedThing deadThing) {
		for (I_Inspector inspector : this.inspectorList) inspector.discardDeadThing(deadThing);
		deadThing.discardThis();
		// TODO JLF 2015.06 in multiple context should be: Context<Object> context = ContextUtils.getContext(agent);
		boolean test = this.context.remove(deadThing);
		if (!test) A_Protocol.event("A_Protocol.wipeOffObject", deadThing + " cannot be removed from context", isError);
		return test;
	}
	@Override
	/** Read user parameters on the GUI, check if temporal scale and cell size has changed, reset timeConverter if needed<br>
	 * rev. jlf 09.2017 */
	public void readUserParameters() {
		try {
			C_Parameters.INIT_RODENT_POP_SIZE = ((Integer) C_Parameters.parameters.getValue("INIT_POP_SIZE"))
					.intValue();
		}
		catch (Exception e) {
			C_Parameters.INIT_RODENT_POP_SIZE = 0;
		}
		/** If true, display the affinity map, else display the value layer */
		C_Parameters.DISPLAY_MAP = ((Boolean) C_Parameters.parameters.getValue("DISPLAY_MAP")).booleanValue();
		C_Parameters.VERBOSE = ((Boolean) C_Parameters.parameters.getValue("VERBOSE")).booleanValue();
		C_Parameters.TERMINATE = ((Boolean) C_Parameters.parameters.getValue("TERMINATE")).booleanValue();

		// Manage new cell size
		int oldCellSize = C_Parameters.CELL_WIDTH_Ucentimeter;
		C_Parameters.CELL_WIDTH_Ucentimeter = ((Integer) C_Parameters.parameters.getValue("CELL_WIDTH_Ucm")).intValue();
		if (oldCellSize != C_Parameters.CELL_WIDTH_Ucentimeter) {
			C_Parameters.CELL_WIDTH_Umeter = (double) C_Parameters.CELL_WIDTH_Ucentimeter / 100.;
			// Reset soilCells coordinates
			C_Parameters.UCS_WIDTH_Umeter = C_Parameters.CELL_WIDTH_Umeter;
			A_Protocol.event("A_ProtocolTransportation.readUserParameters", "New cell size definition: "
					+ C_Parameters.CELL_WIDTH_Umeter + " meters ", isNotError);
		}
		// DISPLAY ICONS OR NOT
		boolean oldValueImage = C_Parameters.IMAGE;
		C_Parameters.IMAGE = ((Boolean) C_Parameters.parameters.getValue("IMAGE")).booleanValue();
		if ((this.styleAgent != null) && (oldValueImage != C_Parameters.IMAGE)) {
			IndexedIterable<Object> it = this.context.getObjects(A_VisibleAgent.class);
			for (int i = 0; i < it.size(); i++) ((A_VisibleAgent) it.get(i)).setHasToSwitchFace(true);
			this.styleAgent.init(this.styleAgent.getFactory());
		}
		// Manage new tick definition
		String oldTickUnit = C_Parameters.TICK_UNIT_Ucalendar;
		int oldTickLength = C_Parameters.TICK_LENGTH_Ucalendar;
		C_Parameters.TICK_LENGTH_Ucalendar = ((Integer) C_Parameters.parameters.getValue("TICK_LENGTH_Ucalendar"))
				.intValue();
		C_Parameters.TICK_UNIT_Ucalendar = (String) C_Parameters.parameters.getValue("TICK_UNIT_Ucalendar");
		if ((C_Parameters.TICK_LENGTH_Ucalendar != oldTickLength)
				|| (C_Parameters.TICK_UNIT_Ucalendar != oldTickUnit)) {
			// Reset time converter
			C_ConvertTimeAndSpace.init(C_Parameters.TICK_LENGTH_Ucalendar, C_Parameters.TICK_UNIT_Ucalendar, "M");
			// Calendar has also to be reset to account for the new timeSpaceConverter.
			Date savedDate = A_Protocol.protocolCalendar.getTime();
			A_Protocol.protocolCalendar = new C_Calendar();
			A_Protocol.protocolCalendar.setTime(savedDate);
			A_Protocol.event("A_Protocol.readUserParameters", "New tick definition: "
					+ C_Parameters.TICK_LENGTH_Ucalendar + " " + C_Parameters.TICK_UNIT_Ucalendar, isNotError);
			// Use a treeSet to ensure that NDS are always triggered in the same order TODO JLF 2017.09 pas compris ???
			for (Object contextObject : this.context) if (contextObject instanceof A_Animal)
				((A_Animal) contextObject).initParameters();
		}
	}
	protected void initFixedParameters() {
		C_Parameters.EXCLOS = false;
		C_Parameters.TICK_MAX = 0;
		C_Parameters.MAX_POP = 10000; // TODO JLF 2015.10 remove ?
		// Default: reproduction all year long, has to be overriden
		C_Parameters.REPRO_START_Umonth = 0;
		C_Parameters.REPRO_END_Umonth = 11;
	}
	/** Fills the context with simple _wandering_ C_Rodent agents (as opposed to C_RodentFossorial's that dig burrows) <br>
	 * The sex ratio is randomly generated , rev. JLF 07.2014 currently unused */
	public void randomlyAddRodents(int nbAgent) {
		Dimension dim = this.landscape.getDimension_Ucell();
		int grid_width = (int) dim.getWidth();
		int grid_height = (int) dim.getHeight();
		for (int i = 0; i < nbAgent; i++) {
			// BELOW, THREE POSSIBLE PATTERNS OF INITIAL DISTRIBUTION :
			// 1) Random number to produce a sensitivity analysis
			// int randx = (int)(Math.random()*grid_width);
			// int randy = (int)(Math.random()*grid_height);
			// 2) Reproducible random distribution
			double randx = C_ContextCreator.randomGeneratorForInitialisation.nextDouble() * grid_width;
			double randy = C_ContextCreator.randomGeneratorForInitialisation.nextDouble() * grid_height;
			// 3) Put all rodents at the middle at init:
			// int randx = (int) (grid_width / 2);
			// int randy = (int) (grid_height / 2);
			C_Rodent agent = createRodent();
			agent.setRandomAge();
			contextualizeNewThingInSpace(agent, randx, randy);
			agent.setNewRandomMove();
		}
	}
	/** Recursively record the current state of every inspectors'indicators in one unique .csv indicatorsFile */
	public void recordIndicatorsInFile() {
		String indicatorValues = "";
		for (A_Inspector inspector : inspectorList) {
			indicatorValues += inspector.getIndicatorsValues();
			indicatorValues += CSV_FIELD_SEPARATOR;
		}
		// indicatorValues = indicatorsFile.getNumRun() + CSV_FIELD_SEPARATOR + indicatorValues;// Simultech 2018
		indicatorsFile.writeln(indicatorValues);
	}
	/** Record the current state of every inspectors'indicators in one unique .csv indicatorsFile */
	public void recordHeadersInFile() {
		String indicatorHeader = "";
		for (A_Inspector inspector : inspectorList) {
			indicatorHeader += inspector.getIndicatorsHeader();
			indicatorHeader += CSV_FIELD_SEPARATOR;
		}
		indicatorHeader += "NumRun";
		System.out.println("A_Protocol.recordHeadersInFile(): " + indicatorHeader);
		indicatorsFile.writeln(indicatorHeader);
	}
	public C_Rodent createRodent() {
		return new C_Rodent(new C_GenomeAmniota());
	}
	/** Check rodent list sizes */
	public void checkRodentLists() {
		TreeSet<C_Rodent> rodentList = C_InspectorPopulation.rodentList;
		int withinContext_Urodent = 0, withinSoilMatrix_Urodent = 0, trapped_Urodent = 0;
		Object[] contextContent = RunState.getInstance().getMasterContext().toArray();
		int contextSize = contextContent.length;
		for (int i = 0; i < contextSize; i++) {
			if (contextContent[i] instanceof C_Rodent) {
				withinContext_Urodent++;
				if (((C_Rodent) contextContent[i]).isTrappedOnBoard()) trapped_Urodent++;
			}
		}
		for (int i = 0; i < this.landscape.dimension_Ucell.getWidth(); i++) {
			for (int j = 0; j < this.landscape.dimension_Ucell.getHeight(); j++) {
				if (!this.landscape.getGrid()[i][j].getFullRodentList().isEmpty())
					withinSoilMatrix_Urodent += this.landscape.getGrid()[i][j].getFullRodentList().size();
			}
		}
		withinSoilMatrix_Urodent += trapped_Urodent;
		if ((withinContext_Urodent != rodentList.size()) || (withinSoilMatrix_Urodent != rodentList.size())) {
			A_Protocol.event("A_Protocol.checkRodentLists", "List sizes differ: rodentList/context/soilMatrix(trapped)"
					+ rodentList.size() + "/" + withinContext_Urodent + "/" + withinSoilMatrix_Urodent + " ("
					+ trapped_Urodent + ")", isError);
		}
	}
	/** record the current state of every inspectors'indicators in one unique .csv indicatorsFile,<br>
	 * count & display the simulation duration. JLF, 2014, 05.2016 */
	protected void haltSimulation() {
		for (A_Inspector inspector : inspectorList) inspector.closeSimulation();
		double simLength = System.currentTimeMillis() - C_ContextCreator.simulationStartTime_Ums;
		A_Protocol.event("A_Protocol.haltSimulation", "Simulation length: " + ((int) simLength / 1000) + "sec. (~"
				+ ((int) simLength / 60000) + "mn, ~" + ((int) simLength / 3600000) + "h.) / Tick "
				+ (int) RepastEssentials.GetTickCount() + ".", isNotError);
		indicatorsFile.closeFile();
		if (C_Parameters.VERBOSE) C_sound.sound("bip.wav");
		RepastEssentials.EndSimulationRun(); // Halt simulation once all step_Utick() are proceeded !
	}
	/** Display the map if on, remove it if off. Only one map object. The switch can only go from on to off and vice versa Version
	 * author J.Le Fur, 09.2014 */
	protected void switchDisplayMap() {
		if (this.context.contains(this.facilityMap)) {
			this.context.remove(this.facilityMap);
		} // Wipe off map
		else {// contextualizeNewThingInSpace(facilityMap, facilityMap.whereX, facilityMap.whereY);
			this.facilityMap.contextualize(this.context, this.landscape);
		}
	}
	/** Sets the initial time for simulation. Sets it to current time if not declared in daughter protocols. */
	public void initCalendar() {// TODO JLF 2015.04 Could also be the first date read on chronogram
		Date date = new Date();// get current date of the run
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.setTime(date);
		protocolCalendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(
				Calendar.DAY_OF_MONTH));
		protocolCalendar.get(Calendar.YEAR);
	}
	/** Exploratory, retrieve events and manage them appropriately. Here, tag and print them / author jlf 03.2015 */
	public static String event(String source, String message, Boolean isError) {
		message = protocolCalendar.stringShortDate() + " (tick " + (int) RepastEssentials.GetTickCount() + "): "
				+ message;
		if (isError) System.err.println(" [" + source + "] " + message);
		else System.out.println(message);
		return message;
	}
	/** Display the context on the output (for checking or debugging) */
	public void printContextFullContent() {
		Object[] list = RunState.getInstance().getMasterContext().toArray();
		System.out.println("=================================");
		System.out.println("A_Protocol, context size/nb agents/current tick: " + list.length + "/"
				+ C_ContextCreator.AGENT_NUMBER + "/" + RepastEssentials.GetTickCount());
		System.out.println("=================================");
		System.out.println("A_Protocol, START OF full context content : ");
		for (int i = 0; i < list.length; i++) System.out.println("élément: " + C_VariousUtilities.getShortClassName(
				list[i].getClass()) + ": " + list[i]);
		System.out.println("A_Protocol, END OF full context content.");
		System.out.println("=================================");
	}
	//
	// SETTER & GETTERS
	//
	public void setLandscape(C_Landscape landscape) {
		this.landscape = landscape;
	}
	/** @return the current date of the protocol as a short string (jj/mm/aa) */
	public String getStringShortDate() {
		return protocolCalendar.stringShortDate();
	}
	/** @return the current date of the protocol as a long string (day month year)) */
	public String getStringLongDate() {
		return protocolCalendar.stringLongDate();
	}
	/** @return the current date of the protocol as a long string (day month year) + hour:min:sec */
	public String getStringHourDate() {
		return protocolCalendar.stringHourDate();
	}
	/** @return the current date of the protocol as a long string (day month year) + hour:min:sec */
	public String getStringFullDate() {
		return protocolCalendar.stringFullDate();
	}
	/** @return the value of seasonToMate (Boolean) */
	public static Boolean isBreedingSeason() {
		return breedingSeason;
	}
	/** Check if chronogram is exhausted or if a precondition is verified (e.g., population is < 2). <br>
	 * This method may be redefined by daughter protocols */
	public boolean isSimulationEnd() {
		// if (RepastEssentials.GetTickCount() == 20000) return true;
		if ((this.chronogram != null) && (this.chronogram.isEndOfChrono)) {
			A_Protocol.event("A_Protocol.isSimulationEnd", "Chronogram exhausted; halting simulation", isNotError);
			return true;
		}
		if ((C_Parameters.TICK_MAX != 0 && RepastEssentials.GetTickCount() >= C_Parameters.TICK_MAX)) {
			A_Protocol.event("A_Protocol.isSimulationEnd", "TickMax reached; halting simulation", isNotError);
			return true;
		}
		return false;
	}
	/** Color the map in black to see the overall distribution of burrows<br>
	 * Author J.Le Fur 10.2014 TODO JLF 2014.10 should be in presentation package ? */
	protected void blackMap() {}

	/** Used for switching between IMAGE true or false - JLF 08.2018 */
	public void setStyleAgent(C_StyleAgent styleAgent) {
		if (this.styleAgent == null) this.styleAgent = styleAgent;

	}
}
