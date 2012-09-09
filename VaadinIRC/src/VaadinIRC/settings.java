/**
 * Copyright (C) 2012 Aleksi Postari (@kulttuuri, aleksi@postari.net)
 * License type: MIT (http://en.wikipedia.org/wiki/MIT_License)
 * This code is part of project Vaadin Irkkia.
 * License in short: You can use this code as you wish, but please keep this license information intach or credit the original author in redistributions.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package VaadinIRC;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;

/**
 * Contains static VaadinIRC application settings.
 * @author Aleksi Postari
 *
 */
public class settings
{
	// HARDCODED APPLICATION INFORMATION
	
	/** Name of the application. */
	public static final String APP_NAME = "VaadinIRC";
	/** Version. */
	public static final String VERSION = "0.831";
	
	// AUTHENTICATION
	
	/** If this is enabled, login panel is shown to user when he opens up the VaadinIRC application. */
	public static boolean AUTHENTICATION_ENABLED = true;
	/** Username that user needs to type in to use the application. */
	public static String AUTHENTICATION_USERNAME = "";
	/** Password that user needs to type in to use the application. */
	public static String AUTHENTICATION_PASSWORD = "";
	
	// APPLICATION CONFIGURATIONS
	
	/** Is debug mode enabled? Set to false on production environment. */
	public static boolean debug = false;
	/** Default IRC server address. */
	public static String DEFAULT_SERVER_ADDRESS = "port80a.se.quakenet.org";
	/** Default IRC server port. */
	public static int DEFAULT_SERVER_PORT = 6667;
	/** Character encoding for messages read from server. Leave as "" to use system default. */
	public static String READER_ENCODING = "UTF-8";
	/** Character encoding for messages written to server. Leave as "" to use system default. */
	public static String WRITER_ENCODING = "";
	
	// IRCBOT CONFIGURATIONS
	
	/** Bot Enabled? */
	public static boolean IRCBOT_ENABLED = true;
	/** Database driver. Defaults to com.mysql.jdbc.Driver. */
	public static String IRCBOT_DATABASE_DRIVER = "com.mysql.jdbc.Driver";
	/** Database address. Defaults to jdbc:mysql://localhost:3306/ */
	public static String IRCBOT_DATABASE_ADDRESS = "jdbc:mysql://localhost:3306/";
	/** Database name. Defaults to vaadinirkkia. */
	public static String IRCBOT_DATABASE_NAME = "vaadinirkkia";
	/** Database username. User should have INSERT, DELETE, SELECT privileges by minimum. */
	public static String IRCBOT_DATABASE_USERNAME = "root";
	/** Database username's password. */
	public static String IRCBOT_DATABASE_PASSWORD = "";
	/** First sign that is used to call bot commands. Defaults to ! */
	public static String IRCBOT_BOT_CALL_SIGN = "!";
	
	/**
	 * Loads all settings from file inside package /config/vaadinirc_settings.ini.
	 */
	public static final void loadSettingsFromFile()
	{
		String path = "/config/vaadinirc_settings.ini";
		// Application settings
		debug = getBooleanProperty(path, "debug", debug);
		DEFAULT_SERVER_ADDRESS = getStringProperty(path, "DEFAULT_SERVER_ADDRESS", DEFAULT_SERVER_ADDRESS);
		DEFAULT_SERVER_PORT = getIntProperty(path, "DEFAULT_SERVER_PORT", DEFAULT_SERVER_PORT);
		READER_ENCODING = getStringProperty(path, "READER_ENCODING", READER_ENCODING);
		WRITER_ENCODING = getStringProperty(path, "WRITER_ENCODING", WRITER_ENCODING);
		// Authentication settings
		AUTHENTICATION_ENABLED = getBooleanProperty(path, "AUTHENTICATION_ENABLED", true);
		AUTHENTICATION_USERNAME = getStringProperty(path, "AUTHENTICATION_USERNAME", "");
		AUTHENTICATION_PASSWORD = getStringProperty(path, "AUTHENTICATION_PASSWORD", "");
		// Ircbot settings
		IRCBOT_ENABLED = getBooleanProperty(path, "IRCBOT_ENABLED", IRCBOT_ENABLED);
		IRCBOT_DATABASE_DRIVER = getStringProperty(path, "IRCBOT_DATABASE_DRIVER", IRCBOT_DATABASE_DRIVER);
		IRCBOT_DATABASE_ADDRESS = getStringProperty(path, "IRCBOT_DATABASE_ADDRESS", IRCBOT_DATABASE_ADDRESS);
		IRCBOT_DATABASE_NAME = getStringProperty(path, "IRCBOT_DATABASE_NAME", IRCBOT_DATABASE_NAME);
		IRCBOT_DATABASE_USERNAME = getStringProperty(path, "IRCBOT_DATABASE_USERNAME", IRCBOT_DATABASE_USERNAME);
		IRCBOT_DATABASE_PASSWORD = getStringProperty(path, "IRCBOT_DATABASE_PASSWORD", IRCBOT_DATABASE_PASSWORD);
		IRCBOT_BOT_CALL_SIGN = getStringProperty(path, "IRCBOT_BOT_CALL_SIGN", IRCBOT_BOT_CALL_SIGN);
	}

