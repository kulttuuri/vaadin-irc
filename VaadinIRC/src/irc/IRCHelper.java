/**
 * Copyright (C) 2012 Aleksi Postari
 * License type: MIT (http://en.wikipedia.org/wiki/MIT_License)
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package irc;

import java.util.ArrayList;

/**
 * Contains some useful IRC helper functions.
 * @author Aleksi Postari
 *
 */
public class IRCHelper
{
	/**
	 * Splits commands to list with given delimiter.
	 * @param row {@link JavadocLibrary#row}
	 * @param delimiter Delimiter used for the split.
	 * @return Returns the split list. If no elements were added, still returns empty ArrayList.
	 */
	public static ArrayList<String> splitCommandsToList(String row, String delimiter)
	{
		ArrayList<String> returnCommands = new ArrayList<String>();
		if (row == null || row.trim().equals("")) return returnCommands;
		try
		{
			String[] split = row.split(delimiter);
			for (String command : split)
			{
				returnCommands.add(command);
			}
		}
		catch (Exception e)
		{
			System.out.println("Error splitting commands to list: " + e);
			e.printStackTrace();
		}
		return returnCommands;
	}
	
	/**
	 * Splits message with given index and returns all the data after given index.
	 * @param row {@link JavadocLibrary#row}
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
			//e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * Gets target users for server sent /MODE message.<br>
	 * Example: :Kulttuuri!u4267@irccloud.com MODE #testikannu12345 +o VaAle101
	 * @param row {@link JavadocLibrary#row}
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
	
	/**
	 * Gets reason message from standard message.
	 * @param row {@link JavadocLibrary#row}
	 * @return Returns the standard message for given row. If reason was not found, will return "".
	 */
	public static String getStdReason(String row)
	{
		String reason = getContentFromStdMessage(row);
		if (reason == null) return "";
		else return reason;
	}
	
	/**
	 * Returns the IRC message type.<br>
	 * For example: PRIVMSG, 513, 001 etc...
	 * @param row {@link irc.JavadocLibrary#row}
	 * @return Returns the {@link JavadocLibrary#row} type. If type was not found, will return "".
	 */
	public static String getStdCommand(String row)
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
	 * Gets channel from standard irc message.
	 * @param row {@link JavadocLibrary#row}
	 * @return Returns the standard channel, or "" if could not be fetched.
	 */
	public static String getChannelFromStdMessage(String row)
	{
		try
		{
			String[] rowSpaces = row.split(" ");
			return rowSpaces[2];
		}
		catch (IndexOutOfBoundsException e)
		{
			return "";
		}
	}
	
	/**
	 * Returns the content from standard irc message.
	 * @param row {@link JavadocLibrary#row}
	 * @return Returns the content from standard irc message, or null if could not be fetched.
	 */
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
	 * Returns the nickname from standard IRC message.<br>
	 * Given message should be in format like this: :VaAle101!~null@a91-152-121-162.elisa-laajakaista.fi PART #testikannu12345 :viesti
	 * @param row {@link JavadocLibrary#row}
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
}
