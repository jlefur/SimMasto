package simmasto0.util;

import java.util.ArrayList;
import thing.ground.C_SoilCell;
import thing.ground.C_SoilCellGraphed;
import thing.ground.I_Container;
import com.vividsolutions.jts.geom.Coordinate;

/** @author P.A. Mboup & J. Le Fur 2014, rev. Mboup 04.2016 */
public class C_PathWanderer {
	//
	// FIELDS
	//
	/** Graph in which the agent moves about */
	public C_Graph myGraph;
	/** All node of a graph has a number; path is an array which contains successively the number of nodes of the shortest path
	 * between the current soil cell and the destination soil cell(last target) (when a agent leave a node of the path, the next
	 * node is what I call next target or targetPoint_Umeter ) */
	public ArrayList<Integer> path;
	/** After building the path, targetSoilCell is successively the next node of the path. <br />
	 * Agent is arrived at the next node if currentSoilCell == targetSoilCell. <br />
	 * Agent is arrived at it's final destination when index Of path == length of path <br />
	 * targetSoilCell is initialize by agent currentSoilCell */
	public C_SoilCellGraphed targetSoilCell;
	/** index to go through the path and get the following node */
	public int indexPath = 0;
	/** number of nodes of each path */
	public int lengthOfPath = 0;
	/** Counter of speed_UcellByTick */
	public double intermediateDistanceTravelled;
	public double track_slow_factor;
	public boolean pathEnd;
	//
	// CONSTRUCTOR
	//
	public C_PathWanderer(C_SoilCell currentSoilCell) {
		this.path = new ArrayList<Integer>();
		this.indexPath = 0;
		this.lengthOfPath = 0;
		this.track_slow_factor = 1;
		this.pathEnd = false;
	}
	//
	// METHODS
	//
	/** Remove references to object fields, discards the associated graph */
	public void discardThis() {
		this.path = null;
		this.myGraph.discardThis();
		this.myGraph = null;
		this.path = null;
		this.targetSoilCell = null;
	}
	/** Plus juste pour les calculs et permet aussi au HC de rester sur les routes même si leur vitesse est très grande */
	public boolean crossingIntermediateDistance(double distanceTravelled, double speed_UmeterByTick) {
		this.intermediateDistanceTravelled += distanceTravelled;
		return this.intermediateDistanceTravelled < speed_UmeterByTick;
	}
	/** Force agent to reach destination (useful just before updating the ground) */
	public void prepareToReachPathEndNow() {
		if (this.indexPath != this.lengthOfPath) {
			this.targetSoilCell = this.myGraph.getNextNode(this.path.get(this.lengthOfPath));
			this.indexPath = this.lengthOfPath;
			pathEnd = true;
		}
	}
	/** When arrived in any path cell towards the targetCell */
	public Coordinate computeNextNodeAndGetTargetPoint_Umeter(Coordinate targetPoint_Umeter) {
		if (this.indexPath >= this.lengthOfPath) this.pathEnd = true;
		else this.targetSoilCell = this.myGraph.getNextNode(this.path.get(++this.indexPath));
		return this.targetSoilCell.getCoordinate_Umeter();
	}
	// TODO PAM 2016.11 prise en compte la durée du trajet (encapsulation de cette méthode. Elle était dans la méthode buildPath
	// suivante)
	/** Build the best path between two cells and inform the pathWeightTab */
	public ArrayList<Integer> builgPath0(C_SoilCell currentSoilCell, C_SoilCell targetSoilCell) {
		return this.myGraph.buildPath(((C_SoilCellGraphed) currentSoilCell).getNumberInGraph(this.myGraph),
				((C_SoilCellGraphed) targetSoilCell).getNumberInGraph(this.myGraph));
	}
	// TODO PAM 2016.11 prise en compte la durée du trajet (voir todo précédent)
	/** Build the best path between two cells */
	public void buildPath(C_SoilCell currentSoilCell, C_SoilCell targetSoilCell) {
		this.targetSoilCell = (C_SoilCellGraphed) targetSoilCell; // select a cell within a destination city
		this.path = builgPath0(currentSoilCell, targetSoilCell);
		this.lengthOfPath = this.path.size() - 1;
		this.indexPath = 0;
		this.pathEnd = false;
		if (this.lengthOfPath == 0) this.indexPath--;// In case a graph has got only one city
	}
	/** Account for duration of path - PAM 2016.11 */
	public double getPathWeight(C_SoilCell currentSoilCell, C_SoilCell targetSoilCell) {
		int entree = ((C_SoilCellGraphed) currentSoilCell).getNumberInGraph(this.myGraph);
		int sortie = ((C_SoilCellGraphed) targetSoilCell).getNumberInGraph(this.myGraph);
		if (this.myGraph.pathWeightTab.get(entree) == null || this.myGraph.pathWeightTab.get(entree).get(sortie) == null)
			builgPath0(currentSoilCell, targetSoilCell);
		return this.myGraph.pathWeightTab.get(entree).get(sortie);
	}
	//
	// GETTERS
	//
	public boolean getPathEnd() {// When a carrier has reached a destination city
		return this.pathEnd;
	}
	public boolean hasToSetNextNode(I_Container currentSC) { // arrived in any path cell towards the targetCell
		if (getPathEnd()) return false;
		else if (this.indexPath == 0) return true;
		return this.targetSoilCell == currentSC;
	}
}
