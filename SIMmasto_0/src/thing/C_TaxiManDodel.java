package thing;

import com.vividsolutions.jts.geom.Coordinate;

import simmasto0.C_ContextCreator;
import simmasto0.protocol.A_Protocol;
import thing.dna.I_DiploidGenome;
import thing.ground.I_Container;

public class C_TaxiManDodel extends C_TaxiMan {
	private I_Container marketPlace = myLandscape.getGrid()[7][21];// TODO number in source 2018.03 jlf
	public C_TaxiManDodel(I_DiploidGenome genome) {
		super(genome);
	}
	@Override
	public void step_Utick() {
		computeNextMoveToTarget();
		if (this.isArrived(this.speed_UmeterByTick)) {
			this.setHasToSwitchFace(true);
			this.nextMove_Umeter.x = 0.0;
			this.nextMove_Umeter.y = 0.0;
			A_VisibleAgent.myLandscape.moveToLocation(this, this.target.getCoordinate_Ucs());
			if (this.target == marketPlace) {
				this.vehicle.unloadRodents();
				A_Protocol.event("C_ProtocolDodel.step_Utick", "Unloaded one rodent", isNotError);
				if (C_ContextCreator.randomGeneratorForInitialisation.nextDouble() > .5) this.setTarget(myLandscape.getGrid()[0][31]);// TODO number in source 2018.03 jlf
				else this.setTarget(myLandscape.getGrid()[18][0]);// TODO number in source 2018.03 jlf
			}
			else this.setDead(true);
		}
		else {
			this.computeNextMoveToTarget();
			this.actionMove();
			A_VisibleAgent.myLandscape.moveToLocation(this.vehicle, this.getCoordinate_Ucs());
			this.vehicle.carryRodentsToMyLocation_Ucs();
		}
	}
	@Override
	public Coordinate getTargetPoint_Umeter() {
		return this.target.getCoordinate_Umeter();
	}
	@Override
	public void discardThis() {
		if (!this.getVehicle().getFullRodentList().isEmpty()) {
			this.vehicle.unloadRodents();
		}
		this.vehicle = null;
	}
}