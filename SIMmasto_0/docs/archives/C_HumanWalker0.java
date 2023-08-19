	/** Add human in the soilCell given in the parameter value */
	public void addHumanWalkerInCell(C_SoilCell oneCell, int nbHumans) {
		for (int i = 0; i < nbHumans; i++) {
			C_HumanWalker0 oneHuman = createHumanWalker();
			oneHuman.setRandomAge();
			oneHuman.setHome(oneCell);
			contextualizeNewThingInSpace(oneHuman, oneCell.getCoordinate_Ucs().x, oneCell.getCoordinate_Ucs().y);
		}
	}
	
	
	/** Fills the context with simple _wandering_ C_Rodent agents (as opposed to C_RodentFossorial's that dig burrows) <br>
	 * The sex ratio is randomly generated , rev. JLF 07.2014 currently unused */
	public void randomlyAddHumanWalkers(int nbAgent) {
		for (int i = 0; i < nbAgent; i++) {
			C_HumanWalker0 agent = createHumanWalker();
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
	
	
	/** Create new human walker */
	public C_HumanWalker0 createHumanWalker() {
		C_HumanWalker0 human = new C_HumanWalker0(new C_GenomeHomoSapiens());
		human.setRandomAge();
		return human;
	}
	
	package thing;

import java.util.ArrayList;
import java.util.TreeSet;

import com.vividsolutions.jts.geom.Coordinate;

import data.C_Parameters;
import data.constants.I_ConstantDodel2;
import simmasto0.C_ContextCreator;
import simmasto0.C_Landscape;
import simmasto0.protocol.C_HumanWalker0;
import simmasto0.util.C_VariousUtilities;
import thing.dna.C_GenomeAmniota;
import thing.dna.C_GenomeHomoSapiens;
import thing.dna.I_DiploidGenome;
import thing.ground.A_Container;
import thing.ground.C_SoilCell;
import thing.ground.I_Container;

/** Urban humans go to specific destinations (@See primaryTarget) (work, home) using streets.<br>
 * Moves are roughly simulated using a brute force algorithm (select the closest free cell from destination as target: if blocked,
 * explore all cells until finding an issue <br>
 * @author J.Le Fur, oct 2019, jan-feb 2020 */
public class C_HumanWalker0 extends C_Human implements I_ConstantDodel2 {
	//
	// DATA
	//
	/** TODO number in source OK: destination positions in Dodel */
	private ArrayList<I_Container> primaryTargets = new ArrayList<I_Container>() {
		private static final long serialVersionUID = 1L;
		{
			C_Landscape scape = A_VisibleAgent.myLandscape;
			add(scape.getGrid()[180][0]);// RoadSouth
			add(scape.getGrid()[31][237]);// RoadNorth
			add(scape.getGrid()[0][0]);// BottomLeft
			add(scape.getGrid()[116][121]);// Market
			add(scape.getGrid()[(int) scape.dimension_Ucell.getWidth() - 1][(int) scape.dimension_Ucell.getHeight()
					- 1]);// TopRight
			// Change cells name
			((A_Container) scape.getGrid()[180][0]).setMyName("ROAD SOUTH");
			((A_Container) scape.getGrid()[31][237]).setMyName("ROAD NORTH");
			((A_Container) scape.getGrid()[0][0]).setMyName("BOTTOM LEFT");
			((A_Container) scape.getGrid()[116][121]).setMyName("MARKET");
			((A_Container) scape.getGrid()[(int) scape.dimension_Ucell.getWidth() - 1][(int) scape.dimension_Ucell
					.getHeight() - 1]).setMyName("TOP RIGHT");
		}
	};
	//
	// FIELDS
	//
	private I_Container primaryTarget = primaryTargets.get(0);// one or the other possibilities
	private TreeSet<I_SituatedThing> visitedCells = new TreeSet<I_SituatedThing>();
	private TreeSet<I_SituatedThing> deadEndCells = new TreeSet<I_SituatedThing>();
	//
	// CONSTRUCTOR
	//
	public C_HumanWalker0(I_DiploidGenome genome) {
		super(genome);
	}
	//
	// OVERRIDEN METHODS
	//
	/** Systematically set desire to TRAVEL 
	 * @version JLF 02.2020 */
	@Override
	public void step_Utick() {
		this.setDesire(TRAVEL);// targets step by step the closest free cell from the primaryTarget
		super.step_Utick();
	}
	/** Switch from one primary target to the other if needed */
	@Override
	protected boolean processTarget() {
		while (this.primaryTarget.equals(this.currentSoilCell))
			this.changePrimaryTarget(this.primaryTargets.get((int) (C_ContextCreator.randomGeneratorForInitialisation
					.nextDouble() * primaryTargets.size())));
		return super.processTarget();
	}
	/** Choose closest from the primary target + tag current cell as visited<br>
	 * If closest cell equals visited cell, then current cell is a dead end<br>
	 * @author JLF 10.2019, 01.2020 */
	@Override
	protected I_SituatedThing setDestination(TreeSet<I_SituatedThing> alternatives) {
		if (this.getDesire().equals(TRAVEL)) {
			if (!alternatives.isEmpty()) {
				I_SituatedThing closest = chooseClosest(this.primaryTarget, alternatives);
				// If aim back to visited cell, then current cell is tagged as a dead end cell
				if (this.visitedCells.contains(closest)) this.deadEndCells.add(this.currentSoilCell);
				this.setTarget(closest);
				this.visitedCells.add(this.currentSoilCell);// Current soil cell becomes visitedCell
				return closest;
			}
			else return null;// no alternatives
		}
		else return super.setDestination(alternatives);
	}
	/** If desire==travel, return surrounding cells only (not compulsory however shortest than using super.perception) :<br>
	 * @author JLF 01-02.2020 */
	@Override
	protected TreeSet<I_SituatedThing> perception() {
		TreeSet<I_SituatedThing> perceivedCells = new TreeSet<I_SituatedThing>();
		// TODO JLF 2019.10 line-col inverted by Pape, to be corrected everywhere
		int positionY = this.retrieveColNo(), positionX = this.retrieveLineNo();
		double gridWidth = C_HumanWalker0.myLandscape.getDimension_Ucell().getWidth();
		double gridHeight = C_HumanWalker0.myLandscape.getDimension_Ucell().getHeight();
		int vision_Ucell = (int) Math.ceil(this.sensing_UmeterByTick / C_Parameters.CELL_WIDTH_Umeter); // m*(m.Ucell^-1)=Ucell
		// Check every surrounding cell
		for (int i = positionX - vision_Ucell; i <= positionX + vision_Ucell; i++)
			if (i >= 0 && i < gridWidth)// inside the grid
				for (int j = positionY - vision_Ucell; j <= positionY + vision_Ucell; j++)
				if (j >= 0 && j < gridHeight) // inside the grid
				{
					perceivedCells.add(A_VisibleAgent.myLandscape.getGrid()[i][j]);
				}
		return perceivedCells;
	}
	/** Return surrounding free cells only; remove dead end, obstacle, current cell or hidden by sight obstacle<br>
	 * @author JLF 01.2020 */
	@Override
	protected TreeSet<I_SituatedThing> deliberation(TreeSet<I_SituatedThing> perceivedThings) {
		TreeSet<I_SituatedThing> freeCells = new TreeSet<I_SituatedThing>();
		if (this.getDesire().equals(TRAVEL)) {// Add to freecells if not dead end, obstacle, current cell or hidden by sight
												// obstacle
			for (I_SituatedThing checkedCell : perceivedThings) {
				if (!deadEndCells.contains(checkedCell)) {
					// Tag obstacles as dead ends
					if (this.isSightObstacle(checkedCell)) this.deadEndCells.add(checkedCell);
					else if (!checkedCell.equals(this.currentSoilCell)) {
						I_SituatedThing sightObstacle = C_VariousUtilities.checkObstacleBefore(this, checkedCell);
						if (sightObstacle == null) freeCells.add(checkedCell);
					}
				}
			}
			return freeCells;
		}
		else return super.deliberation(perceivedThings);
	}
	/** Remove references to primary targets, dead end and visited cells then super */
	@Override
	public void discardThis() {
		this.primaryTarget = null;
		this.deadEndCells=null;
		this.primaryTargets=null;
		this.visitedCells=null;
		super.discardThis();
	}
	/** Temporary (before objectifying doors): check the various obstacle cases in the domain<br>
	 * Author J. Le Fur 01.2020 */
	@Override
	public boolean isSightObstacle(I_SituatedThing thing) {
		if (thing instanceof A_Container) {
			A_Container cell = (A_Container) thing;
			if (cell.getAffinity() == HOUSEDOOR_AFFINITY) return true;
			if (cell.getAffinity() == ROOMDOOR_AFFINITY) return true;
			if (cell.getAffinity() == MARKET_AFFINITY) return true;
			if (cell.getAffinity() == WALL_AFFINITY) return true;
			if (cell.getAffinity() == CONCESSION_AFFINITY) return true;
			if (cell.getAffinity() == CORRIDOR_AFFINITY) return true;
			if (cell.getAffinity() == ROOM_AFFINITY) return true;
			if (cell.getAffinity() == WORKSHOP_AFFINITY) return true;
			if (cell.getAffinity() == ROOMFOOD_AFFINITY) return true;
			if (cell.getAffinity() == SHOPFOOD_AFFINITY) return true;
			if (cell.getAffinity() == HOUSEDOOR_AFFINITY) return true;
		}
		return super.isSightObstacle(thing);
	}
	//
	// METHODS
	//
	public void initializeTraits() {
	    
	}
	public void initializeActivity(String activities) {
	    
	}
	/** Change primary target to destination; reset visited and dead end cells<br>
	 * @author J. Le Fur, 01.2020 */
	private void changePrimaryTarget(I_Container destination) {
		this.primaryTarget = destination;
		this.deadEndCells = new TreeSet<I_SituatedThing>();
		this.visitedCells = new TreeSet<I_SituatedThing>();
	}
	//
	// SETTER AND GETTERS
	//
	/** Provide a random age to agents at initialization */
	public void setRandomAge() {
		// TODO number in source 2020.02 JLF: quickly switch to mature, for 100 human
		long randAge_Uday = Math.round(((C_GenomeAmniota) this.genome).getMaxAge_Uday() / 5.715);
		this.setAge_Uday(randAge_Uday);
	}
	/** For probe display purposes */
	public String getCell0PrimaryTarget() {
		if (this.primaryTarget == null) return "NULL";
		else return this.primaryTarget.toString();
	}
	/** For probe display purposes */
	public String getCell3visited() {
		if (this.visitedCells == null) return "NULL";
		else return this.visitedCells.size() + " / " + visitedCells.toString();
	}
	/** For probe display purposes */
	public String getCell4DeadEnds() {
		if (this.deadEndCells == null) return "NULL";
		return this.deadEndCells.size() + " / " + deadEndCells.toString();
	}
}
