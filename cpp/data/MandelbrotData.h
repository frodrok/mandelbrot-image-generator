#ifndef MANDELBROTREQUEST_H
#define MANDELBROTREQUEST_H



class MandelbrotRequest {
 public:
  int startX, startY;
  int endX, endY;
  int totalX, totalY;
  int maxIterations;
  
  MandelbrotRequest(int startX,
		    int startY,
		    int endX,
		    int endY,
		    int totalX,
		    int totalY,
		    int maxIterations);
};

#endif


#ifndef MANDELBROTRESPONSE_H
#define MANDELBROTRESPONSE_H


class MandelbrotResponse {
 public:
  int data[50];
  
  MandelbrotResponse(int data[50]);
};

#endif
