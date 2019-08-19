package data

import (
	"encoding/json"
	"fmt"
)

type MandelbrotResponse struct {
	Data [][]int
}

func (rq *MandelbrotResponse) ToJson() string {
	b, err := json.Marshal(rq)
	if err != nil {
		fmt.Println(err)
		return "err"
	}

	return string(b)
}

func ResponseFromJson(s string) *MandelbrotResponse {
	data := &MandelbrotResponse{}

	err := json.Unmarshal([]byte(s), data)

	if err != nil {
		fmt.Println(err)
		return data
	}

	return data
}
