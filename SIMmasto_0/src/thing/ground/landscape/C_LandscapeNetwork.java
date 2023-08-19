package thing.ground.landscape;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import repast.simphony.context.Context;
import repast.simphony.valueLayer.GridValueLayer;
import simmasto0.protocol.A_Protocol;
import simmasto0.util.C_Graph;
import thing.ground.C_City;
import thing.ground.C_LandPlot;
import thing.ground.C_Market;
import thing.ground.C_Region;
import thing.ground.C_SoilCell;
import thing.ground.C_SoilCellGraphed;
import data.C_Parameters;
//import data.constants.I_ConstantString;
import data.constants.I_ConstantString;

/** Classe encapsulant les informations sur le graphe Dans cette classe nous allons constuire principalement le graphe et sa
 * matrice d'adjacence (edgesList) en utilisant les classes C_Graph et C_SoilCellGraphed
 * @see simmasto0.util.C_Graph
 * @see thing.ground.C_SoilCellGraphed
 * @author Mboup 05/08/2012, rev. JLF 10.2015, was formerly C_RasterGraph */
public class C_LandscapeNetwork extends C_Landscape {
	//
	// FIELDS
	//
	protected List<C_Graph> graphList;// the whole set of graph in the simulation
	protected GridValueLayer graphedValueLayer;// It will be used to represent the ground types
	protected Set<String> graphTypes;
	protected Set<String> areaTypes;
	//
	// CONSTRUCTOR
	//
	public C_LandscapeNetwork(Context<Object> context, String url, String gridValueName, String continuousSpaceName) {
		super(context, url, gridValueName, continuousSpaceName);
		context.addValueLayer(this.graphedValueLayer);
	}
	//
	// METHODS
	//
	/** Use C_soilCellGraphed as containers for the raster matrix<br>
	 * Author: PAM 2013, rev. jlf 10.2015, 11.2015 */
	@Override
	public void createGround(int[][] matriceLue) {
		// super.createGround(matriceLue);// TODO JLF 2015.11 added for test difference with and without ?
		this.graphedValueLayer = new GridValueLayer(proj_gridvalue2, true,
				new repast.simphony.space.grid.WrapAroundBorders(), dimension_Ucell.width, dimension_Ucell.height);
		this.graphList = new ArrayList<C_Graph>();
		this.graphTypes = I_ConstantString.GRAPH_TYPES;// null value
		this.areaTypes = I_ConstantString.AREA_TYPES;// null value
		this.grid = new C_SoilCellGraphed[(int) dimension_Ucell.width][(int) dimension_Ucell.height];
		for (int i = 0; i < this.dimension_Ucell.width; i++) {
			for (int j = 0; j < this.dimension_Ucell.height; j++) {
				getValueLayer().set(matriceLue[i][j], i, j);
				this.grid[i][j] = new C_SoilCellGraphed(matriceLue[i][j], i, j);
			}
		}
	}
	/** Build new graphs and update the old ones for a group of landPlot in the same groundType. <br />
	 * This method can also build a graph (or a portion of graph) contained in another landPlot with optional parameter; <br />
	 * e.g., build a graph corresponding to the portion of road located on the Groundnut Basin.
	 * @param aLandPlotList
	 * @param graphTypeParam optional */
	public void buildGraphsFromLandPlots(TreeSet<C_LandPlot> aLandPlotList, String... graphTypeParam) {
		int nbTotalLine = dimension_Ucell.width, nbTotalColumn = dimension_Ucell.height;
		int i, j, line, column;
		C_SoilCellGraphed tmp0 = null, tmp1 = null;
		C_Graph aGraph;
		TreeSet<C_SoilCell> aSCListOfaNewLandPlot;
		String newGraphType = null; // le groundType du graph area lp : le lp où le graphe ne doit sortir
		String newAreaLpGroundType = aLandPlotList.first().getPlotType();
		if (graphTypeParam.length != 0) // si graphTypeParam est donné, cas des GNT ...
			newGraphType = graphTypeParam[0];
		else
			newGraphType = newAreaLpGroundType; // sinons le groundType est le graphType
		// Build a graph for each landPlot of the landPlotList
		for (C_LandPlot aLandPlot : aLandPlotList) {
			aGraph = new C_Graph(newGraphType, newAreaLpGroundType); // instancie un nouveau
			// Vérifier si le nouveau graphe à construire ne correspondrait pas à un ancien, qu'on doit mettre à jour
			aSCListOfaNewLandPlot = aLandPlot.getCells();
			i = 0;
			List<C_Graph> graphsToRemoveList = new ArrayList<C_Graph>();
			for (C_Graph oneOldGraph : this.graphList) { // Pour chaque ancien graphe
				// Si le groundType et le graphType correspondent à ceux de ce new landPlot, alors
				if (newAreaLpGroundType.equals(oneOldGraph.getGroundType()) && newGraphType.equals(oneOldGraph
						.getGraphType()))
					// on regarde si dans cet ancien graphe il y a un sc qui appartient au new landPlot
					for (C_SoilCellGraphed sc : oneOldGraph.getNodesList()) {
						if (aSCListOfaNewLandPlot.contains(sc)) {
							// Si oui alors on fait pointer le new graph sur un ancien graph une première fois
							if (i == 0) { // suffisant pour une mise à jour par prolongement ou raccourcissement (et ensuite on
											// peut
											// break pour sortir des 2 boucles)
								aGraph = oneOldGraph;
								if (C_Parameters.VERBOSE)
									A_Protocol.event("C_LandscapeNetwork.buildGraphsFromLandPlots", "Keep old "
											+ newGraphType + " graph " + aGraph, isNotError);
								i = 1;
							} // et s'il y a d'autres graphes candidats, ils vont pointer sur aGraph qui pointe déjà sur un ancien
								// graphe, ce qui permet de relier plusieurs graphes

							else {
								graphsToRemoveList.add(oneOldGraph);// nécessaire si le mis à jour peut relier des graphes
								if (C_Parameters.VERBOSE)
									A_Protocol.event("C_LandscapeNetwork.buildGraphsFromLandPlots", "Build new "
											+ newGraphType + " graph " + aGraph, isNotError);
							}
							break;
						}
					}
			}

			// Détruire les graphes obsolètes
			for (C_Graph rGraph : graphsToRemoveList) {
				this.graphList.remove(rGraph);
				for (C_SoilCellGraphed sc : rGraph.getNodesList()) sc.removeNumberInGraph(rGraph);
			}
			// Si i == 1, alors on ne construit pas un nouveau graphe mais on met à jour un ou des anciens
			// Et si i == 0, alors le new graphe ne correspon pas à un ancien
			if (i == 0) this.graphList.add(aGraph); // on l'ajoute donc dans la liste des graphes pour ensuite le construire
			ArrayList<C_SoilCellGraphed> nodeListTmp = this.identifyNodeList(aGraph, aLandPlot, newGraphType);
			int nodeNumber = nodeListTmp.size();
			// Construction de la matrice d'adjencence
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
							if (tmp1.isOfGroundType(newGraphType) && tmp1 != tmp0 && nodeListTmp.contains(tmp1)) {
								// last condition est nécessaire pour le cas GNT (un sc peut
								// etre du meme graphType mais n'appartenant pas à ce graphe)
								edgesMatrix.get(n1).add(tmp1.getNumberInGraph(aGraph));
							}
						}
					}
				}
			}
		}
	}
	/** Constuction ou mise à jour du graphe aGraphe
	 * Identification des noeuds de ce graphe et comptage de son nombre de noeuds 
	 * PAM 2012, rev JLF 01.2022 (encapsulated for C_HumanWalker management)*/
	protected ArrayList<C_SoilCellGraphed> identifyNodeList(C_Graph aGraph, C_LandPlot aLandPlot, String newGraphType) {
		ArrayList<C_SoilCellGraphed> nodesListTmp = new ArrayList<C_SoilCellGraphed>();
		int nodeNumber = 0;
		for (C_SoilCell sc : aLandPlot.getCells()) {
			if (((C_SoilCellGraphed) sc).isOfGroundType(newGraphType)) {
				((C_SoilCellGraphed) sc).setNumberInGraph(aGraph, nodeNumber);
				nodesListTmp.add((C_SoilCellGraphed) sc);
				nodeNumber++;
			}
		}
		// Initialization or re-initialization of this graph with just it's number of nodes :
		aGraph.Init(nodeNumber);
		// Renseignement du tableau des sommets du graphe
		aGraph.setNodesList(nodesListTmp); // nodesListTmp.toArray(aGraph.getNodesList());
		return nodesListTmp;
	}
	/** This method scan all the soilCellMatrix and detect all landPlots with the groundType in param; <br />
	 * taking into account that a soilCell may belong simultaneously to several landPlots with differents groundTypes. <br />
	 * This method doesn't update old landPlots but it build new ones.<br />
	 * @param groundType
	 * @return a list of landPlot author: P.A. Mboup, 2014, rev. JLF 11.2014 */
	public TreeSet<C_LandPlot> identifyTypeLandPlots(String groundType) {
		int i = 0, j = 0, x, y, x0, y0, k;
		// I use HashSet car HashSet.contain() est le plus rapide (je le use bcp ici), je perds l'ordre mais je trie à la fin puis
		// je transforme en ArrayList (qui garde l'ordre d'insertion et est très optimisé)
		Set<C_LandPlot> newLandPlots = new HashSet<C_LandPlot>();
		C_LandPlot newLandPlot;
		C_SoilCellGraphed sc0, scI;
		List<C_SoilCellGraphed> waitingQueue = new ArrayList<C_SoilCellGraphed>();

		for (i = 0; i < dimension_Ucell.getWidth(); i++) { // Scan all the soilCellMatrix
			for (j = 0; j < dimension_Ucell.getHeight(); j++) {
				// If I see a soilCell in this groundType and if this soilCell's landPlot is not in newLandPlotsList then I make
				// it
				// a new landPlot
				sc0 = (C_SoilCellGraphed) grid[i][j];
				if (sc0.isOfGroundType(groundType) && !newLandPlots.contains(sc0.getLandPlot(groundType))) {
					// CREATE LANDPLOT
					if (groundType.equals(CITY_EVENT)) {
						if (sc0.getGroundTypes().contains(MARKET_EVENT)) {
							newLandPlot = new C_Market(this);
							((C_Market) newLandPlot).setMarketDay_UCalendar(sc0.getAffinity());
						}
						else newLandPlot = new C_City(this);
					}
					// PAM 08/12/15 market is considered as a city
					else if (this.graphTypes.contains(groundType)) newLandPlot = new C_Region(this);// a graph contains cities
					else if (this.areaTypes.contains(groundType)) newLandPlot = new C_Region(this);// an area (GNT) contains
																									// cities
					else newLandPlot = new C_LandPlot(this);// e.g., border ?
					newLandPlot.setPlotType(groundType);
					newLandPlots.add(newLandPlot);
					sc0.setLandPlotByGroundType(groundType, newLandPlot); // includes newPlot.addCell(sc0)
					// And I detect and add to the newPlot all soilCells contiguous to this soilCell0 (sc0)
					// So I build completely this new landPlot before starting to build one other
					// la fileDattente CONTIENT LES SOILCELLS QUI SONT DÉJÀ TRAITÉES :
					// Et dans cette list, les sc à partir de la position k
					// sont ceux dont les voisins ne sont pas encore taités
					waitingQueue.clear();
					waitingQueue.add(sc0); // Seul sc0 est traité en ce moment
					k = 0;
					// Je reste sur le même landPlot pour le construire entièrement
					while (k < waitingQueue.size()) {
						// On récupère un à un les soilCells déja traité, pour pouvoir localiser et traiter ses voisins
						sc0 = waitingQueue.get(k);
						x0 = sc0.retrieveLineNo();
						y0 = sc0.retrieveColNo();
						for (x = x0 - 1; x <= x0 + 1; x++) {
							for (y = y0 - 1; y <= y0 + 1; y++) { // avec ces 2 boucles j'accede à tous les 8 voisins de sc0
								if (!(x == x0 && y == y0) && (0 <= x && x < dimension_Ucell.getWidth() && 0 <= y
										&& y < dimension_Ucell.getHeight())) {
									scI = (C_SoilCellGraphed) grid[x][y]; // Pour chaque voisin i (scI), on teste :
									if (scI.isOfGroundType(groundType) && // si c'est du meme ground type que sc0 et
											!waitingQueue.contains(scI)) { // s'il n'est pas encore dans la file d'attente ici
																			// ajout condition groundType
										scI.setLandPlotByGroundType(groundType, newLandPlot); // pour le traiter
										waitingQueue.add(scI); // et l'ajoute dans la file d'attente pour étudier à son tour
																// chaqu'un de ses voisins lui aussi
									}
								}
							}
						}
						k++; // following item in waitingQueue
					}
				} // End of building a newlandPlot. We continue to build others
			}
		} // end of scan soilCellMatrix
		TreeSet<C_LandPlot> newSortedLandPlots = new TreeSet<C_LandPlot>();
		newSortedLandPlots.addAll(newLandPlots);
		return newSortedLandPlots;
	}
	public GridValueLayer getGraphedValueLayer() {
		return graphedValueLayer;
	}
	public void setGraphedValueLayer(int eventCode, int x, int y) {
		this.graphedValueLayer.set(eventCode, x, y);
	}
	public Set<String> getGraphTypes() {
		return graphTypes;
	}
	public Set<String> getAreaTypes() {
		return areaTypes;
	}
}