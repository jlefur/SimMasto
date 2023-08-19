package thing;

import java.util.TreeSet;
import thing.dna.I_DiploidGenome;
import thing.ground.C_City;
import thing.ground.C_Market;

public class C_TaxiMan extends C_HumanCarrier {
	public C_TaxiMan(I_DiploidGenome genome) {
		super(genome);
	}
	/** Retrieve this.cityList and select markets with market day <br>
	 * Convert into I_SituatedThing for compatibility with upper procedure
	 * @see A_VisibleAgent#retrieveCell2Perception
	 * Version JLF&PAM 03.2016 */
	@Override
	protected TreeSet<I_SituatedThing> perception() {
		TreeSet<I_SituatedThing> convertedList = new TreeSet<I_SituatedThing>();
		for (C_City oneCity : this.cityList)
			if (oneCity instanceof C_Market) 
				if (((C_Market) oneCity).isMarketDay()) convertedList.add(oneCity);
		return convertedList;
	}
}
