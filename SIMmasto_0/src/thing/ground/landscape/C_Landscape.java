/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing.ground.landscape;

import java.awt.Color;
import java.awt.Dimension;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.vividsolutions.jts.geom.Coordinate;

import data.C_Parameters;
import data.C_ReadRaster;
import data.constants.I_ConstantString;
import presentation.epiphyte.C_InspectorOrnithodorosSonrai;
import presentation.epiphyte.C_InspectorPopulation;
import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.SimpleCartesianAdder;
import repast.simphony.space.continuous.WrapAroundBorders;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.valueLayer.GridValueLayer;
import simmasto0.C_ContextCreator;
import simmasto0.protocol.A_Protocol;
import thing.A_Animal;
import thing.A_VisibleAgent;
import thing.C_OrnitodorosSonrai;
import thing.C_Rodent;
import thing.I_SituatedThing;
import thing.dna.I_DiploidGenome;
import thing.ground.C_LandPlot;
import thing.ground.C_SoilCell;
import thing.ground.I_Container;

/** The global container of a given protocol.<br>
 * 1/ Owns a grid/matrix with values ('affinity'), landplots of cells with the same affinity values.<br>
 * Values are read from a file. It can be an ASCII text grid or an image raster. (the image must be in grey levels or in 256 or
 * less colors). affinity values are stored in a gridValueLayer (as well as in the cell container attributes) <br>
 * 2/ Owns a continuous space
 * @see A_Protocol
 * @author Baduel 2009.04, Le Fur 2009.12, Longueville 2011.02, Le Fur 02.2011, 07.2012, 04.2015<br>
 *         rev. JLF 10.2015, 11.2015 - was formerly C_Raster <br>
 *         TODO JLF 2020.04 Should be normally in ground package */
