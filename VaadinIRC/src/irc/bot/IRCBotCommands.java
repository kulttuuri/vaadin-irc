package irc.bot;

import irc.IRCHelper;
import irc.exceptions.NoConnectionInitializedException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Contains all the IRCbot command functions.
 * @author Aleksi Postari
 *
 */
public class IRCBotCommands extends IRCBotSQL
{
	/** List of commands that the bot supports. */
	private static HashMap<String, String> commands = new HashMap<String, String>();
	static
	{
		commands.put("defadd", "Adds new define, Parameters: word content");
		commands.put("defrem", "Removes define, Parameters: word");
		commands.put("define", "Gets define for given word, Parameters: word ?#channel");
		commands.put("definfo", "Gets information about given define, Parameters: word ?#channel");
		commands.put("defchange", "Changes existing define, Parameters: word content");
		commands.put("joinget", "Gets join message that has been added to user, Parameters: nickname");
		commands.put("joinadd", "Adds join message for given user, Parameters: nickname message");
		commands.put("joinrem", "Removes join message from given user, Parameters: nickname");
		commands.put("joinchange", "Changes join message on given existing user, Parameters: nickname message");
		commands.put("wordget", "Gets word for which the bot responds to, Parameters: word");
		commands.put("wordadd", "Adds message for which the bot responds to, Parameters: word content");
		commands.put("wordrem", "Removes message which the bot responds to, Parameters: word");
		commands.put("wordchange", "Changes message for which the bot responds to, Parameters: word content");
		commands.put("userstats", "Gets stats for given user, Parameters: nickname ?#channel");
		commands.put("top10", "Gets top10 chatters for a channel, Parameters: ?#channel");
		commands.put("chanstats", "Gets stats for a channel, Parameters: ?#channel");
		commands.put("commandhelp", "Shows description for given command, Parameters: command");
		commands.put("lastseen", "Shows when the user has last time sent message to the channel, Parameters: nickname ?#channel");
		commands.put("randomnick", "Gets random sentence for given nickname, Parameters: nickname");
	}
	
	/** Sign that is used to call bot commands. For example: !define or .define */
	protected String botCallSign;
	/** Version */
	private String version;
	
	/**
	 * Constructor to pass the information to super method {@link irc.bot.IRCBotSQL}.
	 * @param enabled Is the bot enabled?
	 * @param address SQL server address.
	 * @param username SQL server username.
	 * @param password SQL server password.
	 * @param databaseDriver Java Database Driver.
	 * @param databaseName SQL database name.
	 * @param botCallSign What sign is used to call bot commands.
	 * @param version Bot version.
	 */
	public IRCBotCommands(boolean enabled, String address, String username, String password, String databaseDriver, String databaseName, String botCallSign, String version)
	{
		super(enabled, address, username, password, databaseDriver, databaseName);
		this.botCallSign = botCallSign;
		this.version = version;
	}

	/**
	 * Returns information about the bot.
	 * @return Bot information.
	 */
	protected String getBotInfo()
	{
		return "Vaadin Irkkia by Aleksi Postari. Version: " + version;
	}
	
