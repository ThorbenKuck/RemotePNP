package com.gitub.thorbenkuck.tears.client.ui.character.view;

import com.github.thorbenkuck.tears.shared.datatypes.Attribute;
import com.github.thorbenkuck.tears.shared.datatypes.BaseValue;
import com.github.thorbenkuck.tears.shared.logging.Logger;
import com.gitub.thorbenkuck.tears.client.ui.FXUtils;
import com.gitub.thorbenkuck.tears.client.ui.TextFieldListener;
import com.gitub.thorbenkuck.tears.client.ui.character.presenter.CharacterPresenter;
import com.gitub.thorbenkuck.tears.client.ui.setup.view.SetupView;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

class JavaFXCharacterView implements CharacterView {

	private CharacterPresenter presenter;
	private Parent root;
	private Scene scene;
	private Stage stage;
	@FXML
	private TextField characterName;
	@FXML
	private TextField maxLife;
	@FXML
	private TextField maxMentalHealth;
	@FXML
	private TextField newValueName;
	@FXML
	private TextField newValueInt;
	@FXML
	private ListView<BaseValue> valuesList;
	@FXML
	private TextField newAttributeName;
	@FXML
	private TextField newAttributeValue;
	@FXML
	private ListView<Attribute> attributeList;
	@FXML
	private Label feedback;

	JavaFXCharacterView(CharacterPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public CharacterPresenter getPresenter() {
		return presenter;
	}

	@Override
	public void addBaseValue(BaseValue baseValue) {
		valuesList.getItems().add(baseValue);
	}

	@Override
	public void addAttribute(Attribute value) {
		attributeList.getItems().add(value);
	}

	@Override
	public void setFeedback(String s) {
		feedback.setText(s);
	}

	@Override
	public String selectStoreFile(String fileName) {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Character files (*.character)", "*.character");
		fileChooser.getExtensionFilters().add(extFilter);
		fileChooser.setInitialFileName(fileName);
		File file = fileChooser.showSaveDialog(stage);

		if (file == null) {
			return "";
		}

		return file.getAbsolutePath();
	}

	@Override
	public void close() {
		FXUtils.runOnApplicationThread(() -> {
			if (stage != null) {
				stage.close();
				stage = null;
			}

			presenter = null;
			root = null;
			scene = null;
		});
	}

	@Override
	public void setup() {
		URL location = SetupView.class.getClassLoader().getResource("character.fxml");
		FXMLLoader fxmlLoader = new FXMLLoader(location);
		fxmlLoader.setController(this);
		Parent parent;
		try {
			parent = fxmlLoader.load();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		FXUtils.runOnApplicationThread(() -> {
			root = parent;
			scene = new Scene(root);

			TextFieldListener.integerOnly(maxLife);
			TextFieldListener.integerOnly(maxMentalHealth);
			TextFieldListener.integerOnly(newValueInt);
			TextFieldListener.integerOnly(newAttributeValue);
		});
	}

	@FXML
	public void createNewCharacter(ActionEvent actionEvent) {
		String characterName = this.characterName.getText();
		String characterLifeRaw = maxLife.getText();
		String characterMentalRaw = maxMentalHealth.getText();

		if (characterName.isEmpty()) {
			setFeedback("Bitte geben einen Namen ein!");
			this.characterName.requestFocus();
		}

		if (characterLifeRaw.isEmpty()) {
			setFeedback("Bitte geben dein Maximales Leben ein!");
			maxLife.requestFocus();
		}

		if (characterMentalRaw.isEmpty()) {
			setFeedback("Bitte geben deine Mental Gesundheit ein!");
			maxMentalHealth.requestFocus();
		}

		int characterLife;
		int characterMental;

		try {
			characterLife = Integer.parseInt(characterLifeRaw);
		} catch (NumberFormatException e) {
			setFeedback("Das Leben muss eine Zahl sein");
			maxLife.clear();
			maxLife.requestFocus();
			return;
		}

		try {
			characterMental = Integer.parseInt(characterMentalRaw);
		} catch (NumberFormatException e) {
			setFeedback("Die mentale Gesundheit muss eine Zahl sein");
			maxMentalHealth.clear();
			maxMentalHealth.requestFocus();
			return;
		}

		presenter.createCharacter(characterName, characterLife, characterMental, new ArrayList<>(valuesList.getItems()), new ArrayList<>(attributeList.getItems()));
	}

	@FXML
	public void addNewBaseValue(ActionEvent actionEvent) {
		String baseValueName = newValueName.getText();
		String baseValueRaw = newValueInt.getText();

		if (baseValueName.isEmpty()) {
			setFeedback("Du must einen Namen für das Attribut angeben!");
			return;
		}

		if (baseValueRaw.isEmpty()) {
			setFeedback("Bitte gebe eine Zahl für das Attribut ein!");
			return;
		}

		System.out.println(baseValueName + "(" + baseValueRaw + ")");

		int baseValue;

		try {
			baseValue = Integer.parseInt(baseValueRaw);
		} catch (NumberFormatException e) {
			setFeedback("Das Attribut muss eine Zahl sein!");
			newValueInt.clear();
			newValueInt.requestFocus();
			return;
		}

		newValueInt.clear();
		newValueName.clear();

		BaseValue value = new BaseValue(baseValueName, baseValue);
		addBaseValue(value);
	}

	@FXML
	public void addNewAttribute(ActionEvent actionEvent) {
		String attributeName = newAttributeName.getText();
		String rawValue = newAttributeValue.getText();

		if (attributeName.isEmpty()) {
			setFeedback("Du must einen Namen für die Fertigkeit angeben!");
			return;
		}

		if (rawValue.isEmpty()) {
			setFeedback("Bitte gebe eine Zahl für die Fertigkeit ein!");
			return;
		}

		System.out.println(attributeName + "(" + rawValue + ")");

		int baseValue;

		try {
			baseValue = Integer.parseInt(rawValue);
		} catch (NumberFormatException e) {
			setFeedback("Der Fertigkeitswert muss eine Zahl sein!");
			newAttributeValue.clear();
			newAttributeValue.requestFocus();
			return;
		}

		newAttributeValue.clear();
		newAttributeName.clear();

		Attribute value = new Attribute(attributeName, baseValue);
		addAttribute(value);
	}

	@Override
	public void display() {
		FXUtils.runOnApplicationThread(() -> {
			stage.setScene(scene);
			stage.show();
		});
	}

	@Override
	public void setStage(Stage stage) {
		this.stage = stage;
	}
}
