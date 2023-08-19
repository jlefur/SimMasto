package data.constants;

/** Numbered variables bound to ProtocolCage since SimMasto specification requires no numbers in the java sources
 * @author Le Fur 09.2017 */
public interface I_ConstantCage {
	public int FORBID_ZONE = 2; //  minimum affinity of plots outside the cages
	public int NB_CAGES_COLUMNS = 20, NB_CAGES_LINES = 22;
	public double IN_CAGE_SPEED_REDUCER = 100;// Reduce speed within cages
}
