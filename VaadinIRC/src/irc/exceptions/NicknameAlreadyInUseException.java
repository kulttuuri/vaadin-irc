package irc.exceptions;

public class NicknameAlreadyInUseException extends Exception
{
	public NicknameAlreadyInUseException()
	{
		super();
	}
	
	@Override
	public String getMessage()
	{
		return "Somebody is already using this nickname.";
	}
}
