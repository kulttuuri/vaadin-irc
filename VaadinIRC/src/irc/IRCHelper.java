package irc;

import java.util.ArrayList;

/**
 * Contains some useful IRC helper functions.
 * @author Aleksi Postari
 *
 */
public class IRCHelper
{
	public static ArrayList<String> splitCommandsToList(String row, String delimiter)
	{
		try
		{
			ArrayList<String> returnCommands = new ArrayList<String>();
			String[] split = row.split(delimiter);
			for (String command : split)
			{
				returnCommands.add(command);
			}
			return returnCommands;
		}
		catch (Exception e)
		{
			System.out.println("Error splitting commands to list: " + e);
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getStdReason(String row)
	{
		String reason = getContentFromStdMessage(row);
		if (reason == null) return "";
		else return reason;
	}
	
	public static String getStdCommand(String row)
	{
		try
		{
			String[] splitSpaces = row.split(" ");
			return splitSpaces[1];
		}
		catch (IndexOutOfBoundsException e)
		{
			return null;
		}
	}
	
	public static String getChannelFromStdMessage(String row)
	{
		try
		{
			String[] rowSpaces = row.split(" ");
			return rowSpaces[2];
		}
		catch (IndexOutOfBoundsException e)
		{
			return null;
		}
	}
	
	public static String getContentFromStdMessage(String row)
	{
		try
		{
			String[] splitMsg = row.split(":");
			return row.replace(":"+splitMsg[1], "").substring(1);
		}
		catch (IndexOutOfBoundsException e)
		{
			return null;
		}
	}
	
	/**
	 * Returns the nickname from standard IRC message.
	 * Given message should be in format like this: :VaAle101!~null@a91-152-121-162.elisa-laajakaista.fi PART #testikannu12345 :viesti
	 * @param message IRC Message.
	 * @return Returns the parsed nickname. If there were errors, will return null object.
	 */
	public static String getNicknameFromStdMessage(String row)
	{
		try
		{
			String[] splitNick = row.split("!");
			return splitNick[0].substring(1);
		}
		catch (IndexOutOfBoundsException e)
		{
			return null;
		}
	}
	
	/**
	 * Generates formatted IRC message from the given nickname and message and returns it.
	 * @param nick Nickname.
	 * @param message Message.
	 * @return Returns the generated channel message as an string.
	 */
	public static String generateIRCMessage(String nick, String row)
	{
		return "<b>" + nick + "</b> " + row;
	}
}
