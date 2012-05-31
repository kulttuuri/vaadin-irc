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
 * Contains the IRC user information such as username, server, port...
 * @author Aleksi Postari
 *
 */
public class IRCSession
{
	/**
	 * Constructor to initialize new IRC Session information without
	 * any preliminary information.
	 */
	public IRCSession()
	{
	}
	
	/**
	 * Constructor to ititialize new IRC Session information.
	 * @param server Server address.
	 * @param serverPort Server port.
	 * @param nickname {@link irc.JavadocLibrary#ircNickname nickname}.
	 * @param login Username.
	 */
	public IRCSession(String server, int serverPort, String nickname, String username, String realName)
	{
		this.server = server;
		this.serverPort = serverPort;
		this.nickname = nickname;
		this.login = username;
		this.realName = realName;
	}
	
	/** IRC Server */
    private String server;
    /** Server port */
    private int serverPort;
    /** User {@link irc.JavadocLibrary#ircNickname nickname} */
    private String nickname;
    /** Username */
    private String login;
    /** Real name */
    private String realName;
    
    /** Returns the server address. */
    public String getServer()					{ return server; }
    /** Returns the server port. */
    public int getServerPort()					{ return serverPort; }
    /** Returns the {@link irc.JavadocLibrary#ircNickname nickname}. */
    public String getNickname()					{ return nickname; }
    /** Returns the login name. */
    public String getLogin()					{ return login; }
    /** Returns the real name. */
    public String getRealName()					{ return realName; }
    
    /** Sets the server address. */
    public void setServer(String val)			{ this.server = val; }
    /** Sets the server port. */
    public void setServerPort(int val)			{ this.serverPort = val; }
    /** Sets the {@link irc.JavadocLibrary#ircNickname nickname}. */
    public void setNickname(String val)			{ this.nickname = val; }
    /** Sets the login name. */
    public void setLogin(String val)			{ this.login = val; }
    /** Sets the user real name. */
    public void setRealName(String val)			{ this.realName = val; }
}
