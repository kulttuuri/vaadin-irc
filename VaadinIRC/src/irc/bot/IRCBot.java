package irc.bot;

import irc.IRC;
import irc.IRCHelper;
import irc.IRCInterface;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Main class for IRC Bot, which can be used to save data to database, query data from database etc.
 * @author Aleksi Postari
 *
 */
public class IRCBot extends IRCBotSQL
{
	/** Sign that is used to call bot commands. For example: !define or .define */
	private String botCallSign;
	
	/**
	 * Constructor to initialize the IRCBot.
	 */
	public IRCBot(boolean enabled, String address, String username, String password, String databaseDriver, String databaseName, String botCallSign)
	{
		super(enabled, address, username, password, databaseDriver, databaseName);
		this.botCallSign = botCallSign;
	}
	
	/**
	 * When channel receives a message, this gets called.<br>
	 * Just returns if bot is disabled.<br>
	 * Checks if message if command and executes functionality for it if it was.
	 * @param IRC irc.
	 * @param row {@link {@link irc.JavadocLibrary#row}
	 */
	public void receivedChannelMessage(IRC irc, IRCInterface ircgui, String row)
	{
		if (!enabled) return;
		
		String channel = IRCHelper.getChannelFromStdMessage(row);
		String nickname = IRCHelper.getNicknameFromStdMessage(row);
		String message = IRCHelper.getStdReason(row);
		
		if (!message.startsWith(botCallSign) && !message.equals(""))
		{
			addMessageToDatabase(channel, nickname, message);
		}
		else
		{
			// Split message with spaces to list (Removes everything before message)
			String content = IRCHelper.splitMessageAfterRow(row, " ", 4);
			// Split message after command to list (removes !command from message)
			String contentAfterCommand = IRCHelper.splitMessageAfterRow(row, " ", 5);
			ArrayList<String> contentSpaces = IRCHelper.splitCommandsToList(content, " ");
			
			// Define
			if (message.startsWith(botCallSign + "define"))
			{
				if (contentSpaces.size() < 1)
					ircgui.sendMessageToChannel(channel, "Syntax: define word");
				else
					ircgui.sendMessageToChannel(channel, getDefine(nickname, contentSpaces.get(0)));
			}
			// Defadd
			else if (message.startsWith(botCallSign + "defadd"))
			{
				if (contentSpaces.size() < 2)
					ircgui.sendMessageToChannel(channel, "Syntax: defadd definename content");
				else
					ircgui.sendMessageToChannel(channel, addDefine(nickname, contentSpaces.get(0), contentAfterCommand, channel));
			}
			// Defrem
			else if (message.startsWith(botCallSign + "defrem"))
			{
				if (contentSpaces.size() < 1)
					ircgui.sendMessageToChannel(channel, "Syntax: defrem word");
				else
					ircgui.sendMessageToChannel(channel, removeDefine(nickname, contentSpaces.get(0)));
			}
			// Definfo
			else if (message.startsWith(botCallSign + "definfo"))
			{
				if (contentSpaces.size() < 1)
					ircgui.sendMessageToChannel(channel, "Syntax: definfo word");
				else
					ircgui.sendMessageToChannel(channel, getDefineInfo(contentSpaces.get(0)));
			}
			// Defchange
			else if (message.startsWith(botCallSign + "defchange"))
			{
				if (contentSpaces.size() < 2)
					ircgui.sendMessageToChannel(channel, "Syntax: defchange word content");
				else
					ircgui.sendMessageToChannel(channel, changeDefine(contentSpaces.get(0), contentAfterCommand, channel, nickname));
			}
			// Unknown command
			else
			{
				ircgui.sendMessageToChannel(channel, "Unknown command: " + message.split(" ")[0]);
			}
		}
	}
	
	/**
	 * When new channel message is received, it will be logged into database.
	 * @param channel Channel name.
	 * @param nickname Sender nickname.
	 * @param message Message to be added.
	 */
	private void addMessageToDatabase(String channel, String nickname, String message)
	{
		
	}
	
	/**
	 * Returns the list of commands.
	 * @return List of commands as an string.
	 */
	private String getCommands()
	{
		return "Commands (parameters with ? are optional, remember to add "+botCallSign+"): " +
				"defadd, defrem, randomsentence <nickname> <?word>, searchlogs <word>, searchDefines <word>, " +
				"addjoinmsg <nickname> <message>, removejoinmsg <nickname>, top10";
	}
	
	private String searchFromLogs(String nickname, String search, int amount)
	{
		String SEARCH_NOT_FOUND = nickname + ": Cannot find messages that contains " + search;
		String returnMessage = "";
		try
		{
			int resultAmount = getAmountOfRowsForQuery("SELECT content FROM chanmessages WHERE content LIKE '%"+search+"%");
			if (resultAmount < 1) return SEARCH_NOT_FOUND;
			if (resultAmount > 1) return "Found multiple results for search " + search + ". To get result at index, type: "+botCallSign+"searchrow <index> " + search;
			ResultSet result = executeQuery("SELECT content FROM chanmessages WHERE content LIKE '%"+search+"%");
			return "Result for " +  search + ": " + result.getString("content");
		}
		catch (SQLException e)
		{
			System.out.println(e.toString());
			e.printStackTrace();
			return SEARCH_NOT_FOUND;
		}
	}
	
