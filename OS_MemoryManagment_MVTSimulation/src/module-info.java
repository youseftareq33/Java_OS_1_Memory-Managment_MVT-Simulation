module OS {
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.base;
	
	opens A_RunApplication to javafx.graphics, javafx.fxml;
	opens B_Classes to javafx.base;
}
