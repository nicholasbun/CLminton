package main;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	Login loginScene;
	Register registerScene;
	Home homeScene;
	CartUser cartScene;
	History historyScene;
	Manage manageScene;
	View viewScene;
	Card cardScene;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		loginScene = new Login();
		registerScene = new Register();
		homeScene = new Home();
		cartScene = new CartUser();
		historyScene = new History();
		manageScene = new Manage();
		viewScene = new View();
		cardScene = new Card();
		
		loginScene.main = this;
		registerScene.main = this;
		cardScene.log = loginScene;
		cardScene.cart = cartScene;
		cartScene.card = cardScene;

		
		viewScene.start(primaryStage);
		manageScene.start(primaryStage);
		historyScene.start(primaryStage);
		cartScene.start(primaryStage);
		homeScene.start(primaryStage);
		registerScene.Start(primaryStage);
		cardScene.start(primaryStage);
		loginScene.Start(primaryStage);
		
		changePage(primaryStage);
		
		primaryStage.show();
	}
	public void loginRefresh() {
		loginScene.usernametf.clear();
		loginScene.passwordtf.clear();
	}
	
	public void changePage(Stage primaryStage) {
		loginScene.itemRegis.setOnAction(e -> {
			primaryStage.setTitle("Register");
			registerScene.registRefresh();
			primaryStage.setScene(registerScene.scene);
		});
		
		registerScene.itemLogin.setOnAction(e -> {
			primaryStage.setTitle("Login");
			loginRefresh();
			primaryStage.setScene(loginScene.scene);
		});
		homeScene.itemCart.setOnAction(e ->{
			primaryStage.setTitle("Cart");
			cartScene.refresh();
			primaryStage.setScene(cartScene.scene);
		});
		homeScene.itemHistory.setOnAction(e ->{
			primaryStage.setTitle("My History");
			historyScene.refresh();
			primaryStage.setScene(historyScene.scene);
		});
		homeScene.itemLogout.setOnAction(e ->{
			primaryStage.setTitle("Login");
			loginRefresh();
			primaryStage.setScene(loginScene.scene);
		});
		
		cartScene.itemHome.setOnAction(e ->{
			primaryStage.setTitle("Home");
			homeScene.refreshProduct();
			primaryStage.setScene(homeScene.scene);
		});
		cartScene.itemHistory.setOnAction(e ->{
			primaryStage.setTitle("My History");
			historyScene.refresh();
			primaryStage.setScene(historyScene.scene);
		});
		cartScene.itemLogout.setOnAction(e ->{
			primaryStage.setTitle("Login");
			loginRefresh();
			primaryStage.setScene(loginScene.scene);
		});
		
		historyScene.itemHome.setOnAction(e ->{
			primaryStage.setTitle("Home");
			homeScene.refreshProduct();
			primaryStage.setScene(homeScene.scene);
		});
		historyScene.itemCart.setOnAction(e ->{
			primaryStage.setTitle("Cart");
			cartScene.refresh();
			primaryStage.setScene(cartScene.scene);
		});
		historyScene.itemLogout.setOnAction(e ->{
			primaryStage.setTitle("Login");
			loginRefresh();
			primaryStage.setScene(loginScene.scene);
		});
		
		manageScene.itemHistory.setOnAction(e ->{
			primaryStage.setTitle("My History");
			viewScene.refreshView();
			primaryStage.setScene(viewScene.scene);
		});
		manageScene.itemLogout.setOnAction(e->{
			primaryStage.setTitle("Login");
			loginRefresh();
			primaryStage.setScene(loginScene.scene);
		});
		
		viewScene.itemManage.setOnAction(e_-> {
			primaryStage.setTitle("Manage Product");
			manageScene.refresh();
			primaryStage.setScene(manageScene.scene);
		});
		viewScene.itemLogout.setOnAction(e -> {
			primaryStage.setTitle("Login");
			loginRefresh();
			primaryStage.setScene(loginScene.scene);
		});
	}
		
}
