package simmasto0;

import cern.jet.random.engine.RandomEngine;
import data.C_Parameters;
import data.constants.I_ConstantNumeric;
import data.constants.I_ConstantString;
import data.converters.C_ConvertTimeAndSpace;
import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.random.RandomHelper;
import simmasto0.protocol.A_Protocol;
import simmasto0.protocol.C_ProtocolBandia;
import simmasto0.protocol.C_ProtocolCage;
import simmasto0.protocol.C_ProtocolCentenal;
import simmasto0.protocol.C_ProtocolChize;
import simmasto0.protocol.C_ProtocolDecenal;
import simmasto0.protocol.C_ProtocolDodel;
import simmasto0.protocol.C_ProtocolDodel2;
import simmasto0.protocol.C_ProtocolEnclosure;
import simmasto0.protocol.C_ProtocolGerbil;
import simmasto0.protocol.C_ProtocolHybridUniform;
import simmasto0.protocol.C_ProtocolMusTransport;
import simmasto0.util.C_RandomInNormalDistrib;

/** @author Baduel Quentin - 2009 multiple rev. Le Fur 2009-2012, 02.2013, 11.2015 */
public class C_ContextCreator implements ContextBuilder<Object>, I_ConstantNumeric, I_ConstantString {
	//
	// FIELDS
	//
	public static A_Protocol protocol = null;
	public static C_RandomInNormalDistrib randomGaussianGenerator = null;// TODO JLF 2015.10 comment each generator
	public static RandomEngine randomGeneratorForInitialisation = null;
	public static RandomEngine randomGeneratorForDestination = null;
	public static RandomEngine randomGeneratorForDeathProb = null;
	public static RandomEngine randomGeneratorForDNA = null;
	public static RandomEngine randomGeneratorForMovement = null;
	public static RandomEngine randomGeneratorForDisplay = null;
	public static RandomEngine randomGeneratorForCulturalPractice = null;
	public static RandomEngine randomGeneratorForGameteAndSexSelection = null;
	public static RandomEngine randomGeneratorForEpistasis = null;
	public static RandomEngine randomGeneratorForOlfactionRecognition = null;
	public static RandomEngine randomGeneratorForBoarding = null;
	// Incremental parameters for the corresponding number of objects :
	public static int AGENT_NUMBER = 0;
	public static int XSOME_NUMBER = 0;
	public static int INSPECTOR_NUMBER = 0;
	public static int EVENT_NUMBER = 0;// used only to provide unique identifiers to events / JLF 08.2014
	public static long simulationStartTime_Ums;
	//
	// METHODS
	//
	/** Build and return the context: protocol, ground manager, random generators, calendar, time and space converter.
	 * @return the built context. */
	public Context<Object> build(Context<Object> context) {
		AGENT_NUMBER = 0;
		XSOME_NUMBER = 0;
		INSPECTOR_NUMBER = 0;
		simulationStartTime_Ums = System.currentTimeMillis();
		System.out.println("C_ContextCreator.build(): building the context");
		new C_Parameters();// C_Parameters fields are static, hence it is not necessary to put them in a field PAM
		System.out.println("C_ContextCreator.build(): Parameters are declared");

		// REDIRECT CONSOLE TO TEXT FILE - switch also console within C_UserPanel
		// new C_OutputConsole();
		// System.out.println("C_ContextCreator.build(): console is backed up in " + CONSOLE_OUTPUT_FILE);

		System.out.println("C_ContextCreator.build(): " + initializeRandomGenerators() + " random number generators initialized");
		C_ConvertTimeAndSpace.init(C_Parameters.TICK_LENGTH_Ucalendar, C_Parameters.TICK_UNIT_Ucalendar, "M");
		System.out.println("C_ContextCreator.build(): Initialized time and space converter class with the ground manager ");
		selectProtocol(context);
		System.out.println("C_ContextCreator.build(): protocol " + C_Parameters.PROTOCOL + " selected");
		System.out.println("C_ContextCreator.build(): Building the context ended: " + context.size() + " items");
		System.out.println("------------------------------");
		System.out.println("Now running at " + C_Parameters.TICK_LENGTH_Ucalendar + C_Parameters.TICK_UNIT_Ucalendar + ". per tick, ...");
		return context;
	}

