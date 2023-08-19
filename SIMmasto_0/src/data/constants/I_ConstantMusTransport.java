package data.constants;

import java.util.ArrayList;

/** Gathers all numbered variables since software specifications requires no numbers in the java sources
 * @author Le Fur 10.2014 */
public interface I_ConstantMusTransport extends I_ConstantTransportation {
	//
	// FILE NAMES //
	//
	// public static String CHRONO_FILENAME ="20140723-SenegalMaille2km.2b.REFERENCE.jlf.csv";
	// public static String CHRONO_FILENAME = "20171212-MusTransportEventsOfficiel.9d.jlf.csv";
	
	public static String CHRONO_FILENAME = "20220414-MusTransportJunkSourisSeulement.csv";
//	public static String CHRONO_FILENAME = "20180322-MusTransportEventsOfficielDalecky.Ba.jlf.csv";
	
//	public static String CHRONO_FILENAME = "20180323-MusTransportSansRongeurs.1a.jlf.csv";
//	public static String CHRONO_FILENAME = "20180323-MusTransportSansRongeursRoadMatamJunk.1b.jlf.csv";

	public static int marketCrowdingFactor = 10;
	public static final ArrayList<Double> rasterLongitudeWest_LatitudeSouth_Udegree = new ArrayList<Double>() {
		// stands for 12°18'28.3"N 17°31'48.5"W
		{
			add(-17.530148);
			add(12.307859);
		}
		private static final long serialVersionUID = 1L;
	};
}
