package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import models.Cart;
import util.Connect;


public class CartUser {
	Scene scene;
	
	BorderPane bp,bp1;
	GridPane gp;
	VBox vbox;
	HBox hbox;
	Label cartlist,totalprice,productname,productbrand,productprice;
	Button deletefromcart, checkout;
	
	ObservableList<Cart> cartList;
	
	TableView<Cart> cartTable;
	
	MenuBar menuBar;
	Menu menus;
	MenuItem itemHome;
	MenuItem itemCart;
	MenuItem itemHistory;
	MenuItem itemLogout;
	
	Cart selected;
	Card card;
	
	Connect con = Connect.getInstance();
	Login log;
	public void initComponent() {
		bp1= new BorderPane();
		bp = new BorderPane();
		gp = new GridPane();
		vbox = new VBox();
		
		cartlist = new Label("Your Cart List");
		productname = new Label("Name\t\t: ");
		productbrand = new Label("Brand\t\t:");
		productprice = new Label("Price\t\t\t: ");
		totalprice = new Label("Total Price\t: ");
		
		deletefromcart = new Button("Delete Product");
		checkout = new Button("Checkout");
		
		cartTable = new TableView<>();
		
		menuBar = new MenuBar();
		menus = new Menu("Page");
		itemHome = new MenuItem("Home");
		itemCart = new MenuItem("Cart");
		itemHistory = new MenuItem("History");
		itemLogout = new MenuItem("Logout");
		hbox = new HBox();
		scene = new Scene(bp, 1200, 720);
		
		cartList = FXCollections.observableArrayList();
	}
	
