package presentation.display;

import java.awt.Color;
import java.awt.Font;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import repast.simphony.essentials.RepastEssentials;
import simmasto0.protocol.A_Protocol;

public class C_Chart {

	// Types de chart disponibles //
	public static final int LINE = 0;
	public static final int PIE3D = 1;
	public static final int PIE2D = 2;
	public static final int RING = 3;
	public static final int BAR = 4;

	private DefaultPieDataset dataPie; // Pour la représentation Pie2D, Pie3D et Ring
	private XYSeriesCollection dataLine; // Pour la représentation Line
	private CategoryDataset dataBar; // Pour la représentation Bar JLF 08.2015
	private ChartPanel chartPanel;
	private JFreeChart chart;
	private int type;

	/** Constructeur des Pie charts. Peut aussi être utilisé pour les Line charts mais les noms des axes seront par défault "X" et
	 * "Y".
	 * @param title : le titre du graphique
	 * @param type : le type de graphique */
	public C_Chart(String title, int type) {
		init(title, type, "X", "Y");
	}

	/** Constructeur d'un Line/Bar/Pie Chart
	 * @param title : le titre du graphique
	 * @param type : le type de graphique
	 * @param XLabel : le nom de l'axe des abscisses
	 * @param YLabel : le nom de l'axe des ordonnées */
	public C_Chart(String title, int type, String XLabel, String YLabel) {
		init(title, type, XLabel, YLabel);
	}

	/** Crée un nouvel ensemble de données et un graphique
	 * @param title : le titre du graphique
	 * @param type : le type de graphique
	 * @param XLabel : le nom de l'axe des abscisses (utilisé uniquement avec les Line charts)
	 * @param YLabel : le nom de l'axe des ordonnées (utilisé uniquement avec les Line charts) */
	private void init(String title, int type, String XLabel, String YLabel) {
		this.type = type;

		if (type == LINE) {
		    this.dataLine = new XYSeriesCollection();
		    this.chartPanel = new ChartPanel(createLineChart(title, XLabel, YLabel));
		}
		else if (type == BAR) {
		    this.dataBar = new DefaultCategoryDataset();
		    this.chartPanel = new ChartPanel(createBarChart(title, XLabel, YLabel));
		}
		else {
		    this.dataPie = new DefaultPieDataset();
		    this.chartPanel = new ChartPanel(createPieChart(title));
		}

		this.chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		this.chartPanel.setMouseZoomable(true, false);
		this.chartPanel.getChart().setBackgroundPaint(Color.getColor("#F0F0F0")); // Couleur de java

	}

