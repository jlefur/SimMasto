package presentation.display;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.jfree.chart.plot.XYPlot;

import presentation.epiphyte.C_InspectorBorreliaCrocidurae;
import presentation.epiphyte.C_InspectorCMR;
import presentation.epiphyte.C_InspectorEnergy;
import presentation.epiphyte.C_InspectorFossorialRodents;
import presentation.epiphyte.C_InspectorGenetic;
import presentation.epiphyte.C_InspectorHybrid;
import presentation.epiphyte.C_InspectorOrnithodorosSonrai;
import presentation.epiphyte.C_InspectorPopulation;
import presentation.epiphyte.C_InspectorTransportation;
import presentation.epiphyte.C_InspectorVegetation;
import repast.simphony.context.Context;
import repast.simphony.engine.controller.NullAbstractControllerAction;
import repast.simphony.engine.environment.GUIRegistryType;
import repast.simphony.engine.environment.RunEnvironmentBuilder;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.IAction;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.essentials.RepastEssentials;
import repast.simphony.parameter.Parameters;
import repast.simphony.scenario.ModelInitializer;
import repast.simphony.scenario.Scenario;
import simmasto0.protocol.A_Protocol;

/** Initialise la simulation avec des onglets "programmables"
 * @author A. Realini, rev. J. Le Fur 02.2013, O2.2017, 02.2021 */
public class C_CustomPanelSet implements IAction, ModelInitializer {

