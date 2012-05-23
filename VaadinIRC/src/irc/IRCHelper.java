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
	
	/**
	 * Splits message with given index and returns all the data after given index.
	 * @param row IRC Row.
	 * @param splitSign What sign will be used to split the data.
	 * @param splitIndex After what index is all the data returned. Index starts from 0, which is the first line of data.
	 * @return Will return the splitted data. If there were errors, will return "".
	 */
	public static String splitMessageAfterRow(String row, String splitSign, int splitIndex)
	{
		try
		{
			String[] splitData = row.split(splitSign);
			String removableData = "";
			for (int i = 0; i < splitIndex; i++) removableData += splitData[i] + splitSign;
			return row.replace(removableData, "");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * Returns the IRC message type.<br>
	 * For example: PRIVMSG, 513, 001 etc...
	 * @return Returns the IRC row type. If type was not found, will return "".
	 */
	public static String getRowType(String row)
	{
		String returnType = "";
		try
		{
		returnType =  row.split(" ")[1];
		}
		catch (Exception e)
		{
			return "";
		}
		return returnType == null ? "" : returnType; 
	}
	
	/**
	 * Gets target users for server sent /MODE message.<br>
	 * Example: :Kulttuuri!u4267@irccloud.com MODE #testikannu12345 +o VaAle101
	 * @param row IRC row.
	 * @return Returns the target users in ArrayList.
	 */
	public static ArrayList<String> getModeTargetUsers(String row)
	{
		ArrayList<String> returnList = new ArrayList<String>();
		ArrayList<String> spaceSplit = splitCommandsToList(row, " ");
		try
		{
			if (spaceSplit.get(4) != null) returnList.add(spaceSplit.get(4));
			if (spaceSplit.get(5) != null) returnList.add(spaceSplit.get(5));
			if (spaceSplit.get(6) != null) returnList.add(spaceSplit.get(6));
		}
		catch (Exception e)
		{
			return returnList;
		}
		return returnList;
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
	 * @deprecated Because html tags are being removed from all messages.
	 */
	@Deprecated
	public static String generateIRCMessage(String nick, String row)
	{
		return "<b>" + nick + "</b> " + row;
	}
}
