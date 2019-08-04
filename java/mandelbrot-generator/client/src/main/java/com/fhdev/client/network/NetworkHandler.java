package com.fhdev.client.network;

import java.net.Socket;
import java.net.UnknownHostException;

import java.io.IOException;

import java.nio.charset.StandardCharsets;


public final class NetworkHandler {

    private final String ipAddress;
    private final Integer port;

    private NetworkHandler() {
	ipAddress = "";
	port = 0;
    }

    public NetworkHandler(String ipAddress, Integer port) {
	this.ipAddress = ipAddress;
	this.port = port;
    }

    // Can do: Implement one for byte[] argument as well
    public String sendAndReceive(final String message) {
	Socket socket = null;
	String receivedMessage = "";
	try {
	    socket = new Socket(this.ipAddress, this.port);
	    var withNewLine = message + "\n";
	    byte[] b = withNewLine.getBytes(StandardCharsets.UTF_8);
	    socket.getOutputStream().write(b);
	} catch (UnknownHostException e) {
	    System.err.print(e);
	} catch (IOException e) {
	    System.err.print(e);
	} finally {
	    try {
		socket.close();
	    } catch (IOException e) {
		System.err.print(e);
	    }
	}

	return receivedMessage;
    }
}
