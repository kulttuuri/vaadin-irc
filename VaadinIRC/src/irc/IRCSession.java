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
	 * @param nickname Nickname.
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
    /** User Nickname */
    private String nickname;
    /** Username */
    private String login;
    /** Real name */
    private String realName;
    
    /** Returns the server address. */
    public String getServer()					{ return server; }
    /** Returns the server port. */
    public int getServerPort()					{ return serverPort; }
    /** Returns the nickname. */
    public String getNickname()					{ return nickname; }
    /** Returns the login name. */
    public String getLogin()					{ return login; }
    /** Returns the real name. */
    public String getRealName()					{ return realName; }
    
    /** Sets the server address. */
    public void setServer(String val)			{ this.server = val; }
    /** Sets the server port. */
    public void setServerPort(int val)			{ this.serverPort = val; }
    /** Sets the nickname. */
    public void setNickname(String val)			{ this.nickname = val; }
    /** Sets the login name. */
    public void setLogin(String val)			{ this.login = val; }
    /** Sets the user real name. */
    public void setRealName(String val)			{ this.realName = val; }
}
