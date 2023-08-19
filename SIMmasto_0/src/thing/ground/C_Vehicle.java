/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing.ground;

import simmasto0.C_ContextCreator;
import thing.C_HumanCarrier;
import thing.C_Rodent;
import data.C_Parameters;
import data.constants.I_ConstantTransportation;
import data.converters.C_ConvertTimeAndSpace;
/** A vehicle is an object owned by a human carrier. its coordinates are those of its owner. author J.Le Fur, Mboup 07/2012, rev jlf 03.2015 */
public class C_Vehicle extends A_SupportedContainer implements I_ConstantTransportation {
	//
	// FIELDS
	//
	protected C_HumanCarrier owner;
	protected int speed_UmeterByTick;
	protected String type;
	protected double loadingProba;
	protected String graphType;
	private boolean accountForTrackCondition;// PAM 08/12/15
	protected boolean parked;
	//
	// CONSTRUCTORS
	//
	public C_Vehicle() {
		super();
	}
	public C_Vehicle(String type, C_HumanCarrier happyOwner) {
		this.owner = happyOwner;
		this.currentSoilCell = this.owner.getCurrentSoilCell();
		this.type = type;
		this.setMyName(type + NAMES_SEPARATOR + this.retrieveId());
		setSpeed_UmeterByTick();
		String[] specs = VEHICLE_SPECS.get(type);
		this.loadingProba = Integer.parseInt(specs[LOADING_PROBA_COL]) / C_Parameters.VEHICLE_LOADING_PROBA_DIVIDER;;
		this.graphType = specs[GRAPH_TYPE_COL];
		if (specs[ACCOUNT_FOR_TRACK_CONDITION].equalsIgnoreCase("true")) this.accountForTrackCondition = true;
		else this.accountForTrackCondition = false;
	}
	//
	// METHODS
	//
	/** Parameters override constants */
	public void initParameters() {
		String[] specs = VEHICLE_SPECS.get(this.type);
		this.loadingProba = Integer.parseInt(specs[LOADING_PROBA_COL]) / C_Parameters.VEHICLE_LOADING_PROBA_DIVIDER;
		String[] speedUnit = specs[REAL_SPEED_UNIT_COL].split("/");
		int speed = Integer.parseInt(specs[REAL_SPEED_COL]);
		this.speed_UmeterByTick = (int) C_ConvertTimeAndSpace.convertSpeed_UspaceByTick(speed, speedUnit[0], speedUnit[1]);
	}
	/** Move physically rodents but do not make change currentSoilCell (vehicle is a mobile SoilCell) */
	public void carryRodentsToMyLocation_Ucs() {
		for (thing.C_Rodent one_rodent : this.getFullRodentList())
			myLandscape.moveToLocation(one_rodent, myLandscape.getThingCoord_Ucs(this));
	}
	public void unloadRodents() {
		for (C_Rodent oneRodent : this.getFullRodentList()) {
			C_ContextCreator.protocol.contextualizeOldThingInCell(oneRodent, this.currentSoilCell);
			C_ContextCreator.protocol.landscape.moveToLocation(oneRodent, this.getCoordinate_Ucs());
			oneRodent.setTrappedOnBoard(false);
			oneRodent.hasToSwitchFace = true;
			oneRodent.setNewRandomMove();
			oneRodent.actionMove();
		}
	}
	//
	// SETTERS & GETTERS
	//
	/** Re-compute vehicle speed when tick definition has changed */
	public void setSpeed_UmeterByTick() {
		String[] specs = VEHICLE_SPECS.get(this.type);
		String[] speedUnit = specs[REAL_SPEED_UNIT_COL].split("/");
		int speed = Integer.parseInt(specs[REAL_SPEED_COL]);
		this.speed_UmeterByTick = (int) C_ConvertTimeAndSpace.convertSpeed_UspaceByTick(speed, speedUnit[0], speedUnit[1]);
	}
	@Override
	public int getCarryingCapacity_Urodent() {
		return Integer.parseInt(VEHICLE_SPECS.get(this.type)[RODENT_MAX_LOAD_PERCENT_COL]);
	}
	public String getType() {
		return type;
	}
	public String getGraphType() {
		return graphType;
	}
	public int getSpeed_UmeterByTick() {
		return this.speed_UmeterByTick;
	}
	public double getLoadingProba() {
		return this.loadingProba;
	}
	public boolean isAccountForTrackCondition() {
		return this.accountForTrackCondition;
	}
	public boolean isParked() {
		return this.parked;
	}
	public void setParked(boolean parked) {
		this.parked = parked;
	}
}
