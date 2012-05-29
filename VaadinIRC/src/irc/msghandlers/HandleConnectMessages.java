package irc.msghandlers;

import java.util.HashMap;
import irc.IRC;
import irc.IRCHelper;
import irc.IRCInterface;
import irc.exceptions.InvalidNicknameException;
import irc.exceptions.NicknameAlreadyInUseException;

/**
 * This class handles the messages that are needed when connection to IRC server.
 * @author Aleksi Postari
 *
 */
public class HandleConnectMessages extends MsgHandler
{
	/** Contains map of all connect replies. */
	private static HashMap<String, String> connectReplies = new HashMap<String, String>();
	
	/**
	 * Fills all the required connect replies to {@link #connectReplies}.
	 */
	private static void createConnectReplies()
	{
		connectReplies.put("001", "RPL_WELCOME");
		connectReplies.put("002", "RPL_YOURHOST");
		connectReplies.put("003", "RPL_CREATED");
		// connectReplies.put("004", "RPL_MYINFO");
		connectReplies.put("005", "");
		connectReplies.put("221", "");
		connectReplies.put("252", "");
		connectReplies.put("253", "");
		connectReplies.put("254", "");
		connectReplies.put("255", "");
		connectReplies.put("372", "");
		connectReplies.put("376", "");
	}
	
	/**
	 * Constructor to initialize the class.
	 * @param irc IRCInterface.
	 */
	public HandleConnectMessages(IRCInterface irc)
	{
		super(irc);
		createConnectReplies();
	}

	/**
	 * Handles the connect lines to irc network.
	 * @param ircRow {@link irc.JavadocLibrary#row}
	 * @param ircApp The actual IRC application.
	 * @return Returns true if connection to server was created (004), otherwise false.
	 * @throws InvalidNicknameException When nickname contains invalid characters.
	 * @throws NicknameAlreadyInUseException When nickname is already in use.
	 */
	public boolean handleConnectLines(String ircRow, IRC ircApp) throws InvalidNicknameException, NicknameAlreadyInUseException
	{
		row = ircRow;
		
		if (connectReplies.containsKey(IRCHelper.getStdCommand(row)))
		{
			irc.receivedStatusMessage(IRCHelper.splitMessageAfterRow(row, " ", 3));
			return false;
		}
		
		// PING response
    	if (row.startsWith("PING "))
    	{
    		ircApp.handlePingResponse(row);
    	}
    	// Additional PONG response
    	if (row.indexOf("513") >= 0)
    	{
    		try
    		{
        		String[] split = row.split(" ");
    			ircApp.writeMessageToBuffer("PONG " + split[split.length - 1]);
    		} catch (Exception e) { }
    	}
        // If row was 004, we have succesfully connected to server.
    	else if (row.indexOf("004") >= 0)
        {
            return true;
        }
        // Invalid nickname
        else if(row.indexOf("432") >= 0)
        {
            throw new InvalidNicknameException();
        }
        // Nickname already in use
        else if (row.indexOf("433") >= 0)
        {
            throw new NicknameAlreadyInUseException();
        }
		
		return false;
	}
	
	/**
	 * @deprecated Use {@link #handleConnectLines(String, IRC)} instead in class HandleConnectMessages.
	 */
	@Override
	public boolean handleLine(String ircRow, IRC ircApp)
	{
		return false;
	}
}