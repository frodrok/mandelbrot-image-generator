package main

import (
	"data"
	"fmt"
	//	"math"
)

func getCalculations(x int, y int, totalX int, totalY int, maxIterations int) (int, float64) {
	reEnd := float64(1)
	reStart := float64(-2)
	imEnd := float64(1)
	imStart := float64(-1)

	ca := reStart + (float64(x)/float64(totalX))*(reEnd-reStart)
	cb := imStart + (float64(y)/float64(totalY))*(imEnd-imStart)

	var a, b float64 = 0, 0
	for i := 0; i < maxIterations; i++ {
		as, bs := a*a, b*b
		if as+bs > 2 {
			return i, as + bs
		}

		a, b = as-bs+ca, 2*a*b+cb
	}
	return maxIterations, a*a + b*b
}

func calculate(mbR *data.MandelbrotRequest) *data.MandelbrotResponse {

	fmt.Println("calculate received mbr with endX: ", mbR.EndX)

	xAmount := mbR.EndX - mbR.StartX
	yAmount := mbR.EndY - mbR.StartY

	xInts := make([][]int, xAmount)
	fmt.Println("Allocating xInts with len ", len(xInts))
	//
	for i := mbR.StartX; i < mbR.EndX; i++ {

		yInts := make([]int, yAmount)

		for j := mbR.StartY; j < mbR.EndY; j++ {
			calculations, _ := getCalculations(i, j, mbR.TotalX, mbR.TotalY, mbR.MaxIterations)
			//			value := float64(mbR.MaxIterations-calculations) + math.Log(norm)
			yInts[j-mbR.StartY] = calculations
		}
		xInts[i-mbR.StartX] = yInts
	}

	return &data.MandelbrotResponse{
		Data: xInts,
	}
}
