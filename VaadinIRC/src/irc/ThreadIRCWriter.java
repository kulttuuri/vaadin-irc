package irc;

public class ThreadIRCWriter extends Thread
{
	/** If user wants to send message, this is set to true to send the message. */
	private boolean sendMessageToServer = false;
		/** Message that will be sent to server. */
		private String messageToBeSent = "";
		/** Channel where the user will be sent. */
		private String messageToBeSentChannel = "";
	/** If user wanted to change nickname */
	private boolean userWantedToChangeNickname = false;
		/** New nickname */
		private String newNickName = "";
	
    /** Reference to IRC. */
    private IRC irc;

    /**
     * Creates new writer thread that writes messages to IRC.
     * @param irc
     */
    public ThreadIRCWriter(IRC irc)
        {
        this.irc = irc;
        }

    /**
     * Adds message to be sent to channel.
     * @param message Message to be sent.
     * @param channel Channel where message will be sent.
     */
    public void sendMessageToChannel(String message, String channel)
    {
    	this.sendMessageToServer = true;
    	this.messageToBeSent = message;
    	this.messageToBeSentChannel = channel;
    }
    
    /**
     * Thread will be run here.
     */
    @Override public void run()
    {
        int laskuri = 0;
        String row = "";

        // TODO: Format texts and variables to english.
        
        // Viestien lukeminen palvelimelta alkaa
        while (irc.isConnectionRunning())
        {
            // laskuri++;
            // Get list of nicknames
            if (laskuri == 10000)
            {
            	try {
            		irc.writeMessageToBuffer("NAMES " + irc.GUIInterface.getCurrentChannelName());
            	} catch (Exception e) { System.out.println(e); e.printStackTrace(); }
                laskuri = 0;
            }

            // If user wanted to change nickname
            else if (userWantedToChangeNickname)
            {
            	try {
            		irc.writeMessageToBuffer("NICK " + newNickName);
            	} catch (Exception e) { System.out.println(e); e.printStackTrace(); }
                userWantedToChangeNickname = false;
                newNickName = "";
            }
        }
    }
}