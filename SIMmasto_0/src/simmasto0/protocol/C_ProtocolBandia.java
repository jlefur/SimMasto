package simmasto0.protocol;

import java.util.Calendar;
import java.util.TreeSet;

import data.C_Chronogram;
import data.C_Event;
import data.C_Parameters;
import presentation.display.C_Background;
import presentation.display.C_CustomPanelSet;
import presentation.display.C_UserPanel;
import presentation.epiphyte.C_InspectorCMR;
import presentation.epiphyte.C_InspectorGenetic;
import repast.simphony.context.Context;
import thing.C_Egg;
import thing.C_RodentCmr;
import thing.I_SituatedThing;
import thing.dna.species.C_GenomeMastoErythroleucus;
import thing.ground.C_Trap;
import thing.ground.I_Container;

/** initialize the simulation and manages the inputs coming from the csv events file
 * @author Diakhate & Le Fur july 2013, 08.2014 */
public class C_ProtocolBandia extends A_ProtocolFossorial implements data.constants.I_ConstantBandia {

	protected C_InspectorGenetic geneticInspector;
	protected C_InspectorCMR C_InspectorCMR;
	public static int numSession = 0; // unique id number for each session
	private boolean dayChanged;
	private int currentDay = protocolCalendar.get(Calendar.DATE);
	//
	// CONSTRUCTOR
	//
	public C_ProtocolBandia(Context<Object> ctxt) {
		super(ctxt);// Init parameters and higher level inspectors & displays
		this.chronogram = new C_Chronogram(CHRONO_FILENAME); // Create and build the dataFromChrono
		this.facilityMap = new C_Background(-3.44, 95, 85);
		this.dayChanged = false;
		// Initialize the epiphyte system
		this.geneticInspector = new C_InspectorGenetic();
		this.C_InspectorCMR = new C_InspectorCMR();
		this.inspectorList.add(geneticInspector);
		this.inspectorList.add(C_InspectorCMR);
		C_CustomPanelSet.addCMRInspector(C_InspectorCMR);
		C_CustomPanelSet.addGeneticInspector(geneticInspector);
		C_UserPanel.addGeneticInspector(geneticInspector);
	}
	//
	// METHODS
	//
	@Override
	public void readUserParameters() {
		super.readUserParameters();
		C_Parameters.INIT_BURROW_POP_SIZE = ((Integer) C_Parameters.parameters.getValue("NUMBER_OF_BURROW_SYSTEM"))
				.intValue();
		C_Parameters.REPRO_START_Umonth = ((Integer) C_Parameters.parameters.getValue("REPRO_START_Umonth")).intValue();
		C_Parameters.REPRO_END_Umonth = ((Integer) C_Parameters.parameters.getValue("REPRO_END_Umonth")).intValue();
	}
	/** Used to compute the population in the trap system area (to be comparable with MNA). */
	@Override
	public void initProtocol() {
		super.initProtocol();
		setTrapArea_Ucell();
	}
	@Override
	/** Update the universe according to bandiaEvents (chrono) If the current simulation date is one date of bandiaEvents then
	 * account for the corresponding event then proceed to inspector's step */
	public void step_Utick() {
		// determine if day has changed (to avoid checking each minute if time step is lower than one day)
		if (currentDay != protocolCalendar.get(Calendar.DATE)) {
			this.dayChanged = true;
			this.currentDay = protocolCalendar.get(Calendar.DATE);
		}
		if (dayChanged) {
			// Switch either configuration by (un)commenting the selected one :
			// 1) Disconnect events reader and proceed to one CMR session each new month, Le Fur 08/08/13.
			if (currentDay == 03) addTrapSystem();
			// TODO number in source 2013.08 days when trap system is checked
			else if (currentDay > 03 && currentDay < 8) checkTrapSystem();
			else if (currentDay == 8) removeTrapSystem();
			// 2) Read the effective protocol from the events' chrono
			// updateUniverseFromChrono();
			this.dayChanged = false;
		}
		super.step_Utick();// has to come after the other inspectors step since it records indicators in file
	}
	/** Used to compute the population within the trap system area (provides a value comparable with MNA). <br>
	 * Has to be in protocol since the inspector does not know (correct ?) the raster JLF 08.2014 */
	public void setTrapArea_Ucell() {
		int x = 0, y = 0;
		TreeSet<I_Container> trapArea = new TreeSet<I_Container>();
		int intervalX_Umeter = (int) (TRAP_INTERVALx_Umeter / C_Parameters.CELL_WIDTH_Umeter);
		int intervalY_Umeter = (int) (TRAP_INTERVALy_Umeter / C_Parameters.CELL_WIDTH_Umeter);
		for (int i = 0; i < TRAP_COLS * intervalX_Umeter; i++) {
			x = TRAP0_x_Ucell + i;
			for (int j = 0; j < TRAP_LINES * intervalY_Umeter; j++) {
				y = TRAP0_y_Ucell + j;
				trapArea.add(this.landscape.getGrid()[x][y]);
			}
		}
		C_InspectorCMR.setTrapArea(trapArea);// inspector is in charge of computing the rodent pop. size within the area
	}
	@Override
	/** created rodents are of the Mastomys erythroleucus genus */
	public C_RodentCmr createRodent() {
		return new C_RodentCmr(new C_GenomeMastoErythroleucus());
	}
	/** Create a trap and inform bandiaInspector */
	public void addOneTrap(int x, int y) {
		C_Trap oneTrap = new C_Trap(TRAP_AFFINITY, x, y);
		contextualizeNewThingInGrid(oneTrap, x, y);
		oneTrap.setAffinity(oneTrap.getCurrentSoilCell().getAffinity() + 1);// TODO number in source 2018.09 JLF trap affinity =
																			// cell affinity +n;
		C_InspectorCMR.addTrap(oneTrap);
	}
	/** create a set of traps */
	public void addTrapSystem() {
		int x, y, nbTraps = 0;
		int intervalX = (int) (TRAP_INTERVALx_Umeter / C_Parameters.CELL_WIDTH_Umeter);
		int intervalY = (int) (TRAP_INTERVALy_Umeter / C_Parameters.CELL_WIDTH_Umeter);
		for (int i = 0; i < TRAP_COLS; i++) {
			x = TRAP0_x_Ucell + (i * intervalX);
			for (int j = 0; j < TRAP_LINES; j++) {
				y = TRAP0_y_Ucell + j * intervalY;
				addOneTrap(x, y);
				nbTraps++;
			}
		}
		System.out.println();
		A_Protocol.event("C_ProtocolBandia.addTrapSystem()", "Trap system added (" + nbTraps + " traps)", isNotError);
	}
	/** Check one trap, tag rodent, open trap and release rodent */
	protected void checkTrap(C_Trap oneTrap) {
		for (I_SituatedThing thing : oneTrap.getFullOccupantList()) {
			if (!(thing instanceof C_Egg)) {// TODO MS to JLF 01.2020 Correction de bug : tu as une erreur sur la conversion des
											// egg en C_RodentCmr lors de la vérification des checkTrap. un egg ne peut être
											// caster en C_RodentCmr
				C_RodentCmr rodent = (C_RodentCmr) thing;
				tag(rodent);
				rodent.recordCatch(C_ProtocolBandia.numSession, getStringShortDate());
				// release rodent
				oneTrap.openTrap();
				rodent.setTrappedOnBoard(false);
				rodent.actionRandomExitOfContainer();
				rodent.actionDisperse(); // make rodent escape
			}
		}
	}
	/** Check a set of traps */
	public void checkTrapSystem() {
		for (C_Trap oneTrap : C_InspectorCMR.getTrapList())
			checkTrap(oneTrap);
		A_Protocol.event("C_ProtocolBandia.checkTrapSystem", "Trap system checked (current tag="
				+ C_InspectorCMR.taggedRodentsNumber + ")", isNotError);
	}
	/** add rodent tagged in bandiaInspector */
	protected void tag(C_RodentCmr rodent) {
		C_InspectorCMR.tag(rodent);
	}
	/** suppress traps and remove them in bandiaInspector */
	public void removeTrapSystem() {
		Object[] tempTrapList = C_InspectorCMR.getTrapList().toArray();// Needed to avoid concurrent modification exception
		C_Trap thisTrap = null;
		for (Object o : tempTrapList) {
			thisTrap = (C_Trap) o;
			if (!thisTrap.getFullOccupantList().isEmpty()) checkTrap(thisTrap);
			thisTrap.setDead(true);
		}
		// Process the elapsed session, compute the MNA,DRS and DMR indicators
		C_InspectorCMR.computeDRS(numSession);
		C_InspectorCMR.computeDMR(numSession);
		C_InspectorCMR.computeMNA(numSession);
		C_InspectorCMR.storeCMRIndicators(getStringShortDate());
		A_Protocol.event("C_ProtocolBandia.removeTrapSystem", "Trap system removed", isNotError);
	}
	/** Update the universe according to oneEventLine from BandiaEvents
	 * @param oneEventLine from BandiaEvents(or dataChrono) */
	@Override
	protected void manageOneEvent(C_Event oneEvent) {
		String eventName = oneEvent.type;
		if (eventName.equals(ADD_TRAP)) addTrapSystem();
		else if (eventName.equals(CHECK_TRAP)) checkTrapSystem();
		else if (eventName.equals(REMOVE_TRAP)) {
			checkTrapSystem();
			removeTrapSystem();
		}
	}
	@Override
	public void initCalendar() {
		protocolCalendar.set(2008, Calendar.DECEMBER, 1);
	}
	/** Color the map in black to see the overall distribution of burrows<br>
	 * Author J.Le Fur 10.2014 TODO JLF 2014.10 should be in presentation package ? */
	protected void blackMap() {
		for (int i = 0; i < this.landscape.getDimension_Ucell().getWidth(); i++)
			for (int j = 0; j < this.landscape.getDimension_Ucell().getHeight(); j++) {
				if (this.landscape.getValueLayer().get(i, j) <= 1) // houses and roads
					this.landscape.getValueLayer().set(4, i, j);
				else
					this.landscape.getValueLayer().set(BLACK_MAP_COLOR, i, j);
			}
	}
}
