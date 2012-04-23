package irc.exceptions;

public class UnknownChannelException extends Exception
{
	public UnknownChannelException()
	{
		super();
	}
	
	@Override
	public String getMessage()
	{
		return "Unknown channel.";
	}
}
