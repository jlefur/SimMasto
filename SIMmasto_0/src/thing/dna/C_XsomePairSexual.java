package thing.dna;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import data.constants.I_ConstantNumeric;
import simmasto0.C_ContextCreator;
import thing.dna.variator.C_GeneConstraint;
import thing.dna.variator.C_GeneMutatorSet;
import thing.dna.variator.C_RecombinatorNull;
import thing.dna.variator.I_GeneMutator;

/** chromosome pair in charge of sex affairs
 * @author J.LeFur (hybrid from JE.Longueville and K.Wagner versions) - jan. 2012 */
public class C_XsomePairSexual extends C_ChromosomePair implements I_ConstantNumeric {
	public static final Set<Integer> SEX_GENE_SET = new HashSet<Integer>();
	public static final C_GeneConstraint SEX_GENE_CONSTR = new C_GeneConstraint(SEX_GENE_SET, SEX_GENE_X);
	public static final I_GeneMutator SEX_GENE_MUT = new C_GeneMutatorSet(new ArrayList<Integer>(
			SEX_GENE_SET));
	public static final int DEFAULT_GENDER = SEX_GENE_X;
	public static final double DEFAULT_GONOSOME_MAPLOC = 4.3; // TODO number in source 2013.12 JLF put in I_numeric_constants
	static {
		SEX_GENE_SET.add(SEX_GENE_X);
		SEX_GENE_SET.add(SEX_GENE_Y);
	}
	/** @param gender */
	public C_XsomePairSexual(int gender) {
		super(1, new C_RecombinatorNull());
		// Assume it is female ... give it both sex alleles ("XX"). If it should be male, make one
		// of the alleles a missing sex allele, "Y" (thus, the genome codes for a male - YX or XY).
		Integer sexAlleleL = SEX_GENE_X;
		Integer sexAlleleR = SEX_GENE_X;
		if (gender == SEX_GENE_Y) {
			// Randomly choose which gene is "Y" and which will remain "X".
			if ((int) (C_ContextCreator.randomGeneratorForGameteAndSexSelection.nextDouble() * 2) == 0) sexAlleleL = SEX_GENE_Y;
			else sexAlleleR = SEX_GENE_Y;
		}
		// put a unique sexual gene pair at locus 0.
		setGenePairAtLocus(0, new C_Gene(sexAlleleL, DEFAULT_GONOSOME_MAPLOC, SEX_GENE_MUT,
				this.xsomeStrands[PARENT_1].myId, SEX_GENE_CONSTR), new C_Gene(sexAlleleR,
				DEFAULT_GONOSOME_MAPLOC, SEX_GENE_MUT, this.xsomeStrands[PARENT_2].myId, SEX_GENE_CONSTR));
	}
	/** Used for replication, etc. */
	protected C_XsomePairSexual(C_Chromosome p1Gamete, C_Chromosome p2Gamete) {
		super(p1Gamete, p2Gamete);
	}
	public boolean isMale() {
		C_GenePair alleles = (C_GenePair) getLocusAllele(DEFAULT_GONOSOME_MAPLOC);
		if (alleles.getGene(PARENT_1).allele != alleles.getGene(PARENT_2).allele) return true;
		else return false;
	}
	public boolean isFemale() {
		C_GenePair alleles = (C_GenePair) getLocusAllele(DEFAULT_GONOSOME_MAPLOC);
		if (alleles.getGene(PARENT_1).allele == alleles.getGene(PARENT_2).allele) return true;
		else return false;
	}
	/** crosses the two strands of the gonosome / 01.2012 uses a nullRecombinator, that is do not recombine */
	public C_ChromosomePair crossover() {
		// Crossover both parents' original strands separately -> two haploid strands, or gametes.
		C_ChromosomePair recombinedXsome = recombinator.crossover(xsomeStrands[PARENT_1],
				xsomeStrands[PARENT_2]);
		C_XsomePairSexual crossedGonosome = new C_XsomePairSexual(recombinedXsome.getXsomeStrand(PARENT_1),
				recombinedXsome.getXsomeStrand(PARENT_2));
		return crossedGonosome;
	}
}
