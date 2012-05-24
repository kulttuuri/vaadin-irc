package VaadinIRC.exceptions;

/**
 * Exception thrown if channel was not found.
 * @author Aleksi Postari
 *
 */
public class ChannelNotFoundException extends Exception
{
	/** Name of the channel which was not found. */
	private String channelName;
	
	public ChannelNotFoundException(String channelName)
	{
		super();
		this.channelName = channelName;
	}
	
	@Override
	public String getMessage()
	{
		return "This channel does not exist: " + channelName;
	}
}
