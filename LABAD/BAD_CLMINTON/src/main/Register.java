package main;

import java.sql.SQLException;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import util.Connect;

public class Register{
	BorderPane bp;
	GridPane gp;

	Label register, email, password, confirm, age, gender, nationalityLbl;
	TextField emailField;
	PasswordField passwordField, confirmField;
	
	ToggleGroup genderGroup;
	RadioButton maleRadio, femaleRadio;
	
	ComboBox<String> nationality;
	Spinner<Integer> ageSpinner;
	
	Button registerBtn;
	
	MenuBar menuBar;
	Menu menus;
	MenuItem itemLogin;
	MenuItem itemRegis;
	
	Scene scene;
	Connect con = Connect.getInstance();
	Login log;
	Main main;
	public void initComponent() {
		bp = new BorderPane();
		gp = new GridPane();

		
		age = new Label("Age");
		register = new Label("Register");
		email = new Label("Email");
		password = new Label("Password");
		confirm = new Label("Confirm Password");
		gender = new Label("Gender");
		nationalityLbl = new Label("Nationality");
		
		emailField = new TextField();
		confirmField = new PasswordField();
		
		passwordField = new PasswordField();
		
		genderGroup = new ToggleGroup();
		maleRadio = new RadioButton("Male");
		femaleRadio = new RadioButton("Female");
		
		nationality = new ComboBox<>();
		ageSpinner = new Spinner<>();
		
		menuBar= new MenuBar();
		menus= new Menu("Page");
		itemLogin= new MenuItem("Login");
		itemRegis= new MenuItem("Register");
		
		registerBtn = new Button("Register");
		ageSpinner = new Spinner<>();
		SpinnerValueFactory<Integer> ageSpinnerVal = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
		ageSpinner.setValueFactory(ageSpinnerVal);
		
		scene = new Scene(bp, 1200, 720);
		
	}
	public void registRefresh() {
		emailField.clear();
		confirmField.clear();
		passwordField.clear();
		nationality.getSelectionModel().selectFirst();
		genderGroup.selectToggle(null);
		SpinnerValueFactory<Integer> ageSpinnerVal = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
		ageSpinner.setValueFactory(ageSpinnerVal);
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
		gp.add(email, 1,0);
		gp.add(emailField, 1, 1);
		
		gp.add(password, 1, 2);
		gp.add(passwordField, 1,3);
		
		gp.add(confirm, 1, 4);
		gp.add(confirmField, 1, 5);
		
		gp.add(age, 1, 6);
		gp.add(ageSpinner, 1, 7);
		
		gp.add(nationalityLbl, 3, 3);
		gp.add(nationality, 3, 4);
		
		gp.add(gender, 3, 0);
		gp.add(maleRadio, 3, 1);
		gp.add(femaleRadio, 3, 2);
		gp.add(registerBtn, 3, 5);
	}
	
	private void initRegisterForm() {
		maleRadio.setToggleGroup(genderGroup);
		femaleRadio.setToggleGroup(genderGroup);
		
		nationality.getItems().add("Indonesia");
		nationality.getItems().add("Malaysia");
		nationality.getItems().add("Thailand");
		nationality.getItems().add("Singapore");
		nationality.getItems().add("Brunnei");
		nationality.getItems().add("Vietnam");
		
		nationality.getSelectionModel().selectFirst();
	
	}
	private void adjust() {
		gp.setAlignment(Pos.CENTER);
		
		registerBtn.setPrefWidth(100);
		
		bp.setPrefHeight(400);
		bp.setPrefWidth(500);
		bp.setCenter(gp);
		
		gp.setHgap(10);
		gp.setVgap(10);

	}

	private boolean uniq() {
		boolean uniq = true;
		String query = String.format("SELECT UserEmail FROM MsUser");
		con.execQuery(query);
		try {
			while(con.rs.next()) {
			String username = con.rs.getString("UserEmail");
				if (emailField.getText().equalsIgnoreCase(username)) {
					uniq = false;
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return uniq;
	}
	private boolean validate() {
		Alert warning = new Alert(AlertType.WARNING);
		warning.setHeaderText("Warning");
			if (!emailField.getText().endsWith("@gmail.com")) {
				warning.setContentText("Email must ends with @gmail.com");
				warning.show();
				return false;
			}
//			if (emailField.getText().isEmpty()) {
//				warning.setContentText("Email must be filled");
//				warning.show();
//				return false;
//			}
			if (!uniq()) {
				warning.setContentText("email already been registered");
				warning.show();
				return false;
			}
			if (passwordField.getText().length() < 6) {
				warning.setContentText("password must contain minimum 6 characters");
				warning.show();
				return false;
			}
			if (!passwordField.getText().equals(confirmField.getText())) {
				warning.setContentText("confirm Password must be the same as Password");
				warning.show();
				return false;
			}
			if (ageSpinner.getValue() <= 0) {
				warning.setContentText("age must be greater than 0");
				warning.show();
				return false;
			}
			if (genderGroup.getSelectedToggle() == null) {
				warning.setContentText("gender must be selected");
				warning.show();
				return false;
			}
			if (nationality.getSelectionModel().getSelectedItem().isEmpty()) {
				warning.setContentText("nationality must be selected");
				warning.show();
				return false;
			}
		return true;
	}
	String id;
	private void getID() {
		String query = String.format("SELECT MAX(UserID) AS MaxID FROM MsUser;");
		con.execQuery(query);
		try {
			while(con.rs.next()) {
			String lastID = con.rs.getString("MaxID");
			Integer getNumber = Integer.parseInt(lastID.substring(2));
			id = String.format("UA%03d", getNumber+1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private void addData() {
		getID();
		RadioButton selectedRadioButton = (RadioButton) genderGroup.getSelectedToggle();
		String selectedGender = selectedRadioButton.getText();
		String query = String.format("INSERT INTO MsUser VALUES ('%s', '%s', '%s', %d, '%s','%s', 'User');"
				, id, emailField.getText(), passwordField.getText(), ageSpinner.getValue(), 
				selectedGender, nationality.getSelectionModel().getSelectedItem());
		con.execUpdate(query);
	}
	private void action(Stage primaryStage) {
		registerBtn.setOnMouseClicked(e -> {
			if (validate()) {
				addData();
				log = main.loginScene;
				log.regist = this;
				primaryStage.setScene(log.scene);
				primaryStage.setTitle("Login");
				primaryStage.show();
			}
		});
	}
	public void Start(Stage primaryStage) {
		initComponent();
		initNavBar();
		initBp();
		initGp();
		initRegisterForm();
		adjust();
		action(primaryStage);
		primaryStage.setScene(scene);
	}


}
