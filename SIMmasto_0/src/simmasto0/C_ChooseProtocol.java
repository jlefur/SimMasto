package simmasto0;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import data.C_ReadWriteFile;
import data.constants.I_ConstantString;
import presentation.dataOutput.C_FileWriter;

/** Display buttons to select one of SimMasto protocols, then split xml file to set the protocol<br>
 * Mboup, 2014, rev. Le Fur, 2018 may-july<br>
 * <br>
 * 1.-CHIZE: field vole population in a dynamic agricultural landscape<br>
 * 1.-CHIZE2: field vole population in a dynamic agricultural landscape + MAP<br>
 * 2.-CAGES: laboratory experiment on hybridization<br>
 * 3.-ENCLOSURE: enclosure experiment on hybridization<br>
 * 3bis.-HYBRID_UNIFORM: hybridization simulation in an uniform plane<br>
 * 4.-CENTENAL: colonization of the black rat in Senegal<br>
 * 4bis.deprecated -DECENAL: colonization of the black rat in south-east Senegal<br>
 * 5.-BANDIA: CMR simulation in an african reserve<br>
 * 6.-MUS_TRANSPORT: colonization of North Senegal by the house mouse<br>
 * 7.-DODEL: commensal rodent dynamics in urban landscape<br>
 * 8.-GERBILS: Gerbillus nigeriae in North Senegal (CERISE project)<br>
*/
public class C_ChooseProtocol extends JFrame implements ActionListener, I_ConstantString {
	private static final long serialVersionUID = 1L;
	private JPanel panel = new JPanel();
	private BufferedImage buttonIcon = null;
	private JPanel chizeBox = new JPanel();
	private JPanel transportationBox1 = new JPanel();
	private JPanel transportationBox2 = new JPanel();
	private JPanel hybridizationBox = new JPanel();
	private JPanel CMRBox = new JPanel();
	private JPanel DodelBox = new JPanel();
	private JPanel gerbilBox = new JPanel();

