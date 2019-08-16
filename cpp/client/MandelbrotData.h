#include <vector>
#include <string>
#ifndef MANDELBROTREQUEST_H
#define MANDELBROTREQUEST_H

class MandelbrotRequest {
 public:
  int startX, startY;
  int endX, endY;
  int totalX, totalY;
  int maxIterations;
  
  MandelbrotRequest(int StartX,
		    int StartY,
		    int EndX,
		    int EndY,
		    int TotalX,
		    int TotalY,
		    int MaxIterations);

  std::string toJson();
};

#endif


#ifndef MANDELBROTRESPONSE_H
#define MANDELBROTRESPONSE_H


class MandelbrotResponse {
 public:
  //  int data[50];
  std::vector<std::vector<int>> data;
  
  MandelbrotResponse(std::vector<std::vector<int>> Data);

  std::string toJson();
};

#endif
