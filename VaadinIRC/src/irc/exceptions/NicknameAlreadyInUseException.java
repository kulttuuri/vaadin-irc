package irc.exceptions;

/**
 * When nickname is already in use by someone else, this gets thrown.
 * @author Aleksi Postari
 *
 */
public class NicknameAlreadyInUseException extends Exception
{
	/**
	 * Constructor to initialize the exception.
	 */
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
