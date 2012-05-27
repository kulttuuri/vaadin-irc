package VaadinIRC.VaadinIRC;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.vaadin.artur.icepush.ICEPush;

import com.vaadin.ui.Window;

import VaadinIRC.main;
import VaadinIRC.settings;
import VaadinIRC.exceptions.ChannelNotFoundException;
import irc.IRC;
import irc.IRCHelper;
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
	 * @param channelName Name of the channel.
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
		vairc.refreshTopbarText();
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
	 * @param command Command.
	 * @return Returns true if command was handled here. Otherwise false.
	 */
	private boolean handleCommand(String command)
	{
		try
		{
	    	// Current message splitted with spaces
	    	ArrayList<String> rowSpaces = new ArrayList<String>();
	    	rowSpaces = IRCHelper.splitCommandsToList(command, " ");
			
			if (command.equalsIgnoreCase("CONNECT"))
			{
				irc.connect();
			}
			else if (rowSpaces.get(0).equalsIgnoreCase("QUIT"))
			{
				quitNetwork(irc.getSession().getServer(), "Disconnected from server.");
				return true;
			}
			else if (rowSpaces.get(0).equalsIgnoreCase("QUERY"))
			{
				String target = "";
				String reason = "";
				try { reason = rowSpaces.get(2); } catch (Exception e) { }
				try { target = rowSpaces.get(1); } catch (Exception e) { }
				sendMessageToUser(target.toLowerCase(), reason);
				return true;
			}
			/*
			else if (rowSpaces.get(0).equalsIgnoreCase("PRIVMSG"))
			{
				String target = "";
				String reason = "";
				try { reason = rowSpaces.get(2); } catch (Exception e) { }
				try { target = rowSpaces.get(1); } catch (Exception e) { }
				if (target.startsWith("#")) return false; // Skip channel messages
				sendMessageToUser(target.toLowerCase(), reason);
				return true;
			}*/
			else if (rowSpaces.get(0).equalsIgnoreCase("WC"))
			{
				if (!vairc.getSelectedChannelName().startsWith("#"))
					vairc.removeChannel(vairc.getSelectedChannelName());
				return true;
			}
			else if (rowSpaces.get(0).equalsIgnoreCase("OP"))
			{
				String commandParams = IRCHelper.splitMessageAfterRow(command, " ", 1);
				irc.writeMessageToBuffer("/MODE " + vairc.getSelectedChannelName() + " +o " + commandParams);
				return true;
			}
			else if (rowSpaces.get(0).equalsIgnoreCase("DEOP"))
			{
				String commandParams = IRCHelper.splitMessageAfterRow(command, " ", 1);
				irc.writeMessageToBuffer("/MODE " + vairc.getSelectedChannelName() + " -o " + commandParams);
				return true;
			}
			else if (rowSpaces.get(0).equalsIgnoreCase("VOICE"))
			{
				String commandParams = IRCHelper.splitMessageAfterRow(command, " ", 1);
				irc.writeMessageToBuffer("/MODE " + vairc.getSelectedChannelName() + " +v " + commandParams);
				return true;
			}
			else if (rowSpaces.get(0).equalsIgnoreCase("DEVOICE"))
			{
				String commandParams = IRCHelper.splitMessageAfterRow(command, " ", 1);
				irc.writeMessageToBuffer("/MODE " + vairc.getSelectedChannelName() + " -v " + commandParams);
				return true;
			}
		}
		catch (NoConnectionInitializedException e)
		{
			receivedNoConnectionInitializedMessage();
		}
		catch (Exception e)
		{
			System.out.println("Exception handling user sent command: " + e);
		}
		return false;
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
	
	public boolean isConnectionRunning()
	{
		return irc.isConnectionRunning();
	}
	
	public void connectToServer(IRCSession session)
	{
		irc.connect();
		vairc.refreshTopbarText();
	}
	
	public boolean sendMessageToServer(String message)
	{
		if (message.startsWith("/")) message = message.substring(1);
		try
		{
			if (!handleCommand(message))
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
	
	public void sendMessageToUser(String user, String message)
	{
		try
		{
			if (!vairc.containsChannel(user)) vairc.createPrivateConversation(user.toLowerCase());
			irc.writeMessageToBuffer("PRIVMSG " + user + " " + message);
		}
		catch (NoConnectionInitializedException e)
		{
			e.printStackTrace();
		}
	}
	
	public void receivedNewMessage(String username, String message, String channel)
	{
		try
		{
			vairc.getChannel(channel).addStandardChannelMessage(username, message);
		}
		catch (ChannelNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	public void receivedStatusMessage(String message)
	{
		
		try
		{
			vairc.getStatusChannel().addMessageToChannelTextarea(message);
		}
		catch (ChannelNotFoundException e)
		{
			e.printStackTrace();
		}
		pushChangesToClient();
	}
	
	public void joinedChannel(String channelName, String network)
	{
		// Create new channel if it does not exist already
		if (!vairc.containsChannel(channelName)) vairc.createChannel(channelName);
		pushChangesToClient();
	}
	
	public void userListChanged(String channel, ArrayList<String> users)
	{
		try
		{
			vairc.getChannel(channel).addChannelUsersToTable(users);
		}
		catch (ChannelNotFoundException e)
		{
			e.printStackTrace();
		}
		pushChangesToClient();
	}
	
	public String getCurrentChannelName()
	{
		// Returns the current channel tab caption
		return vairc.getSelectedChannelName();
	}

	public void otherJoinedChannel(String channelName, String network, String nickname)
	{
		try
		{
			vairc.getChannel(channelName).addUserToChannel(nickname);
			vairc.getChannel(channelName).addMessageToChannelTextarea(nickname + " joined the channel.");
		}
		catch (ChannelNotFoundException e)
		{
			e.printStackTrace();
		}
		pushChangesToClient();
	}

	public void leftChannel(String channelName, String network)
	{
			vairc.removeChannel(channelName);
			try
			{
				vairc.getStatusChannel().addMessageToChannelTextarea("You left the channel " + channelName);
			}
			catch (ChannelNotFoundException e)
			{
				e.printStackTrace();
			}
		pushChangesToClient();
	}

	public void otherLeftChannel(String channelName, String network, String nickname)
	{
		try
		{
			vairc.getChannel(channelName).removeUserFromChannel(nickname," left the channel.", true);
		}
		catch (ChannelNotFoundException e)
		{
			e.printStackTrace();
		}
		pushChangesToClient();
	}

	public void kickedFromChannel(String channelName, String network, String reason)
	{
		try
		{
			vairc.removeChannel(channelName);
			vairc.getStatusChannel().addMessageToChannelTextarea("You were kicked from channel: " + channelName + " (" + reason + ")");
		}
		catch (ChannelNotFoundException e)
		{
			e.printStackTrace();
		}
		pushChangesToClient();
	}

	public void otherKickedFromChannel(String channelName, String network, String nickname, String reason)
	{
		try
		{
			vairc.getChannel(channelName).removeUserFromChannel(nickname, "was kicked from the channel (" + reason + ")", true);
		}
		catch (ChannelNotFoundException e)
		{
			e.printStackTrace();
		}
		pushChangesToClient();
	}

	public void bannedFromChannel(String channelName, String network, String reason)
	{
		try
		{
			vairc.removeChannel(channelName);
			vairc.getStatusChannel().addMessageToChannelTextarea("You were banned from channel (" + reason + ")");
		}
		catch (ChannelNotFoundException e)
		{
			e.printStackTrace();
		}
		pushChangesToClient();
	}

	public void otherBannedFromChannel(String channelName, String network, String nickname, String reason)
	{
		try
		{
			vairc.getChannel(channelName).removeUserFromChannel(nickname, nickname + " was banned from the channel (" + reason + ")", true);
		}
		catch (ChannelNotFoundException e)
		{
			e.printStackTrace();
		}
		pushChangesToClient();
	}

	public void quitNetwork(String network, String reason)
	{
		irc.setConnectionRunning(false);
		vairc.removeAllServerTabs();
		vairc.refreshTopbarText();
		pushChangesToClient();
	}

	public void otherQuitNetwork(String nickname, String network, String reason)
	{
		vairc.removeUserFromChannels(nickname, "quit: " + reason, true);
		pushChangesToClient();
	}

	public void usersOpped(String channel, ArrayList<String> nicknames)
	{
		try
		{
			for (String nickname : nicknames) { System.out.println("opping " + nickname + " in chan " + channel); vairc.getChannel(channel).setUserLevel(nickname, "+o"); }
		}
		catch (ChannelNotFoundException e)
		{
			e.printStackTrace();
		}
		pushChangesToClient();
	}

	public void usersDeOpped(String channel, ArrayList<String> nicknames)
	{
		try
		{
			for (String nickname : nicknames) vairc.getChannel(channel).setUserLevel(nickname, "-o");
		}
		catch (ChannelNotFoundException e)
		{
			e.printStackTrace();
		}
		pushChangesToClient();
	}

	public void usersVoiced(String channel, ArrayList<String> nicknames)
	{
		try
		{
			for (String nickname : nicknames) vairc.getChannel(channel).setUserLevel(nickname, "+v");
		}
		catch (ChannelNotFoundException e)
		{
			e.printStackTrace();
		}
		pushChangesToClient();
	}

	public void usersDeVoiced(String channel, ArrayList<String> nicknames)
	{
		try
		{
			for (String nickname : nicknames) vairc.getChannel(channel).setUserLevel(nickname, "-v");
		}
		catch (ChannelNotFoundException e)
		{
			e.printStackTrace();
		}
		pushChangesToClient();
	}

	public void setChannelTopic(String channel, String topic, String nickname, boolean notifyChannel)
	{
		try
		{
			vairc.getChannel(channel).setChannelTopic(topic);
			if (notifyChannel) vairc.getChannel(channel).addMessageToChannelTextarea(nickname + " changed topic to: " + topic);
		}
		catch (ChannelNotFoundException e)
		{
			e.printStackTrace();
		}
		pushChangesToClient();
	}

	public void receivedErrorMessage(String row, String errorCode, String errorMessage)
	{
		ArrayList<String> rowSpaces = IRCHelper.splitCommandsToList(row, " ");
		
		if (errorCode.equals("401")) // :port80a.se.quakenet.org 401 joumataMou joumataasdqw :No such nick
		{
			receivedNewPrivateMessage(rowSpaces.get(3), errorMessage);
		}
		else
			receivedStatusMessage(errorMessage);
	}

	public void otherChangedNickname(String oldNickname, String newNickname)
	{
		vairc.changeUserNickname(oldNickname, newNickname);
		pushChangesToClient();
	}

	public void userChangedNickname(String oldNickname, String newNickname)
	{
		irc.getSession().setNickname(newNickname);
		vairc.changeUserNickname(oldNickname, newNickname);
		vairc.refreshTopbarText();
		pushChangesToClient();
	}

	public boolean isDebugEnabled()
	{
		return settings.debug;
	}

	public void debugSendMessage(String message)
	{
		irc.debugSendMessageToReader(message);
		pushChangesToClient();
	}

	public void connectionStatusChanged(String network, int status)
	{
		if (status == IRC.CONNECTION_STATUS_CONNECTED)
		{
	        receivedStatusMessage("Connected to network: " + network + ".");
			vairc.refreshTopbarText();
		}
		else if (status == IRC.CONNECTION_STATUS_DISCONNECTED)
		{
			receivedStatusMessage("Disconnected from network " + network + ".");
			vairc.refreshTopbarText();
		}
		else if (status == IRC.CONNECTION_STATUS_CONNECTING)
			vairc.setTopbarTextStatusConnecting();
		
		vairc.pushChangesToClient();
	}

	public void receivedNewPrivateMessage(String nickname, String message)
	{
		nickname = nickname.toLowerCase();
		if (!vairc.containsChannel(nickname)) vairc.createPrivateConversation(nickname);
		try
		{
			vairc.getChannel(nickname).addStandardChannelMessage(nickname, message);
		}
		catch (ChannelNotFoundException e)
		{
			e.printStackTrace();
		}
	}
}