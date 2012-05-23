package irc.msghandlers;

import java.util.ArrayList;
import java.util.HashMap;
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
	public boolean handleLine(String ircRow)
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
        	irc.setChannelTopic(rowSpaces.get(0), IRCHelper.getStdReason(row), IRCHelper.getNicknameFromStdMessage(row), false);
        }
        else
        {
        	return false;
        }
		return true;
	}
}