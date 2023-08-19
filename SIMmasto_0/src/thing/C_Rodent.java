/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing;

import java.util.TreeSet;
import simmasto0.C_ContextCreator;
import simmasto0.protocol.A_Protocol;
import thing.dna.C_GenomeAnimalia;
import thing.dna.C_GenomeEucaryote;
import thing.dna.I_DiploidGenome;
import data.C_Parameters;

/** @author JEL & AR, rev. J.Le Fur 2012-2013-2014 */
public class C_Rodent extends A_Amniote {
	//
	// CONSTRUCTOR
	//
	public C_Rodent(I_DiploidGenome genome) {
		super(genome);
	}
	//
	// METHODS
	//
	/** select the kind of animal to interact to, then triggers the corresponding interact procedure
	 * @return true if interaction occurred
	 * @see interact#C_Rodent
	 * @see interact#C_HumanCarrier author JLF, rev. 02.2014 */
	@Override
	protected boolean actionInteract(A_Animal animal) {
		if (animal instanceof C_Rodent) return actionInteract((C_Rodent) animal);
		else if (animal instanceof C_HumanCarrier) return actionInteract((C_HumanCarrier) animal);
		else {
			A_Protocol.event("C_Rodent.actionInteract", animal + ": not a rodent nor a Hcarrier", isError);
			return false;
		}
	}
	/** Interact with another rodent. systematically mates if both partner reproductive status are ok. <br>
	 * it is always the male which interact (mateWithMale) with the female
	 * @see A_Amniote#mateWithMale COMMENT THE recognized(rodent) CONDITION TO STOP THE PRE-ZYGOTIC AND SCARCITY BARRIERS */
	protected boolean actionInteract(C_Rodent rodent) {
		if (this.readyToMate && rodent.readyToMate && A_Protocol.isBreedingSeason() && recognized(rodent)) {
			if (this.testMale() && rodent.testFemale()) return rodent.actionMateWithMale(this);
			else if (rodent.testMale() && this.testFemale()) return this.actionMateWithMale(rodent);
			else return false;
		}
		else return false;
	}

	/** simple interaction with a human carrier (no survival chance for any rodent)- extended in RodentCommensal JLF feb.2014 */
	protected boolean actionInteract(C_HumanCarrier carrier) {
		this.checkDeath(1.);// number in source OK iff =1. JLF 02.2014
		return true;
	}

	/** Specific procedure for hybrids olfaction recognition<br>
	 * Author J.LeFur and A. Comte, 05.2012, rev.JLF 07.2014 <br>
	 * TODO JLF 2014.07 should be more general: Animal.recognized on any genetic signature
	 * @return true if the two rodents olfaction signature are sufficiently close to mate
	 * @see C_GenomeEucaryote#hybridRatio() */
	protected boolean recognized(C_Rodent rodentPartner) {// TODO JLF 2014.07 clean up for normal function
		double thisSignature = ((C_GenomeEucaryote) this.genome).hybridRatio();
		double partnerSignature = ((C_GenomeEucaryote) rodentPartner.genome).hybridRatio();
		// probaMate = genetic similarity of the two "hybrids" if 0 they are far, if 1 they are similar
		double geneticDistance = java.lang.Math.abs(thisSignature - partnerSignature);
		// an alert in case... :-)
		/*
		 * if (geneticSimilarity > 0.0) { System.err.println(":-) C_Rodent.recognized(), genetic similarity > 0.0: " +
		 * geneticSimilarity + "/ tick: " + RepastEssentials.GetTickCount()); C_sound.sound("tip.wav"); }
		 */
		double probaMate = ((geneticDistance * 1) + evalScarcity()) / 2;// TODO number in source 2018.07 reduces mating
																		// probability
		// returns true (ok for mate) if probaMate is low !
		return C_ContextCreator.randomGeneratorForOlfactionRecognition.nextDouble() >= probaMate;
	}

	/** Scarcity is evaluated from within the perception sphere of the rodent. <br>
	 * @return A value between ~0 (scarcity: no conspecific rodents) and 1 (no scarcity: this species only)<br>
	 * @version J.LeFur and A. Comte, 05.2012, rev. jlf 04.2015 */
	protected double evalScarcity() {
		int nbRodent = 0, nbPartners = 0;
		TreeSet<I_SituatedThing> accointanceList = this.perception();
		for (I_SituatedThing accointance : accointanceList) {
			if (accointance instanceof C_Rodent) {
				nbRodent++;
				if (((C_Rodent) accointance).getGenome().getClass() == this.genome.getClass()) nbPartners++;
			}
		}
		return (double) nbPartners / (double) nbRodent;
		/*
		 * // Procedure for hybridation enclosure: return a value between 0 and 1 where 0 means equal number of both species and 1
		 * // scarcity f any of the species. double rateConspecific = (double) nbPartners / (double) nbRodent; double scarcity =
		 * java.lang.Math.abs((rateConspecific - .5) * 2); //
		 * System.out.println("C_Rodent.evalScarcity(): nat/total: "+nbNatalensis+"/"+nbRodent+"="+percentNat+"%, -> "+scarcity);
		 * return scarcity;
		 */
	}

	/** generate a new animal : compulsory for every A_Mammal daughter class */
	@Override
	public A_Animal giveBirth(I_DiploidGenome genome) {
		if (C_Parameters.VERBOSE)
			A_Protocol.event("C_Rodent.giveBirth", "Birth at " + this.currentSoilCell + " (" + this.currentSoilCell
					.getLoad_Urodent() + ")", false);
		return new C_Rodent(genome);
	}
	//
	// GETTERS
	//

	/** Retrieves death probability
	 * @return double value */
	@Override
	protected double computeDeathProbability_Uday() {// TODO JLF 2014.01 clean this
		return getDeathProbabilityMicrotusArvalis_Uday() / 2.;// TODO number in source JLF 2017.12 mortality tuner;

	}
	@Override
	/** JLF 03.2021 */
	public int getCarryingCapacity_Urodent() {
		return 0;
	}
	/** Retrieves death probability from a table
	 * @return double value */
	protected double getDeathProbabilityMicrotusArvalis_Uday() { // TODO JLF 2014.01 encapsulate with a call with arg=genome & age
		double deathProb = 0.;
		double[][] mortalityTable_Uday = {//
				{0, 30, 60, 90, 120, 150, 180, 210, 240, 270, ((C_GenomeAnimalia) this.genome).getMaxAge_Uday()}, //
				{0.011, 0.010, 0.012, 0.012, 0.013, 0.018, 0.022, 0.025, 0.028, 0.030, .04}};// mortality table from Spitz
																								// excepted last value
																								// extrapolated
		if (getAge_Uday() >= 0) {
			int n = mortalityTable_Uday[0].length - 1, i;
			for (i = 0; i < n; i++) {
				if (getAge_Uday() >= mortalityTable_Uday[0][i] && getAge_Uday() < mortalityTable_Uday[0][i + 1]) {
					deathProb = mortalityTable_Uday[1][i];
					break;
				}
			}
			if (getAge_Uday() >= mortalityTable_Uday[0][n] || getAge_Uday() >= ((C_GenomeAnimalia) this.genome)
					.getMaxAge_Uday()) deathProb = .01;
		}
		return deathProb;
	}
}
