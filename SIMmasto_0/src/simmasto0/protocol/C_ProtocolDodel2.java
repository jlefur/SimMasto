/* This source code is licensed under a BSD license as detailed in file SIMmasto_0.license.txt */
package simmasto0.protocol;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import com.vividsolutions.jts.geom.Coordinate;

import data.C_Chronogram;
import data.C_Event;
import data.C_Parameters;
import data.C_ReadRaster;
import data.C_ReadWriteFile;
import data.constants.I_ConstantDodel2;
import data.converters.C_ConvertGeographicCoordinates;
import presentation.display.C_Background;
import presentation.display.C_CustomPanelSet;
import presentation.display.C_UserPanel;
import presentation.epiphyte.C_InspectorBorreliaCrocidurae;
import presentation.epiphyte.C_InspectorEnergy;
import presentation.epiphyte.C_InspectorGenetic;
import presentation.epiphyte.C_InspectorOrnithodorosSonrai;
import presentation.epiphyte.C_InspectorPopulation;
import repast.simphony.context.Context;
import simmasto0.C_ContextCreator;
import simmasto0.util.C_PathWandererAstar;
import thing.A_VisibleAgent;
import thing.C_BorreliaCrocidurae;
import thing.C_Cat;
import thing.C_Food;
import thing.A_HumanUrban;
import thing.C_HumanWalker;
import thing.C_OrnitodorosSonrai;
import thing.C_Rodent;
import thing.C_RodentDomestic2;
import thing.C_TaxiManDodel;
import thing.I_SituatedThing;
import thing.dna.C_GenomeAmniota;
import thing.dna.species.C_GenomeAcaria;
import thing.dna.species.C_GenomeBorrelia;
import thing.dna.species.C_GenomeFelisSilvestrisCatus;
import thing.dna.species.C_GenomeHomoSapiens;
import thing.dna.species.C_GenomeMusMusculus;
import thing.ground.C_BurrowSystem;
import thing.ground.C_LandPlot;
import thing.ground.C_SoilCell;
import thing.ground.C_SoilCellNode;
import thing.ground.C_SoilCellUrban;
import thing.ground.I_Container;
import thing.ground.landscape.C_LandscapeUrban;

