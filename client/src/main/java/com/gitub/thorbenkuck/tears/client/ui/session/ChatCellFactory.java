package com.gitub.thorbenkuck.tears.client.ui.session;

import com.gitub.thorbenkuck.tears.client.ui.chat.DiceRollMessage;
import com.gitub.thorbenkuck.tears.client.ui.chat.OtherChatMessage;
import com.gitub.thorbenkuck.tears.client.ui.chat.OwnChatMessage;
import com.gitub.thorbenkuck.tears.client.ui.chat.SystemMessage;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class ChatCellFactory implements Callback<ListView<Message>, ListCell<Message>> {
	@Override
	public ListCell<Message> call(ListView<Message> param) {
		return new CustomListCell();
	}

	private class CustomListCell extends ListCell<Message> {
		@Override
		protected void updateItem(Message item, boolean empty) {
			super.updateItem(item, empty);
			if (empty) {
				setText(null);
				setGraphic(null);
			} else {
				setStyle("-fx-padding: 5px 0px 0px 0px;");
				switch (item.getMessageType()) {
					case YOUR_MESSAGE:
						setGraphic(new OwnChatMessage(item.getContent(), this));
						break;
					case MY_DICE_ROLL_RESULT:
						setGraphic(new DiceRollMessage(item.getSides(), item.getResult(), item.getAmount(), "Du", this, true));
						break;
					case OTHER_PLAYERS_MESSAGE:
						setGraphic(new OtherChatMessage(item.getContent(), item.getUser().getUserName(), this));
						break;
					case OTHER_DICE_ROLL_RESULT:
						setGraphic(new DiceRollMessage(item.getSides(), item.getResult(), item.getAmount(), item.getUser().getUserName(), this, false));
						break;
					case SYSTEM_MESSAGE:
					default:
						setGraphic(new SystemMessage(item.getContent(), this));
						break;
				}
			}
		}
	}
}
