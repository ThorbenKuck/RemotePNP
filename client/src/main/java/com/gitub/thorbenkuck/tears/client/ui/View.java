package com.gitub.thorbenkuck.tears.client.ui;

import javafx.stage.Stage;

public interface View {

	Presenter getPresenter();

	void close();

	void setup();

	void display();

	void setStage(Stage stage);
}
