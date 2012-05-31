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

/**
 * This class contains base Javadoc notations for IRC functionality.<br>
 * This class is used for variables that are used many times in the application
 * so that there would be one location for all of those frequently used variables.<br>
 * Use links or see Javadoc tags to link to variables in this class.
 * @author Aleksi Postari
 *
 */
public abstract class JavadocLibrary
{
	/**
	 * IRC line that server sent to the client using {@link IRC#reader}.
	 */
	public static final String row = "";
	
	/**
	 * In IRC servers, nickname is necessary field and is used to separate users from each other.<br>
	 * Two users cannot have same nickname in same IRC server.
	 */
	public static final String ircNickname = "";
	
	/**
	 * IRC channels are "rooms" where users can chat with each other. All channels start with #.
	 */
	public static final String ircChannel = "";
}