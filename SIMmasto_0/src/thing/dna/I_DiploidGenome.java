package thing.dna;

import java.util.ArrayList;
import java.util.Map;

public interface I_DiploidGenome {

	/** Successively mates (with the corresponding cross-over) all chromosome pairs of the genome used to specify any kind of
	 * genome in things (e.g., Mastomys genome in C_Rodent)
	 * @param mutRate
	 * @param parent2 */
	public I_DiploidGenome mateGenomes(Object mutRate, I_DiploidGenome parent2);
	public ArrayList<C_Chromosome> makeGametes();
	public int getDiploidNumber();
	public boolean isHybrid();
	public Map<String, Double> getAlleles();// JLF 03.2018 used for organisms probes display

}