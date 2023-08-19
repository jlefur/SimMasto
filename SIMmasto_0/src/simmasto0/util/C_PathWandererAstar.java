package simmasto0.util;

import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;

import data.C_Parameters;
import data.constants.I_ConstantString;
import simmasto0.protocol.C_ProtocolDodel2;
import simmasto0.util.astar.AStar;
import thing.A_VisibleAgent;
import thing.ground.C_SoilCellNode;
import thing.ground.C_SoilCellUrban;
import thing.ground.I_Container;

/** @author P.A. Mboup & J. Le Fur 2014, rev. Mboup 04.2016, added Astar, Le Fur 04.2022 */
public class C_PathWandererAstar implements I_ConstantString {
	//
	// FIELDS
	//
	/** The Astar algorithm */
	private AStar astar = new AStar();
	/** Tableau stockant l'ensemble des sommets du graphe (must be initially null) */
	public C_SoilCellNode[] nodesList;
	/** Nombre de sommets du graphe */
	protected int nodesNumber = 0;
	/** After building the path, targetSoilCell is successively the next node of the path. <br />
	 * Agent is arrived at the next node if currentSoilCell == targetSoilCell. <br />
	 * Agent is arrived at it's final destination when index Of path == length of path <br />
	 * targetSoilCell is initialize by agent currentSoilCell */
	public C_SoilCellNode targetCell;
	/** index to go through the path and get the following node */
	public int indexPath = 0;
	/** number of nodes of each path */
	public int lengthOfPath = 0;
	/** Counter of speed_UcellByTick */
	public double intermediateDistanceTravelled;
	public boolean isPathEnd;
	/** key (start-end cells coordinates) used in the C_ProtocolDodel2.paths treemap */
	private String pathKey;

