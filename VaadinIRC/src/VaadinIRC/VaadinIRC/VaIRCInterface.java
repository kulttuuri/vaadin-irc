package VaadinIRC.VaadinIRC;

import java.util.ArrayList;

import org.vaadin.artur.icepush.ICEPush;

import com.vaadin.ui.Window;

import VaadinIRC.main;
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
	private ICEPush pusher = new ICEPush();
	
	/**
	 * Initializes ICEPush component by adding the component to application main window.
	 * @param window Application window.
	 */
	public void initICEPush(Window window)
	{
		window.addComponent(pusher);
	}
	
	/**
	 * Notifies that there are new activity in the channel
	 * so that new icon and such can be set to channel tab if that channel
	 * is not selected.
	 * @param channelName
	 */
	public void setNewActivityToTab(String channelName)
	{
		vairc.setChannelNewActivity(channelName);
	}
	
	/**
	 * Forces server to send the latest information to client using the
	 * ICEPush addon for Vaadin.
	 */
	public void pushChangesToClient()
	{
		if (ICEPush.getPushContext(pusher.getApplication().getContext()) != null) pusher.push();
	}
	
	/**
	 * Handles the command that client sent.
	 */
	private void handleCommand(String command)
	{
		// QUIT
		if (command.equalsIgnoreCase("QUIT"))
		{
			try
			{
				vairc.removeAllServerTabs();
				receivedStatusMessage("Connection to server has been closed.");
				irc.closeConnection();
			}
			catch (NoConnectionInitializedException e) { receivedStatusMessage(e.getMessage()); }
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
	
	/**
	 * Is connection to IRC server running?
	 * @return Returns true if connection to IRC server is running. Otherwise false.
	 */
	public boolean isConnectionRunning()
	{
		return irc.isConnectionRunning();
	}
	
	public void connectToServer(IRCSession session)
	{
		irc.connect();
	}
	
	public boolean sendMessageToServer(String message)
	{
		if (message.startsWith("/")) message = message.substring(1);
		try
		{
			handleCommand(message);
			irc.writeMessageToBuffer(message);
			return true;
		}
		catch (NoConnectionInitializedException e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean sendMessageToChannel(String channel, String message)
	{
		try
		{
			irc.sendMessageToChannel(channel, message);
			return true;
		}
		catch (NoConnectionInitializedException e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		return false;
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
		pushChangesToClient();
	}
	
	public void joinedChannel(String channelName, String network)
	{
		// Create new channel if it does not exist already
		if (!vairc.channelMap.containsKey(channelName)) vairc.createChannel(channelName, network);
		pushChangesToClient();
	}
	
	public void userListChanged(String channel, ArrayList<String> users)
	{
		if (!vairc.channelMap.containsKey(channel))
		{
			System.out.println("Channel cannot be found: " + channel + ". Cannot add users to channel list.");
			return;
		}
		
		vairc.channelMap.get(channel).addChannelUsersToTable(users);
		pushChangesToClient();
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
		pushChangesToClient();
	}
}