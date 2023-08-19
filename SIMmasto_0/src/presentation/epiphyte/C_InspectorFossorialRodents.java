package presentation.epiphyte;

import java.util.Iterator;
import java.util.TreeSet;
import repast.simphony.engine.environment.RunState;
import repast.simphony.essentials.RepastEssentials;
import simmasto0.protocol.A_Protocol;
import thing.A_NDS;
import thing.C_Rodent;
import thing.I_SituatedThing;
import thing.ground.C_BurrowSystem;

/** Data inspector: retrieves information on burrow systems and manages lists.
 * @author A Realini 05.2011 / J.Le Fur 09.2011, 07.2012, 01/2013 */
public class C_InspectorFossorialRodents extends A_Inspector {

	protected double nbWanderingRodents, wanderingRodents_Upercent;
	protected TreeSet<C_BurrowSystem> burrowList;

	public C_InspectorFossorialRodents() {
		super();
		this.nbWanderingRodents = 0.; // double to allow the wanderingRodents_Upercent computation - LeFur 2011;
		this.wanderingRodents_Upercent = 0.;
		this.burrowList = new TreeSet<C_BurrowSystem>();
		// add to the super class header, this proper header
		this.indicatorsHeader = "tick;NbBurrows;wanderers(%)";
	}

	/** computes nb of wandering rodents, nb of burrows */
	public void indicatorsCompute() {
		TreeSet<C_Rodent> listRodents = C_InspectorPopulation.rodentList;
		Iterator<C_Rodent> rodents = listRodents.iterator();
		this.nbWanderingRodents = 0.; // double to allow the wanderingRodents_Upercent computation - LeFur 2011;
		while (rodents.hasNext())
			if (!(rodents.next().getCurrentSoilCell() instanceof C_BurrowSystem)) this.nbWanderingRodents++;
		this.wanderingRodents_Upercent = this.nbWanderingRodents / listRodents.size();
		this.burrowList.clear();
		Object[] liste = RunState.getInstance().getMasterContext().toArray();
		for (int i = 0; i < liste.length; i++)
			if (liste[i] instanceof C_BurrowSystem) this.burrowList.add((C_BurrowSystem) liste[i]);
	}
	/** Remove burrow system, do not account for C_Traps */
	@Override
	public void discardDeadThing(I_SituatedThing thing) {
		if ((thing instanceof C_BurrowSystem) && !this.burrowList.remove(thing))
			A_Protocol.event("C_InspectorFossorialRodents.discardDeadThing", "Could not remove " + ((A_NDS) thing).retrieveMyName(), isError);
	}
	@Override
	/** stores the current state of indicators in the field including the super ones has to be conform with indicatorsHeader / JLF
	 * 01.2013 */
	public String indicatorsStoreValues() {
	    this.indicatorsValues = RepastEssentials.GetTickCount() + CSV_FIELD_SEPARATOR + getNbBurrows() + CSV_FIELD_SEPARATOR + wanderingRodents_Upercent;
		return (this.indicatorsValues);
	}
	public void addBurrowToList(C_BurrowSystem burrow) {
		if (!this.burrowList.add(burrow)) A_Protocol.event("C_InspectorPopulation.addBurrowToList", "Could not add " + burrow, isError);
	}

	// GETTERS

	public int getNbBurrows() {
		return this.burrowList.size();
	}
	public double getWanderingRodents_Upercent() {
		return this.wanderingRodents_Upercent;
	}
	public TreeSet<C_BurrowSystem> getBurrowList() {
		return this.burrowList;
	}
}