	/**
	 * When new channel message is received, it will be logged into database.<br>
	 * Also logs the channel and user stats.
	 * @param channel Channel name.
	 * @param nickname Sender nickname.
	 * @param message Message to be added.
	 */
	protected void addMessageToDatabase(String channel, String nickname, String message)
	{
		int nickStats = -1;
		int chanStats = -1;
		try
		{
			ArrayList<String> split = IRCHelper.splitCommandsToList(message, " ");
			if (split.size() == 0) { System.out.println("empty!"); return; }
			ResultSet chan = executePreparedQuery("SELECT * FROM chanstats WHERE channel = ?", new String[] { channel });
			ResultSet nick = executePreparedQuery("SELECT * FROM nickstats WHERE nickname = '" + nickname + "' AND channel = '" + channel + "'", new String[] { });
			while (chan.next()) chanStats = chan.getInt("words");
			while (nick.next()) nickStats = nick.getInt("words");

			if (nickStats == -1)
			{
				nickStats = 0;
				executePreparedUpdate("INSERT INTO nickStats (nickname, channel) VALUES (?, ?)", new String[] { nickname, channel });
			}
			if (chanStats == -1)
			{
				chanStats = 0;
				executePreparedUpdate("INSERT INTO chanStats (channel) VALUES (?)", new String[] { channel });
			}
			
			executePreparedUpdate("UPDATE chanStats SET words = words + " + split.size() + " WHERE channel = ?", new String[] { channel });
			executePreparedUpdate("UPDATE nickStats SET words = words + " + split.size() + " WHERE nickname = ? AND channel = ?", new String[] { nickname, channel });
			executePreparedUpdate("INSERT INTO chanmessages (channel, nickname, content) VALUES (?, ?, ?)", new String[] { channel, nickname, message });
		}
		catch (SQLException e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Prints list of commands to the given channel.
	 * @param IRC irc.
	 * @param channel Name of the channel.
	 */
	protected void sendCommandsToChannel(irc.IRC irc, String channel)
	{
		String returnMsg = "";
		try
		{
			returnMsg = "";
			irc.sendMessageToChannel(channel, "List of all commands (parameters with ? are optional): ");
			int i = 0;
			for (Entry<String, String> entry : commands.entrySet())
			{
				i++;
				returnMsg += entry.getKey() + " " + entry.getValue().split(", Parameters: ")[1] + " | ";
				if (i == 10)
				{
					i = 0;
					irc.sendMessageToChannel(channel, returnMsg);
					returnMsg = "";
				}
			}
			irc.sendMessageToChannel(channel, returnMsg);
		}
		catch (NoConnectionInitializedException e)
		{
			e.printStackTrace();
		}
		
		/*return "Commands (parameters with ? are optional, remember to add "+botCallSign+"): " +
				", defrem define, definfo define, defchange define content," +
				"randomsentence nickname ?word, searchlogs word, searchDefines word, " +
				"addjoinmsg nickname message, removejoinmsg nickname, top10, userstats nickname, chanstats #channel, " +
				"botinfo";*/
	}
	
	/**
	 * Returns information when the user has last been seen in a channel 
	 * (when user has last time sent message on a channel).
	 * @param nickname Nickname
	 * @param channel Channel
	 * @return Returns the success message for the operation.
	 */
	public String getLastSeen(String nickname, String channel)
	{
		String NO_NICK_FOUND = "Sorry! I do not have any records for user " + nickname + " in channel " + channel + ".";
		String returnMsg = "";
		try
		{
			if (getAmountOfRowsForQuery("SELECT sent FROM chanmessages WHERE channel = ? AND nickname = ? ORDER BY sent DESC LIMIT 1", new String[] { channel, nickname }) < 1)
				return NO_NICK_FOUND;
			ResultSet result = executePreparedQuery("SELECT sent FROM chanmessages WHERE channel = ? AND nickname = ? ORDER BY sent DESC LIMIT 1", new String[] { channel, nickname });
			while (result.next())
			{
				// Get distance between current and sent time
				java.sql.Timestamp timestamp = result.getTimestamp("sent");
				java.util.Date date = timestamp;
				java.util.Date currentDate = new java.util.Date();
				
				long secs = (currentDate.getTime() - date.getTime()) / 1000;
				int hours = (int) (secs / 3600);
				secs = secs % 3600;
				int mins = (int) (secs / 60);
				secs = secs % 60;
				System.out.println("hours: " + hours + " mins: " + mins);
				System.out.println("last msg added: " + timestamp);
				returnMsg = nickname + " has last time sent message " + hours + " hours and " + mins + " minutes ago in channel " + channel + ".";
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return NO_NICK_FOUND;
		}
		return returnMsg;
	}
	
	/**
	 * Gets help & syntax for given command.
	 * @param command Name of the command. Can contain the bot call sign.
	 * @return Returns the success message for the operation.
	 */
	public String getCommandHelp(String command)
	{
		if (command.startsWith(botCallSign)) command = command.substring(1, command.length());
		
		if (!commands.containsKey(command)) return "Command " + botCallSign + command + " does not exist.";
		else return command + ": " + commands.get(command);
	}
	
	/**
	 * Gets statistics for given channel.
	 * @param channel Name of the channel.
	 * @return Returns the success message for the operation.
	 */
	protected String getChanStats(String channel)
	{
		String NO_CHANNEL_FOUND = "Stats for channel " + channel + " were not found.";
		String returnMsg = "";
		try
		{
			if (getAmountOfRowsForQuery("SELECT * FROM chanstats WHERE channel = ?", new String[] { channel }) < 1)
				return NO_CHANNEL_FOUND;
			ResultSet result = executePreparedQuery("SELECT words, added FROM chanstats WHERE channel = ?", new String[] { channel });
			while (result.next()) returnMsg = "There have been typed total of " + result.getInt("words") + " words in channel " + channel + " since " + result.getTimestamp("added");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return NO_CHANNEL_FOUND;
		}
		return returnMsg;
	}
	
	/**
	 * Gets statistics for given nickname.
	 * @param nickname Nickname
	 * @param channel Channel
	 * @return Returns the success message for the operation.
	 */
	protected String getNickStats(String nickname, String channel)
	{
		String NO_NICK_FOUND = "Stats for nickname " + nickname + " in channel " + channel + " were not found.";
		String returnMsg = "";
		try
		{
			if (getAmountOfRowsForQuery("SELECT * FROM nickstats WHERE channel = ? AND nickname = ?", new String[] { channel, nickname }) < 1)
				return NO_NICK_FOUND;
			ResultSet result = executePreparedQuery("SELECT words, added FROM nickstats WHERE channel = ? AND nickname = ?", new String[] { channel, nickname });
			while (result.next()) returnMsg = nickname + " has typed total of " + result.getInt("words") + " words since " + result.getString("added") + " in channel " + channel + ".";
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return NO_NICK_FOUND;
		}
		return returnMsg;
	}
	
	/**
	 * Searches from all channel messages for given search term.
	 * @param search Search term.
	 * @param channel Channel.
	 * @return Returns the success message for the operation.
	 * @deprecated Does not work. Will need to be implemented. TODO: Implement.
	 */
	@Deprecated
	protected String searchFromLogs(String search, String channel)
	{
		search = search.trim();
		String SEARCH_NOT_FOUND = "Cannot find messages that contain " + search;
		String returnMessage = "";
		try
		{
			int resultAmount = getAmountOfRowsForQuery("SELECT content FROM chanmessages WHERE channel = ? AND content LIKE '%?%'", new String[] { channel,  });
			if (resultAmount < 1) return SEARCH_NOT_FOUND;
			if (resultAmount > 1) return "Found multiple results for search " + search + " ("+resultAmount+" results). To get result at index, type: "+botCallSign+"searchrow index|random " + search;
			ResultSet result = executePreparedQuery("SELECT content FROM chanmessages WHERE channel = ? AND content LIKE '%?%", new String[] { channel, search });
			while (result.next()) returnMessage = "Result for " +  search + ": " + result.getString("added") + " <" + result.getString("nickname") + "> " + result.getString("content");
		}
		catch (SQLException e)
		{
			System.out.println(e.toString());
			e.printStackTrace();
			return SEARCH_NOT_FOUND;
		}
		return returnMessage;
	}
	
	/**
	 * Returns the top chatters for a channel.
	 * @param channel Name of the channel.
	 * @return Returns the success message for the operation.
	 */
	protected String getTopChatters(String channel)
	{
		String NO_STATS_FOUND = "No stats for channel " + channel + " were found.";
		String returnMsg = "";
		try
		{
			if (getAmountOfRowsForQuery("SELECT * FROM nickstats WHERE channel = ?", new String[] { channel }) < 1)
				return NO_STATS_FOUND;
			ResultSet result = executePreparedQuery("SELECT nickname, words FROM nickstats WHERE channel = ? ORDER BY words DESC LIMIT 10", new String[] { channel });
			returnMsg = "Top 10 chatters for channel " + channel + ": ";
			for (int i = 1; result.next(); i++)
				returnMsg += i + ". " + result.getString("nickname") + " (" + result.getInt("words") + "), ";
			returnMsg = returnMsg.substring(0, returnMsg.length()-2);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return NO_STATS_FOUND;
		}
		return returnMsg;
	}
	
	/**
	 * Used to get random sentence from given user on given channel.
	 * @param nickname Nickname.
	 * @param channel Channel.
	 * @return Returns the success message for the operation.
	 */
	protected String getRandomSentenceFromUser(String nickname, String channel)
	{
		String USER_NOT_FOUND = "Username " + nickname + " was not found in the logs for channel " + channel + ".";
		String returnString = "";
		try
		{
			ResultSet results = executePreparedQuery("SELECT content, sent FROM chanmessages WHERE nickname = ? AND channel = ? ORDER BY RAND() LIMIT 1", new String[] { nickname, channel });
			while (results.next()) returnString = results.getString("sent") + " " + nickname + ": " + results.getString("content");
			if (returnString == null || returnString.equals("")) return USER_NOT_FOUND;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return USER_NOT_FOUND;
		}
		return returnString;
	}
	
	/**
	 * Tries to get join message for a user.
	 * @param nickname Target nickname.
	 * @param channel Name of the channel.
	 * @return Returns the join message for given nickname if it exists. If does not exist, will return "".
	 */
	protected String getJoinMessage(String nickname, String channel)
	{
		String returnString = "";
		try
		{
			ResultSet results = executePreparedQuery("SELECT message FROM joinmessages WHERE nickname = ? AND channel = ?", new String[] { nickname, channel });
			while (results.next()) returnString = results.getString("message");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
		return returnString;
	}
	
	/**
	 * Changes join message for given user.
	 * @param nickname Target nickname.
	 * @param message New join message.
	 * @param channel Name of the channel.
	 * @return Returns the success message for the operation.
	 */
	protected String changeJoinMessage(String nickname, String message, String channel)
	{
		String CHANGED = "Changed join message for user " + nickname + " to " + message + ".";
		String DOES_NOT_EXIST = "Join message for user " + nickname + " does not exist.";
		try
		{
			if (getAmountOfRowsForQuery("SELECT message FROM joinmessages WHERE nickname = ? AND channel = ?", new String[] { nickname, channel }) == 0)
				return DOES_NOT_EXIST;
			
			int results = executePreparedUpdate("UPDATE joinmessages SET message = ? WHERE nickname = ? AND channel = ?", new String[] { message, nickname, channel });
			if (results == 0) throw new SQLException();
		}
		catch (SQLException e)
		{
			System.out.println(e.toString());
			e.printStackTrace();
			return DOES_NOT_EXIST;
		}
		return CHANGED;
	}
	
	/**
	 * Adds new join message for user.
	 * @param nickname Target nickname for whom the join message will be added to.
	 * @param message Message to be added when user joins the channel.
	 * @param channel Channel.
	 * @return Returns the success message for the operation.
	 */
	protected String addJoinMessage(String nickname, String message, String channel)
	{
		String ADDED = "Added join message " + message + " to user " + nickname + ".";
		String ALREADY_EXISTS = "Join message for user " + nickname + " already exists. Use changejoinmsg to modify existing messages.";
		try
		{
			if (getAmountOfRowsForQuery("SELECT message FROM joinmessages WHERE nickname = ?", new String[] { nickname }) > 0)
				return ALREADY_EXISTS;
			
			int results = executePreparedUpdate("INSERT INTO joinmessages (nickname, message, channel) VALUES (?, ?, ?)", new String[] { nickname, message, channel });
			if (results == 0) throw new SQLException();
		}
		catch (SQLException e)
		{
			System.out.println(e.toString());
			e.printStackTrace();
			return ALREADY_EXISTS;
		}
		return ADDED;
	}
	
	/**
	 * Removes join message from a user.
	 * @param nickname Target nickname for whom the join message will be removed from. 
	 * @param channel Channel name.
	 * @return Returns the success message for the operation.
	 */
	protected String removeJoinMessage(String nickname, String channel)
	{
		String DOES_NOT_EXIST = "Join message for user " + nickname + " does not exist.";
		String REMOVED = "Removed join message for user " + nickname + ".";
		try
		{
			int results = executePreparedUpdate("DELETE FROM joinmessages WHERE nickname = ?", new String[] { nickname });
			if (results == 0) throw new SQLException();
		}
		catch (SQLException e)
		{
			return DOES_NOT_EXIST;
		}
		return REMOVED;
	}
	
	/**
	 * Searches all defines for given word.
	 * @param nickname Nickname who did the search.
	 * @param search Searchable word.
	 * @return Returns the found row. If row was not found, will return no row message.
	 */
	protected String searchDefines(String nickname, String search)
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
	protected String addDefine(String nickname, String define, String content, String channel)
	{
		String DEFINE_ADDED = "Added define " + define + ".";
		String DEFINE_ALREADY_EXISTS = "Define " + define + " already exists. Use defchange to modify existing defines.";
		try
		{
			if (getAmountOfRowsForQuery("SELECT content FROM defines WHERE define = ?", new String[] { define }) > 0)
				return DEFINE_ALREADY_EXISTS;
			
			int results = executePreparedUpdate("INSERT INTO defines (nickname, define, content, channel) VALUES (?, ?, ?, ?)", new String[] { nickname, define, content, channel });
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
	protected String removeDefine(String nickname, String define)
	{
		String NO_DEFINE_EXISTS = "Define " + define + " does not exist.";
		String REMOVED_DEFINE = "Removed define " + define + ".";
		try
		{
			int results = executePreparedUpdate("DELETE FROM defines WHERE define = ?", new String[] { define });
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
	protected String changeDefine(String define, String content, String channel, String nickname)
	{
		String DEFINE_ADDED = "Changed define " + define + " content to: " + content;
		String NO_DEFINE_EXISTS = "Define " + define + " does not exist.";
		try
		{
			if (getAmountOfRowsForQuery("SELECT content FROM defines WHERE define = ?", new String[] { define }) == 0)
				return NO_DEFINE_EXISTS;
			
			int results = executePreparedUpdate("UPDATE defines SET nickname = ?, define = ?, content = ?, channel = ? WHERE define = ?", new String[] { nickname, define, content, channel, define });
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
	 * @param channel IRC channel name.
	 * @return Returns the success message for the operation.
	 */
	protected String getDefineInfo(String define, String channel)
	{
		String returnString = "";
		String NO_DEFINE_EXISTS = "Define " + define + " does not exist in channel " + channel + ".";
		try
		{
			ResultSet results = executePreparedQuery("SELECT * FROM defines WHERE define = ? AND channel = ?", new String[] { define, channel });
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
	 * @param define Name of the define.
	 * @param channel IRC channel name.
	 * @return Returns the success message for the operation.
	 */
	protected String getDefine(String define, String channel)
	{
		String returnString = "";
		String NO_DEFINE_EXISTS = "Define " + define + " does not exist in channel " + channel;
		try
		{
			ResultSet results = executePreparedQuery("SELECT content FROM defines WHERE define = ? AND channel = ?", new String[] { define, channel });
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
	
	/**
	 * Tries to get word definition.
	 * @param Nickname caller nickname.
	 * @param channel Name of the channel.
	 * @param message Whole message that user sent.
	 * @return Returns the content for the word if it is found. Otherwise "".
	 */
	protected String getWord(String nickname, String channel, String message)
	{
		message = message.toLowerCase();
		String returnString = "";
		try
		{
			ResultSet results = executePreparedQuery("SELECT message, word FROM words WHERE channel = ?", new String[] { channel });
			while (results.next())
			{
				String msg = results.getString("word");
				if (message.contains(msg.toLowerCase())) return parseVariables(results.getString("message"), nickname);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
		return returnString;
	}
	
	/**
	 * Changes the content for given word.
	 * @param word Word that you would want to change.
	 * @param message New content for the word.
	 * @param channel Name of the channel.
	 * @return Returns the success message for the operation.
	 */
	protected String changeWord(String word, String message, String channel)
	{
		if (word.length() <= 3 || word.length() >= 10)
			return "Word length can only be 3-10 characters long.";
		
		String CHANGED = "Changed word's " + word + " content to " + message + ".";
		String DOES_NOT_EXIST = "Word " + word + " does not exist.";
		try
		{
			if (getAmountOfRowsForQuery("SELECT message FROM words WHERE channel = ? AND word = ?", new String[] { channel, word }) == 0)
				return DOES_NOT_EXIST;

			int results = executePreparedUpdate("UPDATE words SET message = ? WHERE word = ? AND channel = ?", new String[] { message, word, channel });
			if (results == 0) throw new SQLException();
		}
		catch (SQLException e)
		{
			System.out.println(e.toString());
			e.printStackTrace();
			return DOES_NOT_EXIST;
		}
		return CHANGED;
	}
	
	/**
	 * Adds new word with content.
	 * @param nickname Sender nickname.
	 * @param word Matching word.
	 * @param message Message to be added that matches channel messages.
	 * @param channel Channel.
	 * @return Returns the success message for the operation.
	 */
	protected String addWord(String nickname, String word, String message, String channel)
	{
		if (word.length() < 4 || word.length() > 10)
			return "Word length can only be 4-10 characters long.";
		
		String ADDED = "Added word " + word + " with content " + message + ".";
		String ALREADY_EXISTS = "Word " + word + " already exists. Use changeword to modify existing words.";
		try
		{
			if (getAmountOfRowsForQuery("SELECT message FROM words WHERE CHANNEL = ? AND word = ?", new String[] { channel, word }) > 0)
				return ALREADY_EXISTS;
			
			int results = executePreparedUpdate("INSERT INTO words (word, nickname, message, channel) VALUES (?, ?, ?, ?)", new String[] { word, nickname, message, channel });
			if (results == 0) throw new SQLException();
		}
		catch (SQLException e)
		{
			System.out.println(e.toString());
			e.printStackTrace();
			return ALREADY_EXISTS;
		}
		return ADDED;
	}
	
	/**
	 * Removes given word message.
	 * @param channel Channel name.
	 * @return Returns the success message for the operation.
	 */
	protected String removeWord(String word, String channel)
	{
		String DOES_NOT_EXIST = "Word " + word + " does not exist.";
		String REMOVED = "Removed word " + word + ".";
		try
		{
			int results = executePreparedUpdate("DELETE FROM words WHERE word = ? AND channel = ?", new String[] { word, channel });
			if (results == 0) throw new SQLException();
		}
		catch (SQLException e)
		{
			return DOES_NOT_EXIST;
		}
		return REMOVED;
	}
	
	/**
	 * Goes through the message and replaces certain variables with new content.
	 * @return Returns the parsed message.
	 */
	private String parseVariables(String message, String nickname)
	{
		message = message.replace("%nickname%", nickname);
		return message;
	}
}