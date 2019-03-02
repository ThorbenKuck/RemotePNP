package com.gitub.thorbenkuck.tears.server.network;

import com.github.thorbenkuck.tears.shared.exceptions.ConnectionEstablishmentFailedException;
import com.github.thorbenkuck.tears.shared.logging.Logger;
import com.github.thorbenkuck.tears.shared.network.ServerContainer;
import com.github.thorbenkuck.tears.shared.network.Session;
import com.github.thorbenkuck.tears.shared.pipeline.Pipeline;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class ServerImpl implements Server {

	private final ServerContainer serverContainer;
	private final Map<Session, ServerUser> userMap = new HashMap<>();
	private final Pipeline<ServerUser> disconnectedPipeline = new Pipeline<>();

	ServerImpl(int port) throws ConnectionEstablishmentFailedException {
		try {
			serverContainer = new ServerContainer(port);
		} catch (IOException e) {
			throw new ConnectionEstablishmentFailedException(e);
		}
		serverContainer.ingoingConnections().subscribe(connection -> {
			String address = connection.remoteAddress().replace("/", "");
			connection.session().setIdentifier(address.split(":")[0]);
			Logger.info("Neuer Client verbunden von " + address);
			Session session = connection.session();

			synchronized (userMap) {
				userMap.put(session, new ServerUserImpl(session));
			}

			try {
				session.send("\n################################\n" +
						"# Welcome to Remote T.E.A.R.S! #\n" +
						"################################\n\n" +
						"This is a stateless, private server for \"remote Pen and Paper\".\n" +
						"No database is connected, you will not gain anything from hacking this. Just saying.\n\n");
			} catch (IllegalStateException e) {
				Logger.warn("Could not send initial welcome message");
			}

			connection.setOnDisconnect(disconnected -> {
				Logger.info("Verbindung verloren zu " + address);
				session.setIdentified(false);
				ServerUser serverUser;
				synchronized (userMap) {
					serverUser = userMap.remove(session);
				}

				disconnectedPipeline.run(serverUser);
			});
		});
	}

	@Override
	public Pipeline<ServerUser> getDisconnectedPipeline() {
		return disconnectedPipeline;
	}

	@Override
	public void run() {
		Thread thread = new Thread(serverContainer::accept, "Server Thread");
		thread.start();
	}

	@Override
	public <T> void handle(Class<T> type, MessageHandler<T> handler) {
		serverContainer.getCommunicationRegistration()
				.register(type, (connection, t) -> {
					ServerUser serverUser;
					Session session = connection.session();

					synchronized (userMap) {
						serverUser = userMap.get(session);
					}

					if(serverUser == null) {
						Logger.warn("Received " + t + " from unknown source!");
						Logger.warn("Malicious source: " + connection.remoteAddress());
						return;
					}

					if(!serverUser.isValid()) {
						Logger.warn("Unvalidated ServerUser detected: " + serverUser);
					}

					Logger.info("Received " + t + " from " + serverUser);

					Message<T> message = new MessageImpl<>(t, serverUser);
					handler.handle(message);
				});
	}

	@Override
	public void verify(ServerUser serverUser) {
		ServerUserImpl real = (ServerUserImpl) serverUser;
		real.identifySession();
	}
}
