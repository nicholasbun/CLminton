module BAD {
	opens main;
	opens models;
	opens util;
	requires javafx.graphics;
	requires javafx.controls;
	requires java.desktop;
	requires java.sql;
	requires jfxtras.window;
	
}