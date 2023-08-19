package simmasto0.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import thing.ground.C_SoilCellGraphed;
import data.constants.I_ConstantTransportation;

/** ground structure with the properties of an unweighted (non étiqueté) graph.
 * @see simmasto0#C_LandscapeNetwork()
 * @author P.A. Mboup 08.2012, rev. pam 11.2013, JLF 09.2014, rev. pam 11.2015 */
public class C_Graph implements I_ConstantTransportation {
	/** The type of this graph like road, rail, river ... */
	protected String graphType;
	/** The ground type of this graph like road, rail, river, GNT ... */
	// ex graph dans un GNT
	protected String groundType;
	/** Nombre de sommets du graphe */
	protected int nodesNumber = 0;
	/** Tableau stockant l'ensemble des sommets du graphe (must be initially null) */
	protected C_SoilCellGraphed[] nodesList;
	protected Map<Integer, Double> weightListByNodeNumber;
	/** Tableau stockant l'ensemble des edgesList du graphe (matrice d'adjacence). <br />
	 * Ce tableau est une matrice triangulaire pour gagner de l'espace */
	// protected ArrayList<boolean[]> edgesMatrix;
	protected Map<Integer, List<Integer>> edgesMatrix;
	protected Map<Integer, Integer[]> precedentsMatrix;
	protected Map<Integer, Map<Integer, ArrayList<Integer>>> pathTab;
	// PAM 04/11/2016 champ pour stocker le poids global des chemins
	protected Map<Integer, Map<Integer, Double>> pathWeightTab;
	//
	// CONSTRUCTOR
	//
	public C_Graph(String graphType, String groundType) {
		this.graphType = graphType;
		this.groundType = groundType;
	}
	/** Remove references to last container left, targeted container and eggs */
	public void discardThis() {
		this.nodesList = null;
		this.weightListByNodeNumber=null;
		this.edgesMatrix=null;
		this.precedentsMatrix=null;
		this.pathTab=null;
		this.pathWeightTab=null;
	}
	/** Création du graphe avec en paramètre son nombre de sommets. ie initialisation du nombre de sommet, du tableau des sommets
	 * et de la matrice d'adjacence à false. Tout le reste de la construction sera fait dans C_LandscapeNetwork.
	 * @param nbs : NombreDeSommetDuGraphe */
	public void Init(int nbs) {
		nodesNumber = nbs;
		nodesList = new C_SoilCellGraphed[nbs];
		edgesMatrix = new HashMap<Integer, List<Integer>>();
		precedentsMatrix = new HashMap<Integer, Integer[]>();
		pathTab = new HashMap<Integer, Map<Integer, ArrayList<Integer>>>();
		pathWeightTab = new HashMap<Integer, Map<Integer, Double>>();
		weightListByNodeNumber = new HashMap<Integer, Double>();
	}

