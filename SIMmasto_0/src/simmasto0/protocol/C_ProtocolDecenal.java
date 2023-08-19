/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package simmasto0.protocol;
import java.util.Calendar;

import data.C_Chronogram;
import data.C_Event;
import data.C_Parameters;
import data.constants.I_ConstantDecenal;
import data.converters.C_ConvertTimeAndSpace;
import presentation.display.C_Background;
import presentation.display.C_CustomPanelSet;
import presentation.epiphyte.C_InspectorGenetic;
import repast.simphony.context.Context;
import thing.C_Rodent;
import thing.C_RodentBlackRat;
import thing.dna.species.C_GenomeRattusRattus;
/** Protocol used in the south-east Senegal case study (grant ANR Chancira) Initialize the simulation and manage inputs coming from the csvevents file
 * @author J.Le Fur, 10.2014, rev. 11.2015 */
public class C_ProtocolDecenal extends A_ProtocolTransportation implements I_ConstantDecenal {
	//
	// FIELD
	//
	protected C_InspectorGenetic geneticInspector;
	//
	// CONSTRUCTOR
	//
	public C_ProtocolDecenal(Context<Object> ctxt) {
		super(ctxt);
		geneticInspector = new C_InspectorGenetic();
		inspectorList.add(geneticInspector);
		C_CustomPanelSet.addGeneticInspector(geneticInspector);
		facilityMap = new C_Background(-.45, 83, 75);
		// Create and build the dataFromChrono from the csv file
		chronogram = new C_Chronogram(I_ConstantDecenal.CHRONO_FILENAME);
	}
	//
	// METHODS
	//
	@Override
	public void step_Utick() {
		// Add rodents in Tambacounda
		if ((protocolCalendar.get(Calendar.DAY_OF_MONTH) == 01) && (protocolCalendar.get(Calendar.YEAR) > 1909))
			addRodentsInCity(10, this.inspectorTransportation.getCityByName("Tambacounda"));
		super.step_Utick();
	}
	@Override
	/** Specific preliminary management for ground nut trade and dirt tracks, then proceed to super.manageEvent()
	 * @see A.ProtocolTransportation.manageEvent(C_Event) <br>
	 *      Author J.Le Fur 09.2014 */
	public void manageOneEvent(C_Event event) {
		int x = event.whereX_Ucell, y = event.whereY_Ucell;
		switch (event.type) {
			case RAT_EVENT :
				int nbRodents = Integer.parseInt(event.value1);
				if (nbRodents > C_Parameters.RODENT_SUPER_AGENT_SIZE) nbRodents = nbRodents / C_Parameters.RODENT_SUPER_AGENT_SIZE;
				for (int i = 0; i < nbRodents; i++) {
					C_Rodent one_rodent = createRodent();
					contextualizeNewThingInGrid(one_rodent, x, y);
				}
				break;
		}
		super.manageOneEvent(event);
	}
	@Override
	public void initCalendar() {
		protocolCalendar.set(1983, Calendar.NOVEMBER, 25);
		protocolCalendar.TICK_MAX = (int) C_ConvertTimeAndSpace.getTimeBetweenDates_Ums("01/01/1910", "01/01/2013",
				C_ConvertTimeAndSpace.tick_UcalendarUnit);
	}
	@Override
	/** created rodents are of the Rattus rattus genus */
	public C_Rodent createRodent() {
		return new C_RodentBlackRat(new C_GenomeRattusRattus());
	}
	@Override
	public String toString() {
		return "protocolDecenal";
	}
}