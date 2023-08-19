package thing.ground;

import java.util.Map;
import java.util.TreeSet;

import simmasto0.protocol.A_Protocol;
import simmasto0.util.C_Graph;
import thing.C_HumanCarrier;
import thing.I_SituatedThing;

import java.util.HashMap;

/** a soil cell involved in any type of graph
 * @see C_Graph
 * @author P.A. Mboup 06.2013 */
public class C_SoilCellGraphed extends C_SoilCell {
	private Map<C_Graph, Integer> graphListAndPosition;
	/** The corresponding landplot of each groundtype key */
	private Map<String, C_LandPlot> landPlotByGroundType;

	public C_SoilCellGraphed(int affinity, int i, int j) {
		super(affinity, i, j);
		graphListAndPosition = new HashMap<C_Graph, Integer>();
		landPlotByGroundType = new HashMap<String, C_LandPlot>();
	}

	/** Add this landPlot at the list of landPlots where this soilCell is in and add also this soilCell to the landPlot.
	 * @param groundType
	 * @param landPlot */
	public void setLandPlotByGroundType(String groundType, C_LandPlot landPlot) {
		landPlotByGroundType.put(groundType, landPlot);
		landPlot.addCell(this);
	}

	public C_LandPlot getLandPlot(String groundType) {
		return landPlotByGroundType.get(groundType);
	}

	public void removeLandPlotByGroundType(String groundType) {
		landPlotByGroundType.remove(groundType);
	}

	/** Get the number of this soilCell in the graph in parameter
	 * @param graph
	 * @return the number of this soilCell in the graph */
	public int getNumberInGraph(C_Graph graph) {
		if (graph == null) A_Protocol.event("C_SoilCellGraphed.getNumberInGraph", this.toString(), isError);
		return graphListAndPosition.get(graph);
	}

	// TODO PAM de JLF 10.2015 A graph has a groundType and a graphType ?
	/** Get in this soil cell the graph with the groundType and the graphType. A graph has a groundType and a graphType. <br />
	 * In one SC we can have several graphs in the same graphType and several graphs in the same groundType. <br />
	 * And in one landPlot we can have several graphs (with different graphType OR NOT (no problem because we access to a graph
	 * from one of its soilCells. And a soilCell can never belong to two different graphs or to different landPlot)). So we must
	 * get a graph with both its groundType and graphType. eg: a portion of a road in a GNT ...<br />
	 * groundType has to correspond to an existing graphType
	 * @param groundType
	 * @return graph */
	public C_Graph getGraph(String groundType) {
		for (C_Graph graph : graphListAndPosition.keySet())
			// TODO PAM 2015.10 review double groundtypes (graphtype)
			if (graph.getGroundType().equals(groundType) && graph.getGraphType().equals(groundType)) return graph;
		return null;
	}
	//
	// SETTERS & GETTERS
	//
	/** Set the number of this soilCell in the graph in parameter
	 * @param graph
	 * @param nodeNumber */
	public void setNumberInGraph(C_Graph graph, int nodeNumber) {
		graphListAndPosition.put(graph, nodeNumber);
	}
	public void removeNumberInGraph(C_Graph graph) {
		graphListAndPosition.remove(graph);
	}
	// TODO PAM de JLF 10.2015 put in other place: epiphyte
	public TreeSet<C_HumanCarrier> getCarrierList() {
		TreeSet<C_HumanCarrier> carrierList = new TreeSet<C_HumanCarrier>();
		for (I_SituatedThing thing : this.getFullOccupantList())
			if (thing instanceof C_HumanCarrier) carrierList.add((C_HumanCarrier) thing);
		return carrierList;
	}
}
