package simmasto0.util;

import java.util.TreeSet;

import data.C_ReadRaster;
import data.constants.I_ConstantString;
import data.converters.C_ConvertRainFiles;
import presentation.dataOutput.C_FileWriter;

/** This class allow us to compute a raster with a list of txt file  .
 * @author M. Sall 06.2018 */
public class C_ComputeRasterWithList implements I_ConstantString {
	private String format = "txt";
	public C_ComputeRasterWithList(String pathFolder, String fileName) {
		TreeSet<String> fileNameList = C_ConvertRainFiles.buildFileNameList(RASTER_PATH + pathFolder);
		if (fileNameList.isEmpty()) {
			System.err.println("C_ComputeRasterWithList: folder " + pathFolder + " is empty.");
			return;
		}
		else {
			int[][] matrixRead;
			int[][] matrixToCompare;
			java.util.Iterator<String> iteratorValue = fileNameList.iterator();
			if (format.compareTo("txt") == 0) {
				String value = iteratorValue.next();
				matrixRead = C_ReadRaster.txtRasterLoader(RASTER_PATH + pathFolder + value);
				System.out.println(value);
				while (iteratorValue.hasNext()) {
					value = iteratorValue.next();
					System.out.println(value);
					matrixToCompare = C_ReadRaster.txtRasterLoader(RASTER_PATH + pathFolder + value);
					for (int j = 0; j < matrixRead.length; j++)
						for (int k = 0; k < matrixRead[0].length; k++) {
							if (matrixToCompare[j][k] != 0 && matrixRead[j][k] != matrixToCompare[j][k]) matrixRead[j][k] = matrixToCompare[j][k];
						}
				}
			}
			else {
				matrixRead = C_ReadRaster.imgRasterLoader(RASTER_PATH + pathFolder + iteratorValue.next());
				while (iteratorValue.hasNext()) {
					matrixToCompare = C_ReadRaster.imgRasterLoader(RASTER_PATH + pathFolder + iteratorValue.next());
					for (int j = 0; j < matrixRead.length; j++)
						for (int k = 0; k < matrixRead[0].length; k++) {
							if (matrixRead[j][k] != matrixToCompare[j][k] && matrixToCompare[j][k] != 0) matrixRead[j][k] = matrixToCompare[j][k];
						}
				}
			}
			int nbColumns;
			int nbLines;
			C_FileWriter rasterFile_Utxt;
			nbColumns = matrixRead.length;
			nbLines = matrixRead[0].length;
			rasterFile_Utxt = new C_FileWriter(RASTER_PATH + pathFolder + "/" + fileName + ".txt", false);
			// Compute max and min values of the matrix
			int maxValue = 0;
			int minValue = Integer.MAX_VALUE;
			for (int i = 0; i < nbColumns; i++) {
				for (int j = 0; j < nbLines; j++) {
					if (maxValue < matrixRead[i][j]) maxValue = matrixRead[i][j];
					if (minValue > matrixRead[i][j]) minValue = matrixRead[i][j];
				}
			}
			// Raster file add values
			rasterFile_Utxt.writeln("DSAA");
			rasterFile_Utxt.writeln(nbColumns + "");
			rasterFile_Utxt.writeln(nbLines + "");
			rasterFile_Utxt.write(minValue + " " + maxValue);
			for (int i = nbLines - 1; i >= 0; i--) {
				rasterFile_Utxt.writeln("");
				rasterFile_Utxt.write(matrixRead[0][i] + "");
				for (int j = 1; j < nbColumns; j++)
					rasterFile_Utxt.write(" " + matrixRead[j][i]);
			}
			rasterFile_Utxt.closeFile();
			System.out.println("Computing raster done!!!");
		}
	}
	@SuppressWarnings("unused")
    public static void main(String[] args) {
		C_ComputeRasterWithList object = new C_ComputeRasterWithList("20180814_RasterDodel2/ZoomSpecial/","20210527-ZoomRoomSpecialRasteDodel2.1a");

	}
}
