package irc;

import java.util.ArrayList;

import irc.exceptions.NoConnectionInitializedException;

public class ThreadIRCReader extends Thread
{
    /** Reference to IRC class. */
    IRC irc;

    /**
     * Constructor to create new reader thread.
     * @param irc Reference to IRC.
     */
    public ThreadIRCReader(IRC irc)
        {
        this.irc = irc;
        }

    /**
     * Thread is running here.
     */
    @Override
    public void run()
    {
    	// Current message
    	String row = "";
    	// Current message splitted with spaces
    	ArrayList<String> rowSpaces = new ArrayList<String>();
	    
	    while (irc.isConnectionRunning())
	    {
	        // Try to read line
	        try
            {
	            row = irc.reader.readLine();
	            rowSpaces = IRCHelper.splitCommandsToList(row, " ");
	            System.out.println("DEBUG: did read line: " + row);
            }
	        catch (Exception e)
            {
	        	System.out.println("Error reading line: " + e);
	        	e.printStackTrace();
            }
	
	        // If connection is closed, return from thread.
	        if (!irc.isConnectionRunning()) return;
	        
	        // Answer PING messages, so that we will be staying as connected.
	        if (row.contains("PING "))
	        {
	        	irc.handlePingResponse(row);
	        }
	        // If going through list of nicknames
	        else if (row.indexOf("353") >= 0)
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
	            irc.GUIInterface.userListChanged(split[4], newNicks);
            }
	        // Current user wanted to join a channel
	        else if (row.startsWith(":" + irc.session.getNickname()) && rowSpaces.get(IRCEnums.IRC_MSG_SPACE_SPLIT_COMMAND).equals("JOIN"))
	        {
	        	irc.GUIInterface.joinedChannel(rowSpaces.get(IRCEnums.IRC_MSG_SPACE_SPLIT_COMMAND+1), irc.session.getServer());
	        }
	        // Other user joined current channel you are in
	        else if (!row.startsWith(":" + irc.session.getNickname()) && rowSpaces.get(IRCEnums.IRC_MSG_SPACE_SPLIT_COMMAND).equals("JOIN"))
	        {
	        	String[] split = row.split("!");
	        	irc.GUIInterface.otherJoinedChannel(rowSpaces.get(IRCEnums.IRC_MSG_SPACE_SPLIT_COMMAND+1), irc.session.getServer(), split[0].substring(1));
	        }
	        // normal channel message
	        else if (rowSpaces.get(IRCEnums.IRC_MSG_SPACE_SPLIT_COMMAND).equals("PRIVMSG"))
            {
	            // Haetaan nick
	            //nick = rivi.split("\\!");
	            //nick[0] = nick[0].substring(1);
	            // Haetaan viesti
	            //viesti = row.substring(1).split("\\:");
	            //irc.GUIInterface.receivedNewMessage(viesti[1], viesti[0]);
	            //irc.UI.LisaaRivi("<" + nick[0] + "> " + viesti[1]);
            }
	
	        // TODO: Botin toiminnot.
	    }
    }
}