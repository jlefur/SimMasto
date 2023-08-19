package data;
import data.constants.I_ConstantString;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import thing.A_Animal;

/** declare every parameters _for all protocols_ which may be modified within the GUI @see parameters.xml
 * @author Longueville, 2011, rev. jlefur 07.2012, rev.PAMBOUP 03.2014, JLF 07.2014 */
public class C_Parameters implements I_ConstantString {
	public static Parameters parameters;

	// GENERAL //
	public static String PROTOCOL;
	/** if true relative System.out.println() statements are displayed on the console */
	public static boolean VERBOSE;
	public static boolean TERMINATE = false;
	public static boolean BLACK_MAP = false;

	// SPACE //
	/** EXCLOS: if yes, rodents leave the domain when they reach bordure (& are replaced with a new one), if no, rebound and stay within the domain<br>
	 * @see thing.ground.landscape.C_Landscape#bordure(A_Animal) */
	public static boolean EXCLOS;
	/**IMAGE: if yes, display objects as icons, else display dots*/
	public static boolean IMAGE;

	public static String RASTER_URL;
	public static String[] RASTERLIST_URL;
	/** Raster background conversion factors: the width (in meter)of one pixel (was formerly size_of_one_box Chize: 747.8 cm.px^-1 */
	public static int CELL_WIDTH_Ucentimeter;
	public static double CELL_WIDTH_Umeter;
	/** conversion factor (e.g., Chize: 7.478 m.cs^-1, continuous space unit -> meters */
	public static double UCS_WIDTH_Umeter;
	public static final double CELL_SIZE_UcontinuousSpace = 1; // _Ucs <-> continuous space

	// TIME //
	public static int TICK_LENGTH_Ucalendar;
	public static String TICK_UNIT_Ucalendar;
	public static int TICK_MAX;// if 0 <-> infinity

	// Protocol Fossorial
	public static int INIT_RODENT_POP_SIZE;
	public static int MAX_POP;// used to stop the simulation when population 'pullulate'; if 0 <-> infinity

	// REPRODUCTION ATTRIBUTES //
	public static int REPRO_START_Umonth;
	public static int REPRO_END_Umonth;

	// C_ParametersFossorial
	/** When true empty burrow systems are not destroyed */
	public static boolean PERSISTANCE_BURROW = false;
	public static int INIT_BURROW_POP_SIZE;

	// C_ParametersDodel
	public static int UNLOAD_FREQUENCY_Uweek;
	// C_ParametersTransportation
	public static double VEHICLE_LOADING_PROBA_DIVIDER;
	public static int RODENT_SUPER_AGENT_SIZE;
	public static int HUMAN_SUPER_AGENT_SIZE;
	public static boolean DISPLAY_MAP;

	/** Instantiate the parameters object and retrieve the shared parameters. NB It is compulsory to make it followed with
	 * C_ContextCreator.protocol.readUserParameters() */
	public C_Parameters() {
		parameters = RunEnvironment.getInstance().getParameters();
		// Following lines are compulsory here for C_ContextCreator to build the calendar and the raster before defining the
		// protocol.
		PROTOCOL = (String) parameters.getValue("PROTOCOL");
		RASTER_URL = RASTER_PATH + parameters.getValue("RASTER_FILE");
		/** Raster background conversion factors: the width (in meter)of one pixel (was formerly size_of_one_box Chize: 747.8 cm.px^-1 */
		{
			CELL_WIDTH_Ucentimeter = ((Integer) parameters.getValue("CELL_WIDTH_Ucm")).intValue();
			CELL_WIDTH_Umeter = (double) CELL_WIDTH_Ucentimeter / 100.;
			/** conversion factor (e.g., Chize: 7.478 m.cs^-1, continuous space unit -> meters */
			UCS_WIDTH_Umeter = CELL_WIDTH_Umeter;
		}
		TICK_LENGTH_Ucalendar = ((Integer) parameters.getValue("TICK_LENGTH_Ucalendar")).intValue();
		TICK_UNIT_Ucalendar = (String) parameters.getValue("TICK_UNIT_Ucalendar");
		TERMINATE = ((Boolean) C_Parameters.parameters.getValue("TERMINATE")).booleanValue();
	}
}