	public static void main(String[] args) {
		new C_ChooseProtocol();
	}
	public C_ChooseProtocol() {
		super("- Choose one simulation protocol (SimMasto project - IRD/CBGP)");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // impératif cette ligne
		this.setLocation(560, 10);
		this.setContentPane(panel);
		this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.PAGE_AXIS));
		
		gerbilBox.setLayout(new BoxLayout(gerbilBox, BoxLayout.X_AXIS));
		gerbilBox.setBorder(BorderFactory.createTitledBorder("Sahelian landscape (circadian rythms)"));
		this.gerbilBox.add(addImageButton("icons/titleGerbil.gif", "GERBILS"));
		this.gerbilBox.add(addImageButton("icons/titleEmpty.gif", ""));
		this.panel.add(gerbilBox);

		DodelBox.setLayout(new BoxLayout(DodelBox, BoxLayout.X_AXIS));
		DodelBox.setBorder(BorderFactory.createTitledBorder("Urban ecology (circadian rythms)"));
		this.DodelBox.add(addImageButton("icons/titleDodel1.gif", "DODEL"));
		this.DodelBox.add(addImageButton("icons/titleDodel2.gif", "DODEL2"));
		this.panel.add(DodelBox);

		transportationBox2.setLayout(new BoxLayout(transportationBox2, BoxLayout.X_AXIS));
		transportationBox2.setBorder(BorderFactory.createTitledBorder("Transportation in Senegal (2)"));
		this.transportationBox2.add(addImageButton("icons/titleMusTransport.gif", "MUS_TRANSPORT"));
		this.transportationBox2.add(addImageButton("icons/titleEmpty.gif", ""));
		this.panel.add(transportationBox2);
		transportationBox1.setLayout(new BoxLayout(transportationBox1, BoxLayout.X_AXIS));
		transportationBox1.setBorder(BorderFactory.createTitledBorder("Transportation in Senegal (1)"));
		this.transportationBox1.add(addImageButton("icons/titleCentenal.gif", "CENTENAL"));
		this.transportationBox1.add(addImageButton("icons/titleDecenal.gif", "DECENAL"));
		this.panel.add(transportationBox1);

		CMRBox.setLayout(new BoxLayout(CMRBox, BoxLayout.X_AXIS));
		CMRBox.setBorder(BorderFactory.createTitledBorder("Catch-Mark-Recatch apparatus"));
		this.CMRBox.add(addImageButton("icons/titleBandia.gif", "BANDIA"));
		this.CMRBox.add(addImageButton("icons/titleEmpty.gif", ""));
		this.panel.add(CMRBox);

		hybridizationBox.setLayout(new BoxLayout(hybridizationBox, BoxLayout.X_AXIS));
		hybridizationBox.setBorder(BorderFactory.createTitledBorder("Hybridization"));
		this.hybridizationBox.add(addImageButton("icons/titleCages.gif", "CAGES"));
		this.hybridizationBox.add(addImageButton("icons/titleUniform.gif", "HYBRID_UNIFORM"));
		this.panel.add(hybridizationBox);

		chizeBox.setLayout(new BoxLayout(chizeBox, BoxLayout.X_AXIS));
		chizeBox.setBorder(BorderFactory.createTitledBorder("Changing agricultural landscape"));
		this.chizeBox.add(addImageButton("icons/titleChize1.gif", "CHIZE"));
		this.chizeBox.add(addImageButton("icons/titleChize2.gif", "CHIZE2"));
		this.panel.add(chizeBox);

		this.pack();
		this.setVisible(true);
	}
	private JButton addImageButton(String iconFilename, String protocolName) {
		try {
			buttonIcon = ImageIO.read(new File(iconFilename));
		} catch (IOException e) {}
		JButton protocolButton = new JButton();
		protocolButton.addActionListener(this);
		protocolButton.setIcon(new ImageIcon(buttonIcon));
		protocolButton.setToolTipText(protocolName);
		protocolButton.setActionCommand(protocolName);
		protocolButton.setAlignmentX(Component.LEFT_ALIGNMENT); 
		return protocolButton;

	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String xmlFileName = "parameters_scenario_" + e.getActionCommand() + ".txt";
		splitRepastXmlConfigFiles(xmlFileName);
		System.out.println(e.getActionCommand() + " Protocol choosed");
//		String[] args = new String[]{"C:\\Users\\sallmous\\Documents\\Workspace_Moussa\\SIMmasto_0\\SIMmasto_0.rs"};
//		 repast.simphony.runtime.RepastMain.main(args);
		System.exit(0);
	} 
	/** Build parameters.xml and scenario.xml files from the merged file : parameter_scenario_protocolName.xml file The merged
	 * file must be in SIMmasto_0.rs/, and files built will be in the same folder.
	 * @param xmlFileName */
	public void splitRepastXmlConfigFiles(String xmlFileName) {
		BufferedReader buffer = C_ReadWriteFile.openBufferReader(REPAST_PATH, xmlFileName);
		C_FileWriter writingXmlFile = null;
		String readLine;
		try {
			// Write the next lines in a matrix before going on with the raterFile
			readLine = buffer.readLine();
			while (readLine != null) {
				if (readLine.contains("<?xml") && !readLine.trim().startsWith("<!--")) {
					String fistReadLine = readLine; // on sauvegarde la première ligne
					readLine = buffer.readLine(); // on passe à la deuxième pr récupèrer le nom
					// Récupération du nom. example: parameters.xml ou scenario.xml d'une ligne comme
					// <!--fileName:parameters.xml-->
					// ou
					// <!--fileName:scenario.xml-->
					String xmlConfigFileName = readLine.replace(" ", "").split(":")[1].split(".xml")[0] + ".xml";
					// Création du fichier .xml en construction
					writingXmlFile = new C_FileWriter(REPAST_PATH + xmlConfigFileName, false);
					// Ecriture de la première ligne (<?xml version="1.0" encoding="UTF-8" ?>)
					writingXmlFile.writeln(fistReadLine);
				} // Ecriture du reste du fichier
				writingXmlFile.writeln(readLine);
				readLine = buffer.readLine();
			}
		} catch (Exception e) {
			System.err.println("C_ChooseProtocol.splitRepastXmlConfigFiles : Error  reading or writing xml file " + e.getMessage());
			e.printStackTrace();
		}
		finally {
			try {
				buffer.close();
				writingXmlFile.closeFile();
			} catch (Exception e) {
				System.err.println("C_ChooseProtocol.splitRepastXmlConfigFiles : buffer or writingXmlFile closing error" + e.getMessage());
			}
		}
	}
}