package models;

public class Product {
	private Integer stock,price;
	private String name,merk;
	
	public Product(Integer stock, Integer price, String name, String merk) {
		super();
		this.stock = stock;
		this.price = price;
		this.name = name;
		this.merk = merk;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMerk() {
		return merk;
	}

	public void setMerk(String merk) {
		this.merk = merk;
	}
	
	
}
