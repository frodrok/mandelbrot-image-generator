package com.fhdev.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/* Json representation of a request to generate a
   part of a mandelbrot image */
public class MandelbrotRequest {

    @JsonProperty(value="part_nr")
    public Integer partNumber;

    @JsonProperty(value="start_x")
    public Integer startX;

    @JsonProperty(value="start_y")
    public Integer startY;

    @JsonProperty(value="end_x")
    public Integer endX;

    @JsonProperty(value="end_y")
    public Integer endY;

    @JsonProperty(value="total_x")
    public Integer totalX;

    @JsonProperty(value="total_y")
    public Integer totalY;

    @JsonProperty(value="re_start")
    public Integer reStart;

    @JsonProperty(value="re_end")
    public Integer reEnd;

    @JsonProperty(value="im_start")
    public Integer imStart;

    @JsonProperty(value="im_end")
    public Integer imEnd;

    @JsonProperty(value="max_iter")
    public Integer maxIter;
    
    private MandelbrotRequest() {
    }

    public MandelbrotRequest(int partNumber, int startX, int startY,
				int endX, int endY,
				int totalX, int totalY,
				int reStart, int reEnd,
				int imStart, int imEnd,
				int maxIter) {

	this.partNumber = partNumber;
	
	this.startX = startX;
	this.startY = startY;
	
	this.endX = endX;
	this.endY = endY;
	
	this.totalX = totalX;
	this.totalY = totalY;
	
	this.reStart = reStart;
	this.reEnd = reEnd;
	
	this.imStart = imStart;
	this.imEnd = imEnd;

	this.maxIter = maxIter;
	    
    }

    public String toString() {
	//	return this.getClass().toString();
	return "{MandelbrotRequest: startX=" + this.startX +
	    ", startY=" + this.startY +
	    ", endX=" + this.endX +
	    ", endY=" + this.endY +
	    ", totalX=" + this.totalX +
	    ", totalY=" + this.totalY +
	    ", reStart=" + this.reStart +
	    ", reEnd=" + this.reEnd +
	    ", imStart=" + this.imStart +
	    ", imEnd=" + this.imEnd +
	    ", maxIter=" + this.maxIter + "}";
	    
    }

    
}

