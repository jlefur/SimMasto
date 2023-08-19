package data.constants;

import java.util.HashMap;
import java.util.Map;

/** Gather all numbered variables since software specifications requires no numbers in the java sources
 * @author P.A.Mboup, 2013, rev. JLF 09.2014 */
public interface I_ConstantCentenal extends I_ConstantTransportation {

	// FILE NAMES //
	public static final String EVENT_CHRONO_NAME = "20161011_CentenalEvents.officiel.14c.csv";
//	public static final String EVENT_CHRONO_NAME = "20161016_CentenalEvents.officiel.14c_noVehicle.csv";
	public static final int nbShipWreckedRats_UperMonth = 1;
	//TODO JLF 2016.06 BIOCLIMATE_TO_AFFINITIES à revoir
	public static final Map<Integer, Integer> BIOCLIMATE_TO_AFFINITIES = new HashMap<Integer, Integer>() {
		{
			put(0, 0);
			put(1, 10);
			put(2, 11);
			put(3, 5);
			put(4, 3);
			put(5, 2);
			put(6, 4);
			put(7, 6);
			put(8, 12);
			put(9, 9);
		}
		private static final long serialVersionUID = 1L;
	};	
	/** @see C_InspectorTransportation#addOutputDataLineForRodent */
	public static final int OUTPUT_BUFFER_SIZE = 3;
}
