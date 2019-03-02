package com.github.thorbenkuck;

import com.github.thorbenkuck.tears.shared.messages.ChatMessage;
import com.gitub.thorbenkuck.tears.server.ChatDistribution;

public class ChatTest {

	public static void main(String[] args) {
		ChatDistribution chatDistribution = new ChatDistribution();
		chatDistribution.publish(null, new ChatMessage("/private Thorben was geht alter?", null), null);
	}

}
