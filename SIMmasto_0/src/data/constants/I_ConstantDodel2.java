package data.constants;

import java.util.HashMap;
import java.util.Map;

/** Gathers all numbered variables since software specifications requires no numbers in the java sources
 * @author M. Sall 10.2018 */
public interface I_ConstantDodel2 extends I_ConstantString {
	// public static String CHRONO_FILENAME = "20201110_Dodel2Events.2d.ms.csv";
	// public static String CHRONO_FILENAME = "20201209_Dodel2Events.2d.ms.csv"; // Chrono whitout humans
	// public static String CHRONO_FILENAME = "20210518_Dodel2Events.2d.ms.jlf.mg.csv";
	// public static String CHRONO_FILENAME = "20211110_Dodel2Events.1f.csv"; // Chrono before JLF clean
	// public static String CHRONO_FILENAME = "20211120_Dodel2Events.2a.testWalker.csv"; // newest chrono clean
	 public static String CHRONO_FILENAME = "20211120_Dodel2Events.2a.csv";
//	public static String CHRONO_FILENAME = "20220520_Dodel2Events.Mice.csv";
//	 public static String CHRONO_FILENAME = "20210804_Dodel2_1HumanEvent.1a.ms.csv"; // For one human
	// public static String CHRONO_FILENAME = "20210831_Dodel2Events.1e.ms.csv"; // For one rodent
	public static String PATHS_FILENAME = "20220517-path100s.csv";

	public static final int STREET = 0, WORKSHOP = 1, BAKERY = 2, SHOP = 3, OFFICE = 4, HUT = 5, ROOM = 6,
			HAIRDRESSER = 7, KITCHEN = 8, DIBITERIE = 9, LIVESTOCK_PARK = 10, GARAGE = 11, GARDEN = 12, LABORATORY = 13,
			MAGASIN = 14, MOSQUE = 15, MILL = 16, HARDWARE_STORE = 17, RESTAURANT = 18, RUINED = 19, CLASS = 20,
			LIVING_ROOM = 21, TANGANA = 22, BUILDING = 23, WALL = 24, HOUSE = 25, MARKET = 26, NATIONAL_ROAD = 27,
			TRACK = 28, HOUSE_DOOR = 29, ROOM_DOOR = 30, THIALAGA = 31, DIERY_DIOUGA = 32, DIOMANDOU = 33,
			SCHOOL_DESTINATION = 34, MEDINA_DODEL_DESTINATION = 35, KOGGA_WALO_DESTINATION = 36,
			DODEL_INTERIOR_DESTINATION = 37, FIELD_DESTINATION = 38;

	public static final int GATE = 8; // Junk number will we changed after test!
	public static final double MIN_VALUE_OF_REBOUNDZONE_Umeter = .2;
	// public static final double MIN_SENSING_VALUE_Umeter = 15;// Ref : see Jean doc file in mice.
	public static final double PERCEPT_RIGHT_DISTANCE_VALUE = 0.9;// Junk value.
	// Value of category converter
	public static final int ROAD_AFFINITY = 0, TRACK_AFFINITY = 1, STREET_AFFINITY = 2, WALL_AFFINITY = 3,
			CONCESSION_AFFINITY = 4, CORRIDOR_AFFINITY = 5, MARKET_AFFINITY = 6, ROOM_AFFINITY = 7,
			WORKSHOP_AFFINITY = 8, ROOMFOOD_AFFINITY = 9, SHOPFOOD_AFFINITY = 10, HOUSEDOOR_AFFINITY = 11,
			ROOMDOOR_AFFINITY = 12, ENCLOSURE_AFFINITY = 13, THIALAGA_AFFINITY = 14, DIOMANDOU_AFFINITY = 15,
			DIERY_DIOUGA_AFFINITY = 16, KOGGA_WALO_AFFINITY = 17, SCHOOL_AFFINITY = 18, DODEL_INTERIOR_AFFINITY = 19,
			MEDINA_DODEL_AFFINITY = 20, FIELD_AFFINITY = 21, MOSQUE_AFFINITY = 22, BAKERY_AFFINITY = 23;