	private void getTopChatters(String channel)
	{
		
	}
	
	private void getJoinMessage(String nickname)
	{
		
	}
	
	private void addJoinMessage(String nickname, String message)
	{
		
	}
	
	private void removeJoinMessage(String nickname)
	{
		
	}
	
	private void joinMessage(String nickname)
	{
		
	}
	
	private void getRandomSentence(String nickname, String channel, String search)
	{
		
	}
	
	/**
	 * Searches all defines for given word.
	 * @param nickname Nickname who did the search.
	 * @param search Searchable word.
	 * @return Returns the found row. If row was not found, will return no row message.
	 */
	private String searchDefines(String nickname, String search)
	{
		return "";
	}
	
	/**
	 * Adds new define.
	 * @param nickname Define adder nickname.
	 * @param define Define name.
	 * @param content Content of the define.
	 * @return Returns the success message for the operation.
	 */
	private String addDefine(String nickname, String define, String content, String channel)
	{
		String DEFINE_ADDED = nickname + ": Added define " + define + ".";
		String DEFINE_ALREADY_EXISTS = nickname + ": Define " + define + " already exists. Use defchange to modify existing defines.";
		try
		{
			if (getAmountOfRowsForQuery("SELECT content FROM defines WHERE define = '" + define + "'") > 0)
				return DEFINE_ALREADY_EXISTS;
			
			int results = executeUpdate("INSERT INTO defines (nickname, define, content, channel) VALUES ('"+nickname+"','"+define+"','"+content+"','"+channel+"')");
			if (results == 0) throw new SQLException();
		}
		catch (SQLException e)
		{
			System.out.println(e.toString());
			e.printStackTrace();
			return DEFINE_ALREADY_EXISTS;
		}
		return DEFINE_ADDED;
	}
	
	/**
	 * Removes define from database.<br>
	 * @param nickname Caller nickname.
	 * @param define Define to be removed.
	 * @return Returns the success message for the operation.
	 */
	private String removeDefine(String nickname, String define)
	{
		String NO_DEFINE_EXISTS = "Define " + define + " does not exist.";
		String REMOVED_DEFINE = "Removed define " + define + ".";
		try
		{
			int results = executeUpdate("DELETE FROM defines WHERE define = '" + define + "'");
			if (results == 0) throw new SQLException();
		}
		catch (SQLException e)
		{
			return NO_DEFINE_EXISTS;
		}
		return REMOVED_DEFINE;
	}
	
	/**
	 * Changes content of define to other.
	 * @param define Name of the define.
	 * @param content New define content.
	 * @param channel Channel.
	 * @param nickname Sender nickname.
	 * @return Returns the success message for the operation.
	 */
	private String changeDefine(String define, String content, String channel, String nickname)
	{
		String DEFINE_ADDED = "Changed define " + define + " content to: " + content;
		String NO_DEFINE_EXISTS = "Define " + define + " does not exist.";
		try
		{
			if (getAmountOfRowsForQuery("SELECT content FROM defines WHERE define = '" + define + "'") == 0)
				return NO_DEFINE_EXISTS;
			
			int results = executeUpdate("UPDATE defines SET nickname = '"+nickname+"', define = '"+define+"', content = '"+content+"', channel = '"+channel+"' WHERE define = '" + define + "'");
			if (results == 0) throw new SQLException();
		}
		catch (SQLException e)
		{
			System.out.println(e.toString());
			e.printStackTrace();
			return NO_DEFINE_EXISTS;
		}
		return DEFINE_ADDED;
	}
	
	/**
	 * Returns information about define.
	 * @param define Name of the define.
	 * @return Returns the success message for the operation.
	 */
	private String getDefineInfo(String define)
	{
		String returnString = "";
		String NO_DEFINE_EXISTS = "Define " + define + " does not exist.";
		try
		{
			ResultSet results = executeQuery("SELECT * FROM defines WHERE define = '" + define + "'");
			while (results.next())
				returnString = "Define " + define + " was created on " + results.getString("created") + " by " + results.getString("nickname") + ".";
			if (returnString == null || returnString.equals("")) returnString = NO_DEFINE_EXISTS;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return NO_DEFINE_EXISTS;
		}
		return returnString;
	}
	
	/**
	 * Gets given define from database.
	 * @param nickname Caller.
	 * @param define Name of the define.
	 * @return Returns the success message for the operation.
	 */
	private String getDefine(String nickname, String define)
	{
		String returnString = "";
		String NO_DEFINE_EXISTS = "Define " + define + " does not exist.";
		try
		{
			ResultSet results = executeQuery("SELECT content FROM defines WHERE define = '" + define + "'");
			while (results.next()) returnString = define + ": " + results.getString("content");
			if (returnString == null || returnString.equals("")) returnString = NO_DEFINE_EXISTS;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return NO_DEFINE_EXISTS;
		}
		return returnString;
	}
}
