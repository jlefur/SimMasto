/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing.ground;
import java.util.TreeSet;

import thing.C_HumanCarrier;
import thing.ground.landscape.C_Landscape;
import data.constants.I_ConstantTransportation;

/** A region stands for a landplot (sufficiently large area) that can encompass(contain) at least one C_City
 * @author Jean Le Fur 08.09.2014 */
public class C_Region extends C_LandPlot implements I_ConstantTransportation {
	//
	// FIELD
	//
	protected TreeSet<C_City> cityList;
	//
	// CONSTRUCTOR
	//
	public C_Region(C_Landscape groundManager) {
		super(groundManager);
		cityList = new TreeSet<C_City>();
	}
	//
	// METHOD
	//
	/** Use the groundPlotsByGroundType to fetch the cities of each soil cell of the region Version JLF 09.2014 */
	public void computeCityList() {
		C_SoilCellGraphed graphedCell;
		this.cityList.clear();
		for (C_SoilCell soilCell : this.cells) {
			graphedCell = (C_SoilCellGraphed) soilCell;
			if (graphedCell.getLandPlot(CITY_EVENT) != null) {
				this.cityList.add((C_City) graphedCell.getLandPlot(CITY_EVENT));
			}
		}
	}
	//
	// GETTERS
	//
	/** Fetch the soil cells of the Region and retrieve the cities (treeSet avoids duplicates) */
	public TreeSet<C_City> getCityList() {
		return this.cityList;
	}
	/** Extract the list of markets from the list of cities / rev. JLF 03.2016 */
	public TreeSet<C_Market> getMarkets() {
		TreeSet<C_Market> markets = new TreeSet<C_Market>();
		for (C_City oneCity : this.cityList) {
			if (oneCity instanceof C_Market) markets.add((C_Market) oneCity);
		}
		return markets;
	}
	/** Extract the list of cities which are not markets from the list of cities / JLF 01.2017 */
	public TreeSet<C_City> getNoMarkets() {
		TreeSet<C_City> noMarkets = new TreeSet<C_City>();
		for (C_City oneCity : this.cityList) {
			if (!(oneCity instanceof C_Market)) noMarkets.add(oneCity);
		}
		return noMarkets;
	}
	public TreeSet<C_HumanCarrier> getCarriers() {
		TreeSet<C_HumanCarrier> carrierList = new TreeSet<C_HumanCarrier>();
		for (C_SoilCell cell : this.cells)
			carrierList.addAll(((C_SoilCellGraphed) cell).getCarrierList());
		return carrierList;
	}
}