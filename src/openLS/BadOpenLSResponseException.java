package openLS;

public class BadOpenLSResponseException extends Exception
{
	private static final long serialVersionUID = -257688139908522858L;

	public BadOpenLSResponseException(String message)
	{
		super(message);
	}
}
