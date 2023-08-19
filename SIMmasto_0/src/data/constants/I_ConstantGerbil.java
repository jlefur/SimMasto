package data.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** Gathers all variables since software specifications requires no numbers in the java sources <br>
 * PE: Petite Emprise, ME, Moyenne Emprise, TPE: Très Petite Emprise
 * @author Le Fur & Sall 09.2015, rev. JLF 02.2021 */
public interface I_ConstantGerbil extends I_ConstantString {

	// CHRONOGRAM FILE NAME
	// public static final String CHRONO_FILENAME = "20151029_GerbilEventsOfficiel.2a.jlf.csv";
	// public static final String CHRONO_FILENAME = "20160719_GerbilEventsOfficiel.2b.jlf.csv";
	// public static String CHRONO_FILENAME = "20151029_GerbilEventsOfficielJunk.2a.jlf.csv";
	public static String CHRONO_FILENAME = "20170517_GerbilEventsJunk.3a.jlf.csv";
	// public static String CHRONO_FILENAME = "20180516_GerbilEventsJunk.4a.ms.csv";
	// public static String CHRONO_FILENAME = "20180320_GerbilEventsCerise2015.1b.jlf.csv";
	// public static String CHRONO_FILENAME = "20180321_GerbilEventsCerise2015EtPelotes.2c.jlf.csv";
	// public static String CHRONO_FILENAME = "20180412_GerbilEventsCerise2015.1c.ms.csv";

	public static int CELL_SIZE = 245; // DEFAULT : 15 //Junk Value add to resize the display in gerbil case
	// INITIALISE SIMULATION also modify Raster_File once parameters_scenario_GERBIL
	public static final ArrayList<Integer> width_heightRaster_Ukilometer = new ArrayList<Integer>() {
		{
			add(0);
			add(0);
		}
		private static final long serialVersionUID = 1L;
	};
	public static final ArrayList<Double> rasterLongitudeWest_LatitudeSouth_Udegree = new ArrayList<Double>() {
		{
			add(0.0);
			add(0.0);
		}
		private static final long serialVersionUID = 1L;
	};
	public static final ArrayList<String> rainUrl_suffixRainFile = new ArrayList<String>() {
		{
			add("");
			add("");
		}
		private static final long serialVersionUID = 1L;
	};
	public static int distancePELatitudeToOriginME_Ukilometer = 217;
	public static int distancePELongitudeToOriginME_Ukilometer = 247;
	public static final double rasterLatitudeNorth_Udegree = 16.75;
	public static final double rasterLongitudeEast_Udegree = -14.;
	// ME
	public static final int widthME_Ukilometer = 976;
	public static final int heightME_Ukilometer = 606;
	public static final double gerbilMELatitudeNorth_Udegree = 18.97833333;
	public static final double gerbilMELatitudeSouth_Udegree = 13.511944;
	public static final double gerbilMELongitudeWest_Udegree = -18.;
	public static final double gerbilMELongitudeEast_Udegree = -9.;
	/** Fixed Values of Gerbillus Nigeriae */
	// Vegetation names
	public static final String str_CROP = "crops";
	public static final String str_GRASS = "grasses";
	public static final String str_SHRUB = "shrubs";
	public static final String str_WATER = "water";
	public static final String str_BARREN = "barren";
	public static final String str_TREE = "tree";
	/** Each landcover data integer value correspond to a couple of vegetation types here defined */
	public static final Map<Integer, String[]> LANDCOVER_TO_VEGETATION = new HashMap<Integer, String[]>() {
		{
			put(18, new String[]{str_TREE, str_SHRUB});
			put(36, new String[]{str_TREE, str_CROP});
			put(37, new String[]{str_SHRUB, str_SHRUB});
			put(38, new String[]{str_GRASS, str_SHRUB});
			put(39, new String[]{str_CROP, str_SHRUB});
			put(40, new String[]{str_BARREN, str_SHRUB});
			put(41, new String[]{str_GRASS, str_GRASS});
			put(42, new String[]{str_GRASS, str_CROP});
			put(43, new String[]{str_GRASS, str_BARREN});
			put(44, new String[]{str_CROP, str_CROP});
			put(45, new String[]{str_BARREN, str_BARREN});
		}
		private static final long serialVersionUID = 1L;
	};
	/** Constant of distance between two vegetation */
	public static final double DISTANCE_THRESHOLD = 0.1; // Junk value
	public static final int nbVegetationInCell = 8;// Simulation choice
	public static final double initialCropBiomass_UgramPerSquareMeter = 80. / nbVegetationInCell;// Ref : (Ngom et al, 2012)
																									// Qualite pastorale des
																									// ressources herbageres de
	public static final double initialGrassBiomass_UgramPerSquareMeter = 99. / nbVegetationInCell;// Ref : (Ngom et al, 2012)
	public static final double VegetationCarryingCapacity_UgramPerSquareMeter = 73.183 / nbVegetationInCell;// Ref : (Ngom et al,
																											// 2012)

