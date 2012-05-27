package irc.bot;

import irc.IRC;
import irc.IRCHelper;
import irc.IRCInterface;
import java.util.ArrayList;

/**
 * Main class for IRC Bot, which can be used to save data to database, query data from database etc.<br>
 * call bot commands (like define, defadd, top10) etc.
 * @author Aleksi Postari
 *
 */
public class IRCBot extends IRCBotCommands
{
	/**
	 * Constructor to initialize the IRCBot.
	 */
	public IRCBot(boolean enabled, String address, String username, String password, String databaseDriver, String databaseName, String botCallSign, String version)
	{
		super(enabled, address, username, password, databaseDriver, databaseName, botCallSign, version);
	}
	
	/**
	 * Removes unwanted characters from message and returns 
	 * the new parsed messages.
	 * @param message Message to be parsed.
	 * @return Parsed message.
	 */
	private String removeExtraCharacters(String message)
	{
		// TODO: Toimiiko / ja laita tähän kaikki mitä mysql kantaan ei saisi mennä, esim. hipsutkin, vai laitetaanko vaan real escape string?
		message = message.replace("/;", "");
		return message;
	}
	
	/**
	 * When user joins channel.
	 * @param IRC irc.
	 * @param ircgui IRC interface.
	 * @param row {@link {@link irc.JavadocLibrary#row}
	 */
	public void joinedChannel(IRC irc, IRCInterface ircgui, String row)
	{
		if (!enabled) return;
		
		String channel = IRCHelper.getChannelFromStdMessage(row);
		String nickname = IRCHelper.getNicknameFromStdMessage(row);
		
		String joinMessage = getJoinMessage(nickname, channel);
		if (!joinMessage.equals("")) ircgui.sendMessageToChannel(channel, joinMessage);
	}
	
