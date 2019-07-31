package com.github.thorbenkuck.tears.shared.messages;

import com.github.thorbenkuck.tears.shared.datatypes.User;

import java.io.Serializable;

public final class ChatMessageResponse implements Serializable {

	private static final long serialVersionUID = 4630763041691659517L;
	private final ChatMessage chatMessage;
	private final User origin;

	public ChatMessageResponse(ChatMessage chatMessage, User origin) {
		this.chatMessage = chatMessage;
		this.origin = origin;
	}

	public ChatMessage getChatMessage() {
		return chatMessage;
	}

	public User getOrigin() {
		return origin;
	}
}
