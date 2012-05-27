package irc.bot;

import irc.IRCHelper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Contains all the IRC bot command functions.<br>
 * @author Aleksi Postari
 *
 */
public class IRCBotCommands extends IRCBotSQL
{
	/** Sign that is used to call bot commands. For example: !define or .define */
	protected String botCallSign;
	/** Version */
	private String version;
	
	public IRCBotCommands(boolean enabled, String address, String username, String password, String databaseDriver, String databaseName, String botCallSign, String version)
	{
		super(enabled, address, username, password, databaseDriver, databaseName);
		this.botCallSign = botCallSign;
		this.version = version;
	}

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
			ResultSet chan = executeQuery("SELECT * FROM chanstats WHERE channel = '" + channel + "'");
			ResultSet nick = executeQuery("SELECT * FROM nickstats WHERE nickname = '" + nickname + "' AND channel = '" + channel + "'");
			while (chan.next()) chanStats = chan.getInt("words");
			while (nick.next()) nickStats = nick.getInt("words");

			if (nickStats == -1)
			{
				nickStats = 0;
				executeUpdate("INSERT INTO nickStats (nickname, channel) VALUES ('"+nickname+"', '"+channel+"')");
			}
			if (chanStats == -1)
			{
				chanStats = 0;
				executeUpdate("INSERT INTO chanStats (channel) VALUES ('"+channel+"')");
			}
			
			executeUpdate("UPDATE chanStats SET words = words + " + split.size() + " WHERE channel = '" + channel + "'");
			executeUpdate("UPDATE nickStats SET words = words + " + split.size() + " WHERE nickname = '" + nickname + "' AND channel = '" + channel + "'");
			executeUpdate("INSERT INTO chanmessages (channel, nickname, content) VALUES ('"+channel+"', '"+nickname+"', '"+message+"')");
		}
		catch (SQLException e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the list of commands.
	 * @return List of commands as an string.
	 */
	protected String getCommands()
	{
		return "Commands (parameters with ? are optional, remember to add "+botCallSign+"): " +
				"defadd define content, defrem define, definfo define, defchange define content," +
				"randomsentence nickname ?word, searchlogs word, searchDefines word, " +
				"addjoinmsg nickname message, removejoinmsg nickname, top10, userstats nickname, chanstats #channel, " +
				"botinfo";
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
			if (getAmountOfRowsForQuery("SELECT * FROM chanstats WHERE channel = '" + channel + "'") < 1)
				return NO_CHANNEL_FOUND;
			ResultSet result = executeQuery("SELECT words, added FROM chanstats WHERE channel = '" + channel + "'");
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
			if (getAmountOfRowsForQuery("SELECT * FROM nickstats WHERE channel = '" + channel + "' AND nickname = '" + nickname + "'") < 1)
				return NO_NICK_FOUND;
			ResultSet result = executeQuery("SELECT words, added FROM nickstats WHERE channel = '" + channel + "' AND nickname = '" + nickname + "'");
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
			int resultAmount = getAmountOfRowsForQuery("SELECT content FROM chanmessages WHERE channel = '"+channel+"' AND content LIKE '%"+search+"%'");
			if (resultAmount < 1) return SEARCH_NOT_FOUND;
			if (resultAmount > 1) return "Found multiple results for search " + search + " ("+resultAmount+" results). To get result at index, type: "+botCallSign+"searchrow index|random " + search;
			ResultSet result = executeQuery("SELECT content FROM chanmessages WHERE channel = '"+channel+"' AND content LIKE '%"+search+"%");
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
			if (getAmountOfRowsForQuery("SELECT * FROM nickstats WHERE channel = '" + channel + "'") < 1)
				return NO_STATS_FOUND;
			ResultSet result = executeQuery("SELECT nickname, words FROM nickstats WHERE channel = '" + channel + "' ORDER BY words DESC LIMIT 10");
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
			ResultSet results = executeQuery("SELECT content, sent FROM chanmessages WHERE nickname = '"+nickname+"' AND channel = '"+channel+"' ORDER BY RAND() LIMIT 1");
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
			ResultSet results = executeQuery("SELECT message FROM joinmessages WHERE nickname = '" + nickname + "' AND channel = '" + channel + "'");
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
			if (getAmountOfRowsForQuery("SELECT message FROM joinmessages WHERE nickname = '" + nickname + "' AND channel = '" + channel + "'") == 0)
				return DOES_NOT_EXIST;
			
			int results = executeUpdate("UPDATE joinmessages SET message = '" + message + "' WHERE nickname = '" + nickname + "' AND channel = '" + channel + "'");
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
			if (getAmountOfRowsForQuery("SELECT message FROM joinmessages WHERE nickname = '" + nickname + "'") > 0)
				return ALREADY_EXISTS;
			
			int results = executeUpdate("INSERT INTO joinmessages (nickname, message, channel) VALUES ('"+nickname+"','"+message+"','"+channel+"')");
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
			int results = executeUpdate("DELETE FROM joinmessages WHERE nickname = '" + nickname + "'");
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
	protected String removeDefine(String nickname, String define)
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
	protected String changeDefine(String define, String content, String channel, String nickname)
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
	protected String getDefineInfo(String define, String channel)
	{
		String returnString = "";
		String NO_DEFINE_EXISTS = "Define " + define + " does not exist in channel " + channel + ".";
		try
		{
			ResultSet results = executeQuery("SELECT * FROM defines WHERE define = '" + define + "' AND channel = '" + channel + "'");
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
	protected String getDefine(String define, String channel)
	{
		String returnString = "";
		String NO_DEFINE_EXISTS = "Define " + define + " does not exist in channel " + channel;
		try
		{
			ResultSet results = executeQuery("SELECT content FROM defines WHERE define = '" + define + "' AND channel = '" + channel + "'");
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