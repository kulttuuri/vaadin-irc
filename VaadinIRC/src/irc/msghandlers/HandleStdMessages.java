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

package irc.msghandlers;

import java.util.ArrayList;
import java.util.HashMap;
import irc.IRC;
import irc.IRCHelper;
import irc.IRCInterface;

/**
 * Class to handle standard IRC messages (lines 300-400).
 * @author Aleksi Postari
 *
 */
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
	
	/**
	 * Constructor to initialize the class.
	 * @param irc IRCInterface.
	 */
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