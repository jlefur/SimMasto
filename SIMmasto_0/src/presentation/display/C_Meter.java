package presentation.display;

import java.awt.Dimension;
import java.awt.Font;
import java.text.NumberFormat;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.dial.DialCap;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer;
import org.jfree.chart.plot.dial.DialTextAnnotation;
import org.jfree.chart.plot.dial.StandardDialFrame;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultValueDataset;

/** @author Audrey Realini 2011 */
public class C_Meter {
	private DefaultValueDataset data;
	private StandardDialScale scale;
	private ChartPanel pan;
	private int tailleInterval;
	private boolean multiplicateur;
	private DialTextAnnotation compteTours;

	/** Crée un meter
	 * @param title : Le nom du meter
	 * @param multiplicateur : Active ou non l'indice (le multiplicateur) au centre du meter
	 * @param interval : La valeur max du meter */
	public C_Meter(String title, boolean multiplicateur, int interval) {
		this.multiplicateur = multiplicateur;

		DialPlot plot = new DialPlot();
		this.data = new DefaultValueDataset(0); // Valeur de départ //
		plot.setDataset(this.data);

		StandardDialFrame df = new StandardDialFrame(); // NE PAS OUBLIER ! //
		plot.setDialFrame(df);
		df.setVisible(true);

		// Réglages ticks et intervalles //
		this.scale = new StandardDialScale();

		this.scale.setTickLabelFont(new Font("Arial", Font.PLAIN, (int) (this.scale.getTickLabelFont().getSize() * 1.3)));
		this.scale.setLowerBound(0); // Valeur minimum //
		this.scale.setTickRadius(0.9); // Position des ticks par rapport au centre //
		this.scale.setTickLabelOffset(0.2); // Position des chiffres par rapport au centre (TENIR COMPTE
										// DU PARAM PREC) //
		this.scale.setMajorTickIncrement((double) interval / 10.); // Interval entre 2 traits //

		if (multiplicateur) {
		    this.scale.setUpperBound(interval - 0.01); // Valeur maximum // On enlève 0.01 pour ne pas
													// avoir le dernier chiffre affiché
		    this.scale.setStartAngle(-105); // Position de la valeur minimum //
		    this.scale.setExtent(-360); // Position de la valeur maximum //
		    this.tailleInterval = ((Integer) interval).toString().length();
			this.compteTours = new DialTextAnnotation("0");
			this.compteTours.setFont(new Font("Arial", Font.BOLD, 40));
			plot.addLayer(this.compteTours);
		}
		else {
		    this.scale.setUpperBound(interval); // Valeur maximum //
		    this.scale.setStartAngle(-120); // Position de la valeur minimum //
			this.scale.setExtent(-300); // Position de la valeur maximum //
		}
		// remove decimals
		NumberFormat nf = this.scale.getTickLabelFormatter();
		if (interval < 10) nf.setMaximumFractionDigits(1);
		else nf.setMaximumFractionDigits(0);
		this.scale.setTickLabelFormatter(nf);

		plot.addScale(0, this.scale); // scale est ajouté à l'index 0

		// Réglages aiguille //
		DialPointer pointer = new DialPointer.Pin();
		plot.addPointer(pointer);
		// Correspond au rond au centre du meteur (là où il y a l'aiguille) //
		DialCap cap = new DialCap();
		cap.setRadius(0.1);
		plot.setCap(cap);

		JFreeChart chart = new JFreeChart(plot);
		chart.setTitle(new TextTitle(title, new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 28)));
		this.pan = new ChartPanel(chart);
		this.pan.setPreferredSize(new Dimension(100, 100));
		this.pan.setMaximumSize(this.pan.getPreferredSize());
	}

	public C_Meter(String title, boolean multiplicateur, int min, int max) {
		this(title, multiplicateur, max - min);
		this.scale.setLowerBound(min); // Valeur minimum //
		this.scale.setMajorTickIncrement(((double) max - (double) min) / 10); // Interval entre 2 traits //
		if (multiplicateur) this.scale.setUpperBound(max - 0.01);
		else this.scale.setUpperBound(max);
	}

	public void setData(int value) {
	    this.data.setValue(value);
		if (this.multiplicateur) gestionMultiplicateur(value);
	}

	public void setData(double value) {
	    this.data.setValue(value);
		if (this.multiplicateur) gestionMultiplicateur((int) value);
	}

	/** Met à jour le multiplicateur au centre du compteur
	 * @param value : la valeur entière de la donnée mise à jour avec setData() */
	private void gestionMultiplicateur(Integer value) {
		if (value.toString().length() >= this.tailleInterval) this.compteTours.setLabel(value / (int) (Math.pow(10, this.tailleInterval - 1)) + ""); // supprime
																																		// les
																																		// n-1
																																		// derniers
																																		// chiffres
		else this.compteTours.setLabel("0");
	}

	public ChartPanel getPan() {
		return this.pan;
	}
}
