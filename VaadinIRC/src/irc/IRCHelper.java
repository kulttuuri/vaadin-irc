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
	 * Generates formatted IRC message from the given nickname and message and returns it.
	 * @param nick Nickname.
	 * @param message Message.
	 * @return Returns the generated channel message as an string.
	 */
	public static String generateIRCMessage(String nick, String message)
	{
		return "<b>" + nick + "</b> " + message;
	}
}
