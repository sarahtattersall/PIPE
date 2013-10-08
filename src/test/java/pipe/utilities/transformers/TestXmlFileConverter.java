package pipe.utilities.transformers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Converts XML file to an escaped String, so that tests need not do file I/O to test XML input
 * @author stevedoubleday
 *
 */
public class TestXmlFileConverter
{
	private static final String SLASH = System.getProperty("file.separator");

	public static void main(String[] args) {
		// copy the output from below to the string variable to be used in testing.
		System.out.println(convertXmlFileToEscapedString(buildFile("simpleNet.xml")));
	}
	protected static File buildFile(String name) {
		File file = new File(System.getProperty("user.dir")+SLASH+
				"test"+SLASH+"xml"+SLASH+name);
		return file;
	}
	public static String convertXmlFileToEscapedString(File file)  {
		String line = null; 
		StringBuffer sb = new StringBuffer(); 
		try {
			BufferedReader  reader = new BufferedReader(new FileReader(file));
			line = reader.readLine(); 
			String tempEsc = null; 
			while (line != null) {
				tempEsc = escapeCharacters(line);
				sb.append(tempEsc); 
				line = reader.readLine(); 
			}
			reader.close(); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return sb.toString();
	}
	protected static String escapeCharacters(String line)
	{
		line = line.replace("\\/", "/"); 
		line = line.replace("\\", "\\\\"); 
		line = line.replace("\"", "\\\""); 
		line = line.replace("\t", "\\\t"); 
		line = line.replace("\b", "\\\b"); 
		line = line.replace("\n", "\\\n"); 
		line = line.replace("\f", "\\\f"); 
		line = line.replace("\'", "\\\'"); 
		return line;
	}
}
