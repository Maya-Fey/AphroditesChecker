package donut;

public class Product {
	
	private final String name;
	private final int stock;
	private final String URL;
	/**
	 * @param name
	 * @param stock
	 * @param uRL
	 */
	public Product(String name, int stock, String uRL) {
		this.name = name;
		this.stock = stock;
		this.URL = uRL;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	/**
	 * @return the stock
	 */
	public int getStock() {
		return this.stock;
	}
	/**
	 * @return the uRL
	 */
	public String getURL() {
		return this.URL;
	}
	

}
