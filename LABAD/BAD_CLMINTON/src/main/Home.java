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
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import models.Product;
import util.Connect;

public class Home {
	BorderPane bp, bp1;
	GridPane gp;
	FlowPane fp;
	
	Label productlist,productname,productbrand,productprice,totalprice;
	
	Spinner<Integer>quantity;
	
	TableView<Product> listprod;
	TableColumn<Product, String> ProductName,ProductMerk;
	TableColumn<Product, Integer> ProductStock,ProductPrice;
	
	Button cartbtn;
	
	MenuBar menuBar;
	Menu menus;
	MenuItem itemHome;
	MenuItem itemCart;
	MenuItem itemHistory;
	MenuItem itemLogout;
	
	Scene scene;
	
	ObservableList<Product> prod;
	Connect con = Connect.getInstance();
	
	Product selected;
	Login login;
	private void initComponent() {
		bp= new BorderPane();
		bp1= new BorderPane();
		gp= new GridPane();
		fp= new FlowPane();
		
		
		productlist= new Label("Product List ");
		productname= new Label("Product Name\t: ");
		productbrand= new Label("Product Brand\t: ");
		productprice= new Label("Price\t\t\t: ");
		totalprice= new Label("Total Price\t: ");
		
		quantity = new Spinner<>(1,1,1);
		
		cartbtn=new Button("Add To Cart");
		
		menuBar = new MenuBar();
		menus = new Menu("Page");
		itemHome = new MenuItem("Home");
		itemCart = new MenuItem("Cart");
		itemHistory = new MenuItem("History");
		itemLogout = new MenuItem("Logout");
			
		listprod= new TableView<>();
		prod = FXCollections.observableArrayList();
		
		scene = new Scene(bp, 1200, 720);
	}
	private void initNavBar() {
		menuBar.getMenus().add(menus);
		menus.getItems().addAll(itemHome, itemCart,itemHistory, itemLogout); 
	}
	private void initTable() {
		TableColumn<Product, String> ProductName= new TableColumn<Product, String>("Name");
		TableColumn<Product, String> ProductMerk= new TableColumn<Product, String>("Brand");
		TableColumn<Product, Integer> ProductStock= new TableColumn<Product, Integer>("Stock");
		TableColumn<Product, Integer> ProductPrice= new TableColumn<Product, Integer>("Price");
		
		listprod.getColumns().addAll(ProductName,ProductMerk,ProductStock,ProductPrice);
		
		ProductName.setCellValueFactory(new PropertyValueFactory<>("name"));
		ProductMerk.setCellValueFactory(new PropertyValueFactory<>("merk"));
		ProductStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
		ProductPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
		
		listprod.setItems(prod);
		getData();
	}
	private void getData() {
		String query = "SELECT ProductName, ProductMerk, ProductStock, ProductPrice FROM MsProduct WHERE ProductStock > 0;";
		con.execQuery(query);
		try {
			while(con.rs.next()) {
				String name = con.rs.getString("ProductName");
				String merk = con.rs.getString("ProductMerk");
				Integer stock = con.rs.getInt("ProductStock");
				Integer price = con.rs.getInt("ProductPrice");
				prod.add(new Product(stock, price, name, merk));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void selected() {
		listprod.setOnMouseClicked(e -> {
			selected = listprod.getSelectionModel().getSelectedItem();
			productname.setText("Product Name\t: "+selected.getName());
			productbrand.setText("Product Brand\t: "+selected.getMerk());
			productprice.setText("Price\t\t\t: "+String.valueOf(selected.getPrice()));
			totalprice.setText("Total Price\t: "+selected.getPrice());
			
			SpinnerValueFactory<Integer> qty = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, selected.getStock(), 1);
			quantity.setValueFactory(qty);
				quantity.setOnMouseClicked(e1 ->{
					int total = selected.getPrice()*quantity.getValue();
					totalprice.setText("Total Price\t: "+total);
				});
		});
	}
	
	private void addData() {
		String update = String.format("UPDATE carttable \r\n"
				+ "SET Quantity = Quantity + %d \r\n"
				+ "WHERE ProductID IN (\r\n"
				+ "    SELECT ProductID \r\n"
				+ "    FROM MsProduct \r\n"
				+ "    WHERE ProductName = '%s' AND ProductMerk = '%s' AND UserID = '%s'\r\n"
				+ ");", quantity.getValue(), selected.getName(), selected.getMerk(), login.ID);
		String insert = String.format("INSERT INTO carttable (userID, productID, Quantity)\r\n"
				+ "SELECT '%s', ProductID, %d\r\n"
				+ "FROM MsProduct\r\n"
				+ "WHERE ProductName = '%s' AND ProductMerk = '%s';", login.ID, quantity.getValue(), selected.getName(), selected.getMerk());
		
		if (checkData()) {
			con.execUpdate(insert);
		} else {
			con.execUpdate(update);
		}
//		System.out.println(update);
//		System.out.println(insert);
		refresh();
	}
	private boolean checkData() {
		String query = String.format("SELECT ProductName, ProductMerk "
				+ "FROM CartTable c JOIN MsProduct m "
				+ "ON c.ProductID = m.ProductID"
				+ " WHERE UserID = '%s'", login.ID);
		con.execQuery(query);
//		System.out.println(query);
		try {
			while(con.rs.next()) {
				String name = con.rs.getString("ProductName");
				String merk = con.rs.getString("ProductMerk");
				if (name.equals(selected.getName()) && merk.equals(selected.getMerk())) {
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	private void refresh() {
		String query = String.format("UPDATE MsProduct\r\n"
				+ "SET ProductStock = ProductStock - %d\r\n"
				+ "WHERE ProductName = '%s' AND ProductMerk = '%s';", quantity.getValue(), selected.getName(), selected.getMerk());
		con.execUpdate(query);
		productname.setText("Product Name\t: ");
		productbrand.setText("Product Brand\t: ");
		productprice.setText("Price\t\t\t: ");
		totalprice.setText("Total Price\t: ");
		quantity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1, 1));
		
		refreshProduct();
	}
	public void refreshProduct() {
		listprod.getItems().clear();
		getData();
	}
	private void initGp() {
		gp.add(productname, 0,0);
		gp.add(productbrand, 0,1);
		gp.add(productprice, 0,2);
		gp.add(quantity, 0, 3);
		gp.add(totalprice, 0,4);
		gp.add(cartbtn, 0, 5);
	}
	private void initBp() {
		bp1.setLeft(listprod);
		bp1.setTop(productlist);
		bp1.setCenter(gp);

		bp.setCenter(bp1);
		bp.setTop(menuBar);
	}
	private void adjust() {
		gp.setHgap(10);
	    gp.setVgap(10);
		bp1.setPadding(new Insets(100));
		productlist.setAlignment(Pos.CENTER);
		bp1.setMargin(listprod, new Insets(0,20,100,200));
		productlist.setFont(new Font(20));
		productlist.setPadding(new Insets(0,0,10,200));
	}	
	private Boolean validate() {
		Alert warning = new Alert(AlertType.WARNING);
		warning.setHeaderText("Warning");
		warning.setContentText("Please choose 1 item!");
		if (selected == null) {
			warning.show();
			return false;
		}
		return true;
	}
	
	private void action() {
		cartbtn.setOnAction(e->{
			if (validate()) {
				addData();
			}
		});
	}

	public void start(Stage primaryStage) {
		initComponent();
		initNavBar();
		initTable();
		initGp();
		initBp();
		adjust();
		selected();
		action();
		primaryStage.setScene(scene);

	}

}