	/**
	 * When channel receives a message, this gets called.<br>
	 * Returns if bot is disabled.<br>
	 * Checks if message if command and executes functionality for it if it was an command.
	 * @param IRC irc.
	 * @aram ircgui IRC interface.
	 * @param row {@link {@link irc.JavadocLibrary#row}
	 */
	public void receivedChannelMessage(IRC irc, IRCInterface ircgui, String row) throws Exception
	{
		if (!enabled) return;
		
		String channel = IRCHelper.getChannelFromStdMessage(row);
		String nickname = IRCHelper.getNicknameFromStdMessage(row);
		String message = IRCHelper.getStdReason(row);
		message = removeExtraCharacters(message);
		
		if (!message.startsWith(botCallSign) && !message.equals(""))
		{
			addMessageToDatabase(channel, nickname, message);
		}
		else
		{
			// Split message after command to list (removes !command from message)
			String contentAfterCommand = IRCHelper.splitMessageAfterRow(row, " ", 5);
			// Get parameters without command (Does not add !command)
			ArrayList<String> parameters = IRCHelper.splitCommandsToList(message, " ");
			if (parameters.size() > 0) parameters.remove(0);

			// Botinfo
			if (message.startsWith(botCallSign + "botinfo"))
			{
				ircgui.sendMessageToChannel(channel, getBotInfo());
			}
			// Randomnick
			if (message.startsWith(botCallSign + "randomnick"))
			{
				if (parameters.size() < 1)
					ircgui.sendMessageToChannel(channel, "Syntax: randomnick nickname ?#channel");
				else
					ircgui.sendMessageToChannel(channel, getRandomSentenceFromUser(parameters.get(0), parameters.size() > 1 ? parameters.get(1) : channel));
			}
			// Searchlogs
			/*if (message.startsWith(botCallSign + "searchlogs"))
			{
				if (parameters.size() < 1)
					ircgui.sendMessageToChannel(channel, "Syntax: searchlogs search");
				else
					ircgui.sendMessageToChannel(channel, searchFromLogs(IRCHelper.splitMessageAfterRow(row, " ", 4), channel));
			}*/
			// Chanstats
			else if (message.startsWith(botCallSign + "chanstats"))
			{
				ircgui.sendMessageToChannel(channel, getChanStats(parameters.size() == 0 ? channel : parameters.get(0)));
			}
			// Top10
			else if (message.startsWith(botCallSign + "top10"))
			{
					ircgui.sendMessageToChannel(channel, getTopChatters(parameters.size() == 0 ? channel : parameters.get(0)));
			}
			// Userstats
			else if (message.startsWith(botCallSign + "userstats"))
			{
				if (parameters.size() < 1)
					ircgui.sendMessageToChannel(channel, "Syntax: userstats nickname ?#channel");
				else
					ircgui.sendMessageToChannel(channel, getNickStats(parameters.get(0), parameters.size() > 1 ? parameters.get(1) : channel));
			}
			// Define
			else if (message.startsWith(botCallSign + "define"))
			{
				if (parameters.size() < 1)
					ircgui.sendMessageToChannel(channel, "Syntax: define word ?#channel");
				else
					ircgui.sendMessageToChannel(channel, getDefine(parameters.get(0), parameters.size() > 1 ? parameters.get(1) : channel));
			}
			// Defadd
			else if (message.startsWith(botCallSign + "defadd"))
			{
				if (parameters.size() < 2)
					ircgui.sendMessageToChannel(channel, "Syntax: defadd definename content");
				else
					ircgui.sendMessageToChannel(channel, addDefine(nickname, parameters.get(0), contentAfterCommand, channel));
			}
			// Defrem
			else if (message.startsWith(botCallSign + "defrem"))
			{
				if (parameters.size() < 1)
					ircgui.sendMessageToChannel(channel, "Syntax: defrem word");
				else
					ircgui.sendMessageToChannel(channel, removeDefine(nickname, parameters.get(0)));
			}
			// Definfo
			else if (message.startsWith(botCallSign + "definfo"))
			{
				if (parameters.size() < 1)
					ircgui.sendMessageToChannel(channel, "Syntax: definfo word ?#channel");
				else
					ircgui.sendMessageToChannel(channel, getDefineInfo(parameters.get(0), parameters.size() > 1 ? parameters.get(1) : channel));
			}
			// Defchange
			else if (message.startsWith(botCallSign + "defchange"))
			{
				if (parameters.size() < 2)
					ircgui.sendMessageToChannel(channel, "Syntax: defchange word content");
				else
					ircgui.sendMessageToChannel(channel, changeDefine(parameters.get(0), contentAfterCommand, channel, nickname));
			}
			// Joinget
			else if (message.startsWith(botCallSign + "joinget"))
			{
				if (parameters.size() < 1)
					ircgui.sendMessageToChannel(channel, "Syntax: joinmessage nickname");
				else
					ircgui.sendMessageToChannel(channel, getDefine(parameters.get(0), channel));
			}
			// Joinadd
			else if (message.startsWith(botCallSign + "joinadd"))
			{
				if (parameters.size() < 2)
					ircgui.sendMessageToChannel(channel, "Syntax: joinadd nickname message");
				else
					ircgui.sendMessageToChannel(channel, addJoinMessage(parameters.get(0), contentAfterCommand, channel));
			}
			// Joinrem
			else if (message.startsWith(botCallSign + "joinrem"))
			{
				if (parameters.size() < 1)
					ircgui.sendMessageToChannel(channel, "Syntax: joinrem nickname");
				else
					ircgui.sendMessageToChannel(channel, removeJoinMessage(parameters.get(0), channel));
			}
			// Joinchange
			else if (message.startsWith(botCallSign + "joinchange"))
			{
				if (parameters.size() < 2)
					ircgui.sendMessageToChannel(channel, "Syntax: joinchange nickname message");
				else
					ircgui.sendMessageToChannel(channel, changeJoinMessage(parameters.get(0), contentAfterCommand, channel));
			}
			// Unknown command
			else
			{
				ircgui.sendMessageToChannel(channel, "Unknown command: " + message.split(" ")[0]);
			}
		}
	}
}