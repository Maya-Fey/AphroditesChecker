package claire.aphroditeschecker;

import java.awt.AWTException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Main {

	public static void main(String[] args) throws AWTException, IOException {
	    NotificationMaker td = new NotificationMaker();
	    int parserfail = 0;
		while(true) {
			System.out.println("Scanning...");
			try {
				List<Product> products = products();
				td.updateMenu(products);
				
				StringBuilder builder = new StringBuilder();
				products.stream().filter((Product p) -> { return p.getStock() > 0; }).filter((Product s) -> { return td.notificationsOnFor(s.getName()); }).forEach((p) -> {
					builder.append(p.getName());
					builder.append("\n");
				});
				if(builder.length() > 1) {
					td.displayTray(builder.substring(0, builder.length() - 1));
				}
			} catch(IOException e) {
				System.out.println("Failed to scan this interval");
				e.printStackTrace();
			} catch (ParserConfigurationException | SAXException  e) {
				System.out.println("Failed to parse");
				td.displayError("Parse exception. Network may be unstable or program may be out of date.");
				parserfail += 1;
				e.printStackTrace();
				if(parserfail >= 5)
					return;
			} 
			synchronized(td) {
				try {
					td.wait(5 * 4 * 1000);
				} catch (InterruptedException e) { /* Should never happen, single-threaded application. */ }
			}
		}
	}
	
	/**
	 * @param string A string containing JSON
	 * @return A JSON object constructed from that string
	 */
	public static JsonObject stringToJSON(String string) {
		return Json.createReader(new StringReader(string)).readObject();
	}
	
	public static List<Product> products() throws IOException, ParserConfigurationException, SAXException
	{
		String page1 = poll(1);
		String page2 = poll(2);
		
		JsonObject obj1 = stringToJSON(page1);
		JsonObject obj2 = stringToJSON(page2);
		
		List<JsonObject> list = new ArrayList<>();
		List<Product> products = new ArrayList<>();
		
		obj1.getJsonArray("products").forEach((ele) -> { list.add(ele.asJsonObject()); });
		obj2.getJsonArray("products").forEach((ele) -> { list.add(ele.asJsonObject()); });
		
		for(JsonObject product : list) {
			System.out.println("Name:  " + product.getString("name"));
			System.out.println("Stock: " + product.getString("stock"));
			Product prod = new Product(product.getString("name"), Integer.parseInt(product.getString("stock")), "https://www.aphrodites.shop/product/" + product.getString("ref") + "/" + product.getString("nameurl"));
			products.add(prod);
		}
		
		return products;
	}
	
	public static String poll(int page) throws IOException
	{
		StringBuilder result = new StringBuilder();
		URL url = new URL("https://www.aphrodites.shop/admin/php/ajax.php?request=shop&page=" + page);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
		   result.append(line);
		}
		rd.close();
		return result.toString();
	}

}
