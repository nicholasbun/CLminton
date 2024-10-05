package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import models.Transaction;
import models.TransactionDetail;
import util.Connect;

public class History {
	Scene scene;
	BorderPane bp, bp1;
	GridPane gp,gp1;
	HBox hbox;
	
	Label myTrans, transDetail, total;
	
	TableView<Transaction> transTable;
	TableView<TransactionDetail> detailTable;
	
	ObservableList<Transaction> transList;
	ObservableList<TransactionDetail> detailList;
	
	MenuBar menuBar;
	Menu menus;
	MenuItem itemHome;
	MenuItem itemCart;
	MenuItem itemHistory;
	MenuItem itemLogout;
	
	Connect con = Connect.getInstance();
	
	Login log;
	private void initComponent() {
		bp = new BorderPane();
		gp = new GridPane();
		bp1 = new BorderPane();
		gp1 = new GridPane();
		hbox = new HBox();
		
		myTrans = new Label("My Transaction");
		transDetail = new Label("Transaction Detail");
		total = new Label("Total Price :");
		
		menuBar = new MenuBar();
		menus = new Menu("Page");
		itemHome = new MenuItem("Home");
		itemCart = new MenuItem("Cart");
		itemHistory = new MenuItem("History");
		itemLogout = new MenuItem("Logout");
		
		transTable = new TableView<>();
		detailTable = new TableView<>();
		
		transList = FXCollections.observableArrayList();
		detailList = FXCollections.observableArrayList();
		
		scene = new Scene(bp, 1200, 720);
	}
	
	private void initTable() {
		TableColumn<Transaction, String> idColumn = new TableColumn<Transaction, String>("ID"); 
		TableColumn<Transaction, String> emailColumn = new TableColumn<Transaction, String>("Email"); 
		TableColumn<Transaction, String> dateColumn = new TableColumn<Transaction, String>("Date"); 
		
		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
		dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
		
		transTable.getColumns().addAll(idColumn, emailColumn, dateColumn);
		transTable.setItems(transList);
//		getDataHeader();
		
		TableColumn<TransactionDetail, String> id = new TableColumn<TransactionDetail, String>("ID");
		TableColumn<TransactionDetail, String> product = new TableColumn<TransactionDetail, String>("Product");
		TableColumn<TransactionDetail, Integer> price = new TableColumn<TransactionDetail, Integer>("Price");
		TableColumn<TransactionDetail, Integer> quantity = new TableColumn<TransactionDetail, Integer>("Quantity");
		TableColumn<TransactionDetail, Integer> totalPrice = new TableColumn<TransactionDetail, Integer>("TotalPrice");
		
		id.setCellValueFactory(new PropertyValueFactory<>("id"));
		product.setCellValueFactory(new PropertyValueFactory<>("product"));
		price.setCellValueFactory(new PropertyValueFactory<>("price"));
		quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		totalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
		
		detailTable.getColumns().addAll(id, product, price, quantity, totalPrice);
		detailTable.setItems(detailList);
	}
	
	private void initNavBar() {
		menuBar.getMenus().add(menus);
		menus.getItems().addAll(itemHome, itemCart, itemHistory, itemLogout);
	}
	
