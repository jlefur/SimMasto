package thing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import simmasto0.C_ContextCreator;
import simmasto0.protocol.C_ProtocolBandia;
import thing.dna.I_DiploidGenome;
import thing.ground.C_Trap;
import thing.ground.I_Container;

/** Implements a rodent able to compute the CMR indicator on its own. Uses a catchHistory table with date, session n° and x,y
 * coordinates of the catches
 * @author Malick Diakhate 2013, rev. Le Fur 10.2013 */
public class C_RodentCmr extends C_RodentFossorial {
	//
	// FIELDS
	//
	private int tag;
	// catchHistory: key=session number, String[]=date, x, y
	private Map<Integer, ArrayList<String[]>> catchHistory = new HashMap<Integer, ArrayList<String[]>>();
	//
	// CONSTRUCTOR
	//
	public C_RodentCmr(I_DiploidGenome genome) {
		super(genome);
		this.tag = -1;
	}
	//
	// METHODS
	//
	/** Generate a new animal, necessary to use spawn
	 * @see A_Amniote#spawn */
	@Override
	public A_Animal giveBirth(I_DiploidGenome genome) {
		return new C_RodentCmr(genome);
	}
	/** Record date and position when rodent is trapped
	 * @param session : the session number */
	public void recordCatch(int session, String date) {
		String[] event = {date, currentSoilCell.getCoordinate_Umeter().x + "", currentSoilCell.getCoordinate_Umeter().y + ""};
		if (catchHistory.get(session) == null) // Initialize a key when new session
			catchHistory.put(session, new ArrayList<String[]>());
		catchHistory.get(session).add(event); // add the current catch in the given session number
	}
	/** Return true if captured within the session or within a further one (used to compute MNA) */
	public boolean aliveInSession(int session) {
		boolean alive = false;
		for (int oneSession = session; oneSession == C_ProtocolBandia.numSession; oneSession++) {
			if (catchHistory.get(oneSession) != null) {
				alive = true;
				break;
			}
		}
		return alive;
	}
	/** Trap this-self if target is a C_Trap, else super<br>(RodentCMR's skill)<br>
	 * JLF 07.2014, rev.05.2018 */
	protected boolean processTarget() {
		// Sucked up by the container target, when arrived at proximity.
		if (this.target instanceof C_Trap) {
			C_Trap trap = (C_Trap) this.target;// the following instruction remove the target of this
			C_ContextCreator.protocol.contextualizeOldThingInCell(this, (I_Container) this.target);
			trap.trapRodent(this);
			return true;
		}
		else return super.processTarget();
	}
	/** Mean distance between successive catches within a given session (DRS: "Distance entre Recaptures Successives")
	 * @param session : the session number */
	public double computeDRS(int session) {
		double xa, xb, ya, yb, drs = 0., nbDistances = 0., sumDistances = 0.;
		int catchEvent, nbCatches;
		if (catchHistory.get(session) != null) {// null occurs when rodent is in inspectors' tagged list but was catched in other
												// sessions and not this one
			nbCatches = catchHistory.get(session).size();
			if (nbCatches != 0) {
				for (catchEvent = 0; catchEvent < nbCatches - 1; catchEvent++) {
					// retrieve the coordinates for the current catchEvent
					xa = Double.parseDouble(catchHistory.get(session).get(catchEvent)[1]);
					ya = Double.parseDouble(catchHistory.get(session).get(catchEvent)[2]);
					// retrieve the coordinates for the following catchEvent
					xb = Double.parseDouble(catchHistory.get(session).get(catchEvent + 1)[1]);
					yb = Double.parseDouble(catchHistory.get(session).get(catchEvent + 1)[2]);
					// compute the hypotenuse/distance between the two catches
					sumDistances += Math.sqrt(Math.pow(xa - xb, 2) + Math.pow(ya - yb, 2));
					nbDistances++;
				}
				if (nbDistances > 0.) drs = sumDistances / nbDistances; // if nbDistances = 0. -> drs = 0.
			}
		}
		return drs;
	}
	/** compute every distances between catches and keep the largest one (DMR: Distance maximum de recapture) */
	public double computeDMR(int session) {
		double xa, xb, ya, yb, dmr = 0., catchDistance = 0.;
		int catchEvent1, catchEvent2, nbCatches;
		if (catchHistory.get(session) != null) {// null occurs when rodent is in inspectors' tagged list but was catched in other
			// sessions and not this one
			nbCatches = catchHistory.get(session).size();
			for (catchEvent1 = 0; catchEvent1 < nbCatches; catchEvent1++) {
				// retrieve the coordinates for the first catchEvent
				xa = Double.parseDouble(catchHistory.get(session).get(catchEvent1)[1]);
				ya = Double.parseDouble(catchHistory.get(session).get(catchEvent1)[2]);
				for (catchEvent2 = 0; catchEvent2 < nbCatches; catchEvent2++) {
					// retrieve the coordinates for the other catchEvents
					xb = Double.parseDouble(catchHistory.get(session).get(catchEvent2)[1]);
					yb = Double.parseDouble(catchHistory.get(session).get(catchEvent2)[2]);
					// compute the hypotenuse/distance between the two catches
					if ((xa != xb) || (ya != yb)) // avoid that when only one catch DMR= sqrt(2)
						catchDistance = Math.sqrt(Math.pow(xa - xb, 2) + Math.pow(ya - yb, 2));
					if (dmr < catchDistance) dmr = catchDistance;
				}
			}
		}
		return dmr;

		// SETTER & GETTERS
	}
	public void setTag(int tag) {
		this.tag = tag;
	}
	public int getTag() {
		return tag;
	}
}
