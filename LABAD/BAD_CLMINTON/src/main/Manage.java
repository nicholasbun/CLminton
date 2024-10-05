package main;

import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import models.Product;
import util.Connect;

public class Manage {
	Scene scene;
	
	BorderPane bp, bp1;
	GridPane gp,gp1;
	HBox hbox;
	VBox vbox;
	
	Label list, productname, productbrand, productprice, name, addstock, delete;
	TextField nameTf, priceTf;
	Button productbtn, stockbtn, deletebtn;
	
	ComboBox<String> combobox;
	Spinner<Integer> spinner;
	
	MenuBar menuBar;
	Menu menus;
	MenuItem itemManage, itemHistory, itemLogout;
	
	TableView<Product> productTable;
	ObservableList<Product> productList;
	Product prod;
	Connect con = Connect.getInstance();
	Login log;
	private void initComponent() {
		bp = new BorderPane();
		bp1 = new BorderPane();
		gp = new GridPane();
		gp1 = new GridPane();
		
		hbox = new HBox();
		vbox = new VBox();
		
		list = new Label("Product List");
		productname = new Label("Product Name");
		productbrand = new Label("Product Brand");
		productprice = new Label("Product Price");
		name = new Label("Name : ");
		addstock = new Label("Add Stock");
		delete = new Label("Delete Product");
		
		nameTf = new TextField();
		priceTf = new TextField();
		
		productbtn = new Button("Add Product");
		stockbtn = new Button("Add Stock");
		deletebtn = new Button("Delete");
		
		combobox = new ComboBox<>();
		spinner = new Spinner<>(1,1000,1);
		
		menuBar = new MenuBar();
		menus = new Menu("Admin");
		itemManage = new MenuItem("Manage Product");
		itemHistory = new MenuItem("View History");
		itemLogout = new MenuItem("Logout");
		
		productList = FXCollections.observableArrayList();
		productTable = new TableView<>();
		scene = new Scene(bp, 1200, 720);
	}
	
	private void initNavBar() {
		menuBar.getMenus().add(menus);
		menus.getItems().addAll(itemManage, itemHistory, itemLogout);
	}
	private void initBp() {
		bp.setTop(menuBar);
		
		hbox.getChildren().addAll(productTable, gp);
		vbox.getChildren().addAll(hbox, gp1);
//		bp1.setTop(list);
		bp1.setCenter(vbox);
		bp1.setTop(list);
		bp.setCenter(bp1);
	}
	private void initGp() {
		gp.add(productname, 0, 0);
		gp.add(nameTf, 0, 1);
		gp.add(productbrand, 0, 2);
		gp.add(combobox, 0, 3);
		gp.add(productprice, 0, 4);
		gp.add(priceTf, 0, 5);
		gp.add(productbtn, 0, 6);
		
		gp1.add(name, 1, 0);
		gp1.add(addstock, 0, 1);
		gp1.add(spinner, 0, 2);
		gp1.add(stockbtn, 0, 3);
		gp1.add(delete, 2, 1);
		gp1.add(deletebtn, 2, 3);
	}
	private void initTable() {
		TableColumn<Product, String> nameColumn = new TableColumn<Product, String>("Name");
		TableColumn<Product, String> brandColumn = new TableColumn<Product, String>("Brand");
		TableColumn<Product, Integer> stockColumn = new TableColumn<Product, Integer>("Stock");
		TableColumn<Product, Integer> priceColumn = new TableColumn<Product, Integer>("Price");
		
		productTable.getColumns().addAll(nameColumn, brandColumn, stockColumn, priceColumn);
		
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		brandColumn.setCellValueFactory(new PropertyValueFactory<>("merk"));
		stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
		priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
		
		productTable.setItems(productList);
		getData();
	}
	private void getData() {
		String query = "SELECT ProductName, ProductMerk, ProductStock, ProductPrice FROM MsProduct;";
		con.execQuery(query);
		try {
			while(con.rs.next()) {
				String name = con.rs.getString("ProductName");
				String merk = con.rs.getString("ProductMerk");
				Integer stock = con.rs.getInt("ProductStock");
				Integer price = con.rs.getInt("ProductPrice");
				productList.add(new Product(stock, price, name, merk));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void refresh() {
		productTable.getItems().clear();
		getData();
	}
	private void setAction() {
		productTable.setOnMouseClicked(e ->{
			prod = productTable.getSelectionModel().getSelectedItem();
			name.setText("Name : " + prod.getName());
		});
		
		deletebtn.setOnMouseClicked( e -> {
			delete();
			name.setText("Name : ");
			refresh();
		});
		stockbtn.setOnMouseClicked(e -> {
			update();
			name.setText("Name : ");
			refresh();
			
		});
		
		productbtn.setOnMouseClicked( e -> {
			add();
			nameTf.clear();
			combobox.getSelectionModel().selectFirst();
			priceTf.clear();
			refresh();
		});
	}
	String id;
	private void getID() {
		String query = String.format("SELECT MAX(ProductID) AS MaxID FROM MsProduct;\r\n");
		con.execQuery(query);
		try {
			while(con.rs.next()) {
			String lastID = con.rs.getString("MaxID");
			Integer getNumber = Integer.parseInt(lastID.substring(2));
			id = String.format("PD%03d", getNumber+1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private void add() {
		getID();
		String brand = combobox.getValue();
		String query = String.format("INSERT INTO MsProduct VALUES ('%s', '%s','%s', %d, %d)", id, nameTf.getText(),brand, Integer.parseInt(priceTf.getText()), 0);
		con.execUpdate(query);
	}
	private void update() {
		String query = String.format("UPDATE MsProduct\n"
				+ "SET ProductStock = ProductStock + %d\n"
				+ "WHERE ProductName = '%s' AND ProductMerk = '%s';", spinner.getValue(), prod.getName(), prod.getMerk());
		con.execUpdate(query);
	}
	private void delete() {
			String query = String.format("DELETE FROM MsProduct WHERE ProductName = '%s' AND ProductMerk = '%s'", prod.getName(), prod.getMerk());
			con.execUpdate(query);
	}
	private void adjust() {
		stockbtn.setMinWidth(100);
		combobox.getItems().addAll("Yonex", "Li-Ning", "Victor");
		combobox.getSelectionModel().selectFirst();
		
		bp1.setPadding(new Insets(100));
		gp.setVgap(10);
		gp1.setVgap(10);
		gp1.setHgap(20);
		list.setPadding(new Insets(0,0,0,250));
		
//		bp.setAlignment(bp1, Pos.CENTER);
//		vbox.setAlignment(Pos.CENTER);
//		bp1.setAlignment(vbox, Pos.CENTER)
//		bp1.setMargin(list, new Insets(0,0,0,0));
		vbox.setMargin(hbox, new Insets(10));
		hbox.setMargin(gp, new Insets(20));
//		vbox.setAlignment(Pos.CENTER);
		gp1.setAlignment(Pos.CENTER);
		hbox.setAlignment(Pos.CENTER); 
		list.setFont(new Font(20));
		name.setFont(new Font(15));
		name.setPadding(new Insets(0,0,0,-170));
//		bp1.setAlignment(list, Pos.CENTER);
//		list.setPadding(new Insets(0,0,0,100));
		
	}

	public void start(Stage primaryStage) {
		initComponent();
		initNavBar();
		initTable();
		initBp();
		initGp();
		adjust();
		setAction();
		
		primaryStage.setScene(scene);
	}
}
