package com.fhdev.mandelbrotgenerator;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fhdev.json.MandelbrotRequest;
import com.fhdev.json.MandelbrotResponse;

import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;

import com.fhdev.mandelbrotgenerator.calculator.MandelbrotCalculator;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Server {

    final static Logger logger = LoggerFactory.getLogger(Server.class);
    final static ObjectMapper mapper = new ObjectMapper();
    final static MandelbrotCalculator calculator = new MandelbrotCalculator();

    public static void main(String[] args) throws Exception {

	ServerSocket serverSocket = null;
	Socket clientSocket;
	String inputLine = null, outputLine = null, jsonData = null;
	Integer portNumber;
	PrintWriter out;
	BufferedReader in;
	Boolean running = true;
    	
	try {

	    // Get port from arguments
	    portNumber = Integer.parseInt(args[0]);
	    
	    logger.info("Starting server on port " + portNumber);
	    
	    serverSocket = new ServerSocket(portNumber);
	    
	    while (running) {

		clientSocket = serverSocket.accept();

		logger.info("Got connection from " + clientSocket.getRemoteSocketAddress().toString());
		
		out = new PrintWriter(clientSocket.getOutputStream(), true);

		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	    
		while (jsonData == null) {
		    logger.info("Received " + inputLine);
		    inputLine = in.readLine();
		    jsonData = inputLine;
		    logger.info(inputLine);
		}

		MandelbrotRequest request = mapper.readValue(jsonData, MandelbrotRequest.class);

		MandelbrotResponse response = calculator.calculate(request);

		String jsonString = mapper.writeValueAsString(response);

		byte[] asBytes = jsonString.getBytes(StandardCharsets.UTF_8);
		int messageLength = asBytes.length;
		
		logger.info("Sending response of length: " + messageLength + " back");

		// Start by sending a packet containing the length
		byte[] mLength = ByteBuffer.allocate(4).putInt(messageLength).array();
		clientSocket.getOutputStream().write(mLength);

		// Send the message in 60k parts
		int sentBytes = 0;
		int start = 0;
		int end = 59999;

		// Start at messageLength and reduce our way down to 0
		int leftToSend = messageLength;
		
		while (leftToSend > 0) {

		    byte[] part = null;

		    if (leftToSend > 59999) {
			part = new byte[60000];
		    } else {
			part = new byte[leftToSend];
		    }

		    // Copy bytes from our serialized response to our part
		    for (int i = 0, offset = start; i < part.length; offset++, i++) {
			part[i] = asBytes[offset];
		    }

		    // write has no return value
		    clientSocket.getOutputStream().write(part);

		    start = start + 60000;
		    end = end + 60000;
		    sentBytes += (end - start);
		    leftToSend = leftToSend - part.length;
		    
		    //		    logger.info("sent " + sentBytes + " / " + messageLength);
		    logger.info("lefttosend: " + leftToSend);
		}

		logger.info("done");
		
	    }

	} catch (Exception ex) {
	    System.out.println("face exception");
	    
	    throw ex;
	} finally {
			serverSocket.close();
	}

    }
}
