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

package irc;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains some useful IRC helper functions & enums.
 * @author Aleksi Postari
 *
 */
public class IRCHelper
{
	/** Removes all previously applied color and formatting attributes. */
	public static final String TEXT_NORMAL = "\u000f";
	/** Bold text. */
	public static final String TEXT_BOLD = "\u0002";
	/** Underlined text. */
	public static final String TEXT_UNDERLINE = "\u001f";
	/** Reversed text (may be rendered as italic text in some clients). */
	public static final String TEXT_REVERSE = "\u0016";
	/** White coloured text. */
	public static final String TEXT_WHITE = "\u000300";
	/** Black coloured text. */
	public static final String TEXT_BLACK = "\u000301";
	/** Dark blue coloured text. */
	public static final String TEXT_DARK_BLUE = "\u000302";
	/** Dark green coloured text. */
	public static final String TEXT_DARK_GREEN = "\u000303";
	/** Red coloured text. */
	public static final String TEXT_RED = "\u000304";
	/** Brown coloured text. */
	public static final String TEXT_BROWN = "\u000305";
	/** Purple coloured text. */
	public static final String TEXT_PURPLE = "\u000306";
	/** Olive coloured text. */
	public static final String TEXT_OLIVE = "\u000307";
	/** Yellow coloured text. */
	public static final String TEXT_YELLOW = "\u000308";
	/** Green coloured text. */
	public static final String TEXT_GREEN = "\u000309";
	/** Teal coloured text. */
	public static final String TEXT_TEAL = "\u000310";
	/** Cyan coloured text. */
	public static final String TEXT_CYAN = "\u000311";
	/** Blue coloured text. */
	public static final String TEXT_BLUE = "\u000312";
	/** Magenta coloured text. */
	public static final String TEXT_MAGENTA = "\u000313";
	/** Dark gray coloured text. */
	public static final String TEXT_DARK_GRAY = "\u000314";
	/** Light gray coloured text. */
	public static final String TEXT_LIGHT_GRAY = "\u000315";
	
	/**
	 * Formats given IRC message to HTML formatting.
	 * @return Returns the formatted text.
	 */
	public static String formatIRCTextToHTML(String ircmsg)
	{
		ircmsg = ircmsg.replace(TEXT_NORMAL, "<font style='color: black; font-style: normal; font-weight: normal; text-decoration: none;'>");
		ircmsg = ircmsg.replace(TEXT_BOLD, "<font style='font-weight: bold;'>");
		ircmsg = ircmsg.replace(TEXT_UNDERLINE, "<font style='text-decoration: underline;'>");
		ircmsg = ircmsg.replace(TEXT_REVERSE, "<font style='font-style: italic;'>");
		ircmsg = ircmsg.replace(TEXT_WHITE, "<font style='color: white;'>");
		ircmsg = ircmsg.replace(TEXT_BLACK, "<font style='color: black;'>");
		ircmsg = ircmsg.replace(TEXT_DARK_BLUE, "<font style='color: darkblue;'>");
		ircmsg = ircmsg.replace(TEXT_DARK_GREEN, "<font style='color: darkgreen;'>");
		ircmsg = ircmsg.replace(TEXT_RED, "<font style='color: red;'>");
		ircmsg = ircmsg.replace(TEXT_BROWN, "<font style='color: brown;'>");
		ircmsg = ircmsg.replace(TEXT_PURPLE, "<font style='color: purple;'>");
		ircmsg = ircmsg.replace(TEXT_OLIVE, "<font style='color: olive;'>");
		ircmsg = ircmsg.replace(TEXT_YELLOW, "<font style='color: yellow;'>");
		ircmsg = ircmsg.replace(TEXT_GREEN, "<font style='color: green;'>");
		ircmsg = ircmsg.replace(TEXT_TEAL, "<font style='color: teal;'>");
		ircmsg = ircmsg.replace(TEXT_CYAN, "<font style='color: cyan;'>");
		ircmsg = ircmsg.replace(TEXT_BLUE, "<font style='color: blue;'>");
		ircmsg = ircmsg.replace(TEXT_MAGENTA, "<font style='color: magenta;'>");
		ircmsg = ircmsg.replace(TEXT_DARK_GRAY, "<font style='color: darkgray;'>");
		ircmsg = ircmsg.replace(TEXT_LIGHT_GRAY, "<font style='color: lightgray;'>");
		
		return ircmsg;
	}
	
	/**
	 * Removes tags from a given string and returns the parsed string.<br>
	 * Example tags: <b>, </b>, <p>, <br/> ...
	 * // TODO: TOO GREEDY. Make only check for HTML tags.
	 * @param string Target String.
	 * @return Returns the String where all the tags have been parsed.
	 */
	public static String removeTags(String string)
	{
	    if (string == null || string.length() == 0) return string;
	    
	    Pattern REMOVE_TAGS = Pattern.compile("<.+?>");
	    Matcher m = REMOVE_TAGS.matcher(string);
	    return m.replaceAll("");
	}
	
	/**
	 * Converts URLs to html links.<br>
	 * example: www.google.com => <a href="www.google.com">www.google.com</a>
	 * @param text Target text.
	 * @return Returns the text where all urls have been converted into HTML links.
	 */
	public static String convertURLsToHTMLLinks(String text)
	{
	    if (text == null) return text;
	    
	    return text.replaceAll("(\\A|\\s)((http|https|ftp|mailto):\\S+)(\\s|\\z)", "$1<a target=\"_blank\" href=\"$2\">$2</a>$4");
	}
	
	/**
	 * Generates message timestamp.
	 * @param useTwentyFourFormat If true, return hour in 24 hour format.
	 * @return Returns message timestamp.
	 */
	public static String getTimestamp(boolean useTwentyFourFormat)
	{
		return "<small>"+getCurrentHour(useTwentyFourFormat) + ":" + getCurrentMinutes() + "</small> ";
	}
	
	/**
	 * Returns the current hour.
	 * @param useTwentyFourFormat If true, will return in 24 hour format. Otherwise in 12 hour format.
	 * @return Returns the current hour in given format.
	 */
	private static String getCurrentHour(boolean useTwentyFourFormat)
	{
		return new java.text.SimpleDateFormat(useTwentyFourFormat == true ? "H" : "h").format(new java.util.Date());
	}
	
	/**
	 * Returns the current minutes.
	 * @return Current minutes.
	 */
	private static String getCurrentMinutes()
	{
		return new java.text.SimpleDateFormat("m").format(new java.util.Date());
	}
	
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
			int index = 4;
			while (spaceSplit.get(index) != null)
			{
				returnList.add(spaceSplit.get(index));
				index++;
			}
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
	 * @return Returns the parsed {@link irc.JavadocLibrary#ircNickname nickname}. If there were errors, will return null object.
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