	private void initNavBar() {
		menuBar.getMenus().add(menus);
		menus.getItems().addAll(itemHome, itemCart,itemHistory, itemLogout); 
	}
	private void initTable() {
		TableColumn<Cart, String> cartName= new TableColumn<Cart, String>("Name");
		TableColumn<Cart, String> cartBrand= new TableColumn<Cart, String>("Brand");
		TableColumn<Cart, Integer> cartPrice= new TableColumn<Cart, Integer>("Price");
		TableColumn<Cart, Integer> cartQuantity= new TableColumn<Cart, Integer>("Quantity");
		TableColumn<Cart, Integer> cartTotal= new TableColumn<Cart, Integer>("Total");
		
		cartTable.getColumns().addAll(cartName, cartBrand, cartPrice, cartQuantity, cartTotal);
		
		cartName.setCellValueFactory(new PropertyValueFactory<>("name"));
		cartBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
		cartPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
		cartQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		cartTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
		
		cartTable.setItems(cartList);
	}
	private void getData() {
		String query = String.format("SELECT\r\n"
				+ "    m.ProductName,\r\n"
				+ "    m.ProductMerk,\r\n"
				+ "    m.ProductPrice,\r\n"
				+ "    c.Quantity,\r\n"
				+ "    SUM(m.ProductPrice * c.Quantity) as Total\r\n"
				+ "FROM\r\n"
				+ "    CartTable c\r\n"
				+ "JOIN\r\n"
				+ "    MsProduct m ON c.ProductID = m.ProductID\r\n"
				+ "WHERE UserID = '%s'"
				+ "GROUP BY\r\n"
				+ "    m.ProductName, m.ProductMerk, m.ProductPrice;\r\n", log.ID);
		con.execQuery(query);
		try {
			while(con.rs.next()) {
				String nameCart = con.rs.getString("ProductName");
				String brandCart = con.rs.getString("ProductMerk");
				int priceCart = con.rs.getInt("ProductPrice");
				int quantityCart = con.rs.getInt("Quantity");
				int totalCart = con.rs.getInt("Total");
				cartList.add(new Cart(nameCart, brandCart, priceCart,quantityCart,totalCart));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void initGp() {
		gp.add(productname, 0, 0);
		gp.add(productbrand, 0, 1);
		gp.add(productprice, 0, 2);
		gp.add(totalprice, 0, 3);
	}
	private void initBp() {
		hbox.getChildren().addAll(cartTable, gp);
		vbox.getChildren().addAll(checkout, deletefromcart);
		bp1.setTop(cartlist);
		bp1.setCenter(hbox);
		bp1.setBottom(vbox);
		
		bp.setTop(menuBar);
		bp.setCenter(bp1);
	}
	private void adjust() {
		gp.setHgap(10);
	    gp.setVgap(10);

	    checkout.setPrefWidth(500);
	    deletefromcart.setPrefWidth(500);
	    
	    bp1.setAlignment(cartTable, Pos.CENTER);
	    cartTable.setMinWidth(400);
	    hbox.setMargin(cartTable, new Insets(0,10,10,200));
		bp1.setMargin(cartlist, new Insets(0,10,10,200));
		bp1.setPadding(new Insets(100));
		cartlist.setFont(new Font(20));
		vbox.setAlignment(Pos.CENTER);
		vbox.setMargin(deletefromcart, new Insets(10,0,0,0));
	}
	private void selected() {
		cartTable.setOnMouseClicked(e -> {
			selected = cartTable.getSelectionModel().getSelectedItem();
			productname.setText("Name\t\t: "+selected.getName());
			productbrand.setText("Brand\t\t: "+selected.getBrand());
			productprice.setText("Price\t\t\t: "+String.valueOf(selected.getPrice()));
			totalprice.setText("Total Price\t: "+String.valueOf(total));
		});
		 
		deletefromcart.setOnMouseClicked( e->{
			if (validate()) {
				delete();
				updateStock();
				refresh();
			}
		});
		
	}
	private void toWindow(Stage primaryStage) {
		checkout.setOnMouseClicked(e->{
			if (checkout()) {
				card.refresh();
				primaryStage.setScene(card.scene);
				primaryStage.setTitle("Transaction Card");
				primaryStage.show();
			}
		});
	}
	
	private boolean validate() {
		Alert warning = new Alert(AlertType.WARNING);
		warning.setHeaderText("Warning");
		warning.setContentText("Please select product to delete");
		if (selected == null) {
			warning.show();
			return false;
		}
		return true;
	}
	private boolean checkout() {
		Alert warning = new Alert(AlertType.WARNING);
		warning.setHeaderText("Warning");
		warning.setContentText("Please insert item to your cart");
		if (cartTable.getItems().isEmpty()) {
			warning.show();
			return false;
		}
		return true;
	}
	public void refresh() {
		total();
		cartTable.getItems().clear();
		getData();
		
		productname.setText("Name\t\t: ");
		productbrand.setText("Brand\t\t: ");
		productprice.setText("Price\t\t\t: ");
		totalprice.setText("Total Price\t: "+String.valueOf(total));
	}
	int total;
	private void total() {
		String query = String.format("SELECT SUM(Total) as totalprice\r\n"
				+ "FROM (\r\n"
				+ "    SELECT\r\n"
				+ "        ProductName,\r\n"
				+ "        ProductMerk,\r\n"
				+ "        ProductPrice,\r\n"
				+ "        SUM(Quantity) as TotalQuantity,\r\n"
				+ "        SUM(ProductPrice * Quantity) as Total\r\n"
				+ "    FROM\r\n"
				+ "        CartTable c\r\n"
				+ "    JOIN\r\n"
				+ "        MsProduct m ON c.ProductID = m.ProductID\r\n"
				+ "    WHERE UserId = '%s'\r\n"
				+ "    GROUP BY\r\n"
				+ "        ProductName, ProductMerk, ProductPrice\r\n"
				+ ") AS sub;\r\n"
				+ "", log.ID);
		con.execQuery(query);
		try {
			while(con.rs.next()) {
			total = con.rs.getInt("totalprice");
			totalprice.setText("Total Price\t: "+String.valueOf(total));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	private void delete() {
		 String query = String.format("DELETE c\r\n"
		 		+ "FROM CartTable c\r\n"
		 		+ "JOIN MsProduct m ON c.ProductID = m.ProductID\r\n"
		 		+ "WHERE m.ProductName = '%s' AND m.ProductMerk = '%s' AND c.Quantity = %d;",
		            selected.getName(), selected.getBrand(), selected.getQuantity());
		    con.execUpdate(query);
	}
	private void updateStock() {
		String query = String.format("UPDATE MsProduct\n"
				+ "SET ProductStock = ProductStock + %d;", selected.getQuantity());
		con.execUpdate(query);
	}
	public void start(Stage primaryStage) {
		initComponent();
		initNavBar();
		initTable();
		initGp();
		initBp();
		adjust();
		selected();
		toWindow(primaryStage);
		primaryStage.setScene(scene);
	}
}