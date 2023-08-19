/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package simmasto0.protocol;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import com.vividsolutions.jts.geom.Coordinate;

import data.C_Parameters;
import data.constants.I_ConstantCage;
import data.constants.I_ConstantNumeric;
import presentation.display.C_Background;
import presentation.display.C_CustomPanelSet;
import presentation.display.C_UserPanel;
import presentation.epiphyte.C_InspectorGenetic;
import presentation.epiphyte.C_InspectorHybrid;
import presentation.epiphyte.C_InspectorPopulation;
import repast.simphony.context.Context;
import simmasto0.C_ContextCreator;
import thing.A_Amniote;
import thing.C_Rodent;
import thing.C_RodentCaged;
import thing.I_SituatedThing;
import thing.dna.C_GenomeEucaryote;
import thing.dna.species.C_GenomeMastoErythroleucus;
import thing.dna.species.C_GenomeMastoNatalensis;
import thing.dna.species.C_GenomeMastomys;
import thing.ground.C_LandPlot;
import thing.ground.C_SoilCell;

/** first line: nat x nat and ery x ery second line: nat x ery and ery x nat third line : hybrid x hybrid fourth line : hybrid x
 * nat(male) and hybrid x ery(male)
 * @author J.Le Fur, A.Comte 03/2012, rev. JLF 09.2017 */
