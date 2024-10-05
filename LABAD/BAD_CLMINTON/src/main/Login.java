package main;
import java.sql.SQLException;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import util.Connect;


public class Login {
	BorderPane bp;
	GridPane gp;
	FlowPane fp;
	
	Label login, username,password;
	TextField usernametf;
	PasswordField passwordtf;
	Button loginbtn;
	
	MenuBar menuBar;
	Menu menus;
	Main main;
	MenuItem itemLogin;
	MenuItem itemRegis;
	
	Scene scene;
	Connect con  = Connect.getInstance();
	Home home;
	Manage manage;
	CartUser cart;
	History hist;
	Register regist;
	Card card;
	private void initComponent() {
		bp = new BorderPane();
		gp = new GridPane(); 
		fp = new FlowPane();
		
		login = new Label("Login");
		username = new Label("Email");
		password = new Label("Password");
		
		usernametf= new TextField();
		passwordtf= new PasswordField();
		loginbtn= new Button("Login");
		
		menuBar= new MenuBar();
		menus= new Menu("Page");
		itemLogin= new MenuItem("Login");
		itemRegis= new MenuItem("Register");
		
		scene = new Scene(bp, 1200, 720);
		
	}
	
	private void initNavBar() {
		menuBar.getMenus().add(menus);
		menus.getItems().addAll(itemLogin,itemRegis);
	}
	private void initBp() {
		bp.setCenter(gp);
		bp.setTop(menuBar);
	}

	private void initGp() {
		gp.add(username, 1, 0);
		gp.add(usernametf, 1, 1);
		gp.add(password, 1, 2);
		gp.add(passwordtf, 1, 3);
		gp.add(loginbtn,1,4);
	}

	private void adjust() {
		gp.setAlignment(Pos.CENTER);
		gp.setVgap(10);
		loginbtn.setMaxWidth(100);
		usernametf.setPrefWidth(200);
		passwordtf.setPrefWidth(200);
	}
	String ID;
	private void action(Stage primaryStage) {
		loginbtn.setOnMouseClicked(e -> {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setHeaderText("Warning");
			
			if (usernametf.getText().isEmpty()|| passwordtf.getText().isEmpty()) {
				alert.setContentText("Email or password must be filled!");
				alert.show();
				return;
			}
			
			String email = usernametf.getText();
			String pass = passwordtf.getText();
			String query = String.format("SELECT UserEmail, UserPassword FROM MsUser");
			con.execQuery(query);
			boolean found  = false;
			try {
				while (con.rs.next()) {
					String getEmail = con.rs.getString("UserEmail");
					String getPass = con.rs.getString("UserPassword");
					if (email.equals(getEmail) && pass.equals(getPass)) {
						found = true;
						break;
					} 
				} if (!found) {
					alert.setContentText("Wrong email or password!");
					alert.show();
				}
			
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			String role = String.format("SELECT UserID, UserRole "
					+ "FROM MsUser WHERE UserEmail = '%s' and UserPassword = '%s'", email, pass);
			con.execQuery(role);
			try {
				while (con.rs.next()) {
					hist = main.historyScene;
					hist.log = this;
					cart = main.cartScene;
					cart.log = this;
//					card.log = this;
//					card = main.cardScene;
					ID = con.rs.getString("UserID");
					String getRole = con.rs.getString("UserRole");
					if (getRole.equalsIgnoreCase("User")) {
						home = main.homeScene;
						home.login = this;
						primaryStage.setScene(home.scene);
						primaryStage.setTitle("Home");
						home.refreshProduct();
						primaryStage.show();
					} else {
						manage = main.manageScene;
						manage.log = this;
							primaryStage.setScene(manage.scene);
							primaryStage.setTitle("Manage Product");
							manage.refresh();
							primaryStage.show();
					}
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});
	}
	
	public void Start(Stage primaryStage) {
		initComponent();
		initNavBar();
		initGp();
		initBp();
		adjust();
		action(primaryStage);
		primaryStage.setTitle("Login");
		primaryStage.setScene(scene);
	}
	

}
