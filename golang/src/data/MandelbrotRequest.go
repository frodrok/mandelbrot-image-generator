package data

import (
	"encoding/json"
	"fmt"
)

type MandelbrotRequest struct {
	StartX        int
	StartY        int
	EndX          int
	EndY          int
	TotalX        int
	TotalY        int
	MaxIterations int
	PartNo        int
}

func (rq *MandelbrotRequest) ToJson() string {
	b, err := json.Marshal(rq)

	if err != nil {
		fmt.Println(err)
		return "err"
	}

	return string(b)
}

func RequestFromJson(s []byte) (f *MandelbrotRequest, err error) {
	mbRequest := &MandelbrotRequest{}

	err0r := json.Unmarshal(s, mbRequest)

	if err0r != nil {
		fmt.Println(err)
		return mbRequest, err0r
	}

	return mbRequest, err0r
}
