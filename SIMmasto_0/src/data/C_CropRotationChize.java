package data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import simmasto0.C_ContextCreator;
import thing.ground.C_LandPlot;
import thing.ground.landscape.C_Landscape;

/** @see C_Style2dAffinityType#colorMapChizeGrid
 * @author Jean Le Fur, 2011 */
public class C_CropRotationChize {
	//
	// CONSTANTS
	//
	private static String[] PLOT_TYPES = {"HIGHWAY", "HOUSES_AND_ROADS", "HEDGE", "PERENNIAL_MEADOWS", "PERENNIAL_ALFALFA",
			"ANNUAL_WINTER", "ANNUAL_SPRING"};
	private int PLOUGHING = 2;// , MOWING = 3
	//
	// FIELDS
	//
	private static Map<Integer, String> INITIAL_AFFINITIES = new HashMap<Integer, String>();
	private static Map<String, double[]> CHIZE_CROP_TRANSITION = new HashMap<String, double[]>();
	private static Map<String, int[]> MONTHLY_AFFINITIES = new HashMap<String, int[]>();
	private C_Landscape landscape;
	//
	// CONSTRUCTOR
	//
	/** monthly change of the farming technical operations */
	public C_CropRotationChize(C_Landscape landscape) {
		this.landscape = landscape;

		// used to set landPlots type given the affinity read at init (march 2011) - JLF 02.2012
		INITIAL_AFFINITIES.put(0, "HIGHWAY");
		INITIAL_AFFINITIES.put(1, "HOUSES_AND_ROADS");
		INITIAL_AFFINITIES.put(4, "ANNUAL_WINTER");
		INITIAL_AFFINITIES.put(5, "ANNUAL_SPRING");
		INITIAL_AFFINITIES.put(7, "HEDGE");
		INITIAL_AFFINITIES.put(8, "PERENNIAL_MEADOWS");
		INITIAL_AFFINITIES.put(9, "PERENNIAL_ALFALFA");

		// used to change affinity given mowing, ploughing, etc.
		MONTHLY_AFFINITIES.put("HIGHWAY", new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		MONTHLY_AFFINITIES.put("HOUSES_AND_ROADS", new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1});
		MONTHLY_AFFINITIES.put("ANNUAL_WINTER", new int[]{8, 8, 9, 9, 4, 4, 3, 2, 3, 6, 6, 8});
		MONTHLY_AFFINITIES.put("ANNUAL_SPRING", new int[]{6, 2, 2, 2, 8, 9, 5, 3, 3, 3, 3, 6});
		MONTHLY_AFFINITIES.put("HEDGE", new int[]{7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7});
		MONTHLY_AFFINITIES.put("PERENNIAL_MEADOWS", new int[]{8, 8, 8, 3, 8, 8, 3, 8, 8, 8, 8, 8});
		MONTHLY_AFFINITIES.put("PERENNIAL_ALFALFA", new int[]{9, 9, 9, 8, 3, 8, 3, 9, 9, 9, 9, 9});

		GenerateCropTransitionMatrix();// used to determine changing ground type
	}
	//
	// METHODS
	//
	/** yearly change of the fields (viz. landPlots) culture type : Draw within the cumulated probability distribution: the more
	 * probable a transition the more its contribution and chances to be drawn <br>
	 * Author JLF 10.2011 */
	private void GenerateCropTransitionMatrix() {
		double[] HIGHWAY_TRANSITION = {1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00};
		double[] HOUSES_AND_ROADS_TRANSITION = {0.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00};
		double[] HEDGE_TRANSITION = {0.00, 0.00, 1.00, 1.00, 1.00, 1.00, 1.00};
		double[] PERENNIAL_MEADOWS_TRANSITION = {0.00, 0.00, 0.00, 0.80, 0.80, 0.90, 1.00};
		double[] PERENNIAL_ALFALFA_TRANSITION = {0.00, 0.00, 0.00, 0.04, 0.70, 0.85, 1.00};
		double[] ANNUAL_WINTER_TRANSITION = {0.00, 0.00, 0.00, 0.08, 0.25, 0.75, 1.00};
		double[] ANNUAL_SPRING_TRANSITION = {0.00, 0.00, 0.00, 0.08, 0.25, 0.50, 1.00};
		CHIZE_CROP_TRANSITION.put("HIGHWAY", HIGHWAY_TRANSITION);
		CHIZE_CROP_TRANSITION.put("HOUSES_AND_ROADS", HOUSES_AND_ROADS_TRANSITION);
		CHIZE_CROP_TRANSITION.put("HEDGE", HEDGE_TRANSITION);
		CHIZE_CROP_TRANSITION.put("PERENNIAL_MEADOWS", PERENNIAL_MEADOWS_TRANSITION);
		CHIZE_CROP_TRANSITION.put("PERENNIAL_ALFALFA", PERENNIAL_ALFALFA_TRANSITION);
		CHIZE_CROP_TRANSITION.put("ANNUAL_WINTER", ANNUAL_WINTER_TRANSITION);
		CHIZE_CROP_TRANSITION.put("ANNUAL_SPRING", ANNUAL_SPRING_TRANSITION);
	}
	public void culturalPractice(int month) {
		TreeSet<C_LandPlot> plotList = this.landscape.getAffinityLandPlots();
		C_LandPlot oneCrop;
		Iterator<C_LandPlot> it = plotList.iterator();
		while (it.hasNext()) {
			oneCrop = it.next();
			String currentCrop = oneCrop.getPlotType();
			int[] practices = MONTHLY_AFFINITIES.get(currentCrop);
			oneCrop.setAffinity(practices[month]);
			if (oneCrop.getAffinity() == PLOUGHING) oneCrop.ploughing();
		}
	}
	public void cropTransition(int month) {
		TreeSet<C_LandPlot> plots = this.landscape.getAffinityLandPlots();
		C_LandPlot tempoPlot;
		java.util.Iterator<C_LandPlot> it = plots.iterator();
		while (it.hasNext()) {
			tempoPlot = it.next();
			String currentCrop = tempoPlot.getPlotType();
			double random = C_ContextCreator.randomGeneratorForCulturalPractice.nextDouble();
			double[] transitionProbs = CHIZE_CROP_TRANSITION.get(currentCrop);
			for (int i = 0; i < transitionProbs.length; i++) {
				if (transitionProbs[i] > random) {
					String type = PLOT_TYPES[i];
					if (type != tempoPlot.getPlotType()) {
						tempoPlot.setPlotType(type);
						tempoPlot.setAffinity(MONTHLY_AFFINITIES.get(type)[month]);
					}
					break;
				}
			}
		}
	}
	/** initializing the land plot types - JLF 02.2012, 02.2013, 07.2014 */
	public void setInitialLandplotTypes() {
		TreeSet<C_LandPlot> plots = this.landscape.getAffinityLandPlots();
		for (C_LandPlot a_plot : plots) {
			String plotType = INITIAL_AFFINITIES.get(a_plot.getAffinity());
			a_plot.setPlotType(plotType);
		}
	}
}
