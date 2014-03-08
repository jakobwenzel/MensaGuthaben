package com.codebutler.farebot.card.desfire;

/**
 * Created by Jakob Wenzel on 16.11.13.
 */
public class DesfireException extends Exception {
	public DesfireException(String message) {
		super(message);
	}
	public DesfireException(Throwable cause) {
		super(cause);
	}
}
