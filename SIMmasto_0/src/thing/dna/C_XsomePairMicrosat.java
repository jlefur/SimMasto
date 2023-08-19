package thing.dna;
import data.constants.I_ConstantNumeric;
import simmasto0.C_ContextCreator;
import thing.dna.variator.C_GeneMutatorInteger;
import thing.dna.variator.C_RecombinatorMicrosat;
import thing.dna.variator.I_GeneMutator;

/** Class of one type of chromosome pairs to keep safe the Chromosomes class.
 * @author J-E Longueville, LeFur 09-2011 */
public class C_XsomePairMicrosat extends C_ChromosomePair implements I_ConstantNumeric {

	/** Its the constructor of the first generation.
	 * @param numLociMicrosat */
	public C_XsomePairMicrosat(int numLociMicrosat) {
		super(numLociMicrosat, new C_RecombinatorMicrosat()); // specifies a microsatellite recombinator (i.e., shuffling alleles)
		// Variable propre à l'ajout des gènes microsat un style de mutation et une contrainte de taille.
		I_GeneMutator geneMutator = new C_GeneMutatorInteger((int) (MEAN_GAUSS - STD_GAUSS), (int) (MEAN_GAUSS + STD_GAUSS));
		// Boucle de remplissage du nombre désiré de gènes.
		for (int i = 0; i < numLociMicrosat; i++) {
			double mapLoc = i * SPACE_BETWEEN_GENES;
			Integer randAllele1 = (int) C_ContextCreator.randomGaussianGenerator.nextGaussian(MEAN_GAUSS, STD_GAUSS);
			Integer randAllele2 = (int) C_ContextCreator.randomGaussianGenerator.nextGaussian(MEAN_GAUSS, STD_GAUSS);
			this.setGenePairAtLocus(i, new C_Gene(randAllele1, mapLoc, geneMutator, this.xsomeStrands[PARENT_1].myId), new C_Gene(
					randAllele2, mapLoc, geneMutator, this.xsomeStrands[PARENT_2].myId));
		}
	}
	/** Used for replication, etc. */
	protected C_XsomePairMicrosat(C_Chromosome p1Gamete, C_Chromosome p2Gamete) {
		super(p1Gamete, p2Gamete);
	}
	/** Takes this Genetic object and mixes it up with another genetic object, producing a third Genetic object. Assume that both are
	 * diploid. Neither parent is modified in the process. Offspring always gets recombinator used by the "host" parent ("this"),
	 * never parent2's recombinator.
	 * @param parent2 Another Genetic object with which to mix components (genes or other parts). Must be DiploidGenome. */
	/*
	 * public C_XsomeMicrosat crossover() { C_ChromosomePair x = recombinator.crossover(xsomeStrands[PARENT_1],
	 * xsomeStrands[PARENT_2]); C_XsomeMicrosat crossedMicrosat = new C_XsomeMicrosat(x.getXsomeStrand(PARENT_1),
	 * x.getXsomeStrand(PARENT_2)); return crossedMicrosat; }
	 */
}
