package com.gitub.thorbenkuck.tears.server;

import com.github.thorbenkuck.tears.shared.logging.Logger;
import com.github.thorbenkuck.tears.shared.messages.*;
import com.gitub.thorbenkuck.tears.server.network.ServerUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class UploadRepository {

	private final Map<String, UploadInformation> informationList = new HashMap<>();

	void add(byte[] data, String fileName, List<ServerUser> receivers, ServerUser sender, String id) {
		UploadInformation information = new UploadInformation(data, fileName, receivers, id, sender);
		UploadRequest uploadRequest = new UploadRequest(null, id, fileName, null);
		synchronized (informationList) {
			informationList.put(id, information);
		}

		information.receivers.forEach(receiver -> receiver.notify(uploadRequest));
	}

	void done(String id, ServerUser user) {
		Logger.debug(user + " done");
		UploadInformation uploadInformation;
		synchronized (informationList) {
			uploadInformation = informationList.get(id);

			if (!uploadInformation.removeUser(user)) {
				throw new IllegalStateException("Un authorized user " + user + " rejected " + id);
			}

			if(uploadInformation.receivers.isEmpty()) {
				informationList.remove(id);
			}
		}

		uploadInformation.sender.notify(new UploadFinished(id, user.toShared()));
	}

	void accept(String id, ServerUser user) {
		Logger.debug(user + " accepted");
		UploadInformation uploadInformation;
		synchronized (informationList) {
			uploadInformation = informationList.get(id);
		}

		uploadInformation.sender.notify(new UploadAccepted(uploadInformation.getId(), user.toShared()));
		user.notify(uploadInformation.content);
	}

	void reject(String id, ServerUser user) {
		Logger.debug(user + " rejected");
		UploadInformation uploadInformation;
		synchronized (informationList) {
			uploadInformation = informationList.get(id);
		}

		if (!uploadInformation.removeUser(user)) {
			throw new IllegalStateException("Un authorized user " + user + " rejected " + id);
		}

		uploadInformation.sender.notify(new UploadRejected(uploadInformation.getId(), user.toShared()));
	}

	private class UploadInformation {

		private final List<ServerUser> receivers;
		private final SoundDownload content;
		private final ServerUser sender;

		private UploadInformation(byte[] data, String fileName, List<ServerUser> receivers, String id, ServerUser sender) {
			this.sender = sender;
			this.content = new SoundDownload(id, fileName, data);
			this.receivers = receivers;
		}

		private boolean removeUser(ServerUser serverUser) {
			return receivers.remove(serverUser);
		}

		private String getId() {
			return content.getId();
		}
	}

}
