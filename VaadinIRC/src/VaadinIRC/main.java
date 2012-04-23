package VaadinIRC;

import org.vaadin.artur.icepush.ICEPush;

import irc.IRCSession;
import VaadinIRC.VaadinIRC.VaadinIRC;

import com.vaadin.Application;
import com.vaadin.ui.*;

/*
 * TODO: Enter painallus toimimaan
 * TODO: Kannun topic captioniin (pienemmällä fontilla)
 * TODO: Nimimerkkilista.
 * TODO: Modal login / settings popuppi alkuun ja napista saa takaisin.
 * TODO: Kun vaihtaa nickin minä tai joku muu: DEBUG: did read line: :ASDQWEASD!~null@a91-152-121-162.elisa-laajakaista.fi NICK :testaaja
 * TODO: Turn off serialization: http://dev-answers.blogspot.com/2007/03/how-to-turn-off-tomcat-session.html
 * TODO: Writer threadia ei varmaan tarvitse.
 * TODO: External configurations file tai sitten passaa komentoriviltä parametrit.
 * TODO: Settingsistä pituus ja korkeus (ja prossina vai pikseleinä)
 * TODO: Jos kanavalle tullut uusia viestejä, niin boldaa tabi (ellei ollut valittuna).
 * TODO: Refresh: http://stackoverflow.com/questions/8881566/vaadin-databinding-between-listselect-and-java-util-liststring
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
		IRCSession session = new IRCSession("port80a.se.quakenet.org", 6667, "VaAle23", "VaIRC2", "VaUsr2");
		
		// Start VaadinIRC application.
		VaadinIRC vaadinIRC = new VaadinIRC(main, session);
	}
}