package com.gitub.thorbenkuck.tears.client;

import com.gitub.thorbenkuck.tears.client.ui.CloseEventHandler;
import com.gitub.thorbenkuck.tears.client.ui.MetaController;
import com.gitub.thorbenkuck.tears.client.ui.setup.view.SetupView;
import javafx.application.Application;
import javafx.stage.Stage;

public class ClientMain extends Application  {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
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
