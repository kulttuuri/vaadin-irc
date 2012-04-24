package VaadinIRC;


import irc.IRCSession;
import VaadinIRC.VaadinIRC.VaadinIRC;
import com.vaadin.Application;
import com.vaadin.ui.*;

/*
 * TODO: Kannun topic captioniin (pienemm‰ll‰ fontilla)
 * TODO: Toimii enter vain yhdell‰ kanavalla, se actionlistener ei varmaan mene kuin yhdelle nyt
 * TODO: Modal login / settings popuppi alkuun ja napista saa takaisin.
 * TODO: Kun vaihtaa nickin min‰ tai joku muu: DEBUG: did read line: :ASDQWEASD!~null@a91-152-121-162.elisa-laajakaista.fi NICK :testaaja
 * TODO: Turn off serialization: http://dev-answers.blogspot.com/2007/03/how-to-turn-off-tomcat-session.html
 * TODO: External configurations file tai sitten passaa komentorivilt‰ parametrit.
 * TODO: Oma nimimerkki n‰kym‰‰n jonnekkin ja helposti vaihdettavaksi.
 * TODO: Settings valikko.
 * TODO: Settingsist‰ pituus ja korkeus (ja prossina vai pikselein‰)
 * TODO: Katso voiko tehd‰ oman actionlistenerin VaIRCInterfaceen
 * TODO: Linkit ei toimi kanavan viesteiss‰.
 * TODO: poista html tagit kanavan viesteist‰.
 */

/**
 * Entry point for VaadinIRC.
 * @author Aleksi Postari
 *
 */
public class main extends Application
{
	/**
	 * Initializes vaadin application.
	 */
	@Override
	public void init()
	{
		// Create the main window.
		Window main = new Window(settings.APP_NAME);
		main.setTheme("VaIRCTheme");
		setMainWindow(main);
		
		// TODO: Poista kun valmis.
		IRCSession session = new IRCSession("port80a.se.quakenet.org", 6667, "VaAle101", "VaIRC2", "VaUsr2");
		
		// Start VaadinIRC application.
		VaadinIRC vaadinIRC = new VaadinIRC(main, session, (Application)this);
	}
}