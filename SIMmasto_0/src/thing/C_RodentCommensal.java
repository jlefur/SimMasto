package thing;

import data.constants.I_ConstantTransportation;
import simmasto0.C_ContextCreator;
import thing.dna.I_DiploidGenome;
import thing.ground.C_Vehicle;

/** Define rodent agents that are used to live with humans (e.g., random move if no destination, try boarding in vehicles)<br>
 * created for protocolCentenal
 * @author Jean Le Fur, oct.2014 */
public class C_RodentCommensal extends C_RodentFossorial implements I_ConstantTransportation {
	//
	// FIELD
	//
	public boolean everTransported;
	//
	// CONSTRUCTOR
	//
	public C_RodentCommensal(I_DiploidGenome genome) {
		super(genome);
		everTransported = false;
	}
	//
	// METHODS
	//
	/** Generate a new animal, necessary to use spawn
	 * @see A_Amniote#actionSpawn */
	@Override
	public A_Animal giveBirth(I_DiploidGenome genome) {
		return new C_RodentCommensal(genome);
	}
	public boolean isEverTransported() {
		return everTransported;
	}
	/** Interact with another rodent. systematically mate if both partner reproductive status are ok. <br>
	 * @see C_Rodent#mateWithMale <br>
	 *      Here, no breeding season test: commensal rodent do not have a reproduction season, they mate all year long */
	@Override
	protected boolean actionInteract(C_Rodent rodent) {
		if (this.genome.getClass().equals(rodent.genome.getClass()) && (this.readyToMate && rodent.readyToMate && recognized(rodent))) {
			if (this.testFemale() && rodent.testMale()) {
				this.actionMateWithMale(rodent);
				return true;
			}
			else if (this.testMale() && rodent.testFemale()) {
				rodent.actionMateWithMale(this);
				return true;
			}
		}
		return false;// TODO JLF 2017.01 place for future agonistic behaviours
	}

	/** Sucked up by the container */
	protected boolean processTarget() {
		// Sucked up by the container target, when arrived at proximity.
		if (this.target instanceof C_Vehicle) {
			if (this.actionTryBoardingContainer((C_Vehicle) this.target)) return true;
			else {
				this.discardTarget();
				return false;
			}
		}
		else return super.processTarget();
	}

	/** Rat boarding is computed following the loading probability of the carrier's vehicle Mboup 2013 Version rev. JLF 02.2014, 10.2015 MS 2019.10*/
	protected boolean actionTryBoardingContainer(C_Vehicle vehicle) {
		// JLF everTransported below accounts for possible previous successful boarding
		if (vehicle.isParked() && !this.trappedOnBoard){// && !this.everTransported) {
			// TODO number in source NOT OK (%) JLF 2015
			double x = C_ContextCreator.randomGeneratorForBoarding.nextDouble() * 100;
			if (!vehicle.isFull() && x < vehicle.getLoadingProba()) {
				C_ContextCreator.protocol.contextualizeOldThingInCell(this, vehicle);
				this.discardTarget();
				for (A_Animal oneAnimal : this.animalsTargetingMe)
					oneAnimal.target = null;// cannot use discardTarget due to concurrent modification exception / jlf 12.2017
				this.animalsTargetingMe.clear();
				this.energy_Ukcal--;
				this.everTransported = true;
				return (this.trappedOnBoard = true);
			}
		}
		return (this.trappedOnBoard = false);
	}
}
