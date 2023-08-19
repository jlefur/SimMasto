package data.converters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;

import data.C_Parameters;
import data.constants.I_ConstantMusTransport;
import data.constants.I_ConstantString;
import simmasto0.protocol.A_Protocol;

/** Utility to convert from geographic coordinates to meters and compute distances in meter from the raster origin? <br>
 * Calculation depends on the raster used
 * @author author Moussa Sall, oct.2015 <br>
 *         source: http://geodesie.ign.fr/contenu/fichiers/documentation/pedagogiques/TransformationsCoordonneesGeodesiques.pdf */
public class C_ConvertGeographicCoordinates implements I_ConstantString {
	//
	// CONSTANTS
	//
	// JLF 2016.08 used WGS84 system; see source p.4
	private static final double earthRadiusAtEquator_Umeter = 6378249.2;// 6378249.2 half of great diameter
	private static final double earthRadiusAtGreenwich_Umeter = 6356515.;// 6356515.; // half of little diameter
	//
	// FIELDS
	//
	public Coordinate rasterOrigin_Umeter;
	public Coordinate rasterOrigin_Uradian;
	public Coordinate rasterOrigin_Udegree;
	public Coordinate rasterEnd_Udegree;
	private double ellipsoidEccentricitySquared_Umeter2;
	//
	// CONSTRUCTOR
	//
	public C_ConvertGeographicCoordinates(Coordinate rasterOrigin_Udegree) {
		this.rasterOrigin_Udegree = rasterOrigin_Udegree;
		this.rasterOrigin_Uradian = new Coordinate(convertDegree_Uradian(rasterOrigin_Udegree.x), convertDegree_Uradian(
				rasterOrigin_Udegree.y));
		this.ellipsoidEccentricitySquared_Umeter2 = (Math.pow(earthRadiusAtEquator_Umeter, 2.0) - Math.pow(
				earthRadiusAtGreenwich_Umeter, 2.0)) / Math.pow(earthRadiusAtEquator_Umeter, 2.0);
		// Compute radius of the normal curvature
		this.rasterOrigin_Umeter = convertCoordinate_Umeter(this.rasterOrigin_Uradian);
	}
	/** Initialize the constructor with origin Coordinate and end Coordinate in degree<br>
	 * author M.Sall */
	public C_ConvertGeographicCoordinates(Coordinate rasterOrigin_Udegree, Coordinate endRaster_Udegree) {
		this(rasterOrigin_Udegree);
		this.rasterEnd_Udegree = endRaster_Udegree;
	}
	//
	// METHODS
	//
	/** computeRadiusOfNormalCurvature */
	public Coordinate convertCoordinate_Umeter(Coordinate coordinate_Uradian) {
		double tmp1 = Math.sqrt(1.0 - this.ellipsoidEccentricitySquared_Umeter2 * ((1.0 - Math.cos(2.0
				* coordinate_Uradian.y)) / 2.0));
		double radius_Umeter = earthRadiusAtEquator_Umeter / tmp1; // raster origin radius
		return new Coordinate(//
				radius_Umeter * Math.cos(coordinate_Uradian.y) * Math.cos(coordinate_Uradian.x), //
				radius_Umeter * Math.cos(coordinate_Uradian.y) * Math.sin(coordinate_Uradian.x), //
				radius_Umeter * (1.0 - this.ellipsoidEccentricitySquared_Umeter2) * Math.sin(coordinate_Uradian.y));
	}
	/** Compute distance to origin of a geographic position in meters */
	public double distanceToRasterOrigin_Umeter(double longitude_Udegree, double latitude_Udegree) {
		Coordinate coordinate_Uradian = new Coordinate(convertDegree_Uradian(longitude_Udegree), convertDegree_Uradian(
				latitude_Udegree));
		Coordinate coordinate_Umeter = convertCoordinate_Umeter(coordinate_Uradian);
		// compute distance using Pythagorean theorem
		return Math.sqrt(Math.pow((this.rasterOrigin_Umeter.x - coordinate_Umeter.x), 2.0) + Math.pow((this.rasterOrigin_Umeter.y
				- coordinate_Umeter.y), 2.0) + Math.pow((this.rasterOrigin_Umeter.z - coordinate_Umeter.z), 2.0));
	}
	public double convertDegree_Uradian(double value_Udegree) {
		return value_Udegree * Math.PI / 180.0;
	}
	public double convertRadian_Udegree(double value_Uradian) {
		return value_Uradian * 180.0 / Math.PI;
	}
	/** "return latitude and longitude geographic coordinates had with the cell conversion by the rule of three */
	public Coordinate convertCell_Udegree(Coordinate oneCell) {
		Coordinate coordinateCell_Udegree = new Coordinate();
		double worldRadius = earthRadiusAtEquator_Umeter / Math.sqrt(1.0 - this.ellipsoidEccentricitySquared_Umeter2
				* ((1.0 - Math.cos(2.0 * this.rasterOrigin_Uradian.y)) / 2.0));
		// C is an intermediate variable used to simplify the expression
		double cosinus_C = Math.sqrt(1.0 - ((Math.sqrt(oneCell.x * oneCell.x + oneCell.y * oneCell.y)) / worldRadius)
				* (Math.sqrt(oneCell.x * oneCell.x + oneCell.y * oneCell.y)) / worldRadius);
		double sinus_C = (Math.sqrt(oneCell.x * oneCell.x + oneCell.y * oneCell.y)) / worldRadius;
		coordinateCell_Udegree.x = this.rasterOrigin_Uradian.x + Math.atan((oneCell.x * sinus_C) / ((Math.sqrt(oneCell.x
				* oneCell.x + oneCell.y * oneCell.y) * Math.cos(this.rasterOrigin_Uradian.y) * cosinus_C) - ((oneCell.y
						* Math.sin(this.rasterOrigin_Uradian.y))) * sinus_C));
		coordinateCell_Udegree.y = Math.asin((cosinus_C * Math.sin(this.rasterOrigin_Uradian.y)) + (oneCell.y * Math
				.cos(this.rasterOrigin_Uradian.y) * sinus_C) / (Math.sqrt(oneCell.x * oneCell.x + oneCell.y
						* oneCell.y)));
		coordinateCell_Udegree.x = this.convertRadian_Udegree(coordinateCell_Udegree.x);
		coordinateCell_Udegree.y = this.convertRadian_Udegree(coordinateCell_Udegree.y);
		return coordinateCell_Udegree;
	}
	/** Return the line and column of the corresponding cell */
	public Coordinate convertCoordinate_Ucs(double longitude_Udegree, double latitude_Udegree) {
		if (C_Parameters.UCS_WIDTH_Umeter == 0.0)
			A_Protocol.event("C_ConvertGeographicCoordinates.convertCoordinate_Ucs", "Ucs worth 0 meter", isError);
		// Compute distance between the projection of Y axis and the origin point
		double coordinatey_Umeter = this.distanceToRasterOrigin_Umeter(this.rasterOrigin_Udegree.x, latitude_Udegree);
		double y = coordinatey_Umeter / C_Parameters.UCS_WIDTH_Umeter;
		// Compute distance between the projection of X axis and the origin point
		double coordinatex_Umeter = this.distanceToRasterOrigin_Umeter(longitude_Udegree, this.rasterOrigin_Udegree.y);
		double x = coordinatex_Umeter / C_Parameters.UCS_WIDTH_Umeter;
		return new Coordinate(x, y);
	}
	public Coordinate convertValueCoordinate_Umeter(Coordinate coordinate, double UcsWidth) {
		return new Coordinate(coordinate.x * UcsWidth, coordinate.y * UcsWidth);
	}
	public Coordinate convertCellRuleOfThree_Udegree(Coordinate cell_Umeter, ArrayList<Double> rasterLongitudeWest_LatitudeSouth_Udegree, Coordinate width_HeightOrigin_Umeter) {
		// compute values in degree of width and height
		Coordinate correspondingCoordWidthAndHeight_Udegree = new Coordinate((this.rasterEnd_Udegree.x
				- this.rasterOrigin_Udegree.x), (this.rasterEnd_Udegree.y - this.rasterOrigin_Udegree.y));
		Coordinate coordinate_Udegree = new Coordinate();
		// compute values of the cell in degree
		coordinate_Udegree.x = rasterLongitudeWest_LatitudeSouth_Udegree.get(0)
				+ (correspondingCoordWidthAndHeight_Udegree.x * cell_Umeter.x / width_HeightOrigin_Umeter.x);
		coordinate_Udegree.y = rasterLongitudeWest_LatitudeSouth_Udegree.get(1)
				+ (correspondingCoordWidthAndHeight_Udegree.y * cell_Umeter.y / width_HeightOrigin_Umeter.y);
		return coordinate_Udegree;
	}
	// Main for Mus transportation
	public static void main(String[] args) {
		Coordinate musTransportOrigin = new Coordinate(I_ConstantMusTransport.rasterLongitudeWest_LatitudeSouth_Udegree
				.get(0), I_ConstantMusTransport.rasterLongitudeWest_LatitudeSouth_Udegree.get(1));
		Coordinate musTransportEnd = new Coordinate(-11.32016, 16.71801);
		C_ConvertGeographicCoordinates distance = new C_ConvertGeographicCoordinates(musTransportOrigin,
				musTransportEnd);
		Map<String, Coordinate> cities_Ucell = new HashMap<String, Coordinate>() {
			private static final long serialVersionUID = 1L;
			{
				put("Touba-Mbacke", new Coordinate(91, 30 + 110));// 110
				put("Thies", new Coordinate(34, 30 + 109));
				put("Saint-Louis", new Coordinate(57, 100 + 110));
				put("Richard-Toll", new Coordinate(103, 125 + 110));
				put("Podor", new Coordinate(144, 136 + 110));
				put("Pikine", new Coordinate(7, 28 + 110));
				put("Mbour", new Coordinate(31, 9 + 110));
				put("Matam", new Coordinate(240, 79 + 110));
				put("Louga", new Coordinate(71, 78 + 110));
				put("Linguere", new Coordinate(135, 64 + 110));
				put("Kebemer", new Coordinate(61, 63 + 110));
				put("Kaolack", new Coordinate(82, 0 + 110));
				put("Fatick", new Coordinate(63, 4 + 110));
				put("Dakar", new Coordinate(4, 28 + 110));
				put("Dahra", new Coordinate(113, 61 + 110));
				put("Dagana", new Coordinate(113, 128 + 110));
				put("Bakel", new Coordinate(294, 29 + 110));
				put("Tassette Peulh", new Coordinate(37, 21 + 110));
				put("Naoure", new Coordinate(174, 45 + 110));
				put("Galoya", new Coordinate(207, 103 + 110));
				put("Diourbel", new Coordinate(73, 22 + 110));
				put("Coki Gueye", new Coordinate(55, 57 + 110));
				put("Yabal", new Coordinate(93, 52 + 110));
				put("Wendou Thingoly IV", new Coordinate(123, 98 + 110));
				put("Velingara Mbonaye I", new Coordinate(162, 41 + 110));
				put("Toukar", new Coordinate(59, 16 + 110));
				put("Touba Toul", new Coordinate(48, 32 + 110));
				put("Tivaouane", new Coordinate(40, 39 + 110));
				put("Thionokh", new Coordinate(157, 21 + 110));
				put("Thilogne Tokossel", new Coordinate(221, 97 + 110));
				put("Thilmakha", new Coordinate(72, 44 + 110));
				put("Thille Boubacar", new Coordinate(137, 128 + 110));
				put("Thiambene", new Coordinate(43, 45 + 110));
				put("Tessekere Ouolof", new Coordinate(139, 89 + 110));
				put("Tessekere Forage II", new Coordinate(138, 91 + 110));
				put("Tataguine", new Coordinate(53, 8 + 110));
				put("Tassette Toucouleur", new Coordinate(38, 21 + 110));
				put("Tassette Serere", new Coordinate(37, 20 + 110));
				put("Somono Ouaounde", new Coordinate(262, 57 + 110));
				put("Sibassor", new Coordinate(79, 0 + 110));
				put("Sandiara Serere", new Coordinate(41, 10 + 110));
				put("Saint Louis", new Coordinate(57, 100 + 110));
				put("Sagatta Djolof", new Coordinate(111, 54 + 110));
				put("Sagatta", new Coordinate(76, 58 + 110));
				put("Sadio", new Coordinate(111, 31 + 110));
				put("Richard Toll", new Coordinate(103, 125 + 110));
				put("Ribo escale", new Coordinate(192, 0 + 110));
				put("Rao Peulh", new Coordinate(62, 93 + 110));
				put("Pout Diack", new Coordinate(37, 23 + 110));
				put("Potou I", new Coordinate(56, 83 + 110));
				put("Podor", new Coordinate(144, 136 + 110));
				put("Pikine", new Coordinate(7, 28 + 110));
				put("Pekesse", new Coordinate(62, 49 + 110));
				put("Payar", new Coordinate(196, 4 + 110));
				put("Patar Banane", new Coordinate(77, 13 + 110));
				put("Patar", new Coordinate(64, 17 + 110));
				put("Ourossogui", new Coordinate(237, 76 + 110));
				put("Orefonde", new Coordinate(214, 102 + 110));
				put("Notto", new Coordinate(36, 25 + 110));
				put("Niassante", new Coordinate(111, 108 + 110));
				put("Niamga Ndioum", new Coordinate(162, 128 + 110));
				put("Niakhar", new Coordinate(63, 14 + 110));
				put("Ngnith", new Coordinate(91, 109 + 110));
				put("Ndiosmone", new Coordinate(49, 9 + 110));
				put("Ndiéné Lagana", new Coordinate(105, 24 + 110));
				put("Ndiagne", new Coordinate(64, 60 + 110));
				put("Ndiaganiao Escale", new Coordinate(45, 16 + 110));
				put("Ndande", new Coordinate(56, 58 + 110));
				put("Naoure", new Coordinate(173, 45 + 110));
				put("Mpal Gare", new Coordinate(71, 94 + 110));
				put("Mpal Dioungo", new Coordinate(70, 93 + 110));
				put("Mekhe", new Coordinate(50, 48 + 110));
				put("Mbouloukhtene", new Coordinate(49, 22 + 110));
				put("Mboss", new Coordinate(95, 4 + 110));
				put("Mbar", new Coordinate(100, 16 + 110));
				put("Matam", new Coordinate(240, 79 + 110));
				put("Lompoul", new Coordinate(44, 67 + 110));
				put("Linguere", new Coordinate(135, 64 + 110));
				put("KoungKoung", new Coordinate(188, 3 + 110));
				put("Keur Momar Sarr", new Coordinate(87, 94 + 110));
				put("Kebemer", new Coordinate(61, 63 + 110));
				put("Guinguineo", new Coordinate(88, 0 + 110));
				put("Guia", new Coordinate(146, 133 + 110));
				put("Gueoul Escale", new Coordinate(67, 69 + 110));
				put("Gouye Mbeuth", new Coordinate(84, 81 + 110));
				put("Gossas", new Coordinate(82, 13 + 110));
				put("Gollere Barangol", new Coordinate(193, 113 + 110));
				put("Gnibi", new Coordinate(100, 7 + 110));
				put("Gandiaye", new Coordinate(71, 0 + 110));
				put("Gande", new Coordinate(274, 48 + 110));
				put("Galoya", new Coordinate(206, 103 + 110));
				put("Fass Boye", new Coordinate(38, 57 + 110));
				put("dodel", new Coordinate(174, 127 + 110));
				put("Diouroup", new Coordinate(58, 5 + 110));
				put("Diogo", new Coordinate(42, 57 + 110));
				put("Dinguiraye", new Coordinate(63, 40 + 110));
				put("Diarhao", new Coordinate(70, 12 + 110));
				put("Diarer", new Coordinate(59, 11 + 110));
				put("Diaoulé", new Coordinate(73, 6 + 110));
				put("Decolle Taredji", new Coordinate(150, 128 + 110));
				put("Darou Mousty", new Coordinate(83, 45 + 110));
				put("Dangalma", new Coordinate(53, 26 + 110));
				put("Dahra", new Coordinate(113, 61 + 110));
				put("Dagana", new Coordinate(113, 128 + 110));
				put("Colobane", new Coordinate(102, 22 + 110));
				put("Coky", new Coordinate(86, 71 + 110));
				put("Boulel", new Coordinate(107, 0 + 110));
				put("Bokhol", new Coordinate(119, 129 + 110));
				put("Belakadio", new Coordinate(70, 5 + 110));
				put("Barkedji", new Coordinate(153, 19 + 110));
				put("Bambey", new Coordinate(60, 25 + 110));
				put("Baba Garage", new Coordinate(58, 39 + 110));
				put("Agnam Thioulel Thialle", new Coordinate(219, 98 + 110));
				put("Agnam Goly", new Coordinate(216, 99 + 110));
				put("Ngohe Mbouguel", new Coordinate(50, 6 + 110));
				put("Ndiabel", new Coordinate(78, 11 + 110));
				put("Diohine", new Coordinate(57, 14 + 110));
			}
		};
		Object[] keys = cities_Ucell.keySet().toArray();
		for (int i = 0; i < cities_Ucell.size(); i++) {
			double cellSize_Umeter = 1965;
			String name = (String) keys[i];
			Coordinate coordinate = new Coordinate(cities_Ucell.get(name).x * cellSize_Umeter, cities_Ucell.get(name).y
					* cellSize_Umeter);
			// Coordinate result = distance.convertCell_Udegree(coordinate);
			Coordinate result = distance.convertCellRuleOfThree_Udegree(coordinate,I_ConstantMusTransport.rasterLongitudeWest_LatitudeSouth_Udegree,new Coordinate(351.*1950.,251.*1950.));
			System.out.println(result.y + "," + result.x + "," + cities_Ucell.keySet().toArray()[i]);
		}
	}

