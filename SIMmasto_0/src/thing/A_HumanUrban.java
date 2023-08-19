package thing;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import com.vividsolutions.jts.geom.Coordinate;

import data.constants.I_ConstantDodel2;
import data.converters.C_ConvertTimeAndSpace;
import simmasto0.C_ContextCreator;
import simmasto0.protocol.A_Protocol;
import thing.dna.I_DiploidGenome;
import thing.ground.C_SoilCell;
import thing.ground.C_SoilCellUrban;

/** Abstract Urban humans have a daily activity list with location and desires list with desires for each slice of days.<br>
 * They do not deliberate but got their activity location and perform the corresponding desire<br>
 * @author J.Le Fur, oct 2019, jan-feb 2020, 03.2021, 06.2022 */
public abstract class A_HumanUrban extends A_Human implements I_ConstantDodel2 {
	//
	// FIELDS
	//
	protected boolean a_tag = false;
	protected String aim = "";// details on desire
	private Map<String, Coordinate> activityList = new HashMap<String, Coordinate>();
	private Map<String, String> desireList = new HashMap<String, String>();
	private Map<String, String> aimList = new HashMap<String, String>();
	//
	// CONSTRUCTOR
	//
	public A_HumanUrban(I_DiploidGenome genome) {
		super(genome);
		this.setDesire(NONE);
		this.setAim(NONE);
	}
	//
	// OVERRIDEN METHODS
	//
	/** Human urban do not need to make a perception, they are driven by their activityList */
	@Override
	protected TreeSet<I_SituatedThing> perception() {
		return null;
	}
	/** Human urban do not deliberate, they are driven by their activityList */
	@Override
	protected TreeSet<I_SituatedThing> deliberation(TreeSet<I_SituatedThing> perceivedThings) {
		return null;
	}
	/** Human urban do not choose a destination, they are driven by their activityList */
	@Override
	protected I_SituatedThing setDestination(TreeSet<I_SituatedThing> alternatives) {
		return null;
	}
	/** JLF, 03.2021 ,rev MS 08.2021 if desire is wander, keep wandering, if */
	@Override
	protected void actionNoChoice() {
		if (this.getDesire().equals(WANDER)) this.actionWander();
		else if (this.getDesire().equals(STANDBY)) this.actionWander();
		else if (this.getDesire().equals(REST)) this.actionRest();
		else if (this.getDesire().equals(FEED)) this.actionEat();
		else if (!(FEED + REPRODUCE).contains(this.desire)) {
			// super.actionNoChoice();
			if (!(REST + NONE).contains(this.getDesire())) this.actionForage();
		}
	}
	/** JLF & MS 08.2021, rev JLF 06.2022 reset energy lost in foraging */
	@Override
	protected boolean actionEat() {
		this.energy_Ukcal=0.;
		return true;
	}

	/** Stay wandering in his current land plot / MS 08.2021, rev JLF 01.2022 */
	@Override
	public void actionWander() {
		// Object[] cells = this.getMyLandPlot().getCells().toArray();
		Object[] cells = ((C_SoilCellUrban) this.currentSoilCell).getConcession().getCells().toArray();
		if (cells.length != 0) {
			this.setTarget((C_SoilCell) cells[(int) (C_ContextCreator.randomGeneratorForMovement.nextDouble()
					* cells.length)]);
			if (this.desire.equals(STANDBY)) {
				this.computeNextMoveToTarget(this.speed_UmeterByTick / SLOW_FACTOR);
			}
			else this.computeNextMoveToTarget(this.speed_UmeterByTick);// @@ / SLOW_FACTOR);
			// Store the landPlot where wandering occurs JLF 01.2022
			this.actionMove();
			if (!this.getMyLandPlot().getCells().contains(this.currentSoilCell)) {
				C_SoilCell oneWanderingCell = (C_SoilCell) cells[(int) (C_ContextCreator.randomGeneratorForMovement
						.nextDouble() * cells.length)];
				C_ContextCreator.protocol.contextualizeOldThingInCell(this, oneWanderingCell);
			}
			this.setTarget(null);
		}
		else super.actionWander();
	}
	@Override
	public void discardThis() {
		this.activityList = null;
		this.desireList = null;
		this.aimList = null;
		super.discardThis();
	}
	//
	// METHODS
	//
	/** MS 08.2021 Allow human to choose the next activities */
	public void manageActivities() {
		Object[] keys = this.activityList.keySet().toArray();
		for (int i = 0; i < this.activityList.size(); i++) {
			String key = (String) keys[i];
			if (this.isIncludedInTimeStepInterval(key)) {
				this.setDesire(desireList.get(key));
				this.setAim(aimList.get(key));
				// Patch JLF: set target excepted when target is outside the grid
				if (A_VisibleAgent.myLandscape.getGrid().length >= (int) activityList.get(key).x && //
						A_VisibleAgent.myLandscape.getGrid()[0].length >= (int) activityList.get(key).y)
					this.setTarget(A_VisibleAgent.myLandscape.getGrid()[(int) activityList.get(
							key).x][(int) activityList.get(key).y]);
				break;
			}
		}
	}

