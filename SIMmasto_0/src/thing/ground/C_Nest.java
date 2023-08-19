package thing.ground;

/** This class accounts for Tick .
 * @author M. Sall 03.2017 */
public class C_Nest extends C_BurrowSystem {
	//
	// CONSTRUCTOR
	//
	/** Constructor sets the affinity to 10 */
	public C_Nest(int affinity, int lineNo, int colNo) {
		super(affinity, lineNo, colNo);
	}
	@Override
	public boolean isDead() {
		return false;
	}

}