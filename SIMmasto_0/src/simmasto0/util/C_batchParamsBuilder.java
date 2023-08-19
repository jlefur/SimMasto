package simmasto0.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import presentation.dataOutput.C_FileWriter;
import data.C_ReadWriteFile;
import data.constants.I_ConstantString;
/**
 * @author Pape Adama MBOUP 2014 revu 28/06/2015
 */
public class C_batchParamsBuilder extends JFrame implements I_ConstantString,ActionListener, MouseListener {
	private static final long serialVersionUID = 1L;
	private JTextField params;
	private JTextField initXmlFileName;
	JButton bouton;
	public C_batchParamsBuilder() {
		Build();
	}
	public void Build() {
		addMouseListener(this);
		setTitle("Batch parameters Builder");
		setSize(500, 150);
		setLocation(80, 100);
		setLocationRelativeTo(null);
		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // On dit à l'application de se fermer lors du clic sur la croix
		setContentPane(buildContentPane());
		params.addMouseListener(this);
		initXmlFileName.addMouseListener(this);
	}
	private JPanel buildContentPane() {
		JPanel panel = new JPanel();
		// panel.setLayout(new FlowLayout());
		panel.setLayout(new BorderLayout());
		panel.add(new JLabel(" We are in file SIMmasto_O/batch/.  For many param use ; separator."), BorderLayout.NORTH);

		params = new JTextField("param_name : a:pas:b OR param_name : space separator lils of values");
		params.setColumns(42);
		params.setForeground(Color.GRAY);
		initXmlFileName = new JTextField("batch_params_file.xml");
		initXmlFileName.setColumns(42);
		initXmlFileName.setForeground(Color.GRAY);
		// txt.setBackground(Color.CYAN);
		JPanel top = new JPanel();
		top.add(initXmlFileName);
		top.add(params);
		panel.add(top, BorderLayout.CENTER);

		bouton = new JButton("Genrate Params Files");
		panel.add(bouton, BorderLayout.SOUTH);
		bouton.setBackground(Color.green);
		bouton.addActionListener(this);

		return panel;
	}

//	public static void main(String[] args) {
//		C_batchParamsBuilder fenetre = new C_batchParamsBuilder();
//		fenetre.setVisible(true);
//	}
//	public static void main(String[] args) {
//		String initParameterName = "20160908-batch_params_CENTENAL.xml";
//		String newParamsDatas = "VEHICLE_LOADING_PROBA_DIVIDER:1 10 100 500 1000 5000 10000 20000 50000 100000 500000 1000000";
//		buildBatchParametersFiles(initParameterName, newParamsDatas);
//	}
	public static void main(String[] args) {
		String initParameterName = "20160908-batch_params_CENTENAL.xml";
		String newParamsDatas = "VEHICLE_LOADING_PROBA_DIVIDER:1 10 100 500 1000 5000 10000 20000 50000 100000 500000 1000000";
		buildBatchParametersFiles(initParameterName, newParamsDatas);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bouton) {
			buildBatchParametersFiles(initXmlFileName.getText(), params.getText());;
			// System.exit(0);
		}
	}
	public static void buildBatchParametersFiles(String initParameterName, String newParamsDatas) {
		BufferedReader buffer = C_ReadWriteFile.openBufferReader("batch/", initParameterName);
		C_FileWriter bash_paramFile = null;
		ArrayList<String> paramData = new ArrayList<String>();
		Map<Integer, Integer> oldIndicesList0 = new HashMap<Integer, Integer>();
		Map<Integer, String[]> oldValuesAnLines0 = new HashMap<Integer, String[]>();
		String oldVal = "", oldLine = "", readLine;
		ArrayList<String> namesList = new ArrayList<String>();
		ArrayList<String[]> NewValListArray = new ArrayList<String[]>();
		String[] newValList = null;
		if (newParamsDatas.contains(CSV_FIELD_SEPARATOR) || newParamsDatas.contains(":")) {
			String[] newParamsDatasSplited;
			if (newParamsDatas.contains(CSV_FIELD_SEPARATOR))
				newParamsDatasSplited = newParamsDatas.split(CSV_FIELD_SEPARATOR);
			else 
				newParamsDatasSplited = new String[]{newParamsDatas};
			for (String oneNewParamsDatas : newParamsDatasSplited) {
				if (oneNewParamsDatas.contains(":")) {
					String[] strTab = oneNewParamsDatas.split(":");
					namesList.add("name=\"" + strTab[0].trim() + "\"");
					if (strTab.length == 2)
						newValList = strTab[1].trim().split(" ");
					else {
						int a = 0, pas = 0, b = 0;
						if (strTab.length == 3) {
							a = Integer.parseInt(strTab[1].trim());
							pas = 1;
							b = Integer.parseInt(strTab[2].trim());
						}
						else if (strTab.length == 4) {
							a = Integer.parseInt(strTab[1].trim());
							pas = Integer.parseInt(strTab[2].trim());
							b = Integer.parseInt(strTab[3].trim());
						}
						else {
							System.err.println("nothing : check 2nd data field");
							return;
						}
						newValList = new String[1 + (b - a) / pas];
						int j = 0;
						for (int i = a; i <= b; i += pas)
							newValList[j++] = i + "";
					}
					NewValListArray.add(newValList);
				}
				else {
					System.err.println("nothing : check 2nd data field");
					return;
				}
			}
		}
		else {
			System.err.println("nothing : check 2nd data field");
			return;
		}
		try {
			readLine = buffer.readLine();
			String[] tmp;
			int indice;
			while (readLine != null) {
				readLine = readLine.replace(" = \"", "=\"");
				readLine = readLine.replace("= \"", "=\"");
				readLine = readLine.replace(" =\"", "=\"");
				indice = 0;
				for (String oneName : namesList) {
					if (readLine.contains(oneName)) {
						indice = namesList.indexOf(oneName);
						tmp = new String[2];
						tmp[0] = readLine.split("value=\"")[1].split("\"")[0];//ex. valeur dans value="valeur"
						tmp[1] = readLine;
						oldValuesAnLines0.put(indice, tmp);
						oldIndicesList0.put(indice, paramData.size());
					}
				}
				paramData.add(readLine);
				readLine = buffer.readLine();
			}
			for(String oneName : namesList)
				if(! oldValuesAnLines0.keySet().contains(namesList.indexOf(oneName)))
					System.err.println("This name '"+oneName+"' is not good or is not in "+initParameterName);
			
			int n = namesList.size() - 1, m = NewValListArray.get(0).length, indiceList[] = new int[n + 1], indiceList2[] = new int[n + 1];
			for (int i = 0; i <= n; i++)
				indiceList[i] = 0;
			indiceList2[n] = 1;
			for (int i = n - 1; i >= 0; i--)
				indiceList2[i] = indiceList2[i + 1] * NewValListArray.get(i + 1).length;
			m = indiceList2[0] * NewValListArray.get(0).length;
			int j = 0, k = 0;
			DecimalFormat df = new DecimalFormat("000");
			String oneNewVal;
			List<String> forOutputCsv = new ArrayList<String>();
			//for Csv Output
			String str = "";
			for(String name : namesList)
				str += CSV_FIELD_SEPARATOR+name.split("\"")[1];
			forOutputCsv.add(str);
			
			while (k < m) {
				str = "";
				k++;
				for (int i = 0; i <= n; i++) {
					oneNewVal = NewValListArray.get(i)[indiceList[i]];
					oldVal = oldValuesAnLines0.get(i)[0];
					oldLine = oldValuesAnLines0.get(i)[1];
					int oldIndice = oldIndicesList0.get(i);
					paramData.set(oldIndice, oldLine.replace("value=\"" + oldVal, "value=\"" + oneNewVal));
					
					if (k % indiceList2[i] == 0) indiceList[i]++;
					if (indiceList[i] == NewValListArray.get(i).length) indiceList[i] = 0;
					//for Csv Output
					str += CSV_FIELD_SEPARATOR+oneNewVal;
				}
				bash_paramFile = new C_FileWriter("batch/batchParametersGenerated/batch_params" + df.format(j) + ".xml", false);
				for(String line : paramData)
					bash_paramFile.writeln(line);
				bash_paramFile.closeFile();
				//for Csv Output
				forOutputCsv.add("batch_params"+df.format(j)+".xml"+str);
				
				j++;
			}
			//for Csv Output
			bash_paramFile = new C_FileWriter("batch/batchParametersGenerated/global_batch_params.csv", false);
			for(String line : forOutputCsv)
				bash_paramFile.writeln(line);
			bash_paramFile.closeFile();
			
			
			System.out.println("C_batchParamsBuilder.buildBatchParametersFiles() : " + m + " files built from "+initParameterName);
			System.out.println("C_batchParamsBuilder.buildBatchParametersFiles() : changing: "+newParamsDatas);
		} catch (Exception e) {
			System.err.println("C_batchParamsBuilder.buildBatchParametersFiles() : Erreur de parcours du résultat " + e.getMessage());
			e.printStackTrace();
		}
		finally {
			try {
				buffer.close();
				bash_paramFile.closeFile();
			} catch (Exception e) {
				System.err.println("C_batchParamsBuilder.buildBatchParametersFiles() : buffer or rasterFile closing error " + e.getMessage());
			}
		}
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == params) if (params.getText().contains("param_name : a:pas:b OR param_name : space separator lils of values")) {
			params.setForeground(Color.BLACK);
			params.setText("");
		}
		if (e.getSource() == initXmlFileName) if (initXmlFileName.getText().contains("batch_params_file.xml")) {
			initXmlFileName.setForeground(Color.BLACK);
			initXmlFileName.setText("");
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		if (params.getText().length() == 0) {
			params.setForeground(Color.LIGHT_GRAY);
			params.setText("param_name : a:pas:b OR param_name : space separator lils of values");
		}
		if (initXmlFileName.getText().length() == 0) {
			initXmlFileName.setForeground(Color.LIGHT_GRAY);
			initXmlFileName.setText("batch_params_file.xml");
		}
	}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
}
