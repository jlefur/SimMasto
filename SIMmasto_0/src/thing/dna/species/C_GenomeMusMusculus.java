/*This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt*/
package thing.dna.species;

import java.util.ArrayList;

import thing.dna.C_ChromosomePair;
import thing.dna.C_GenomeAmniota;
import thing.dna.C_GenomeEucaryote;
import thing.dna.C_XsomePairMicrosat;
import thing.dna.C_XsomePairSexual;

/** @author JLF 07.2014, rev. JLF & L.Granjon 08.2014 */
public class C_GenomeMusMusculus extends C_GenomeAmniota {
	//
	// CONSTRUCTORS
	//
	/** Returns a new C_GenomeMusMusculus. <br>
	 * Trait values references (see comments in source code):<br>
	 * - Doumerc, S. (2004) Elevage et reproduction des rongeurs myomorphes domestiques en France. Veterinary Ph. D. thesis, 286p. <br>
	 * Baker, J. (2004) National Gerbil Society Website http://www.gerbils.co.uk/gerbils/genetics.html] <br>
	 * Maeda, Ki, Ohkura, S., Tsukamura, H. (2000) Physiology of Reproduction. In : Krinke, GJ, editor. The Laboratory Rat. 1 st ed. London : Academic
	 * Press, 2000, 145-174. <br>
	 * - AnAge database (2014) http://genomics.senescence.info/species/entry.php?species=Mus_musculus<br>
	 * - Encyclopedia of Life database (2014) http://eol.org/pages/328450/hierarchy_entries/55937675/details#reproduction */
	public C_GenomeMusMusculus() {
		super();
		if( this.getGonosome().isFemale())
			alleles.put(SEXUAL_MATURITY_Uday, 50.); //6-9 weeks [Baker, 1979, Maeda et al., 2000 in Doumerc, 2004] / 42 days / 7 [http://genomics.senescence.info/species/entry.php?species=Mus_musculus]
		else
			alleles.put(SEXUAL_MATURITY_Uday, 50.); 
		alleles.put(LITTER_SIZE, 3.); // source:Average Nb embryos ENEMI project 4.9 (1 to 11) x 60% -> 3 (LG) _OR_ Average 5 or 7 litter size
										// [Encyclopedia of Life database 2014] _OR_ 7 [AnAge, 2014]
		alleles.put(WEANING_AGE_Uday, 21.);// source: [Baker, 1979, Maeda et al., 2000 in Doumerc, 2004] _OR_ [Encyclopedia of Life database 2014]
											// _OR_ 22 [AnAge database, 2014]
		alleles.put(MATING_LATENCY_Uday, 9.); // source: Inter litter interval: 30days [AnAge database 2014]
		alleles.put(GESTATION_LENGTH_Uday, 21.); // source: [Encyclopedia of Life database 2014] / 19 [AnAge database 2014]
		alleles.put(MAX_AGE_Uday, 450.); // source: 12-18 months in the wild (http://eol.org/pages/328450/hierarchy_entries/55937675/details#life_expectancy)
		alleles.put(SENSING_UmeterByDay, 15.);// source : Meehan, A. P. (1984). Rats and mice. Their biology and control. Rentokil Ltd..
		alleles.put(SPEED_UmeterByDay, 4000.);//2m./min [!= 40m./day in Lorenz, G. C., & Barrett, G. W. (1990). Influence of simulated landscape corridors on house mouse (Mus musculus) dispersal. American Midland Naturalist, 348-356.
		makeAmniotaBivalent(this.alleles);
		makeAnimaliaBivalent(this.alleles);
	}
	/** Returns a new GenomeMusMusculus. This method is used for genome mate.
	 * @param microsatXsome
	 * @param gonosome
	 * @param autosomes
	 * @see C_GenomeEucaryote#mate */
	public C_GenomeMusMusculus(C_XsomePairMicrosat microsatXsome, C_XsomePairSexual gonosome, ArrayList<C_ChromosomePair> autosomes) {
		super(microsatXsome, gonosome, autosomes);
	}
	//
	// GETTER
	//
	@Override
	protected Double getTaxonSignature() {
		return .41;
	}
}
