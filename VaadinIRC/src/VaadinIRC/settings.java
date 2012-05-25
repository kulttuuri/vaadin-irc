package VaadinIRC;

/**
 * Contains static VaadinIRC application settings.
 * @author Aleksi Postari
 *
 */
public class settings
{
	/** Name of the application. */
	public static final String APP_NAME = "VaadinIRC";
	/** Version. */
	public static final String VERSION = "0.2";
	/** Default server address. */
	public static final String DEFAULT_SERVER_ADDRESS = "port80a.se.quakenet.org";
	/** Default server port. */
	public static final int DEFAULT_SERVER_PORT = 6667;
	/** Character encoding for messages read from server. Leave to "" to use system default. */
	public static final String READER_ENCODING = "UTF-8";
	/** Character encoding for messages written to server. Leave to "" to use system default. */
	public static final String WRITER_ENCODING = "";
	/** Is debug mode enabled? Set to false on production environment. */
	public static final boolean debug = false;
}