	/** Build a chart of type Bar , JLF 08.2015 */
	private JFreeChart createBarChart(String title, String XLabel, String YLabel) {
	    this.chart = ChartFactory.createBarChart(title, XLabel, YLabel, this.dataBar, PlotOrientation.VERTICAL, true, true, true);
		CategoryPlot plot = (CategoryPlot) this.chart.getPlot();

		this.chart.addSubtitle(new TextTitle("total / city"));
		this.chart.setBackgroundPaint(Color.white);
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setDrawBarOutline(false);
		this.chart.getLegend().setFrame(BlockBorder.NONE);
		CategoryAxis thisCategoryAxis = this.chart.getCategoryPlot().getDomainAxisForDataset(0);
		thisCategoryAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 9));
		thisCategoryAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));
		plot.setBackgroundPaint(Color.white);
		plot.getRenderer().setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		plot.getRenderer().setBaseItemLabelsVisible(true);
		for (int i = 0; i < 2; i++) {
			plot.getRenderer().setSeriesItemLabelFont(i, new java.awt.Font("Times New Roman", Font.PLAIN, 7));
		}

		return this.chart;
	}

	/** Crée un chart de type Pie, Pie3D ou Ring */
	private JFreeChart createPieChart(String title) {
	    this.chart = null;

		if (type == PIE3D) {
		    this.chart = ChartFactory.createPieChart3D(title, this.dataPie, false, false, false);
		}
		else if (type == RING) {
		    this.chart = ChartFactory.createRingChart(title, this.dataPie, false, true, false);
		}
		else if (type == PIE2D) {
		    this.chart = ChartFactory.createPieChart(title, this.dataPie, false, true, false);
		}
		PiePlot plot = (PiePlot) this.chart.getPlot();
		plot.setBackgroundPaint(Color.getColor("#F0F0F0")); // Couleur de fond du pie
		if (this.type == PIE3D) plot.setForegroundAlpha(0.5f); // Pour la transparence
		plot.setOutlineVisible(false); // Définit si l'encadré autour du chart est visible ou non

		plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		plot.setLabelBackgroundPaint(Color.white);
		plot.setNoDataMessage("No data available"); // Message à afficher quand il n'y a pas de données disponibles

		return this.chart;
	}
	/** Crée un line chart */
	private JFreeChart createLineChart(String title, String XLabel, String YLabel) {

		JFreeChart chart = ChartFactory.createXYLineChart(title, XLabel, YLabel, this.dataLine, PlotOrientation.VERTICAL, true, true,
				false);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.white); // Couleur de fond du graphe //
		plot.setDomainGridlinePaint(Color.lightGray); // Couleur des lignes horizontales //
		plot.setRangeGridlinePaint(Color.lightGray); // Couleur des lignes verticales //
		// plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		// plot.setDomainCrosshairVisible(false);
		// plot.setRangeCrosshairVisible(false);

		// Gestion légende //
		// LegendTitle lt = new LegendTitle(plot);
		// lt.setItemFont(new Font("Dialog", Font.PLAIN, 9));
		// lt.setFrame(new BlockBorder(Color.white));
		// lt.setPosition(RectangleEdge.BOTTOM);
		// XYTitleAnnotation ta = new XYTitleAnnotation(0.98, 0.02, lt,RectangleAnchor.BOTTOM_RIGHT);
		// ta.setMaxWidth(0.48);
		// plot.addAnnotation(ta);

		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
		renderer.setSeriesLinesVisible(0, true); // Affichage lignes
		renderer.setSeriesShapesVisible(0, false); // Affichage carrés

		// Gestion valeurs sur les axes //
		// ValueAxis xAxis = plot.getDomainAxis();
		// xAxis.setLowerMargin(0);
		// ValueAxis yAxis = plot.getRangeAxis();
		// yAxis.setLowerMargin(0);

		return chart;
	}

	/** Ajoute une donnée à la série en paramètre.
	 * @param title : le titre de la série utilisé lors de sa création
	 * @param Xvalue : la valeur en X
	 * @param Yvalue : la valeur en Y (null pour PIE2D, PIE3D, RING); */
	public void addData(String title, Number Xvalue, Number Yvalue) {
		if (type == LINE) this.dataLine.getSeries(title).add(Xvalue, Yvalue);
		else {
			int numItem = this.dataPie.getItemCount();
			this.dataPie.insertValue(numItem, title, Xvalue);
			// adding colors to the pie sectors - ad hoc for the sex-ratio pie - JLeFur 02.2013
			Color[] colors = {new Color(127, 127, 127), Color.LIGHT_GRAY, Color.WHITE, Color.BLACK};
			PiePlot plot = (PiePlot) this.chart.getPlot();
			Comparable key = this.dataPie.getKey(numItem);
			plot.setSectionPaint(key, colors[numItem]);
		}
	}

	/** Ajoute une série (courbe) au chart (à utiliser avec les lines charts).
	 * @param title : le titre de la série */
	public void addXYSerie(String title) {
		if (type == LINE) this.dataLine.addSeries(new XYSeries(title));
	}
	/** Met à jour les données d'un chart
	 * @param serie : le nom de la série à mettre à jour
	 * @param Xvalue : la valeur en X
	 * @param Yvalue : la valeur en Y (null pour PIE2D, PIE3D, RING); */
	public void setData(String serie, Number Xvalue, Number Yvalue) {
		if (type == LINE) this.dataLine.getSeries(serie).add(Xvalue, Yvalue);
		else this.dataPie.setValue(serie, Xvalue);
	}
	/** Met à jour les données d'un bar chart, JLF 08.2015
	 * @param value : la valeur de la barre
	 * @param serie : le nom de la barre à mettre à jour
	 * @param category : la série de données globale */
	public void setBarData(int value, String serie, String category) {
		((DefaultCategoryDataset) this.dataBar).setValue(value, serie, category);
	}

	public ChartPanel getChartPanel() {
		return this.chartPanel;
	}

	public XYSeriesCollection getDataLine() {
		return this.dataLine;
	}
	public CategoryDataset getDataBar() {
		return this.dataBar;
	}

	public JFreeChart getChart() {
		return this.chart;
	}

	public void setTitle(String title) {
	    this.chart.setTitle((int) RepastEssentials.GetTickCount() + " / " + A_Protocol.protocolCalendar.stringHourDate() + ": " + title);
	}
}
