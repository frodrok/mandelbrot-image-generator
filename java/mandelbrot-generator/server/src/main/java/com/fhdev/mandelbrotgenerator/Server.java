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

import com.fhdev.mandelbrotgenerator.calculator.MandelbrotCalculator;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Server {

    final static Logger logger = LoggerFactory.getLogger(Server.class);
    final static ObjectMapper mapper = new ObjectMapper();
    final static MandelbrotCalculator calculator = new MandelbrotCalculator();

    public static void main(String[] args) throws Exception {

	ServerSocket serverSocket;
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

		logger.info("got the string, continuing");
		
		MandelbrotRequest request = mapper.readValue(jsonData, MandelbrotRequest.class);
		logger.info(request.toString());
		logger.info(request.startX + "");
		logger.info(request.startY + "");
		logger.info(request.endX + "");	     
		logger.info(request.endY + "");	     
		
		logger.info(request.maxIter + "");

		logger.info("starting calculations");
		MandelbrotResponse response = calculator.calculate(request);
		logger.info("ending calculations");

		String jsonString = mapper.writeValueAsString(response);
		logger.info("sending response back" + jsonString.length());
		out.println(jsonString);

		logger.info("done");
		
	    }

	} catch (Exception ex) {
	    System.out.println("face exception");
	    
	    throw ex;
	}

    }
}
