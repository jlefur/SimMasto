/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing.ground;

import java.util.TreeSet;
import com.vividsolutions.jts.geom.Coordinate;
import data.C_Parameters;
import data.constants.I_ConstantString;

/** a soil unit, cell, pixel, ... (initially stood for land parcel)
 * @author Q.Baduel - 2009, JLF 2011, PAM 2013 */
public class C_SoilCell extends A_Container implements I_ConstantString {
	//
	// FIELDS
	//
	protected TreeSet<String> groundTypeList = new TreeSet<String>();
	public C_LandPlot affinityLandPlot;
	//
	// CONSTRUCTORS
	//
	/** A simple do nothing constructor necessary for daughter classes */
	public C_SoilCell() {};

	public C_SoilCell(int aff, int lineNo, int colNo) {// added PAMboup 21/12/2012, rev. JLF 07.2015
		this.affinity = aff;
		this.lineNo = lineNo;
		this.colNo = colNo;
	}
	//
	// OVERRIDEN METHOD
	//
	/** Remove references to groundTypeList */
	@Override
	public void discardThis() {
		this.groundTypeList = null;
		this.affinityLandPlot = null;
		super.discardThis();
	}
	//
	// METHODS
	//
	public int getCarryingCapacity_Urodent() {
		if (!this.isDead()) {// TODO JLF 2021.03 patch bound to agents'probes crash with new drastic wipeOffObjects
			if (this.getMyLandPlot() instanceof C_City) {
				C_City city = (C_City) this.getMyLandPlot();
				return city.getHumanPopSize_Uindividual() / city.getCells().size();
			}
			// TODO JLF 2015.03 reminder soil cells K = affinity; i.e., better habitat have higher carrying capacity
			else return affinity;
		}
		else return 0;
	}
	@Override
	public String toString() {
		return this.retrieveMyName() + "(" + this.lineNo + "," + this.colNo + ")";
	}
	/** To make this soil cell to be one of the ground types
	 * @param groundType : one ground type */
	public void addGroundType(String groundType) {
		this.groundTypeList.add(groundType);
	}
	public void removeGroundType(String groundType) {
		this.groundTypeList.remove(groundType);
	}
	public C_LandPlot getMyLandPlot() {
		return affinityLandPlot;
	}
	/** Inform the landPlot of this soilCell and add this soilcell to the landPlot.
	 * @param plot */
	public void setAffinityLandPlot(C_LandPlot plot) {
		this.affinityLandPlot = plot;
		plot.addCell(this);
	}
	/** Search in this.groundTypeList */
	public boolean isOfGroundType(String groundType) {
		return groundTypeList.contains(groundType);
	}
	public TreeSet<String> getGroundTypes() {
		return groundTypeList;
	}
	/** Account for things not contextualized (e.g.,soilCell)<br>
	 * Version PAMboup 04/2014, rev. jlf 01,02.2018
	 * @return Location of the soil cell's center in the continuous space if cell is within the context, else recompute from the
	 *         grid position */
	public Coordinate getCoordinate_Ucs() {
		double cellSize_Ucs = C_Parameters.CELL_SIZE_UcontinuousSpace;
		return new Coordinate((lineNo * cellSize_Ucs) + cellSize_Ucs / 2.0, (colNo * cellSize_Ucs) + cellSize_Ucs
				/ 2.0);
	}
}
