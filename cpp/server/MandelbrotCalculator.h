#include "MandelbrotData.h"
#include <iostream>
#include <vector>

#include <complex>

using namespace std;

class MandelbrotCalculator {
 public:

  static int getCalculation(int x, int y, int totalX, int totalY, int maxIterations) {

    complex<float> point((float) x / totalX -1.5, (float) y / totalY -0.5);
    // we divide by the image dimensions to get values smaller than 1
    // then apply a translation
    complex<float> z(0, 0);
    unsigned int nb_iter = 0;
    
    while (abs (z) < 2 && nb_iter <= maxIterations) {
           z = z * z + point;
           nb_iter++;
    }
    return nb_iter;
  }
  
  static MandelbrotResponse* calculate(MandelbrotRequest* request) {

    //    MandelbrotRequest heyhey = &request;
    //std::vector<int> yValues;
    //yValues.reserve(request->endY - request->startY);
    
    


    // X from 0 to request->endX
    // from 0 to request->endX

    std::vector<vector<int>> xValues;
    xValues.reserve(request->endX - request->startX);
    
    for (int xIndex = 0, xStart = request->startX; xStart < request->endX; xIndex++, xStart++) {
      
      std::vector<int> yValues;
      yValues.reserve(request->endY - request->startY);
      
      for (int yIndex = 0, yStart = request->startY ; yStart < request->endY; yIndex++, yStart++) {

	yValues.push_back(getCalculation(xStart,
					 yStart,
					 request->totalX,
					 request->totalY,
					 request->maxIterations));
			  
      }


      //      xValues[xIndex] = yValues;
      xValues.push_back(yValues);
    }

	      
    //    return xValues;

    auto mbResponse = new MandelbrotResponse(xValues);
    return mbResponse;
  }
};
