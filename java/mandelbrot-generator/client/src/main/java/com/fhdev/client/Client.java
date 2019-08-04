package com.fhdev.client;

import com.fhdev.json.MandelbrotRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fhdev.client.network.NetworkHandler;

public class Client {

    private static final Integer WIDTH = 320;
    private static final Integer HEIGHT = 240;
    static Integer RE_START = -2;
    static Integer RE_END = 1;
    static Integer IM_START = -1;
    static Integer IM_END = 1;
    static Integer MAX_ITERATIONS = 160;
    static Integer PARTS = 2;

    final static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {

	System.out.println("client starting..!");

	NetworkHandler handler = new NetworkHandler("127.0.0.1", 1000);

	// @TODO: Receive parameters as arguments later
	
	MandelbrotRequest hey = new MandelbrotRequest(0, 0, 0, WIDTH, HEIGHT,
						      WIDTH,HEIGHT,
						      RE_START, RE_END,
						      IM_START, IM_END,
						      MAX_ITERATIONS);

	String requestAsJson = mapper.writeValueAsString(hey);

	String response = handler.sendAndReceive(requestAsJson);

	System.out.println(response);

	System.out.println("client done");


    }

}
