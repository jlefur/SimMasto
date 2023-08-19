/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package data.constants;

import java.util.Set;
import java.util.TreeSet;

/** - Constant names concerning types names shared within all protocols<br>
 * - Strings as well as unnecessary numbers are meant to disappear from the source code (see also I_numeric_constants and other
 * data/I_???_constants.java) <br>
 * - The correspondence is centralized here for string parameters.<br>
 * - Almost all packages implement this interface.
 * @see I_ConstantNumeric
 * @author J.Le Fur 08.2014, rev. JLF 11.2014, 02.2021 */
public interface I_ConstantString {

	// DESIRES
	public final String FEED = "FEED";
	public final String FLEE = "FLEE";
	public final String REPRODUCE = "REPRODUCE";
	public final String SUCKLE = "SUCKLE";
	public final String HIDE = "HIDE";
	public final String NONE = "NONE";
	public final String REST = "REST";
	public final String STANDBY = "STILL";
	public final String TRAVEL = "TRAVEL";
	public final String WANDER = "WANDER";

	// SPATIAL PROJECTIONS NAMES
	// options: 1) ascii (provide a grid.csv), 2) any other string, image (/ provide e.g. one *.gif,.jpg,.bmp).
	public final String RASTER_MODE = "ascii";
	public final String GEOGRAPHY_NAME = "geography";// used in SIMmasto0.rs/context.xml
	public final String CONTINUOUS_SPACE_NAME = "space";// used in SIMmasto0.rs/context.xml
	public final String VALUE_LAYER_NAME = "valuegrid";// used in SIMmasto0.rs/context.xml
	public final String proj_gridvalue2 = "valuegrid2";// used in networked/graphed landscapes

	// CONSOLE OUTPUT
	public final boolean isError = true;
	public final boolean isNotError = false;
	// FILES & URLs //
	public final String CONSOLE_OUTPUT_FILE = "retour_console.txt";
	public final String CSV_PATH = "data_csv/";
	public final String RASTER_PATH = "data_raster/";
	public final String OUTPUT_PATH = "data_output/";
	public final String REPAST_PATH = "SIMmasto_0.rs/";
	public final String EVENT_VALUE2_FIELD_SEPARATOR = ":";// used when event value2 is a list
	public final String HOUR_MINUTE_SEPARATOR = ":";// used when event value2 is a list
	public final String CSV_FIELD_SEPARATOR = ";";
	public final String NAMES_SEPARATOR = "_";// used when event value2 is a list

	// PROTOCOL NAMES - used in context creator
	public static final String BANDIA = "BANDIA";
	public static final String CAGES = "CAGES";
	public static final String CENTENAL = "CENTENAL";
	public static final String DECENAL = "DECENAL";
	public static final String CHIZE = "CHIZE";
	public static final String ENCLOSURE = "ENCLOSURE";
	public static final String HYBRID_UNIFORM = "HYBRID_UNIFORM";
	public static final String MUS_TRANSPORT = "MUS_TRANSPORT";
	public static final String DODEL = "DODEL";
	public static final String DODEL2 = "DODEL2";
	public static final String VILLAGE = "VILLAGE";
	public static final String GERBIL_PROTOCOL = "GERBIL";

	// EVENT TYPES CONSTANTS - used in chrono events
	public static final String GERBIL_EVENT = "Gerbillus_nigeriae";
	public static final String OWL_EVENT = "owl";
	public static final String RAT_EVENT = "Rattus rattus";
	public static final String MUS_EVENT = "Mus musculus";
	public static final String MASTO_ERY_EVENT = "Mastomys erythroleucus";
	public static final String CITY_EVENT = "city";
	public static final String MARKET_EVENT = "market";
	public static final String TOWN_EVENT = "town";
	public static final String RAIL_EVENT = "rail";
	public static final String RIVER_EVENT = "river";
	public static final String ROAD_EVENT = "road";
	public static final String GOOD_TRACK_EVENT = "track2";
	public static final String TRACK_EVENT = "track";
	public static final String GNT_WEAK_EVENT = "GNT-WEAK";// GNT for GroundNut Trade
	public static final String GNT_MEDIUM_EVENT = "GNT-MEDIUM";
	public static final String GNT_HEAVY_EVENT = "GNT-HEAVY";
	public static final String BORDER_EVENT = "border";
	public static final String SENEGAL_EVENT = "Senegal";// ! not implemented JLF 09.2014
	public static final String POPULATION_EVENT = "population";//
	public static final String TRUCK_EVENT = "truck";// a truck event generates (a) human carrier(s) with truck
	public static final String TAXI_EVENT = "taxi";// a taxi event generates (a) human carrier(s) with bush taxi
	public static final String TAXI_DODEL_EVENT = "taxiDodel";// a taxi event generates (a) human carrier(s) with bush taxi
	public static final String TRAIN_EVENT = "train";// a train event generates (a) human carrier(s) with train
	public static final String BOAT_EVENT = "boat";// a boat event generates (a) human carrier(s) with boat
	public static final String RAIN_EVENT = "rain";// triggers a rain event
	public static final String TICK_EVENT = "Tick";// triggers a tick event
	public static final String HUMAN_EVENT = "Human";// triggers a human event
	public static final String HUMAN_ACTIVITY_EVENT = "Human activity";// triggers a human activity event

	// LIFE TRAITS CONSTANTS - used in genomes
	public static final String LITTER_SIZE = "litterSize";
	public static final String WEANING_AGE_Uday = "weaningAge_Uday";
	public static final String MATING_LATENCY_Uday = "matingLatency_Uday";
	public static final String GESTATION_LENGTH_Uday = "gestationLength_Uday";
	public static final String GROWTH_RATE_UkcalPerDay = "growthRate_UgramPerDay";
	public static final String SEXUAL_MATURITY_Uday = "sexualMaturity_Uday";
	public static final String SPEED_UmeterByDay = "speed_UmeterByDay";
	public static final String SENSING_UmeterByDay = "sensing_UmeterByDay";
	public static final String MAX_AGE_Uday = "maxAge_Uday";

	// TRAP CONSTANTS - used in CMR
	public static final String ADD_TRAP = "addTrap";
	public static final String CHECK_TRAP = "checkTrap";
	public static final String REMOVE_TRAP = "removeTrap";

	// GRAPH CONSTANTS
	public static final Set<String> GRAPH_TYPES = new TreeSet<String>() {
		{
			// add(xxx);
		}
		private static final long serialVersionUID = 1L;
	};
	/** @see #GROUND_TYPE_CODES */
	public static final Set<String> AREA_TYPES = new TreeSet<String>() {
		{
			// add(xxx);
		}
		private static final long serialVersionUID = 1L;
	};
}
