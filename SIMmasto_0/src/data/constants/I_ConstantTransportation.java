package data.constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import simmasto0.protocol.A_ProtocolTransportation;

/** Gather all variables since software specifications require no numbers in the java sources
 * @author P.A.Mboup, 2013, rev. JLF 09.2014 */
public interface I_ConstantTransportation extends I_ConstantString {

	/** Vehicle specifications by type; we have [speed, speedUnit, rodentMaxLoad, loadingProba, graphType, account for track
	 * condition] Speed in km/h unit is converted to the simulation space unit/tick */
	public static final Map<String, String[]> VEHICLE_SPECS = new HashMap<String, String[]>() {
		{
			put("truck", new String[]{"50", "km/h", "5", "100", "road", "true"});
			put("taxi", new String[]{"90", "km/h", "5", "100", "road", "true"});// bush transport; used first for mus transport
																				// protocol JLF 12.2015
			put("train", new String[]{"20", "km/h", "5", "100", "rail", "false"});
			put("boat", new String[]{"10", "km/h", "5", "100", "river", "false"});
		}
		private static final long serialVersionUID = 1L;
	};
	// Column numbers for VEHICLE_SPECS
	public static final int REAL_SPEED_COL = 0;
	public static final int REAL_SPEED_UNIT_COL = 1;
	public static final int RODENT_MAX_LOAD_PERCENT_COL = 2;
	public static final int LOADING_PROBA_COL = 3;
	public static final int GRAPH_TYPE_COL = 4;
	public static final int ACCOUNT_FOR_TRACK_CONDITION = 5;
	/** @see C_InspectorTransportation#addOutputDataLineForRodent */
	public static final int OUTPUT_BUFFER_SIZE = 100;
	public static final Set<String> GRAPH_TYPES = new TreeSet<String>() {
		{
			add(ROAD_EVENT);
			add(RAIL_EVENT);
			add(RIVER_EVENT);
		}
		private static final long serialVersionUID = 1L;
	};
	/** @see #GROUND_TYPE_CODES */
	public static final Set<String> AREA_TYPES = new TreeSet<String>() {
		{
			add(GNT_WEAK_EVENT);
			add(GNT_MEDIUM_EVENT);
			add(GNT_HEAVY_EVENT);
		}
		private static final long serialVersionUID = 1L;
	};
	/** Used to convert groundType events from the csv file from String to int. <br />
	 * &nbsp;&nbsp;&nbsp Used to build valueLayer2 (the ground types' display) */
	public static Map<String, Integer> GROUND_TYPE_CODES = new HashMap<String, Integer>() {
		{
			put(CITY_EVENT, 5);
			put(RIVER_EVENT, 0);
			put(RAIL_EVENT, 2);
			put(ROAD_EVENT, 1);
			put(GOOD_TRACK_EVENT, 3);
			put(TRACK_EVENT, 4);
			put(TOWN_EVENT, 6);
			put(GNT_HEAVY_EVENT, 7);
			put(GNT_MEDIUM_EVENT, 8);
			put(GNT_WEAK_EVENT, 9);
			put(SENEGAL_EVENT, 10);
			put(BORDER_EVENT, 11);
		}
		private static final long serialVersionUID = 1L;
	};
	/** Used to convert affinity events from the csv file from string to int. <br />
	 * &nbsp;&nbsp;&nbsp Used to build valueLayer (the affinity's display) and the soilCellMatrix */
	/*
	 * //comment 10.2016 JLF public static final Map<String, Integer> EVENT_AFFINITY_CODES = new HashMap<String, Integer>() { {
	 * put(BORDER, -1); put(RIVER, 0); put(ROAD, 1); put(TOWN, 10); put(CITY, 13); // put(MARKET, 13); PAM 2015.12 n'est
	 * plus utile MARKET est considéré comme un city 08/12/15 } private static final long serialVersionUID = 1L; };
	 */
	public static int DEFAULT_HAMLET_SIZE_Uindividual = 500;// Population sizes of hamlets
	/** accounted for in calculating effect of bioclimate
	 * @see A_RodentCommensalSimplified#updatePhysiologicStatus */
	public static int CITY_AFFINITY = 4;// Population sizes of hamlets
	// Correspondence in Senegal:0, 1: out of Senegal
	public static final int GROUNDTYPE_INSIDE_DOMAIN = GROUND_TYPE_CODES.get(SENEGAL_EVENT);
	public static final int GROUNDTYPE_OUTSIDE_DOMAIN = GROUND_TYPE_CODES.get(BORDER_EVENT);
	public static final int AFFINITY_OUTSIDE_DOMAIN = 0;

	/** Retrieve the city code (used for valueLayer2) since cities are contiguous cells in decenal map
	 * @see I_ConstantDecenal#GROUND_TYPE_CODES */
	public static Integer CITY_EVENT_CODE = 0;
	/** Used to compare cities population sizes and remove surplus : rodent agents simulated are 1/1E3 of the supposed real number
	 * of rodents CAUTION: account for this coefficient for loading probabilities
	 * @see A_ProtocolTransportation#step_Utick / author jlefur 04.2015 */
	public static Integer CITY_RODENT_SCALING = 1000;

	// Used for weighted/unweighted graphs, PAM, 10.2015
	public static final Map<String, String[]> GRAPH_TYPES_MAP = new HashMap<String, String[]>() {
		{
			put(ROAD_EVENT, new String[]{TRACK_EVENT, GOOD_TRACK_EVENT, ROAD_EVENT});// une cellule contenant track et road est réellement track
			put(RAIL_EVENT, new String[]{RAIL_EVENT});
			put(RIVER_EVENT, new String[]{RIVER_EVENT});
		}
		private static final long serialVersionUID = 1L;
	};
	// Used for weighted/unweighted graphs, PAM, 10.2015
	public static final Map<String, Double> TRACK_SLOW_FACTOR = new HashMap<String, Double>() {
		{
			put(ROAD_EVENT, 1.0);
			put(GOOD_TRACK_EVENT, 2.0);
			put(TRACK_EVENT, 3.0);
			put(RAIL_EVENT, 1.0);
			put(RIVER_EVENT, 1.0);
		}
		private static final long serialVersionUID = 1L;
	};
}
