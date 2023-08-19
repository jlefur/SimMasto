/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package data.converters;

import java.io.File;
import java.util.TreeSet;

import presentation.dataOutput.C_FileWriter;
import data.C_ReadRaster;
import data.constants.I_ConstantGerbil;
import data.constants.I_ConstantString;

/** Read the rain bitmaps, rescale colors and save the grid in ASCII
 * @author M.Sall 10.2015, rev. MS&JLF 04.2016 */
public class C_ConvertRainFiles implements I_ConstantString, I_ConstantGerbil {
    //
    // METHODS
    //
    /** Read the rain raster bitmap file and build the corresponding matrix */
    public int[][] readRainRaster(String fileNameInRain) {
        int[][] rainMatrix = C_ReadRaster.imgRasterLoader(RASTER_PATH + fileNameInRain);
        rainMatrix = rescaleColorValues(rainMatrix);
        System.out.println(fileNameInRain + " bitmap read ");
        return rainMatrix;
    }
    /** Save the grid in the file following the name passed in args */
    public static void saveRainRasterFile(int[][] matriceLue, String fileNameInRain, String path) {
        int nbColumns;
        int nbLines;
        C_FileWriter rasterFile_Utxt;
        // Create or recreate empty gerbilSavanna file
        nbColumns = matriceLue.length;
        nbLines = matriceLue[0].length;
        rasterFile_Utxt = new C_FileWriter(RASTER_PATH + path + "/" + fileNameInRain + ".txt", false);
        // Compute max and min values of the matrix
        int maxValue = 0;
        int minValue = Integer.MAX_VALUE;
        for (int i = 0; i < nbColumns; i++) {
            for (int j = 0; j < nbLines; j++) {
                if (maxValue < matriceLue[i][j]) maxValue = matriceLue[i][j];
                if (minValue > matriceLue[i][j]) minValue = matriceLue[i][j];
            }
        }
        // Raster file add values
        System.err.println("C_ConvertRainFiles.saveRainRasterFile(): Writing file");
        rasterFile_Utxt.writeln("DSAA");
        rasterFile_Utxt.writeln(nbColumns + "");
        rasterFile_Utxt.writeln(nbLines + "");
        rasterFile_Utxt.write(minValue + " " + maxValue);
        for (int i = nbLines - 1; i >= 0; i--) {
            rasterFile_Utxt.writeln("");
            rasterFile_Utxt.write(matriceLue[0][i] + "");
            for (int j = 1; j < nbColumns; j++)
                rasterFile_Utxt.write(" " + matriceLue[j][i]);
        }
        rasterFile_Utxt.closeFile();
        System.err.println("C_ConvertRainFiles.saveRainRasterFile(): " + fileNameInRain + ".txt conversion complete");
    }
    /** Compute the name of the resulting file from the original name with date and suffix<br>
     * run this one time for all */
    public String computeFileName(String fileNameInRain) {
        // ex: fileNameInRain = TRMM-3B42-19990101-19990131 to 19990101
        return fileNameInRain.substring(10, 16) + "-ME-Rain";
    }
    /** Elaborate a list with the whole series of file names (one for each month) contained in the rain folder */
    public static TreeSet<String> buildFileNameList(String directoryPath) {
        TreeSet<String> fileNameList = new TreeSet<String>();
        // Find all of file name in the directory
        File directory = new File(directoryPath);
        if (!directory.exists()) System.err.println("C_ConvertRainFiles.buildFileNameList(); Folder " + directoryPath + " does not exist");
        else {
            File[] subfiles = directory.listFiles();
            int length = subfiles.length;
            for (int i = 0; i < length; i++)
                fileNameList.add(subfiles[i].getName());
        }
        return fileNameList;
    }
    /** Replace all occurrences of old value with new value in the grid */
    public int[][] replaceValueInMatrix(int oldValue, int newValue, int[][] grid) {
        if (checkOccurrenceInMatrix(newValue, grid)) {
            System.err.println(newValue + " already exist, impossible to change with " + oldValue);
            return null;
        }
        int oldValueSaved = 0, frontierValue = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == oldValue) {
                    grid[i][j] = newValue;
                    oldValueSaved = newValue;
                }
                // When frontierValue is encountered, replace with preceding cell value. Used only once
                else {
                    if (grid[i][j] == frontierValue) grid[i][j] = oldValueSaved;
                    else oldValueSaved = grid[i][j];
                }
            }
        }
        return grid;
    }
    /** @return true if the requested value exist in matrix */
    public boolean checkOccurrenceInMatrix(int value, int[][] matrix) {
        for (int i = 0; i < matrix.length; i++)
            for (int j = 0; j < matrix[0].length; j++)
                if (matrix[i][j] == value) return true;
        return false;
    }
    /** Rain bitmap values are arbitrary color values, order them to the corresponding levels of rain intensity */
    public int[][] rescaleColorValues(int[][] matriceLue) {
        int[][] newMatrix = matriceLue;
        // Reorder bitmap values to conform to rain intensity scale
        newMatrix = replaceValueInMatrix(8, 17, newMatrix);
        newMatrix = replaceValueInMatrix(3, 16, newMatrix);
        newMatrix = replaceValueInMatrix(4, 15, newMatrix);
        newMatrix = replaceValueInMatrix(1, 14, newMatrix);
        newMatrix = replaceValueInMatrix(2, 13, newMatrix);
        newMatrix = replaceValueInMatrix(5, 12, newMatrix);
        newMatrix = replaceValueInMatrix(6, 11, newMatrix);
        newMatrix = replaceValueInMatrix(7, 10, newMatrix);
        // Rescale values from 1 to 8
        newMatrix = replaceValueInMatrix(10, 1, newMatrix);
        newMatrix = replaceValueInMatrix(11, 2, newMatrix);
        newMatrix = replaceValueInMatrix(12, 3, newMatrix);
        newMatrix = replaceValueInMatrix(13, 4, newMatrix);
        newMatrix = replaceValueInMatrix(14, 5, newMatrix);
        newMatrix = replaceValueInMatrix(15, 6, newMatrix);
        newMatrix = replaceValueInMatrix(16, 7, newMatrix);
        newMatrix = replaceValueInMatrix(17, 8, newMatrix);
        return newMatrix;
    }
    //
    // MAIN
    //
    public static void main(String[] args) {
        int[][] matriceMELue;
        String rainBitmapFolder = "rain-bitmaps-ME/";
        C_ConvertRainFiles converter = new C_ConvertRainFiles();
        TreeSet<String> fileNameList = C_ConvertRainFiles.buildFileNameList(RASTER_PATH + rainBitmapFolder);
        // build all ME matrix
        for (String fileNameInRain : fileNameList) {
            if (! fileNameInRain.contains("svn")) {
                matriceMELue = converter.readRainRaster(rainBitmapFolder + fileNameInRain);
                C_ConvertRainFiles.saveRainRasterFile(matriceMELue, converter.computeFileName(fileNameInRain), "rain-ASCII-ME");
            }
        }
        System.out.println("conversion complete!!!");
    }
}