	/**
	 * Gets integer type property from given file inside current package.
	 * @param pathToFile Path to file inside archive.
	 * @param property Property key you want to get.
	 * @param defaultValue Default value if property was not found or there were other errors.
	 * @return Returns the value that was got from settings file or defaultValue on errors.
	 */
	private static final int getIntProperty(String pathToFile, String property, int defaultValue)
	{
		InputStream in = null;
		String readValue = "";
		try
		{
			Properties properties = new Properties();
			in = Application.class.getClassLoader().getResourceAsStream(pathToFile);
			properties.load(in);
			readValue = properties.getProperty(property);
			in.close();
		}
		catch (Exception e)
		{
			try { if (in != null) in.close(); } catch (IOException ee) { }
			System.out.println(e);
			e.printStackTrace();
		}
		finally
		{
			try { if (in != null) in.close(); } catch (IOException ee) { }
		}
		
		try
		{
			if (readValue == null || readValue.trim().equals("")) return defaultValue;
			int returnNumber = Integer.parseInt(readValue);
			return returnNumber;
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}
	
	/**
	 * Gets boolean type property from given file inside current package.
	 * @param pathToFile Path to file inside archive.
	 * @param property Property key you want to get.
	 * @param defaultValue Default value if property was not found or there were other errors.
	 * @return Returns the value that was got from settings file or defaultValue on errors.
	 */
	private static final boolean getBooleanProperty(String pathToFile, String property, boolean defaultValue)
	{
		InputStream in = null;
		String readValue = "";
		try
		{
			Properties properties = new Properties();
			in = Application.class.getClassLoader().getResourceAsStream(pathToFile);
			properties.load(in);
			readValue = properties.getProperty(property);
			in.close();
		}
		catch (Exception e)
		{
			try { if (in != null) in.close(); } catch (IOException ee) { }
			System.out.println(e);
			e.printStackTrace();
		}
		finally
		{
			try { if (in != null) in.close(); } catch (IOException ee) { }
		}
		
		if (readValue == null || readValue.trim().equals("")) return defaultValue;
		if (readValue.equalsIgnoreCase("true")) return true;
		else return false;
	}
	
	/**
	 * Gets String type property from given file inside current package.
	 * @param pathToFile Path to file inside archive.
	 * @param property Property key you want to get.
	 * @param defaultValue Default value if property was not found or there were other errors.
	 * @return Returns the value that was got from settings file or defaultValue on errors.
	 */
	private static final String getStringProperty(String pathToFile, String property, String defaultValue)
	{
		InputStream in = null;
		String returnString = "";
		try
		{
			Properties properties = new Properties();
			in = Application.class.getClassLoader().getResourceAsStream(pathToFile);
			properties.load(in);
			returnString = properties.getProperty(property);
			in.close();
		}
		catch (Exception e)
		{
			try { if (in != null) in.close(); } catch (IOException ee) { }
			System.out.println(e);
			e.printStackTrace();
		}
		finally
		{
			try { if (in != null) in.close(); } catch (IOException ee) { }
		}
		if (returnString == null || returnString.trim().equals("")) return defaultValue;
		return returnString;
	}
}