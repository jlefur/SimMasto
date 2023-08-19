package presentation.display;

import repast.simphony.render.RenderListener;
import repast.simphony.render.RendererListenerSupport;
import repast.simphony.visualization.*;

import javax.swing.*;

/** Un onglet ajouté à la simulation
 * @author A. Realini */
public class C_CustomPanelFactory implements IDisplay {

	/** Support du RendererListener qui rafraichit ce display */
	private RendererListenerSupport support;
	private C_Chart chart;

	/** Crée un nouveau display
	 * @param title : le titre du chart
	 * @param type : le type de chart (C_Chart.LINE/PIE3D/PIE2D/RING) */
	public C_CustomPanelFactory(String title, int type) {
	    this.support = new RendererListenerSupport();
	    this.chart = new C_Chart(title, type);
	}

	/** Crée un nouveau display en précisant les noms des axes du graphique (pour les Line charts) */
	public C_CustomPanelFactory(String title, int type, String XLabel, String YLabel) {
	    this.support = new RendererListenerSupport();
	    this.chart = new C_Chart(title, type, XLabel, YLabel);
	}

	@Override
	// Appelée juste après le constructeur //
	public JPanel getPanel() {
		return this.chart.getChartPanel();
	}

	// @Override // A NE SURTOUT PAS SUPPRIMER //
	public void addRenderListener(RenderListener listener) {
	    this.support.addListener(listener);
	}

	@Override
	// A NE SURTOUT PAS SUPPRIMER //
	public void render() {
	    this.support.fireRenderFinished(this);
	}

	public C_Chart getChart() {
		return this.chart;
	}

	// OVERRIDE & UNUSED METHODS //

	public void init() {} // Called after getPanel()
	public void update() {}
	public void destroy() {}
	@Override
	public void setPause(boolean pause) {}
	public void resetHomeView() {}
	public void registerToolBar(JToolBar bar) {}
	public void addDisplayListener(DisplayListener listener) {}
	public void iconified() {}
	public void deIconified() {}
	public void closed() {}
	public void addProbeListener(ProbeListener listener) {}
	public void setLayout(Layout layout) {}
	public void setLayoutFrequency(LayoutFrequency frequency, int interval) {}
	public Layout getLayout() {
		return null;
	}
	public DisplayEditorLifecycle createEditor(JPanel panel) {
		return null;
	}
}
