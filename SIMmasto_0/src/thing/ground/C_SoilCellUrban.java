package thing.ground;

import data.constants.I_ConstantDodel2;

/** Soil unit able to support wall obstacle * @author JLF MS 09-2019, full rev. JLF 01,05.2022 */
public class C_SoilCellUrban extends C_SoilCellNode implements I_ConstantDodel2 {
	//
	// FIELDS
	//
	/** concession is necessary to distinguish movements within a dwelling (standard movement) and out of dwelling (with
	 * PathWandererAstar).<br>
	 * The field is identified on the first pass of identifyLandPlot with an override here of setAffinityLandPlot */
	private C_LandPlot concession;
	/** isStreet field tested at this level following pb of multiple identifications on initialization. IsStreet is used to
	 * provide isBlock used by Astar */
	private Integer street;
	//
	// CONSTRUCTOR
	//
	public C_SoilCellUrban(int aff, int lineNo, int colNo) {
		super(aff, lineNo, colNo);
	}
	//
	// OVERRIDEN METHODS
	//
	@Override
	public boolean isBlock() {
		// identify street here since several manipulation of affinity done by Moussa and not understood JLF 04.2022
		if (this.street != null) return this.street.equals(0);
		else {
			if (affinity == ROAD_AFFINITY || affinity == STREET_AFFINITY || affinity == MARKET_AFFINITY)
				this.street = 1;
			else
				this.street = 0;
		}
		return this.street.equals(0);
	}
	public boolean isStreet() {
		return !this.isBlock();
	}
	/** Determine on the first pass the concession to which oneSoilCell belongs - JLF 05.2022 <br>
	 * Inform the landPlot of this soilCell and add this soilcell to the landPlot. */
	@Override
	public void setAffinityLandPlot(C_LandPlot plot) {
		this.affinityLandPlot = plot;
		plot.addCell(this);
		if (this.concession == null) this.setConcession(plot);
	}
	@Override
	public String toString() {
		return this.affinityLandPlot.plotType + " Cell [row=" + this.lineNo + ", col=" + this.colNo + "]";
	}
	//
	// GETTERS
	//
	public boolean isDangerousArea() {
		return affinity == ROAD_AFFINITY || affinity == STREET_AFFINITY || affinity == TRACK_AFFINITY;
	}
	public boolean isWall() {
		return affinity == WALL_AFFINITY;
	}
	public C_LandPlot getConcession() {
		return concession;
	}
	public void setConcession(C_LandPlot concession) {
		this.concession = concession;
	}
}
