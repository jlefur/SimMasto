package presentation.display;

import java.awt.Color;
import java.awt.Font;

import data.C_Parameters;
import data.constants.I_ConstantGerbil;
import data.constants.I_ConstantImagesNames;
import data.constants.I_ConstantNumeric;
import data.constants.I_ConstantString;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.visualizationOGL2D.StyleOGL2D;
import saf.v3d.ShapeFactory2D;
import saf.v3d.scene.Position;
import saf.v3d.scene.VSpatial;
import simmasto0.C_ContextCreator;
import thing.A_Amniote;
import thing.A_Animal;
import thing.A_NDS;
import thing.A_VisibleAgent;
import thing.A_HumanUrban;
import thing.C_Vegetation;
import thing.I_SituatedThing;

/** Style des agents "animaux". Définit une icône ou une ellipse pour chaque agent au lancement de la simulation en fonction de
 * son sexe et la fait varier suivant son âge.
 * @author A Realini 2011 */
public class C_StyleAgent implements StyleOGL2D<I_SituatedThing>, I_ConstantString, I_ConstantNumeric,
		I_ConstantImagesNames {
	private float imageScale = IMAGE_SCALE; // Taille d'une image initiale .15
	// Ellipse scales (nb: 50x50-> .3)
	private float ELLIPSE_SCALE = 1.5f;
	private final float CIRCLE_RADIUS = 5; // Rayon de l'ellipse
	private final int CIRCLE_SLICES = 10; // Nombres d’arêtes de l’ellipse (joue sur le rendu : sera plus ou moins rond)
	private C_IconSelector selectImg;
	private ShapeFactory2D factory;

	/** Initialise un gestionnaire d'images et enregistre les images qui seront utilisées au cours de la simulation dans le
	 * factory */
	public void init(ShapeFactory2D factory) {
		this.factory = factory;
		selectImg = new C_IconSelector();
		if (C_Parameters.PROTOCOL.equals(CHIZE)) initChize();
		else if (C_Parameters.PROTOCOL.equals(ENCLOSURE)) initEnclosMbour();
		else if (C_Parameters.PROTOCOL.equals(DODEL)) initDodel();
		else if (C_Parameters.PROTOCOL.equals(CAGES)) initEnclosMbour();
		else if (C_Parameters.PROTOCOL.equals(HYBRID_UNIFORM)) initEnclosMbour();
		else if (C_Parameters.PROTOCOL.contains(CENTENAL)) initCentenal();
		else if (C_Parameters.PROTOCOL.equals(DECENAL)) initDecenal();
		else if (C_Parameters.PROTOCOL.equals(MUS_TRANSPORT)) initMusTransport();
		else if (C_Parameters.PROTOCOL.equals(GERBIL_PROTOCOL)) initGerbil();
		else if (C_Parameters.PROTOCOL.equals(BANDIA)) initBandia();
		else if (C_Parameters.PROTOCOL.equals(DODEL2)) initDodel2();
		C_ContextCreator.protocol.setStyleAgent(this);
	}

	public void initChize() {
		this.ELLIPSE_SCALE = .6f;
		factory.registerImage(VOLE_FEMALE_CHILD, selectImg.loadImage(VOLE_FEMALE_CHILD));
		factory.registerImage(VOLE_FEMALE_ADULT, selectImg.loadImage(VOLE_FEMALE_ADULT));
		factory.registerImage(VOLE_MALE_CHILD, selectImg.loadImage(VOLE_MALE_CHILD));
		factory.registerImage(VOLE_MALE_ADULT, selectImg.loadImage(VOLE_MALE_ADULT));
		factory.registerImage(VOLE_PREGNANT, selectImg.loadImage(VOLE_PREGNANT));
	}
	public void initDodel() {
		this.ELLIPSE_SCALE = 1.4f;
		this.imageScale = .3f;
		factory.registerImage(MOUSE_FEMALE_CHILD, selectImg.loadImage(MOUSE_FEMALE_CHILD));
		factory.registerImage(MOUSE_FEMALE_ADULT, selectImg.loadImage(MOUSE_FEMALE_ADULT));
		factory.registerImage(MOUSE_MALE_CHILD, selectImg.loadImage(MOUSE_MALE_CHILD));
		factory.registerImage(MOUSE_MALE_ADULT, selectImg.loadImage(MOUSE_MALE_ADULT));
		factory.registerImage(MOUSE_PREGNANT, selectImg.loadImage(MOUSE_PREGNANT));
		factory.registerImage(MOUSE_DISPERSE, selectImg.loadImage(MOUSE_DISPERSE));
		factory.registerImage(VEHICLE_TAXI_DODEL, selectImg.loadImage(VEHICLE_TAXI_DODEL));
		factory.registerImage(DAY, selectImg.loadImage(DAY));
		factory.registerImage(NIGHT, selectImg.loadImage(NIGHT));
		factory.registerImage(DAWN, selectImg.loadImage(DAWN));
		factory.registerImage(TWILIGHT, selectImg.loadImage(TWILIGHT));
	}
	public void initDodel2() {
		initDodel();
		this.ELLIPSE_SCALE = 2.f;
		this.imageScale = .8f;
		factory.registerImage(BURROW, selectImg.loadImage(BURROW));
		factory.registerImage(MAN, selectImg.loadImage(MAN));
		factory.registerImage(TAGGED, selectImg.loadImage(TAGGED));
		factory.registerImage(WOMAN, selectImg.loadImage(WOMAN));
		factory.registerImage(BOY, selectImg.loadImage(BOY));
		factory.registerImage(GIRL, selectImg.loadImage(GIRL));
		factory.registerImage(CHILD, selectImg.loadImage(CHILD));
		factory.registerImage(ORNITHODOROS_ADULT, selectImg.loadImage(ORNITHODOROS_ADULT));
		factory.registerImage(ORNITHODOROS_NYMPH, selectImg.loadImage(ORNITHODOROS_NYMPH));
		factory.registerImage(ORNITHODOROS_LARVAE, selectImg.loadImage(ORNITHODOROS_LARVAE));
		factory.registerImage(ORNITHODOROS_EGG, selectImg.loadImage(ORNITHODOROS_EGG));
		factory.registerImage(INFECTED_MOUSE_MALE_ADULT, selectImg.loadImage(INFECTED_MOUSE_MALE_ADULT));
		factory.registerImage(INFECTED_MOUSE_FEMALE_ADULT, selectImg.loadImage(INFECTED_MOUSE_FEMALE_ADULT));
		factory.registerImage(INFECTED_YOUNG_MOUSE_MALE, selectImg.loadImage(INFECTED_YOUNG_MOUSE_MALE));
		factory.registerImage(INFECTED_YOUNG_MOUSE_FEMALE, selectImg.loadImage(INFECTED_YOUNG_MOUSE_FEMALE));
		factory.registerImage(ORNITHODOROS_INFECTED, selectImg.loadImage(ORNITHODOROS_INFECTED));
		factory.registerImage(DODEL2_FOOD, selectImg.loadImage(DODEL2_FOOD));
		factory.registerImage(MOUSE_HIDE, selectImg.loadImage(MOUSE_HIDE));
		factory.registerImage(CAT_ADULT_MALE, selectImg.loadImage(CAT_ADULT_MALE));
		factory.registerImage(CAT_ADULT_FEMALE, selectImg.loadImage(CAT_ADULT_FEMALE));
		factory.registerImage(CAT_JUVENILE_MALE, selectImg.loadImage(CAT_JUVENILE_MALE));
		factory.registerImage(CAT_JUVENILE_FEMALE, selectImg.loadImage(CAT_JUVENILE_FEMALE));
		factory.registerImage(CAT_YOUNG_MALE, selectImg.loadImage(CAT_YOUNG_MALE));
		factory.registerImage(CAT_YOUNG_FEMALE, selectImg.loadImage(CAT_YOUNG_FEMALE));
	}
	public void initGerbil() {
		this.ELLIPSE_SCALE = 1.f;
		// Change image scale depending on the map resolution JLF 03.2018
		float scaleForIcon = .2f;
		switch (((String) RunEnvironment.getInstance().getParameters().getValue("RASTER_FILE")).toLowerCase()) {
			case "zoom4" :
			case "zoom3" :
				scaleForIcon = .25f;
				break;
			case "me" :
				scaleForIcon = 16.f;
				break;
			case "pe" :
				scaleForIcon = 6.f;
				this.ELLIPSE_SCALE = 8.f;
				break;
			case "zoom1" :// lac de Guiers
				this.ELLIPSE_SCALE = 15.f;
				scaleForIcon = 1.5f;
				break;
			case "zoom2" :
				this.ELLIPSE_SCALE = 8.f;
				scaleForIcon = 3.f;
				break;
		}
		this.imageScale = scaleForIcon;
		factory.registerImage(GERBIL_MALE, selectImg.loadImage(GERBIL_MALE));
		factory.registerImage(GERBIL_FEMALE, selectImg.loadImage(GERBIL_FEMALE));
		factory.registerImage(GERBIL_PREGNANT, selectImg.loadImage(GERBIL_PREGNANT));
		factory.registerImage(GERBIL_IMMATURE, selectImg.loadImage(GERBIL_IMMATURE));
		factory.registerImage(GERBIL_DISPERSE, selectImg.loadImage(GERBIL_DISPERSE));
		factory.registerImage(GERBIL_HIDE, selectImg.loadImage(GERBIL_HIDE));
		factory.registerImage(GERBIL_UNDERGROUND, selectImg.loadImage(GERBIL_UNDERGROUND));
		factory.registerImage(BARNOWL_ICON, selectImg.loadImage(BARNOWL_ICON));
		factory.registerImage(SHRUBS_ICON, selectImg.loadImage(SHRUBS_ICON));
		factory.registerImage(TREES_ICON, selectImg.loadImage(TREES_ICON));
		factory.registerImage(GRASSES_ICON, selectImg.loadImage(GRASSES_ICON));
		factory.registerImage(BARREN_ICON, selectImg.loadImage(BARREN_ICON));
		factory.registerImage(CROPS_ICON, selectImg.loadImage(CROPS_ICON));
		factory.registerImage(DAY, selectImg.loadImage(DAY));
		factory.registerImage(NIGHT, selectImg.loadImage(NIGHT));
		factory.registerImage(DAWN, selectImg.loadImage(DAWN));
		factory.registerImage(TWILIGHT, selectImg.loadImage(TWILIGHT));
	}
	public void initCentenal() {
		this.ELLIPSE_SCALE = 2.4f;
		this.imageScale = 0.2f;
		factory.registerImage(VEHICLE_BOAT, selectImg.loadImage(VEHICLE_BOAT));
		factory.registerImage(VEHICLE_TRAIN, selectImg.loadImage(VEHICLE_TRAIN));
		factory.registerImage(VEHICLE_TRUCK, selectImg.loadImage(VEHICLE_TRUCK));
		factory.registerImage(VEHICLE_CAR, selectImg.loadImage(VEHICLE_CAR));
		factory.registerImage(RATTUS_MATURE, selectImg.loadImage(RATTUS_MATURE));
		factory.registerImage(NEWBORN, selectImg.loadImage(NEWBORN));
		factory.registerImage(RATTUS_PREGNANT, selectImg.loadImage(RATTUS_PREGNANT));
		factory.registerImage(VEHICLE_LOADED, selectImg.loadImage(VEHICLE_LOADED));
		factory.registerImage(VEHICLE_PARKED, selectImg.loadImage(VEHICLE_PARKED));
	}
	public void initMusTransport() {
		this.ELLIPSE_SCALE = 3.5f;
		this.imageScale = 1.2f;
		factory.registerImage(VEHICLE_BOAT, selectImg.loadImage(VEHICLE_BOAT));
		factory.registerImage(VEHICLE_TRAIN, selectImg.loadImage(VEHICLE_TRAIN));
		factory.registerImage(VEHICLE_TRUCK, selectImg.loadImage(VEHICLE_TRUCK));
		factory.registerImage(VEHICLE_TAXI, selectImg.loadImage(VEHICLE_TAXI));
		factory.registerImage(MUS_IMAGE, selectImg.loadImage(MUS_IMAGE));
		factory.registerImage(RATTUS_MATURE, selectImg.loadImage(RATTUS_MATURE));
		factory.registerImage(NEWBORN, selectImg.loadImage(NEWBORN));
		factory.registerImage(RATTUS_PREGNANT, selectImg.loadImage(RATTUS_PREGNANT));
		factory.registerImage(MUS_PREGNANT, selectImg.loadImage(MUS_PREGNANT));
		factory.registerImage(MASTO_ERY_IMAGE, selectImg.loadImage(MASTO_ERY_IMAGE));
		factory.registerImage(VEHICLE_LOADED, selectImg.loadImage(VEHICLE_LOADED));
		factory.registerImage(VEHICLE_PARKED, selectImg.loadImage(VEHICLE_PARKED));
		factory.registerImage(MUSTDIE, selectImg.loadImage(MUSTDIE));
	}
	public void initDecenal() {
		this.ELLIPSE_SCALE = 2.6f;
		imageScale = 0.4f;
		factory.registerImage(VEHICLE_BOAT, selectImg.loadImage(VEHICLE_BOAT));
		factory.registerImage(VEHICLE_TRAIN, selectImg.loadImage(VEHICLE_TRAIN));
		factory.registerImage(VEHICLE_TRUCK, selectImg.loadImage(VEHICLE_TRUCK));
		factory.registerImage(VEHICLE_TAXI, selectImg.loadImage(VEHICLE_TAXI));
		factory.registerImage(RATTUS_MATURE, selectImg.loadImage(RATTUS_MATURE));
		factory.registerImage(NEWBORN, selectImg.loadImage(NEWBORN));
		factory.registerImage(VEHICLE_LOADED, selectImg.loadImage(VEHICLE_LOADED));
		factory.registerImage(VEHICLE_PARKED, selectImg.loadImage(VEHICLE_PARKED));
	}
	public void initEnclosMbour() {
		this.imageScale = .15f;
		this.ELLIPSE_SCALE = 1.5f;
		factory.registerImage(MASTO_ERYTHROLEUCUS, selectImg.loadImage(MASTO_ERYTHROLEUCUS));
		factory.registerImage(MASTO_NATALENSIS, selectImg.loadImage(MASTO_NATALENSIS));
		factory.registerImage(MASTO_LAZARUS, selectImg.loadImage(MASTO_LAZARUS));
		factory.registerImage(MASTO_HYBRID, selectImg.loadImage(MASTO_HYBRID));
		factory.registerImage(MASTO_PREGNANT, selectImg.loadImage(MASTO_PREGNANT));
		factory.registerImage(UNKNOWN, selectImg.loadImage(UNKNOWN));
	}

	public void initBandia() {
		this.ELLIPSE_SCALE = 2.f;
		this.imageScale = .6f;
		factory.registerImage(MASTO_MALE, selectImg.loadImage(MASTO_MALE));
		factory.registerImage(MASTO_FEMALE, selectImg.loadImage(MASTO_FEMALE));
		factory.registerImage(MASTO_PREGNANT, selectImg.loadImage(MASTO_PREGNANT));
		factory.registerImage(MASTO_JUVENILE, selectImg.loadImage(MASTO_JUVENILE));
		factory.registerImage(MASTO_DISPERSE, selectImg.loadImage(MASTO_DISPERSE));
		factory.registerImage(MASTO_UNDERGROUND, selectImg.loadImage(MASTO_UNDERGROUND));
		factory.registerImage(LOADED_TRAP, selectImg.loadImage(LOADED_TRAP));
		factory.registerImage(EMPTY_TRAP, selectImg.loadImage(EMPTY_TRAP));
	}

	/** Attribue une nouvelle "image" à un agent ou la modifie si besoin est, sinon renvoie le spatial en paramètre sans le
	 * modifier.
	 * @param agent : l'agent à qui appartient l'icône
	 * @param spatial : représentation de l'agent (image ou forme géométrique) */
	@Override
	public VSpatial getVSpatial(I_SituatedThing agent, VSpatial spatial) {
		if (agent instanceof C_Background) {
			if (((C_Background) agent).hasToSwitchFace || spatial == null)
				spatial = factory.getNamedSpatial(selectImg.getNameOfImage(agent));
		}
		else if (((A_VisibleAgent) agent).hasToSwitchFace || spatial == null) {
			if (C_Parameters.IMAGE) spatial = factory.getNamedSpatial(selectImg.getNameOfImage(agent));
			else {
				if (agent instanceof C_Vegetation) {
					spatial = factory.createCircle(CIRCLE_RADIUS * 0.5f, 3);// triangle shape
				} // mature=circle, immature=square
				else
					if (agent instanceof A_Amniote && !((A_Amniote) agent).isSexualMature())
						spatial = factory.createRectangle((int) CIRCLE_RADIUS, (int) CIRCLE_RADIUS);
				else
						spatial = factory.createCircle(CIRCLE_RADIUS, CIRCLE_SLICES);

				if (agent instanceof A_Animal && ((A_Animal) agent).isHasToLeaveFullContainer()) {
					if (agent instanceof A_Amniote && !((A_Amniote) agent).isSexualMature())
						spatial = factory.createCircle(CIRCLE_RADIUS * 1.5f, 3);
					else
						spatial = factory.createCircle(CIRCLE_RADIUS * 2.f, 3);// triangle shape
				}
				if (agent instanceof A_Animal && ((A_Animal) agent).hasEnteredDomain) {
					spatial = factory.createCircle(CIRCLE_RADIUS * 2.f, 4);// diamond shape
					// TODO number in source 2018.05 JLF for displaying entering rodents
					if (Math.random() > .98) ((A_Animal) agent).hasEnteredDomain = false;
				}
			}
			((A_VisibleAgent) agent).hasToSwitchFace = false;
		}
		return spatial;
	}

	@Override
	public Color getColor(I_SituatedThing agent) {
		if (C_Parameters.IMAGE) return Color.white; // the color is not important
		else return C_IconSelector.getColor(agent);
	}

	@Override
	/** getscale modified only for vegetation, rev. M.Sall 03.2016 */
	public float getScale(I_SituatedThing object) {
		if (object instanceof C_Vegetation) {
			// TODO number in source OK JLF 2021.03 compute mean
			double energy = I_ConstantGerbil.INITIAL_VEGET_ENERGY + ((C_Vegetation) object).getEnergy_Ukcal() / 2;
			if (C_Parameters.IMAGE) // TODO number in source 2016.03 MS, 2021.02 JLF
				return (float) (energy * .005);
			else
				return (float) (energy * .1);
		}
		if (C_Parameters.IMAGE) {
			if (object instanceof A_HumanUrban) {
				if (((A_HumanUrban) object).isa_Tag()) return this.imageScale * 5;
				if (!((A_Animal) object).getDesire().equals(REST)) return this.imageScale * 2;// TODO number in source JLF 2021.07
																								// taille humains
				else return this.imageScale / 2;
			}
			else return this.imageScale;
		}
		// Show (badly) the relative importance of agents sensing
		// else if (object instanceof A_Animal) return (float) (this.ELLIPSE_SCALE * ((A_Animal) object).getSensing_UmeterByTick()
		// / 10.);
		else return this.ELLIPSE_SCALE;
	}
	// OVERRIDEN & UNUSED METHODS //
	public float getRotation(I_SituatedThing object) {
		return 0;
	}
	public int getBorderSize(I_SituatedThing object) {
		return 0;
	}
	public Color getBorderColor(I_SituatedThing object) {
		return null;
	}
	public String getLabel(I_SituatedThing object) {
		if (C_Parameters.VERBOSE) {
			if (object instanceof A_Animal)
				return ((A_NDS) object).retrieveId() + "/" + ((A_Animal) object).getCell1Target();
			if (object instanceof A_NDS) return ((A_NDS) object).retrieveId();
			else return null;
		}
		else return " ";// Does not work if return null JLF 09.2017
	}
	public Font getLabelFont(I_SituatedThing object) {
		return new Font("lucida sans", Font.PLAIN, 12);// JLF 03.2018 font style does not seem to have effect
	}
	public float getLabelXOffset(I_SituatedThing object) {
		return 0;
	}
	public float getLabelYOffset(I_SituatedThing object) {
		return 0;
	}
	public ShapeFactory2D getFactory() {
		return factory;
	}
	public Position getLabelPosition(I_SituatedThing object) {
		return Position.SOUTH;
	}
	public Color getLabelColor(I_SituatedThing object) {
		// TODO number(/data) in source JLF 2019.02 : desires list
		String desires = "/FORAGE/FEED/REST/REPRODUCTION/SUCKLE/HIDE/NONE/WANDER/";
		if ((object instanceof A_Animal) && desires.contains(((A_Animal) object).getCell1Target())) return Color.RED;
		else return Color.BLUE;
	}
}
