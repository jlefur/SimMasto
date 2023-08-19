/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package simmasto0.protocol;

import java.awt.Dimension;

import data.C_Parameters;
import presentation.display.C_Background;
import presentation.display.C_CustomPanelSet;
import presentation.display.C_UserPanel;
import presentation.epiphyte.C_InspectorFossorialRodents;
import presentation.epiphyte.C_InspectorGenetic;
import presentation.epiphyte.C_InspectorHybrid;
import presentation.epiphyte.C_InspectorPopulation;
import repast.simphony.context.Context;
import simmasto0.C_ContextCreator;
import thing.C_RodentFossorial;
import thing.dna.C_GenomeEucaryote;
import thing.dna.species.C_GenomeMastoErythroleucus;
import thing.dna.species.C_GenomeMastoNatalensis;
import thing.ground.C_BurrowSystem;

/** This is a test protocol for observing the various types of behaviours, including hybridization between species on a simple patchy environment<br>
 * Can be run using different levels of the rodent's hierarchy (instantiable classes): for that purpose, switch the import and the lines concerned
 * between C_Rodent, C_RodentFossorial, C_RodentFossorialColonial... then recompile the java class<br>
 * @version J.Le Fur, A.Comte 03.2012 / J.Le Fur 07.2012, 02.2013, 07-09.2017 */

public class C_ProtocolHybridUniform extends A_Protocol {
	//
	// FIELDS
	//
	protected C_InspectorHybrid hybridInspector;
	protected C_InspectorGenetic geneticInspector;
	protected C_InspectorFossorialRodents burrowInspector;
	//
	// CONSTRUCTOR
	//
	public C_ProtocolHybridUniform(Context<Object> ctxt) {
		super(ctxt);
		this.burrowInspector = new C_InspectorFossorialRodents();
		this.geneticInspector = new C_InspectorGenetic();
		this.hybridInspector = new C_InspectorHybrid();
		this.inspectorList.add(hybridInspector);
		this.inspectorList.add(burrowInspector);
		this.inspectorList.add(geneticInspector);
		// Declare the inspector that stores the lethal alleles causes JLF 02.2013
		C_GenomeEucaryote.init(hybridInspector);
		C_CustomPanelSet.addHybridInspector(hybridInspector);
		C_CustomPanelSet.addGeneticInspector(geneticInspector);
		C_UserPanel.addGeneticInspector(geneticInspector);
		C_CustomPanelSet.addBurrowInspector(burrowInspector);
		C_UserPanel.addBurrowInspector(burrowInspector);
		this.facilityMap = new C_Background(-.13, 25, 31);
	}
	//
	// METHODS
	//
	/** Fills the context with dynamics agent for the first step of a simulation. The sex ratio is randomly generated, rev jlf 01.2018 */
	public void initProtocol() {
		int nbAgent = C_Parameters.INIT_RODENT_POP_SIZE;
		// TODO JLF 2014.08 code below should replaced with an upper method
		C_RodentFossorial agent;
		Dimension dim = this.landscape.getDimension_Ucell();
		int grid_width = (int) dim.getWidth();
		int grid_height = (int) dim.getHeight();
		for (int i = 0; i < nbAgent; i++) {
			double randx = (C_ContextCreator.randomGeneratorForInitialisation.nextDouble() * grid_width);
			double randy = (C_ContextCreator.randomGeneratorForInitialisation.nextDouble() * grid_height);
			// Put all rodents at the middle at init:
			// int randx = (int) (grid_width / 2);
			// int randy = (int) (grid_height / 2);
			// Create the agents
			if (C_ContextCreator.randomGeneratorForInitialisation.nextDouble() >= .5) agent = new C_RodentFossorial(new C_GenomeMastoNatalensis());
			else agent = new C_RodentFossorial(new C_GenomeMastoErythroleucus());
			agent.setRandomAge();
			contextualizeNewThingInSpace(agent, randx, randy);
			agent.setNewRandomMove();
		}
		super.initProtocol();
	}
	@Override
	public void readUserParameters() {
		super.readUserParameters();
		// If switch to non-persistent burrows then tag empty burrows as dead
		boolean oldValue = C_Parameters.PERSISTANCE_BURROW;
		C_Parameters.PERSISTANCE_BURROW = ((Boolean) C_Parameters.parameters.getValue("PERSISTANCE_BURROW")).booleanValue();
		if (oldValue != C_Parameters.PERSISTANCE_BURROW && this.burrowInspector != null) for (C_BurrowSystem burrow : this.burrowInspector
				.getBurrowList())
			if (burrow.getOccupantList().size() == 0 && burrow.getAnimalsTargetingMe().size() == 0) burrow.setDead(true);
		C_Parameters.EXCLOS = ((Boolean) C_Parameters.parameters.getValue("EXCLOS")).booleanValue();
		// blackmap
		boolean oldValueBlackMap = C_Parameters.BLACK_MAP;
		C_Parameters.BLACK_MAP = ((Boolean) C_Parameters.parameters.getValue("BLACK_MAP")).booleanValue();
		if (oldValueBlackMap != C_Parameters.BLACK_MAP) {
			if (C_Parameters.BLACK_MAP) this.blackMap();
			else if (this.landscape != null) this.landscape.resetCellsColor();
		}// Reproduction attributes common to both sexes //
		C_Parameters.REPRO_START_Umonth = ((Integer) C_Parameters.parameters.getValue("REPRO_START_Umonth")).intValue(); // 91;
		C_Parameters.REPRO_END_Umonth = ((Integer) C_Parameters.parameters.getValue("REPRO_END_Umonth")).intValue();
	}
	/** Close simulation if population is < 2 or if a precondition is verified. <br>
	 * This method may be redefined by daughter protocols */
	public boolean isSimulationEnd() {
		if (C_InspectorPopulation.getNbFemales() == 0) {
			if (C_Parameters.VERBOSE) java.awt.Toolkit.getDefaultToolkit().beep();
			A_Protocol.event("C_ProtocolHybridUniform.isSimulationEnd", "Population is extinct (no female); halting simulation", isNotError);
			return true;
		}
		return super.isSimulationEnd();
	}
	/** Color the map in black to see the overall distribution of burrows<br>
	 * Copy from protocolChize<br>
	 * TODO JLF 2014.10 should be in presentation package ? */
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
