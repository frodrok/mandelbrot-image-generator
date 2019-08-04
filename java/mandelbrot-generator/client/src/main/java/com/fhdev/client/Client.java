package com.fhdev.client;

import com.fhdev.json.MandelbrotRequest;
import com.fhdev.json.MandelbrotResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fhdev.client.network.NetworkHandler;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;

import javax.imageio.ImageIO;

public class Client {

    private static final Integer WIDTH = 640;
    private static final Integer HEIGHT = 480;
    
    static Integer RE_START = -2;
    static Integer RE_END = 1;
    static Integer IM_START = -1;
    static Integer IM_END = 1;
    static Integer MAX_ITERATIONS = 255;
    static Integer PARTS = 2;

    final static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {

	System.out.println("client starting..!");

	var bi = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	var ig2 = bi.createGraphics();

	NetworkHandler handler = new NetworkHandler("127.0.0.1", 1000);

	// @TODO: Receive parameters as arguments later
	MandelbrotRequest hey = new MandelbrotRequest(0, 0, 0, WIDTH, HEIGHT,
						      WIDTH,HEIGHT,
						      RE_START, RE_END,
						      IM_START, IM_END,
						      MAX_ITERATIONS);

	String requestAsJson = mapper.writeValueAsString(hey);

       	String response = handler.sendAndReceive(requestAsJson);

	// Write the response we got to file for debugging
	try {
	    
           FileWriter fw = new FileWriter("response.json");    
           fw.write(response);    
           fw.close();
	   
	} catch(Exception e) {
	    System.out.println(e);
	}
	
	MandelbrotResponse mbResponse = mapper.readValue(response, MandelbrotResponse.class);


	// What we get here is a [startX - endX][startY - endY] array
	Integer[][] ww = mbResponse.getData();

	// Iterate the data we received and translate to colors
	for (int i = hey.startX; i < hey.endX; i++) {
	    for (int j = hey.startY; j < hey.endY; j++) {
		Integer calculations = ww[i][j];
		int value = 255 * calculations / MAX_ITERATIONS;
		//int r = 5;
		//int g = 25; 
		//int b = 255;
		int r = value, g = value, b = value;
		int col = (r << 16) | (g << 8) | b;
		bi.setRGB(i, j, col);
	    }
	}

	// Write our buffer to file on disk
	ImageIO.write(bi, "PNG", new File("./output.png"));

    }

}
