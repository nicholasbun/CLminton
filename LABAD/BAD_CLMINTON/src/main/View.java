package main;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Transaction;
import models.TransactionDetail;
import util.Connect;

public class View extends Application{
	Scene scene;
	
	BorderPane bp, bp1;
	VBox vbox, vbox1;
	HBox hbox;
	ScrollPane sp;
	
	Label all, detail, total;
	
	MenuBar menuBar;
	Menu menus;
	MenuItem itemManage, itemHistory, itemLogout;
	
	TableView<Transaction> transTable;
	TableView<TransactionDetail> detailTable;
	ObservableList<Transaction> transList;
	ObservableList<TransactionDetail> detailList;
	
	
	Connect con = Connect.getInstance();
	
	private void initComponent() {
		bp = new BorderPane();
		bp1 = new BorderPane();
		vbox = new VBox();
		vbox1 = new VBox();
		hbox = new HBox();
		sp = new ScrollPane();
		
		all = new Label("All Transaction");
		detail = new Label("Transaction Detail");
		total = new Label("Total Price : ");
		
		menuBar = new MenuBar();
		menus = new Menu("Admin");
		itemManage = new MenuItem("Manage Product");
		itemHistory = new MenuItem("View History");
		itemLogout = new MenuItem("Logout");
		
		transTable = new TableView<>();
		detailTable = new TableView<>();
		transList = FXCollections.observableArrayList();
		detailList = FXCollections.observableArrayList();
		
		scene = new Scene(bp, 1200, 720);
	}
	private void initTable() {
		TableColumn<Transaction, String> idcolumn = new TableColumn<Transaction, String>("ID");
		TableColumn<Transaction, String> emailcolumn = new TableColumn<Transaction, String>("Email");
		TableColumn<Transaction, String> datecolumn = new TableColumn<Transaction, String>("Date");
        
		idcolumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		emailcolumn.setCellValueFactory(new PropertyValueFactory<>("email"));
		datecolumn.setCellValueFactory(new PropertyValueFactory<>("date")); 
		
		transTable.getColumns().addAll(idcolumn, emailcolumn, datecolumn);
		transTable.setItems(transList);
		
		TableColumn<TransactionDetail, String> id = new TableColumn<TransactionDetail, String>("ID");
		TableColumn<TransactionDetail, String> product = new TableColumn<TransactionDetail, String>("Product");
		TableColumn<TransactionDetail, Integer> price = new TableColumn<TransactionDetail, Integer>("Price");
		TableColumn<TransactionDetail, Integer> quantity = new TableColumn<TransactionDetail, Integer>("Quantity");
		TableColumn<TransactionDetail, Integer> totalPrice = new TableColumn<TransactionDetail, Integer>("Total Price");
		
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
		menus.getItems().addAll(itemManage, itemHistory, itemLogout);

	}
	private void initSp() {
		sp.setContent(transTable);
		sp.setHbarPolicy(ScrollBarPolicy.ALWAYS);
		sp.setVbarPolicy(ScrollBarPolicy.ALWAYS);
	}
	private void initBp() {
		bp.setTop(menuBar);
		bp.setCenter(bp1);
		vbox.getChildren().addAll(all, sp);
		vbox1.getChildren().addAll(detail, detailTable, total);
		
		hbox.getChildren().addAll(vbox, vbox1);
		
		bp1.setCenter(hbox);

	}
	private void adjust() {
		hbox.setAlignment(Pos.CENTER);
		bp1.setPadding(new Insets(50));
		vbox.setPadding(new Insets(10));
		vbox1.setPadding(new Insets(10));
		vbox.setSpacing(10);
		vbox1.setSpacing(10);
		
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
	public void refreshView() {
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
				+ "    MsUser mu ON th.UserID = mu.UserID\r\n");	
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
	@Override
	public void start(Stage primaryStage) throws Exception {
		initComponent();
		initTable();
		initNavBar();
		initSp();
		initBp();
		adjust();
		action();
		
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}
	public static void main(String[] args) {
		launch(args);
	}

}
