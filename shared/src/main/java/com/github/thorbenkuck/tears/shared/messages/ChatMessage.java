package com.github.thorbenkuck.tears.shared.messages;

import com.github.thorbenkuck.tears.shared.datatypes.GameSession;

import java.io.Serializable;

public final class ChatMessage implements Serializable {

	private final String msg;
	private final GameSession session;
	private static final long serialVersionUID = 7383524410818602780L;

	public ChatMessage(String msg, GameSession session) {
		this.msg = msg;
		this.session = session;
	}

	public String getMsg() {
		return msg;
	}

	public GameSession getSession() {
		return session;
	}

	@Override
	public String toString() {
		return "ChatMessage{" +
				"msg='" + msg + '\'' +
				", session=" + session +
				'}';
	}
}
