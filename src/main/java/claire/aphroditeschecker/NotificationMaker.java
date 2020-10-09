package claire.aphroditeschecker;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

public class NotificationMaker implements ActionListener, ItemListener {
	
	private final Map<String, MenuItem> menu = new HashMap<>();
	private final Map<MenuItem, String> notifyMenuMap = new HashMap<>();
	private final Map<String, Boolean> config = new HashMap<>();
	
	private final PopupMenu popup = new PopupMenu();
	
	private final TrayIcon trayIcon;
	
	private final MenuItem exit = new MenuItem("Exit");
	
	private boolean initialized = false;
	
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
		if(this.initialized) {
			for(Product p : items) {
				MenuItem item = this.menu.get(p.getName());
				item.setLabel(String.format("%3d %s", p.getStock(), p.getName()));
				if(p.getStock() == 0)
					item.setEnabled(false);
			}
		} else {
			this.initialize(items);
		}
	}
	
	private void initialize(List<Product> items)
	{
		this.popup.removeAll();
		this.popup.add("Last scan: " + new Date());
		this.popup.addSeparator();
		for(Product p : items) {
			MenuItem item = new MenuItem(String.format("%3d %s", p.getStock(), p.getName()));
			if(p.getStock() == 0)
				item.setEnabled(false);
			item.addActionListener((e) -> { openWebpage(p.getURL()); });
			this.popup.add(item);
			this.menu.put(p.getName(), item);
		}
		this.popup.addSeparator();
		
		Menu notifyMenu = new Menu("Notifications");
		for(Product p : items) {
			CheckboxMenuItem check = new CheckboxMenuItem(p.getName());
			notifyMenu.add(check);
			this.notifyMenuMap.put(check, p.getName());
			check.addActionListener(this);
			check.addItemListener(this);
		}
		
		this.popup.add(notifyMenu);
		this.popup.add(this.exit);
		
		this.initialized = true;
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
	
	public boolean notificationsOnFor(String name)
	{
		return this.config.containsKey(name) && this.config.get(name);
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource() == this.exit) {
			System.exit(0);
		} 
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		boolean state = e.getStateChange() == ItemEvent.SELECTED;
		String name = this.notifyMenuMap.get(e.getSource());
		this.config.put(name, state);
	}

}
