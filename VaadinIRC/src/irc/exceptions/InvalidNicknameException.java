package irc.exceptions;

public class InvalidNicknameException extends Exception
{
	public InvalidNicknameException()
	{
		super();
	}
	
	@Override
	public String getMessage()
	{
		return "Nickname contains invalid characters.";
	}
}
