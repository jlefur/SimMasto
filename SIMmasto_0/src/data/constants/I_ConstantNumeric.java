/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package data.constants;

import java.util.ArrayList;

/** Centralizes all shared numbered variables since software specifications requires no numbers in the java sources.<br>
 * protocols may overload it with their specific constants. Was formerly I_sim_constants
 * @author Q.Baduel, 2008, rev. Le Fur 07-09-11.2014, 10.2015
 * @see I_ConstantString */
public interface I_ConstantNumeric {

	// SIMULATION CONDITIONS //
	/** If pop size < threshold, FIS is not computed @see: C_InspectorGenetic#getFixationIndex */
	public final int FIS_COMPUTATION_THRESHOLD_Urodent = 30;// Below this population size, FIS is not computed (= 0.)

	// SPACE: GROUND AND PROJECTIONS //
	/** Size of one cell in the grid value layer (it must be checked that it is the same value that the scale given to the
	 * Continuous space within the definition of Raster display in the Repast Simphony GUI.. there is no known unit. */
	public static final ArrayList<Integer> cellSize = new ArrayList<Integer>() {
		{
			add(15);
		}
		private static final long serialVersionUID = 1L;
	};
	// RANDOM GENERATORS SEEDS //
	public static final int BOARDING_RANDOM_SEED = 1554563219; // 1554563209
	public static final int CULTURE_RANDOM_SEED = 254983425;
	public static final int DEATH_PROB_RANDOM_SEED = 1554563209;
	public static final int DESTINATION_RANDOM_SEED = 1122259370;// 1122259370
	public static final int DNA_RANDOM_SEED = 984863554;
	public static final int EPISTASIS_RANDOM_SEED = 654824589;
	public static final int GAMETE_RANDOM_SEED = 987654321;
	public static final int INITIALISATION_RANDOM_SEED = 1122259370;
	public static final int MOVEMENT_RANDOM_SEED = 564823654;
	public static final int OLFACTION_RECOGNITION_RANDOM_SEED = 564811123;
	public static final long GAUSSIAN_RANDOM_SEED = 321657567;
	public static final int DIRECTION_RANDOM_SEED = 1;

	// FILES & URLs //
	public static final int INTERVAL_ECRITURE_GENE_POP = 80;
	// csv file chronoEvents fields' order and values/ author pamboup
	public static final int DATE_COL = 0, X_COL = 1, Y_COL = 2, EVENT_COL = 3, VALUE1_COL = 4, VALUE2_COL = 5, VALUE3_COL = 6,
			CELL_ID_COL = 6, COMMENT_COL = 7;

	// DISPLAY & PRESENTATION
	public final int CIRCLE_ACCURACY_Upx = 32; // number of points used to draw circles
	public final int GUI_SPRITE_SIZE_Upx = 32;
	public final int BLACK_MAP_COLOR = 10;
	public float IMAGE_SCALE = .2f;

	// GENETICS //
	// life traits default values
	// amniota
	/** NB: .001 on any allele means junk value, else literature value<br>
	 * Warning: this invalidate the hybrid test @see C_GenomeEucaryote#isHybrid<br>
	 * JLF 03.2018, 02.2021 */
	public final double DEFAULT_SEXUAL_MATURITY_Uday = 10.001;// source: junk value must be redefined in daughter genomes
	public final double DEFAULT_LITTER_SIZE = 1.001;// source: junk value must be redefined in daughter genomes
	public final double DEFAULT_WEANING_AGE_Uday = 1.001;// source: junk value must be redefined in daughter genomes
	public final double DEFAULT_MATING_LATENCY_Uday = 1.001;// source: junk value must be redefined in daughter genomes
	public final double DEFAULT_GESTATION_LENGTH_Uday = 1.001;// source: junk value must be redefined in daughter genomes
	// animalia
	public final double DEFAULT_SPEED_UmeterByDay = 50.001; // source: junk value must be redefined in daughter genomes
	public final double DEFAULT_SENSING_UmeterByDay = 150.001;// DEFAULT_SPEED_UmeterByDay*3.; // TODO number in source JLF
																// 2018.03 Check if really accounted for
	public static final double INSTANTANEOUS_SENSING_Umeter = 5.001;// TODO number in source JLF 2018.03 used when tick is very
																	// small and leads to sensing sphere too small
	public final double DEFAULT_MAX_AGE_Uday = 600.001; // source: junk value must be redefined in daughter genomes
	// spermatophyta
	public final double DEFAULT_GROWTH_RATE_UgramPerDay = 2.001; // source: junk value must be redefined in daughter genomes

	// microsats //
	public final int NB_MICROSAT_GENES = 10;
	public double MEAN_GAUSS = 35;
	public double STD_GAUSS = 10;
	public double SPACE_BETWEEN_GENES = 4.3;
	// other genetics
	public static final double DEFAULT_MUT_RATE = 1E-9;// JLF; source talk Mathieu Gauthier, 04.11.2014
	public static final int SEX_GENE_Y = 0;
	public static final int SEX_GENE_X = 1;
	public static final int LOCUS_LETHAL_ALLELE = 0;
	public static final int STRAND_LETHAL_ALLELE = 0;

	// Living things (A_NDS) attributes
	public static final double DEFAULT_DEATH_PROBABILITY_UperDay = 1E-10;// quasi immortality: 1/(213 billion years)
	public static final int DEFAULT_AGE0_Utick = 0;
	public static final double EMPTY_BURROW_LIFESPAN_Uday = 10;
	// Animal attributes /
	public static final int INITIAL_ENERGY_Ukcal = 0;
	public static final int	REPEAT_DISPERSAL=4;
	public static int DANGEROUS_AREA_AFFINITY = 1; // Value 2 For Chize (highway, houses, plugged fields)
	public static final double DANGEROUS_AREA_MORTALITY_RATE = .8;// induce a mortality when in dangerous area (=.8 for ploughing
																	// expertise from B.Gauffre, 01.2012)
}