	//
	// CONSTRUCTOR
	//
	public C_PathWandererAstar() {
		this.indexPath = 0;
		this.lengthOfPath = 0;
		this.isPathEnd = false;

	}
	//
	// METHODS
	//
	/** Search the correct path within the paths already computed and stored, else build new path - Le Fur 05.2022 */
	public void getPath(C_SoilCellUrban currentCell, C_SoilCellUrban targetCell) {
		C_SoilCellUrban streetTarget = this.closestStreet(targetCell);
		C_SoilCellUrban streetStart = this.closestStreet(currentCell);
		this.pathKey = String.format("%03d", streetStart.retrieveLineNo()) + String.format("%03d", streetStart
				.retrieveColNo()) + String.format("%03d", streetTarget.retrieveLineNo()) + String.format("%03d",
						streetTarget.retrieveColNo());
		this.nodesList = C_ProtocolDodel2.paths.get(this.pathKey);
		if (this.nodesList == null) buildBestPath(currentCell, targetCell);
		else this.initPath();
	}
	/** Build the best path between two cells */
	public void buildBestPath(C_SoilCellUrban currentCell, C_SoilCellUrban targetCell) {
		// first get into street
		C_SoilCellUrban streetTarget = this.closestStreet(targetCell);
		C_SoilCellUrban streetStart = this.closestStreet(currentCell);
		// Build the graph
		ArrayList<C_SoilCellNode> nodes = astar.findPath(streetStart, streetTarget);
		// add target concession cell in case path ends to closest street
		int numberNodes = nodes.size();
		if (currentCell != streetStart) numberNodes++;
		if (targetCell != streetTarget) numberNodes++;
		this.nodesList = new C_SoilCellNode[numberNodes];
		int istart = 0;
		if (currentCell != streetStart) {
			this.nodesList[0] = currentCell;
			istart = 1;
		}
		for (int i = 0; i < nodes.size(); i++) {
			this.nodesList[i + istart] = nodes.get(i);
			// Trace the new paths generated in green
			if (C_Parameters.VERBOSE)
				A_VisibleAgent.myLandscape.getValueLayer().set(10, ((C_SoilCellUrban) nodes.get(i)).retrieveLineNo(),
						nodes.get(i).retrieveColNo());// @@vert boutique
		}
		if (targetCell != streetTarget) {
			this.nodesList[numberNodes - 1] = targetCell;
		}
		this.initPath();
		// store the new path
		this.pathKey = String.format("%03d", streetStart.retrieveLineNo()) + String.format("%03d", streetStart
				.retrieveColNo()) + String.format("%03d", streetTarget.retrieveLineNo()) + String.format("%03d",
						streetTarget.retrieveColNo());
		C_ProtocolDodel2.paths.put(this.pathKey, this.nodesList);
		/*
		 * // use the following lines to generate the paths files @see C_ProtocolDodel2.initPaths System.out.print(this.pathKey +
		 * CSV_FIELD_SEPARATOR); for (int i = 0; i < this.nodesList.length; i++) { this.pathKey = String.format("%03d",
		 * this.nodesList[i].retrieveLineNo()) + String.format("%03d", this.nodesList[i].retrieveColNo());
		 * System.out.print(this.pathKey + CSV_FIELD_SEPARATOR); } System.out.println();
		 */
	}
	/** Use either in collecting stored path or when building a new one */
	private void initPath() {
		this.lengthOfPath = this.nodesList.length - 1;
		this.indexPath = 0;
		this.isPathEnd = false;
		if (this.lengthOfPath == 0) {
			this.indexPath--;// In case a graph has got only one city
		}
		else this.targetCell = this.nodesList[this.lengthOfPath - 1]; // select a cell within a destination city
	}
	/** Determine the closest street from the current position: used to get out of concession since Astar algorithm is only
	 * computed with street cells / Le Fur 05.2022
	 * @see C_SoilCellUrban#isBlock() */
	public C_SoilCellUrban closestStreet(C_SoilCellUrban currentCell) {
		C_SoilCellUrban streetCell = null;
		if (currentCell.isStreet()) return currentCell;
		else {
			int gridLength_y = A_VisibleAgent.myLandscape.getGrid()[0].length;
			int gridLength_x = A_VisibleAgent.myLandscape.getGrid().length;
			C_SoilCellUrban testCell = null;
			int y = currentCell.retrieveColNo(), x = currentCell.retrieveLineNo(), i = 1;
			do {
				if (y + i < gridLength_y) {
					testCell = (C_SoilCellUrban) A_VisibleAgent.myLandscape.getGridCell(x, y + i);
					if (testCell.isStreet()) streetCell = testCell;
				}
				else if (y - i >= 0) {
					testCell = (C_SoilCellUrban) A_VisibleAgent.myLandscape.getGridCell(x, y - i);
					if (testCell.isStreet()) streetCell = testCell;
				}
				else if (x + i < gridLength_x) {
					testCell = (C_SoilCellUrban) A_VisibleAgent.myLandscape.getGridCell(x + i, y);
					if (testCell.isStreet()) streetCell = testCell;
				}
				else if (x - i >= 0) {
					testCell = (C_SoilCellUrban) A_VisibleAgent.myLandscape.getGridCell(x - i, y);
					if (testCell.isStreet()) streetCell = testCell;
				}
				i++;
			} while (streetCell == null);
		}
		return streetCell;
	}
	/** Plus juste pour les calculs et permet aussi au HC de rester sur les routes même si leur vitesse est très grande */
	public boolean crossingIntermediateDistance(double distanceTravelled, double speed_UmeterByTick) {
		this.intermediateDistanceTravelled += distanceTravelled;
		return this.intermediateDistanceTravelled < speed_UmeterByTick;
	}
	/** When arrived in any path cell towards the targetCell */
	public Coordinate computeNextNodeAndGetTargetPoint_Umeter(Coordinate targetPoint_Umeter) {
		if (this.indexPath >= this.lengthOfPath) this.isPathEnd = true;
		else this.targetCell = this.nodesList[++this.indexPath];
		return this.targetCell.getCoordinate_Umeter();
	}
	/** Remove references to object fields, discards the associated graph */
	public void discardThis() {
		this.astar = null;
		this.targetCell = null;
		this.nodesList = null;
	}
	//
	// GETTERS
	//
	public boolean hasToSetNextNode(I_Container currentSC) { // arrived in any path cell towards the targetCell
		if (this.isPathEnd) return false;
		else if (this.indexPath == 0) return true;
		return (this.targetCell == null || this.targetCell == currentSC);
	}
}