public class C_ProtocolCage extends A_Protocol implements I_ConstantNumeric, I_ConstantCage {
	//
	// FIELDS
	//
	private C_LandPlot cagesMatrix[][];
	protected C_InspectorGenetic geneticInspector;
	private C_InspectorHybrid hybridInspector;
	//
	// CONSTRUCTOR
	//
	/** declare the inspectors, add them to the inspector list, declare them to the panelInitializer for indicators graphs. Author
	 * J. Le Fur 02.2013 */
	public C_ProtocolCage(Context<Object> ctxt) {
		super(ctxt);
		cagesMatrix = new C_LandPlot[NB_CAGES_LINES][NB_CAGES_COLUMNS];
		geneticInspector = new C_InspectorGenetic();
		hybridInspector = new C_InspectorHybrid();
		inspectorList.add(geneticInspector);
		inspectorList.add(hybridInspector);
		C_GenomeEucaryote.init(hybridInspector);// declares the inspector that stores the lethal alleles causes JLF 02.2013
		A_Amniote.init(hybridInspector);// declares the inspector that stores the lethal alleles causes JLF 02.2013
		C_CustomPanelSet.addGeneticInspector(geneticInspector);
		C_CustomPanelSet.addHybridInspector(hybridInspector);
		C_UserPanel.addGeneticInspector(geneticInspector);
		C_UserPanel.addHybridInspector(hybridInspector);
		this.facilityMap = new C_Background(-.04, 31, 25);
	}
	//
	// METHODS
	//
	/** Position the pure rodents Ery and Nat in the cages for the beginning of the protocol. <br>
	 * The first line: pures crosses nat x nat and ery x ery <br>
	 * The second line: crosses nat x ery and ery x nat <br>
	 * A-The third line: Hybrids from the second line are crossed with themselves <br>
	 * B-The fourth line: Hybrids from the second line are crossed with pure males <br>
	 * C-the fifth line: Hybrids from the second line are crossed with pure females <br>
	 * Then, A,B and C are repeated for hybrids from the fourth line.<br>
	 * Author J.Le Fur, A.Comte 03/2012 */
	@Override
	public void initProtocol() {
		TreeSet<C_LandPlot> landPlotList = (TreeSet<C_LandPlot>) this.landscape.getAffinityLandPlots().clone();
		for (C_LandPlot plot : landPlotList)
			if (plot.getAffinity() < FORBID_ZONE) {
				this.landscape.getAffinityLandPlots().remove(plot);
				this.context.remove(plot);
			}
		C_LandPlot[] cageList = new C_LandPlot[NB_CAGES_COLUMNS * NB_CAGES_LINES];
		this.landscape.getAffinityLandPlots().toArray(cageList);
		// Invert the array, JLF 10.214
		for (int i = 0; i < cageList.length / 2; i++) {
			C_LandPlot temp = cageList[i];
			cageList[i] = cageList[cageList.length - i - 1];
			cageList[cageList.length - i - 1] = temp;
		}
		C_RodentCaged male = null;
		C_RodentCaged female = null;
		// The PlotMatrix is created to have the origin (line = 0, col = 0) at the upper left cage.
		int line = NB_CAGES_LINES - 1, col = 0;
		for (C_LandPlot cage : cageList) {
			if (col < NB_CAGES_COLUMNS) {
				if (line >= 0) {
					this.cagesMatrix[line][col] = cage;
					cage.setMyName("C" + line + "-" + col);
					line--;
				}
				else {
					line = NB_CAGES_LINES - 1;
					col++;
					this.cagesMatrix[line][col] = cage;
					cage.setMyName("Cage-" + line + "-" + col);
					line--;
				}
			}
		}
		// Position cages at the barycentre of cells
		for (C_LandPlot oneCage : this.landscape.getAffinityLandPlots()) {
			double xx = 0., yy = 0.;
			for (C_SoilCell cell : oneCage.getCells()) {
				xx += cell.getCoordinate_Ucs().x;
				yy += cell.getCoordinate_Ucs().y;
			}
			xx = xx / oneCage.getCells().size();
			yy = yy / oneCage.getCells().size();
			this.landscape.moveToLocation(oneCage, new Coordinate(xx, yy));
			oneCage.setCurrentSoilCell(oneCage.getCells().first());
		}
		// (NB_CAGES_COLUMNS / 2) is to have half the column erythroleucus and the other half natalensis.
		for (int col2 = 0; col2 < NB_CAGES_COLUMNS / 2; col2++) {
			// ery x ery and nat x nat
			male = new C_RodentCaged(new C_GenomeMastoNatalensis(SEX_GENE_Y));
			female = new C_RodentCaged(new C_GenomeMastoNatalensis(SEX_GENE_X));
			contextualizeNewRodentInCage(female, 0, col2);
			contextualizeNewRodentInCage(male, 0, col2);
		}
		for (int col2 = NB_CAGES_COLUMNS / 2; col2 < NB_CAGES_COLUMNS; col2++) {
			male = new C_RodentCaged(new C_GenomeMastoErythroleucus(SEX_GENE_Y));
			female = new C_RodentCaged(new C_GenomeMastoErythroleucus(SEX_GENE_X));
			contextualizeNewRodentInCage(female, 0, col2);
			contextualizeNewRodentInCage(male, 0, col2);
		}
		// Mery x Fnat and Mnat x Fery
		for (int col2 = 0; col2 < NB_CAGES_COLUMNS / 2; col2++) {
			male = new C_RodentCaged(new C_GenomeMastoNatalensis(SEX_GENE_Y));
			female = new C_RodentCaged(new C_GenomeMastoErythroleucus(SEX_GENE_X));
			contextualizeNewRodentInCage(female, 1, col2);
			contextualizeNewRodentInCage(male, 1, col2);
		}
		for (int col2 = NB_CAGES_COLUMNS / 2; col2 < NB_CAGES_COLUMNS; col2++) {
			male = new C_RodentCaged(new C_GenomeMastoErythroleucus(SEX_GENE_Y));
			female = new C_RodentCaged(new C_GenomeMastoNatalensis(SEX_GENE_X));
			contextualizeNewRodentInCage(female, 1, col2);
			contextualizeNewRodentInCage(male, 1, col2);
		}
		// nat x Hyb and ery x Hyb --> here only the pure Nat and ery are added.
		for (int line2 = 1; (line2 * 3 + 1) < NB_CAGES_LINES; line2++) {
			for (int col2 = 0; col2 < NB_CAGES_COLUMNS / 2; col2++) {
				male = new C_RodentCaged(new C_GenomeMastoNatalensis(SEX_GENE_Y));
				contextualizeNewRodentInCage(male, (line2 * 3), col2); // males are added every three lines
				female = new C_RodentCaged(new C_GenomeMastoNatalensis(SEX_GENE_X));
				contextualizeNewRodentInCage(female, (line2 * 3 + 1), col2);// females are added every three lines after the males
			}
			for (int col2 = NB_CAGES_COLUMNS / 2; col2 < NB_CAGES_COLUMNS; col2++) {
				male = new C_RodentCaged(new C_GenomeMastoErythroleucus(SEX_GENE_Y));
				contextualizeNewRodentInCage(male, (line2 * 3), col2); // males are added every three lines
				female = new C_RodentCaged(new C_GenomeMastoErythroleucus(SEX_GENE_X));
				contextualizeNewRodentInCage(female, (line2 * 3 + 1), col2);// females are added every three lines after the males
			}
		}
		super.initProtocol();
	}
	private void contextualizeNewRodentInCage(C_RodentCaged rodent, int line, int col) {
		int h = (int) (C_ContextCreator.randomGeneratorForInitialisation.nextDouble() * (cagesMatrix[line][col]).getCells().size());
		C_SoilCell cell = (cagesMatrix[line][col]).retrieveOneCell(h);
		contextualizeNewThingInGrid(rodent, cell.retrieveLineNo(), cell.retrieveColNo());
		rodent.setRandomAge();
		rodent.setBirthCage(cagesMatrix[line][col]);
		rodent.setCurrentCage(cagesMatrix[line][col]);
		rodent.setCurrentLine(line);
	}
	/** method must be called at each step */
	@Override
	public void step_Utick() {
		// Select hybrids
		TreeSet<C_RodentCaged> hybridList = new TreeSet<C_RodentCaged>();
		for (C_Rodent rodent : C_InspectorPopulation.rodentList)
			if (rodent.getGenome().getClass() == C_GenomeMastomys.class) hybridList.add((C_RodentCaged) rodent);
		//
		// LOOP
		//
		for (C_RodentCaged cagedRodent : hybridList) {
			boolean found = false;
			for (int i = 0; i < NB_CAGES_LINES / 3; i++) {
				int lineHybrids = (i * 3) + 2, lineMixMale = lineHybrids + 1, lineMixFemale = lineMixMale + 1;
				for (int col = 0; col < NB_CAGES_COLUMNS; col++) {
					// System.out.println(cagedRodent.getCurrentLine()+" -- "+cagedRodent.getCurrentSoilCell().toString()+" --
					// "+lineHybrids+" -- "+col);
					C_LandPlot cage = cagesMatrix[lineHybrids][col];
					TreeSet<C_Rodent> occupants = cage.getFullRodentList();
					if (occupants.isEmpty()) {
						changeCage(cagedRodent, lineHybrids, col, "F" + (i + 1));
						found = true;
						break;
					}
					else if (occupants.size() == 1) {
						C_RodentCaged occupant = (C_RodentCaged) occupants.first();

						if ((occupant.getBirthCage() != cagedRodent.getBirthCage()) && (!sameSex(occupant, cagedRodent))) {
							changeCage(cagedRodent, lineHybrids, col, "F" + (i + 1));
							found = true;
							break;
						}
					}
				}
				if (!found) {
					for (int col = 0; col < NB_CAGES_COLUMNS; col++) {
						if ((cagesMatrix[lineMixMale][col].getLoad_Urodent() == 1) && (cagedRodent.testFemale())) {
							changeCage(cagedRodent, lineMixMale, col, "F" + (i + 1));
							found = true;
							break;
						}
						else if ((cagesMatrix[lineMixFemale][col].getLoad_Urodent() == 1) && (cagedRodent.testMale())) {
							changeCage(cagedRodent, lineMixFemale, col, "F" + (i + 1));
							found = true;
							break;
						}
					}
				}
				else break;
			}
		}
		// Killing rodents not used
		for (int line = 0; line < NB_CAGES_LINES; line++) {
			for (int col = 0; col < NB_CAGES_COLUMNS; col++) {
				C_LandPlot cage2 = cagesMatrix[line][col];
				TreeSet<C_Rodent> occupants2 = cage2.getFullRodentList();
				if (occupants2.size() >= 2) {
					ArrayList<C_RodentCaged> parents = new ArrayList<C_RodentCaged>();
					for (I_SituatedThing rodent : occupants2) {

						// Rodents are not killed if they are pure and born at the beginning of the simulation (i.e
						// first line, BirthDate = -1) OR if they are hybrid and older than newborn(age = 0).
						if ((((((C_RodentCaged) rodent).getGenome()).getClass() != C_GenomeMastomys.class) && (((C_RodentCaged) rodent)
								.getBirthDate_Utick() == -1)) || ((((C_RodentCaged) rodent).getAge_Uday() > 0)) && ((((C_RodentCaged) rodent)
										.getGenome()).getClass() == C_GenomeMastomys.class)) {
							parents.add((C_RodentCaged) rodent);
						}
					}
					if (occupants2.size() > 2) {
						occupants2.removeAll(parents);
						for (Iterator<C_Rodent> iterator = occupants2.iterator(); iterator.hasNext();)
							iterator.next().checkDeath(1);
					}
				}
			}
		}
		super.step_Utick();// has to be after the other inspectors step since it records indicators in file
	}
	private boolean sameSex(C_RodentCaged one, C_RodentCaged two) {
		if (one.testMale() && two.testMale()) return true;
		else if (one.testFemale() && two.testFemale()) return true;
		else if (one.testFemale() && two.testMale()) return false;
		if (one.testMale() && two.testFemale()) return false;
		else {
			System.err.println("C_ProtocolCage.compareSex(): unable to compute");
			return false;
		}
	}
	private void changeCage(C_RodentCaged rodent, int line, int col, String generation) {
		int h = (int) (C_ContextCreator.randomGeneratorForInitialisation.nextDouble() * (cagesMatrix[line][col]).getCells().size());
		rodent.generation = generation;
		C_SoilCell destinationCell = (cagesMatrix[line][col]).retrieveOneCell(h);
		this.landscape.moveToContainer(rodent, destinationCell);
		this.landscape.moveToLocation(rodent, landscape.getThingCoord_Ucs(destinationCell));
		rodent.setCurrentCage(cagesMatrix[line][col]);
		rodent.setCurrentLine(line);
	}
	public C_LandPlot[][] getCagesMatrix() {
		return cagesMatrix;
	}
	public int getNB_CAGES_COLUMNS() {
		return NB_CAGES_COLUMNS;
	}
	public int getNB_CAGES_LINES() {
		return NB_CAGES_LINES;
	}
	@Override
	public void readUserParameters() {
		super.readUserParameters();
		// DYNAMIC AGENTS CONSTANTS //
		/** used only for the computation of the mortality table
		 * @see C_Rodent#getDeathProbabilityMicrotusArvalis_Uday */
		// Reproduction attributes common to both sexes //
		C_Parameters.REPRO_START_Umonth = ((Integer) C_Parameters.parameters.getValue("REPRO_START_Umonth")).intValue(); // 91;
		C_Parameters.REPRO_END_Umonth = ((Integer) C_Parameters.parameters.getValue("REPRO_END_Umonth")).intValue();
	}
}
