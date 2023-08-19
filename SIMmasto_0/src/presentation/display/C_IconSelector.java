package presentation.display;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import data.C_Parameters;
import data.constants.I_ConstantImagesNames;
import data.constants.I_ConstantString;
import simmasto0.protocol.A_Protocol;
import thing.A_Amniote;
import thing.A_Animal;
import thing.A_NDS;
import thing.C_BarnOwl;
import thing.C_Cat;
import thing.C_Food;
import thing.A_Human;
import thing.C_HumanCarrier;
import thing.A_HumanUrban;
import thing.C_OrnitodorosSonrai;
import thing.C_Rodent;
import thing.C_RodentGerbil;
import thing.C_RodentHouseMouse;
import thing.C_RodentMastoErySimple;
import thing.C_TaxiManDodel;
import thing.C_Vegetation;
import thing.I_SituatedThing;
import thing.dna.species.C_GenomeAcacia;
import thing.dna.species.C_GenomeBalanites;
import thing.dna.species.C_GenomeFabacea;
import thing.dna.species.C_GenomeMastoErythroleucus;
import thing.dna.species.C_GenomeMastoNatalensis;
import thing.dna.species.C_GenomeMastomys;
import thing.dna.species.C_GenomePoacea;
import thing.ground.C_BurrowSystem;
import thing.ground.C_Market;
import thing.ground.C_Nest;
import thing.ground.C_SoilCellSavanna;
import thing.ground.C_Trap;

/** Gestionnaire d'images / Icons may be either gif image files or geonetric icons
 * @author A Realini, rev. JLF 10.2015 */
public class C_IconSelector implements I_ConstantString, I_ConstantImagesNames {

