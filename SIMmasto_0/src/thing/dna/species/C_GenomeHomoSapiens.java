/*This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt*/
package thing.dna.species;

import java.util.ArrayList;

import thing.dna.C_ChromosomePair;
import thing.dna.C_GenomeAmniota;
import thing.dna.C_GenomeEucaryote;
import thing.dna.C_XsomePairMicrosat;
import thing.dna.C_XsomePairSexual;

/** @author JLF 07.2014, rev. JLF & L.Granjon 08.2014 */
public class C_GenomeHomoSapiens extends C_GenomeAmniota {
	//
	// CONSTRUCTORS
	//
	/** Returns a new C_GenomeHomoSapiens. <br>
	 * Trait values references (see comments in source code):<br>
	 * - */
	public C_GenomeHomoSapiens() {
		super();
		if (this.getGonosome().isFemale()) alleles.put(SEXUAL_MATURITY_Uday, 5110.); // 14 years
		else alleles.put(SEXUAL_MATURITY_Uday, 5110.);
		alleles.put(LITTER_SIZE, 1.);
		alleles.put(WEANING_AGE_Uday, 60.);
		alleles.put(MATING_LATENCY_Uday, 365.); //
		alleles.put(GESTATION_LENGTH_Uday, 3285.); // 9 months
		alleles.put(MAX_AGE_Uday, 29200.); // 80 year
		alleles.put(SENSING_UmeterByDay, 100.);
		/** 4km traveled within a day (urban) 72.000=3km/h during 24h source : Browning, R. C., Baker, E. A., Herron, J. A., &
		 * Kram, R. (2006) Effects of obesity and sex on the energetic cost and preferred speed of walking. Journal of applied
		 * physiology, 100(2), 390-398 */
		alleles.put(SPEED_UmeterByDay, 9100.);//TODO MS 08.2021 Junk value
		makeAmniotaBivalent(this.alleles);
		makeAnimaliaBivalent(this.alleles);
	}
	/** Returns a new Genome Homo sapiens. This method is used for genome mate.
	 * @param microsatXsome
	 * @param gonosome
	 * @param autosomes
	 * @see C_GenomeEucaryote#mate */
	public C_GenomeHomoSapiens(C_XsomePairMicrosat microsatXsome, C_XsomePairSexual gonosome,
			ArrayList<C_ChromosomePair> autosomes) {
		super(microsatXsome, gonosome, autosomes);
	}
	//
	// GETTER
	//
	@Override
	protected Double getTaxonSignature() {
		return .01;
	}
}
