package presentation.display;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;

import bsh.util.JConsole;
import data.C_Parameters;
import data.constants.I_ConstantImagesNames;
import data.constants.I_ConstantString;
import presentation.epiphyte.C_InspectorFossorialRodents;
import presentation.epiphyte.C_InspectorGenetic;
import presentation.epiphyte.C_InspectorHybrid;
import presentation.epiphyte.C_InspectorPopulation;
import presentation.epiphyte.C_InspectorTransportation;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.userpanel.ui.UserPanelCreator;
import simmasto0.C_ContextCreator;
import simmasto0.protocol.A_Protocol;

/** Tableau de bord de la simulation. Contient la date de la simulation (à chaque tick), des méteurs et un retour de la console.
 * @author A Realini 2011, rev. Le Fur feb., jul. 2013, may 2018 */
public class C_UserPanel extends JPanel implements UserPanelCreator, I_ConstantString, I_ConstantImagesNames {
    //
    // FIELDS
    //
    private static final long serialVersionUID = 1L;
    private static C_InspectorFossorialRodents burrowInspector = null;
    private static C_InspectorTransportation transportationInspector = null;
    private static C_InspectorGenetic geneticInspector = null;
    private static C_InspectorPopulation populationInspector = null;
    private static C_InspectorHybrid hybridInspector = null;

    private JPanel titleBox = null;
    private JPanel dateBox = null;
    private JPanel metersPopulation = null;
    private JPanel metersDispersal = null;
    private JPanel metersHybrid = null;;
    private JPanel consoleOutBox = null;
    private JPanel consoleErrBox = null;
    private JPanel daytimeJpanel = null;
    private JLabel dayMomentsJlabel = null;
    // JPanel metersTransportation = initBoxLayout("Transportation");
    // JPanel metersHybridization = initBoxLayout("Hybridization");

    public static final String METER_POPSIZE_TITLE = "Size (X100)";
    public static final String METER_SEXRATIO_TITLE = "sex ratio";
    public static final String METER_MAXDISP_TITLE = "Max (x100m)";
    public static final String METER_MEANDISP_TITLE = "Mean  (x100m)";
    public static final String METER_FIS_TITLE = "Inbreeding (FISx100)";
    public static final String METER_WANDERERS_TITLE = "Wanderers(%)";
    public static final String METER_NIPPED_EGG_TITLE = "Nipped eggs (X1000)";
    public static final String METER_HYBRIDS_TITLE = "hybrids (x100)";
    public static final String METER_LAZARUS_TITLE = "introgressed (x100)";
    //
    private Font font = new Font("Courier", Font.BOLD, 18);
    private BufferedImage img = null, chronoImage = null;
    private JLabel dateLab;
    public static JConsole consoleOut = null;
    public static JConsole consoleErr = null;

    // meters Population
    private C_Meter meterPopSize;
    private C_Meter meterSexRatio;
    // meters Dispersal
    private C_Meter meterFIS;
    private C_Meter meterMeanDispersal;
    private C_Meter meterMaxDispersal;
    private C_Meter meterWanderers;
    // meters Transportation
    private C_Meter meterCities;
    private C_Meter meterLoads;
    // meters Hybridization
    private C_Meter meterHybrids;
    private C_Meter meterIntrogressed;
    private C_Meter meterNippedEggs;
    // meters General
    // private C_Meter meterObjects;
    String currentImageName = "";

    //
    // CONSTRUCTOR
    //
    public C_UserPanel() {
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
        RunState.getInstance().getMasterContext().add(this); // TODO TMP
        init();
    }
    //
    // METHODS
    //
    /** Méthode utilisée par le UserPanelCreator pour afficher le tableau de bord dans le userPanel */
    public JPanel createPanel() {
        return this;
    }
    @ScheduledMethod(start = 0, interval = 1, shuffle = false, priority = 0)
    public void step() {
        this.dateLab.setText(C_ContextCreator.protocol.getStringFullDate());
        update_Meters();
        if (this.hasToShowDayMoments()) this.updateDaysMomentsJpanel();
    }

