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
import data.constants.I_ConstantTransportation;
public class C_Style2dGroundType implements ValueLayerStyleOGL, I_ConstantNumeric, I_ConstantString, I_ConstantTransportation {
	//
	// FIELDS
	//
	protected ValueLayer layer; // the layer to represent
	Map<Integer, Color> colorMap;// Used to associate a color to each value of the layer
	//
	// CONSTRUCTOR
	//
	public C_Style2dGroundType() {// TODO JLF 2014.11 rename into protocolColorMapFactory
		// we try to get back the colormodel which should be read in the same time as the raster
	    this.colorMap = new HashMap<Integer, Color>();
	    this.colorMap = C_Landscape.getColormap();
		// if there is no colormap, create one
		if (this.colorMap == null) {
			System.out.print("C_Style2dGroundType(): creating ");
			this.colorMap = new HashMap<Integer, Color>();
			switch (C_Parameters.PROTOCOL) {
				case CENTENAL :
				case DECENAL :
				case VILLAGE :
				    this.colorMap = colorMapCentenalGrid(this.colorMap);
					System.out.println("centenal colormap: " + this.colorMap.size() + " colors identified");
					break;
				case MUS_TRANSPORT :
				    this.colorMap = colorMapMusTransportGrid(this.colorMap);
					System.out.println("mus transportation colormap: " + this.colorMap.size() + " colors identified");
					break;
				default :
					break;
			}

			/** Elaborating a random colored map colorMap = colorMapPattern55(colorMap); // we associate at each index between 0 and
			 * 255 a random color // ( we can equally decide to build our own colormap with specific // value. for (int i = 0; i <
			 * 256; i++) { colorMap.put(i, new Color( (int) (C_ContextCreator.randomGeneratorForDisplay.nextDouble() * 255), (int)
			 * (C_ContextCreator.randomGeneratorForDisplay.nextDouble() * 255), (int)
			 * (C_ContextCreator.randomGeneratorForDisplay.nextDouble() * 255))); } */
		}
	}
	//
	// METHODS
	//
	@Override
	public void init(ValueLayer layer) {
		this.layer = layer;
	}
	public Map<Integer, Color> colorMapCentenalGrid(Map<Integer, Color> colorMap) {
		colorMap = new HashMap<Integer, Color>();
		// R G B red green blue
		colorMap.put(GROUND_TYPE_CODES.get(CITY_EVENT), new Color(192, 172, 62)); // CITY (red)
		colorMap.put(GROUND_TYPE_CODES.get(TOWN_EVENT), new Color(172, 152, 42)); // CITY (red)
		colorMap.put(GROUND_TYPE_CODES.get(RAIL_EVENT), new Color(200, 200, 200)); // RAIL (noir un peu moins foncé)
		colorMap.put(GROUND_TYPE_CODES.get(RIVER_EVENT), new Color(103, 200, 255)); // RIVER
		colorMap.put(GROUND_TYPE_CODES.get(ROAD_EVENT), new Color(255, 0, 0)); // ROAD (gris foncé)
		colorMap.put(GROUND_TYPE_CODES.get(GOOD_TRACK_EVENT), new Color(0,200,0)); // good track (marron foncé)
		colorMap.put(GROUND_TYPE_CODES.get(TRACK_EVENT), new Color(160, 114, 19)); // TRACK piste (marron jaune)
		colorMap.put(GROUND_TYPE_CODES.get(GNT_WEAK_EVENT), new Color(0, 99, 0)); // GROUND_NUT_TRADE_WEAK (GNT-WEAK) vert olive //171, 171, 57
		colorMap.put(GROUND_TYPE_CODES.get(GNT_MEDIUM_EVENT), new Color(153, 255, 0)); // GROUND_NUT_TRADE_MEDIUM (GNT-MEDIUM) plus petit
																					// (vers 1950) 206, 206, 48
		colorMap.put(GROUND_TYPE_CODES.get(GNT_HEAVY_EVENT), new Color(236, 236, 19)); // GROUND_NUT_TRADE_HEAVY (GNT-HEAVY) jaune
		colorMap.put(GROUND_TYPE_CODES.get(SENEGAL_EVENT), new Color(195, 143, 43)); // Senegal
		colorMap.put(GROUND_TYPE_CODES.get(BORDER_EVENT), new Color(135, 143, 237)); // Border, Ocean and Abroad
		return colorMap;
	}
	public Map<Integer, Color> colorMapMusTransportGrid(Map<Integer, Color> colorMap) {
		colorMap = new HashMap<Integer, Color>();
		// R G B red green blue
		colorMap.put(GROUND_TYPE_CODES.get(CITY_EVENT), new Color(255, 0, 153)); // CITY (red) (192, 172, 62)
		colorMap.put(GROUND_TYPE_CODES.get(RAIL_EVENT), new Color(200, 200, 200)); // RAIL (noir un peu moins foncé)
		colorMap.put(GROUND_TYPE_CODES.get(RIVER_EVENT), new Color(103, 201, 255)); // RIVER
		colorMap.put(GROUND_TYPE_CODES.get(TOWN_EVENT), new Color(214,163,60)); // TOWN (marron clair (ou un peu plus foncé: 237, 180, 66)
		colorMap.put(GROUND_TYPE_CODES.get(ROAD_EVENT), new Color(76, 76, 76)); // ROAD (gris foncé)
		colorMap.put(GROUND_TYPE_CODES.get(TRACK_EVENT), new Color(160, 114, 19)); // TRACK piste (marron jaune)
		colorMap.put(GROUND_TYPE_CODES.get(GOOD_TRACK_EVENT), new Color(0,200,0)); // good track (marron foncé)
		colorMap.put(GROUND_TYPE_CODES.get(SENEGAL_EVENT), new Color(190, 144, 49)); // Senegal
		colorMap.put(GROUND_TYPE_CODES.get(BORDER_EVENT), new Color(137, 137, 233)); // Border, Ocean and Abroad
		return colorMap;
	}
	public Map<Integer, Color> colorapChizeGrid(Map<Integer, Color> colorMap) {
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
		colorMap.put(10, new Color(0, 0, 0));
		return colorMap;
	}
	//
	// GETTERS
	//
	@Override
	public Color getColor(double... coordinates) {
		return this.colorMap.get((int) this.layer.get(coordinates));
	}
	@Override
	public float getCellSize() {
		return cellSize.get(0);
	}
}
