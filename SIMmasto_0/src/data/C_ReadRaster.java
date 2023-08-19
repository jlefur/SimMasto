package data;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import data.constants.I_ConstantString;
import thing.ground.landscape.C_Landscape;

/** Two utilities to read either an ASCII or bitmap raster and return a grid in SimMasto format (the image must be in grey levels
 * or in 256 or less)
 * @see C_Landscape
 * @author Quentin Baduel, 2008, rev. JLF 10.2015 */
public class C_ReadRaster implements I_ConstantString {
	/** Downloads grid in ASCII text format : 1st line "DSAA" 2nd line number of rows 3rd line number of columns 4th line min and
	 * max values remaining: line 0: column 0 to j-1 (delimiter blank space) line i line i-1
	 * @param url
	 * @return matrix of affinities (or whatever) */
	public static int[][] txtRasterLoader(String url) {
		// on charge le fichier d'après l'url donnée en paramètre
		File fichier_raster = new File(url);
		String chaine = null;
		StringTokenizer st;
		int[][] matrice = null;
		try {
			// on crée un flux de lecture du fichier
			DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(fichier_raster)));
			// on crée un lecteur de flux
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader lecteur = new BufferedReader(isr);
			lecteur.readLine();// DSAA
			st = new StringTokenizer(lecteur.readLine());
			int largeur = Integer.parseInt(st.nextToken());
			st = new StringTokenizer(lecteur.readLine());
			int hauteur = Integer.parseInt(st.nextToken());
			matrice = new int[largeur][hauteur];
			lecteur.readLine();
			int i = 0;
			int j = 0;
			// tant qu'il y a des lignes à lire :
			while ((chaine = lecteur.readLine()) != null) {
				// on récupère une ligne ...
				st = new StringTokenizer(chaine);
				// ... tant qu'elle a des éléments
				while (st.hasMoreElements()) {
					// on lit l'entier correspondant et on l'enregistre dans la matrice.
					matrice[j][hauteur - i - 1] = Integer.parseInt((st.nextToken()));
					j++;
				}
				j = 0;
				i++;
			}
			lecteur.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return matrice;
	}

	/** Downloads grid in raster image with its related colormodel this function should be modified in order to read the data type
	 * contained in the raster. For now, we read a png image which contain int value in a byte array for each pixel */
	public static int[][] imgRasterLoader(String url) {
		int[][] matriceLue = null;
		Map<Integer, Color> colorMap = null;
		try {
			BufferedImage img = ImageIO.read(new File(url));
			/** valeur 12: gif valeur 13: TYPE_BYTE_INDEXED Represents an indexed byte image. When this type is used as the
			 * imageType argument to the BufferedImage constructor that takes an imageType argument but no ColorModel argument, an
			 * IndexColorModel is created with a 256-color 6/6/6 color cube palette with the rest of the colors from 216-255
			 * populated by grayscale values in the default sRGB ColorSpace. */
			// retrieves the image type
			int type = img.getType();
			System.out.println("C_ReadRaster.imgRasterLoader(), image type: " + type);
			// then we build a color map in different cases.
			switch (type) {
				case 10 : // TYPE_BYTE_GRAY
					colorMap = new HashMap<Integer, Color>();
					// we will use 256 grey level
					for (int i = 0; i < 256; i++) {
						colorMap.put(i, new Color(i, i, i));
					}
					break;
				case 12 : // TYPE_BYTE_BINARY -> quand l'image est transformée en
							// moins de 256 couleurs
					IndexColorModel c12 = (IndexColorModel) img.getColorModel();
					colorMap = new HashMap<Integer, Color>();
					// we make a copy of the color model in the color map
					for (int i = 0; i < c12.getMapSize(); i++) {
						colorMap.put(i, new Color(c12.getRGB(i)));
						System.out.println(i + "ème couleur Type 12: " + colorMap.get(i));
					}
					break;
				case 13 : // TYPE_BYTE_INDEXED
					IndexColorModel c = (IndexColorModel) img.getColorModel();
					colorMap = new HashMap<Integer, Color>();
					// we make a copy of the color model in the color map
					for (int i = 0; i < c.getMapSize(); i++) {
						colorMap.put(i, new Color(c.getRGB(i)));
						System.out.println(i + "ème couleur Type 13: " + colorMap.get(i));
					}
					break;
				default :
					break;
			}
			Raster rasterLu = img.getData();
			matriceLue = new int[rasterLu.getWidth()][rasterLu.getHeight()];
			System.out.println("C_ReadRaster.imgRasterLoader(), image size: " + rasterLu.getWidth() + "," + rasterLu.getHeight());
			for (int i = 0; i < matriceLue.length; i++) {
				for (int j = 0; j < matriceLue[0].length; j++) {
					Object tab = null;
					Object dataElement = rasterLu.getDataElements(i, j, tab);
					// System.out.println(o.getClass().getCanonicalName());
					rasterLu.getSampleModel().getDataType();
					if (dataElement != null && dataElement instanceof byte[]) {
						byte[] tab_byte = (byte[]) dataElement;
						matriceLue[i][matriceLue[0].length - j - 1] = (int) ((int) tab_byte[0] & 0xff);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("erreur lors de la lecture du raster (verifier le chemin et le format?)");
		}
		return matriceLue;
	}
}
