package sftp.filetrasfer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
/**
 * 
 * @author Ramesh K
 *
 */
public class PropertiesUtility {

	private String  propertiesJson;
	private static JsonObject gsonObject = new JsonObject();
	
	public PropertiesUtility() {
		
	}
	/**
	 * This method will read the file as a string and convert to JSON OBject
	 * @param path
	 * @throws IOException
	 */
	public void  loadPropertiesJSON(String path) throws IOException {
		propertiesJson = loadInputFileDetailsConfig(path);
		JsonParser jsonParser = new JsonParser();
		gsonObject = (JsonObject) jsonParser.parse(propertiesJson);

	}
	/**
	 * This method will return all the Mail Properties as JSON Object
	 * @return
	 */
	public static JsonObject getMailProperties() {
		
		return  gsonObject.getAsJsonObject("mailproperties");
	}
	/**
	 * This method will return all path properties as JSONOBject
	 * @return
	 */
	
	public static JsonObject getPathProperties() {
		
		return  gsonObject.getAsJsonObject("pathProperties");
	}
	/**
	 * This method will return all SAPInputFiles as JSONOBject
	 * @return
	 */
	public static JsonObject getSAPInputFiles() {
		
		return gsonObject.getAsJsonObject("SAPInputFiles");
	}
	
	
	/**
	 * To get the Database config details.
	 * @return
	 */
	public static JsonObject getDBConfigs() {
		
		return gsonObject.getAsJsonObject("dbconfig");
		
	}
	/**
	 * To get the even hub configuration - connection URL.
	 * @return
	 */
	public static JsonObject getEventHubConnectionURL() {
		 return  gsonObject.getAsJsonObject("eventHubConfig");
	}
	
	/**
	 * Number of materials should be processed per batch
	 * @return
	 */
	public static JsonObject getBatchSize() {
		
		return  gsonObject.getAsJsonObject("batchSize");
	}
	
	
	
	/**
	 * Get Attributes Configuration File Path
	 * @return
	 */
	public static JsonObject getAttributesConfigPath() {
		
		return  gsonObject.getAsJsonObject("attributesConfig");
	}
	
	/**
	 * GET SFTP  Configurations.
	 * @return sftp configurations JSON Object.
	 */
	public static JsonObject getSFTPConfigJsonObject() {
		
		return  gsonObject.getAsJsonObject("SFTPConfigurations");
	}
	
	
	/**
	 * This method read the file and will return content as string
	 * @param resourceFileName
	 * @return
	 * @throws IOException
	 */
	 private String loadInputFileDetailsConfig(String resourceFileName) throws IOException {
	    	
		 	InputStream inputStream =   new FileInputStream(resourceFileName);
	    	InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
	    	@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(streamReader);
	    	StringBuilder sbBuilder = new StringBuilder();
	    	
	    	for (String line; (line = reader.readLine()) != null;) {
	    		sbBuilder.append(line);
	    	}
	    	
	    	return sbBuilder.toString();
	    }
	 
	
	
	
}
