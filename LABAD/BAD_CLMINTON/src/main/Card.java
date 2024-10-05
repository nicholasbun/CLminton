package main;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import jfxtras.scene.control.window.Window;
import util.Connect;

public class Card {
	Label Listlabel,productlist,courierlabel,totalprice;
	
	ComboBox<String>courier;
	
	CheckBox insurance;
	
	Button checkout;
	
	Window Transactionwindow;
	Scene scene;
	Connect con = Connect.getInstance();
	
	CartUser cart;
	VBox vbox;
	BorderPane bp;
	
	Login log;
//	Main main;
	
	private void initcomp() {
		Listlabel = new Label("List");
//		log.card = this;
//		log = main.loginScene;
		productlist = new Label();
		courierlabel = new Label("Courier");
//		totalprice = new Label();
		totalprice = new Label(cart.totalprice.getText());
		
		courier = new ComboBox<>();
		insurance = new CheckBox("Use Insurance");
		
		Transactionwindow = new Window("Transaction Card");
		
		checkout = new Button("Checkout");
		
		bp = new BorderPane();
		vbox = new VBox();
		scene = new Scene(bp, 1200, 720);
	
	}
	private void addComp() {
		courier.getItems().addAll("J&E", "JET", "Gejok","Nanji Express");
		courier.getSelectionModel().selectFirst();
		
		for (int i = 0; i < cart.cartList.size(); i++) {
			productlist.setText(productlist.getText() 
					+cart.cartList.get(i).getName() 
					+ "  :"+cart.cartList.get(i).getTotal() +"\n" );
			}
		
		vbox.getChildren().addAll(Listlabel, productlist, courierlabel, courier, insurance, totalprice, checkout);
		Transactionwindow.getContentPane().getChildren().add(vbox);
		
		bp.setCenter(Transactionwindow);
	}
	
	public void refresh() {
		productlist.setText("");
		for (int i = 0; i < cart.cartList.size(); i++) {
			productlist.setText(productlist.getText() 
					+cart.cartList.get(i).getName() 
					+ " :"+cart.cartList.get(i).getTotal() +"\n" );
			}
		totalprice.setText(cart.totalprice.getText());
	}
	
	private void adjust() {
		Listlabel.setFont(new Font(20));
		
		courier.setMinWidth(200);
		checkout.setMinWidth(300);
		
		vbox.setAlignment(Pos.CENTER);
		
		Transactionwindow.setPadding(new Insets(20,50,20,50));
		vbox.setSpacing(10);
	}
	String id;
	private void getID() {
		String query = String.format("SELECT MAX(TransactionID) AS MaxID FROM transactionheader;\r\n");
		con.execQuery(query);
		try {
			while(con.rs.next()) {
			String lastID = con.rs.getString("MaxID");
			Integer getNumber = Integer.parseInt(lastID.substring(2));
			id = String.format("TH%03d", getNumber+1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private void addHeader() {
		getID();
		Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
		String query = String.format("INSERT INTO transactionheader VALUES ('%s', '%s','%s',%d, '%s')", id, log.ID, date, delivInsurance, courier.getSelectionModel().getSelectedItem());
		con.execUpdate(query);
	}
	private void addDetail() {
		String query = String.format("INSERT INTO TransactionDetail()\r\n"
				+ "SELECT ProductId, '%s', Quantity\r\n"
				+ "FROM CartTable WHERE UserID = '%s'", id, log.ID);
		con.execUpdate(query);
	}
	int delivInsurance;
	private void action(Stage primaryStage) {
		insurance.setOnAction(e -> {
			if (insurance.isSelected()) {
				int getInsurance = cart.total+ 90000;
				delivInsurance = 1;
				totalprice.setText("Total Price\t : " +String.valueOf(getInsurance));
			} else {
				delivInsurance = 0;
				totalprice.setText(cart.totalprice.getText());
			}
		});
		
		Alert confirmation = new Alert(AlertType.CONFIRMATION);
		confirmation.setHeaderText("Confirmation");
		confirmation.setContentText("Are you sure to Checkout all the item?");
		
		checkout.setOnMouseClicked(e ->{
			confirmation.show();
		});
		
		confirmation.setOnCloseRequest(e -> {
			if (confirmation.getResult() == ButtonType.CANCEL) {
				confirmation.close();
			} else if (confirmation.getResult() == ButtonType.OK) {
				addHeader();
				addDetail();
				remove();
				try {
					primaryStage.setTitle("Cart");
					cart.refresh();
					primaryStage.setScene(cart.scene);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	
	private void remove() {
		 String query = String.format("DELETE FROM CartTable WHERE UserID = '%s';", log.ID);
		 con.execUpdate(query);
	}
	public void start(Stage primaryStage) {
		initcomp();
		addComp();
		adjust();
		action(primaryStage);
		
		primaryStage.setScene(scene);
	}
	

}
