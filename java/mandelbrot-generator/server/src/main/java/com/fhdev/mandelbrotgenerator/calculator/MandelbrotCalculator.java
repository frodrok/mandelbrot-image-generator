package com.fhdev.mandelbrotgenerator.calculator;

import com.fhdev.json.MandelbrotRequest;
import com.fhdev.json.MandelbrotResponse;

import com.fhdev.mandelbrotgenerator.calculator.Complex;


public class MandelbrotCalculator {

    private int mand(Complex z0, int maxIterations) {
	Complex z = z0;
	for (int t = 0; t < maxIterations; t++) {
	    if (z.abs() > 2.0) return t;
	    z = z.times(z).plus(z0);
	}
	return maxIterations;
    }

    public MandelbrotResponse calculate(MandelbrotRequest request) {

	//	System.out.println("calculating request: " + request.toString());
	
	// Allocate a two dimensional matrix
	//System.out.println("Allocating array with [" + (request.endX - request.startX) + "][" + (request.endY - request.startY) + "]");
	
	Integer[][] calculations = new Integer[request.endX - request.startX][request.endY - request.startY];

	double size = 2;

	for (int i = request.startX; i < request.endX; i++) {
	    for (int j = request.startY; j < request.endY; j++) {
		
		double x0 = -0.5 - size / 2 + size * i / request.totalX;
		double y0 = 0 - size / 2 + size * j / request.totalY;
		Complex z0 = new Complex(x0, y0);
		int pointCalculations = mand(z0, request.maxIter);

		// We iterate startX to endX which can be 320-640, therefor
		// we cannot use i or j to access points in the calculations array,
		// so subtract startX from i to get the array index
		
		calculations[i - request.startX][j - request.startY] = pointCalculations;
	    }
	}

	// Allocate a MandelbrotResponse with the calculations
	MandelbrotResponse response = new MandelbrotResponse(calculations);

	System.out.println("calculator calculated array with length: " + response.getData().length);

	return response;
    }
}
