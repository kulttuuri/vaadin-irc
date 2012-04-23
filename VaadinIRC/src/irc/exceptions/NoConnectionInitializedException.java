package irc.exceptions;

public class NoConnectionInitializedException extends Exception
{
	public NoConnectionInitializedException()
	{
		super();
	}
	
	@Override
	public String getMessage()
	{
		return "No IRC connection has been initialized.";
	}
}