/** author J.Le Fur & M. Sall 03.2018 */
public class C_ProtocolDodel2 extends A_ProtocolFossorial implements I_ConstantDodel2 {
	//
	// FIELDS
	//
	private C_ConvertGeographicCoordinates geographicCoordinateConverter;
	protected C_InspectorGenetic geneticInspector;
	protected C_InspectorEnergy energyInspector;
	protected C_InspectorOrnithodorosSonrai ornithodorosInspector;
	protected C_InspectorBorreliaCrocidurae borreliaInspector;
	private Map<Integer, String> INITIAL_AFFINITIES_MUS = new HashMap<Integer, String>();
	private C_Chronogram humanActivitiesChrono;
	private String oldZoomRaster;
	/** Stored list of paths already computed */
	public static TreeMap<String, C_SoilCellNode[]> paths;
	public static int nbPaths = 0;
	//
	// CONSTRUCTOR
	//
	/** Declare the inspectors , add them to the inspector list, declares them to the panelInitializer for indicators graphs,
	 * retrieve stored paths. Author J.Le Fur 02.2013 rev. M.Sall 01.2018, Le Fur 05.2022 */
	public C_ProtocolDodel2(Context<Object> ctxt) {
		super(ctxt);
		C_ProtocolDodel2.paths = new TreeMap<String, C_SoilCellNode[]>();
		// used to set landPlots type given the affinity read at init
		this.INITIAL_AFFINITIES_MUS.put(0, "ROAD");
		this.INITIAL_AFFINITIES_MUS.put(1, "TRACK");
		this.INITIAL_AFFINITIES_MUS.put(2, "STREET");
		this.INITIAL_AFFINITIES_MUS.put(3, "WALL");
		this.INITIAL_AFFINITIES_MUS.put(4, "CONCESSION");
		this.INITIAL_AFFINITIES_MUS.put(5, "CORRIDOR");
		this.INITIAL_AFFINITIES_MUS.put(6, "MARKET");
		this.INITIAL_AFFINITIES_MUS.put(7, "ROOM");
		this.INITIAL_AFFINITIES_MUS.put(8, "WORKSHOP");
		this.INITIAL_AFFINITIES_MUS.put(9, "ROOMFOOD");
		this.INITIAL_AFFINITIES_MUS.put(10, "SHOPFOOD");
		this.INITIAL_AFFINITIES_MUS.put(11, "HOUSEDOOR");
		this.INITIAL_AFFINITIES_MUS.put(12, "ROOMDOOR");
		this.computeOthersLandPlot();
		this.manageTimeLandmarks();
		// Create and build the dataFromChrono from the csv file
		this.chronogram = new C_Chronogram(I_ConstantDodel2.CHRONO_FILENAME);
		this.facilityMap = new C_Background(-.169, 293., 299);
		this.setInitialAffinities();
		this.geneticInspector = new C_InspectorGenetic();
		this.energyInspector = new C_InspectorEnergy();
		this.ornithodorosInspector = new C_InspectorOrnithodorosSonrai();
		this.borreliaInspector = new C_InspectorBorreliaCrocidurae();
		this.inspectorList.add(this.borreliaInspector);
		this.inspectorList.add(this.ornithodorosInspector);
		this.inspectorList.add(this.geneticInspector);
		this.inspectorList.add(this.energyInspector);
		C_CustomPanelSet.addBorreliaInspector(this.borreliaInspector);
		C_CustomPanelSet.addOrnithodorosInspector(this.ornithodorosInspector);
		C_CustomPanelSet.addGeneticInspector(this.geneticInspector);
		C_CustomPanelSet.addEnergyInspector(this.energyInspector);
		C_UserPanel.addGeneticInspector(this.geneticInspector);
		// Position concessions at the barycentre of cells
		int i = 0, line, col;
		for (C_LandPlot lp : this.landscape.getAffinityLandPlots()) {
			double xx = 0., yy = 0.;
			for (C_SoilCell cell : lp.getCells()) {
				xx += cell.getCoordinate_Ucs().x;
				yy += cell.getCoordinate_Ucs().y;
				// Add one door to walls each n cells
				if (cell.getAffinity() == WALL_AFFINITY) {
					i++;
					if (i % ONE_DOOR_EACH_N_STEP == 0) {
						line = cell.retrieveLineNo();
						col = cell.retrieveColNo();
						cell.setAffinity(ROOMDOOR_AFFINITY);
						A_VisibleAgent.myLandscape.getValueLayer().set(ROOMDOOR_AFFINITY, line, col);
						i++;
					}
				}
			}
			xx = xx / lp.getCells().size();
			yy = yy / lp.getCells().size();
			this.landscape.moveToLocation(lp, new Coordinate(xx, yy));
		}
		// Read the stored paths file and fill this.paths Le Fur 05.2022
		this.initPaths();
	}
	//
	// OVERRIDEN METHODS
	//
	@Override
	public void step_Utick() {
		int previousDayOfMonth = protocolCalendar.get(Calendar.DAY_OF_MONTH);// @@ when paths are figured on the display JLF
																				// 05.2022

		super.step_Utick();// has to be after the other inspectors step since it records indicators in file
		if (protocolCalendar.get(Calendar.DAY_OF_MONTH) != previousDayOfMonth)
			A_VisibleAgent.myLandscape.resetCellsColor();
	}
	/** Color the map in black to see the overall distribution of burrows<br>
	 * Author J.Le Fur 10.2014, rev.03.2018 TODO JLF 2014.10 should be in presentation package ? */
	@Override
	protected void blackMap() {
		for (int i = 0; i < this.landscape.getDimension_Ucell().getWidth(); i++) for (int j = 0; j < this.landscape
				.getDimension_Ucell().getHeight(); j++) {
					if (this.landscape.getGrid()[i][j] instanceof C_SoilCell) {// safety measure
						C_SoilCell x = (C_SoilCell) this.landscape.getGrid()[i][j];
						String plotType = x.getMyLandPlot().getPlotType();
						if (plotType.equals("ROAD")) this.landscape.getValueLayer().set(9, i, j);
						else this.landscape.getValueLayer().set(BLACK_MAP_COLOR, i, j);
					}
				}
	}
	@Override
	/** The contact structure (term coined from S.E.Page: Diversity and Complexity) */
	protected void initLandscape(Context<Object> context) {
		this.setLandscape(new C_LandscapeUrban(context, C_Parameters.RASTERLIST_URL[0], VALUE_LAYER_NAME,
				CONTINUOUS_SPACE_NAME));
		// Comment the following lines to undisplay soil cells, JLF 10.2015, 11.2015
		int[][] matrixRead = new int[this.landscape.getGrid().length][this.landscape.getGrid()[0].length];
		for (int j = 0; j < matrixRead.length; j++) for (int k = 0; k < matrixRead[0].length; k++)
			matrixRead[j][k] = this.landscape.getGrid()[j][k].getAffinity();
		int[][] matrixTest = this.computeNewAffinity(matrixRead);
		this.landscape.createGround(matrixTest);// TODO MS 2019.08 dangerous manipulation of landscape create ground to public
		I_Container cell;
		for (int i = 0; i < this.landscape.dimension_Ucell.width; i++) {
			for (int j = 0; j < this.landscape.dimension_Ucell.height; j++) {
				cell = this.landscape.getGridCell(i, j);
				context.add(cell);
				this.landscape.moveToLocation(cell, cell.getCoordinate_Ucs());
			}
		}
	}
	@Override
	public void initCalendar() {
		protocolCalendar.set(2018, Calendar.NOVEMBER, 8, 9, 00);
	}
	@Override
	public void initProtocol() {

		this.initFixedParameters();
		if (C_Parameters.DISPLAY_MAP) {
			this.facilityMap.contextualize(this.context, this.landscape);
		}
		super.initProtocol();// manage inspectors and files after everything
	}
	@Override
	protected void initPopulations() {
		// randomlyAddHumanWalkers(1);// add human walkers
		// addBurrowSystems(1);// C_Parameters.INIT_BURROW_POP_SIZE
		// randomlyAddRodents(150);// add rodents within already created burrows C_Parameters.INIT_RODENT_POP_SIZE
		// randomlyAddTicks(15);
		// randomlyAddBorrelia(25);
		// this.randomlyAddCat(15);
		this.addFoodInLandPlot();
		// C_Rodent agent = createRodent();
		// agent.setRandomAge();
		// contextualizeNewThingInSpace(agent, 52, 144);
	}
	@Override
	public C_RodentDomestic2 createRodent() {
		return new C_RodentDomestic2(new C_GenomeMusMusculus());
	}
	@Override
	public void readUserParameters() {
		super.readUserParameters();
		if (oldZoomRaster == null)// Initialize oldZoomRaster variable if it's value is null
			oldZoomRaster = "";
		if (oldZoomRaster != C_Parameters.RASTER_URL.toLowerCase()) {// Verify if we change the grid
			String[] raster_Parameters = RASTER_PARAMETERS.get(C_Parameters.RASTER_URL.toLowerCase());
			if (raster_Parameters != null) {
				String[] list_URL = raster_Parameters[0].split(",");
				for (int i = 0; i < list_URL.length; i++) {
					list_URL[i] = RASTER_PATH + "20180814_RasterDodel2/" + list_URL[i];
				}
				C_Parameters.RASTERLIST_URL = list_URL;
				String[] origineCoordinate = raster_Parameters[1].split(",");
				this.geographicCoordinateConverter = new C_ConvertGeographicCoordinates(new Coordinate(Double
						.parseDouble(origineCoordinate[0]), Double.parseDouble(origineCoordinate[1])));
			}
		}
	}
	@Override
	public void manageTimeLandmarks() {
		if (!oldZoomRaster.equals(C_Parameters.RASTER_URL.toLowerCase())) {
			oldZoomRaster = C_Parameters.RASTER_URL.toLowerCase();
			String[] chronogram_file = HUMANACTIVITY_FILES.get(oldZoomRaster);
			this.humanActivitiesChrono = new C_Chronogram(chronogram_file[0]);
		}
		super.manageTimeLandmarks();
	}