    public static void addBurrowInspector(C_InspectorFossorialRodents inspector) {
        burrowInspector = inspector;
    }
    public static void addTransportationInspector(C_InspectorTransportation inspector) {
        transportationInspector = inspector;
    }
    public static void addGeneticInspector(C_InspectorGenetic inspector) {
        geneticInspector = inspector;
    }
    public static void addHybridInspector(C_InspectorHybrid inspector) {
        hybridInspector = inspector;
    }
    /** Initialise les composants du tableau de bord */
    private void init() {

        this.titleBox = initBoxLayout("SimMasto project / IRD / CBGP");
        this.dateBox = initBoxLayout("Simulation Date");
        this.metersPopulation = initBoxLayout("Population (every rodents)");
        if (this.hasToShowDayMoments()) this.createDayMomentsJanel();
        if (C_UserPanel.hybridInspector == null) this.metersDispersal = initBoxLayout("Dispersal (every rodents)");
        if (C_UserPanel.hybridInspector != null) this.metersHybrid = initBoxLayout("Hybridization (Mastomys)");
        this.consoleOutBox = initBoxLayout("Console output");
        this.consoleErrBox = initBoxLayout("Console Error");

        // General layout for the panel
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.setAutoscrolls(false);

        // A. BOX TITLE //
        this.titleBox.add(createTitleBlock());

        // B. BOX DATE //
        this.dateLab = new JLabel();
        this.dateLab.setFont(this.font);
        this.dateLab.setText(C_ContextCreator.protocol.getStringFullDate());
        this.dateBox.add(this.dateLab);

        // C. BOXES METERS //
        // 1.BOX POPULATION
        this.meterPopSize = new C_Meter(METER_POPSIZE_TITLE, true, 100);
        if (this.hasToShowDayMoments()) this.metersPopulation.add(this.daytimeJpanel);
        else {
            this.meterSexRatio = new C_Meter(METER_SEXRATIO_TITLE, false, 0, 2);
            this.metersPopulation.add(this.meterSexRatio.getPan());
        }
        this.metersPopulation.add(this.meterPopSize.getPan());
        this.meterWanderers = new C_Meter(METER_WANDERERS_TITLE, false, 100);
        this.metersPopulation.add(this.meterWanderers.getPan());
        // 2.BOX DISPERSAL
        if (C_UserPanel.hybridInspector == null) {// TODO JLF 2018.05 Temporary for cages, but cannot not be applied for
                                                  // hybridUniform
            this.meterMeanDispersal = new C_Meter(METER_MEANDISP_TITLE, true, 100);
            this.meterMaxDispersal = new C_Meter(METER_MAXDISP_TITLE, true, 100);
            this.metersDispersal.add(this.meterMeanDispersal.getPan());
            this.metersDispersal.add(this.meterMaxDispersal.getPan());
            if (C_UserPanel.geneticInspector != null) {
                this.meterFIS = new C_Meter(METER_FIS_TITLE, true, -5, 5);
                this.metersDispersal.add(this.meterFIS.getPan());
            }
        }
        // 3. BOX HYBRIDIZATION
        if (C_UserPanel.hybridInspector != null) {
            this.meterNippedEggs = new C_Meter(METER_NIPPED_EGG_TITLE, true, 1000);
            this.metersHybrid.add(this.meterNippedEggs.getPan());
            this.meterHybrids = new C_Meter(METER_HYBRIDS_TITLE, true, 100);
            this.metersHybrid.add(this.meterHybrids.getPan());
            this.meterIntrogressed = new C_Meter(METER_LAZARUS_TITLE, true, 100);
            this.metersHybrid.add(this.meterIntrogressed.getPan());
        }
        // D. BOX CONSOLE OUT//
        C_UserPanel.consoleOut = createConsole();
        System.setOut(consoleOut.getOut()); // redirect output to GUI console
        this.consoleOutBox.add(C_UserPanel.consoleOut);
        // E. BOX CONSOLE ERR//
        C_UserPanel.consoleErr = createConsole();
        System.setErr(C_UserPanel.consoleErr.getErr()); // redirect error output to GUI console
        this.consoleErrBox.add(C_UserPanel.consoleErr);
    }