public class C_Landscape implements I_ConstantString {
	//
	// FIELDS
	//
	protected ContinuousSpace<I_SituatedThing> continuousSpace = null;// virtual agents' space (continuous)
	protected I_Container[][] grid;// Matrix of the containers, usually C_SoilCell, associated to the grids and rasters
	public Dimension dimension_Ucell;// width and height of grid
	/** used by the gui and xml files */
	protected GridValueLayer gridValueLayer = null;// grid value layer will be used to represent the area
	protected TreeSet<C_LandPlot> landPlots;// contiguous sets with the same 'affinity' value
	protected static Map<Integer, Color> colorMap;// color associated to each different value of the raster or text grid file
	//
	// CONSTRUCTOR
	//
	/** Constructor of grid ground : creates a gridValueLayer with values (affinity); an equivalent matrix with field agents, a
	 * continuous space where agents have real coordinates. Retrieves also for that the user parameters provided within the GUI */
	public C_Landscape(Context<Object> context, String url, String gridValueName, String continuousSpaceName) {
		// READ RASTER FILE
		int[][] matriceLue;
		if (RASTER_MODE.compareTo("ascii") == 0) {
			System.out.println();
			matriceLue = C_ReadRaster.txtRasterLoader(url);
			A_Protocol.event("C_Landscape constructor", "ASCII grid", isNotError);
		}
		else {
			matriceLue = C_ReadRaster.imgRasterLoader(url);
			A_Protocol.event("C_Landscape constructor", "bitmap", isNotError);
		}
		// IDENTIFY DIMENSIONS
		this.dimension_Ucell = new Dimension(matriceLue.length, matriceLue[0].length);
		A_Protocol.event("C_Landscape constructor", "dimensions dim-ucell: width = " + dimension_Ucell.width
				+ " height = " + dimension_Ucell.height, isNotError);
		// INIT EMPTY GRID OF CONTAINERS
		this.grid = new I_Container[(int) dimension_Ucell.getWidth()][(int) dimension_Ucell.getHeight()];
		A_Protocol.event("C_Landscape constructor", "Initialized empty grid of containers", isNotError);
		// SET GRID VALUE LAYER WITH DIMENSIONS, PUT IT IN THE CONTEXT
		this.gridValueLayer = new GridValueLayer(gridValueName, true,
				new repast.simphony.space.grid.WrapAroundBorders(), dimension_Ucell.width, dimension_Ucell.height);
		context.addValueLayer(this.gridValueLayer);
		A_Protocol.event("C_Landscape constructor", "dimensions gridValueLayer: " + gridValueLayer.getDimensions(),
				isNotError);
		// SET CONTINUOUS SPACE
		ContinuousSpaceFactory continousfactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(
				new TreeMap<String, Object>());
		this.continuousSpace = continousfactory.createContinuousSpace(continuousSpaceName, context,
				new SimpleCartesianAdder(), new WrapAroundBorders(), gridValueLayer.getDimensions().getWidth(),
				gridValueLayer.getDimensions().getHeight());
		A_Protocol.event("C_Landscape constructor", "dimensions continuous space:" + continuousSpace.getDimensions(),
				isNotError);
		A_Protocol.event("C_Landscape constructor", C_Parameters.CELL_WIDTH_Umeter + " metres in one cell", isNotError);

		// Fill both (!) gridValueLayer and C_SoilCell matrices with the value read in the raster
		this.createGround(matriceLue);
	}
	public C_Landscape(Context<Object> context, String[] urlList, String gridValueName, String continuousSpaceName) {}
	//
	// METHODS
	//
	/** Initialize both (!) gridValueLayer and container(ex: C_SoilCell) matrices
	 * @param matriceLue the values read in the raster, bitmap<br>
	 *            rev. JLF 11.2015 */
	public void createGround(int[][] matriceLue) {
		for (int i = 0; i < this.dimension_Ucell.width; i++) {
			for (int j = 0; j < this.dimension_Ucell.height; j++) {
				this.gridValueLayer.set(matriceLue[i][j], i, j);
				this.grid[i][j] = new C_SoilCell(matriceLue[i][j], i, j);
			}
		}
	}
	/** Addition of new agent in simulation. The agent is not sexed in this method. And get the position of it's "parents".
	 * @param parent the parents agent
	 * @param child the new agent it's sex can be provided by its type.<br>
	 *            Author Jean-Emmanuel Longueville 2011-01 */
	public void addChildAgent(A_Animal parent, A_Animal child) { // TODO JLF 2015.08 move to Animal (same as digBurrow)
		double[] new_location = new double[2];
		NdPoint p_Ucs = continuousSpace.getLocation(parent);
		new_location[0] = p_Ucs.getX();
		new_location[1] = p_Ucs.getY();
		parent.getCurrentSoilCell().agentIncoming(child);
		Context<Object> context = ContextUtils.getContext(parent);
		context.add(child);
		// TODO JLF 2015.10 epiphyte in business, move to protocol ?
		if (child instanceof C_Rodent) C_InspectorPopulation.addRodentToBirthList((C_Rodent) child);
		// TODO MS 2020.04 Add birth tick in the corresponding inspector list!
		// TODO MS de JLF 2022.01 reference to OrnitodorosSonrai misplaced + epiphyte in business, move to protocol ?
		else
			if (child instanceof C_OrnitodorosSonrai)
				C_InspectorOrnithodorosSonrai.addTickBirthList((C_OrnitodorosSonrai) child);
		continuousSpace.moveTo(child, new_location);
		child.bornCoord_Umeter = getThingCoord_Umeter(child);
	}
	/** Provide list of all perceptible things in a radius of vision_Umeter (except this).<br>
	 * This method deals with gridcells (i.e., C_SoilCell); we convert the vision of the agent from a vision in meter to a vision
	 * in cells then we obtain the field agent associated to the concerned cells. We then keep only those within their perception
	 * radius
	 * @param thing : I_situated_thing
	 * @param radius_Umeter the perception sphere of agent
	 * @return list of situated things */
	public TreeSet<I_SituatedThing> findObjectsOncontinuousSpace(I_SituatedThing thing, double radius_Umeter) {
		TreeSet<I_SituatedThing> surroundingObjects = new TreeSet<I_SituatedThing>();
		// next declaration aims to avoid ConcurrentModificationException when checking the unwanted agents
		int vision_Ucell = (int) Math.ceil(radius_Umeter / C_Parameters.CELL_WIDTH_Umeter); // m*(m.Ucell^-1)=Ucell
		int gridWidth_Ucell = (int) gridValueLayer.getDimensions().getWidth();
		int gridHeight_Ucell = (int) gridValueLayer.getDimensions().getHeight();
		Coordinate position_Ucell = this.getThingCoord_Ucell(thing);
		GridPoint gridPoint_Ucell = new GridPoint((int) position_Ucell.x, (int) position_Ucell.y);
		int maxX_Ucell = gridPoint_Ucell.getX() + vision_Ucell;
		int maxY_Ucell = gridPoint_Ucell.getY() + vision_Ucell;
		int minX_Ucell = gridPoint_Ucell.getX() - vision_Ucell;
		int minY_Ucell = gridPoint_Ucell.getY() - vision_Ucell;
		// Agent restricts cellsContent to acquaintances strictly within their sensing radius - LeFur 2011, Mboup 2014 JLF 2020 */
		for (int i = minX_Ucell; i <= maxX_Ucell; i++) {
			for (int j = minY_Ucell; j <= maxY_Ucell; j++) {
				if (i >= 0 && j >= 0 && i < gridWidth_Ucell && j < gridHeight_Ucell && ((A_Animal) thing)
						.canPerceiveThing(grid[i][j])) {
					surroundingObjects.add((I_SituatedThing) grid[i][j]);// TODO JLF 2020.01 should use also radius_Umeter ?
					// TODO JLF 2017.08 should work also with getOccupants only - should speed up simulations
					for (I_SituatedThing oneOccupant : grid[i][j].getFullOccupantList()) {
						/** Agent restricts cellsContent to acquaintances strictly within their sensing radius - LeFur 2011 */
						if (continuousSpace.getLocation(oneOccupant) != null) { // Patch: occurs when border drives agent out
							if (thing.canInteractWith(oneOccupant))
								surroundingObjects.add((I_SituatedThing) oneOccupant);
						}
					}
				}
			}
		}
		surroundingObjects.remove(thing);
		return surroundingObjects;
	}

