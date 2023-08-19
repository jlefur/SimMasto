package simmasto0.protocol;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;

import presentation.display.C_CustomPanelSet;
import presentation.display.C_UserPanel;
import presentation.epiphyte.C_InspectorFossorialRodents;
import presentation.epiphyte.C_InspectorPopulation;
import repast.simphony.context.Context;
import simmasto0.C_ContextCreator;
import thing.A_VisibleAgent;
import thing.C_Rodent;
import thing.C_RodentFossorial;
import thing.I_SituatedThing;
import thing.dna.C_GenomeAmniota;
import thing.ground.C_BurrowSystem;
import thing.ground.I_Container;
import data.C_Parameters;

/** author J.Le Fur, A.Comte 03.2012 / J.Le Fur 07.2012, 07.2013, 02.2014, 04.2020 */

public abstract class A_ProtocolFossorial extends A_Protocol {
	//
	// FIELD
	//
	protected C_InspectorFossorialRodents burrowInspector;
	//
	// CONSTRUCTOR
	//
	/** declare the inspectors, add them to the inspector list, declare them to the panelInitializer for indicators graphs. Author J.Le Fur 02.2013 */
	public A_ProtocolFossorial(Context<Object> ctxt) {
		super(ctxt);// Init parameters, raster ground and higher level inspectors & displays
		burrowInspector = new C_InspectorFossorialRodents();
		inspectorList.add(burrowInspector);
		C_CustomPanelSet.addBurrowInspector(burrowInspector);
		C_UserPanel.addBurrowInspector(burrowInspector);
	}
	//
	// METHODS
	//
	/** Randomly add burrows and randomly put RodentAgents in them */
	protected void initPopulations() {
		// add burrow systems and agents within
		addBurrowSystems(C_Parameters.INIT_BURROW_POP_SIZE);
		randomlyAddRodents(C_Parameters.INIT_RODENT_POP_SIZE);// add rodents within already created burrows
		// clean unused burrows
		for (C_BurrowSystem burrow : burrowInspector.getBurrowList())
			if (burrow.getOccupantList().isEmpty()) burrow.setDead(true); // end of clean
		System.out.println("C_ProtocolFossorial.init(): " + burrowInspector.getNbBurrows() + "(asked: " + C_Parameters.INIT_BURROW_POP_SIZE
				+ ") burrows created");
		System.out.println("C_ProtocolFossorial.init(): Population of " + C_Parameters.INIT_RODENT_POP_SIZE
				+ " fossorial rodents created and positioned randomly in burrow systems");
	}
	@Override
	public void initProtocol() {
		this.initPopulations();
		super.initProtocol();// manage inspectors and files after everything
	}
	@Override
	/** Declares a new object in the context and positions it within the raster ground; if burrow system, declare it to the inspector */
	public void contextualizeNewThingInContainer(I_SituatedThing thing, I_Container cell) {
		super.contextualizeNewThingInContainer(thing, cell);
		if (thing instanceof C_BurrowSystem) burrowInspector.addBurrowToList((C_BurrowSystem) thing);
	}
	/** Initialize the first set of burrowSystem using one of three methods: pure random, repeatable random, centered.
	 * @param nbBurrowSystem Number of burrowSystem <br>
	 *            rev. jlf 01.2018 */
	public void addBurrowSystems(int nbBurrowSystem) {
		double x, y;
		int ix, iy;
		I_Container[][] soilCellsMatrix = this.landscape.getGrid();
		Dimension dim = this.landscape.getDimension_Ucell();
		int grid_width = (int) dim.getWidth();
		int grid_height = (int) dim.getHeight();

		for (int i = 0; i < nbBurrowSystem; i++) {
			// BELOW, THREE POSSIBLE PATTERNS OF BURROW SYSTEMS INITIAL DISTRIBUTION :

			// pure random number to produce a sensitivity analysis
			// x = Math.random()*grid_width;
			// y = Math.random()*grid_height;

			// reproducible random distribution
			x = C_ContextCreator.randomGeneratorForInitialisation.nextDouble() * grid_width;
			y = C_ContextCreator.randomGeneratorForInitialisation.nextDouble() * grid_height;

			// put all burrow systems at the middle at init:
			// x = grid_width / 2;
			// y = grid_height / 2;

			// convert into continuous space and grid coordinates
			x = x * C_Parameters.CELL_SIZE_UcontinuousSpace; // cell / cs.cell^-1 -> cs;
			y = y * C_Parameters.CELL_SIZE_UcontinuousSpace; // cell / cs.cell^-1 -> cs;
			ix = (int) x;
			iy = (int) y;

			// Provide existence to the agent within the continuous space if not in a highway or lethal area
			if (soilCellsMatrix[ix][iy].getAffinity() > 0) {
				C_BurrowSystem burrow = new C_BurrowSystem(soilCellsMatrix[ix][iy].getAffinity(), ix, iy);
				contextualizeNewThingInSpace(burrow, x, y);
			}
			else i--;
		}
	}
	@Override
	/** Fills the context with dynamics agent _within the burrows_ for the first step of a simulation.<br>
	 * Sex ratio is randomly generated */
	public void randomlyAddRodents(int nbRodents) {
		C_Rodent oneRodent;
		ArrayList<C_BurrowSystem> burrowList = new ArrayList<C_BurrowSystem>();
		burrowList.addAll(burrowInspector.getBurrowList());
		Collections.sort(burrowList);
		for (int i = 0; i < nbRodents; i++) {
			int randPosition = (int) (C_ContextCreator.randomGeneratorForInitialisation.nextDouble() * burrowList.size());
			C_BurrowSystem burrow = burrowList.get(randPosition);
			// creates the agents
			oneRodent = createRodent();
			oneRodent.setRandomAge();
			contextualizeNewThingInContainer(oneRodent, burrow);
			A_VisibleAgent.myLandscape.moveToLocation(oneRodent, burrow.getCoordinate_Ucs());
		}
	}
	@Override
	public C_RodentFossorial createRodent() {
		return new C_RodentFossorial(new C_GenomeAmniota());
	}
	@Override
	public void readUserParameters() {
		super.readUserParameters();
		C_Parameters.INIT_BURROW_POP_SIZE = ((Integer) C_Parameters.parameters.getValue("NUMBER_OF_BURROW_SYSTEM")).intValue();
		// if switch to no persistance burrow tag empty burrows as dead
		boolean oldValuePersistanceBurrow = C_Parameters.PERSISTANCE_BURROW;
		boolean oldValueBlackMap = C_Parameters.BLACK_MAP;
		boolean oldValueExclos = C_Parameters.EXCLOS;
		C_Parameters.PERSISTANCE_BURROW = ((Boolean) C_Parameters.parameters.getValue("PERSISTANCE_BURROW")).booleanValue();
		if (oldValueExclos != C_Parameters.EXCLOS)
			A_Protocol.event("A_ProtocolFossorial.readUserParameters", "meta-population set to " + C_Parameters.EXCLOS, isNotError);

		if (oldValuePersistanceBurrow != C_Parameters.PERSISTANCE_BURROW) {
			A_Protocol.event("A_ProtocolFossorial.readUserParameters()", "BURROW PERSIST is " + C_Parameters.PERSISTANCE_BURROW, isNotError);
			if (this.burrowInspector != null) for (C_BurrowSystem burrow : this.burrowInspector.getBurrowList())
				if (burrow.getOccupantList().size() == 0 && burrow.getAnimalsTargetingMe().size() == 0) burrow.setDead(true);
		}
		C_Parameters.EXCLOS = ((Boolean) C_Parameters.parameters.getValue("EXCLOS")).booleanValue();
		if (oldValueExclos != C_Parameters.EXCLOS) {
			A_Protocol.event("A_ProtocolFossorial.readUserParameters()", "METAPOPULATION is " + C_Parameters.EXCLOS, isNotError);
		}
		C_Parameters.BLACK_MAP = ((Boolean) C_Parameters.parameters.getValue("BLACK_MAP")).booleanValue();
		if (oldValueBlackMap != C_Parameters.BLACK_MAP) {
			if (C_Parameters.BLACK_MAP) this.blackMap();
			else if (this.landscape != null) this.landscape.resetCellsColor();
		}
	}

	/** Close simulation if population is < 2 or if a precondition is verified. <br>
	 * This method may be redefined by daughter protocols */
	public boolean isSimulationEnd() {
		if (C_Parameters.MAX_POP != 0 && C_InspectorPopulation.rodentList.size() > C_Parameters.MAX_POP) {
			A_Protocol.event("A_ProtocolFossorial.isSimulationEnd", "MaxPop reached; halting simulation", isNotError);
			return true;
		}
		else if (C_InspectorPopulation.getNbFemales() == 0) {
			if (C_Parameters.VERBOSE) java.awt.Toolkit.getDefaultToolkit().beep();
			A_Protocol.event("A_ProtocolFossorial.isSimulationEnd", "Population is extinct (no female); halting simulation", isNotError);
			return true;
		}
		// OTHER STOP CONDITIONS :
		// else if(RepastEssentials.GetTickCount()>1000)return true;
		// else if (A_Protocol.protocolCalendar.stringShortDate().equals("01/04/2118")) return true;

		return super.isSimulationEnd();
	}
}
