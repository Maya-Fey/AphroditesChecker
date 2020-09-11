package donut;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.net.URL;
import java.util.Date;
import java.util.List;

public class NotificationMaker {
	
	private final PopupMenu popup = new PopupMenu();
	private final TrayIcon trayIcon;
	
	/**
	 * @param available
	 * @throws AWTException 
	 */
	public NotificationMaker() throws AWTException {
		//Obtain only one instance of the SystemTray object
        SystemTray tray = SystemTray.getSystemTray();

        //If the icon is a file
        Image image = Toolkit.getDefaultToolkit().createImage("answer.png");

        trayIcon = new TrayIcon(image, "Tray Demo");
        //Let the system resize the image if needed
        trayIcon.setImageAutoSize(true);
        trayIcon.setPopupMenu(popup);
        //Set tooltip text for the tray icon
        trayIcon.setToolTip("Aphrodites Scanner");
        tray.add(trayIcon);
	}
	
	public void updateMenu(List<Product> items) {
		popup.removeAll();
		popup.add("Last scan: " + new Date());
		popup.addSeparator();
		for(Product p : items) {
			MenuItem item = new MenuItem(String.format("%3d %s", p.getStock(), p.getName()));
			item.addActionListener((e) -> { openWebpage(p.getURL()); });
			popup.add(item);
		}
	}

	public void displayTray(String available) throws AWTException {
        trayIcon.displayMessage("Hormones Available", available, MessageType.INFO);
    }
	
	public static void openWebpage(String urlString) {
	    try {
	        Desktop.getDesktop().browse(new URL(urlString).toURI());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}



}