	// Different wall position to agent
	public static final int FORWARD_TO_AGENT = 1, RIGHT_TO_AGENT = 2, LEFT_TO_AGENT = 3, NO_WALL = 0;
	// Tick constant values
	public static final double MATING_LATENCY_DURATION_Uday = 15.001; // Source : junk!
	// Source : Gondard, M. (2017). A la découverte des agents pathogènes et microorganismes des tiques par séquençage de nouvelle
	// génération et QPCR microfluidique à haut débit (Doctoral dissertation, Paris Est).
	public static final double GESTATION_DURATION_Uday = 5.;
	public static final int MEAL_DURATION_Umn = 30;
	public static final int oneDay_Umn = 1440;
	public static final int oneYear_Uday = 365;
	public static final String ADULT = "adult", NYMPH = "nymph", LARVAE = "larvae", EGG = "egg";
	public static final double STARVING_DURATION_Uday = 5.001;// Source : junk value time waiting without meal
	public static final double EGG_STASIS_DURATION_Uday = 10.5;// Embryogenesis on the ground (8-13 days)
	public static final double LARVAE_STASIS_DURATION_Uday = 15.5; // Egg Statis duration + Larval moulting 5 days
	public static final double NYMPH_STASIS_DURATION_Uday = 26.5; // Larval stasis duration + nymphal moulting
	// Cat constant values

	public static final double HUNT_MIN_AGE_Uday = 70.; // between 8 and 12 weeks source:
														// (https://en.wikipedia.org/wiki/Cat#Reproduction)

	public static final double JUVENILE_MIN_AGE_Uday = 210.; // between 6 and 8 months source:
																// (https://en.wikipedia.org/wiki/Cat#Reproduction)
	public static final double PREDATION_SUCCESS = 5.;

	public static final Map<String, String[]> RASTER_PARAMETERS = new HashMap<String, String[]>() {
		{
			// Key Value: data_raster/name of house raster, name of room raster, origin longitude,origin latitude
			// 20201027-ZoomHouseRasteDodel2.1a.txt,20201027-ZoomRoomRasteDodel2.1a.txt
			put(RASTER_PATH + "market", new String[]{
					"20201027-ZoomHouseRasteDodel2.1a.txt,20201027-ZoomRoomRasteDodel2.1a.txt",
					"-14.433808462406692,16.48482308819331"});
			put(RASTER_PATH + "aroundmarket", new String[]{
					"20220515-ZoomHouseSpecialRasteDodel2.2a.txt,20220515-ZoomRoomSpecialRasteDodel2.2a.txt",
					// "20210527-ZoomHouseSpecialRasteDodel2.1a.txt,20210527-ZoomRoomSpecialRasteDodel2.1a.txt",
					"-14.434423819127202,16.48454321296872"});
		}
		private static final long serialVersionUID = 1L;
	};

	public static final Map<String, String[]> HUMANACTIVITY_FILES = new HashMap<String, String[]>() {
		{
			// Key Value: data_raster/name of zoom, name of zoom activity chrono,
			// put(RASTER_PATH + "market", new String[]{"20210825-ChronoActivityMarket.temp.1a.csv"});
			put(RASTER_PATH + "market", new String[]{"20211120-ChronoActivityMarket.2a.csv"});
			put(RASTER_PATH + "aroundmarket", new String[]{
					// "20210825-ChronoActivityAroundMarket.temp.1a.csv"});
					// "20220119-ChronoActivityAroundMarket.2b.csv"});
					// "20211120-ChronoActivityAroundMarket.2a.csv"});
					"20220119-ChronoActivityAroundMarket.3a.csv"});
			// "20220501-ChronoActivityJunk.csv"});
			// put(RASTER_PATH + "market", new String[]{
			// "20210804-1PersonActivityChrono.csv"});
			// put(RASTER_PATH + "aroundmarket", new String[]{
			// "20210804-1PersonActivityChrono.csv"});
		}
		private static final long serialVersionUID = 1L;
	};
	public static final double SLOW_FACTOR = 6.;	// Slow motion when human urban standby, JLF 01,05.2022
	public static final int ONE_DOOR_EACH_N_STEP = 15;// Add one door to walls each n cells? JLF 05.2022
}
