package data;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import thing.ground.C_LandPlot;
import thing.ground.landscape.C_Landscape;

/** @author Malick Diakhate, 2014, rev. JLF 10.2014 */
public class C_CircadianAffinitiesMus {

	private static Map<Integer, String> INITIAL_AFFINITIES_MUS = new HashMap<Integer, String>();
	private static Map<String, int[]> HOURLY_AFFINITIES = new HashMap<String, int[]>();
	private C_Landscape landscape;

	// CONSTRUCTOR
	/** hourly change of the danger in areas */
	public C_CircadianAffinitiesMus(C_Landscape landscape) {
		this.landscape = landscape;
		
		// used to set landPlots type given the affinity read at init
		INITIAL_AFFINITIES_MUS.put(1, "ROAD");
		INITIAL_AFFINITIES_MUS.put(2, "STREET");
		INITIAL_AFFINITIES_MUS.put(4, "TREE");
		INITIAL_AFFINITIES_MUS.put(6, "CONCESSION");
		INITIAL_AFFINITIES_MUS.put(8, "ROOM");
		INITIAL_AFFINITIES_MUS.put(9, "MARKET");

		// used to change affinity
		HOURLY_AFFINITIES.put("ROAD",	 new int[]{0,0,0,0,1,3,3,5,5,3,3,1});
		HOURLY_AFFINITIES.put("STREET",	 new int[]{0,0,0,0,1,3,4,5,5,4,3,1});
		HOURLY_AFFINITIES.put("TREE",	 new int[]{5,5,5,5,5,1,4,4,4,4,1,5});
		HOURLY_AFFINITIES.put("CONCESSION",	 new int[]{1,1,1,1,1,3,3,5,5,3,3,2});
		HOURLY_AFFINITIES.put("ROOM",	 new int[]{7,7,7,7,7,3,6,9,9,8,3,7});
		HOURLY_AFFINITIES.put("MARKET",	 new int[]{1,1,1,1,2,3,5,6,6,5,3,2});
	}
	/** used to change land plot affinities */
	public void setAffinity(int hour) {
		TreeSet<C_LandPlot> plots = this.landscape.getAffinityLandPlots();
		for (C_LandPlot onePlot : plots) {
			String plotType = onePlot.getPlotType();
			int[] affinities = HOURLY_AFFINITIES.get(plotType);
			onePlot.setAffinity(affinities[hour]);
		}
	}
	/** initializing the land plot types */
	public void setInitialAffinities() {
		TreeSet<C_LandPlot> plots = this.landscape.getAffinityLandPlots();
		for (C_LandPlot a_plot : plots) {
			String plotType = INITIAL_AFFINITIES_MUS.get(a_plot.getAffinity());
			a_plot.setPlotType(plotType);
		}
	}
}
