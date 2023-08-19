package simmasto0.protocol;

import java.util.Calendar;

import com.vividsolutions.jts.geom.Coordinate;

import presentation.display.C_Background;
import presentation.display.C_CustomPanelSet;
import presentation.display.C_UserPanel;
import presentation.epiphyte.C_InspectorEnergy;
import presentation.epiphyte.C_InspectorGenetic;
import repast.simphony.context.Context;
import repast.simphony.essentials.RepastEssentials;
import thing.C_RodentFossorial;
import thing.dna.species.C_GenomeMicrotusArvalis;
import thing.ground.C_LandPlot;
import thing.ground.C_SoilCell;
import data.C_CropRotationChize;
import data.C_Parameters;

/** Common voles' colonies within a dynamic agricultural landscape
 * @author J.Le Fur, A.Comte 03.2012 / J.Le Fur 07.2012, 07.2014 */

public class C_ProtocolChize extends A_ProtocolFossorial {
	//
	// FIELDS
	//
	private C_CropRotationChize cropRotation = null;
	protected C_InspectorGenetic geneticInspector;
	protected C_InspectorEnergy energyInspector;
	//
	// CONSTRUCTOR
	//
	/** Declare the inspectors, add them to the inspector list, declare them to the panelInitializer for indicators graphs<br>
	 * Author J.Le Fur 02.2013 */
	public C_ProtocolChize(Context<Object> ctxt) {
		super(ctxt);
		geneticInspector = new C_InspectorGenetic();
		inspectorList.add(geneticInspector);
		C_CustomPanelSet.addGeneticInspector(geneticInspector);
		C_UserPanel.addGeneticInspector(geneticInspector);
		
		
		cropRotation = new C_CropRotationChize(this.landscape);
		// Position crop at the barycentre of cells
		for (C_LandPlot lp : this.landscape.getAffinityLandPlots()) {
			double xx = 0., yy = 0.;
			for (C_SoilCell cell : lp.getCells()) {
				xx += cell.getCoordinate_Ucs().x;
				yy += cell.getCoordinate_Ucs().y;
			}
			xx = xx / lp.getCells().size();
			yy = yy / lp.getCells().size();
			this.landscape.moveToLocation(lp, new Coordinate(xx, yy));
			lp.setCurrentSoilCell(this.landscape.getGrid()[(int) xx][(int) yy]);
			lp.bornCoord_Umeter = this.landscape.getThingCoord_Umeter(lp.getCurrentSoilCell());
		}
		// facilityMap = new C_Background(-.05, 26, 27); //chize 1
		facilityMap = new C_Background(-.1, 27 - .4, 32 - .4); // chize 2
	}
	//
	// METHODS
	//
	@Override
	public void readUserParameters() {
		super.readUserParameters();
		C_Parameters.REPRO_START_Umonth = ((Integer) C_Parameters.parameters.getValue("REPRO_START_Umonth")).intValue();
		C_Parameters.REPRO_END_Umonth = ((Integer) C_Parameters.parameters.getValue("REPRO_END_Umonth")).intValue();
	}
	@Override
	/** randomly add burrows and randomly put fossorial rodent agents in them */
	public void initProtocol() {
		super.initProtocol();// initializes burrow systems in C_ProtocolFossorial
		this.cropRotation.setInitialLandplotTypes();
	}
	@Override
	public C_RodentFossorial createRodent() {
		return new C_RodentFossorial(new C_GenomeMicrotusArvalis());
	}
	@Override
	public void step_Utick() {
		super.step_Utick();// has to be after the other inspectors step since it records indicators in file
		if (!C_Parameters.BLACK_MAP) this.landscape.resetCellsColor();// account for culture changes
	}
	@Override
	/** Manages agricultural changes Authors JEL2011, AR, rev. Le Fur 2011, 2012, 04.2014, 08,09.2014 */
	public void manageTimeLandmarks() {
		if (RepastEssentials.GetTickCount() == 1.) inspector.recordSpatialDistributionInFile(this.landscape.getGrid());
		int currentYear = protocolCalendar.get(Calendar.YEAR);
		int currentMonth = protocolCalendar.get(Calendar.MONTH);
		super.manageTimeLandmarks();
		if (currentYear != protocolCalendar.get(Calendar.YEAR)) {
			A_Protocol.event("C_ProtocolChize.manageTimeLandmarks", "Crop Transition", isNotError);
			inspector.recordSpatialDistributionInFile(this.landscape.getGrid());
			cropRotation.cropTransition(protocolCalendar.get(Calendar.MONTH));
		}
		if (currentMonth != protocolCalendar.get(Calendar.MONTH)) {
			A_Protocol.event("C_ProtocolChize.manageTimeLandmarks", "Cultural Practice", isNotError);;
			cropRotation.culturalPractice(protocolCalendar.get(Calendar.MONTH));
		}
	}
	@Override
	public void initCalendar() {
		super.initCalendar();
		protocolCalendar.set(protocolCalendar.get(Calendar.YEAR), Calendar.FEBRUARY, 1, 0, 0, 0);
	}
	/** Color the map in black to see the overall distribution of burrows<br>
	 * Author J.Le Fur 10.2014 TODO JLF 2014.10 should be in presentation package ? */
	protected void blackMap() {
		for (int i = 0; i < this.landscape.getDimension_Ucell().getWidth(); i++)
			for (int j = 0; j < this.landscape.getDimension_Ucell().getHeight(); j++) {
				if (this.landscape.getValueLayer().get(i, j) == 1) // houses
					this.landscape.getValueLayer().set(2, i, j);
				else if (this.landscape.getValueLayer().get(i, j) != 7) // hedges
					this.landscape.getValueLayer().set(BLACK_MAP_COLOR, i, j);
			}
	}
}
