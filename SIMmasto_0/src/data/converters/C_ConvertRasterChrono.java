package data.converters;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

import presentation.dataOutput.C_FileWriter;
import data.C_ReadWriteFile;
import data.constants.I_ConstantNumeric;
import data.constants.I_ConstantString;

/** Can read a raster and convert it to a chrono or vice versa
 * @author Pape Mboup 2013, rev.JLF 08.2014 */
public class C_ConvertRasterChrono implements I_ConstantString, I_ConstantNumeric {
	//
	// METHODS
	//
	/** Build the data raster from a chronogram file. <br>
	 * Chronogram format : DATE_COL, X_COL, Y_COL, VALUE1_COL ...<br>
	 * CSV semicolon separator (;) X_COL and Y_COL must be growing and DATE_COL may be void("";) but not null!
	 * @param csvRasterChronoName
	 * @param rasterURL where put the raster built
	 * @param bioclimate_to_affinitiesMap used to convert bioclimate to affinities optional Author pamboup 21/06/2013 */
	public void buildRasterFileFromChrono(String csvRasterChronoName, String rasterURL,
			Map<Integer, Integer>... bioclimate_to_affinitiesMap) {
		boolean mustConvert = false;
		if (bioclimate_to_affinitiesMap.length != 0) mustConvert = true;
		// ouverture du rasterChrono .csv
		BufferedReader rasterChronoBufferReader = C_ReadWriteFile.openBufferReader(CSV_PATH, csvRasterChronoName);
		C_FileWriter rasterFileToWrite = new C_FileWriter(rasterURL, false);
		String readLine;
		int lineNo = 0, colNo = 0;
		try {
			readLine = rasterChronoBufferReader.readLine();
			readLine = rasterChronoBufferReader.readLine();
			// I use a collection instead of a matrix because I don't know a priori the number of line and column of the raster
			Map<Integer, Map<Integer, Integer>> affinityMatrixTmp = new HashMap<Integer, Map<Integer, Integer>>();
			int affinityValue, maxAffinityValue = 0, minAffinityValue = Integer.MAX_VALUE;
			String[] readLineTable;
			while (readLine != null) {
				readLineTable = readLine.split(CSV_FIELD_SEPARATOR);
				if (readLineTable.length == 0) {// pour ne pas prendre en compte les éventuelles ligne vides (;;;;;;;)
					readLine = rasterChronoBufferReader.readLine();
					continue;
				}
				lineNo = Integer.parseInt(readLineTable[X_COL]);// at the end, x = the number of line of the raster
				colNo = Integer.parseInt(readLineTable[Y_COL]); // at the end, y = the number of column of the raster
				// /build the affinity matrix from the rasterChrono
				if (mustConvert) affinityValue = bioclimate_to_affinitiesMap[0].get(Integer.parseInt(readLineTable[VALUE1_COL]));
				else affinityValue = Integer.parseInt(readLineTable[VALUE1_COL]);
				if (!affinityMatrixTmp.containsKey(colNo)) affinityMatrixTmp.put(colNo, new HashMap<Integer, Integer>());
				affinityMatrixTmp.get(colNo).put(lineNo, affinityValue);
				if (minAffinityValue > affinityValue) minAffinityValue = affinityValue;
				if (maxAffinityValue < affinityValue) maxAffinityValue = affinityValue;
				readLine = rasterChronoBufferReader.readLine();
			}
			// Write the four first lines of raster after computation
			rasterFileToWrite.writeln("DSAA"); // First line of the raster file
			rasterFileToWrite.writeln((lineNo + 1) + ""); // Number of line of the raster file
			rasterFileToWrite.writeln((colNo + 1) + ""); // Number of column of the raster file
			rasterFileToWrite.write(minAffinityValue + " " + maxAffinityValue); // min and max values of the raster file
			// Invert and copy the matrix in the rasterFile
			int i, j;
			for (j = colNo; j >= 0; j--) {
				rasterFileToWrite.writeln("");
				rasterFileToWrite.write("" + affinityMatrixTmp.get(j).get(0));
				for (i = 1; i <= lineNo; i++)
					rasterFileToWrite.write(" " + affinityMatrixTmp.get(j).get(i));
			}
			// End building the raster file
		} catch (Exception e) {
			System.out.println("C_ReadWriteFile.buildRasterFileFromChrono() : Erreur de parcours du résultat " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				rasterChronoBufferReader.close();
				rasterFileToWrite.closeFile();
			} catch (Exception e) {
				System.err
						.println("C_CsvChronoReader.makeRasterFromChrono() : buffer or rasterFile closing error" + e.getMessage());
			}
		}
	}
	/** Build a chronogram from a raster file. Ex : We can use excel to build a raster file and this method to transform it to a
	 * chronogram. The raster file must be in data_raster/, and the chronogram built will be in data_csv/ in
	 * chronogramFromRaster.csv name.
	 * @param rasterFileName (.txt) */
	public static void buildChronoFromRasterFile(String rasterFileName) {
		BufferedReader buffer = C_ReadWriteFile.openBufferReader(RASTER_PATH, rasterFileName);
		C_FileWriter chronogramFile = new C_FileWriter("data_csv/chronogramFromRaster.csv", false);
		String readLine;
		Map<String, String> map = new HashMap<String, String>() {
			private static final long serialVersionUID = 1L;
			{
				put("-1", "bioClimate;0");
				put("0", "road");
				put("1", "track");
				put("2", "city;city1");
				put("3", "city;city2");
			}
		};
		try {
			// Compute the number of lines, column for the four first lines of rasterFile
			readLine = buffer.readLine();
			int nbrCol, x = 0;
			while (readLine != null) {
				String[] line = readLine.split(" ");
				nbrCol = line.length;
				for (int y = 0; y < nbrCol; y++) {
					chronogramFile.writeln("00/00/0000;" + x + CSV_FIELD_SEPARATOR + y + CSV_FIELD_SEPARATOR + map.get(line[y]));
				}
				x++;
				readLine = buffer.readLine();
			}
		} catch (Exception e) {
			System.err.println("C_CsvChronoReader.makeRasterFromChrono() : Erreur de parcours du résultat " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				buffer.close();
				chronogramFile.closeFile();
			} catch (Exception e) {
				System.err
						.println("C_CsvChronoReader.makeRasterFromChrono() : buffer or rasterFile closing error" + e.getMessage());
			}
		}
	}
	//
	// MAIN
	//
	/** Make the raster file from the raster.csv */
	public static void main(String[] args) {
		buildChronoFromRasterFile("testeurPlusCourtChemin.txt");
	}

}
