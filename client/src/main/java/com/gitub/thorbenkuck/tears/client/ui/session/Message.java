package com.gitub.thorbenkuck.tears.client.ui.session;

import com.github.thorbenkuck.tears.shared.datatypes.User;

public class Message {

	private final String content;
	private final MessageTypes messageType;
	private final User sender;
	private final int sides;
	private final int result;
	private final int amount;

	public Message(String content, MessageTypes messageType, User sender) {
		this(content, messageType, sender, -1, -1, -1);
	}

	public Message(String content, MessageTypes messageType, User sender, int sides, int result, int amount) {
		this.content = content;
		this.messageType = messageType;
		this.sender = sender;
		this.sides = sides;
		this.result = result;
		this.amount = amount;
	}

	public String getContent() {
		return content;
	}

	public MessageTypes getMessageType() {
		return messageType;
	}

	public User getUser() {
		return sender;
	}

	public int getSides() {
		return sides;
	}

	public int getResult() {
		return result;
	}

	public int getAmount() {
		return amount;
	}
}
