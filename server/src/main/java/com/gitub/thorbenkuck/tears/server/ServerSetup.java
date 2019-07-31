package com.gitub.thorbenkuck.tears.server;

import com.github.thorbenkuck.tears.shared.datatypes.GameSession;
import com.github.thorbenkuck.tears.shared.logging.Logger;
import com.github.thorbenkuck.tears.shared.messages.*;
import com.github.thorbenkuck.tears.shared.messages.groups.GMDistributeMessage;
import com.github.thorbenkuck.tears.shared.messages.groups.GMPublishMessage;
import com.github.thorbenkuck.tears.shared.messages.groups.UploadDone;
import com.gitub.thorbenkuck.tears.server.network.Server;
import com.gitub.thorbenkuck.tears.server.network.ServerUser;

import java.util.Collections;
import java.util.Optional;

class ServerSetup {

	static void setup(Server server) {
		GameSessionManagement gameSessionManagement = new GameSessionManagement();
		UserManagement userManagement = new UserManagement();
		ChatDistribution chatDistribution = new ChatDistribution();

		gameSessionManagement.addObserver((o, arg) -> userManagement.distribute(arg));
		server.getDisconnectedPipeline().add(userManagement::logout);
		server.getDisconnectedPipeline().add(gameSessionManagement::leaveAll);

		server.handle(ChatMessage.class, message -> {
			ChatMessage chatMessage = message.getCore();

			Optional<ServerGameSession> optional = gameSessionManagement.getGameSession(chatMessage.getSession().getName());
			optional.ifPresent(session -> chatDistribution.publish(session, chatMessage, message.getServerUser()));
		});

		server.handle(UpdateUser.class, message -> {
			Logger.debug("Updating user " + message.getCore().getUser().detailedString());
			ServerUser serverUser = message.getServerUser();
			serverUser.set(message.getCore().getUser());
			gameSessionManagement.updateFor(serverUser);
		});

		server.handle(JoinServerRequest.class, message -> {
			ServerUser serverUser = message.getServerUser();
			if (serverUser.isValid()) {
				message.answer(new JoinServerResponse("Du bist bereits validiert!"));
				return;
			}

			serverUser.set(message.getCore().getUser());
			if (userManagement.login(serverUser)) {
				message.answer(new JoinServerResponse(message.getCore().getUser()));
				message.answer(gameSessionManagement.updateMessage());
				serverUser.identify();
			} else {
				message.answer(new JoinServerResponse("Der Name ist auf diesem Server bereits vergeben.."));
			}
		});

		server.handle(ImageToDisplayMessage.class, message -> {
			ImageToDisplayMessage core = message.getCore();
			Optional<ServerGameSession> gameSessionOptional = gameSessionManagement.getGameSession(core.getGameSession().getName());
			gameSessionOptional.ifPresent(gameSession -> {
				if (core.isToAll()) {
					gameSession.toParticipants(new DisplayImage(core.getData()));
				} else {
					gameSession.findAll(core.getSubset())
							.forEach(serverUser -> serverUser.notify(new DisplayImage(core.getData())));
				}
			});

		});

		server.handle(CreateSessionRequest.class, message -> {
			CreateSessionRequest createSessionRequest = message.getCore();
			boolean success = gameSessionManagement.createNewGameSession(createSessionRequest.getName(), message.getServerUser());
			if (!success) {
				message.answer(new CreateSessionResponse("Die GameSession muss einen Namen haben, der noch nicht vergeben und nicht leer ist!"));
			} else {
				ServerGameSession serverGameSession = gameSessionManagement.accessGameSession(createSessionRequest.getName());
				message.answer(new CreateSessionResponse(serverGameSession.toShared()));
			}
		});

		server.handle(PlaySound.class, message -> {
			ServerUser serverUser = message.getServerUser();
			PlaySound playSound = new PlaySound(message.getCore().getSoundName(), serverUser.toShared());
			gameSessionManagement.gmPublishAction(serverUser, playSound);
		});

		server.handle(JoinGameSessionRequest.class, message -> {
			if(gameSessionManagement.joinGameSession(message.getCore().getGameSession().getName(), message.getServerUser())) {
				GameSession gameSession = gameSessionManagement.accessGameSession(message.getCore().getGameSession().getName()).toShared();
				message.answer(new JoinGameSessionResponse(gameSession));
			}
		});

		server.handle(UploadRequest.class, message -> {
			UploadRequest origin = message.getCore();
			gameSessionManagement.getGameSessionForGM(message.getServerUser()).ifPresent(session -> {
				if (origin.getTarget() != null) {
					Optional<ServerUser> target = userManagement.find(origin.getTarget());
					Logger.debug("Target îs specified. Informing only target.");
					if(target.isPresent()) {
						session.prepareDownload(origin.getData(), origin.getFileName(), origin.getId(), message.getServerUser(), Collections.singletonList(target.get()));
					} else {
						Logger.error("No user found for " + origin.getTarget());
					}
				} else {
					Logger.debug("No target specified. Informing GameSession.");
					session.prepareDownload(origin.getData(), origin.getFileName(), origin.getId(), message.getServerUser());
				}
			});
		});

		server.handle(UploadResponse.class, message -> {
			UploadResponse origin = message.getCore();

			gameSessionManagement.findGameSessions(message.getServerUser()).forEach(game -> {
				if(origin.isAccepted()) {
					game.accept(origin.getId(), message.getServerUser());
				} else {
					game.reject(origin.getId(), message.getServerUser());
				}
			});
		});

		server.handle(UploadDone.class, message -> {
			gameSessionManagement.findGameSessions(message.getServerUser()).forEach(game -> game.done(message.getCore().getId(), message.getServerUser()));
		});

		server.handle(LeaveGameSessionRequest.class, message -> gameSessionManagement.leaveAll(message.getServerUser()));

		server.handle(KillAllSounds.class, message -> {
			gameSessionManagement.gmPublishAction(message.getServerUser(), message.getCore());
		});

		server.handle(GMPublishMessage.class, message -> {
			gameSessionManagement.gmPublishAction(message.getServerUser(), message.getCore().getToPublish());
		});

		server.handle(GMDistributeMessage.class, message -> {
			gameSessionManagement.gmPublishAction(message.getServerUser(), message.getCore().getMessage());
		});

		server.handle(PublicRollRequest.class, message -> {
			ServerUser serverUser = message.getServerUser();
			// TODO Security check
			Optional<ServerGameSession> serverGameSession = gameSessionManagement.getGameSession(message.getCore().getGameSession().getName());
			serverGameSession.ifPresent(gameSession -> {
				int result = 0;
				int rolls = message.getCore().getAmountRolls();

				for(int i = 0 ; i < rolls ; i++) {
					result += serverUser.randomNumber(message.getCore().getNumberOfSides());
				}

				// Ensure that we only use the
				// server side objects. We do
				// not reuse the elements send
				// by the client!
				gameSession.publish(new PublicRollResponse(message.getServerUser().toShared(), result, message.getCore().getNumberOfSides(), gameSession.toShared(), message.getCore().getAmountRolls()));
			});
		});
	}

}
