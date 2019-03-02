package com.gitub.thorbenkuck.tears.server;

import com.github.thorbenkuck.tears.shared.messages.ChatMessage;
import com.github.thorbenkuck.tears.shared.messages.DisplayNotes;
import com.github.thorbenkuck.tears.shared.messages.PlaySound;
import com.gitub.thorbenkuck.tears.server.network.ServerUser;

public class ChatDistribution {

	private void handleCommand(String command, ServerUser origin, ServerGameSession serverGameSession) {
		// BBUUUUUUUUUHHHH!
		// TODO: Make it better you dumb fuck!

		command = command.substring(1);

		if (command.startsWith("private")) {
			String raw = command.substring(8);
			String[] split = raw.split(" ");
			String userName = split[0];
			String msg = raw.substring(userName.length());
			ServerUser target = serverGameSession.find(userName);
			if (target == null) {
				origin.notify(new ChatMessage("Kein Nutzer " + userName + " gefunden", serverGameSession.toShared()));
			} else {
				target.notify(new ChatMessage("{from " + origin.toShared().getUserName() + "}: " + msg, serverGameSession.toShared()));
				origin.notify(new ChatMessage("{to " + target.toShared().getUserName() + "}: " + msg, serverGameSession.toShared()));
			}
		} else if(command.startsWith("play")) {
			String raw = command.substring(5);
			serverGameSession.publish(new PlaySound(raw, origin.toShared()));
		} else {
			origin.notify(new ChatMessage("Unknown command: " + command, serverGameSession.toShared()));
		}
	}

	public void publish(ServerGameSession gameSession, ChatMessage chatMessage, ServerUser origin) {
		if (chatMessage.getMsg().startsWith("/")) {
			handleCommand(chatMessage.getMsg(), origin, gameSession);
		} else {
			String newMessage = "[" + origin.toShared().getUserName() + "]: " + chatMessage.getMsg();
			gameSession.publish(new ChatMessage(newMessage, gameSession.toShared()));
		}
	}

}
