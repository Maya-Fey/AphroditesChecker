package claire.aphroditeschecker;

import java.awt.AWTException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Main {
	
	public static final Set<String> watching = new HashSet<>();
	
	static {
		watching.add("Climara Forte (Estradiol - 100mcg)");
		watching.add("Oestrogel 80g (Estradiol Beta17)");
		watching.add("Progesterone 200");
		//watching.add("Androcur 50mg (Cyproterone Acetate)");
	}

	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, AWTException {
	    NotificationMaker td = new NotificationMaker();
		while(true) {
			System.out.println("Scanning...");
			List<Product> instock = products();
			Stream<Product> stream = instock.stream().filter((Product s) -> { return watching.contains(s.getName()); });
			List<Product> matching = new ArrayList<>();
			stream.forEach((s) -> { matching.add(s); });
			String total = "";
			for(Product s : matching) {
				total += s.getName() + "\n";
			}
			td.updateMenu(instock);
			if(matching.size() > 0) {
				total = total.substring(0, total.length() - 1);
				td.displayTray(total);
			}
			synchronized(watching) {
				try {
					watching.wait(5 * 60 * 1000);
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
