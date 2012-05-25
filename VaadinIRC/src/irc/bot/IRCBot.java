package irc.bot;

/**
 * Main class for IRC Bot, which can be used to save data to database, query data from database etc.
 * @author Aleksi Postari
 *
 */
public class IRCBot extends IRCBotSQL
{
	/**
	 * Constructor to initialize the IRCBot.
	 */
	public IRCBot(boolean enabled, String address, String username, String password, String databaseDriver, String databaseName)
	{
		super(enabled, address, username, password, databaseDriver, databaseName);
	}
}