	/** Charge une image
	 * @param nomImage : le nom de l'image à charger */
	public BufferedImage loadImage(String nomImage) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File($PATH + nomImage + ext));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return img;
	}
	/** Renvoie le nom de l'image à utiliser pour l'agent en paramètre */
	public String getNameOfImage(I_SituatedThing agent) {
		if (C_Parameters.PROTOCOL.equals(CHIZE)) return getNameOfImageChize(agent);
		else if (C_Parameters.PROTOCOL.equals(GERBIL_PROTOCOL)) return getNameOfImageGerbil(agent);
		else if (C_Parameters.PROTOCOL.equals(ENCLOSURE)) return getNameOfImageEnclosMbour(agent);
		else if (C_Parameters.PROTOCOL.equals(CAGES)) return getNameOfImageEnclosMbour(agent);
		else if (C_Parameters.PROTOCOL.equals(HYBRID_UNIFORM)) return getNameOfImageEnclosMbour(agent);
		else if (C_Parameters.PROTOCOL.contains(CENTENAL)) return getNameOfImageTransportRattus(agent);
		else if (C_Parameters.PROTOCOL.equals(DECENAL)) return getNameOfImageTransportRattus(agent);
		else if (C_Parameters.PROTOCOL.equals(MUS_TRANSPORT)) return getNameOfImageTransportMus(agent);
		else if (C_Parameters.PROTOCOL.equals(VILLAGE)) return getNameOfImageTransportRattus(agent);
		else if (C_Parameters.PROTOCOL.equals(DODEL)) return getNameOfImageDodel(agent);
		else if (C_Parameters.PROTOCOL.equals(DODEL2)) return getNameOfImageDodel2(agent);
		else if (C_Parameters.PROTOCOL.equals(BANDIA)) return getNameOfImageBandia(agent);
		else return getNameOfImageChize(agent);
	}
	public String getNameOfImageBandia(I_SituatedThing agent) {
		if (agent instanceof C_Trap) {
			if (((C_Trap) agent).getRodentList().size() > 0) return LOADED_TRAP;
			else return EMPTY_TRAP;
		}
		else if (agent instanceof C_Rodent) {
			C_Rodent rodent = (C_Rodent) agent;
			if (rodent.getCurrentSoilCell() instanceof C_BurrowSystem) return MASTO_UNDERGROUND;
			if (rodent.isHasToLeaveFullContainer()) return MASTO_DISPERSE;
			if (!rodent.isSexualMature()) return MASTO_JUVENILE;
			if (rodent.testMale()) return MASTO_MALE;
			if (rodent.isPregnant()) return MASTO_PREGNANT;
			else return MASTO_FEMALE;
		}
		A_Protocol.event("C_IconSelector.getNameOfImageBandia", "No icon found for " + agent, isError);
		return null;// PB in this case
	}

	public String getNameOfImageTransportMus(I_SituatedThing agent) {
		if (agent instanceof C_RodentHouseMouse) {
			if (((C_RodentHouseMouse) agent).isPregnant()) return MUS_PREGNANT;
			else return MUS_IMAGE;
		}
		else if (agent instanceof C_RodentMastoErySimple) return MASTO_ERY_IMAGE;
		else return this.getNameOfImageTransportRattus(agent);
	}
	public String getNameOfImageTransportRattus(I_SituatedThing agent) {
		if (agent instanceof C_HumanCarrier) {
			String typeVehicle = ((C_HumanCarrier) agent).getVehicle().getType();
			if (((C_HumanCarrier) agent).getVehicle().isParked()) return VEHICLE_PARKED;
			else if (((C_HumanCarrier) agent).getVehicle().getLoad_Urodent() > 0) return VEHICLE_LOADED;
			else if (typeVehicle.equals(BOAT_EVENT)) return VEHICLE_BOAT;
			else if (typeVehicle.equals(TRAIN_EVENT)) return VEHICLE_TRAIN;
			else if (typeVehicle.equals(TRUCK_EVENT)) return VEHICLE_TRUCK;
			else if (typeVehicle.equals(TAXI_EVENT)) return VEHICLE_TAXI;
		}
		else if (agent instanceof C_Rodent) {
			if (((C_Rodent) agent).isSexualMature()) {
				if (((C_Rodent) agent).isPregnant()) return RATTUS_PREGNANT;
				else return RATTUS_MATURE;
			}
			else return NEWBORN;
		}
		return UNKNOWN;
	}

	public String getNameOfImageChize(I_SituatedThing agent) {
		C_Rodent rodent = (C_Rodent) agent;
		if (!rodent.isSexualMature()) {
			if (rodent.testMale()) return VOLE_MALE_CHILD;
			else return VOLE_FEMALE_CHILD;
		}
		else if (rodent.testMale()) return VOLE_MALE_ADULT;
		else if (rodent.isPregnant()) return VOLE_PREGNANT;
		return VOLE_FEMALE_ADULT;
	}
	public String getNameOfImageDodel(I_SituatedThing agent) {
		if (agent instanceof C_Rodent) {
			C_Rodent rodent = (C_Rodent) agent;
			if (!rodent.isSexualMature()) {
				if (rodent.testMale()) return MOUSE_MALE_CHILD;
				else return MOUSE_FEMALE_CHILD;
			}
			else if (rodent.testMale()) return MOUSE_MALE_ADULT;
			else if (rodent.isPregnant()) return MOUSE_PREGNANT;
			return MOUSE_FEMALE_ADULT;
		}
		else if (agent instanceof C_TaxiManDodel) return VEHICLE_TAXI_DODEL;
		// A_Protocol.event("C_IconSelector.getNameOfImageDodel", agent + " not displayable", isError);
		return null;
	}
	public String getNameOfImageDodel2(I_SituatedThing agent) {
		String imageName = this.getNameOfImageDodel(agent);
		if (imageName != null) {
			if (agent.getCurrentSoilCell() instanceof C_BurrowSystem) return BURROW;
			if ((agent instanceof C_Rodent) && ((C_Rodent) agent).getDesire().equals(HIDE)) return MOUSE_HIDE;

			if (agent instanceof C_Rodent && agent.isInfected()) {
				C_Rodent rodent = (C_Rodent) agent;
				if (rodent.isSexualMature()) if (rodent.testMale()) return INFECTED_MOUSE_MALE_ADULT;
				else return INFECTED_MOUSE_FEMALE_ADULT;
				else if (rodent.testMale()) return INFECTED_YOUNG_MOUSE_MALE;
				else return INFECTED_YOUNG_MOUSE_FEMALE;
			}
			return imageName;
		}
		if (agent instanceof C_Cat) {
			C_Cat oneCat = (C_Cat) agent;
			if (oneCat.isSexualMature()) if (oneCat.testMale()) return CAT_ADULT_MALE;
			else return CAT_ADULT_FEMALE;
			if (oneCat.isJuvenile()) if (oneCat.testMale()) return CAT_JUVENILE_MALE;
			else return CAT_JUVENILE_FEMALE;
			else if (oneCat.testMale()) return CAT_YOUNG_MALE;
			else return CAT_YOUNG_FEMALE;
		}
		if (agent instanceof A_Human) {
			if (((A_HumanUrban) agent).isa_Tag()) {//((C_HumanUrban) agent).getDesire().equals(WANDER)||
				((A_Human) agent).setHasToSwitchFace(true);
				return TAGGED;
			}
			if (!((A_Human) agent).isSexualMature()) {
				if (((A_Human) agent).testMale()) return BOY;
				return GIRL;
			}
			else if (((A_Human) agent).testMale()) return MAN;
			else if (((A_Human) agent).testFemale()) return WOMAN;
		}
		if (agent instanceof C_BurrowSystem) return BURROW;
		if (agent instanceof C_OrnitodorosSonrai) {
			if (((C_OrnitodorosSonrai) agent).getStasis().equals(ADULT)) {
				if (agent.isInfected()) return ORNITHODOROS_INFECTED;
				return ORNITHODOROS_ADULT;
			}
			if (((C_OrnitodorosSonrai) agent).getStasis().equals(NYMPH)) return ORNITHODOROS_NYMPH;
			if (((C_OrnitodorosSonrai) agent).getStasis().equals(LARVAE)) return ORNITHODOROS_LARVAE;
			if (((C_OrnitodorosSonrai) agent).getStasis().equals(EGG)) return ORNITHODOROS_EGG;
		}
		if (agent instanceof C_Food) return DODEL2_FOOD;
		return null;
	}

	public String getNameOfImageGerbil(I_SituatedThing agent) {
		if (agent instanceof C_Vegetation) {
			if (((C_Vegetation) agent).getGenome() instanceof C_GenomeBalanites) return SHRUBS_ICON;
			if (((C_Vegetation) agent).getGenome() instanceof C_GenomePoacea) return GRASSES_ICON;
			if (((C_Vegetation) agent).getGenome() instanceof C_GenomeFabacea) return CROPS_ICON;
			if (((C_Vegetation) agent).getGenome() instanceof C_GenomeAcacia) return TREES_ICON;
		}
		if (agent instanceof C_BarnOwl) {
			if (agent.getCurrentSoilCell() instanceof C_Nest) return NESTLE_ICON;
			return BARNOWL_ICON;
		}
		else if (agent instanceof C_RodentGerbil) {
			C_RodentGerbil rodent = (C_RodentGerbil) agent;
			if (rodent.getCurrentSoilCell() instanceof C_BurrowSystem) return GERBIL_UNDERGROUND;
			if (rodent.isHasToLeaveFullContainer()) return GERBIL_DISPERSE;
			if (rodent.getDesire().equals(HIDE)) return GERBIL_HIDE;
			if (!rodent.isSexualMature()) return GERBIL_IMMATURE;
			if (rodent.testMale()) return GERBIL_MALE;
			if (rodent.isPregnant()) return GERBIL_PREGNANT;
			else return GERBIL_FEMALE;
		}
		A_Protocol.event("C_IconSelector.getNameOfImageGerbil", "No icon found for " + agent, isError);
		return null;// PB in this case
	}

	/** Renvoie le nom de l'image à utiliser pour l'agent en paramètre */
	public String getNameOfImageEnclosMbour(I_SituatedThing agent) {
		if (agent instanceof C_Rodent) {
			C_Rodent rodent = (C_Rodent) agent;
			if (rodent.isPregnant()) return MASTO_PREGNANT;
			else if (rodent.getGenome().getClass() == C_GenomeMastoErythroleucus.class) return MASTO_ERYTHROLEUCUS;
			else if (rodent.getGenome().getClass() == C_GenomeMastoNatalensis.class) return MASTO_NATALENSIS;
			else
				if ((!rodent.getGenome().isHybrid()) && (rodent.getGenome().getClass() == C_GenomeMastomys.class))
					return MASTO_LAZARUS;
				else
					if (rodent.getGenome().isHybrid()) return MASTO_HYBRID;
					else return UNKNOWN;
		}
		else return UNKNOWN;
	}

	/** Définit la couleur de l'agent en paramètre en fonction de son sexe, de son âge, son état... (A utiliser avec l'affichage
	 * d'ellipses et non d'images)
	 * @return la nouvelle couleur de l'agent */
	public static Color getColor(I_SituatedThing agent) {
		if (C_Parameters.PROTOCOL.equals(CHIZE)) return getColorChize(agent);
		else if (C_Parameters.PROTOCOL.equals(GERBIL_PROTOCOL)) return getColorGerbil(agent);
		else if (C_Parameters.PROTOCOL.equals(ENCLOSURE)) return getColorMbour(agent);
		else if (C_Parameters.PROTOCOL.equals(CAGES)) return getColorMbour(agent);
		else if (C_Parameters.PROTOCOL.equals(HYBRID_UNIFORM)) return getColorMbour(agent);
		else if (C_Parameters.PROTOCOL.contains(CENTENAL)) return getColorTransportation(agent);
		else if (C_Parameters.PROTOCOL.equals(DECENAL)) return getColorTransportation(agent);
		else if (C_Parameters.PROTOCOL.equals(MUS_TRANSPORT)) return getColorTransportation(agent);
		else if (C_Parameters.PROTOCOL.equals(VILLAGE)) return getColorTransportation(agent);
		else if (C_Parameters.PROTOCOL.equals(BANDIA)) return getColorBandia(agent);
		else if (C_Parameters.PROTOCOL.equals(DODEL2)) return getColorDodel2(agent);
		else return getColorChize(agent);
	}

	public static Color getColorGerbil(I_SituatedThing agent) {
		Color rainColor = Color.black;
		if (agent instanceof C_SoilCellSavanna) {
			switch ((((C_SoilCellSavanna) agent)).getRainLevel()) {
				case 1 :
					rainColor = new Color(255, 255, 253);// >= 270 mm
					break;
				case 2 :
					rainColor = new Color(255, 255, 212);// 0 mm
					break;
				case 3 :
					rainColor = new Color(254, 254, 165);// 10 mm
					break;
				case 4 :
					rainColor = new Color(226, 254, 43);// 20-30 mm
					break;
				case 5 :
					rainColor = new Color(34, 254, 33);// 150-200
					break;
				case 6 :
					rainColor = new Color(2, 221, 221);// 210-260 mm
					break;
				case 7 :
					rainColor = new Color(35, 30, 252);// 40-80 mm
					break;
				case 8 :
					rainColor = new Color(190, 1, 254);// water or river
					break;
			}
		}
		/*
		 * if (agent instanceof C_SoilCellSavanna) { switch ((((C_SoilCellSavanna) agent)).getRain_Umm()) { case 0 : rainColor =
		 * new Color(0, 0, 0);// water or river break; case 1 : rainColor = new Color(34, 254, 33);// 90-140 mm break; case 2 :
		 * rainColor = new Color(226, 254, 43);// 40-80 mm break; case 3 : rainColor = new Color(35, 30, 252);// 210-260 mm break;
		 * case 4 : rainColor = new Color(2, 221, 221);// 150-200 break; case 5 : rainColor = new Color(254, 254, 165);// 20-30 mm
		 * break; case 6 : rainColor = new Color(255, 255, 212);// 10 mm break; case 7 : rainColor = new Color(255, 255, 253);// 0
		 * mm break; case 8: rainColor = new Color(190, 1, 254);// >= 270 mm break; default : rainColor = new Color(0, 0, 0);
		 * break; } }
		 */
		else if (agent instanceof C_Rodent) return getColorChize(agent);
		else if (agent instanceof C_BarnOwl) return new Color(0, 255, 0);
		else if (agent instanceof C_Vegetation) {
			if (((C_Vegetation) agent).getGenome() instanceof C_GenomeBalanites) return new Color(219, 149, 28);
			if (((C_Vegetation) agent).getGenome() instanceof C_GenomeFabacea) return new Color(40, 186, 70);
			if (((C_Vegetation) agent).getGenome() instanceof C_GenomePoacea) return new Color(140, 234, 159);
			if (((C_Vegetation) agent).getGenome() instanceof C_GenomeAcacia) return new Color(148, 178, 154);
		}
		return rainColor;
	}
	/** Définit la couleur de l'agent en paramètre en fonction de son sexe et de son âge (A utiliser avec l'affichage d'ellipses
	 * et non d'images)
	 * @return la nouvelle couleur de l'agent */
	public static Color getColorBandia(I_SituatedThing agent) {
		if (agent instanceof C_Trap) {
			if (((C_Trap) agent).getRodentList().size() > 0) return Color.black;
			else return Color.white;
		}
		else if (agent instanceof C_Rodent) return getColorChize(agent);
		else {
			System.err.println("C_SelecteurImage.getColorBandia() agent non reconnu: " + agent);
			return Color.green;
		}
	}
	public static Color getColorDodel2(I_SituatedThing agent) {
		Color oneColor = Color.black;
		if (agent instanceof C_Rodent) oneColor = getColorBandia(agent);
		else if (agent instanceof A_Human) {
			if (!((A_Human) agent).isSexualMature()) {
				if (((A_Human) agent).testMale()) oneColor = Color.yellow;
				else oneColor = Color.cyan;
			}
			else if (((A_Human) agent).testMale()) oneColor = Color.blue;
			else if (((A_Human) agent).testFemale()) oneColor = Color.green;
		}
		else if (agent instanceof C_OrnitodorosSonrai) {
			oneColor = Color.orange;
		}
		return oneColor;
	}
	public static Color getColorMbour(I_SituatedThing agent) {
		if (!(agent instanceof A_Animal)) {// TODO JLF 2019.02 remove (for debugging purpose)
			System.out.println();
		}
		Color couleur = Color.white;
		if (((A_Animal) agent).getGenome() instanceof C_GenomeMastoNatalensis) {
			if (((C_Rodent) agent).testMale()) couleur = Color.red;
			else couleur = Color.pink;
		}
		else if (((A_Animal) agent).getGenome() instanceof C_GenomeMastoErythroleucus) {
			if (((C_Rodent) agent).testMale()) couleur = Color.blue;
			else couleur = Color.cyan;
		}
		else if (((A_Animal) agent).getGenome().isHybrid()) {
			if (((C_Rodent) agent).testMale()) couleur = Color.darkGray;
			else couleur = Color.lightGray;
		}
		else couleur = Color.white;// not a species and not an hybrid -> introgressed
		if (((A_Animal) agent).isHasToLeaveFullContainer()) couleur = Color.black;
		if (((A_Amniote) agent).isPregnant()) couleur = Color.yellow;
		return couleur;
	}

	public static Color getColorTransportation(I_SituatedThing agent) {
		Color couleur = Color.white;
		if (agent instanceof C_HumanCarrier) {
			String typeVehicle = ((C_HumanCarrier) agent).getVehicle().getType();
			if (((C_HumanCarrier) agent).getVehicle().getLoad_Urodent() > 0) couleur = Color.red;
			else if (typeVehicle.equals(C_IconSelector.BOAT_EVENT)) couleur = Color.yellow;
			else if (typeVehicle.equals(C_IconSelector.TRAIN_EVENT)) couleur = Color.cyan;
			else if (typeVehicle.equals(C_IconSelector.TRUCK_EVENT)) couleur = Color.blue;
			else if (typeVehicle.equals(C_IconSelector.TAXI_EVENT)) couleur = Color.green;
		}
		else if (agent instanceof C_Rodent) {
			if (((C_Rodent) agent).isSexualMature()) couleur = Color.red;
			else couleur = Color.pink;
			if (((C_Rodent) agent).isPregnant()) couleur = Color.yellow;
		}
		else if (agent instanceof C_Market) {
			couleur = Color.pink;
		}
		if (agent instanceof A_NDS) if (((A_NDS) agent).isDead()) couleur = Color.black;
		return couleur;
	}
	/** Définit la couleur de l'agent en paramètre en fonction de son sexe et de son âge (A utiliser avec l'affichage d'ellipses
	 * et non d'images)
	 * @return la nouvelle couleur de l'agent */
	public static Color getColorChize(I_SituatedThing agent) {
		Color couleur = Color.gray;
		if (agent instanceof C_Rodent) {
			C_Rodent rodent = (C_Rodent) agent;
			if (rodent.hasEnteredDomain) return Color.green;
			if (!rodent.preMature && !rodent.isSexualMature()) return Color.gray;
			if (rodent.isPregnant()) return Color.yellow;
			if (rodent.isSexualMature()) if (rodent.testMale()) couleur = Color.blue;
			else couleur = Color.red;
			if (!rodent.isSexualMature()) if (rodent.testMale()) couleur = Color.cyan;
			else couleur = Color.pink;
			if (rodent.isHasToLeaveFullContainer()) return Color.black;
		}
		else if (agent instanceof C_BurrowSystem) {
			if (((C_BurrowSystem) agent).getLoad_Urodent() > 4) couleur = Color.black;
			else couleur = Color.green;
		}
		return couleur;
	}
}