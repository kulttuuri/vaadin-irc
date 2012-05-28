package VaadinIRC;

import irc.IRCSession;
import VaadinIRC.VaadinIRC.VaadinIRC;
import com.vaadin.Application;
import com.vaadin.ui.*;

/*
 * TODO: Status channelin viestit liian lyhyitä (Word wrappaa perus channelin tavoin), ei oo 100% width viestit.
 * TODO: !wordadd komennot (kanavalle jos tulee viesti, niin tarkistaa containsilla oliko wordeissa)
 * TODO: Automaagisesti luomaan tietokannat ja puuttuvat taulut jne.
 * TODO: Turn off serialization: http://dev-answers.blogspot.com/2007/03/how-to-turn-off-tomcat-session.html
 * TODO: External configurations file tai sitten passaa komentoriviltä parametrit.
 * TODO: Settingsistä pituus ja korkeus (ja prossina vai pikseleinä)
 * IRC Numerics: http://www.mirc.net/raws/#top
 * Threadeihin timeout checkkaus (jos yli 300s viime timeoutista, niin tapa threadit)
 * Tarkista clientiltä onko vielä connectannut tietyin väliajoin.
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
		// Create main window
		Window window = new Window(settings.APP_NAME);
		window.setTheme("VaIRCTheme");
		setMainWindow(window);
		
		// Set main window to full size.
		window.setSizeFull();
		window.getContent().setSizeFull();
		window.setStyleName("mainWindow");
		
		// Start VaadinIRC application.
		VaadinIRC vaadinIRC = new VaadinIRC(window, (Application)this);
	}
}