package com.fhdev.client;

import com.fhdev.client.MandelbrotDataCompiler;

import java.io.File;
import java.util.List;
import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    final static List<String> hosts = Arrays.asList("127.0.0.1:1000",
					   "127.0.0.1:1001",
					   "127.0.0.1:1002",
					   "127.0.0.1:1003");
					   

    private static final ObjectMapper mapper = new ObjectMapper();

    private static ExecutorService executorService = Executors.newFixedThreadPool(4);

    public static void main(String[] args) throws Exception {

	Integer imageParts = Integer.parseInt(args[0]);
	Integer imageWidth = Integer.parseInt(args[1]);
	Integer imageHeight = Integer.parseInt(args[2]);
	Integer maxIterations = Integer.parseInt(args[3]);
	String outputFilePath = args[4];

	var fileToWriteTo = new File(outputFilePath);

	boolean success = new MandelbrotDataCompiler(fileToWriteTo,
				   hosts,
				   mapper,
				   executorService).execute(imageParts,
							    imageWidth,
							    imageHeight,
							    maxIterations);

	if (success) {
	    System.out.println("The client succeeded, wrote to file " + outputFilePath);
	} else {
	    System.out.println("The client did not succeed");
	}

	executorService.shutdown();

    }

}
