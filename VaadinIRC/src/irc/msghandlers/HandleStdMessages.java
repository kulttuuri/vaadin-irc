package irc.msghandlers;

import java.util.ArrayList;
import java.util.HashMap;
import irc.IRC;
import irc.IRCEnums;
import irc.IRCHelper;
import irc.IRCInterface;

public class HandleStdMessages extends MsgHandler
{
	/** Contains map of all standard messages. */
	private static HashMap<String, String> standardMessages = new HashMap<String, String>();
	
	/**
	 * Fills all the standard messages to be able to see what commands does this client support.
	 */
	private static void createStandardMessages()
	{
		standardMessages.put("331", "RPL_NOTOPIC");
		standardMessages.put("332", "RPL_NOTOPIC");
		standardMessages.put("353", "RPL_NAMREPLY");
	}
	
	public HandleStdMessages(IRCInterface irc)
	{
		super(irc);
		createStandardMessages();
	}

	@Override
	public boolean handleLine(String ircRow, IRC ircApp)
	{
		this.row = ircRow;
		
    	// Current message splitted with spaces
    	ArrayList<String> rowSpaces = new ArrayList<String>();
    	rowSpaces = IRCHelper.splitCommandsToList(row, " ");
        
        // List of nicknames
        if (checkCommand("353"))
        {
        	String split[] = row.split(" ");
        	ArrayList<String> newNicks = new ArrayList<String>();
            int i = 0;
            // Go through all the nicknames.
            for (String nickname : split)
            {
                // List of nicknames begins from split 5.
                if (i > 4)
                {
                    // Remove ":" from first nickname.
                    if (i == 5) newNicks.add(nickname.substring(1) + "\n");
                    else newNicks.add(nickname + "\n");
                }
                i++;
            }
            irc.userListChanged(split[4], newNicks);
        }
        // Store channel topic on join
        else if (checkCommand("331") || checkCommand("332"))
        {
        	irc.setChannelTopic(rowSpaces.get(3), IRCHelper.splitMessageAfterRow(row, ":", 2), IRCHelper.getNicknameFromStdMessage(row), false);
        }
		// AUTO-AWAY (DEBUG: did read line: :port80a.se.quakenet.org 301 joumatamou Kulttuuri :Auto-away)
        else if (checkCommand("301"))
        {
        	irc.receivedNewPrivateMessage(rowSpaces.get(3), IRCHelper.getStdReason(row));
        }
		// WHOIS commands
        else if (checkCommand("311") || checkCommand("319") || checkCommand("312") || checkCommand("330") || checkCommand("330"))
        {
        	irc.receivedNewPrivateMessage(rowSpaces.get(3), IRCHelper.getStdReason(row));
        }
		// If command was not handled, return false.
        else
        {
        	return false;
        }
		return true;
	}
}