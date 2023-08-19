package data;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import simmasto0.protocol.A_Protocol;

public class C_ReadWriteFile implements data.constants.I_ConstantString {
	/** Open a buffer reader for a file
	 * @param url
	 * @param fileName (with extension) */
	public static BufferedReader openBufferReader(String url, String fileName) {
		// System.out.println("C_ReadWriteFile.openBufferReader(): reading file " + url + fileName);
		try {
			return new BufferedReader(new FileReader(url + fileName));
		} catch (IOException ioe) {
			A_Protocol.event("C_ReadWriteFile.openBufferReader","File read error, please verify file :" + url + fileName + ". "
					+ ioe.getMessage(), isError);
		}
		return null;
	}
}