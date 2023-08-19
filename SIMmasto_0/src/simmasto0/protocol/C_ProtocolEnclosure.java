package simmasto0.protocol;

import java.util.Calendar;

import presentation.display.C_CustomPanelSet;
import presentation.display.C_UserPanel;
import presentation.epiphyte.C_InspectorFossorialRodents;
import presentation.epiphyte.C_InspectorGenetic;
import presentation.epiphyte.C_InspectorHybrid;
import repast.simphony.context.Context;
import thing.C_Rodent;
import thing.dna.C_GenomeEucaryote;
import thing.dna.species.C_GenomeMastoErythroleucus;
import thing.dna.species.C_GenomeMastoNatalensis;
import data.C_Parameters;
import data.constants.I_ConstantNumeric;

/** author J.Le Fur, A.Comte 03/2012 / rev. J.Le Fur feb.2013 */

public class C_ProtocolEnclosure extends A_Protocol implements I_ConstantNumeric {
	protected C_InspectorGenetic geneticInspector;
	protected C_InspectorFossorialRodents burrowInspector;
	protected C_InspectorHybrid hybridInspector;
	public C_ProtocolEnclosure(Context<Object> ctxt) {
		super(ctxt);// Init parameters and higher level inspectors & displays
		burrowInspector = new C_InspectorFossorialRodents();
		geneticInspector = new C_InspectorGenetic();
		hybridInspector = new C_InspectorHybrid();
		inspectorList.add(hybridInspector);
		inspectorList.add(burrowInspector);
		inspectorList.add(geneticInspector);
		// declare the inspector that stores the lethal alleles causes JLF 02.2013
		C_GenomeEucaryote.init(hybridInspector);
		C_CustomPanelSet.addHybridInspector(hybridInspector);
		C_CustomPanelSet.addGeneticInspector(geneticInspector);
		C_UserPanel.addBurrowInspector(burrowInspector);
	}

	@Override
	public void initProtocol() { //TODO JLF 2012.02 use of Math.random() in protocole cage ~OK (robustless however)
		for (int i = 0; i < 4; i++) {
			C_Rodent female = new C_Rodent(new C_GenomeMastoNatalensis(SEX_GENE_X));
			contextualizeNewThingInSpace(female,(Math.random() * 40) + 4, (Math.random() * 40) + 4);
		}
		for (int i = 0; i < 2; i++) {
			C_Rodent male = new C_Rodent(new C_GenomeMastoNatalensis(SEX_GENE_Y));
			contextualizeNewThingInSpace(male, (Math.random() * 40) + 4,  (Math.random() * 40) + 4);
		}
		for (int i = 0; i < 3; i++) {
			C_Rodent female = new C_Rodent(new C_GenomeMastoErythroleucus(SEX_GENE_X));
			contextualizeNewThingInSpace(female,(Math.random() * 40) + 48,  (Math.random() * 40) + 4);
		}
		for (int i = 0; i < 3; i++) {
			C_Rodent male = new C_Rodent(new C_GenomeMastoErythroleucus(SEX_GENE_Y));
			contextualizeNewThingInSpace(male, (Math.random() * 40) + 48,(Math.random() * 40) + 4);
		}
		super.initProtocol();
	}
	@Override
	public void step_Utick() {
		super.step_Utick();// has to be after the other inspectors step since it records indicators in file
	}

	@Override
	public void readUserParameters() {
		super.readUserParameters();
		C_Parameters.INIT_BURROW_POP_SIZE = ((Integer) C_Parameters.parameters.getValue("NUMBER_OF_BURROW_SYSTEM")).intValue();
		C_Parameters.REPRO_START_Umonth = ((Integer) C_Parameters.parameters.getValue("REPRO_START_Umonth")).intValue();
		C_Parameters.REPRO_END_Umonth = ((Integer) C_Parameters.parameters.getValue("REPRO_END_Umonth")).intValue();
	}

	@Override
	public void initCalendar() {
		protocolCalendar.set(2014, Calendar.JANUARY, 1);
	}
}