	/** Move the desired object to a Coordinate of continuous space. <br />
	 * CAUTION This method is a patch : using this procedure should be accompanied with the various list update necessary (see
	 * previous uses for suggestion) Version JEL/JLF 2011, rev. PAM 2013, 06.2015, jlf 12.2015 @param thing I_SituatedThing to
	 * move
	 * @param positionVG_Ucell location on continuous space */
	public boolean moveToLocation(I_SituatedThing thing, Coordinate positionVG_Ucell) {
		return continuousSpace.moveTo(thing, positionVG_Ucell.x, positionVG_Ucell.y);
	}

	/** @param thing the thing to move
	 * @param destination the destination container Version Author P.A. MBOUP 22/06/2015, rev. JLF 12.2015 */
	public boolean moveToContainer(I_SituatedThing thing, I_Container destination) {
		if (thing.getCurrentSoilCell().agentLeaving(thing) && destination.agentIncoming(thing)) return true;
		else A_Protocol.event("C_Landscape.moveToContainer", "Could not move" + thing + " to " + destination, isError);
		return false;
	}

	/** Move the agent on the continuous space p_Ucell.getX() + (int) (distanceDeplacement_Ucs.x / SPACE_CELL_SIZE_UCS), Remark:
	 * The ground manager may change the requested move to match the field constraint Link between raster and continuous space<br>
	 * Was formerly moveByDisplacement, JLF 11.2015
	 * @param thing A_Animal
	 * @param moveDistance_Umeter Coordinate in meters SI units */
	public void translate(A_VisibleAgent thing, Coordinate moveDistance_Umeter) {
		// 1. MOVE IN CONTINUOUS SPACE: 1) Retrieve coordinates in continuous space, compute move vector
		NdPoint thingLocation_Ucs = this.continuousSpace.getLocation(thing);
		Coordinate moveDistance_Ucs = new Coordinate(moveDistance_Umeter.x / C_Parameters.UCS_WIDTH_Umeter,
				moveDistance_Umeter.y / C_Parameters.UCS_WIDTH_Umeter); // m/m.cs^-1=cs
		// Check the validity of the displacement, if necessary, this function will modify the given value
		Coordinate distanceDeplacement_Ucs = this.checkGoalPosition(thingLocation_Ucs, moveDistance_Ucs, thing);
		if (C_Parameters.EXCLOS && thing.hasLeftDomain && thing instanceof A_Animal) {
			this.bordure((A_Animal) thing);
		}
		else {
			moveDistance_Umeter.x = moveDistance_Ucs.x * C_Parameters.UCS_WIDTH_Umeter;
			moveDistance_Umeter.y = moveDistance_Ucs.y * C_Parameters.UCS_WIDTH_Umeter; // cs*m.cs^-1 = m
			// Move the agent by mean of the projection's methods
			this.continuousSpace.moveByDisplacement(thing, distanceDeplacement_Ucs.x, distanceDeplacement_Ucs.y);
			if (thing instanceof A_Animal && !((A_Animal) thing).isTrappedOnBoard()) this.checkAndMoveToNewCell(thing);
		}
	}
	/** Compute the half cell of diagonal */
	public static double halfCellDiagonal() {
		return Math.sqrt((C_Parameters.CELL_WIDTH_Umeter / 2) * (C_Parameters.CELL_WIDTH_Umeter / 2)
				+ (C_Parameters.CELL_WIDTH_Umeter / 2) * (C_Parameters.CELL_WIDTH_Umeter / 2));
	}
	/** 2. Move thing IN THE GRID if previousCell and newCell are not same coordinates (line & column)<br>
	 * @version J.Le Fur 01.2018 */
	public void checkAndMoveToNewCell(A_VisibleAgent thing) {
		// !!! do not use previousCell==newCell or previousCell.equals(newCell) )
		I_Container previousCell = thing.getCurrentSoilCell();
		I_Container newCell = grid[(int) getThingCoord_Ucell(thing).x][(int) getThingCoord_Ucell(thing).y];
		if (previousCell != newCell) this.moveToContainer(thing, newCell);
	}
	/** Verify if point is in grid<br>
	 * author MS 2019.08<br>
	 * TODO JLF&MS 2019.08 verify redundancy with @see checkGoalPosition */
	public boolean isPointInGrid(Coordinate onePoint) {
		if (onePoint == null) return false;
		return (onePoint.x >= 0.) && (this.getDimension_Ucell().width > onePoint.x) && (this
				.getDimension_Ucell().height > onePoint.y) && (onePoint.y >= 0.);
	}
	/** Check next position and backward if leaving the continuous space.
	 * @param currentPosition_Ucs
	 * @param moveDistance_Ucs
	 * @return The distance of displacement in cs */
	private Coordinate checkGoalPosition(NdPoint currentPosition_Ucs, Coordinate moveDistance_Ucs,
			A_VisibleAgent agent) {
		NdPoint goalPoint_Ucs = new NdPoint(currentPosition_Ucs.getX() + moveDistance_Ucs.x, currentPosition_Ucs.getY()
				+ moveDistance_Ucs.y);
		Coordinate dep_Ucs = new Coordinate();
		dep_Ucs.x = moveDistance_Ucs.x;
		dep_Ucs.y = moveDistance_Ucs.y;
		// we test the four cases where the agent is going out and if needed, we put them at one unit of Continuous space of the
		// boundaries and we reverse their displacements
		if (goalPoint_Ucs.getX() < 0 && moveDistance_Ucs.x < 0) {
			moveDistance_Ucs.x = -moveDistance_Ucs.x;
			dep_Ucs.x = -currentPosition_Ucs.getX() + 1;
			agent.hasLeftDomain = true;
		}
		else if (goalPoint_Ucs.getX() >= continuousSpace.getDimensions().getWidth() && moveDistance_Ucs.x > 0) {
			moveDistance_Ucs.x = -moveDistance_Ucs.x;
			dep_Ucs.x = (continuousSpace.getDimensions().getWidth() - 1) - currentPosition_Ucs.getX();
			agent.hasLeftDomain = true;
		}
		else if (goalPoint_Ucs.getY() < 0 && moveDistance_Ucs.y < 0) {
			moveDistance_Ucs.y = -moveDistance_Ucs.y;
			dep_Ucs.y = -currentPosition_Ucs.getY() + 1;
			agent.hasLeftDomain = true;
		}
		else if (goalPoint_Ucs.getY() > continuousSpace.getDimensions().getHeight() && moveDistance_Ucs.y > 0) {
			moveDistance_Ucs.y = -moveDistance_Ucs.y;
			dep_Ucs.y = (continuousSpace.getDimensions().getHeight() - 1) - currentPosition_Ucs.getY();
			agent.hasLeftDomain = true;
		}
		return dep_Ucs;
	}
	/** Results in the exit of an agent and the entry of a new one in a random point at the edges of the simulation space. author
	 * Longueville 2011, rev. JLF 11.2015
	 * @param animalLeavingLandscape will be removed and a new one of the same class will enter */
	private void bordure(A_Animal animalLeavingLandscape) {// TODO JLF 2015.11 change name (agentLeaving)
		// Identify on which side the new animal will appear
		if (animalLeavingLandscape.isDead()) return;// Could leave landscape in the middle of a step.
		double[] newLocation = new double[2];
		int x = 0, y = 0;
		int randkey = (int) (C_ContextCreator.randomGeneratorForInitialisation.nextDouble() * 4);// TODO number in source
		switch (randkey) {
			case 0 : // bottom
				x = (int) (C_ContextCreator.randomGeneratorForInitialisation.nextDouble() * this.dimension_Ucell
						.getWidth());
				y = 0;
				newLocation[0] = x * C_Parameters.CELL_SIZE_UcontinuousSpace; // cell / cs.cell^-1 -> cs;
				newLocation[1] = y * C_Parameters.CELL_SIZE_UcontinuousSpace; // cell / cs.cell^-1 -> cs;
				break;
			case 1 : // left
				x = 0;
				y = (int) (C_ContextCreator.randomGeneratorForInitialisation.nextDouble() * this.dimension_Ucell
						.getHeight());
				newLocation[0] = x * C_Parameters.CELL_SIZE_UcontinuousSpace; // cell / cs.cell^-1 -> cs;
				newLocation[1] = y * C_Parameters.CELL_SIZE_UcontinuousSpace; // cell / cs.cell^-1 -> cs;
				break;
			case 2 : // top
				x = (int) (C_ContextCreator.randomGeneratorForInitialisation.nextDouble() * this.dimension_Ucell
						.getWidth());
				y = (int) (this.dimension_Ucell.getHeight() - 1);
				newLocation[0] = x * C_Parameters.CELL_SIZE_UcontinuousSpace; // cell / cs.cell^-1 -> cs;
				newLocation[1] = y * C_Parameters.CELL_SIZE_UcontinuousSpace; // cell / cs.cell^-1 -> cs;
				break;
			case 3 : // right
				x = (int) (this.dimension_Ucell.getWidth() - 1);
				y = (int) (C_ContextCreator.randomGeneratorForInitialisation.nextDouble() * this.dimension_Ucell
						.getHeight());
				newLocation[0] = x * C_Parameters.CELL_SIZE_UcontinuousSpace; // cell / cs.cell^-1 -> cs;
				newLocation[1] = y * C_Parameters.CELL_SIZE_UcontinuousSpace; // cell / cs.cell^-1 -> cs;
				break;
			default :
				A_Protocol.event("C_Landscape.bordure", "Nonsense", isError);
				break;
		}

		// The outcomer dies, a new incomer enters
		Context<I_SituatedThing> context = ContextUtils.getContext(animalLeavingLandscape);// Keep context to introduce incomer
		animalLeavingLandscape.setDead(true);// Kill the leaving agent

		// Create new animal of the same class //
		A_Animal incomer = null;
		Class<? extends A_VisibleAgent> animalClass = animalLeavingLandscape.getClass();
		Class<? extends I_DiploidGenome> genomeClass = animalLeavingLandscape.getGenome().getClass();
		try {
			Constructor<?> constructor = animalClass.getDeclaredConstructor(I_DiploidGenome.class);
			constructor.setAccessible(true);
			incomer = (A_Animal) constructor.newInstance(genomeClass.newInstance());
			// Production code should handle these exceptions more gracefully
		}
		catch (InstantiationException x1) {
			x1.printStackTrace();
		}
		catch (IllegalAccessException x1) {
			x1.printStackTrace();
		}
		catch (InvocationTargetException x1) {
			x1.printStackTrace();
		}
		catch (NoSuchMethodException x1) {
			x1.printStackTrace();
		}

		// Initialize incomer
		incomer.hasLeftDomain = false;
		incomer.hasEnteredDomain = false;
		incomer.setHasToLeaveFullContainer(true);
		incomer.hasToSwitchFace = true;
		incomer.setAge_Uday(animalLeavingLandscape.getAge_Uday());
		context.add(incomer);
		// JLF 2016.05 cannot add rodent to birthList since it has age of sexual maturity (see above) ?
		if (incomer instanceof C_Rodent) C_InspectorPopulation.addRodentToList((C_Rodent) incomer);
		continuousSpace.moveTo(incomer, newLocation);
		grid[x][y].agentIncoming(incomer);
		incomer.bornCoord_Umeter = getThingCoord_Umeter(incomer);

		// Aim toward landscape center
		incomer.hasEnteredDomain = true;
		incomer.setTarget(grid[(int) (continuousSpace.getDimensions().getWidth() / 2)][(int) (continuousSpace
				.getDimensions().getHeight() / 2)]);
		incomer.computeNextMoveToTarget();
		incomer.actionDisperse();
		incomer.discardTarget();

		if (C_Parameters.VERBOSE)
			A_Protocol.event("C_Landscape:bordure", "BORDURE event: " + animalLeavingLandscape + " leaves landscape at "
					+ animalLeavingLandscape.getCurrentSoilCell().retrieveLineNo() + "," + animalLeavingLandscape
							.getCurrentSoilCell().retrieveColNo() + " / " + incomer + " enters at " + x + "," + y,
					isNotError);
	}
	/** Scan all SoilCells and allocate them to a given landPlot, create a new one each time it changes. <br />
	 * This method doesn't update old landPlots but it builds new ones. And old ones remain in the context. <br />
	 * If necessary, it is possible to "update" the old see (C_RasterGraphManager.identifyTypeLandPlots()).<br />
	 * 25/09/2013
	 * @param context
	 * @see thing.ground.landscape.C_LandscapeNetwork#identifyTypeLandPlots */
	public TreeSet<C_LandPlot> identifyAffinityLandPlots(Context<Object> context) {
		int i = 0, j = 0, x, y, x0, y0, k, affinity0;
		this.landPlots = new TreeSet<C_LandPlot>();
		C_LandPlot newPlot;
		C_SoilCell sc0, scI;
		List<C_SoilCell> fileDattente = new ArrayList<C_SoilCell>();
		Set<C_SoilCell> fileDattente2 = new HashSet<C_SoilCell>();
		C_SoilCell oneSoilCell = null;
		// Scan all the soilCellMatrix
		for (i = 0; i < this.dimension_Ucell.getWidth(); i++) {
			for (j = 0; j < this.dimension_Ucell.getHeight(); j++) {
				oneSoilCell = (C_SoilCell) grid[i][j];
				// if soil cell has no affinity landplot or affinity landplot init list does not contain soil cell then make a new
				// land plot for the soil cell.
				if (oneSoilCell.getMyLandPlot() == null || !landPlots.contains(oneSoilCell.getMyLandPlot())) {
					// Alors je lui fabrique un nouveau landPlot
					newPlot = new C_LandPlot(this);
					context.add(newPlot);

					landPlots.add(newPlot);
					sc0 = oneSoilCell;
					affinity0 = sc0.getAffinity();
					// Je traite sc0
					sc0.setAffinityLandPlot(newPlot);
					newPlot.setAffinity(affinity0);
					// And I detect and add to the newPlot all soilCells contiguous to this soilCell0 (sc0)
					// So I build completely this new landPlot befor stating to build one other
					// la fileDattente CONTIEN LES SOILCELL QUI SONT DÉJÀ TRAITÉS :
					// Et dans cette list, les sc à partir de la position k
					// sont ceux dont les voisins ne sont pas encore taités
					fileDattente.clear();
					fileDattente.add(sc0); // Seul sc0 est traité en ce moment
					fileDattente2.clear();
					fileDattente2.add(sc0);
					k = 0;
					// Je reste sur le même landPlot pour le construire entièrement
					while (k < fileDattente.size()) {
						// On récupère un à un les soilCells déja traité, pour pouvoir localiser et traiter ses voisins
						sc0 = fileDattente.get(k);
						x0 = sc0.retrieveLineNo();
						y0 = sc0.retrieveColNo();
						for (x = x0 - 1; x <= x0 + 1; x++) {
							for (y = y0 - 1; y <= y0 + 1; y++) { // avec ces 2 boucles j'accede à tous les 8 voisins de sc0
								if (!(x == x0 && y == y0) && (0 <= x && x < dimension_Ucell.getWidth() && 0 <= y
										&& y < dimension_Ucell.getHeight())) {
									scI = (C_SoilCell) grid[x][y]; // Pour chaque voisin scI de sc0, on teste :
									if (scI.getAffinity() == affinity0 && // si c'est de la même affinité que sc0 et
											!fileDattente2.contains(scI)) { // s'il n'est pas encore dans la file d'attente
										scI.setAffinityLandPlot(newPlot); // pour le traiter
										fileDattente.add(scI); // et l'ajoute dans la file d'attente pour étudier à son tour
																// chaqu'un de ses voisins
										fileDattente2.add(scI);
									}
								}
							}
						}
						k++; // pour prendre l'élément suivant de la file d'attente
					}
					newPlot.setThisName(null);
				}
				// End of building a newlandPlot. We continue to build others
			}
		}
		A_Protocol.event("C_Landscape.identifyAffinityLandPlots(): ", landPlots.size() + " land plot(s) initialized",
				isNotError);
		return landPlots;
	}
	/** TODO JLF 2014.12 should be moved to hasToChange for a C_SoilCell ? */
	public void resetCellsColor() {
		for (int i = 0; i < this.dimension_Ucell.getWidth(); i++) {
			for (int j = 0; j < this.dimension_Ucell.getHeight(); j++) {
				this.gridValueLayer.set(grid[i][j].getAffinity(), i, j);
			}
		}
		C_Parameters.BLACK_MAP = false;
	}
	//
	// GETTERS
	//
	/** Getter of the color colorMap.
	 * @return map */
	public static Map<Integer, Color> getColormap() {
		return colorMap;
	}

