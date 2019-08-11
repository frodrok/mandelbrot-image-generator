#include "MandelbrotData.h"
#include <iostream>
#include <vector>
#include <sstream>

using namespace std;

MandelbrotRequest::MandelbrotRequest(int StartX, int StartY,
				     int EndX, int EndY,
				     int TotalX, int TotalY,
				     int MaxIterations) {

  startX = StartX;
  startY = StartY;

  endX = EndX;
  endY = EndY;

  totalX = TotalX;
  totalY = TotalY;
  
  maxIterations = MaxIterations;
}

MandelbrotResponse::MandelbrotResponse(std::vector<std::vector<int>> Data) {
  data = Data;
}

std::string MandelbrotResponse::toJson() {

  // I don't remember the last time I did my own JSON
  // serializing. Does c++ really not have reflection?
  
  std::stringstream ss;
  ss << "{\"data\": [";

  for (int i = 0; i < data.size(); i++) {
    std::vector<int> yValues = data.at(i);
    ss << "[";
    for (int j = 0; j < yValues.size(); j++) {
      
      int calculations = yValues.at(j);
      ss << std::to_string(calculations) + ',';
    }
    ss << "],";
  }

  ss << "]}";

  return ss.str();
}
