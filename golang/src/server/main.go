package main

import (
	//	"bufio"
	"fmt"
	"net"
	//"os"
	//"strings"
	"bytes"
	"data"
	"os"
)

type ClientManager struct {
	clients    map[*Client]bool
	broadcast  chan []byte
	register   chan *Client
	unregister chan *Client
}

type Client struct {
	socket net.Conn
	data   chan []byte
}

func (manager *ClientManager) start() {

	// Somehow messages from channels end up here
	// Infinite loop?
	for {
		// Pattern matching?
		select {

		// When a new client connects, the loop in
		// startServerMode adds a client to the manager.register channel
		case connection := <-manager.register:
			manager.clients[connection] = true
			fmt.Println("Added new connection!")

		// When a client disconnects or we couldn't read their
		// message we send the client-struct to the unregister channel
		case connection := <-manager.unregister:
			if _, ok := manager.clients[connection]; ok {
				close(connection.data)
				delete(manager.clients, connection)
				fmt.Println("A connection has terminated")
			}

		// Our receive function receives text from the connection
		// and sends a message to the manager.broadcast channel
		// so we listen for that and sent the message to all
		// connected clients
		case message := <-manager.broadcast:
			for connection := range manager.clients {
				select {
				case connection.data <- message:
				default:
					close(connection.data)
					delete(manager.clients, connection)
				}
			}
		}
	}
}

func (manager *ClientManager) receive(client *Client) {

	for {
		message := make([]byte, 2048)

		length, err := client.socket.Read(message)

		if err != nil {
			manager.unregister <- client
			client.socket.Close()
			break
		}

		fmt.Printf("Received %d length message\n", length)

		if length > 0 {

			// Parse message into a mandelbrotRequest
			// do calculations and return back
			// a mandelbrotResponse as json
			b := bytes.Trim(message, "\x00")

			mbRequest, err := data.RequestFromJson(b)

			if err != nil {
				manager.broadcast <- []byte("Could not parse json")
			} else {

				mbResponse := calculate(mbRequest)

				toSend := mbResponse.ToJson() + "\n"

				fmt.Println("RECEIVED: " + string(message))
				manager.broadcast <- []byte(toSend)
			}
		}
	}

}

func (manager *ClientManager) send(client *Client) {
	defer client.socket.Close()
	for {
		select {
		case message, ok := <-client.data:
			if !ok {
				return
			}
			client.socket.Write(message)
		}
	}
}

func startServerMode(portNo string) {
	fmt.Println("Starting server on port ", portNo)

	// Start a tcp listener on port 1000
	portString := ":" + portNo
	listener, error := net.Listen("tcp", portString)

	if error != nil {
		fmt.Println(error)
	}

	// Initialize our clientmanager struct that has
	// channels for messages
	manager := ClientManager{
		clients:    make(map[*Client]bool),
		broadcast:  make(chan []byte),
		register:   make(chan *Client),
		unregister: make(chan *Client),
	}

	go manager.start()

	// Infinite loop is just a `for {}` block
	for {
		// When a new connection comes in, get one here
		connection, _ := listener.Accept()

		if error != nil {
			fmt.Println(error)
		}

		// Set up our client struct
		client := &Client{socket: connection, data: make(chan []byte)}

		// Send the client to our manager.register channel
		manager.register <- client

		// Start two goroutines that send and receive
		go manager.receive(client)
		go manager.send(client)

	}
}

// Flow:
// startServerMode listens for connections,
// when a connection comes in we register it
// when a client sends us a message we broadcast it
// to all connected clients

func main() {
	portNo := os.Args[1]
	startServerMode(portNo)
}