	//
	// SETTERS & GETTERS
	//
	public void addActivityList(String time, Coordinate location, String desire1, String desire2) {
		this.activityList.put(time, location);
		this.desireList.put(time, desire1);
		this.aimList.put(time, desire2);
	}
	/** TODO MS de JLF 03.2021 inclure MS, D, MON, Y */
	public boolean isIncludedInTimeStepInterval(String key) {
		String[] keyTime = key.split(HOUR_MINUTE_SEPARATOR);
		Boolean flag = false;
		String tickUnit = C_ConvertTimeAndSpace.tick_UcalendarUnit;
		double simulationTime_Utick = this.getCurrentSimulationTime_Utick(tickUnit);
		switch (tickUnit) {
			case "H" : {
				double activityTime = Double.parseDouble(keyTime[0]) + Double.parseDouble(keyTime[1]) / 60;
				if (((simulationTime_Utick - activityTime) >= 0) && ((simulationTime_Utick
						- activityTime) <= C_ConvertTimeAndSpace.tick_Ucalendar)) flag = true;
			}
				break;
			case "M" : {
				double activityTime = Double.parseDouble(keyTime[0]) * 60 + Double.parseDouble(keyTime[1]);
				if (((simulationTime_Utick - activityTime) >= 0) && ((simulationTime_Utick
						- activityTime) <= C_ConvertTimeAndSpace.tick_Ucalendar)) flag = true;
			}
				break;
			case "S" : {
				double activityTime = Double.parseDouble(keyTime[0]) * 3600 + (Double.parseDouble(keyTime[1]) * 60);
				if (((simulationTime_Utick - activityTime) >= 0) && ((simulationTime_Utick
						- activityTime) <= C_ConvertTimeAndSpace.tick_Ucalendar)) flag = true;
			}
				break;
		}
		return flag;
	}
	/** TODO MS de JLF 03.2021 inclure MS, D, MON, Y */
	public double getCurrentSimulationTime_Utick(String unit) {
		double simulationTime = .0;
		switch (unit) {
			case "H" :
				simulationTime = A_Protocol.protocolCalendar.get(Calendar.HOUR_OF_DAY) + (A_Protocol.protocolCalendar
						.get(Calendar.MINUTE) / 60);
				break;
			case "M" :
				simulationTime = (A_Protocol.protocolCalendar.get(Calendar.HOUR_OF_DAY) * 60)
						+ A_Protocol.protocolCalendar.get(Calendar.MINUTE);
				break;
			case "S" :
				simulationTime = (A_Protocol.protocolCalendar.get(Calendar.HOUR_OF_DAY) * 3600)
						+ (A_Protocol.protocolCalendar.get(Calendar.MINUTE) * 60) + A_Protocol.protocolCalendar.get(
								Calendar.SECOND);
				break;
		}
		return simulationTime;
	}
	public String getActivityList() {
		if (this.isDead()) return "dead";// to avoid probe crash (JLF 03.2021)
		return this.activityList.toString();
	}
	public void setAim(String aim) {
		this.aim = aim;
	}
	public String getAim() {
		return this.aim;
	}
	public boolean isa_Tag() {
		return a_tag;
	}
	public void seta_Tag(boolean tagged) {
		this.a_tag = tagged;
		this.setHasToSwitchFace(true);
	}
}