	// Main for gerbil
	// public static void main(String[] args) {
	// I_ConstantGerbil.rasterLongitudeWest_LatitudeSouth_Udegree.set(0, -16.);
	// I_ConstantGerbil.rasterLongitudeWest_LatitudeSouth_Udegree.set(1, 15.758572);
	// Coordinate gerbilOrigin = new Coordinate(I_ConstantGerbil.rasterLongitudeWest_LatitudeSouth_Udegree.get(0),
	// I_ConstantGerbil.rasterLongitudeWest_LatitudeSouth_Udegree.get(1));
	// Coordinate gerbilEnd = new Coordinate(I_ConstantGerbil.rasterLongitudeEast_Udegree,
	// I_ConstantGerbil.rasterLatitudeNorth_Udegree);
	// C_ConvertGeographicCoordinates distance = new C_ConvertGeographicCoordinates(gerbilOrigin, gerbilEnd);
	// List<Coordinate> geographiqueListCoordinate = new ArrayList<Coordinate>() {
	// private static final long serialVersionUID = 1L;
	// {
	// add(new Coordinate(-15.6355, 16.428));
	// add(new Coordinate(-15.6355, 16.428));
	// add(new Coordinate(-15.91121793, 16.17916017));
	// add(new Coordinate(-14.4541, 16.495031));
	// add(new Coordinate(-15.65713333, 16.457));
	// add(new Coordinate(-15.65713333, 16.457));
	// add(new Coordinate(-15.65661667, 16.45445));
	// add(new Coordinate(-15.66311667, 16.45258333));
	// add(new Coordinate(-15.65978333, 16.44906667));
	// add(new Coordinate(-15.33341667, 15.99895));
	// add(new Coordinate(-15.3389, 16.00056667));
	// add(new Coordinate(-15.3389, 16.00056667));
	// add(new Coordinate(-15.3389, 16.00056667));
	// add(new Coordinate(-15.3389, 16.00056667));
	// add(new Coordinate(-15.33341667, 15.99895));
	// add(new Coordinate(-15.33341667, 15.99895));
	// add(new Coordinate(-15.33341667, 15.99895));
	// add(new Coordinate(-15.33341667, 15.99895));
	// };
	// };
	// int sizeListResult_Udegree = geographiqueListCoordinate.size();
	// List<Coordinate> listResult_Ucell = new ArrayList<>();
	// List<Coordinate> firstListResult_Ucell = new ArrayList<>();
	// for (int i = 0; i < sizeListResult_Udegree; i++) {
	// listResult_Ucell.add(distance.convertCoordinate_Ucs(geographiqueListCoordinate.get(i).x,
	// geographiqueListCoordinate.get(i).y));
	// System.out.println(geographiqueListCoordinate.get(i).x + " , " + geographiqueListCoordinate.get(i).y);
	// }
	// System.err.println("End reference list coordinate!!!");
	// for (int i = 0; i < sizeListResult_Udegree; i++) {
	// firstListResult_Ucell.add(distance.convertCell_Udegree(distance.convertValueCoordinate_Umeter(listResult_Ucell.get(i),
	// I_ConstantGerbil.UCS_WIDTH_Umeter)));
	// System.out.println(firstListResult_Ucell.get(i).x + " , " + firstListResult_Ucell.get(i).y);
	// }
	// System.err.println("End converted list coordinate!!!");
	// }
}