	private Map<String, Double> energyMap = null;// used for energy graph
	public static ArrayList<String> energyCurves = new ArrayList<String>();// used for energy graph (must be static to be invoked
																			// and cleared in
	private IAction action = this; // Récupère l'implémentation de IDisplay
	private C_CustomPanelFactory curveEnergy, curvePopSize, curveFIS, curveDispersal, curveRates, citiesBars,
			curveVegetation, curveOrnithodoros, curvedesease;
	// the following are meant to avoid multiple calls to ContextCreator (see execute()) // JLF 02.2013
	private C_InspectorPopulation inspector;
	private static C_InspectorEnergy inspectorEnergy = null;
	private static C_InspectorHybrid hybridInspector = null;
	private static C_InspectorTransportation transportationInspector = null;
	private static C_InspectorGenetic geneticInspector = null;
	private static C_InspectorFossorialRodents burrowInspector = null;
	private static C_InspectorCMR C_InspectorCMR = null;// Ajout Malick
	private static C_InspectorVegetation vegetationInspector = null;
	private static C_InspectorOrnithodorosSonrai ornithodorosInspector = null;
	/** This is ran after the model has been loaded. This is only ran once, but the settings set through the
	 * {@link repast.simphony.scenario.Scenario} will apply to every run of the simulation.
	 * @param scen the {@link repast.simphony.scenario.Scenario} object that hold settings for the run */
	public void initialize(Scenario scen, RunEnvironmentBuilder builder) {
		scen.addMasterControllerAction(new NullAbstractControllerAction<Object>() {
			/** Ajoute des onglets à la simulation et les initialise */
			public void runInitialize(RunState runState, Context<?> context, Parameters runParams) {
				/*
				 * Initialisation des onglets: On crée un nouveau display puis on l'ajoute au registre des GUI de RunState. On
				 * recommence ces deux étapes autant de fois que l'on souhaite ajouter d'onglets.
				 */
				XYPlot plot;

				// ENERGY JLF 02.2021
				if (inspectorEnergy != null) {
					energyMap = inspectorEnergy.getEnergyBySpecies();
					curveEnergy = new C_CustomPanelFactory("Energy (mean)", C_Chart.LINE, "Tick",
							"mean energy (pseudo-kcal)");
					runState.getGUIRegistry().addDisplay("Energy", GUIRegistryType.OTHER, curveEnergy);
					plot = curveEnergy.getChart().getChartPanel().getChart().getXYPlot();
					// for (int i = 0; i < energyMap.size(); i++)
					// plot.getRenderer().setSeriesPaint(i, Color.PINK);
					plot.getRenderer().setSeriesStroke(3, new BasicStroke(.5f, BasicStroke.CAP_ROUND,
							BasicStroke.JOIN_ROUND, 1.0f, new float[]{10.0f, 6.0f}, 0.0f));
				}

				// POPULATION SIZES
				curvePopSize = new C_CustomPanelFactory("Populations sizes", C_Chart.LINE, "Ticks", "Population size");
				runState.getGUIRegistry().addDisplay("Rodent Pop. sizes", GUIRegistryType.OTHER, curvePopSize);
				curvePopSize.getChart().addXYSerie("PopMales");
				curvePopSize.getChart().addXYSerie("PopFemales");
				curvePopSize.getChart().addXYSerie("WanderingRodents");
				if (C_InspectorCMR != null) {
					curvePopSize.getChart().addXYSerie("MNA");
					curvePopSize.getChart().addXYSerie("TrapSystem");
				}
				if (hybridInspector != null) {
					curvePopSize.getChart().addXYSerie("M.ery");
					curvePopSize.getChart().addXYSerie("M.nat");
					curvePopSize.getChart().addXYSerie("M.laz");
					curvePopSize.getChart().addXYSerie("Hybrids");
				}
				curvePopSize.getChart().addXYSerie("LoadedRodents(x10)");
				plot = curvePopSize.getChart().getChartPanel().getChart().getXYPlot();
				plot.getRenderer().setSeriesPaint(0, Color.BLUE);
				plot.getRenderer().setSeriesPaint(1, Color.RED);
				plot.getRenderer().setSeriesPaint(2, Color.GREEN);
				plot.getRenderer().setSeriesPaint(3, Color.BLACK);
				plot.getRenderer().setSeriesPaint(4, Color.MAGENTA);
				plot.getRenderer().setSeriesPaint(5, Color.CYAN);
				plot.getRenderer().setSeriesPaint(6, Color.ORANGE);
				plot.getRenderer().setSeriesPaint(7, Color.GREEN);
				plot.getRenderer().setSeriesPaint(8, Color.darkGray);
				plot.getRenderer().setSeriesPaint(9, Color.LIGHT_GRAY);

				// RATES
				curveRates = new C_CustomPanelFactory("Rates", C_Chart.LINE, "Ticks", "rate (%)");
				runState.getGUIRegistry().addDisplay("Rates", GUIRegistryType.OTHER, curveRates);
				curveRates.getChart().addXYSerie("Death");
				curveRates.getChart().addXYSerie("Birth");
				curveRates.getChart().addXYSerie("WanderingRodents");

				// DISPERSAL
				curveDispersal = new C_CustomPanelFactory("dispersals (home range size)", C_Chart.LINE, "Ticks",
						"Dispersal (m)");
				runState.getGUIRegistry().addDisplay("dispersals (home range size)", GUIRegistryType.OTHER,
						curveDispersal);
				curveDispersal.getChart().addXYSerie("MaxFemaleDispersal");
				curveDispersal.getChart().addXYSerie("MaxMaleDispersal");
				curveDispersal.getChart().addXYSerie("MeanFemaleDispersal");
				curveDispersal.getChart().addXYSerie("MeanMaleDispersal");
				curveDispersal.getChart().addXYSerie("DRS");
				curveDispersal.getChart().addXYSerie("DMR");
				plot = curveDispersal.getChart().getChartPanel().getChart().getXYPlot();
				plot.getRenderer().setSeriesPaint(0, Color.PINK);
				plot.getRenderer().setSeriesPaint(1, Color.CYAN);
				plot.getRenderer().setSeriesPaint(2, Color.MAGENTA);
				plot.getRenderer().setSeriesPaint(3, Color.BLUE);
				plot.getRenderer().setSeriesPaint(4, Color.DARK_GRAY);
				plot.getRenderer().setSeriesPaint(5, Color.GREEN);

				// FIS
				if (geneticInspector != null) {
					curveFIS = new C_CustomPanelFactory("Fixation index (FIS)", C_Chart.LINE, "Ticks",
							"Fixation Index (FIS)");
					runState.getGUIRegistry().addDisplay("FIS", GUIRegistryType.OTHER, curveFIS);
					curveFIS.getChart().addXYSerie("Fixation Index");
				}

				// TRANSPORTATION
				if (transportationInspector != null) {
					// citiesBars = new C_CustomPanelFactory("Carriers & rodents in cities", C_Chart.BAR, "city", "N
					// carriers/rodents AGENTS");
					// runState.getGUIRegistry().addDisplay("Transportation", GUIRegistryType.OTHER, citiesBars);
				}

				// VEGETATION
				if (vegetationInspector != null) {
					curveVegetation = new C_CustomPanelFactory("Total energy (Vegetation)", C_Chart.LINE, "Tick",
							"mean energy (pseudo-kcal)");
					runState.getGUIRegistry().addDisplay("Vegetation and rain", GUIRegistryType.OTHER, curveVegetation);
					curveVegetation.getChart().addXYSerie("ShrubEnergy");
					curveVegetation.getChart().addXYSerie("CropEnergy");
					curveVegetation.getChart().addXYSerie("GrassEnergy");
					curveVegetation.getChart().addXYSerie("TotalRainFall");
					plot = curveVegetation.getChart().getChartPanel().getChart().getXYPlot();
					plot.getRenderer().setSeriesPaint(0, Color.PINK);
					plot.getRenderer().setSeriesPaint(1, Color.GREEN);
					plot.getRenderer().setSeriesPaint(2, Color.BLUE);
					plot.getRenderer().setSeriesPaint(3, Color.BLACK);
					plot.getRenderer().setSeriesStroke(3, new BasicStroke(.5f, BasicStroke.CAP_ROUND,
							BasicStroke.JOIN_ROUND, 1.0f, new float[]{10.0f, 6.0f}, 0.0f));
				}

				// ORNITHODOROS
				if (ornithodorosInspector != null) {
					curveOrnithodoros = new C_CustomPanelFactory("Tick action curve", C_Chart.LINE, "Tick", "Actions");
					runState.getGUIRegistry().addDisplay("Ticks", GUIRegistryType.OTHER, curveOrnithodoros);
					curveOrnithodoros.getChart().addXYSerie("Births");
					// curveOrnithodoros.getChart().addXYSerie("Hibernation Average");
					curveOrnithodoros.getChart().addXYSerie("Bites");
					curveOrnithodoros.getChart().addXYSerie("Infected");
					// curveOrnithodoros.getChart().addXYSerie("Population");
					// curveOrnithodoros.getChart().addXYSerie("Male");
					// curveOrnithodoros.getChart().addXYSerie("Female");
					plot = curveOrnithodoros.getChart().getChartPanel().getChart().getXYPlot();
					plot.getRenderer().setSeriesPaint(0, Color.BLUE);
					plot.getRenderer().setSeriesPaint(1, Color.DARK_GRAY);
					plot.getRenderer().setSeriesPaint(2, Color.PINK);
					plot.getRenderer().setSeriesPaint(3, Color.GREEN);
					plot.getRenderer().setSeriesPaint(4, Color.YELLOW);
					plot.getRenderer().setSeriesPaint(5, Color.ORANGE);

					// DESEASE CURVE
					curvedesease = new C_CustomPanelFactory("Desease curve", C_Chart.LINE, "Tick", "Infected size");
					runState.getGUIRegistry().addDisplay("Infected size", GUIRegistryType.OTHER, curvedesease);
					curvedesease.getChart().addXYSerie("Infected Ticks");
					curvedesease.getChart().addXYSerie("Healthy Ticks");
					curvedesease.getChart().addXYSerie("Infected Rodent");
					curvedesease.getChart().addXYSerie("Healthy Rodent");
					plot = curvedesease.getChart().getChartPanel().getChart().getXYPlot();
					plot.getRenderer().setSeriesPaint(0, Color.PINK);
					plot.getRenderer().setSeriesPaint(1, Color.GREEN);
					plot.getRenderer().setSeriesPaint(2, Color.red);
					plot.getRenderer().setSeriesPaint(3, Color.BLUE);
				}

				/** Ajoute SimMastoInitializer au registre des plannings pour que la fonction execute() soit appelée à
				 * l'intervalle demandé. La dernière variable ne peut pas être this à cause de la double implémentation. C'est
				 * pourquoi on utilise une variable action initialisée avec this<br>
				 * The action with greater value is activated first */
				runState.getScheduleRegistry().getModelSchedule().schedule(ScheduleParameters.createRepeating(0, 1, 1),
						action);
			}

			/** Ferme les flux de la console du user panel (exécuté lorsque l'on réinitialise une simulation sans couper
			 * l'exécution du programme) */
			public void runCleanup(RunState runState, Context<?> context) {
				C_CustomPanelSet.energyCurves.clear();
				// La console n'existe pas en batch car C_TableauDeBord n'est pas initialisé
				if (C_UserPanel.consoleOut != null) {
					System.out.println("SimMastoInitializer.runCleanup : closing flow to dismiss display");
					try {
						C_UserPanel.consoleOut.getIn().close();
						C_UserPanel.consoleErr.getIn().close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					C_UserPanel.consoleOut.getOut().close();
					C_UserPanel.consoleErr.getOut().close();
				}
			}

			public String toString() {
				return "SimMastoInitializer";
			}
		});
	}

	/** Use it for no graphs in the GUI */
	public void execute0() {}

	/** Update each series with the corresponding data */
	public void execute() {
		inspector = A_Protocol.inspector;
		inspectorEnergy = A_Protocol.inspectorEnergy;

		// POPULATION DISPLAY
		curvePopSize.getChart().setData("PopMales", RepastEssentials.GetTickCount(), C_InspectorPopulation
				.getNbMales());
		curvePopSize.getChart().setData("PopFemales", RepastEssentials.GetTickCount(), C_InspectorPopulation
				.getNbFemales());
		// BURROWS
		/*
		 * if (burrowInspector != null) curvePopSize.getChart().setData("WanderingRodents", RepastEssentials.GetTickCount(),
		 * burrowInspector .getWanderingRodents_Upercent() * C_InspectorPopulation.rodentList.size());
		 */
		// TRANSPORTATION
		if (transportationInspector != null)
			curvePopSize.getChart().setData("LoadedRodents(x10)", RepastEssentials.GetTickCount(),
					transportationInspector.getCarriersLoad_Urodent() * 10);
		// HYBRIDIZATION IN POPULATION DISPLAY
		if (hybridInspector != null) {
			curvePopSize.getChart().setData("Hybrids", RepastEssentials.GetTickCount(), hybridInspector.getNbHybrids());
			curvePopSize.getChart().setData("M.ery", RepastEssentials.GetTickCount(), hybridInspector.getNbEry());
			curvePopSize.getChart().setData("M.laz", RepastEssentials.GetTickCount(), hybridInspector.getNbLazarus());
			curvePopSize.getChart().setData("M.nat", RepastEssentials.GetTickCount(), hybridInspector.getNbNat());
		}
		// CMR DISPLAY
		if (C_InspectorCMR != null) {
			curvePopSize.getChart().setData("MNA", RepastEssentials.GetTickCount(), C_InspectorCMR.getCurrentMNA());
			curvePopSize.getChart().setData("TrapSystem", RepastEssentials.GetTickCount(), C_InspectorCMR
					.getTrapAreaPopulation_Urodent());
			curveDispersal.getChart().setData("DRS", RepastEssentials.GetTickCount(), C_InspectorCMR.getCurrentDRS());
			curveDispersal.getChart().setData("DMR", RepastEssentials.GetTickCount(), C_InspectorCMR.getCurrentDMR());
		}
		// DISPERSAL DISPLAY
		curveDispersal.getChart().setData("MaxFemaleDispersal", RepastEssentials.GetTickCount(), inspector
				.getMaxFemaleDispersal());
		curveDispersal.getChart().setData("MaxMaleDispersal", RepastEssentials.GetTickCount(), inspector
				.getMaxMaleDispersal());
		curveDispersal.getChart().setData("MeanMaleDispersal", RepastEssentials.GetTickCount(), inspector
				.getMeanMaleDispersal());
		curveDispersal.getChart().setData("MeanFemaleDispersal", RepastEssentials.GetTickCount(), inspector
				.getMeanFemaleDispersal());
		// FIS DISPLAY
		if (geneticInspector != null)
			curveFIS.getChart().setData("Fixation Index", RepastEssentials.GetTickCount(), geneticInspector
					.getFixationIndex());
		// RATES DISPLAY
		curveRates.getChart().setData("Birth", RepastEssentials.GetTickCount(), ((double) inspector.getBirthRatio()
				* 100));
		curveRates.getChart().setData("Death", RepastEssentials.GetTickCount(), ((double) inspector.getDeathRatio()
				* 100));
//		if (burrowInspector != null)
//			curveRates.getChart().setData("WanderingRodents", RepastEssentials.GetTickCount(), burrowInspector
//					.getWanderingRodents_Upercent() * 100);
		// CITIES BARS DISPLAY (RODENTS & CARRIERS BY CITY)
		if (transportationInspector != null) {/*
												 * for (C_City oneCity : transportationInspector.getCityList()) {
												 * citiesBars.getChart().setBarData(oneCity.getFullRodentList().size(),
												 * "rodent pop.", oneCity.toString()); // TODO number in source 2015.10 JLF HC
												 * graph multiplier
												 * citiesBars.getChart().setBarData(oneCity.getFullLoad_Ucarrier() * 10,
												 * "carrier pop.", oneCity.toString()); } citiesBars.getChart().setTitle(
												 * transportationInspector.getCarrierList().size() *
												 * C_Parameters.HUMAN_SUPER_AGENT_SIZE + " carriers total; " +
												 * C_InspectorPopulation.getNbRodents() * C_Parameters.RODENT_SUPER_AGENT_SIZE +
												 * "000 rodents total");
												 */}
		// VEGETATION DISPLAY
		if (vegetationInspector != null) {
			int nbVegetationAgents = vegetationInspector.getVegetationList().size();
			nbVegetationAgents = 1;// to compute sum instead of mean, may be commented if needed
			curveVegetation.getChart().setData("ShrubEnergy", RepastEssentials.GetTickCount(), vegetationInspector
					.getShrubEnergy() / nbVegetationAgents);
			curveVegetation.getChart().setData("CropEnergy", RepastEssentials.GetTickCount(), vegetationInspector
					.getCropEnergy() / nbVegetationAgents);
			curveVegetation.getChart().setData("GrassEnergy", RepastEssentials.GetTickCount(), vegetationInspector
					.getGrassEnergy() / nbVegetationAgents);
			// TODO number in source 2016.07 JLF rainFall multiplier (arbitrary)
			curveVegetation.getChart().setData("TotalRainFall", RepastEssentials.GetTickCount(), vegetationInspector
					.getTotalPrecipitation_Umm());
		}
		// ENERGY jlf 02.2021
		if (C_CustomPanelSet.inspectorEnergy != null) {
			this.energyMap = C_CustomPanelSet.inspectorEnergy.getEnergyBySpecies();
			// Provide an energy curve for each type or organism
			for (String key : this.energyMap.keySet()) {
				// if species not yet registered, create the XYSerie
				if (!C_CustomPanelSet.energyCurves.contains(key)) {
					this.curveEnergy.getChart().addXYSerie(key);
					C_CustomPanelSet.energyCurves.add(key);
				}
				// if (!key.equals("C_GenomeGerbillusNigeriae"))
				curveEnergy.getChart().setData(key, RepastEssentials.GetTickCount(), this.energyMap.get(key));
			}
		}
		// ORNITHODOROS DISPLAY
		if (ornithodorosInspector != null) {
			curveOrnithodoros.getChart().setData("Births", RepastEssentials.GetTickCount(), ornithodorosInspector
					.getBirthNumber());
			curveOrnithodoros.getChart().setData("Bites", RepastEssentials.GetTickCount(), ornithodorosInspector
					.getBiteNumber());
			curvedesease.getChart().setData("Infected Ticks", RepastEssentials.GetTickCount(), ornithodorosInspector
					.getInfectedNumber());
			curvedesease.getChart().setData("Healthy Ticks", RepastEssentials.GetTickCount(), ornithodorosInspector
					.getHealthyNumber());
			// curveOrnithodoros.getChart().setData("Hibernation Average", RepastEssentials.GetTickCount(),
			// ornithodorosInspector.getHibernationAverage());
			// curveOrnithodoros.getChart().setData("Population", RepastEssentials.GetTickCount(),
			// ornithodorosInspector.getTicksNumber());
			// curveOrnithodoros.getChart().setData("Male", RepastEssentials.GetTickCount(), ornithodorosInspector.getTickMale());
			// curveOrnithodoros.getChart().setData("Female", RepastEssentials.GetTickCount(),
			// ornithodorosInspector.getTickFemale());
			// INFECTED RODENT DISPLAY
			curvedesease.getChart().setData("Infected Rodent", RepastEssentials.GetTickCount(), inspector
					.getInfectedRodents());
			curvedesease.getChart().setData("Healthy Rodent", RepastEssentials.GetTickCount(), inspector
					.getHealthyRodent());
		}
	}
	public static void addHybridInspector(C_InspectorHybrid inspector) {
		hybridInspector = inspector;
	}
	public static void addEnergyInspector(C_InspectorEnergy inspector) {
		inspectorEnergy = inspector;
	}
	public static void addGeneticInspector(C_InspectorGenetic inspector) {
		geneticInspector = inspector;
	}
	public static void addTransportationInspector(C_InspectorTransportation inspector) {
		transportationInspector = inspector;
	}
	public static void addBurrowInspector(C_InspectorFossorialRodents inspector) {
		burrowInspector = inspector;
	}
	public static void addCMRInspector(C_InspectorCMR inspector) {
		C_InspectorCMR = inspector;
	}
	public static void addVegetationInspector(C_InspectorVegetation inspector) {
		vegetationInspector = inspector;
	}
	public static void addOrnithodorosInspector(C_InspectorOrnithodorosSonrai inspector) {
		ornithodorosInspector = inspector;
	}
	public static void addBorreliaInspector(C_InspectorBorreliaCrocidurae inspector) {}
}
