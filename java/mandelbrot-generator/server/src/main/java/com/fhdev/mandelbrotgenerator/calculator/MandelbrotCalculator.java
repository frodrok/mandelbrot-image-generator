package com.fhdev.mandelbrotgenerator.calculator;

import com.fhdev.json.MandelbrotRequest;
import com.fhdev.json.MandelbrotResponse;


public class MandelbrotCalculator {

    // I'm unsure whether to pass a reference to the logger or create a new one

    public MandelbrotResponse calculate(MandelbrotRequest request) {

	System.out.println("my face is ready");

	// Allocate a two dimensional matrix
	Integer[][] calculations = new Integer[request.endX - request.startX][request.endY - request.startY];

	System.out.println("before loop");
	for (int i = request.startX; i < request.endX; i++) {
	    for (int j = request.startY; j < request.endY; j++) {
		// i = the current x pixel
		// j = the current y pixel
	    }
	}

	System.out.println("after loop");

	// Allocate a MandelbrotResponse
	MandelbrotResponse response = new MandelbrotResponse(calculations);

	System.out.println("returning");
	return response;
    }
}
