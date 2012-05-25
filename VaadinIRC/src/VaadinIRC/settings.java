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
	public static final boolean debug = true;
	
	// APPLICATION INFORMATION
	
	/** Name of the application. */
	public static final String APP_NAME = "VaadinIRC";
	/** Version. */
	public static final String VERSION = "0.2";
	
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
	/** Database name. Defaults to Vaadin_Irkkia */
	public static final String IRCBOT_DATABASE_NAME = "vaadinirkkia";
	/** Database username. User should have INSERT, DELETE, SELECT privileges by minimum. */
	public static final String IRCBOT_DATABASE_USERNAME = "root";
	/** Database username's password. */
	public static final String IRCBOT_DATABASE_PASSWORD = "";
}
