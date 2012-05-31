/**
 * Copyright (C) 2012 Aleksi Postari (@kulttuuri, aleksi@postari.net)
 * License type: MIT (http://en.wikipedia.org/wiki/MIT_License)
 * This code is part of project Vaadin Irkkia.
 * License in short: You can use this code as you wish, but please keep this license information intach or credit the original author in redistributions.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package irc.bot;

import irc.IRC;
import irc.IRCHelper;
import irc.IRCInterface;
import java.util.ArrayList;

/**
 * Main class for IRCbot, which can be used to save data to database, query data from database etc.<br>
 * Calls bot commands (like define, defadd, top10) etc.
 * @author Aleksi Postari
 *
 */
public class IRCBot extends IRCBotCommands
{
	/**
	 * Constructor to start the IRCBot.
	 * @param enabled Is the bot enabled?
	 * @param address SQL server address.
	 * @param username SQL server username.
	 * @param password SQL server password.
	 * @param databaseDriver Java Database Driver.
	 * @param databaseName SQL database name.
	 * @param botCallSign What sign is used to call bot commands.
	 * @param version Bot version.
	 */
	public IRCBot(boolean enabled, String address, String username, String password, String databaseDriver, String databaseName, String botCallSign, String version)
	{
		super(enabled, address, username, password, databaseDriver, databaseName, botCallSign, version);
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
		
		if (!message.startsWith(botCallSign) && !message.trim().equals("") || message.trim().equals("!"))
		{
			// Add message to database
			addMessageToDatabase(channel, nickname, message);
			// Check if message contains matchable words.
			String checkWord = getWord(nickname, channel, message);
			if (!checkWord.equals("")) ircgui.sendMessageToChannel(channel, checkWord);
		}
		else
		{
			// Split message after command to list (removes "!command" from message)
			String contentAfterCommand = IRCHelper.splitMessageAfterRow(row, " ", 4);
			// Split message after command par1 to list (removes "!command par1" from message)
			String contentAfterSecondCommand = IRCHelper.splitMessageAfterRow(row, " ", 5);
			// Get parameters without command (Does not add !command)
			ArrayList<String> parameters = IRCHelper.splitCommandsToList(message, " ");
			if (parameters.size() > 0) parameters.remove(0);

			// Botinfo
			if (message.startsWith(botCallSign + "botinfo"))
			{
				ircgui.sendMessageToChannel(channel, getBotInfo());
			}
			// Help
			else if (message.startsWith(botCallSign + "help"))
			{
				sendCommandsToChannel(irc, channel);
			}
			// Commandhelp
			else if (message.startsWith(botCallSign + "commandhelp"))
			{
				if (parameters.size() < 1)
					ircgui.sendMessageToChannel(channel, getCommandHelp("commandhelp"));
				else
					ircgui.sendMessageToChannel(channel, getCommandHelp(parameters.get(0)));
			}
			// Lastseen
			else if (message.startsWith(botCallSign + "lastseen"))
			{
				if (parameters.size() < 1)
					ircgui.sendMessageToChannel(channel, getCommandHelp("lastseen"));
				else
					ircgui.sendMessageToChannel(channel, getLastSeen(parameters.get(0), parameters.size() > 1 ? parameters.get(1) : channel));
			}
			// Randomnick
			else if (message.startsWith(botCallSign + "randomnick"))
			{
				if (parameters.size() < 1)
					ircgui.sendMessageToChannel(channel, getCommandHelp("randomnick"));
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
					ircgui.sendMessageToChannel(channel, getCommandHelp("userstats"));
				else
					ircgui.sendMessageToChannel(channel, getNickStats(parameters.get(0), parameters.size() > 1 ? parameters.get(1) : channel));
			}
			// Votestart
			else if (message.startsWith(botCallSign + "votestart"))
			{
				if (parameters.size() < 1)
					ircgui.sendMessageToChannel(channel, getCommandHelp("votestart"));
				else
					ircgui.sendMessageToChannel(channel, getVotingPlugin(channel).startVoting(nickname, contentAfterCommand));
			}
			// Votestop
			else if (message.startsWith(botCallSign + "votestop"))
			{
				ArrayList<String> msgs = getVotingPlugin(channel).stopVoting();
				for (String msg : msgs) ircgui.sendMessageToChannel(channel, msg);
			}
			// Voteyes
			else if (message.startsWith(botCallSign + "voteyes"))
			{
				String reply = getVotingPlugin(channel).addUserToVoting(nickname, true, parameters.size() < 1 ? "" : contentAfterCommand);
				if (!reply.equals("")) ircgui.sendMessageToChannel(channel, reply);
			}
			// Voteno
			else if (message.startsWith(botCallSign + "voteno"))
			{
				String reply = getVotingPlugin(channel).addUserToVoting(nickname, false, parameters.size() < 1 ? "" : contentAfterCommand);
				if (!reply.equals("")) ircgui.sendMessageToChannel(channel, reply);
			}
			// Voteinfo
			else if (message.startsWith(botCallSign + "voteinfo"))
			{
				ArrayList<String> msgs = getVotingPlugin(channel).getVotingInformation();
				for (String msg : msgs) ircgui.sendMessageToChannel(channel, msg);
			}
			// Define
			else if (message.startsWith(botCallSign + "define"))
			{
				if (parameters.size() < 1)
					ircgui.sendMessageToChannel(channel, getCommandHelp("define"));
				else
					ircgui.sendMessageToChannel(channel, getDefine(parameters.get(0), parameters.size() > 1 ? parameters.get(1) : channel));
			}
			// Defadd
			else if (message.startsWith(botCallSign + "defadd"))
			{
				if (parameters.size() < 2)
					ircgui.sendMessageToChannel(channel, getCommandHelp("defadd"));
				else
					ircgui.sendMessageToChannel(channel, addDefine(nickname, parameters.get(0), contentAfterSecondCommand, channel));
			}
			// Defrem
			else if (message.startsWith(botCallSign + "defrem"))
			{
				if (parameters.size() < 1)
					ircgui.sendMessageToChannel(channel, getCommandHelp("defrem"));
				else
					ircgui.sendMessageToChannel(channel, removeDefine(nickname, parameters.get(0)));
			}
			// Definfo
			else if (message.startsWith(botCallSign + "definfo"))
			{
				if (parameters.size() < 1)
					ircgui.sendMessageToChannel(channel, getCommandHelp("definfo"));
				else
					ircgui.sendMessageToChannel(channel, getDefineInfo(parameters.get(0), parameters.size() > 1 ? parameters.get(1) : channel));
			}
			// Defchange
			else if (message.startsWith(botCallSign + "defchange"))
			{
				if (parameters.size() < 2)
					ircgui.sendMessageToChannel(channel, getCommandHelp("defchange"));
				else
					ircgui.sendMessageToChannel(channel, changeDefine(parameters.get(0), contentAfterSecondCommand, channel, nickname));
			}
			// Joinget
			else if (message.startsWith(botCallSign + "joinget"))
			{
				if (parameters.size() < 1)
					ircgui.sendMessageToChannel(channel, getCommandHelp("joinget"));
				else
					ircgui.sendMessageToChannel(channel, getDefine(parameters.get(0), channel));
			}
			// Joinadd
			else if (message.startsWith(botCallSign + "joinadd"))
			{
				if (parameters.size() < 2)
					ircgui.sendMessageToChannel(channel, getCommandHelp("joinadd"));
				else
					ircgui.sendMessageToChannel(channel, addJoinMessage(parameters.get(0), contentAfterSecondCommand, channel));
			}
			// Joinrem
			else if (message.startsWith(botCallSign + "joinrem"))
			{
				if (parameters.size() < 1)
					ircgui.sendMessageToChannel(channel, getCommandHelp("joinrem"));
				else
					ircgui.sendMessageToChannel(channel, removeJoinMessage(parameters.get(0), channel));
			}
			// Joinchange
			else if (message.startsWith(botCallSign + "joinchange"))
			{
				if (parameters.size() < 2)
					ircgui.sendMessageToChannel(channel, getCommandHelp("joinchange"));
				else
					ircgui.sendMessageToChannel(channel, changeJoinMessage(parameters.get(0), contentAfterSecondCommand, channel));
			}
			// Wordget
			else if (message.startsWith(botCallSign + "wordget"))
			{
				if (parameters.size() < 1)
					ircgui.sendMessageToChannel(channel, getCommandHelp("wordget"));
				else
					ircgui.sendMessageToChannel(channel, getWord(nickname, channel, parameters.get(0)));
			}
			// Wordadd
			else if (message.startsWith(botCallSign + "wordadd"))
			{
				if (parameters.size() < 2)
					ircgui.sendMessageToChannel(channel, getCommandHelp("wordadd"));
				else
					ircgui.sendMessageToChannel(channel, addWord(nickname, parameters.get(0), contentAfterSecondCommand, channel));
			}
			// Wordrem
			else if (message.startsWith(botCallSign + "wordrem"))
			{
				if (parameters.size() < 1)
					ircgui.sendMessageToChannel(channel, getCommandHelp("wordrem"));
				else
					ircgui.sendMessageToChannel(channel, removeWord(parameters.get(0), channel));
			}
			// Wordchange
			else if (message.startsWith(botCallSign + "wordchange"))
			{
				if (parameters.size() < 2)
					ircgui.sendMessageToChannel(channel, getCommandHelp("wordchange"));
				else
					ircgui.sendMessageToChannel(channel, changeWord(parameters.get(0), contentAfterSecondCommand, channel));
			}
			// Unknown command
			else
			{
				ircgui.sendMessageToChannel(channel, "Unknown command: " + message.split(" ")[0]);
			}
		}
	}
}