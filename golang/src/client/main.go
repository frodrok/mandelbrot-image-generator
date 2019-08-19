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

func main() {

	fmt.Printf("Starting client")

	partsAmount, _ := strconv.Atoi(os.Args[1])
	imageWidth, _ := strconv.Atoi(os.Args[2])
	imageHeight, _ := strconv.Atoi(os.Args[3])
	maxIterations, _ := strconv.Atoi(os.Args[4])
	imageName := os.Args[5]

	m := image.NewRGBA(image.Rect(0, 0, imageWidth, imageHeight))

	hosts := []string{"127.0.0.1:1000", "127.0.0.1:1001", "127.0.0.1:1002",
		"127.0.0.1:1003"}
	//	hosts := []string{"127.0.0.1:1000"}

	fmt.Println(partsAmount, imageWidth, imageHeight, maxIterations, imageName)
	fmt.Println(hosts)

	var totalData map[Point]*data.MandelbrotResponse = make(map[Point]*data.MandelbrotResponse)

	generatedParts := 0

	for i := 0; i < partsAmount; i++ {
		for j := 0; j < partsAmount; j++ {

			onePartX := imageWidth / partsAmount
			onePartY := imageHeight / partsAmount

			startX := i * onePartX
			startY := j * onePartY

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
				fmt.Println(err)
			}

			punno := Point{x: mbRequest.StartX, y: mbRequest.StartY}

			mbResponse := data.ResponseFromJson(dataReceived)

			totalData[punno] = mbResponse

			generatedParts += 1

		}
	}

	fmt.Println(len(totalData))

	for offsetPoint, mbResponse := range totalData {

		theData := mbResponse.Data

		fmt.Println(offsetPoint, len(theData), len(theData[0]))

		for i := 0; i < len(theData); i++ {
			theYes := theData[i]

			for j := 0; j < len(theYes); j++ {
				calculations := uint8(theYes[j])
				//		color := color.RGBA{calculations, calculations, calculations, calculations}
				color := color.RGBA{uint8(calculations),
					uint8(255 - calculations),
					uint8(255 - calculations),
					uint8(255)}
				m.Set(i+offsetPoint.x, j+offsetPoint.y, color)
			}
		}
	}

	f, err := os.Create(imageName)

	if err != nil {
		panic(err)
	}

	defer f.Close()
	png.Encode(f, m)

}
