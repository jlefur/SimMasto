package simmasto0.protocol;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.TreeSet;

import com.vividsolutions.jts.geom.Coordinate;

import data.C_Chronogram;
import data.C_Event;
import data.C_Parameters;
import data.C_ReadRaster;
import data.constants.I_ConstantDodel;
import data.constants.I_ConstantGerbil;
import data.constants.I_ConstantNumeric;
import data.converters.C_ConvertGeographicCoordinates;
import presentation.display.C_Background;
import presentation.display.C_CustomPanelSet;
//import presentation.epiphyte.C_InspectorEnergy;
import presentation.epiphyte.C_InspectorVegetation;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunState;
import repast.simphony.util.collections.IndexedIterable;
import simmasto0.C_ContextCreator;
import thing.A_NDS;
import thing.C_BarnOwl;
import thing.C_RodentGerbil;
import thing.C_Vegetation;
import thing.I_SituatedThing;
import thing.dna.species.C_GenomeAcacia;
import thing.dna.species.C_GenomeBalanites;
import thing.dna.species.C_GenomeFabacea;
import thing.dna.species.C_GenomeGerbillusNigeriae;
import thing.dna.species.C_GenomePoacea;
import thing.dna.species.C_GenomeTytoAlba;
import thing.ground.C_Nest;
import thing.ground.C_SoilCellSavanna;

/** Initialize the simulation and manage inputs coming from the csv events file
 * @author J.Le Fur, 10.2014, rev. M.Sall 12.2015, JLF 02.2021 */
