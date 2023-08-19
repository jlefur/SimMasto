package thing;
import thing.dna.I_DiploidGenome;
/** a simple structure containing a diploid genome, gets all the properties of NDS
 * @author J.LeFur 2011 */
public class C_Egg extends A_Organism {

	public C_Egg(I_DiploidGenome genome) {
		super(genome);
		this.setMyName("egg" + NAMES_SEPARATOR + myId);
	}
}