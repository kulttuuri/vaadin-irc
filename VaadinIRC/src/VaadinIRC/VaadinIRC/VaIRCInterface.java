package VaadinIRC.VaadinIRC;

import java.util.ArrayList;
import irc.IRC;
import irc.IRCInterface;
import irc.IRCSession;
import irc.exceptions.*;

/**
 * Implemented VaIRCInterface to handle all the actions between VaadinIRC GUI and actual IRC connection.
 * @author Aleksi Postari
 *
 */
public class VaIRCInterface implements IRCInterface
{
	private VaadinIRC vairc;
	private IRC irc;
	
	/**
	 * Handles the command that client sent.
	 */
	private void handleCommand(String command)
	{
		command = command.toUpperCase();
		
		// QUIT
		if (command.equalsIgnoreCase("quit"))
		{
			try {
				irc.closeConnection(); }
			catch (NoConnectionInitializedException e) {  }
		}
		// CONNECT
		else if (command.equalsIgnoreCase("CONNECT"))
		{
			irc.connect();
		}
		// NICK
		else if (command.startsWith("NICK"))
		{
			String[] split = command.split(" ");
			irc.getSession().setNickname(split[1]);
		}
	}
	
	/**
	 * Used to set reference to VaadinIRC here.
	 * @param vairc Reference to current running VaadinIRC.
	 */
	public void setVaadinIRC(VaadinIRC vairc)
	{
		this.vairc = vairc;
		}
	
	/**
	 * Used to set reference to IRC here.
	 * @param irc Reference to current running IRC.
	 */
	public void setIRC(IRC irc)
	{
		this.irc = irc;
	}
	
	public void connectToServer(IRCSession session)
	{
		irc.connect();
	}
	
	public void sendMessageToServer(String message)
	{
		if (message.startsWith("/")) message = message.substring(1);
		try
		{
			irc.writeMessageToBuffer(message);
			handleCommand(message);
		}
		catch (NoConnectionInitializedException e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	public boolean sendMessageToChannel(String channel, String message)
	{
		irc.sendMessageToChannel(channel, message);
		return true;
	}
	
	public boolean sendMessageToUser(String user, String message)
	{
		// TODO: Implement.
		return false;
	}
	
	public void receivedNewMessage(String message, String channel)
	{
		if (vairc.channelMap.containsKey(channel))
			vairc.channelMap.get(channel).addMessageToChannelTextarea(message);
	}
	
	public void receivedStatusMessage(String message)
	{
		vairc.channelMap.get("status").addMessageToChannelTextarea(message);
	}
	
	public void joinedChannel(String channelName, String network)
	{
		// Create new channel if it does not exist already
		if (!vairc.channelMap.containsKey(channelName)) vairc.createChannel(channelName, network);
	}
	
	public void userListChanged(String channel, ArrayList<String> users)
	{
		if (!vairc.channelMap.containsKey(channel))
			System.out.println("Channel cannot be found: " + channel + ". Cannot add users to channel list.");
		
		vairc.channelMap.get(channel).addChannelUsersToTable(users);
	}
	
	public String getCurrentChannelName()
	{
		// Returns the current channel tab caption
		return vairc.getSelectedChannelName();
	}

	public void otherJoinedChannel(String channelName, String network, String nickname)
	{
		// Add user to channel if the channel exists.
		if (vairc.channelMap.containsKey(channelName)) vairc.channelMap.get(channelName).addUserToChannel(nickname);
	}
}