    private JConsole createConsole() {
        JConsole console = new JConsole();
        console.createHorizontalScrollBar();
        console.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        console.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        console.setVisible(true);
        console.setFont(new Font("serif", Font.PLAIN, 9));
        console.setPreferredSize(new Dimension(1000, 100));
        return console;
    }
    /** Met à jour les données des compteurs */
    private void update_Meters() {
        C_UserPanel.populationInspector = A_Protocol.inspector;
        int popSize = C_InspectorPopulation.rodentList.size();
        this.meterPopSize.setData(popSize);
        if (!this.hasToShowDayMoments())
            this.meterSexRatio.setData((double) C_InspectorPopulation.getNbFemales() / (double) C_InspectorPopulation.getNbMales());
        if (C_UserPanel.hybridInspector == null) {// TODO JLF 2018.05 Temporary for cages, but cannot not be applied for
                                                  // hybridUniform
            this.meterMeanDispersal.setData((populationInspector.getMeanFemaleDispersal() + populationInspector.getMeanMaleDispersal()) * .5);
            this.meterMaxDispersal.setData(Math.max(populationInspector.getMaxFemaleDispersal(), populationInspector.getMaxMaleDispersal()));
            if (geneticInspector != null) this.meterFIS.setData(100 * geneticInspector.getFixationIndex());
        }
        if (burrowInspector != null) this.meterWanderers.setData(burrowInspector.getWanderingRodents_Upercent() * 100);
        if (hybridInspector != null) {
            this.meterNippedEggs.setData(hybridInspector.getPbNippedEgg());
            hybridInspector.resetPbNippedEggs();
            this.meterHybrids.setData(hybridInspector.getNbHybrids());
            this.meterIntrogressed.setData(hybridInspector.getNbLazarus());
        }
        if (transportationInspector != null) {
            this.meterCities.setData(transportationInspector.getCityList().size());
            this.meterLoads.setData(transportationInspector.getCarriersLoad_Urodent());
        }
        // meterObjects.setData(RunState.getInstance().getMasterContext().size());
    }
    public JPanel initBoxLayout(String title) {
        JPanel onePanel = new JPanel();
        onePanel.setLayout(new BoxLayout(onePanel, BoxLayout.LINE_AXIS));
        onePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        onePanel.setSize(350, onePanel.getHeight());
        if (!title.isEmpty()) onePanel.setBorder(BorderFactory.createTitledBorder(title));
        this.add(onePanel);
        return onePanel;
    }
    /** Gestion de l'image centrale (JLF - june 2011, may 2018) */
    private JLabel createTitleBlock() {
        String fileName = "";
        switch (C_Parameters.PROTOCOL) {
            case "CHIZE" :
                fileName = "icons/titleChize.jpg";
                break;
            case "CHIZE2" :
                fileName = "icons/titleChize2.gif";
                break;
            case "CENTENAL" :
                fileName = "icons/titleCentenal.gif";
                break;
            case "DECENAL" :
                fileName = "icons/titleDecenal.gif";
                break;
            case "CAGES" :
                fileName = "icons/titleCages.gif";
                break;
            case "DODEL" :
                fileName = "icons/titleDodel1.gif";
                break;
            case "DODEL2" :
                fileName = "icons/titleDodel2.gif";
                break;
            case "MUS_TRANSPORT" :
                fileName = "icons/titleMusTransport.gif";
                break;
            case "BANDIA" :
                fileName = "icons/titleBandia.gif";
                break;
            case "GERBIL" :
                fileName = "icons/titleGerbil.gif";
                break;
            case "HYBRID_UNIFORM" :
                fileName = "icons/titleUniform.gif";
                break;
        }
        try {
            this.img = ImageIO.read(new File(fileName));
        } catch (IOException e) {
            A_Protocol.event("C_UserPanel.createTitleBlock", "Could not find " + fileName, isError);
        }
        JLabel image = new JLabel();
        image.setIcon(new ImageIcon(this.img));
        return (image);
    }
    /** It the container of the daytime display.
     * @author M SALL 12.2020, */
    private void createDayMomentsJanel() {
        this.daytimeJpanel = initBoxLayout("Daytime");
        this.daytimeJpanel.setMaximumSize(new Dimension(100, 120));
        this.daytimeJpanel.add(this.updateChronoBlock(DAY));
    }

    /** Verify the current display of the daytime JPanel and change it if necessary.
     * @author M SALL 12.2020, */
    private void updateDaysMomentsJpanel() {
        Boolean hasToSwitchImage = false;
        if (A_Protocol.protocolCalendar.isDayTime() && this.currentImageName != DAY) {
            this.currentImageName = DAY;
            hasToSwitchImage = true;
        }
        else if (A_Protocol.protocolCalendar.isTwilight() && this.currentImageName != TWILIGHT) {
            this.currentImageName = TWILIGHT;
            hasToSwitchImage = true;
        }
        else if (A_Protocol.protocolCalendar.isNightTime() && this.currentImageName != NIGHT) {
            this.currentImageName = NIGHT;
            hasToSwitchImage = true;
        }
        else if (A_Protocol.protocolCalendar.isDawn() && this.currentImageName != DAWN) {
            this.currentImageName = DAWN;
            hasToSwitchImage = true;
        }
        if (hasToSwitchImage) {
            this.updateChronoBlock(this.currentImageName);
        }

    }
    /** Create the chrono block image if it null or update it if not.
     * @author M SALL 12.2020, */
    private JLabel updateChronoBlock(String imageName) {
        try {
            this.chronoImage = ImageIO.read(new File($PATH + imageName + ext));
        } catch (IOException e) {
            A_Protocol.event("C_UserPanel.createTitleBlock", "Could not find " + "", isError);
        }
        if (this.dayMomentsJlabel != null) this.dayMomentsJlabel.setIcon(new ImageIcon(this.chronoImage.getScaledInstance(85, 85, 0)));
        else this.dayMomentsJlabel = new JLabel(new ImageIcon(this.chronoImage.getScaledInstance(85, 85, 0)));
        return this.dayMomentsJlabel;
    }
    /** Create the chrono block image if it null or update it if not.
     * @author M SALL 12.2020, */
    public boolean hasToShowDayMoments() {
        switch (C_Parameters.PROTOCOL) {
            case DODEL2 :
            case DODEL :
            case GERBIL_PROTOCOL :
                return true;
            default :
                return false;
        }
    }

    @Override
    public String toString() {
        return "SimMasto panel";
    }
}
