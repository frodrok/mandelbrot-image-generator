package com.fhdev.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class MandelbrotResponse {

    @JsonProperty
    private final Integer[][] data;

    private MandelbrotResponse() {
	data = new Integer[0][0];
    }

    public MandelbrotResponse(Integer[][] data) {
	this.data = data;
    }

    public Integer[][] getData() {
	return this.data;
    }
}