	/** Several random generators are used in the model. Each is controlled by a seed and provides variability for specific features or processes in
	 * the model. <br>
	 * Each random generator is controlled by a specific seed that can be fixed or changed. When fixed, the random generator always provides the same
	 * pseudorandom series of numbers. <br>
	 * This makes it possible to carry out simulations in which only selected sources of variation change. author: J. Le Fur 2012, rev. 08.2014
	 * @return nbGen the number of random generators initialized at that time */
	private int initializeRandomGenerators() {
		int nbGen = 0;
		randomGaussianGenerator = new C_RandomInNormalDistrib(GAUSSIAN_RANDOM_SEED);
		nbGen++;
		randomGeneratorForInitialisation = RandomHelper.registerGenerator("", INITIALISATION_RANDOM_SEED);
		nbGen++;
		randomGeneratorForDestination = RandomHelper.registerGenerator("", DESTINATION_RANDOM_SEED);
		nbGen++;
		randomGeneratorForBoarding = RandomHelper.registerGenerator("", BOARDING_RANDOM_SEED);
		nbGen++;
		randomGeneratorForDeathProb = RandomHelper.registerGenerator("", DEATH_PROB_RANDOM_SEED);
		nbGen++;
		randomGeneratorForDNA = RandomHelper.registerGenerator("", DNA_RANDOM_SEED);
		nbGen++;
		randomGeneratorForMovement = RandomHelper.registerGenerator("", MOVEMENT_RANDOM_SEED);
		nbGen++;
		randomGeneratorForCulturalPractice = RandomHelper.registerGenerator("", CULTURE_RANDOM_SEED);
		nbGen++;
		randomGeneratorForGameteAndSexSelection = RandomHelper.registerGenerator("", GAMETE_RANDOM_SEED);
		nbGen++;
		randomGeneratorForEpistasis = RandomHelper.registerGenerator("", EPISTASIS_RANDOM_SEED);
		nbGen++;
		randomGeneratorForOlfactionRecognition = RandomHelper.registerGenerator("", OLFACTION_RECOGNITION_RANDOM_SEED);
		nbGen++;
		return (nbGen);
	}
	/** Initialization of the protocol declared in sim_constants author: J.LeFur 07.2012 */
	private void selectProtocol(Context<Object> context) {
		if (C_Parameters.PROTOCOL.contains(CHIZE)) protocol = new C_ProtocolChize(context);
		else if (C_Parameters.PROTOCOL.equals(HYBRID_UNIFORM)) protocol = new C_ProtocolHybridUniform(context);
		else if (C_Parameters.PROTOCOL.equals(CAGES)) protocol = new C_ProtocolCage(context);
		else if (C_Parameters.PROTOCOL.equals(ENCLOSURE)) protocol = new C_ProtocolEnclosure(context);
		else if (C_Parameters.PROTOCOL.equals(MUS_TRANSPORT)) protocol = new C_ProtocolMusTransport(context);
		else if (C_Parameters.PROTOCOL.equals(CENTENAL)) protocol = new C_ProtocolCentenal(context);
		else if (C_Parameters.PROTOCOL.equals(DECENAL)) protocol = new C_ProtocolDecenal(context);
		else if (C_Parameters.PROTOCOL.equals(BANDIA)) protocol = new C_ProtocolBandia(context);
		else if (C_Parameters.PROTOCOL.equals(GERBIL_PROTOCOL)) protocol = new C_ProtocolGerbil(context);
		else if (C_Parameters.PROTOCOL.equals(DODEL)) protocol = new C_ProtocolDodel(context);
		else if (C_Parameters.PROTOCOL.equals(DODEL2)) protocol = new C_ProtocolDodel2(context);
		else {
			System.err.println("C_ContextCreator.selectProtocol() NO PROTOCOL DEFINED");
			System.exit(0);
		}
		context.add(protocol);
		protocol.initProtocol();
	}
}