	@Override
	protected void contextualizeNewThingInGrid(I_SituatedThing thing, int line_Ucell, int col_Ucell) {
		I_Container oneCell = this.landscape.getGrid()[line_Ucell][col_Ucell];
		if (!oneCell.getContainerList().contains(thing))
			super.contextualizeNewThingInGrid(thing, line_Ucell, col_Ucell);
	}
	@Override
	public boolean isSimulationEnd() {
		if (C_InspectorPopulation.getNbFemales() == 0 && !this.chronogram.isEndOfChrono) return false;// Avoid end of simulation
																										// when the number of
																										// female is null but the
																										// chronogram is not!
		return super.isSimulationEnd();
	}
	//
	// OTHERS METHODS
	//
	/** Initialize rodents, humans and ticks agents Author M.Sall 10.2015 rev. M.Sall 12.2020<br>
	 * @see A_Protocol#manageOneEvent */
	public void manageOneEvent(C_Event event) {
		Coordinate coordinateCell_Ucs = null;
		if (event.whereX_Ucell == null) {// then: 1) suppose that y is also null, 2) double are values in decimal degrees
			coordinateCell_Ucs = this.geographicCoordinateConverter.convertCoordinate_Ucs(event.whereX_Udouble,
					event.whereY_Udouble);
			event.whereX_Ucell = (int) coordinateCell_Ucs.x;
			event.whereY_Ucell = (int) coordinateCell_Ucs.y;
		}
		if (coordinateCell_Ucs == null) coordinateCell_Ucs = new Coordinate(event.whereX_Ucell, event.whereY_Ucell);
		Dimension dim = this.landscape.getDimension_Ucell();
		if ((coordinateCell_Ucs.x < dim.getWidth()) && (coordinateCell_Ucs.y < dim.getHeight())) {
			switch (event.type) {
				case MUS_EVENT :
					this.addRodentWithEvent(event);
					break;
				case TICK_EVENT :
					this.addTickWithEvent(event);
					break;
				case HUMAN_EVENT : {
					this.addHumanWithEvent(event);
				}
					break;
			}
		}
		super.manageOneEvent(event);
	}
	//
	// OTHER METHODS
	//
	/** Load the stored paths within this.paths Le Fur 05.2022 */
	private void initPaths() {
		A_Protocol.event("C_ProtocolDodel2.initPaths()", " : " + I_ConstantDodel2.PATHS_FILENAME, isNotError);
		BufferedReader buffer = C_ReadWriteFile.openBufferReader(CSV_PATH, I_ConstantDodel2.PATHS_FILENAME);
		C_SoilCellNode[] nodesList;
		String readLine, cellString, pathKey;
		String[] onePath;
		int pathLength, line, col;
		try {
			readLine = buffer.readLine();// reads the first line
			do {
				onePath = readLine.split(CSV_FIELD_SEPARATOR);
				pathKey = onePath[0];
				pathLength = onePath.length - 1;
				nodesList = new C_SoilCellNode[pathLength];
				for (int i = 1; i < onePath.length; i++) {
					cellString = onePath[i];
					line = Integer.parseInt(cellString.substring(0, 3));
					col = Integer.parseInt(cellString.substring(3));
					nodesList[i - 1] = (C_SoilCellNode) this.landscape.getGridCell(line, col);
				}
				C_ProtocolDodel2.paths.put(pathKey, nodesList);
			} while ((readLine = buffer.readLine()) != null); // reads the next line
		}
		catch (Exception e) {
			System.err.println("error when retrieving the paths stored " + e.getMessage());
			e.printStackTrace();
		}
		finally {
			try {
				buffer.close();
			}
			catch (Exception e) {
				System.err.println(": " + "buffer or rasterFile closing error" + e.getMessage());
			}
		}
	}
	/** Compute new value of the matrix in its parameter
	 * @author M.Sall 06.2019 */
	public int[][] computeNewAffinity(int[][] matrixRead) {
		int[][] matrixTest = new int[this.landscape.getGrid().length][this.landscape.getGrid()[0].length];
		for (int i = 0; i < matrixRead.length; i++) {
			for (int j = 0; j < matrixRead[0].length; j++) {
				switch (matrixRead[i][j]) {
					case NATIONAL_ROAD :
						matrixTest[i][j] = ROAD_AFFINITY;
						break;
					case TRACK :
						matrixTest[i][j] = TRACK_AFFINITY;
						break;
					case STREET :
						matrixTest[i][j] = STREET_AFFINITY;
						break;
					case WALL :
						matrixTest[i][j] = WALL_AFFINITY;
						break;
					case HOUSE :
						matrixTest[i][j] = CONCESSION_AFFINITY;
						break;
					case BUILDING :
						matrixTest[i][j] = CORRIDOR_AFFINITY;
						break;
					case RUINED :
					case GARDEN :
					case LIVESTOCK_PARK :
						matrixTest[i][j] = ENCLOSURE_AFFINITY;
						break;
					case MARKET :
						matrixTest[i][j] = MARKET_AFFINITY;
						break;
					case HUT :
					case ROOM :
					case LIVING_ROOM :
						matrixTest[i][j] = ROOM_AFFINITY;
						break;
					case WORKSHOP :
					case GARAGE :
					case LABORATORY :
					case HARDWARE_STORE :
					case HAIRDRESSER :
					case CLASS :
					case OFFICE :
						matrixTest[i][j] = WORKSHOP_AFFINITY;
						break;
					case KITCHEN :
						matrixTest[i][j] = ROOMFOOD_AFFINITY;
						break;
					case MAGASIN :
					case MILL :
					case RESTAURANT :
					case SHOP :
					case TANGANA :
					case DIBITERIE :
						matrixTest[i][j] = SHOPFOOD_AFFINITY;
						break;
					case HOUSE_DOOR :
						matrixTest[i][j] = HOUSEDOOR_AFFINITY;
						break;
					case ROOM_DOOR :
						matrixTest[i][j] = ROOMDOOR_AFFINITY;
						break;
					// TODO MS 08.2021 new destination to correct home error for persons who lived out of the simulated land
					case THIALAGA :
						matrixTest[i][j] = THIALAGA_AFFINITY;
						break;
					case DIERY_DIOUGA :
						matrixTest[i][j] = DIERY_DIOUGA_AFFINITY;
						break;
					case DIOMANDOU :
						matrixTest[i][j] = DIOMANDOU_AFFINITY;
						break;
					case SCHOOL_DESTINATION :
						matrixTest[i][j] = SCHOOL_AFFINITY;
						break;
					case MEDINA_DODEL_DESTINATION :
						matrixTest[i][j] = MEDINA_DODEL_AFFINITY;
						break;
					case KOGGA_WALO_DESTINATION :
						matrixTest[i][j] = KOGGA_WALO_AFFINITY;
						break;
					case DODEL_INTERIOR_DESTINATION :
						matrixTest[i][j] = DODEL_INTERIOR_AFFINITY;
						break;
					case FIELD_DESTINATION :
						matrixTest[i][j] = FIELD_AFFINITY;
						break;
					case BAKERY :
						matrixTest[i][j] = BAKERY_AFFINITY;
						break;
				}
			}
		}
		return matrixTest;
	}
	/** Create new human urban */
	public C_HumanWalker createHumanWalker() {
		C_HumanWalker human = new C_HumanWalker(new C_GenomeHomoSapiens());
		human.setRandomAge();
		return human;
	}
	/** Create new tick */
	public C_OrnitodorosSonrai createTick() {
		return new C_OrnitodorosSonrai(new C_GenomeAcaria());
	}
	/** Create new borrelia */
	public C_BorreliaCrocidurae createBorrelia() {
		return new C_BorreliaCrocidurae(new C_GenomeBorrelia());
	}
	public C_TaxiManDodel createCarrier() {
		C_TaxiManDodel oneCarrier = new C_TaxiManDodel(new C_GenomeAmniota());
		// Declares a new object in the context and positions it within the raster ground
		I_Container cell;
		if (C_ContextCreator.randomGeneratorForInitialisation.nextDouble() > .5) cell = landscape.getGrid()[0][390];
		else cell = landscape.getGrid()[260][0];
		contextualizeNewThingInContainer(oneCarrier, cell);
		oneCarrier.ownVehicle(TAXI_EVENT);
		oneCarrier.setTarget(landscape.getGrid()[175][175]);
		return oneCarrier;
	}
	/** Create new food
	 * @author M.Sall 09.2020 */
	public C_Food createFood(C_SoilCell oneSoilCell) {
		return new C_Food(oneSoilCell.getAffinity(), oneSoilCell.getCoordinate_Ucs().X, oneSoilCell
				.getCoordinate_Ucs().Y);
	}
	/** Create new human walker */
	public C_Cat createCat() {
		C_Cat oneCat = new C_Cat(new C_GenomeFelisSilvestrisCatus());
		oneCat.setRandomAge();
		return oneCat;
	}
	/** Add foods in the interesting landplots.
	 * @author M.Sall 09.2020 */
	public void addFoodInLandPlot() {
		TreeSet<C_LandPlot> landplots = this.landscape.getAffinityLandPlots();
		for (C_LandPlot oneLandPlot : landplots) {
			switch (oneLandPlot.getAffinity()) {
				case ROOMFOOD_AFFINITY :
				case SHOPFOOD_AFFINITY : {
					this.randomlyAddFoodInLandPlot(oneLandPlot);
				}
			}
		}
	}

