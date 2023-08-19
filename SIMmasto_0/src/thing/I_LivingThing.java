/**
 * 
 */
package thing;
/** @author Jean Le Fur, 2010-2018, rev. JE.Longueville 2011-01 */
public interface I_LivingThing {

	/** get existence
	 * @return the identification */
	public String retrieveMyName();

	/** @return the birth time step. */
	public double getBirthDate_Utick();

	/** @return the age in time step. */
	public double getAge_Uday();

	/** increment time step */
	public void actionGrowOlder_Utick();

	/** linked to its environment<br>
	 * public void interact() ? */
}
