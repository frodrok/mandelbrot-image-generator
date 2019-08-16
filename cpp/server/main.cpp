#include <iostream>
#include <unistd.h> 
#include <stdio.h> 
#include <sys/socket.h> 
#include <stdlib.h> 
#include <netinet/in.h> 
#include <string.h>

#include <typeinfo>
#include <MandelbrotData.h>
#include <MandelbrotCalculator.h>

#include <json.hpp>

using json = nlohmann::json;

using namespace std;

class Server {
  
private:
  int port;
  
public:

  Server(int port1) {
    port = port1;
  }
  
  void start() {
    
    cout << "Starting server on port " << port << "\n";

    int server_fd, new_socket, valread;
    int opt = 1;
    struct sockaddr_in address;
    int addrlen = sizeof(address);
    char buffer[1024] = {0};
    char *hello = "Hello from server";

    // Set up a socket and start a loop that accepts connections
    if ((server_fd = socket(AF_INET, SOCK_STREAM, 0)) == 0) {
      perror("socket creation failed");
      exit(EXIT_FAILURE);
    }

    if (setsockopt(server_fd, SOL_SOCKET, SO_REUSEADDR | SO_REUSEPORT,
		   &opt, sizeof(opt))) {
      perror("setsockopt");
      exit(EXIT_FAILURE);
    }

    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(port);

    if (bind(server_fd, (struct sockaddr *)&address,
	     sizeof(address)) < 0) {
      perror("bind failed");
      exit(EXIT_FAILURE);
    }

    if (listen(server_fd, 3) < 0) {
      perror("listen");
      exit(EXIT_FAILURE);
    }

    int running = 1;
    
    while (running) {

      // Accept new connections, serialize the received
      // string into a MandelbrotRequest and return back
      // a MandelbrotResponse
      if ((new_socket = accept(server_fd, (struct sockaddr*)&address,
			       (socklen_t*)&addrlen)) < 0) {
	perror("accept");
	exit(EXIT_FAILURE);  
      }

      // read(from, to, length)
      valread = read(new_socket, buffer, 1024);
      cout << valread << " bytes read" << "\n";
      cout << " read: " << buffer << '\n';

      MandelbrotRequest* mbRequest;

      try {

	cout << buffer << "\n";

	auto j3 = json::parse(buffer);

	mbRequest = new MandelbrotRequest(j3["start_x"],
					       j3["start_y"],
					       j3["end_x"],
					       j3["end_y"],
					       j3["total_x"],
					       j3["total_y"],
					       j3["max_iter"]);

      } catch (const std::exception& e) {
	cout << "got exception: " << e.what() << "\n";
      }

      // How to print type of variable
      cout << typeid(mbRequest).name() << '\n';

      auto mbResponse = MandelbrotCalculator::calculate(mbRequest);

      auto responseAsString = mbResponse->toJson();

      cout << "response len: " << responseAsString.length() << '\n';

      cout << "toJson type: " << typeid(responseAsString).name() << '\n';

      char cstr[responseAsString.length() + 1];
      responseAsString.copy(cstr, responseAsString.size() + 1);
      cstr[responseAsString.size()] = '\0';

      int messageLength = responseAsString.length();

      // casting an int to a byte[] (unsigned char[])
      //      arrayOfByte[3] = (int >> (0))
      //      arrayOfByte[2] = (int >> (8))
      //      arrayOfByte[1] = (int >> (16))
      //      arrayOfByte[0] = (int >> (24))      


      unsigned char bytes[4] = {0};

      for (int i = 0; i < 4; i++) {
	bytes[3 - i] = (messageLength >> (i * 8));
      } 

      cout << "Sending " << sizeof(bytes) << " bytes\n";
      
      send(new_socket, bytes, sizeof(bytes), 0);

      // Loop and send packets of 60k to the open socket until
      // everything is sent
      int sentBytes = 0;
      int start = 0;
      while (sentBytes < messageLength) {

	// Copy 60k chars from responseAsString to temp array and send it
	char cstr[60000];
	cout << "Copying responseAsString to cstr from " << start << "\n";
	responseAsString.copy(cstr, 60000, start);
	cout << "Sending " << strlen(cstr) << " bytes\n";
	send(new_socket, cstr, strlen(cstr), 0);
	sentBytes += strlen(cstr);
	start += 60000;
      }
      

      // send(to, what, len, 0?)

      cout << "Sent message back\n";      
    }
  }
};
  

int len(char* argv[]) {
  return (sizeof(argv) / sizeof(*argv));
}

int main(int argc, char const *argv[]) {

  if (argc < 2) {
    cout << "Please provide a port to run the server on\n";
    return 1;
  }

  int port = std::stoi(argv[1]);

  auto *server = new Server(port);
  server->start();
    
  return 0;
}
