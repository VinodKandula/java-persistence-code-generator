/**
 * 
 */
package in.sivalabs.jpcg.core.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * @author Siva
 *
 */
public class Utilities {
	
	public static String classpathFileToString(String tmplName)
	{
		try {
			StringBuffer sb = new StringBuffer();

			InputStream stream = Utilities.class.getClass().getResourceAsStream("/"+tmplName);
			BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
			for (int c = br.read(); c != -1; c = br.read()) sb.append((char)c);

			return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
	/**
	 * Convert a column name with underscores to the corresponding property name using "camel case".  A name
	 * like "customer_number" would match a "customerNumber" property name.
	 * @param name the column name to be converted
	 * @return the name using "camel case"
	 */
	public static String convertUnderscoreNameToPropertyName(String name) {
		StringBuilder result = new StringBuilder();
		boolean nextIsUpper = false;
		if (name != null && name.length() > 0) {
			if (name.length() > 1 && name.substring(1,2).equals("_")) {
				result.append(name.substring(0, 1).toUpperCase());
			}
			else {
				result.append(name.substring(0, 1).toLowerCase());
			}
			for (int i = 1; i < name.length(); i++) {
				String s = name.substring(i, i + 1);
				if (s.equals("_")) {
					nextIsUpper = true;
				}
				else {
					if (nextIsUpper) {
						result.append(s.toUpperCase());
						nextIsUpper = false;
					}
					else {
						result.append(s.toLowerCase());
					}
				}
			}
		}
		return result.toString();
	}
	
	public static String convertUnderscoreNameToClassName(String name) {
		String str = convertUnderscoreNameToPropertyName(name);
		return (""+str.charAt(0)).toUpperCase()+str.substring(1);
	}
	
	public static String capitalize(String str) {
		return (""+str.charAt(0)).toUpperCase()+str.substring(1);
	}
	
	public static String camelCase(String str) {
		return (""+str.charAt(0)).toLowerCase()+str.substring(1);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T unmarshal(String fileName, Class<T> type) {
		T val = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(type);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();			
			File file = new File(fileName);
			val = (T) jaxbUnmarshaller.unmarshal(file);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return val;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T unmarshal(InputStream is, Class<T> type) {
		T val = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(type);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();			
			val = (T) jaxbUnmarshaller.unmarshal(is);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return val;
	}
	
	public static void writeFile(String filePath, String content) {
		try {

			File file = new File(filePath);
			
			File dir = file.getParentFile();
			if (!dir.exists()) {
				dir.mkdirs();
			}
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();

			//System.out.println("Done");

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
}
