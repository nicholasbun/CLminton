package models;

public class TransactionDetail {
	private String id;
	private String product;
	private Integer price;
	private Integer quantity;
	private Integer totalPrice;
	public TransactionDetail(String id, String product, Integer price, Integer quantity, Integer totalPrice) {
		super();
		this.id = id;
		this.product = product;
		this.price = price;
		this.quantity = quantity;
		this.totalPrice = totalPrice;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Integer getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(Integer totalPrice) {
		this.totalPrice = totalPrice;
	}
	
}
