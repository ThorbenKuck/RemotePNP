package com.github.thorbenkuck.tears.shared.exceptions;

public class ConnectionEstablishmentFailedException extends Exception {

	public ConnectionEstablishmentFailedException() {
	}

	public ConnectionEstablishmentFailedException(String message) {
		super(message);
	}

	public ConnectionEstablishmentFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConnectionEstablishmentFailedException(Throwable cause) {
		super(cause);
	}

	public ConnectionEstablishmentFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
