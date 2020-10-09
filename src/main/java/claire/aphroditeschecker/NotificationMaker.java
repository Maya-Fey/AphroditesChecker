package claire.aphroditeschecker;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

public class NotificationMaker implements ActionListener {
	
	private final PopupMenu popup = new PopupMenu();
	
	private final TrayIcon trayIcon;
	
	private final MenuItem exit = new MenuItem("Exit");
	
	/**
	 * @param available
	 * @throws AWTException 
	 * @throws IOException 
	 */
	public NotificationMaker() throws AWTException, IOException {
		//Obtain only one instance of the SystemTray object
        SystemTray tray = SystemTray.getSystemTray();

        //If the icon is a file
        Image image = ImageIO.read(this.getClass().getResourceAsStream("resources/icon.png"));

        this.trayIcon = new TrayIcon(image, "Tray Demo");
        //Let the system resize the image if needed
        this.trayIcon.setImageAutoSize(true);
        this.trayIcon.setPopupMenu(this.popup);
        //Set tooltip text for the tray icon
        this.trayIcon.setToolTip("Aphrodites Scanner");
        tray.add(this.trayIcon);
        
        this.exit.addActionListener(this);
	}
	
	public void updateMenu(List<Product> items) {
		this.popup.removeAll();
		this.popup.add("Last scan: " + new Date());
		this.popup.addSeparator();
		for(Product p : items) {
			MenuItem item = new MenuItem(String.format("%3d %s", p.getStock(), p.getName()));
			if(p.getStock() == 0)
				item.setEnabled(false);
			item.addActionListener((e) -> { openWebpage(p.getURL()); });
			this.popup.add(item);
		}
		this.popup.addSeparator();
		this.popup.add(this.exit);
		
	}

	public void displayTray(String available) throws AWTException {
		this.trayIcon.displayMessage("Hormones Available", available, MessageType.INFO);
    }
	
	public void displayError(String error) throws AWTException {
		this.trayIcon.displayMessage("Error", error, MessageType.ERROR);
    }
	
	public static void openWebpage(String urlString) {
	    try {
	        Desktop.getDesktop().browse(new URL(urlString).toURI());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource() == this.exit) {
			System.exit(0);
		}
	}



}
