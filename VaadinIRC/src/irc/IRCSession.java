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
    
    public String getServer()					{ return server; }
    public int getServerPort()					{ return serverPort; }
    public String getNickname()					{ return nickname; }
    public String getLogin()					{ return login; }
    public String getRealName()					{ return realName; }
    
    public void setServer(String val)			{ this.server = val; }
    public void setServerPort(int val)			{ this.serverPort = val; }
    public void setNickname(String val)			{ this.nickname = val; }
    public void setLogin(String val)			{ this.login = val; }
    public void setRealName(String val)			{ this.realName = val; }
}