	public static final double initialVegetationBiomass_Ugram = 1E4;// Junk Values
	// Daily consumption of Gerbillus Nigeriae
	// souris 4-6g/j. Dr Vet Laurence Yaguiyan-Colliard, UMES-Nutrition Clinique, Ecole Nationale Vétérinaire d’Alfort
	// Dans les aliments, l'énergie peut venir de 4 sources : glucides (4 cal/g.); lipides (9 cal/g.); protéines (4 cal/g.); alcool (7 cal/g.)
	public static final double DAILY_CONSUMPTION_NEED_UkcalPerDay = 25.001;
	//public static final int AVERAGE_DISTANCE_HUNTING_OWL_Umeter = 2500;// Source : P.TABERLET, 1983
	public static final int BACKGROUND_COLOR = 38;
	double RAIN_VALUE_MULTIPLIER = 1.E8;// Convert rain class value (1 to 8) to carrying capacity
	int repeatEating = 50;// JLF 02.2021 repeatEating gerbils
	double SENSITIVITY_TO_RAIN = 5.;// Sensitivity to rain
	// Unique carrying capacity for any savanna cell (as the different zooms use the same size for one cell)
	public static final double CARRYING_CAPACITY_SAVANNA_UrodentPerSquareMeter = 0.03;// 0.03 ref. LG comm perso, it's possible to
																						// have 300 gerbils in 1 Ha
	public static final int UCS_WIDTH_Umeter = 1000;
	public static final int INIT_BURROW_POP_SIZE = 100;
	public static final int INIT_RODENT_POP_SIZE = 50;
	public static final Map<String, String[]> RASTER_PARAMETERS = new HashMap<String, String[]>() {
		{
			// Key Value: data_raster/name of raster, String list : landcover path, rain path, rain suffix, width raster, height
			// raster, origin
			// longitude,origin latitude
			put(RASTER_PATH + "zoom1", new String[]{RASTER_PATH + "Zoom_001_12.2015/landcover.txt", "Zoom_001_12.2015/",
					"-Zoom-Rain.txt", "30", "30", "-16.", "16.2"});
			put(RASTER_PATH + "zoom2", new String[]{RASTER_PATH + "Zoom_002_12.2015/landcover.txt", "Zoom_002_12.2015/",
					"-Zoom-Rain.txt", "75", "45", "-15.45", "15.76"});
			put(RASTER_PATH + "zoom3", new String[]{RASTER_PATH + "Zoom_003_09.2016/landcover.txt", "Zoom_003_09.2016/",
					"-Zoom-Rain.txt", "3", "3", "-16.", "15.758572"});
			put(RASTER_PATH + "zoom4", new String[]{RASTER_PATH + "Zoom_004_01.2018/landcover.txt", "Zoom_004_01.2018/",
					"-Zoom-Rain.txt", "5", "5", "-15.713078", " 16.421078"});
			put(RASTER_PATH + "pe", new String[]{RASTER_PATH + "Zoom_PE_12.2015/landcover.txt", "Zoom_PE_12.2015/",
					"-Zoom-Rain.txt", "214", "110", "-16.", "15.758572"});
			put(RASTER_PATH + "me", new String[]{RASTER_PATH + "Zoom_ME_03.2018/landcover.txt", "Zoom_ME_03.2018/",
					"-Zoom-Rain.txt", "700", "400", "-17.899695", " 13.616352"});
			put(RASTER_PATH + "fd", new String[]{RASTER_PATH + "Zoom_FD_2019.03/landcover.txt", "Zoom_FD_2019.03/",
					"-Zoom-Rain.txt", "700", "400", "-17.899695", " 13.616352"});
		}
		private static final long serialVersionUID = 1L;
	};
	public static final double vegetationCoverageRadius_Umeter = 25.001; // MS 2016.10 Junk value

	// UNUSED
	//
	// source : http://theses.vet-alfort.fr/telecharger.php?id=1335 source
	// http://lvts.fr/download/other_universities/formation_expérimentation_animale_lvl_1_paris_6_university/La physiologie
	// digestive etl'alimentation des Rongeurs.pdf
	public static final double initialGerbilVitalEnergy_Ukcal = 38.3;
	// source : barn owl eats between 70 and 105 gram per day(Mikkola, H. (1983).- Owls of Europe. T et A.D. Poyrer, Calton. 397
	// p.), and gerbil weights between 25 and 26 gram (Thomas and Hinton, 1920) on average 3.5
	public static final double eatRodentGainedEnergy_UKcal = 37.5;
	public static final double eatGainedEnergy_UKcal = 8.6;
	// Ajouts JLF 02.2021
	public static final double INITIAL_VEGET_ENERGY = 10;
	public static final double	PREDATION_SUCCESS = 5.;
}
