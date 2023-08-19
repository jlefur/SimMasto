package presentation.dataOutput;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import javax.swing.JTextArea;

import data.constants.I_ConstantString;
/** @author Audrey Realini - 2011 All that appears in the console is backed up in the file */
public class C_OutputConsole implements Runnable, I_ConstantString {
	private File file;
	private JTextArea area;
	private InputStreamReader reader;
	private BufferedReader buff;

	public C_OutputConsole() {
	    this.file = new File(CONSOLE_OUTPUT_FILE);
		try {
			System.setOut(new PrintStream(this.file));
			this.reader = new InputStreamReader(new FileInputStream(this.file));
			this.buff = new BufferedReader(this.reader);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		/*
		 * The file is overwritten at the start of each simulation
		 */
	}
	public C_OutputConsole(JTextArea area) {
		super();
		this.area = area;
		this.file.setReadable(true);
	}

	public void write() throws IOException {
	    this.buff = new BufferedReader(this.reader);
		System.out.println("Youhou");
		try {
		    this.area.append(this.buff.readLine() + "\n");
		    this.buff = null;
		} catch (Exception e) {
			System.out.println(e.toString());
		}

	}

	@Override
	public void run() {}
}