public class C_ProtocolGerbil extends A_ProtocolFossorial implements I_ConstantGerbil {
	//
	// FIELDS
	//
	private C_ConvertGeographicCoordinates geographicCoordinateConverter = null;
	protected C_InspectorVegetation vegetationInspector;
	//
	// CONSTRUCTOR
	//
	public C_ProtocolGerbil(Context<Object> ctxt) {
		super(ctxt);
		// Create and build the dataFromChrono from the csv file
		this.chronogram = new C_Chronogram(I_ConstantGerbil.CHRONO_FILENAME);
		// facilityMap = new C_Background(0, -.4, -.4);
		// -10 plus gros, -5 plus petit
		if ((((String) RunEnvironment.getInstance().getParameters().getValue("RASTER_FILE")).toLowerCase()).equals(
				"zoom1")) this.facilityMap = new C_Background(-10.8, 15., 15.);
		else this.facilityMap = null;// For zoom1
		I_ConstantNumeric.cellSize.set(0, CELL_SIZE);// Choose value of cell size
		this.vegetationInspector = new C_InspectorVegetation(this.landscape.getGrid());
		this.inspectorList.add(vegetationInspector);
		for (int i = 0; i < this.landscape.dimension_Ucell.width; i++) {
			for (int j = 0; j < this.landscape.dimension_Ucell.height; j++) {
				TreeSet<I_SituatedThing> agentList = this.landscape.getGrid()[i][j].getOccupantList();
				for (I_SituatedThing agent : agentList) {
					if (agent instanceof C_Vegetation) vegetationInspector.addVegetationToList((C_Vegetation) agent);
				}
			}
		}
		C_CustomPanelSet.addVegetationInspector(vegetationInspector);
	}
	//
	// METHODS
	//
	@Override
	/** Initialize the protocol with the raster origin */
	public void initProtocol() {
		this.geographicCoordinateConverter = new C_ConvertGeographicCoordinates(new Coordinate(
				I_ConstantGerbil.rasterLongitudeWest_LatitudeSouth_Udegree.get(0),
				I_ConstantGerbil.rasterLongitudeWest_LatitudeSouth_Udegree.get(1)));
		this.initFixedParameters();
		super.initProtocol();
		if (C_Parameters.DISPLAY_MAP)
			if (this.facilityMap != null) this.facilityMap.contextualize(this.context, this.landscape);
	}
	@Override
	/** In gerbil protocol, the landscape raster values (i.e., affinity) contain the landcover values<br>
	 * author M.Sall 2017, rev. jlf 01.2018 */
	protected void initLandscape(Context<Object> context) {
		super.initLandscape(context);
		// Comment the following lines to undisplay soil cells, JLF 10.2015, 11.2015
		for (int i = 0; i < this.landscape.dimension_Ucell.width; i++) {
			for (int j = 0; j < this.landscape.dimension_Ucell.height; j++) {
				C_SoilCellSavanna cell = new C_SoilCellSavanna(this.landscape.getGrid()[i][j].getAffinity(), i, j);
				context.add(cell);
				this.landscape.setGridCell(i, j, cell);
				this.landscape.moveToLocation(cell, cell.getCoordinate_Ucs());
				this.contextualizeVegetationInSavannaCell((C_SoilCellSavanna) cell);
				// Homogenize background color once vegetation is set
				if (cell.getAffinity() != 0) {// 0 = water TODO number in source 2018.02 jlf
					this.landscape.getValueLayer().set(BACKGROUND_COLOR, i, j);
					cell.setAffinity(BACKGROUND_COLOR);
				}
			}
		}
	}
	@Override
	public void initCalendar() {
		// protocolCalendar.set(1999, Calendar.JANUARY, 1);
		protocolCalendar.set(1999, Calendar.JUNE, 1, I_ConstantDodel.TWILIGHT_END_Uhour + 1, 0);// To avoid being stuck at noon
																								// when time step>1day
	}
	@Override
	/** Default rodents are of the Gerbillus nigeriae species */
	public C_RodentGerbil createRodent() {
		return new C_RodentGerbil(new C_GenomeGerbillusNigeriae());
	}
	public C_BarnOwl createBarnOwl() {
		return new C_BarnOwl(new C_GenomeTytoAlba());
	}
	/** Add vegetation in soil cell at the requested position from the value of land cover (i.e., cell affinity) which contains
	 * two types of vegetation per cell<br>
	 * rev. JLF 03.2021 */
	public void contextualizeVegetationInSavannaCell(C_SoilCellSavanna currentSoilCellSavanna) {
		String[] vegetationInLandcover = LANDCOVER_TO_VEGETATION.get(currentSoilCellSavanna.getAffinity());
		List<Coordinate> coordinateList = new ArrayList<Coordinate>();
		if (vegetationInLandcover != null) {
			Coordinate oneCoordinate = null;
			// Randomly add vegetation in soil cell, check that vegetation are not too close
			for (int i = 0; i < 9; i++) {
				for (int coverComponent = 0; coverComponent < 2; coverComponent++) {
					oneCoordinate = getVegetationCoordinate(DISTANCE_THRESHOLD, coordinateList, currentSoilCellSavanna);
					C_Vegetation oneVegetation = createVegetation(vegetationInLandcover[coverComponent]);
					if (oneVegetation != null) {
						contextualizeNewThingInContainer(oneVegetation, currentSoilCellSavanna);
						this.landscape.moveToLocation(oneVegetation, oneCoordinate);
						coordinateList.add(oneCoordinate);
					}
				}
			}
		}
	}
	/** Compute one coordinate, compare its position with the other in the list and if it's correct return the position */
	public Coordinate getVegetationCoordinate(double distanceThresHold, List<Coordinate> coordinateList,
			C_SoilCellSavanna currentCell) {
		Coordinate oneCoordinate = null;
		while (oneCoordinate == null) {
			oneCoordinate = new Coordinate(currentCell.retrieveLineNo()
					+ C_ContextCreator.randomGeneratorForInitialisation.nextDouble(), currentCell.retrieveColNo()
							+ C_ContextCreator.randomGeneratorForInitialisation.nextDouble());
			if (coordinateList.size() != 0) {
				int i = coordinateList.size();
				while ((oneCoordinate != null) && (i > 0)) {
					double vegetationDistance = .0;
					Coordinate secondCoord = coordinateList.get(i - 1);
					vegetationDistance = Math.sqrt(((oneCoordinate.x - secondCoord.x) * (oneCoordinate.x
							- secondCoord.x)) + (oneCoordinate.y - secondCoord.y) * (oneCoordinate.y - secondCoord.y));
					if (vegetationDistance < distanceThresHold) oneCoordinate = null;
					i--;
				}
			}
		}
		return oneCoordinate;
	}
	/** Create vegetation with genome given in args */
	public C_Vegetation createVegetation(String vegetationInLandcover) {
		C_Vegetation oneVegetation = null;
		switch (vegetationInLandcover) {
			case str_SHRUB :
				oneVegetation = new C_Vegetation(new C_GenomeBalanites());
				break;
			case str_CROP :
				oneVegetation = new C_Vegetation(new C_GenomeFabacea());
				break;
			case str_GRASS :
				oneVegetation = new C_Vegetation(new C_GenomePoacea());
				break;
			case str_TREE :
				oneVegetation = new C_Vegetation(new C_GenomeAcacia());
				break;
		}
		return oneVegetation;
	}
	/** Author M.Sall 10.2015<br>
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
		switch (event.type) {
			case RAIN_EVENT :// file name example: 199901-PE-Rain.txt or 199901-TPE-Rain.txt
				String url; {
				Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
				calendar.setTime(event.when_Ucalendar);
				// Month of simulation begin in 0 why we need to add 1 in the month value and put 0 before the month value between
				// 0 and 8
				if (calendar.get(Calendar.MONTH) < 9)
					url = RASTER_PATH + rainUrl_suffixRainFile.get(0) + calendar.get(Calendar.YEAR) + "0" + (calendar
							.get(Calendar.MONTH) + 1) + rainUrl_suffixRainFile.get(1);
				else
					url = RASTER_PATH + rainUrl_suffixRainFile.get(0) + calendar.get(Calendar.YEAR) + (calendar.get(
							Calendar.MONTH) + 1) + rainUrl_suffixRainFile.get(1);
				int[][] matriceLue = C_ReadRaster.txtRasterLoader(url);
				int imax = this.landscape.getDimension_Ucell().width;
				int jmax = this.landscape.getDimension_Ucell().height;
				// Change rain value of the cell with the value in the corresponding rain file
				for (int i = 0; i < imax; i++) {
					for (int j = 0; j < jmax; j++) {
						int value = matriceLue[i][j];
						((C_SoilCellSavanna) this.landscape.getGrid()[i][j]).setRainLevel(value);
					}
				}
			}
				break;
			case OWL_EVENT : {
				// Verify that the thing location is within the domain
				if ((coordinateCell_Ucs.x < width_heightRaster_Ukilometer.get(0))
						&& (coordinateCell_Ucs.y < width_heightRaster_Ukilometer.get(1))) {
					C_SoilCellSavanna eventSC = (C_SoilCellSavanna) this.landscape
							.getGrid()[event.whereX_Ucell][event.whereY_Ucell];
					Coordinate oneCoordinate = new Coordinate(eventSC.retrieveLineNo()
							+ C_ContextCreator.randomGeneratorForInitialisation.nextDouble(), eventSC.retrieveColNo()
									+ C_ContextCreator.randomGeneratorForInitialisation.nextDouble());
					// create tree
					C_Vegetation oneTree = createVegetation(str_TREE);
					contextualizeNewThingInSpace(oneTree, oneCoordinate.x, oneCoordinate.y);
					// create nest
					C_Nest oneNest = new C_Nest(eventSC.getAffinity(), eventSC.retrieveColNo(), eventSC
							.retrieveLineNo());
					C_ContextCreator.protocol.contextualizeNewThingInContainer(oneNest, oneTree);
					// create owl
					C_BarnOwl one_barnOwl = this.createBarnOwl();
					one_barnOwl.setRandomAge();
					contextualizeNewThingInContainer(one_barnOwl, oneNest);
					one_barnOwl.energy_Ukcal--;
				}
			}
				break;
			case GERBIL_EVENT : {
				if ((coordinateCell_Ucs.x < width_heightRaster_Ukilometer.get(0))
						&& (coordinateCell_Ucs.y < width_heightRaster_Ukilometer.get(1))) {
					C_RodentGerbil oneRodent = this.createRodent();
					contextualizeNewThingInSpace(oneRodent, event.whereX_Ucell, event.whereY_Ucell);
					oneRodent.setRandomAge();
				}
			}
				break;
		}
		super.manageOneEvent(event);
	}
	@Override
	public String toString() {
		return "protocolGerbil";
	}
	// @Override
	// /** Display the map and dayNight icon if on, remove it if off. Only one map object. The switch can only go from on to off
	// and
	// * vice versa Version author J.Le Fur, 09.2014 rev: 01.2021 MS*/
	// protected void switchDisplayMap() {
	// if (C_Parameters.DISPLAY_MAP) this.facilityMap.contextualize(this.context, this.landscape);
	// super.initProtocol();// manage inspectors and files after everything
	// }
	@Override
	public void readUserParameters() {
		super.readUserParameters();
		String[] raster_Parameters = RASTER_PARAMETERS.get(C_Parameters.RASTER_URL.toLowerCase());
		if (raster_Parameters != null) {
			C_Parameters.RASTER_URL = (raster_Parameters[0]);
			rainUrl_suffixRainFile.set(0, raster_Parameters[1]);
			rainUrl_suffixRainFile.set(1, raster_Parameters[2]);
			width_heightRaster_Ukilometer.set(0, Integer.parseInt(raster_Parameters[3]));
			width_heightRaster_Ukilometer.set(1, Integer.parseInt(raster_Parameters[4]));
			rasterLongitudeWest_LatitudeSouth_Udegree.set(0, Double.parseDouble(raster_Parameters[5]));
			rasterLongitudeWest_LatitudeSouth_Udegree.set(1, Double.parseDouble(raster_Parameters[6]));
		}
	}
	/** TODO JLF 2021.02 temporaire pour réduire la population */
	protected void blackMap0() {
		IndexedIterable<Object> it = RunState.getInstance().getMasterContext().getObjects(C_RodentGerbil.class);
		for (int i = 0; i < 100; i++) ((A_NDS) it.get(i)).setDead(true);
	}
	@Override
	/** Color the map in black to see the overall distribution of burrows<br>
	 * Author J.Le Fur 10.2014 TODO JLF 2014.10 should be in presentation package ? */
	protected void blackMap() {
		for (int i = 0; i < this.landscape.getDimension_Ucell().getWidth(); i++) for (int j = 0; j < this.landscape
				.getDimension_Ucell().getHeight(); j++) {
					this.landscape.getValueLayer().set(BLACK_MAP_COLOR, i, j);
				}
	}
}