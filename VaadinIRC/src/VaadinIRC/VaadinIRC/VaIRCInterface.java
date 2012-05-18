package VaadinIRC.VaadinIRC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	 * If no connection to the server has been made, this is used to send message to
	 * status channel to inform the user about that.
	 */
	public void receivedNoConnectionInitializedMessage()
	{
		receivedStatusMessage("Connection to server has not yet been established. Could not send message to server.");
	}
	
	/**
	 * Forces server to send the latest information to client using the
	 * ICEPush addon for Vaadin.
	 */
	public void pushChangesToClient()
	{
		vairc.pushChangesToClient();
	}
	
	/**
	 * Handles the command that client sent.
	 */
	private void handleCommand(String command)
	{
		try
		{
			// CONNECT
			if (command.equalsIgnoreCase("CONNECT"))
			{
				irc.connect();
			}
			// NICK
			// TODO: Check if nickname exists. Does server send new message when nick was changed?
			else if (command.startsWith("NICK"))
			{
				String[] split = command.split(" ");
				irc.getSession().setNickname(split[1]);
			}
			else if (command.startsWith("OP"))
			{
				command = command.replace("OP ", "");
				irc.writeMessageToBuffer("/MODE +o " + command);
			}
			else if (command.startsWith("DEOP"))
			{
				command = command.replace("DEOP ", "");
				irc.writeMessageToBuffer("/MODE -o " + command);
			}
			else if (command.startsWith("VOICE"))
			{
				command = command.replace("VOICE ", "");
				irc.writeMessageToBuffer("/MODE +v " + command);
			}
			else if (command.startsWith("DEVOICE"))
			{
				command = command.replace("DEVOICE ", "");
				irc.writeMessageToBuffer("/MODE -v " + command);
			}
		}
		catch (NoConnectionInitializedException e)
		{
			receivedNoConnectionInitializedMessage();
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
			receivedNoConnectionInitializedMessage();
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
		if (vairc.channelMap.containsKey(channelName))
		{
			vairc.channelMap.get(channelName).addUserToChannel(nickname);
			vairc.channelMap.get(channelName).addMessageToChannelTextarea(nickname + " joined the channel.");
		}
		pushChangesToClient();
	}

	public void leftChannel(String channelName, String network)
	{
		if (vairc.channelMap.containsKey(channelName))
		{
			vairc.removeChannel(channelName);
			vairc.channelMap.get("status").addMessageToChannelTextarea("You left the channel " + channelName);
		}
		pushChangesToClient();
	}

	public void otherLeftChannel(String channelName, String network, String nickname)
	{
		if (vairc.channelMap.containsKey(channelName))
		{
			vairc.channelMap.get(channelName).removeUserFromChannel(nickname);
			vairc.channelMap.get(channelName).addMessageToChannelTextarea(nickname + " left the channel.");
		}
		pushChangesToClient();
	}

	public void kickedFromChannel(String channelName, String network, String reason)
	{
		if (vairc.channelMap.containsKey(channelName))
		{
			vairc.removeChannel(channelName);
			vairc.channelMap.get("status").addMessageToChannelTextarea("You were kicked from channel: " + channelName + " (" + reason + ")");
		}
		pushChangesToClient();
	}

	public void otherKickedFromChannel(String channelName, String network, String nickname, String reason)
	{
		if (vairc.channelMap.containsKey(channelName))
		{
			vairc.channelMap.get(channelName).removeUserFromChannel(nickname);
			vairc.channelMap.get(channelName).addMessageToChannelTextarea(nickname + " was kicked from the channel (" + reason + ")");
		}
		pushChangesToClient();
	}

	public void bannedFromChannel(String channelName, String network, String reason)
	{
		if (vairc.channelMap.containsKey(channelName))
		{
			vairc.removeChannel(channelName);
			vairc.channelMap.get("status").addMessageToChannelTextarea("You were banned from channel (" + reason + ")");
		}
		pushChangesToClient();
	}

	public void otherBannedFromChannel(String channelName, String network, String nickname, String reason)
	{
		if (vairc.channelMap.containsKey(channelName))
		{
			vairc.channelMap.get(channelName).removeUserFromChannel(nickname);
			vairc.channelMap.get(channelName).addMessageToChannelTextarea(nickname + " was banned from the channel (" + reason + ")");
		}
		pushChangesToClient();
	}

	public void quitNetwork(String network)
	{
		try
		{
			vairc.removeAllServerTabs();
			receivedStatusMessage("Connection to server has been closed.");
			irc.closeConnection();
		}
		catch (NoConnectionInitializedException e)
		{
			receivedStatusMessage(e.getMessage());
		}
	}

	public void otherQuitNetwork(String network, String reason)
	{
		// TODO Auto-generated method stub
		
	}

	public void usersOpped(String channel, ArrayList<String> nicknames)
	{
		for (String nickname : nicknames) { System.out.println("opping " + nickname + " in chan " + channel); vairc.channelMap.get(channel).setUserLevel(nickname, "+o"); }
	}

	public void usersDeOpped(String channel, ArrayList<String> nicknames)
	{
		for (String nickname : nicknames) vairc.channelMap.get(channel).setUserLevel(nickname, "-o");
	}

	public void usersVoiced(String channel, ArrayList<String> nicknames)
	{
		for (String nickname : nicknames) vairc.channelMap.get(channel).setUserLevel(nickname, "+v");
	}

	public void usersDeVoiced(String channel, ArrayList<String> nicknames)
	{
		for (String nickname : nicknames) vairc.channelMap.get(channel).setUserLevel(nickname, "-v");
	}

	public void setChannelTopic(String nickname, String channel, String topic, boolean notifyChannel)
	{
		if (vairc.channelMap.get(channel) != null)
		{
			vairc.channelMap.get(channel).setChannelTopic(topic);
			if (notifyChannel)
				vairc.channelMap.get(channel).addMessageToChannelTextarea(nickname + " changed topic to: " + topic);
		}
	}

	public void receivedErrorMessage(String error)
	{
		// TODO: N‰ytt‰m‰‰n popuppi
		receivedStatusMessage(error);
	}
}