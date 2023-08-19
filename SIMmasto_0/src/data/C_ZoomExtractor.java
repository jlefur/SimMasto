package data;

import java.awt.Point;
import java.util.TreeSet;

import com.vividsolutions.jts.geom.Coordinate;

import data.constants.I_ConstantGerbil;
import data.constants.I_ConstantString;
import data.converters.C_ConvertGeographicCoordinates;
import data.converters.C_ConvertRainFiles;
/** Extract the input raster files (rain series or landcover) of a zoom or TPE ('très petite emprise') landscape at the desired size
 * from the reference grid of ME ('moyenne emprise') and save the resulting files / Used in Gerbil protocol only
 * @author M.Sall & J.Le Fur 12.2015, rev. JLF&MS 03.2016 */
public class C_ZoomExtractor implements I_ConstantString, I_ConstantGerbil {
	//
	// METHODS
	//
	/** Extract the zoom matrix from the reference grid (ME) */
	private void createZoomFiles(Coordinate zoomOrigin_Udegree, int zoomWidth_Ucell, int zoomHeight_Ucell, String pathRecording,
			String pathFolder) {
		C_ConvertRainFiles rainFilesConverter = new C_ConvertRainFiles();
		int[][] zoomMatrix = new int[zoomWidth_Ucell][zoomHeight_Ucell];
		int[][] referenceMatrix = null;
		String zoomFileName = "";
		Point zoomOrigin_Ucell = computeZoomOrigin_Ucell(zoomOrigin_Udegree);
		TreeSet<String> fileNameList = C_ConvertRainFiles.buildFileNameList(RASTER_PATH + pathFolder);
		if (fileNameList.isEmpty()) {
			System.err.println("C_ZoomExtractor.extractZoom(): folder " + pathFolder + " is empty.");
			return;
		}
		String format;
		// Extract the zoom matrix from the reference matrix
		for (String fileName : fileNameList) {
			format = getExtension(fileName);
			switch (format) {
				case "bmp" : {
					referenceMatrix = rainFilesConverter.readRainRaster(RASTER_PATH + pathFolder + "/" + fileName);
					zoomMatrix = extractZoomFromGrid(referenceMatrix, zoomOrigin_Ucell, zoomWidth_Ucell, zoomHeight_Ucell, "bmp");
					if (fileName.contains("landcover")) zoomFileName = "landcover";
					else zoomFileName = (fileName.split("-")[0]).substring(0, 6) + "-Zoom-Rain";
					C_ConvertRainFiles.saveRainRasterFile(zoomMatrix, zoomFileName, pathRecording);
				}
					break;
				case "txt" : {
					System.out.println(RASTER_PATH + pathFolder + "/" + fileName);
					referenceMatrix = C_ReadRaster.txtRasterLoader(RASTER_PATH + pathFolder + "/" + fileName);
					zoomMatrix = extractZoomFromGrid(referenceMatrix, zoomOrigin_Ucell, zoomWidth_Ucell, zoomHeight_Ucell, "txt");
					if (fileName.contains("landcover")) zoomFileName = "landcover";
					else zoomFileName = (fileName.split("-")[0]).substring(0, 6) + "-Zoom-Rain";
					C_ConvertRainFiles.saveRainRasterFile(zoomMatrix, zoomFileName, pathRecording);
				}
					break;
			}
		}
	}
	/** Extract zoom matrix from ME reference matrix; all units are cell units<br>
	 * Version MS&JLF 03.2016 */
	public int[][] extractZoomFromGrid(int[][] referenceMatrix, Point zoomOrigin_Ucell, int zoomWidth_Ucell, int zoomHeight_Ucell,
			String format) {
		int[][] zoomMatrix = new int[zoomWidth_Ucell][zoomHeight_Ucell];
		System.err.println("C_ZoomExtractor.extractZoomFromGrid()"+referenceMatrix.length);
		try {
			for (int i = zoomOrigin_Ucell.x; i < zoomOrigin_Ucell.x + zoomWidth_Ucell; i++) {
				for (int j = zoomOrigin_Ucell.y; j < zoomOrigin_Ucell.y + zoomHeight_Ucell; j++) {
					switch (format) {
						case "bmp" :
							zoomMatrix[i - zoomOrigin_Ucell.x][j - zoomOrigin_Ucell.y] = referenceMatrix[i][j];
							break;
						case "txt" :
							zoomMatrix[i - zoomOrigin_Ucell.x][j - zoomOrigin_Ucell.y] = referenceMatrix[i][j];
							break;
					}
				}
			}
		} catch (Exception e) {
			System.err.println("C_ZoomExtractor.extractZoomFromGrid() : zoom extraction failed.");
		}
		return zoomMatrix;
	}
	/** Convert zoom origin in degrees into continuous space then into cell coordinates of the ME reference grid */
	public Point computeZoomOrigin_Ucell(Coordinate zoomOrigin_Udegree) {
		Point zoomOrigin_Ucell;
		C_ConvertGeographicCoordinates converter = new C_ConvertGeographicCoordinates(new Coordinate(
				I_ConstantGerbil.gerbilMELongitudeWest_Udegree, I_ConstantGerbil.gerbilMELatitudeSouth_Udegree));
		Coordinate zoomOrigin_Ucs = converter.convertCoordinate_Ucs(zoomOrigin_Udegree.x, zoomOrigin_Udegree.y);
		zoomOrigin_Ucell = new Point((int) zoomOrigin_Ucs.x, (int) zoomOrigin_Ucs.y);
		return zoomOrigin_Ucell;
	}
	//
	// GETTER
	//
	/** Retrieve the extension of the input file */
	public String getExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}
	//
	// MAIN
	//
	public static void main(String[] args) {
		C_ZoomExtractor zoomExtractorTest = new C_ZoomExtractor();
		// Folder to save extracted files
		String pathRecording;
		String referencePathFolder = "rain-ASCII-ME";// Average grip
		// // CASE Zoom 001 08.12.2015
		// pathRecording = "Zoom_001_12.2015";
		// Coordinate zoom1Origin_Udegree = new Coordinate(-16, 16.2);// here Longitude_Udegree,Latitude_Udegree
		// width_heightRaster_Ukilometer.set(0, 30);
		// width_heightRaster_Ukilometer.set(1, 30);
		// zoomExtractorTest.createZoomFiles(zoom1Origin_Udegree, width_heightRaster_Ukilometer.get(0),
		// width_heightRaster_Ukilometer
		// .get(1), pathRecording, referencePathFolder);

		// CASE Zoom 002 08.12.2015
		// pathRecording = "Zoom_002_12.2015";
		// Coordinate zoom2Origin_Udegree = new Coordinate(-15.45, 15.76);// here Longitude_Udegree,Latitude_Udegree
		// width_heightRaster_Ukilometer.set(0, 75);
		// width_heightRaster_Ukilometer.set(1, 45);
		// zoomExtractorTest.createZoomFiles(zoom2Origin_Udegree, width_heightRaster_Ukilometer.get(0),
		// width_heightRaster_Ukilometer
		// .get(1), pathRecording, referencePathFolder);

		// CASE PE
//		 pathRecording = "Zoom_PE_12.2015";
//		 Coordinate zoom2Origin_Udegree = new Coordinate( -14.46, 14.52);// here Longitude_Udegree,Latitude_Udegree
//		 width_heightRaster_Ukilometer.set(0, 32);
//		 width_heightRaster_Ukilometer.set(1, 20);
//		 zoomExtractorTest.createZoomFiles(zoom2Origin_Udegree, width_heightRaster_Ukilometer.get(0),
//		 width_heightRaster_Ukilometer.get(1),
//		 pathRecording, referencePathFolder);
//		 CASE Zoom 3
//		pathRecording = "Zoom_003_15_09.2016";
//		Coordinate zoom2Origin_Udegree = new Coordinate(-16., 15.758572);// here Longitude_Udegree,Latitude_Udegree
//		width_heightRaster_Ukilometer.set(0, 3);
//		width_heightRaster_Ukilometer.set(1, 3);
//		zoomExtractorTest.createZoomFiles(zoom2Origin_Udegree, width_heightRaster_Ukilometer.get(0), width_heightRaster_Ukilometer
//				.get(1), pathRecording, referencePathFolder);
//		CASE Zoom 4
//		pathRecording = "Zoom_004_01.2018";
//		Coordinate zoom2Origin_Udegree = new Coordinate(-15.713078, 16.421078);// here Longitude_Udegree,Latitude_Udegree
//		width_heightRaster_Ukilometer.set(0, 5);
//		width_heightRaster_Ukilometer.set(1, 5);
//		zoomExtractorTest.createZoomFiles(zoom2Origin_Udegree, width_heightRaster_Ukilometer.get(0), width_heightRaster_Ukilometer
//				.get(1), pathRecording, referencePathFolder);
//		CASE djifère Françoise
		pathRecording = "Zoom_FD_2019.03";
		Coordinate zoom2Origin_Udegree = new Coordinate(-16.774386,13.931634);// here Longitude_Udegree,Latitude_Udegree
		width_heightRaster_Ukilometer.set(0, 30);
		width_heightRaster_Ukilometer.set(1, 40);
		zoomExtractorTest.createZoomFiles(zoom2Origin_Udegree, width_heightRaster_Ukilometer.get(0), width_heightRaster_Ukilometer
				.get(1), pathRecording, referencePathFolder);
		// CASE TPE of 25 cells
//		 pathRecording = "Zoom_004_01.2018";
//		 Coordinate zoom2Origin_Udegree = new Coordinate(-15.713078, 16.421078);// here Longitude_Udegree,Latitude_Udegree
//		 width_heightRaster_Ukilometer.set(0, 5);
//		 width_heightRaster_Ukilometer.set(1, 5);
//		 zoomExtractorTest.createZoomFiles(zoom2Origin_Udegree, width_heightRaster_Ukilometer.get(0),
//		 width_heightRaster_Ukilometer.get(1),
//		 pathRecording, referencePathFolder);
//		 pathRecording = "Zoom_ME_20.03.2018";
//		 Coordinate zoom2Origin_Udegree = new Coordinate(-17.899695,  13.616352);// here Longitude_Udegree,Latitude_Udegree
//		 width_heightRaster_Ukilometer.set(0, 700);
//		 width_heightRaster_Ukilometer.set(1, 400);
//		 zoomExtractorTest.createZoomFiles(zoom2Origin_Udegree, width_heightRaster_Ukilometer.get(0),
//				 width_heightRaster_Ukilometer.get(1),
//				 pathRecording, referencePathFolder);
	}
}