	/** Use the others raster to compute rooms landplots
	 * @author M.Sall 10.2020 */
	public void computeOthersLandPlot() {
		String[] urlList = C_Parameters.RASTERLIST_URL;
		if (urlList.length > 1) {
			int[][] matrixRead;
			int[][] matrixToCompare;
			if (RASTER_MODE.compareTo("ascii") == 0) matrixRead = C_ReadRaster.txtRasterLoader(urlList[1]);
			else matrixRead = C_ReadRaster.imgRasterLoader(urlList[1]);
			for (int i = 2; i < urlList.length; i++) {
				if (RASTER_MODE.compareTo("ascii") == 0) matrixToCompare = C_ReadRaster.txtRasterLoader(urlList[i]);
				else matrixToCompare = C_ReadRaster.imgRasterLoader(urlList[i]);
				for (int j = 0; j < matrixRead.length; j++) for (int k = 0; k < matrixRead[0].length; k++) {
					if (matrixRead[j][k] != matrixToCompare[j][k] && matrixToCompare[j][k] != STREET)
						matrixRead[j][k] = matrixToCompare[j][k];
				}
			}
			int[][] matrixTest = this.computeNewAffinity(matrixRead);
			for (int j = 0; j < matrixTest.length; j++) for (int k = 0; k < matrixTest[0].length; k++) {
				if (((this.landscape.getGrid()[j][k]).getAffinity() != matrixTest[j][k])
						&& (matrixTest[j][k] != STREET_AFFINITY)) {
					(this.landscape.getGrid()[j][k]).setAffinity(matrixTest[j][k]);
					this.landscape.getValueLayer().set(matrixTest[j][k], j, k);
				}
			}
			this.landscape.identifyAffinityLandPlots(this.context);
			// do not let traders wander outside of their shop, Le Fur 05.2022
			for (int i = 0; i < this.landscape.dimension_Ucell.getWidth(); i++) {
				for (int j = 0; j < this.landscape.dimension_Ucell.getHeight(); j++) {
					C_SoilCellUrban oneCell = (C_SoilCellUrban) landscape.getGrid()[i][j];
					if (oneCell.getAffinity() == SHOPFOOD_AFFINITY || oneCell.getAffinity() == WORKSHOP_AFFINITY) {
						oneCell.setConcession(oneCell.affinityLandPlot);// @@
					}
				}
			}
		}
	}
	/** Use the activity list to initialize human activities
	 * @author M.Sall 10.2020 */
	public void initHumanActivity(A_HumanUrban oneHuman) {
		ArrayList<String> activitiesList = this.humanActivitiesChrono.getFullEvents_Ustring();
		for (int i = 0; i < this.humanActivitiesChrono.getChronoLength(); i++) {
			String[] activities = activitiesList.get(i).split(CSV_FIELD_SEPARATOR);
			String humanID = oneHuman.retrieveMyName().split(NAMES_SEPARATOR)[DATE_COL];
			if (humanID.equals(activities[DATE_COL])) {
				if (activities[X_COL].contains(".") || activities[X_COL].contains("."))// TODO MS de JLF 2021.07.21 redondant ?
					oneHuman.addActivityList(activities[EVENT_COL], this.geographicCoordinateConverter
							.convertCoordinate_Ucs(Double.parseDouble(activities[X_COL]), Double.parseDouble(
									activities[Y_COL])), activities[VALUE1_COL], activities[VALUE2_COL] + "/"
											+ activities[VALUE3_COL]);
				else
					oneHuman.addActivityList(activities[EVENT_COL], new Coordinate(Integer.parseInt(activities[X_COL]),
							Integer.parseInt(activities[Y_COL])), activities[VALUE1_COL], activities[VALUE2_COL] + "-"
									+ activities[CELL_ID_COL]);
			}
		}
		oneHuman.manageActivities();
	}
	/** Initialize the first set of burrowSystem within walls.
	 * @param nbBurrowSystem Number of burrowSystem <br>
	 *            jlf 04.2020 */
	public void addBurrowSystems(int nbBurrowSystem) {
		double x, y;
		int ix, iy;
		I_Container[][] soilCellsMatrix = this.landscape.getGrid();
		Dimension dim = this.landscape.getDimension_Ucell();
		int grid_width = (int) dim.getWidth();
		int grid_height = (int) dim.getHeight();
		for (int i = 0; i < nbBurrowSystem; i++) {
			// reproducible random distribution
			x = C_ContextCreator.randomGeneratorForInitialisation.nextDouble() * grid_width;
			y = C_ContextCreator.randomGeneratorForInitialisation.nextDouble() * grid_height;
			// convert into continuous space and grid coordinates
			x = x * C_Parameters.CELL_SIZE_UcontinuousSpace; // cell / cs.cell^-1 -> cs;
			y = y * C_Parameters.CELL_SIZE_UcontinuousSpace; // cell / cs.cell^-1 -> cs;
			ix = (int) x;
			iy = (int) y;
			// Provide existence to the agent within the continuous space if within a wall
			if (soilCellsMatrix[ix][iy].getAffinity() == WALL_AFFINITY) {
				C_BurrowSystem burrow = new C_BurrowSystem(soilCellsMatrix[ix][iy].getAffinity(), ix, iy);
				contextualizeNewThingInSpace(burrow, x, y);
			}
			else i--;
		}
	}
	public void addRodentWithEvent(C_Event rodentEvent) {
		C_RodentDomestic2 oneRodent = this.createRodent();
		C_SoilCell oneCell = (C_SoilCell) this.landscape.getGridCell(rodentEvent.whereX_Ucell,
				rodentEvent.whereY_Ucell);
		if (!(oneCell.getAffinity() == ROAD_AFFINITY || oneCell.getAffinity() == STREET_AFFINITY || oneCell
				.getAffinity() == MARKET_AFFINITY || oneCell.getAffinity() == TRACK_AFFINITY)) {
			C_BurrowSystem oneBurrow = new C_BurrowSystem(oneCell.getAffinity(), rodentEvent.whereX_Ucell,
					rodentEvent.whereY_Ucell);
			contextualizeNewThingInContainer(oneBurrow, oneCell);
			contextualizeNewThingInContainer(oneRodent, oneBurrow);
		}
		else {
			contextualizeNewThingInContainer(oneRodent, oneCell);
			oneRodent.setHasToCreateHome(true);
			oneRodent.setDesire(FLEE);
		}
		oneRodent.setRandomAge();
		if ((!rodentEvent.value2.isEmpty()) && (rodentEvent.value2.equals("+"))) {
			C_BorreliaCrocidurae oneBorrelia = this.createBorrelia();
			oneRodent.getInfection(oneBorrelia);
			borreliaInspector.addBorreliaToList(oneBorrelia);
		}
	}
	public void addTickWithEvent(C_Event tickEvent) {
		C_SoilCell oneCell = (C_SoilCell) this.landscape.getGridCell(tickEvent.whereX_Ucell, tickEvent.whereY_Ucell);
		C_BurrowSystem oneBurrow = null;
		for (I_SituatedThing oneThing : oneCell.getOccupantList()) {
			if (oneThing instanceof C_BurrowSystem) {
				oneBurrow = (C_BurrowSystem) oneThing;
				break;
			}
		}
		if (oneBurrow == null) {
			oneBurrow = new C_BurrowSystem(oneCell.getAffinity(), tickEvent.whereX_Ucell, tickEvent.whereY_Ucell);
			contextualizeNewThingInContainer(oneBurrow, oneCell);
		}
		int tickNumber = Integer.parseInt(tickEvent.value1);
		int infectedNumber = 0;
		if (!tickEvent.value2.isEmpty()) infectedNumber = Integer.parseInt(tickEvent.value2);
		for (int i = 0; i < tickNumber; i++) {
			C_OrnitodorosSonrai oneTick = this.createTick();
			contextualizeNewThingInContainer(oneTick, oneBurrow);
			oneTick.setRandomAge();
			if (infectedNumber > 0) {
				C_BorreliaCrocidurae oneBorrelia = this.createBorrelia();
				oneTick.getInfection(oneBorrelia);
				borreliaInspector.addBorreliaToList(oneBorrelia);
				infectedNumber--;
			}
		}
	}
	public void addHumanWithEvent(C_Event humanEvent) {
		String[] humanData = humanEvent.value2.split(EVENT_VALUE2_FIELD_SEPARATOR);
		// tag field added for control and keep only human flagged, JLF:11.01.2022
		// C_HumanUrban oneHuman = this.createHumanUrban();
		C_HumanWalker oneHuman = this.createHumanWalker();
		if (humanData[2].equals("TRUE")) {
			oneHuman.seta_Tag(true);
		}

		oneHuman.setMyName(humanEvent.value1);
		if (humanData[0].equals("M")) oneHuman.setMale(true);
		else oneHuman.setMale(false);
		oneHuman.setAge_Uday(oneYear_Uday * Integer.parseInt(humanData[1]));
		C_SoilCellUrban humanHomeCell = (C_SoilCellUrban) this.landscape
				.getGrid()[humanEvent.whereX_Ucell][humanEvent.whereY_Ucell];
		oneHuman.setMyHome(humanHomeCell);
		contextualizeNewThingInContainer(oneHuman, humanHomeCell);
		this.initHumanActivity(oneHuman);
		if (oneHuman instanceof C_HumanWalker) ((C_HumanWalker) oneHuman).pathWanderer = new C_PathWandererAstar();
	}
	public void randomlyAddTicks(int nbTick) {
		int listLength = this.burrowInspector.getNbBurrows();
		ArrayList<C_BurrowSystem> burrowList = new ArrayList<>();
		burrowList.addAll(burrowInspector.getBurrowList());
		for (int i = 0; i < nbTick; i++) {
			C_OrnitodorosSonrai oneTick = this.createTick();
			oneTick.setRandomAge();
			int index = (int) (C_ContextCreator.randomGeneratorForInitialisation.nextDouble() * listLength);
			C_BurrowSystem oneBurrow = burrowList.get(index);
			ornithodorosInspector.addTickToList(oneTick);
			contextualizeNewThingInContainer(oneTick, oneBurrow);
		}
	}
	public void randomlyAddFoodInLandPlot(C_LandPlot oneLandPlot) {
		int listLength = oneLandPlot.getCells().size();
		ArrayList<C_SoilCell> cells = new ArrayList<>();
		cells.addAll(oneLandPlot.getCells());
		int index = (int) (C_ContextCreator.randomGeneratorForInitialisation.nextDouble() * listLength);
		C_Food oneFood = this.createFood(cells.get(index));
		contextualizeNewThingInContainer(oneFood, cells.get(index));
	}
	public void randomlyAddBorrelia(int nbBorrelia) {
		int nbTick = this.ornithodorosInspector.getTicksNumber();
		ArrayList<C_OrnitodorosSonrai> tickList = new ArrayList<>();
		tickList.addAll(ornithodorosInspector.getTickList());
		for (int i = 0; i < nbBorrelia; i++) {
			C_BorreliaCrocidurae oneBorrelia = this.createBorrelia();
			int index = (int) (C_ContextCreator.randomGeneratorForInitialisation.nextDouble() * nbTick);
			C_OrnitodorosSonrai oneOrnithodoros = tickList.get(index);
			borreliaInspector.addBorreliaToList(oneBorrelia);
			oneBorrelia.setTrappedOnBoard(true);
			contextualizeNewThingInContainer(oneBorrelia, oneOrnithodoros);
		}
	}
	public void randomlyAddCat(int nbCat) {
		for (int i = 0; i < nbCat; i++) {
			C_Cat agent = createCat();
			Coordinate oneCoordinate = null;
			int cellAffinity = 0;
			do {
				oneCoordinate = new Coordinate(C_ContextCreator.randomGeneratorForInitialisation.nextDouble()
						* this.landscape.dimension_Ucell.width, C_ContextCreator.randomGeneratorForInitialisation
								.nextDouble() * this.landscape.dimension_Ucell.height);
				cellAffinity = this.landscape.getGrid()[(int) oneCoordinate.x][(int) oneCoordinate.y].getAffinity();
			} while (cellAffinity == WALL_AFFINITY);
			contextualizeNewThingInSpace(agent, oneCoordinate.x, oneCoordinate.y);
		}
	}
	/** initializing the land plot types */
	public void setInitialAffinities() {
		TreeSet<C_LandPlot> plots = this.landscape.getAffinityLandPlots();
		for (C_LandPlot a_plot : plots) {
			String plotType = INITIAL_AFFINITIES_MUS.get(a_plot.getAffinity());
			a_plot.setPlotType(plotType);
		}
	}
}