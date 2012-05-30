/**
 * Copyright (C) 2012 Aleksi Postari
 * License type: MIT (http://en.wikipedia.org/wiki/MIT_License)
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package VaadinIRC;

/**
 * Contains static VaadinIRC application settings.
 * @author Aleksi Postari
 *
 */
public class settings
{
	// DEBUG
	
	/** Is debug mode enabled? Set to false on production environment. */
	public static final boolean debug = false;
	
	// APPLICATION INFORMATION
	
	/** Name of the application. */
	public static final String APP_NAME = "VaadinIRC";
	/** Version. */
	public static final String VERSION = "0.82";
	
	// APPLICATION SETTINGS
	
	/** Default server address. */
	public static final String DEFAULT_SERVER_ADDRESS = "port80a.se.quakenet.org";
	/** Default server port. */
	public static final int DEFAULT_SERVER_PORT = 6667;
	/** Character encoding for messages read from server. Leave to "" to use system default. */
	public static final String READER_ENCODING = "UTF-8";
	/** Character encoding for messages written to server. Leave to "" to use system default. */
	public static final String WRITER_ENCODING = "";
	
	// IRCBOT CONFIGURATIONS
	
	/** Bot Enabled? */
	public static final boolean IRCBOT_ENABLED = true;
	/** Database driver. Defaults to com.mysql.jdbc.Driver. */
	public static final String IRCBOT_DATABASE_DRIVER = "com.mysql.jdbc.Driver";
	/** Database address. Defaults to jdbc:mysql://localhost:3306/ */
	public static final String IRCBOT_DATABASE_ADDRESS = "jdbc:mysql://localhost:3306/";
	/** Database name. Defaults to vaadinirkkia */
	public static final String IRCBOT_DATABASE_NAME = "vaadinirkkia";
	/** Database username. User should have INSERT, DELETE, SELECT privileges by minimum. */
	public static final String IRCBOT_DATABASE_USERNAME = "root";
	/** Database username's password. */
	public static final String IRCBOT_DATABASE_PASSWORD = "";
	/** First sign that is used to call bot commands. Defaults to ! */
	public static final String IRCBOT_BOT_CALL_SIGN = "!";
}
