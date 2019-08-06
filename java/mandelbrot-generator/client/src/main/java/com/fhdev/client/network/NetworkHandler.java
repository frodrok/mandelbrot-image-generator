package com.fhdev.client.network;

import java.net.Socket;
import java.net.UnknownHostException;

import java.io.IOException;

import java.util.concurrent.Callable;

import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;


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

    public NetworkHandler(String ipAndPort) {
	var ip = ipAndPort.split(":")[0];
	var port = ipAndPort.split(":")[1];
	this.ipAddress = ip;
	this.port = Integer.parseInt(port);
    }

    // Can do: Implement one for byte[] argument as well
    public String sendAndReceive(final String message) {
	
	Socket socket = null;
	var receivedMessage = "";
	
	try {
	    socket = new Socket(this.ipAddress, this.port);

	    // Add a newline byte because java in.readLine()
	    var withNewLine = message + "\n";
	    byte[] b = withNewLine.getBytes(StandardCharsets.UTF_8);
	    socket.getOutputStream().write(b);

	    // Receive a LEN package first and read that number of bytes
	    byte[] len = new byte[4];
	    int successfullRead = socket.getInputStream().read(len, 0, 4);

	    if (successfullRead > 0) {
		int messageLength = ByteBuffer.wrap(len).getInt();

		// Allocate a new byte array for the message itself
		byte[] messageBytes = new byte[messageLength];

		// Read 60k bytes each and fill the message bytes
		int readBytes = 0;
		int start = 0;
		int end = 59999;

		while (readBytes < messageLength) {

		    // we have index 0 to 59999, 60000 will be array index out of bounds
		    byte[] read = new byte[60000];

		    int wellfareBytes = socket.getInputStream().read(read, 0, 60000);

		    for (int index = 0; index < wellfareBytes; index++) {
			messageBytes[index + start] = read[index];
		    }
		    
		    start = start + wellfareBytes;
		    end = end + wellfareBytes;
		    readBytes += wellfareBytes;
		    
		}

		receivedMessage = new String(messageBytes, StandardCharsets.UTF_8);
		   
	    } else {
		System.err.print("We did not receive a good length message");
	    }
	    
	} catch (UnknownHostException e) {
	    System.err.print(e);
	} catch (IOException e) {
	    System.err.print(e);
	} finally {
	    if (socket != null) {
		try {
		    socket.close();
		} catch (IOException e) {
		    System.err.print(e);
		}
	    }
	}

	return receivedMessage;
    }

}
