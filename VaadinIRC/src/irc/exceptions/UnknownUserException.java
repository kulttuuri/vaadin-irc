package irc.exceptions;

public class UnknownUserException extends Exception
{
	public UnknownUserException()
	{
		super();
	}
	
	@Override
	public String getMessage()
	{
		return "This user does not exist in the network.";
	}
}
