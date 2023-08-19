/* This source code is licensed under a Creative Commons-BY licence*/
/* J.Le Fur 03.2014, rev. 2014-2017, 2021 */
package thing.ground;

import java.util.TreeSet;
import thing.C_Rodent;
import thing.I_SituatedThing;

public interface I_Container extends I_SituatedThing {

	public boolean agentIncoming(I_SituatedThing a_thing);
	public boolean agentLeaving(I_SituatedThing agent);

	public TreeSet<I_SituatedThing> getOccupantList();
	public TreeSet<I_SituatedThing> getFullOccupantList();
	public TreeSet<I_Container> getContainerList();
	public boolean isFull();

	public void setAffinity(int a);
	public int getAffinity();

	public int getCarryingCapacity_Urodent();
	public TreeSet<C_Rodent> getRodentList();
	public TreeSet<C_Rodent> getFullRodentList();
	public int getLoad_Urodent();
	public int getFullLoad_Urodent();
}
