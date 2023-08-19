package presentation.display;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import repast.simphony.valueLayer.ValueLayer;
import repast.simphony.visualizationOGL2D.ValueLayerStyleOGL;
import thing.ground.landscape.C_Landscape;
import data.C_Parameters;
import data.constants.I_ConstantNumeric;
import data.constants.I_ConstantString;

public class C_Style2dAffinityType implements ValueLayerStyleOGL, I_ConstantNumeric, I_ConstantString {
	protected ValueLayer layer;// the layer to represent

	Map<Integer, Color> colorMap;// the colormap is used to associate a color at each value of the layer
	public C_Style2dAffinityType() {
		// we try to get back the colormodel which should be read in the same time as the raster
	    this.colorMap = new HashMap<Integer, Color>();
	    this.colorMap = C_Landscape.getColormap();
		// if there is no colormap we create one
		if (this.colorMap == null) {
			System.out.print("C_Style2dAffinityType(): colormap not found; creating colormap");
			this.colorMap = new HashMap<Integer, Color>();
			if (C_Parameters.PROTOCOL.equals(CHIZE)) {
			    this.colorMap = colorMapChizeGrid(this.colorMap);
				System.out.print(" Chize");
			}
			else if (C_Parameters.PROTOCOL.contains(CENTENAL)) {
			    this.colorMap = colorMapCentenalGrid(this.colorMap);
				System.out.print(" Centenal");
			}
			else if (C_Parameters.PROTOCOL.equals(DECENAL)) {
			    this.colorMap = colorMapDecenalGrid(this.colorMap);
				System.out.print(" Decenal");
			}
			else if (C_Parameters.PROTOCOL.equals(MUS_TRANSPORT)) {
			    this.colorMap = colorMapDecenalGrid(this.colorMap);
				System.out.print(" Chize");
			}
			else if (C_Parameters.PROTOCOL.equals(DODEL)) {
			    this.colorMap = colorMapAfricanVillage(this.colorMap);
				System.out.print(" African village");
			}
			else if (C_Parameters.PROTOCOL.equals(DODEL2)) {
			    this.colorMap = colorMapDodel2(this.colorMap);
				System.out.print(" Dodel 2");
			}
			else if (C_Parameters.PROTOCOL.equals(BANDIA)) {
			    this.colorMap = colorMapBandia(this.colorMap);
				System.out.print(" Bandia");
			}
			else if (C_Parameters.PROTOCOL.equals(GERBIL_PROTOCOL)) {
			    this.colorMap = colorMapGerbilLandcover(this.colorMap);
				System.out.print(" Gerbil landcover");
			}
			else {
			    this.colorMap = colorMapChizeGrid(this.colorMap);
				System.out.print(" Chize");
			}
			System.out.println(": " + this.colorMap.size() + " colors identified");
			/** Elaborating a random colored map colorMap = colorMapPattern55(colorMap); // we associate at each index between 0
			 * and 255 a random color // ( we can equally decide to build our own colormap with specific // value. for (int i = 0;
			 * i < 256; i++) { colorMap.put(i, new Color( (int) (C_ContextCreator.randomGeneratorForDisplay.nextDouble() * 255),
			 * (int) (C_ContextCreator.randomGeneratorForDisplay.nextDouble() * 255), (int)
			 * (C_ContextCreator.randomGeneratorForDisplay.nextDouble() * 255))); } */
		}
		else {
			// colorMap = colorMapChizeGrid(colorMap);
			// System.out.println(colorMap);
		}
	}
	@Override
	/** Return the necessary size for the display */
	public float getCellSize() {
		return cellSize.get(0);
	}
	public Map<Integer, Color> colorMapDodel2(Map<Integer, Color> colorMap) {
		colorMap = new HashMap<Integer, Color>();
		// R G B red green blue
		colorMap.put(0, new Color(128, 128, 128)); //  Road
		colorMap.put(1, new Color(193, 138, 108)); // Track
		colorMap.put(2, new Color(243, 240, 233)); // Street
		colorMap.put(3, new Color(0, 0, 0)); // wall
		colorMap.put(4, new Color(221, 239, 247)); // Concession
		colorMap.put(5, new Color(96, 213, 225)); // Corridor
		colorMap.put(6, new Color(214, 214, 173)); // Market
		colorMap.put(7, new Color(33, 182, 52)); // Room
		colorMap.put(8, new Color(232, 151, 40)); // workShop
		colorMap.put(9, new Color(253, 168, 193)); // RoomFood
		colorMap.put(10, new Color(25, 140, 40)); // Food store :255, 40, 40
		colorMap.put(11, new Color(255, 255, 0)); // HouseDoor : 
        colorMap.put(12, new Color(255, 0, 255)); // RoomDoor 
        colorMap.put(13, new Color(141, 182, 20)); // Enclosure
        colorMap.put(14, new Color(255, 0, 0)); // Thialaga
        colorMap.put(15, new Color(255, 0, 0)); // Diomandou
        colorMap.put(16, new Color(255, 0, 0)); // Diery Diouga
        colorMap.put(17, new Color(255, 0, 0)); // Kogga Walo
        colorMap.put(18, new Color(0, 91, 255)); // School
        colorMap.put(19, new Color(255, 0, 0)); // Dodel interior
        colorMap.put(20, new Color(255, 0, 0)); // Medina Dodel
        colorMap.put(21, new Color(91, 255, 91)); // FIELD
        colorMap.put(22, new Color(124, 116, 231)); // MOSQUE
        colorMap.put(23, new Color(13, 255, 114)); // BAKERY
		return colorMap;
	}
	public Map<Integer, Color> colorMapDodel2_old(Map<Integer, Color> colorMap) {
		colorMap = new HashMap<Integer, Color>();
		// R G B red green blue
		colorMap.put(0, new Color(243, 240, 233)); //  ground cell
		colorMap.put(1, new Color(155, 155, 155)); // workshop
		colorMap.put(2, new Color(255, 204, 153)); // Bakery
		colorMap.put(3, new Color(255, 60, 255)); // Shop
		colorMap.put(4, new Color(255, 245, 24)); // Office
		colorMap.put(5, new Color(243, 240, 233)); // Hut 242, 226, 9
		colorMap.put(6, new Color(237, 180, 23)); // Room
		colorMap.put(7, new Color(0, 0, 0)); // Hairdresser 255, 0, 0
		colorMap.put(8, new Color(197, 216, 21)); // Kitchen
		colorMap.put(9, new Color(197, 216, 21)); // Dibiterien
		colorMap.put(10, new Color(0, 255, 64)); // Enclosure
		colorMap.put(11, new Color(242, 255, 99)); // Garage
		colorMap.put(12, new Color(17, 178, 151)); // Garden
		colorMap.put(13, new Color(103, 200, 255)); // Laboratory
		colorMap.put(14, new Color(0, 128, 128)); // Magazin
		colorMap.put(15, new Color(0, 251, 251)); // Mosque
		colorMap.put(16, new Color(255, 0, 0)); // Mill
		colorMap.put(17, new Color(247, 210, 240)); // Hardware store
		colorMap.put(18, new Color(0, 200, 0)); // Restaurant
		colorMap.put(19, new Color(128, 64, 64)); // Ruined
		colorMap.put(20, new Color(0, 0, 255)); // Class
		colorMap.put(21, new Color(255, 128, 64)); // Living room
		colorMap.put(22, new Color(184, 96, 37)); // Tangana
		colorMap.put(23, new Color(0, 128, 255)); // Bulding
		colorMap.put(24, new Color(0, 0, 0)); // Wall
		colorMap.put(25, new Color(221, 239, 247)); // House
		colorMap.put(26, new Color(180, 177, 92)); // Market
		colorMap.put(27, new Color(0, 0, 128)); // National Road
		colorMap.put(28, new Color(128, 64, 64)); // Track
		return colorMap;
	}
	public Map<Integer, Color> colorMapCentenalGrid(Map<Integer, Color> colorMap) {
		colorMap = new HashMap<Integer, Color>();
		// R G B red green blue
		colorMap.put(-1, new Color(29, 29, 165)); //  Border, Ocean, and Abroad
		colorMap.put(0, new Color(29, 29, 165)); //  Border, Ocean, and Abroad
		colorMap.put(1, new Color(155, 155, 155)); // ROAD (black)
		colorMap.put(2, new Color(255, 204, 153)); // Ferlo
		colorMap.put(3, new Color(255, 255, 50)); // Grande côte and delta
		colorMap.put(4, new Color(255, 245, 24)); // Soudanien
		colorMap.put(5, new Color(242, 226, 9)); // Sine
		colorMap.put(6, new Color(237, 180, 23)); // Terres neuves and zone cotonnière
		colorMap.put(7, new Color(255, 0, 0)); // CITY (red)
		colorMap.put(9, new Color(197, 216, 21)); // Haute Casamance and Sénégal Oriental
		colorMap.put(10, new Color(245, 245, 150)); // Niayes
		colorMap.put(11, new Color(242, 255, 99)); // Saloum
		colorMap.put(12, new Color(17, 178, 151)); // Basse-Casamance
		colorMap.put(13, new Color(103, 200, 255)); // RIVER
		return colorMap;
	}
	public Map<Integer, Color> colorMapChizeGrid(Map<Integer, Color> colorMap) {
		colorMap = new HashMap<Integer, Color>();
		colorMap.put(0, new Color(8, 99, 120));
		colorMap.put(1, new Color(204, 200, 206));
		colorMap.put(2, new Color(153, 102, 51));
		colorMap.put(3, new Color(173, 144, 96));
		colorMap.put(4, new Color(255, 255, 0));// formerly : 145, 145, 104
		colorMap.put(5, new Color(250, 197, 0));// formerly : 0, 102, 51;
		colorMap.put(6, new Color(211, 238, 207));
		colorMap.put(7, new Color(65, 138, 0));
		colorMap.put(8, new Color(51, 153, 153));
		colorMap.put(9, new Color(0, 199, 102));
		colorMap.put(BLACK_MAP_COLOR, new Color(0, 0, 0));
		return colorMap;
	}
	public Map<Integer, Color> colorMapGerbilLandcover(Map<Integer, Color> colorMap) {
		colorMap = new HashMap<Integer, Color>();
		colorMap.put(0, new Color(115, 178, 255));// water for rain
		colorMap.put(1, new Color(255, 255, 253));// / 0 mm
		colorMap.put(2, new Color(255, 255, 212));// 10 mm
		colorMap.put(3, new Color(254, 254, 165));// 20-30 mm
		colorMap.put(4, new Color(226, 254, 43));// 40-80 mm
		colorMap.put(5, new Color(34, 254, 33));// 90-140 mm
		colorMap.put(6, new Color(2, 221, 221));// 150-200 mm
		colorMap.put(7, new Color(35, 30, 252));// 210-260 mm
		colorMap.put(8, new Color(190, 1, 254));// >= 270 mm
		colorMap.put(17, new Color(115, 178, 255));// water for lancover
		colorMap.put(18, new Color(255, 130, 80));// Trees & Shrubs
		colorMap.put(36, new Color(160, 200, 160));// trees & Crops
		colorMap.put(37, new Color(160, 82, 45));// Shrubs
		colorMap.put(38, new Color(225, 200, 50));// Shrubs & Grasses
		colorMap.put(39, new Color(160, 200, 160));// Shrubs & Crops //TODO JLF&MS 2020.04 color repeated (see 42)
		colorMap.put(40, new Color(230, 200, 140));// Shrubs & Barren
		colorMap.put(41, new Color(238, 238, 000));// Grasses
		colorMap.put(42, new Color(160, 200, 160));// Grasses & crops
		colorMap.put(43, new Color(238, 238, 000));// Grasses & Barren
		colorMap.put(44, new Color(200, 160, 160));// Crops
		colorMap.put(45, new Color(238, 236, 180));// Barren
		colorMap.put(BLACK_MAP_COLOR, new Color(0, 0, 0));
		return colorMap;
	}
	public Map<Integer, Color> colorMapDecenalGrid(Map<Integer, Color> colorMap) {
		colorMap = new HashMap<Integer, Color>();
		colorMap.put(0, new Color(8, 99, 120));
		colorMap.put(1, new Color(204, 200, 206));
		colorMap.put(2, new Color(153, 102, 51));
		colorMap.put(3, new Color(173, 144, 96));
		colorMap.put(4, new Color(255, 255, 0));
		colorMap.put(5, new Color(250, 197, 0));
		colorMap.put(6, new Color(211, 238, 207));
		colorMap.put(7, new Color(65, 138, 0));
		colorMap.put(8, new Color(51, 153, 153));
		colorMap.put(9, new Color(0, 199, 102));
		colorMap.put(10, new Color(214, 163, 60));
		colorMap.put(13, new Color(0, 0, 0));
		return colorMap;
	}
	public Map<Integer, Color> colorMapAfricanVillage(Map<Integer, Color> colorMap) {
		colorMap = new HashMap<Integer, Color>();
		colorMap.put(0, new Color(94, 8, 7)); // road
		colorMap.put(1, new Color(162, 6, 4)); // road
		colorMap.put(2, new Color(234, 3, 1));// street
		colorMap.put(3, new Color(255, 71, 0));// brown
		colorMap.put(4, new Color(255, 131, 0)); // tree
		colorMap.put(5, new Color(255, 145, 0));
		colorMap.put(6, new Color(255, 174, 0));// concession
		colorMap.put(7, new Color(255, 200, 0));
		colorMap.put(8, new Color(255, 228, 0));// room brown:(153, 102, 51)
		colorMap.put(9, new Color(255, 241, 0));// market
		colorMap.put(BLACK_MAP_COLOR, new Color(0, 0, 0));
		return colorMap;
	}
	public Map<Integer, Color> colorMapBandia(Map<Integer, Color> colorMap) {
		colorMap = new HashMap<Integer, Color>();
		colorMap.put(0, new Color(90, 97, 117));
		colorMap.put(1, new Color(232, 249, 250));
		colorMap.put(4, new Color(177, 162, 164));
		colorMap.put(5, new Color(101, 89, 89));
		colorMap.put(6, new Color(51, 153, 102)); // homogeneous field
		colorMap.put(BLACK_MAP_COLOR, new Color(0, 0, 0));
		return colorMap;
	}
	@Override
	public Color getColor(double... coordinates) {
		return this.colorMap.get((int) this.layer.get(coordinates));
	}
	@Override
	public void init(ValueLayer layer) {
		this.layer = layer;
	}
}
