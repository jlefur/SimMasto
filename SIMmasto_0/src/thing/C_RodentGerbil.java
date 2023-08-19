package thing;

import java.util.TreeSet;

import data.C_Parameters;
import data.constants.I_ConstantGerbil;
import data.converters.C_ConvertTimeAndSpace;
import simmasto0.protocol.A_Protocol;
import thing.dna.I_DiploidGenome;
import thing.dna.species.C_GenomeBalanites;
import thing.dna.species.C_GenomeFabacea;
import thing.dna.species.C_GenomePoacea;
import thing.ground.C_BurrowSystem;
import thing.ground.C_Nest;
import thing.ground.I_Container;

/** Case study on Gerbillus nigeriae: gerbils choose food, partners or shelter depending on their energetic state and their perceived environment.
 * @author Moussa Sall, 2015, rev. 2016, JLF 04.2017, JLF&MS 01.2018, JLF 07.218 */
public class C_RodentGerbil extends C_RodentFossorial implements I_ConstantGerbil {
	//
	// FIELD
	//
	/** 6-methoxybenzoquinone is a molecule produced by young shoots that could trigger rodents'reproduction (Schadler et al.,1988,Biol.Reprod.
	 * 38:817-820; Butterstein et al. Biol Reprod. 1985,32(5):1018-23. */
	protected boolean methoxybenzoxazolinone;
	public boolean isMethoxybenzoxazolinone() {
		return methoxybenzoxazolinone;
	}
	//
	// CONSTRUCTOR
	//
	public C_RodentGerbil(I_DiploidGenome genome) {
		super(genome);
		energy_Ukcal = INITIAL_ENERGY_Ukcal;
	}
	//
	// METHODS
	//
	@Override
	/** - if day comes find or remain in burrow, desire=NONE<br>
	 * - if night comes if not suckling, desire = empty + exit from burrow (by reflex)<br>
	 * - at night do super<br>
	 * JLF&MS 2017, rev.02-07.2018 */
	public void step_Utick() {
		if (!this.isTrappedOnBoard()) {
			// REMAIN OR SEARCH SHELTER
			if (A_Protocol.protocolCalendar.isDawn() || A_Protocol.protocolCalendar.isDayTime()) {
				if (this.currentSoilCell instanceof C_BurrowSystem) this.setDesire(NONE); // keep quiet
				else this.setDesire(HIDE); // search shelter
			}
			// WAKE UP : exit from burrow when twilight comes (sucklings do not act)
			else if (A_Protocol.protocolCalendar.isTwilight() && this.getCurrentSoilCell() instanceof C_BurrowSystem) {
				if (!this.isSucklingChild()) {
					this.actionRandomExitOfContainer();
					if (this.getDesire().equals(NONE) || this.getDesire().equals(HIDE)) this.setDesire("");
				}
			}
			super.step_Utick();
			// If did not found or do not target shelter, then dig burrow
			if (this.getDesire().equals(HIDE) && !(this.currentSoilCell instanceof C_BurrowSystem) && this.target == null) {
				if (A_Protocol.protocolCalendar.isDayTime()) {
					this.actionEnterContainer(this.actionDig());
					this.setDesire(NONE);
				}
			}
		}
	}
	/** If within a burrow system, only retrieve the burrow's occupants, else super<br>
	 * Version Authors J.E. Longueville 2011 - J.Le Fur 03.2012,04.2015,01.2016
	 * @see A_VisibleAgent#retrieveCell2Perception
	 * @return TreeSet<I_situated_thing> listeVisibleObject */
	@Override
	protected TreeSet<I_SituatedThing> perception() {
		if (this.currentSoilCell instanceof C_BurrowSystem) return this.currentSoilCell.getOccupantList();
		else return super.perception();
	}
	/** Author MS 2018, Check if barn owl are perceived */
	@Override
	protected void checkDanger() {
		TreeSet<I_SituatedThing> perceivedThings = this.perception();
		for (I_SituatedThing oneThing : perceivedThings) {
			if (oneThing instanceof C_BarnOwl) {
				this.setDesire(HIDE);
				break;
			}
		}
		super.checkDanger();
	};
	/** Author MS 2016, rev. JLF&MS 05.2017, JLF 03.2018.
	 * @return list of candidates selected */
	@Override
	protected TreeSet<I_SituatedThing> deliberation(TreeSet<I_SituatedThing> perceivedThings) {
		if (this.getDesire().equals(HIDE)) return this.chooseShelter(perceivedThings);
		return super.deliberation(perceivedThings);
	}
	/** Use list perceived things and choose the candidate shelter which can be one burrow, a vegetation list or nothing */
	@Override
	public TreeSet<I_SituatedThing> chooseShelter(TreeSet<I_SituatedThing> perceivedThings) {
		//TreeSet<I_SituatedThing> shrubShelters = new TreeSet<I_SituatedThing>();
		TreeSet<I_SituatedThing> shelters = new TreeSet<I_SituatedThing>();
		for (I_SituatedThing onePerceivedThing : perceivedThings) {
			if ((onePerceivedThing instanceof C_BurrowSystem) && !(onePerceivedThing instanceof C_Nest)) {// Choose burrow and
				shelters.add(onePerceivedThing);
			}
			else if (onePerceivedThing instanceof C_Vegetation) {
				C_Vegetation oneVegetation = (C_Vegetation) onePerceivedThing;
				if ((oneVegetation.genome instanceof C_GenomeBalanites) && this.canInteractWith(oneVegetation)) shelters.add(onePerceivedThing);
			}
		}
		if (shelters.size() != 0) return shelters;
//		if (shrubShelters.size() != 0) return shrubShelters;
		return super.chooseShelter(perceivedThings);
	}
	/** Use list perceived things and choose grass or crop */
	@Override
	public TreeSet<I_SituatedThing> chooseFood(TreeSet<I_SituatedThing> perceivedThings) {
		TreeSet<I_SituatedThing> vegetationSelected = new TreeSet<I_SituatedThing>();
		for (I_SituatedThing onePerceivedThing : perceivedThings) {
			if ((onePerceivedThing instanceof C_Vegetation) && canInteractWith((C_Vegetation) onePerceivedThing)//
					&& (//
					(((C_Vegetation) onePerceivedThing).getGenome() instanceof C_GenomePoacea) || (((C_Vegetation) onePerceivedThing)
							.getGenome() instanceof C_GenomeFabacea) || (((C_Vegetation) onePerceivedThing).getGenome() instanceof C_GenomeBalanites)) //
			) {
				vegetationSelected.add(onePerceivedThing);
			}
		}
		return vegetationSelected;
	}
	/** Account for the vegetation coverage radius to determine if vegetation is perceived; return true if it can interact with it else return
	 * false */
	public boolean canInteractWith(I_SituatedThing oneOccupant) {
		if (oneOccupant instanceof C_Vegetation) {
			double distanceToVegetation_Umeter = this.getDistance_Umeter(oneOccupant);
			if ((vegetationCoverageRadius_Umeter + this.sensing_UmeterByTick) > distanceToVegetation_Umeter) return true;
		}
		return super.canInteractWith(oneOccupant);
	}
	@Override
	/** process target when desire==HIDE */
	protected boolean processTarget() {
		if (this.getDesire().equals(HIDE)) {
			if (this.target instanceof C_BurrowSystem) {
				this.actionEnterContainer((C_BurrowSystem) this.target);
				if (this.currentSoilCell.isFull() && !this.getDesire().equals(NONE)) {
					actionDisperse();
				}
				return true;
			}
			// case of SHRUB
			else if (this.target instanceof C_Vegetation && ((C_Vegetation) this.target).genome instanceof C_GenomeBalanites)
				this.actionEnterContainer((I_Container) this.target);// schrubs (vegetation) are I_Containers - jlf 07.2018
			// Case rodent does not get target
			else {
				this.setNewRandomMove();
				this.actionMove();
			}
			this.setDesire("");
			return true;
		}
		return super.processTarget();
	}
	@Override
	/** Change burrow affinity to the number of vegetation in the cell (affinity is used for landcover coding) JLF 02.2018 */
	public C_BurrowSystem actionDig() {
		C_BurrowSystem newBurrow = super.actionDig();
		int affinity = 0;
		for (I_SituatedThing thing : newBurrow.getCurrentSoilCell().getOccupantList())
			if (thing instanceof C_Vegetation) affinity++;
		newBurrow.setAffinity(affinity);
		return newBurrow;
	}
	@Override
	protected boolean actionEat() {
		for (int i = 0; i < repeatEating; i++)
			this.energy_Ukcal++;
		if (this.currentSoilCell instanceof C_Vegetation) {
			this.methoxybenzoxazolinone = ((C_Vegetation) this.currentSoilCell).getMethoxybenzoxazolinone();
			((C_Vegetation) this.currentSoilCell).energy_Ukcal--;
		}
		this.actionDisperse();
		return true;
	}
	/** Consume vegetation biomass and return gain energy (1g vegetation = 1000 cal); intake proportional to available biomass
	 * @version JLF 09.2017, rev. JLF&MS 01.2018, JLF 02.2021
	 * @return true if success */
	protected boolean actionEat0() {
		if (this.currentSoilCell instanceof C_Vegetation) {
			C_Vegetation oneVegetation = (C_Vegetation) this.currentSoilCell;
			I_DiploidGenome genome = oneVegetation.getGenome();
			if ((genome instanceof C_GenomeFabacea) || (genome instanceof C_GenomePoacea) || (genome instanceof C_GenomeBalanites)) {
				double foodIntake_UkcalByTick = Math.min(oneVegetation.getEnergy_Ukcal(), DAILY_CONSUMPTION_NEED_UkcalPerDay
						/ C_ConvertTimeAndSpace.oneDay_Utick);
				// oneVegetation.setEaten(foodIntake_UkcalByTick);
				this.methoxybenzoxazolinone = oneVegetation.getMethoxybenzoxazolinone();
				this.energy_Ukcal += foodIntake_UkcalByTick * 1000;// TODO number in source JLF 2021.02 temporary shortcut: 1 gram
																	// vegetation = 1kcal
				/*
				 * A_Protocol.event("C_RodentGerbil.actionEat", this + " eats " + Math.round(foodIntake_UgramByTick 1000.) + " cal. on " +
				 * oneVegetation, isNotError);
				 */
				return true;
			}
			else A_Protocol.event("C_RodentGerbil.actionEat", this + " do not target crop or grass (" + this.getCurrentSoilCell() + ")", isError);
		}
		else if (C_Parameters.VERBOSE)
			A_Protocol.event("C_RodentGerbil.actionEat", this + " do not forage on vegetation (" + this.target + ")", isError);
		return false;
	}
	/** choose to disperse if no target found.<br>
	 * May be overridden in daughter classes<br>
	 * J.Le Fur, 07.2018 */
	@Override
	protected void actionNoChoice() {
		this.actionDisperse();
	}
	@Override
	/** Methoxybenzoxazolinone is used to trigger readyToMate<br>
	 * author: MS et JLF 04.2018 */
	protected void updatePhysiologicStatus() {
		super.updatePhysiologicStatus();
		if (this.isSexualMature() && !this.isPregnant() && this.methoxybenzoxazolinone) this.readyToMate = true;
		else this.readyToMate = false;
	}
	/** Generate a new animal, necessary to use spawn
	 * @see A_Amniote#spawn */
	@Override
	public A_Animal giveBirth(I_DiploidGenome genome) {
		return new C_RodentGerbil(genome);
	}
}
