/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing.ground;

import java.util.Iterator;
import java.util.TreeSet;

import com.vividsolutions.jts.geom.Coordinate;

import data.constants.I_ConstantString;
import simmasto0.protocol.A_Protocol;
import thing.A_Animal;
import thing.I_SituatedThing;
import thing.ground.landscape.C_Landscape;

/** Landplots are sets of identical joint C_SoilCells
 * @see C_SoilCell
 * @author jlefur 2012, rev 10.2015 */
public class C_LandPlot extends A_Container implements I_ConstantString {
	//
	// FIELDS
	//
	/** One landplot is only composed of cells, that is, its agents are all cells. The occupantList field is a copy of the field
	 * cells. the Cells treeSet permit to ensure the C_SoilCell nature of cells but keeping the I_container nature and heritage
	 * @see A_Container#occupantList */
	protected TreeSet<C_SoilCell> cells = new TreeSet<C_SoilCell>();
	/** e.g., road, house, crop, trap, ... */
	protected String plotType;
	//
	// CONSTRUCTOR
	//
	public C_LandPlot(C_Landscape groundManager) {
		// TODO JLF 2022.01 not necessary ? since already within A_VisibleAgent.init() / see A_Protocol constructor
		// C_LandPlot.myLandscape = groundManager;/ }
	}

	//
	// METHODS
	//
	public boolean agentIncoming(I_SituatedThing a_thing) {
		if (a_thing instanceof C_SoilCell) return occupantList.add(a_thing);
		else {
			A_Protocol.event("C_LandPlot.agentIncoming", a_thing + " is not a C_SoilCell", isError);
			return false;
		}
	}
	/** Induce a mortality when a landPlot is ploughed (value = .8 expertise from B.Gauffre, 01.2012) */
	public void ploughing() {
		for (C_SoilCell oneSoilCell : this.cells) {
			for (I_SituatedThing inhabitant : oneSoilCell.getFullRodentList()) {
				if (inhabitant instanceof A_Animal) ((A_Animal) inhabitant).checkDeath(DANGEROUS_AREA_MORTALITY_RATE);
			}
		}
	}
	//
	// SETTERS & GETTERS
	//
	@Override
	/** Use the first part of the name of the first cell to set the landPlot name. Useful to identify cities
	 * @see C_InspectorTransportation#getCityByName(String)
	 * @args nullString arg kept for compatibility with NDS args / author J.Le Fur 09.2014 */
	public void setThisName(String nullString) {
		String cellName = "";
		if (cells.size() != 0) cellName = this.cells.first().retrieveMyName();
		else A_Protocol.event("C_Region.setThisName", "No cells for " + this, isError);
		if (cellName.indexOf(NAMES_SEPARATOR) == -1) {
			System.err.println("C_LandPlot.setThisName, cannot find plot name in cell" + cellName);
			this.setMyName(this.plotType + NAMES_SEPARATOR + this.myId);
		}
		else // unique landplots GNT weak. Replace cell id with landplot id
			this.setMyName(cellName.substring(0, cellName.indexOf(NAMES_SEPARATOR)) + NAMES_SEPARATOR + this.myId);
	}
	public void addCell(C_SoilCell cell) {
		this.cells.add(cell);
		this.occupantList.add(cell);// used to benefit from the I_container heritage. jlf 03.2015
	}
	public C_SoilCell retrieveOneCell(int i) {
		C_SoilCell cell = this.cells.first();
		Iterator<C_SoilCell> iterator = this.cells.iterator();
		for (int j = 0; j <= i; j++) cell = iterator.next();
		return cell;
	}
	public void setPlotType(String plotType) {
		this.plotType = plotType;
		this.setMyName(plotType+NAMES_SEPARATOR+this.retrieveId());
	}
	public TreeSet<C_SoilCell> getCells() {
		return this.cells;
	}
	public String getPlotType() {
		return this.plotType;
	}
	@Override
	/** if rodent load of the land plot is > carrying capacity then land plot is full */
	public boolean isFull() {
		int nrodents = 0;
		for (C_SoilCell oneCell : this.cells) nrodents += oneCell.getFullLoad_Urodent();
		return nrodents > getCarryingCapacity_Urodent();
	}
	// @Override
	// /** if any cell of the land plot is not full then land plot is not full*/
	// public boolean isFull() {
	// Iterator<C_SoilCell> it = this.cells.iterator();
	// while (it.hasNext())
	// if (!it.next().isFull()) return false;
	// return true;
	// }
	@Override
	public int getCarryingCapacity_Urodent() {
		int k = 0;
		for (C_SoilCell oneCell : this.cells) k += oneCell.getCarryingCapacity_Urodent();
		return k;
	}
	/** Unused, needed by the container interface <br>
	 * author JLF 10.2015, rev. 09.2016 */
	@Override
	public Coordinate getCoordinate_Ucs() {
		return this.cells.first().getCoordinate_Ucs();
	}
	@Override
	/** author JLF 01.2022 */
	public C_LandPlot getMyLandPlot() {
		// TODO Auto-generated method stub
		return null;
	}
}