	private void initBp() {
		
		hbox.getChildren().addAll(gp, gp1);
		
		bp.setTop(menuBar);
		bp1.setCenter(hbox);
		
	
		bp.setCenter(bp1);
//		bp1.setLeft(gp);
//		bp1.setCenter(detailTable);
	}
	private void initGp() {
		gp.add(myTrans, 0, 0);
		gp.add(transTable, 0, 2);
		detailTable.setMinWidth(600);
		detailTable.setMinHeight(500);
		transTable.setMinWidth(400);
		transTable.setMinHeight(500);
		gp1.add(transDetail, 0, 0);
		gp1.add(detailTable, 0, 2);
		gp1.add(total, 0, 3);
	}
	private void adjust() {
		gp.setVgap(10);
		gp1.setVgap(10);
		
		bp1.setAlignment(hbox, Pos.CENTER);
		hbox.setMargin(gp, new Insets(20));
		hbox.setMargin(gp1, new Insets(20));
		bp.setAlignment(bp1, Pos.CENTER);
		
		bp1.setPadding(new Insets(30,0,0,60));
		
		myTrans.setFont(new Font(20));
		transDetail.setFont(new Font(20));
		total.setFont(new Font(15));
	}
	Transaction trans;
	private void action() {
		transTable.setOnMouseClicked(e ->{
			detailTable.getItems().clear();
			trans = transTable.getSelectionModel().getSelectedItem();
			getDataDetail();
			getSum();
			total.setText("Total Price\t: " + String.valueOf(sumPrice));
		});
	}
	private void getDataDetail() {
		String query = String.format("SELECT\r\n"
				+ "    TransactionID,\r\n"
				+ "    ProductName,\r\n"
				+ "    ProductPrice,\r\n"
				+ "    Quantity,\r\n"
				+ "    SUM(ProductPrice * Quantity) as TotalPrice\r\n"
				+ "FROM\r\n"
				+ "    TransactionDetail td\r\n"
				+ "JOIN\r\n"
				+ "    MsProduct mp ON td.ProductID = mp.ProductID\r\n"
				+ "WHERE TransactionID = '%s'"
				+ "GROUP BY\r\n"
				+ "    TransactionID, ProductName, ProductPrice;\r\n", trans.getId());
		con.execQuery(query);
		try {
			while(con.rs.next()) {
				String id = con.rs.getString("TransactionID");
				String name = con.rs.getString("ProductName");
				Integer price = con.rs.getInt("ProductPrice");
				Integer quantity = con.rs.getInt("Quantity");
				Integer total = con.rs.getInt("TotalPrice");
				detailList.add(new TransactionDetail(id, name, price, quantity, total));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	Integer sumPrice;
	private void getSum() {
		String query = String.format("SELECT SUM(TotalPrice) as sumPrice\r\n"
				+ "FROM (\r\n"
				+ "    SELECT\r\n"
				+ "        TransactionID,\r\n"
				+ "        ProductName,\r\n"
				+ "        ProductPrice,\r\n"
				+ "        Quantity,\r\n"
				+ "        SUM(ProductPrice * Quantity) as TotalPrice\r\n"
				+ "    FROM\r\n"
				+ "        TransactionDetail td\r\n"
				+ "    JOIN\r\n"
				+ "        MsProduct mp ON td.ProductID = mp.ProductID\r\n"
				+ "    WHERE\r\n"
				+ "        TransactionID = '%s' \r\n"
				+ "    GROUP BY\r\n"
				+ "        TransactionID, ProductName, ProductPrice\r\n"
				+ ") as SUBQUERY;\r\n"
				+ "", trans.getId());
		
		con.execQuery(query);
		try {
			while(con.rs.next()) {
				sumPrice = con.rs.getInt("sumPrice");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public void refresh() {
		transTable.getItems().clear();
		getDataHeader();
	}
	private void getDataHeader() {
		String query = String.format("SELECT\r\n"
				+ "    TransactionID,\r\n"
				+ "    UserEmail,\r\n"
				+ "    TransactionDate\r\n"
				+ "FROM\r\n"
				+ "    TransactionHeader th\r\n"
				+ "JOIN\r\n"
				+ "    MsUser mu ON th.UserID = mu.UserID\r\n"
				+ "WHERE\r\n"
				+ "    th.UserID = '%s';", log.ID);	
		con.execQuery(query);
		try {
			while(con.rs.next()) {
				String id = con.rs.getString("TransactionID");
				String email = con.rs.getString("UserEmail");
				String date = con.rs.getString("TransactionDate");
				transList.add(new Transaction(id, email, date));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void start(Stage primaryStage) {
		initComponent();
		initTable();
		initNavBar();
		initBp();
		initGp();
		adjust();
		action();
		primaryStage.setScene(scene);
	}
}

