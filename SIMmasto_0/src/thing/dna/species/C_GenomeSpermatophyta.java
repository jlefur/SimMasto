package thing.dna.species;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import thing.dna.C_Chromosome;
import thing.dna.C_ChromosomePair;
import thing.dna.C_Gene;
import thing.dna.C_GenomeEucaryote;

/** The spermatophyts are a group plants that produce seeds
 * @author J.Le Fur 2012 (source code origin: Kyle Wagner), rev. JLF&MS 04.2016 */
public class C_GenomeSpermatophyta extends C_GenomeEucaryote {
	// TODO JLF 2015.11 following lines should be put in one map mapPosition and one map mapLoci Maplocs and locus of the
	// chromosome Pair, ex:
	// private TreeMap<String, Double[]> mapPosition = new TreeMap<String, Double[]>;
	// this.mapPosition.put(LITTER_SIZE,12.);
	protected static final ArrayList<Double> GROWTH_RATE_MAPLOCS = new ArrayList<Double>(Arrays.asList(16.));
	protected static final int GROWTH_RATE_LOCUS = 0;
	protected static final int numGenesXsomeSpermatophyta = 1;
	protected C_ChromosomePair xsomePairSpermatophyta;
	//
	// CONSTRUCTOR
	//
	/** Return a new GenomeSpermatophyta, adds an autosome bearing the reproductive traits of the (daughters) species.<br>
	 */
	public C_GenomeSpermatophyta() {
		super();
		this.alleles.put(GROWTH_RATE_UkcalPerDay, DEFAULT_GROWTH_RATE_UgramPerDay);// source : junk value for the vegetation growth rate, need a real source!
		makeSpermatophytaBivalent(this.alleles);// values inside the allele map are each time replaced by the daughter class and its
		// intermediates JLF 08.2014
	}
	//
	// METHOD
	//
	/** @param allelesMap the specific allele value from the appropriate daughter genome /author J. Le Fur 08.2014 */
	protected void makeSpermatophytaBivalent(Map<String, Double> allelesMap) {
		// remove obsolete xsomePairSpermatophyta constructed by mother classes
		this.bivalents.remove(this.xsomePairSpermatophyta);
		this.xsomePairSpermatophyta = new C_ChromosomePair(C_Chromosome.MAP_LENGTH, numGenesXsomeSpermatophyta,
				recombinator);

		this.xsomePairSpermatophyta.setGenePairAtLocus(GROWTH_RATE_LOCUS, //
				new C_Gene(allelesMap.get(GROWTH_RATE_UkcalPerDay) + getTaxonSignature(), GROWTH_RATE_MAPLOCS.get(0),
						mutator, this.xsomePairSpermatophyta.xsomeStrands[0].getMyId()), //
				new C_Gene(allelesMap.get(GROWTH_RATE_UkcalPerDay) + getTaxonSignature(), GROWTH_RATE_MAPLOCS.get(0),
						mutator, this.xsomePairSpermatophyta.xsomeStrands[1].getMyId()));
		this.bivalents.add(bivalents.size(), this.xsomePairSpermatophyta);
	}
	//
	// GETTER
	//
	// Public direct access to the genes value <br>
	// Reminder: the expressor is an expressor of type average // SUPPOSES ALL MAPLOCS ARE ON THE SAME CHROMOSOME.
	// To account for maplocs on several xsomes: for each map loc find the chromosome, apply the expressor each time, process the
	// list of results (one value per chromosome).<br>
	// author Kyle Wagner 2002, rev.JLF 05.2012, 07.2014, 08.2014, JLF&MS 04.2016 */
	//
	public double getGrowthRateValue() {
		return ((double) expressor.evalTrait(getChromosomePairFromMaploc(GROWTH_RATE_MAPLOCS.get(0)),
				GROWTH_RATE_MAPLOCS));
	}
}
