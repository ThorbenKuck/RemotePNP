package com.gitub.thorbenkuck.tears.client;

import com.gitub.thorbenkuck.tears.client.ui.CloseEventHandler;
import com.gitub.thorbenkuck.tears.client.ui.MetaController;
import com.gitub.thorbenkuck.tears.client.ui.setup.view.SetupView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.controlsfx.dialog.ExceptionDialog;

public class ClientMain extends Application  {

	public static void main(String[] args) {
		launch(args);
	}

	private void showErrorDialog(Thread t, Throwable e) {
		ExceptionDialog exceptionDialog = new ExceptionDialog(e);
		exceptionDialog.setHeaderText("An uncaught exception was thrown in thread " + t
				+ ". Click below to view the stacktrace, or close this "
				+ "dialog to terminate the application.");
		exceptionDialog.show();
	}

	@Override
	public void start(Stage primaryStage) {
		Thread.setDefaultUncaughtExceptionHandler(((t, e) -> e.printStackTrace()));
		MetaController metaController = MetaController.create();
		metaController.setMainStage(primaryStage);
		primaryStage.setOnCloseRequest(new CloseEventHandler());
		try {
			metaController.show(SetupView.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
