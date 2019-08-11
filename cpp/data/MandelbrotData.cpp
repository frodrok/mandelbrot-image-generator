#include "MandelbrotData.h"
#include <iostream>

using namespace std;

MandelbrotRequest::MandelbrotRequest(int startX, int startY,
				     int endX, int endY,
				     int totalX, int totalY,
				     int maxIterations) {
  startX = startX;
  startY = startY;

  endX = endX;
  endY = endY;

  totalX = totalX;
  totalY = totalY;

  maxIterations = maxIterations;
}

MandelbrotResponse::MandelbrotResponse(int data[50]) {
  data = data;
}
