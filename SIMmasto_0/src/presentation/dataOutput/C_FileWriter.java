package presentation.dataOutput;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import data.C_Parameters;
import data.constants.I_ConstantString;

public class C_FileWriter implements I_ConstantString {
	/** the directory within which output files are placed */
	private String $DOSSIER = "";
	private String fileName = "";
	private PrintWriter printer;
	public int numRun = 0;
	//
	// CONSTRUCTOR
	//
	/** Create a new output data file
	 * @param fileNameOrUrlPlusFileName with extension
	 * @param incrementNameOrNot boolean to inform if the name must be increment or not
	 * Version rev. PAMboup 25/06/2014 */
	public C_FileWriter(String fileNameOrUrlPlusFileName, boolean incrementNameOrNot) {
		// Création du dossier (s'il n'existe pas encore)
		if (fileNameOrUrlPlusFileName.contains("/")) // if the complete url is given
		buildFolders(fileNameOrUrlPlusFileName);
		else {
			if (fileNameOrUrlPlusFileName == "HybGeneralIndicators.csv" || fileNameOrUrlPlusFileName == "MbourCages.csv"
					|| fileNameOrUrlPlusFileName == "DiploidNumber.csv") buildFolders(OUTPUT_PATH + "Hybrids/");
			else buildFolders(OUTPUT_PATH + C_Parameters.PROTOCOL + "/");
		}
		// Création du fichier, récupération de la dernière partie de l'URL Ex: titre.extension dans rep1/rep1.1/titre.extension
		fileNameOrUrlPlusFileName = fileNameOrUrlPlusFileName.split("/")[fileNameOrUrlPlusFileName.split("/").length - 1];
		File file = new File(this.$DOSSIER + this.numRun + fileNameOrUrlPlusFileName);
		if (incrementNameOrNot) { // En incrémentant le nom
			while (file.exists()) {
			    this.numRun++;
				file = new File(this.$DOSSIER + this.numRun + fileNameOrUrlPlusFileName + "");
			}
		}
		else // En écrasant l'ancien de meme nom : PAMboup 27/12/2012
		file = new File(this.$DOSSIER + fileNameOrUrlPlusFileName);
		this.fileName = this.$DOSSIER + file.getName();
		// Ouverture d'un flux d'écriture du fichier créé (écriture en écrasant, pr ecire en ajout: new FileWriter(file, true))
		try {
			printer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//
	// METHODS
	//
	/** Build folders, for exemple : if url = "folder1/forder2/ or url = "folder1/forder2/file.extension, this method build the
	 * folder1 and inside it build the folder2 (and don't build file.extension)
	 * @param url
	 * @author pamboup 20/06/2014 */
	private void buildFolders(String url) {
		String folders[] = url.split("/");
		this.$DOSSIER = "";
		int n;
		if (url.charAt(url.length() - 1) == '/') n = folders.length;// if l'url is finished by "/" (just folders and not file name)
		else n = folders.length - 1;// else the url contains folders name and file name
		for (int i = 0; i < n; i++) {
		    this.$DOSSIER += folders[i] + "/";
			new File(this.$DOSSIER).mkdir();
		}
	}
	// ECRITURE FICHIERS //
	/** Ecrit le String en paramètre */
	public void write(String str) {
	    this.printer.print(str);
	    this.printer.flush();
	}
	/** Ecrit le String en paramètre et saute une ligne */
	public void writeln(String str) {
	    this.printer.println(str);
	    this.printer.flush();
	}
	/** Constructs a FileWriter object given a file name and append the data written.
	 * @param urlAndfileName - String The filename.
	 * @param multiLine - ArrayList of String . author mboup 03/2014 */
	public static void writeMultiLineAndClose(String urlAndfileName, List<String> multiLine) {
		PrintWriter buffer = null;
		try {
			buffer = new PrintWriter(new BufferedWriter(new FileWriter(urlAndfileName, true)));
		} catch (IOException e) {
			System.err.println("C_OutputData.writeMultiLineAndClose() : Error openning " + urlAndfileName + "in writing");
			e.printStackTrace();
		}
		try {
			for (String line : multiLine)
				buffer.println(line);
		} catch (Exception e) {
			System.err.println("C_OutputData.writeMultiLineAndClose() : Erreur d'écriture sur le fichier " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				buffer.close();
			} catch (Exception e) {
				System.err.println("C_OutputData.writeMultiLineAndClose() : buffer closing error" + e.getMessage());
			}
		}
	}

	/** Ferme le fichier (à utiliser une fois que l'on a terminé d'écrire */
	public void closeFile() {
	    this.printer.close();
	}
	public String getName() {
		return this.fileName;
	}
	public int getNumRun() {
		return this.numRun;
	}
}
