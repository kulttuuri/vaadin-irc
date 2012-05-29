package irc.exceptions;

/**
 * When nickname contains some invalid characters, this gets thrown.
 * @author Aleksi Postari
 *
 */
public class InvalidNicknameException extends Exception
{
	/**
	 * Constructor to initialize the exception.
	 */
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
