package main

import (
	"bufio"
	"data"
	"fmt"
	"image"
	"image/color"
	"image/png"
	"net"
	"os"
	"strconv"
	"time"
)

func sendAndReceive(datas []byte, host string) (string, error) {

	conn, err := net.Dial("tcp", host)

	if err != nil {
		return "", err
	}

	//	asJson := mbRequest.ToJson()

	fmt.Println("Sending len ", len(string(datas)), " to ", host)
	fmt.Fprintf(conn, string(datas))

	message, err := bufio.NewReader(conn).ReadString('\n')

	return message, err

	/* 	var mbResponse []byte = data.ResponseFromJson(message)

	fmt.Println(len(mbResponse.Data))

	fmt.Print("Message from server: " + message) */
}

type Point struct {
	x int
	y int
}

type ServerResult struct {
	p     Point
	mbRes *data.MandelbrotResponse
}

func main() {

	fmt.Println("Starting client")

	partsAmount, _ := strconv.Atoi(os.Args[1])
	imageWidth, _ := strconv.Atoi(os.Args[2])
	imageHeight, _ := strconv.Atoi(os.Args[3])
	maxIterations, _ := strconv.Atoi(os.Args[4])
	imageName := os.Args[5]

	m := image.NewRGBA(image.Rect(0, 0, imageWidth, imageHeight))

	hosts := []string{"127.0.0.1:1000", "127.0.0.1:1001", "127.0.0.1:1002",
		"127.0.0.1:1003"}
	//	hosts := []string{"127.0.0.1:1000"}

	var totalData map[Point]*data.MandelbrotResponse = make(map[Point]*data.MandelbrotResponse)
	results := make(chan ServerResult)
	done := make(chan bool)

	// Sequential execution ~0.675s
	// How would I use goroutines? Create a function that fetches data from
	// the server and puts the result on a channel and then have
	// a goroutine that listens to the channel and put the data into `totalData`?

	generatedParts := 0

	// Measure the time spent in each part
	var startTime int64 = time.Now().UnixNano() / int64(time.Millisecond)

	for i := 0; i < partsAmount; i++ {
		for j := 0; j < partsAmount; j++ {

			// Start a goroutine that gets the data from the server
			// and puts the result on the results channel
			go func(results chan ServerResult, generatedParts int, xPart int, yPart int) {

				onePartX := imageWidth / partsAmount
				onePartY := imageHeight / partsAmount

				startX := xPart * onePartX
				startY := yPart * onePartY

				endX := (i + 1) * onePartX
				endY := (j + 1) * onePartY

				mbRequest := &data.MandelbrotRequest{
					PartNo:        generatedParts,
					StartX:        startX,
					StartY:        startY,
					EndX:          endX,
					EndY:          endY,
					TotalX:        imageHeight,
					TotalY:        imageWidth,
					MaxIterations: maxIterations,
				}

				asBytes := []byte(mbRequest.ToJson() + "\n")

				dataReceived, err := sendAndReceive(asBytes, hosts[generatedParts])

				if err != nil {
					fmt.Println("Could not get data from the server")
				}

				aPoint := Point{x: mbRequest.StartX, y: mbRequest.StartY}

				mbResponse := data.ResponseFromJson(dataReceived)

				results <- ServerResult{aPoint, mbResponse}

			}(results, generatedParts, i, j)

			generatedParts += 1

		}
	}

	var afterSendingTime int64 = (time.Now().UnixNano() / int64(time.Millisecond)) - startTime

	// Start a goroutine that collects the results from the results channel
	// and draws the result to the image
	go func(results chan ServerResult, totalData map[Point]*data.MandelbrotResponse, done chan bool) {

		totalResultsAmount := 0
		for totalResultsAmount < 4 {
			var result ServerResult = <-results

			// Translate calculations to pixel colors and draw on the image buffer
			offsetPoint := result.p
			mbResponse := result.mbRes

			theData := mbResponse.Data

			for i := 0; i < len(theData); i++ {
				theYes := theData[i]

				for j := 0; j < len(theYes); j++ {
					calculations := uint8(theYes[j])

					color := color.RGBA{uint8(calculations),
						uint8(255 - calculations),
						uint8(255 - calculations),
						uint8(255)}

					m.Set(i+offsetPoint.x, j+offsetPoint.y, color)
				}
			}

			totalResultsAmount++
		}

		done <- true

	}(results, totalData, done)

	// Wait for all goroutines to finish
	<-done
	var afterCollectingTime int64 = (time.Now().UnixNano() / int64(time.Millisecond)) - startTime

	// Write the image buffer to disk
	f, err := os.Create(imageName)

	if err != nil {
		panic(err)
	}

	defer f.Close()
	png.Encode(f, m)

	var afterWritingfileTime int64 = (time.Now().UnixNano() / int64(time.Millisecond)) - startTime

	fmt.Println("starttime: ", startTime)
	fmt.Println("after sending time: ", afterSendingTime)
	fmt.Println("after collecting time: ", afterCollectingTime)
	fmt.Println("after writing time: ", afterWritingfileTime)

}
