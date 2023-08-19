package thing.ground;
import java.util.TreeSet;

import simmasto0.protocol.A_Protocol;
import thing.C_HumanCarrier;
import thing.I_SituatedThing;
import thing.ground.landscape.C_Landscape;
import data.constants.I_ConstantTransportation;

/** a city owns a population size, it also take part of several graphs?
 * @author jlefur 2014 */
public class C_City extends C_LandPlot implements I_ConstantTransportation {
	//
	// FIELDS
	//
	private int humanPopSize_Uindividual;
	//
	// CONSTRUCTOR
	//
	public C_City(C_Landscape groundManager) {
		super(groundManager);
		this.setHumanPopSize_Uindividual(DEFAULT_HAMLET_SIZE_Uindividual);
	}
	//
	// SETTERS & GETTERS
	//
	@Override
	/** Cities are unique, hence remove the id tag to name the city
	 * @see C_LandPlot#setThisName
	 * @args nullString arg kept for compatibility with NDS args / author J.Le Fur 10.2015 */
	public void setThisName(String nullString) {
		super.setThisName(nullString);
		this.setMyName(this.retrieveMyName().substring(0, this.retrieveMyName().indexOf(NAMES_SEPARATOR)));
	}
	/** Set the field and modify city plot and cells affinities accordingly<br>
	 * rev. JLF 10.2018 */
	public void setHumanPopSize_Uindividual(int popSize_Uindividual) {
		this.humanPopSize_Uindividual = popSize_Uindividual;
		// securely tag all contained cells as depending on the city patch JLF 10.2018 needed for getCarryingCapacity of cells
		for (C_SoilCell cell : this.cells)
			cell.affinityLandPlot = this;
	}
	/** get cells of the city that belong to a graph of the given type.
	 * @param graphType the requested graph (for a given vehicle) : road,rail,river
	 * @return a the set of cells of the graph type / author PAM, rev. JLF 09.2014 */
	public TreeSet<C_SoilCellGraphed> getGraphCells(String graphType) {
		TreeSet<C_SoilCellGraphed> graphCellsOfCity = new TreeSet<C_SoilCellGraphed>();
		C_SoilCellGraphed oneGraphedCell = null;
		for (C_SoilCell oneCell : getCells()) {// landPlot getCells returns C_SoilCell objects
			oneGraphedCell = (C_SoilCellGraphed) oneCell;
			if (oneCell.isOfGroundType(graphType)) graphCellsOfCity.add(oneGraphedCell);
		}
		if (graphCellsOfCity.size() == 0) {
			A_Protocol.event("A_ProtocolTransportation.getGraphCells", this.myId + " has no " + graphType, isError);
		}
		return graphCellsOfCity; // return si faux
	}
	/** Recursively return a treeset of all carriers including those in the contained containers/ Author Le Fur 08.2015
	 * @return carriers.size(): the number of carriers agent (not accounting for superAgent size) in the container
	 * @see A_Container#getFullRodentList() from where is has been copied */
	public int getFullLoad_Ucarrier() {
		TreeSet<C_HumanCarrier> carriers = new TreeSet<C_HumanCarrier>();
		for (I_SituatedThing agent : getFullOccupantList())
			if (agent instanceof C_HumanCarrier) {
				carriers.add((C_HumanCarrier) agent);
			}
		return carriers.size();
	}
	public int getHumanPopSize_Uthousand() {
		return (int) Math.floor(humanPopSize_Uindividual / 1000); // number in source ok JLF 09.2014
	}
	public int getHumanPopSize_Uindividual() {
		return this.humanPopSize_Uindividual;
	}
	@Override
	/** log base b of x = logk(x)/logk(b) Where logk is the function that returns the base-k logarithm of a number, and it can be any real number If
	 * humanPopSize>1E5 -> /1000, if > 1E4 -> /10, if > 1E3 return /1, else return humanPopSize<br>
	 * @version jlf 2015, rev. jlf 09.2017, 01.2018 */
	public int getCarryingCapacity_Urodent() {
		double baseOfLog = 1.05;// TODO number in source 2015 JLF getCarryingCapacity_Urodent
		return (int) (Math.log(this.humanPopSize_Uindividual) / Math.log(baseOfLog));
	}
}