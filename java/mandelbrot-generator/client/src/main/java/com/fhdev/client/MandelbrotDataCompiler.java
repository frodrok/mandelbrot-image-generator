package com.fhdev.client;

import com.fhdev.json.MandelbrotRequest;
import com.fhdev.json.MandelbrotResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.fhdev.client.network.NetworkHandler;


import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.util.concurrent.CompletableFuture;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;

import com.fhdev.client.MandelbrotDataCompiler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;


class Pair<T, V> {
	
	T x = null;
	V y = null;
	
	public Pair(T x, V y) {
	    this.x = x;
	    this.y = y;
	}
	
}

final class MandelbrotDataCompiler {

    private final File fileToWriteTo;
    private final List<String> hosts;
    private final ObjectMapper mapper;
    private final ExecutorService executorService;
    private final CompletionService<Pair<Point, MandelbrotResponse>> completionService;

    private static final Logger logger = LoggerFactory.getLogger(MandelbrotDataCompiler.class);

    protected MandelbrotDataCompiler(File fileToWriteTo,
				     List<String> hosts,
				     ObjectMapper mapper,
				     ExecutorService executorService) {

	this.fileToWriteTo = fileToWriteTo;
	this.hosts = hosts;
	this.mapper = mapper;
	this.executorService = executorService;
	this.completionService = new ExecutorCompletionService<Pair<Point, MandelbrotResponse>>(this.executorService);
    }

    private Map<Integer, Pair<Point, MandelbrotResponse>> splitAndGetData(int partsAmount,
									  int width,
									  int height,
									  int maxIterations,
									  List<String> hosts) {


	var resultMap = new HashMap<Integer, Pair<Point, MandelbrotResponse>>();

	double onePartX = width / partsAmount;
	double onePartY = height / partsAmount;
	
	int generatedParts = 0;

	// Generated a MandelbrotRequest, fire it off to our server and add the Future
	// to our CompletionService
	for (int i = 0; i < partsAmount; i++) {
	    for (int j = 0; j < partsAmount; j++) {

		// These are final because we use them in our callable (responseCallable)
		final int startX = i * (int) onePartX;
		final int startY = j * (int) onePartY;

		int endX = (i + 1) * (int) onePartX;
		int endY = (j + 1) * (int) onePartY;

		var mbRequest = new MandelbrotRequest(generatedParts,
							startX,
							startY,
							endX,
							endY,
							width,
							height,
							-2, 1,
							-1, 1,
							maxIterations);

		var host = hosts.get(generatedParts);
		
		logger.info("Using " + host + " to generate part " + generatedParts + " with request " + mbRequest.toString());
		
		final var networkHandler = new NetworkHandler(host);

		var requestAsJson = "";
		try {
		    requestAsJson = mapper.writeValueAsString(mbRequest);
		} catch (JsonProcessingException ex) {
		}
		
		// Wrap sendAndReceive in a Callable so we can parallellize it
		// Copy it to make it final
		final String requestAsJsonCopy = requestAsJson;

		// Fire off a call to our networkHandler, deserialize the response and wrap it into a future
		Callable<Pair<Point, MandelbrotResponse>> responseCallable = () -> {
		    
		    var point = new Point(startX, startY);
		    
		    var resultString = networkHandler.sendAndReceive(requestAsJsonCopy);

		    MandelbrotResponse mbResponse = null;
		    
		    try {
			mbResponse = mapper.readValue(resultString, MandelbrotResponse.class);
		    } catch (IOException ex) {
		    }
			
		    return new Pair(point, mbResponse);
		    
		};
		
		completionService.submit(responseCallable);

		generatedParts++;				    
	    }
	}

	// Wait for all sent future's to finish
	int received = 0;
	int expectedFutures = partsAmount * 2;
	boolean errors = false;
	while (received < expectedFutures && !errors) {
	    
	    try {
		// completionService.take() blocks if there are no finished future's
		// it's like a stack of Future's
		Pair<Point, MandelbrotResponse> result = completionService.take().get();

		// Collect our result
		resultMap.put(received, result);
				
		received++;
	    } catch (Exception ex) {
		logger.info("Had an error while deserializing responses: " + ex.getMessage());
		errors = true;
		
	    }
	}


	return resultMap;
	
    }

    public boolean execute(int imageParts, int imageWidth, int imageHeight, int maxIterations) {
	boolean success = false;

	var bi = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
	
	var responses = splitAndGetData(imageParts, imageWidth, imageHeight, maxIterations, this.hosts);

	// value will be a Pair<Point, MandelbrotResponse>
	// Iterate our pairs of offsets and MandelbrotResponses and
	// translate calculations to color and draw to the BufferedImage
	responses.values().stream().forEach(value -> {
		
		var offsetPoint = value.x;
		var mbResponse = value.y;

		Integer[][] rawCalculations = mbResponse.getData();

		for (int i = 0; i < rawCalculations.length; i++) {
		    Integer[] yValues = rawCalculations[i];
		    for (int j = 0; j < yValues.length; j++) {
			Integer calculations = yValues[j];

			Integer color = Color.HSBtoRGB(calculations / 256f,
						       1,
						       calculations / (calculations + 8f));

			bi.setRGB(i + offsetPoint.x, j + offsetPoint.y, color);
		    }
		}
	    });

	// Write our buffer to file on disk
	try {
	    ImageIO.write(bi, "PNG", this.fileToWriteTo);
	    success = true;
	} catch (IOException ex) {
	    //	    success = false;
	}

	return success;
    }
			 
	
}
