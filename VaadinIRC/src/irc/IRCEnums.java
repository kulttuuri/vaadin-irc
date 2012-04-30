package irc;

/**
 * Contains some important enums for IRC protocol.
 * @author Aleksi Postari
 *
 */
public class IRCEnums
{
	/** If connection was succesful. */
	public static final String CONNECT_ONNECTION_SUCCESFUL = "004";
	/** If connection needs additional PONG <number> response from client. */
	public static final String CONNECT_ADDITIONAL_PING_RESPONSE = "513";
	
	// #########################
	// # GENERAL IRC RESPONSES #
	// #########################
	
	/** No topic is set in the channel. */
	public static final String RPL_NOTOPIC = "331";
	/** Channel topic message. */
	public static final String RPL_TOPIC = "332";
	
	// #################
	// # ERROR REPLIES #
	// #################
	
	/** You're not channel operator message. */
	public static final String ERR_CHANOPRIVSNEEDED = "482";
	/** Nickname contains invalid characters. */
	public static final String CONNECT_INVALID_NICKNAME = "432";
	/** Nickname is already in use in the network. */
	public static final String CONNECT_NICKNAME_IN_USE = "433";
}
