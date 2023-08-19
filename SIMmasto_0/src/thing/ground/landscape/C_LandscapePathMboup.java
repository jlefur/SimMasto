package thing.ground.landscape;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import data.constants.I_ConstantDodel2;
import repast.simphony.context.Context;
import simmasto0.util.C_Graph;
import thing.ground.C_LandPlot;
import thing.ground.C_SoilCell;
import thing.ground.C_SoilCellGraphed;

public class C_LandscapePathMboup extends C_LandscapeNetwork implements I_ConstantDodel2 {



	public C_LandscapePathMboup(Context<Object> context, String url, String gridValueName, String continuousSpaceName) {
		super(context, url, gridValueName, continuousSpaceName);
		// TODO Auto-generated constructor stub
	}
	/** Constuction ou mise à jour du graphe aGraphe Identification des noeuds de ce graphe et comptage de son nombre de noeuds
	 * @author PAM 2012, rev JLF 01.2022 */
	@Override
	protected ArrayList<C_SoilCellGraphed> identifyNodeList(C_Graph aGraph, C_LandPlot aLandPlot, String newGraphType) {
		ArrayList<C_SoilCellGraphed> nodesListTmp = new ArrayList<C_SoilCellGraphed>();
		int nodeNumber = 0;
		for (C_SoilCell sc : aLandPlot.getCells()) {
			((C_SoilCellGraphed) sc).setNumberInGraph(aGraph, nodeNumber);
			nodesListTmp.add((C_SoilCellGraphed) sc);
			nodeNumber++;
		}
		// Initialization or re-initialization of this graph with just it's number of nodes :
		aGraph.Init(nodeNumber);
		// Renseignement du tableau des sommets du graphe
		aGraph.setNodesListSimple(nodesListTmp); // nodesListTmp.toArray(aGraph.getNodesList());
		return nodesListTmp;
	}
	/** Build one graph / adapted from buildGraphsFromLandPlots
	 * @see C_LandScapeNetwork.buildGraphsFromLandPlots
	 * @param aLandPlot treeSet for compatibility however, here contain only one graph
	 * @param graphTypeParam for compatibility @author PAM, rev JLF 01.2022 */
	public C_Graph buildGraph(C_LandPlot aLandPlot) {
		// Initialization
		int nbTotalLine = dimension_Ucell.width, nbTotalColumn = dimension_Ucell.height, i, j, line, column;
		C_SoilCellGraphed tmp0 = null, tmp1 = null;
		C_Graph aGraph = new C_Graph("STREET", "STREET"); // Build a graph for aLandPlot

		ArrayList<C_SoilCellGraphed> nodeListTmp = this.identifyNodeList(aGraph, aLandPlot, "STREET");
		int nodeNumber = nodeListTmp.size();
		// Construction de la matrice d'adjacence
		Map<Integer, List<Integer>> edgesMatrix = aGraph.getEdgesMatrix();
		for (int n1 = 0; n1 < nodeNumber; n1++) {
			tmp0 = aGraph.getNode(n1);
			i = tmp0.retrieveLineNo();
			j = tmp0.retrieveColNo();
			edgesMatrix.put(n1, new ArrayList<Integer>());
			for (line = i - 1; line <= i + 1; line++) {
				for (column = j - 1; column <= j + 1; column++) {
					if (0 <= line && line < nbTotalLine && 0 <= column && column < nbTotalColumn) {
						tmp1 = (C_SoilCellGraphed) this.grid[line][column];
						if (tmp1 != tmp0 && nodeListTmp.contains(tmp1)) {
							// last condition est nécessaire pour le cas GNT (un sc peut
							// être du même graphType mais n'appartenant pas à ce graphe)
							edgesMatrix.get(n1).add(tmp1.getNumberInGraph(aGraph));
						}
					}
				}
			}
		}
		System.out.println("C_LandscapePath.buildGraph()" + "-" + "/" + edgesMatrix.size());
		return aGraph;
	}

}
