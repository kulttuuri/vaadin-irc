package VaadinIRC;

import VaadinIRC.VaadinIRC.VaadinIRC;
import com.vaadin.Application;
import com.vaadin.ui.*;

/*
 * TODO: close() is missing from JDBC calls (it is automatically now closed after certain times, not best practice though)
 * TODO: definelle, addgroupille ja vastaavalle p‰‰luokka mik‰ yliajetaan ja toimii suoraan niiss‰ se toiminnallisuudet ja tulee komentoihin automaagisesti.
 * TODO: Timestampit asetukseen ett‰ n‰ytt‰‰kˆ chat viestien kanssa.
 * TODO: handlaamaan irkin boldaus ja n‰‰ formatoinnit.
 * TODO: Skandit.
 * TODO: Mode oppauksia ja muita VOI olla enemm‰n kuin 3. Nyt ei tajua kuin 3.
 * TODO: Status channelin viestit liian lyhyit‰ (Word wrappaa perus channelin tavoin), ei oo 100% width viestit.
 * TODO: Turn off serialization: http://dev-answers.blogspot.com/2007/03/how-to-turn-off-tomcat-session.html
 * TODO: External configurations file tai sitten passaa komentorivilt‰ parametrit.
 * TODO: Settingsist‰ pituus ja korkeus (ja prossina vai pikselein‰)
 * IRC Numerics: http://www.mirc.net/raws/#top
 * Threadeihin timeout checkkaus (jos yli 300s viime timeoutista, niin tapa threadit)
 * Tarkista clientilt‰ onko viel‰ connectannut tietyin v‰liajoin.
 * TODO: Tarkistamaan ett‰ k‰ytt‰j‰ on OP kun koittaa suorittaa komentoa.
 * TODO: Uusia ominaisuuksia:
 * 		- RSS readeri uutisille, automaaginen, voidaan enabloida / disabloida laittamalla !enablerss tai !disablerss
 * 		- !randomsentence ?#channel hakemaan random lause kanavalta.
 * 		- !randomlink ?#channel hakemaan random linkki kanavalta.
 * 		- votet kuntoon.
 * 		- message nickname viesti (vastaanottaja saa viestin kun connectaa tai kirjoittaa jotain kanavalle)
 * 		- definesearch (etsii defineist‰)
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