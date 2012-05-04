package irc;

import java.util.ArrayList;

import VaadinIRC.VaadinIRC.VaIRCInterface;

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
	        // List of nicknames
	        else if (row.indexOf("353") >= 0)
            {
	        	System.out.println("DEBUG: going through nicknames for row: " + row);
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
	            System.out.println("DEBUG: WENT THROUGH NICKNAMES");
	            irc.GUIInterface.userListChanged(split[4], newNicks);
            }
	        // Join
	        else if (checkCommand(row, "JOIN"))
	        {
	        	if (IRCHelper.getNicknameFromStdMessage(row).equals(irc.session.getNickname()))
	        		irc.GUIInterface.joinedChannel(IRCHelper.getChannelFromStdMessage(row), irc.session.getServer());
	        	else
	        		irc.GUIInterface.otherJoinedChannel(IRCHelper.getChannelFromStdMessage(row), irc.session.getServer(), IRCHelper.getNicknameFromStdMessage(row));
	        	// Wait so that channel will have time to get generated
	        	try { Thread.sleep(1000); } catch (InterruptedException e) { }
	        }
	        // Normal channel message (PRIVMSG)
	        else if (!row.startsWith(":" + irc.session.getNickname()) && checkCommand(row, "PRIVMSG"))
            {
	        	if (!IRCHelper.getNicknameFromStdMessage(row).equals(irc.session.getNickname()))
	        		irc.GUIInterface.receivedNewMessage(IRCHelper.generateIRCMessage(IRCHelper.getNicknameFromStdMessage(row), IRCHelper.getContentFromStdMessage(row)), IRCHelper.getChannelFromStdMessage(row));
            }
	        // Part (:VaAle101!~null@a91-152-121-162.elisa-laajakaista.fi PART #testikannu12345 :reason)
	        else if (checkCommand(row, "PART"))
	        {
	        	if (IRCHelper.getNicknameFromStdMessage(row).equals(irc.session.getNickname()))
	        		irc.GUIInterface.leftChannel(IRCHelper.getChannelFromStdMessage(row), irc.session.getServer());
	        	else
	        		irc.GUIInterface.otherLeftChannel(IRCHelper.getChannelFromStdMessage(row), irc.session.getServer(), IRCHelper.getNicknameFromStdMessage(row));
	        }
	        // Kick (:Kulttuuri!u4267@irccloud.com KICK #testikannu12345 VaAle101 :reason)
	        else if (checkCommand(row, "KICK"))
	        {
	        	if (IRCHelper.getNicknameFromStdMessage(row).equals(irc.session.getNickname()))
	        		irc.GUIInterface.kickedFromChannel(IRCHelper.getChannelFromStdMessage(row), irc.session.getServer(), IRCHelper.getStdReason(row));
	        	else
	        		irc.GUIInterface.otherKickedFromChannel(IRCHelper.getChannelFromStdMessage(row), irc.session.getServer(), IRCHelper.getNicknameFromStdMessage(row), IRCHelper.getStdReason(row));
	        }
	        // Mode (:Kulttuuri!u4267@irccloud.com MODE #testikannu12345 +bbb VaAle101!*@* reason!*@* viesti!*@*)
	        else if (checkCommand(row, "MODE"))
	        {
	        	String mode = rowSpaces.get(3);
	        	if (mode.startsWith("+o"))
	        		irc.GUIInterface.usersOpped(IRCHelper.getChannelFromStdMessage(row), IRCHelper.getModeTargetUsers(row));
	        	if (mode.startsWith("-o"))
	        		irc.GUIInterface.usersDeOpped(IRCHelper.getChannelFromStdMessage(row), IRCHelper.getModeTargetUsers(row));
	        	if (mode.startsWith("+v"))
	        		irc.GUIInterface.usersVoiced(IRCHelper.getChannelFromStdMessage(row), IRCHelper.getModeTargetUsers(row));
	        	if (mode.startsWith("-v"))
	        		irc.GUIInterface.usersDeVoiced(IRCHelper.getChannelFromStdMessage(row), IRCHelper.getModeTargetUsers(row));
	        }
	        // TOPIC (:Kulttuuri!u4267@irccloud.com TOPIC #testikannu12345 :asd)
	        else if (checkCommand(row, "TOPIC"))
	        {
	        	irc.GUIInterface.setChannelTopic(IRCHelper.getChannelFromStdMessage(row), IRCHelper.getStdReason(row), IRCHelper.getNicknameFromStdMessage(row), true);
	        }
	        // Store channel topic on join
	        else if (checkCommand(row, IRCEnums.RPL_NOTOPIC) || checkCommand(row, IRCEnums.RPL_TOPIC))
	        {
	        	irc.GUIInterface.setChannelTopic(rowSpaces.get(0), IRCHelper.getStdReason(row), IRCHelper.getNicknameFromStdMessage(row), false);
	        }
	        // Handle error replies
	        handleErrorReplies(row);
	        
	        
	        
	        // TODO: Botin toiminnot.
	    }
    }
    
    /**
     * Handles IRC sent error replies.
     * @param row Whole IRC message.
     */
    private void handleErrorReplies(String row)
    {
        // You are not op message
        if (checkCommand(row, IRCEnums.ERR_CHANOPRIVSNEEDED))
        {
        	irc.GUIInterface.sendMessageToChannel(IRCHelper.getChannelFromStdMessage(row), IRCHelper.getStdReason(row));
        }
    }
    
    /**
     * Checks the command that server sent to client.
     * @param row Full IRC message row.
     * @param message Command that should match the command sent from server. This is not case sensitive.
     * @return Returns true if command was same that was passed. Otherwise false.
     */
    private boolean checkCommand(String row, String command)
    {
    	String getCommand = IRCHelper.getStdCommand(row);
    	if (getCommand == null) return false;
    	
    	if (command.equalsIgnoreCase(getCommand)) return true;
    	else return false;
    }
}