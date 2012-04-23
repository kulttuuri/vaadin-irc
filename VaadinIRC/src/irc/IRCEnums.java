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
	/** If nickname contains invalid characters. */
	public static final String CONNECT_INVALID_NICKNAME = "432";
	/** If nickname is already in use in the network. */
	public static final String CONNECT_NICKNAME_IN_USE = "433";
	
	/** Number of split index for the command name/number in IRC Messages splitted by space. */
	public static final int IRC_MSG_SPACE_SPLIT_COMMAND = 1;
}
