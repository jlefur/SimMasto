package simmasto0.protocol;
import java.util.Calendar;

import data.C_Chronogram;
import data.C_Event;
import data.C_Parameters;
import data.constants.I_ConstantCentenal;
import data.converters.C_ConvertTimeAndSpace;
import presentation.display.C_Background;
import presentation.display.C_CustomPanelSet;
import presentation.display.C_UserPanel;
import presentation.epiphyte.C_InspectorGenetic;
import repast.simphony.context.Context;
import simmasto0.C_ContextCreator;
import thing.C_Rodent;
import thing.C_RodentBlackRat;
import thing.dna.species.C_GenomeRattusRattus;
import thing.ground.C_City;
import thing.ground.C_SoilCellGraphed;
/** Initialize the simulation and manages the inputs coming from the csv events file / author J.Le Fur, Mboup 07/2012, rev. P.A.Mboup, 08.2013, JLF
 * 08.2014, 04.2015 */
public class C_ProtocolCentenal extends A_ProtocolTransportation implements I_ConstantCentenal {
	protected C_InspectorGenetic geneticInspector;
	//
	// CONSTRUCTOR
	//
	public C_ProtocolCentenal(Context<Object> ctxt) {
		super(ctxt);
		geneticInspector = new C_InspectorGenetic();
		inspectorList.add(geneticInspector);
		C_CustomPanelSet.addGeneticInspector(geneticInspector);
		C_UserPanel.addGeneticInspector(geneticInspector);
		facilityMap = new C_Background(-.255, 45, 32);
		// Create and build the dataFromChrono from the csv file except the bioclimate part used to build the initial raster
		chronogram = new C_Chronogram(EVENT_CHRONO_NAME);
	}
	//
	// METHODS
	//
	@Override
	public void step_Utick() {
		addRodentInTradingPost();
		super.step_Utick();
	}
	/** Add rodents in France */
	public void addRodentInTradingPost1() {
		if ((protocolCalendar.get(Calendar.DAY_OF_MONTH) == 01) && (protocolCalendar.get(Calendar.YEAR) > 1909))
			addRodentsInCity(nbShipWreckedRats_UperMonth, this.inspectorTransportation.getCityByName("France"));
	}
	public void addRodentInTradingPost0() {
		// Add rats randomly in trading post
		if ((protocolCalendar.get(Calendar.DAY_OF_MONTH) == 01) && (protocolCalendar.get(Calendar.YEAR) > 1909)) {
			C_City[] harbour = new C_City[9];
			harbour[0] = this.inspectorTransportation.getCityByName("Mbour");
			harbour[1] = this.inspectorTransportation.getCityByName("Dakar");
			harbour[2] = this.inspectorTransportation.getCityByName("Saint-Louis");
			harbour[3] = this.inspectorTransportation.getCityByName("Saint-Louis");
			harbour[4] = this.inspectorTransportation.getCityByName("Saint-Louis");
			harbour[5] = this.inspectorTransportation.getCityByName("Saint-Louis");// 4 times to increase this city
			harbour[6] = this.inspectorTransportation.getCityByName("Ziguinchor");
			harbour[7] = this.inspectorTransportation.getCityByName("Kaolack");
			harbour[8] = this.inspectorTransportation.getCityByName("Banjul");
			int randx = (int) (C_ContextCreator.randomGeneratorForInitialisation.nextDouble() * 8.);
			if (C_Parameters.VERBOSE)
				A_Protocol.event("C_ProtocolCentenal.step_Utick","Added " + nbShipWreckedRats_UperMonth + "rats from sea cargo/wreck in "
						+ harbour[randx] + ".", isNotError);
			addRodentsInCity(nbShipWreckedRats_UperMonth, harbour[randx]);
			super.step_Utick();// has to be after the other inspectors step since it records indicators in file
		}
	}
	public void addRodentInTradingPost() {
		// Add rats randomly in trading post
		if ((protocolCalendar.get(Calendar.DAY_OF_MONTH) == 02) && (protocolCalendar.get(Calendar.YEAR) > 1909)) {
			C_City[] harbour = new C_City[9];
			harbour[0] = this.inspectorTransportation.getCityByName("Dakar");// 4 times to increase this city
			harbour[1] = this.inspectorTransportation.getCityByName("Dakar");
			harbour[2] = this.inspectorTransportation.getCityByName("Dakar");
			harbour[3] = this.inspectorTransportation.getCityByName("Saint-Louis");
			int randx = (int) (C_ContextCreator.randomGeneratorForInitialisation.nextDouble() * 4.);
			if (C_Parameters.VERBOSE)
				A_Protocol.event("C_ProtocolCentenal.addRodentInTradingPost","Added " + nbShipWreckedRats_UperMonth + " rats from sea cargo or wreck in "
						+ harbour[randx] + ".", isNotError);
			addRodentsInCity(nbShipWreckedRats_UperMonth, harbour[randx]);
		}
	}
	@Override
	/** Specific preliminary management for ground nut trade and dirt tracks, then proceed to super.manageEvent()
	 * @see A.ProtocolTransportation.manageEvent(C_Event) <br>
	 *      Author J.Le Fur 09.2014 */
	public void manageOneEvent(C_Event event) {
		int x = event.whereX_Ucell, y = event.whereY_Ucell;
		C_SoilCellGraphed eventSC = (C_SoilCellGraphed) this.landscape.getGrid()[x][y];// SC for SoilCell
		switch (event.type) {
			case RAT_EVENT :
				int nbRodents = Integer.parseInt(event.value1);
				if (nbRodents > C_Parameters.RODENT_SUPER_AGENT_SIZE) nbRodents = nbRodents / C_Parameters.RODENT_SUPER_AGENT_SIZE;
				for (int i = 0; i < nbRodents; i++) {
					C_Rodent one_rodent = createRodent();
					contextualizeNewThingInGrid(one_rodent, x, y);
				}
				break;// Set ground nut trade areas embedding according to: weak include medium include heavy
			case GNT_HEAVY_EVENT :
				eventSC.getGroundTypes().add(GNT_MEDIUM_EVENT);
				eventSC.getGroundTypes().add(GNT_WEAK_EVENT);
				break;
			case GNT_MEDIUM_EVENT :
				eventSC.getGroundTypes().add(GNT_WEAK_EVENT);
				break;
		}
		super.manageOneEvent(event);
	}
	@Override
	/** Create rodent of the Rattus rattus genus */
	public C_Rodent createRodent() {
		return new C_RodentBlackRat(new C_GenomeRattusRattus());
	}
	@Override
	public void initCalendar() {
		protocolCalendar.set(1910, Calendar.JANUARY, 31);// protocolCalendar.set(1909, Calendar.DECEMBER, 29);
		// protocolCalendar.set(1940, Calendar.JANUARY, 31);// protocolCalendar.set(1909, Calendar.DECEMBER, 29);
		protocolCalendar.TICK_MAX = (int) C_ConvertTimeAndSpace.getTimeBetweenDates_Ums("01/01/1910", "01/01/2013",
				C_ConvertTimeAndSpace.tick_UcalendarUnit);
	}
	@Override
	public String toString() {
		return "protocolCentenal";
	}
}