	/** Gives the location of the agent in matrix coordinates.
	 * @param thing I_situated_thing
	 * @return Coordinate of location in Ucell */
	protected Coordinate getThingCoord_Ucell(I_SituatedThing thing) {
		NdPoint location_Ucs = continuousSpace.getLocation(thing);
		Coordinate location_Ucell = new Coordinate(location_Ucs.getX() / C_Parameters.CELL_SIZE_UcontinuousSpace,
				location_Ucs.getY() / C_Parameters.CELL_SIZE_UcontinuousSpace); // cs / cs.cell^-1= cell
		// MS & PAM 2016.10 get if position not exist
		if (dimension_Ucell.width <= location_Ucell.x) location_Ucell.x -= 1;
		if (dimension_Ucell.height <= location_Ucell.y) location_Ucell.y -= 1;
		return location_Ucell;
	}

	/** Return the current coordinate of the agent in meter
	 * @param thing I_situated_thing
	 * @return coordinate of the agent in meter */
	public Coordinate getThingCoord_Umeter(I_SituatedThing thing) {
		// we call the ground manager to ask him the agent coordinate
		Coordinate co = getThingCoord_Ucs(thing);
		return new Coordinate(co.x * C_Parameters.UCS_WIDTH_Umeter, co.y * C_Parameters.UCS_WIDTH_Umeter);// cs*m.cs^-1
	}
	/** Coordinate getter in unit of Continuous Space
	 * @param thing any object
	 * @return Object coordinate in continuous space unit. <br>
	 *         rev. jlf 02.2018 */
	public Coordinate getThingCoord_Ucs(I_SituatedThing thing) {
		NdPoint location = continuousSpace.getLocation(thing);
		if (location != null) return new Coordinate(location.getX(), location.getY());
		else// return null;
		{
			// Soil cells normally return null (not contextualized in space)
			if (thing instanceof C_SoilCell) return thing.getCoordinate_Ucs();
			// else problem
			else {
				A_Protocol.event("C_Landscape.getThingCoord_Ucs", "PB " + thing, isError);
				return null;
			}
		}
	}
	public I_Container[][] getGrid() {
		return grid;
	}
	public void setGridCell(int line, int col, I_Container cell) {
		this.grid[line][col] = cell;
	}
	public I_Container getGridCell(int line, int col) {
		return this.grid[line][col];
	}
	public GridValueLayer getValueLayer() {
		return gridValueLayer;
	}
	public Dimension getDimension_Ucell() {
		return dimension_Ucell;
	}
	public TreeSet<C_LandPlot> getAffinityLandPlots() {
		return landPlots;
	}
	public ContinuousSpace<I_SituatedThing> getContinuousSpace() {
		return continuousSpace;
	}
}