	/** Pour les graphes pondérés, cette méthode construit pour un sommet d'entrée Se, le tableau des sommets précédents de chaque
	 * sommet de sortie jusqu'à Se, Et ajoute ce tableau dans la matrice des sommets précédents<br>
	 * Cette méthode ne sera appelée qu'une seule fois avec Se. Cette méthode marche pour les graphes non pondérés aussi, mais
	 * dans cette situation, utiliser dijkstraForUnWeightedGraph() qui est plus rapide.
	 * @see #dijkstraForUnWeightedGraph(int)
	 * @param entree */
	public void dijkstraForWeightedGraph(int entree) {
		Set<Integer> marquee = new HashSet<Integer>(); // tableau contenant sommets marqué
		double[] distance = new double[nodesNumber]; // label : tableau contenant les distances
		Integer[] precedentTab = new Integer[nodesNumber]; // Pour le sommet d'entrée Se, ce tableau contient pour chaque sommet
															// de
		Map<Double, Integer> nodeByLabel = new TreeMap<>();
		// Map<Double, Integer> nodeByLabel_fast = new HashMap<>();
		int i = 0, n1;
		double dist, plusPtiLabel, divisor = 1000000.; // divisor c'est pour permettre les doublons dans nodeByLabel1 et dans
														// nodeByLabel
		// Ex. si noeud n1 = 34, son label = 15 alors je mets à la place de 15, 15.000035 dans le map on aura : 15.000035 -> 35
		// Si noeud n2 = 35, son label = 15 alors je mets à la place de 15, 15.000036 dans le map on aura : 15.000036 -> 36
		distance[entree] = weightListByNodeNumber.get(entree);
		precedentTab[entree] = entree;
		nodeByLabel.put(0.0 + entree / divisor, entree);
		while (i++ < nodesNumber) {
			// nodeByLabel.clear();
			// nodeByLabel.putAll(nodeByLabel_fast); // pour trier suivant le plus petit label
			plusPtiLabel = nodeByLabel.keySet().iterator().next(); // pour récupérer le noeud ayant le plus petit label
			n1 = nodeByLabel.get(plusPtiLabel); // en O(1) en temps
			nodeByLabel.remove(plusPtiLabel); // en O(1) en temps
			marquee.add(n1);
			for (int n2 : edgesMatrix.get(n1)) {
				if (!marquee.contains(n2)) {
					if ((dist = distance[n1] + weightListByNodeNumber.get(n2)) < distance[n2] || distance[n2] == 0) {// car j'ai
																														// pas
																														// initialiser
																														// les
																														// distances
																														// à
																														// l'infini
																														// (Double.MAX_VALUE)
						distance[n2] = dist;
						precedentTab[n2] = n1;
						nodeByLabel.put(dist + n2 / divisor, n2);
					}
				}
			}
		}
		precedentsMatrix.put(entree, precedentTab);
	}
	/** Pour les graphes non pondérés (poid = 1), cette méthode construit pour un sommet d'entrée Se, le tableau des sommets
	 * précédents de chaque sommet de sortie jusqu'à Se, Et ajoute ce tableau dans la la matrice des sommets précédents comme ça
	 * cette méthode ne sera appelée qu'une seule fois avec Se
	 * @param entree
	 * @see #dijkstraForWeightedGraph(int) */
	public void dijkstraForUnWeightedGraph(int entree) {
		Set<Integer> marquee = new HashSet<Integer>(); // tableau contenant sommets marqué
		List<Integer> fifo = new ArrayList<Integer>(); // tableau contenant les voisins ajoutés avec (.get(élément) en O(1) mais
														// .contains(élément) en O(n)
		Set<Integer> dejaInFifo = new HashSet<Integer>(); // contient exactement la même chose que fifo sauf que pas de
															// .get(élément) mais .contains(élément) en O(1),
		double[] distance = new double[nodesNumber]; // label : tableau contenant les distances
		Integer[] precedentTab = new Integer[nodesNumber]; // Pour le sommet d'entrée Se, ce tableu contient pour chaque sommet de
															// sortie Ss, les sommets précédents jusqu'à Se
		int n1 = 0;
		double d;
		fifo.add(entree);
		dejaInFifo.add(entree);
		distance[entree] = 0;
		precedentTab[entree] = entree;

		for (int i = 0; i < nodesNumber; ++i) {
			n1 = fifo.get(i); // en O(1) en temps
			marquee.add(n1);
			for (int n2 : edgesMatrix.get(n1)) {
				d = distance[n1] + 1;
				if (!marquee.contains(n2)) {
					if (!dejaInFifo.contains(n2)) { // en O(1) en temps
						fifo.add(n2);
						dejaInFifo.add(n2);
						distance[n2] = d;
						precedentTab[n2] = n1;
					}
					else if (distance[n2] > d) {
						System.err.println(
								"PAM : Je crois que ce if n'est pas nécessaire pr des graphes non-étiquetés");
						distance[n2] = d;
						precedentTab[n2] = n1;
					}
				}
			}
		}
		precedentsMatrix.put(entree, precedentTab);
	}
	/** Construit le plus court chemin à partir de precedentsMatrix <br />
	 * Si precedentsMatrix n'est pas encore renseignée pour ce chemin, alors on appele dijkstra avant de construire le chemin
	 * @param entree : soilCell de départ
	 * @param sortie : soilCell d'arrivée
	 * @return longeur du chemin */
	public ArrayList<Integer> buildPath(int entree, int sortie) {
		if (pathTab.get(entree) != null && pathTab.get(entree).get(sortie) != null) {
			return pathTab.get(entree).get(sortie);
		}
		if (precedentsMatrix.get(entree) != null || precedentsMatrix.get(sortie) != null) {
			Integer[] precedentsTab = null;
			ArrayList<Integer> chemin = new ArrayList<Integer>();
			boolean versDroite = false;
			int entreeInit = entree;
			int sortieInit = sortie;
			if (precedentsMatrix.get(entree) != null) { // vers gauche
				precedentsTab = precedentsMatrix.get(entree);
				int entreeTmp = entree;
				entree = sortie;
				sortie = entreeTmp;
				versDroite = false;
			}
			else if (precedentsMatrix.get(sortie) != null) { // vers droite
				precedentsTab = precedentsMatrix.get(sortie);
				versDroite = true;
			}
			chemin.add(entree);
			// More secure to write while(entree != sortie && l <= nodesNumber) (mais moinsrapide)
			while (entree != sortie) {
				entree = precedentsTab[entree];
				if (versDroite) chemin.add(entree);
				else chemin.add(0, entree);
			}
			if (pathTab.get(entreeInit) == null) pathTab.put(entreeInit, new HashMap<Integer, ArrayList<Integer>>());
			pathTab.get(entreeInit).put(sortieInit, chemin);

			// Taking into account the duration of the journey (calculates and stores the overall weight of the path that has just
			// been built) PAM 2016.11
			double pathWeight = 0;
			for (Integer node : chemin) pathWeight += 1 / weightListByNodeNumber.get(node);
			if (pathWeightTab.get(entreeInit) == null) pathWeightTab.put(entreeInit, new HashMap<Integer, Double>());
			pathWeightTab.get(entreeInit).put(sortieInit, pathWeight);

			return chemin;
		}
		dijkstraForWeightedGraph(sortie);
		return buildPath(entree, sortie);
	}
	/** Selects the next node in the path to inform the targetPoint_Umeter
	 * @param nodeNumber */
	public C_SoilCellGraphed getNextNode(int nodeNumber) {
		return nodesList[nodeNumber];
	}
	/** for weighted graphs (PAM) */
	public void setNodesList(ArrayList<C_SoilCellGraphed> nodesList) {
		nodesList.toArray(this.nodesList);
		for (int i = 0; i < this.nodesNumber; i++) {
			for (String oneSubGraphType : GRAPH_TYPES_MAP.get(this.graphType))
				// for example track, road
				if (this.nodesList[i].isOfGroundType(oneSubGraphType)) {
					this.weightListByNodeNumber.put(i, TRACK_SLOW_FACTOR.get(oneSubGraphType));
					break;
				}
		}
	}
	/** For untypes and unweighted graphs (JLF 2022.01 for Dodel2) */
	public void setNodesListSimple(ArrayList<C_SoilCellGraphed> nodesList) {
		nodesList.toArray(this.nodesList);
		for (int i = 0; i < this.nodesNumber; i++) {
			this.weightListByNodeNumber.put(i, 1.0);// TODO number in source OK skip weight in graphs, JLF 2022.01
			break;
		}
	}
	/** @return the edgesMatrix */
	public Map<Integer, List<Integer>> getEdgesMatrix() {return edgesMatrix;
	}
	public String getGraphType() {
		return graphType;
	}
	public String getGroundType() {
		return groundType;
	}
	public C_SoilCellGraphed getNode(int i) {
		return nodesList[i];
	}
	public C_SoilCellGraphed[] getNodesList() {
		return nodesList;
	}
}
