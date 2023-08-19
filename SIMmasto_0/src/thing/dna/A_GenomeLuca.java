/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing.dna;

import simmasto0.util.C_VariousUtilities;
import thing.dna.phenotyper.C_TraitExpressorAvgMapLoc;
import thing.dna.phenotyper.I_MapLocTraitExpressor;
import thing.dna.variator.C_GeneMutatorDouble;
import thing.dna.variator.C_RecombinatorMapGenome;
import thing.dna.variator.I_GeneMutator;
import thing.dna.variator.I_Recombinator;

/** Luca: the Last Universal Common Ancestor Genome mother class of the phylogenetic (C_Genomexxx) tree
 * @author jlefur / lefur@ird.fr
 * Version 02.2011/09.2011/01.2012/02.2012/06.2014 */
public abstract class A_GenomeLuca {
	//
	// FIELDS
	//
	// the following three are always the same, need not to be unique for each gene
	protected I_MapLocTraitExpressor expressor = new C_TraitExpressorAvgMapLoc();// used to express all traits requests
	protected I_GeneMutator mutator = new C_GeneMutatorDouble();
	protected I_Recombinator recombinator = new C_RecombinatorMapGenome();
	//
	// METHOD
	//
	@Override
	public String toString() {
		return (C_VariousUtilities.getShortClassName(getClass()).substring(8));
	}
	//
	// GETTER
	//
	/** @return number of chromosome pairs = 0, has to be specified in daughter classes */
	public int getDiploidNumber() {
		return 0;// TODO number in source OK JLF
	}
}
