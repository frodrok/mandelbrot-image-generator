#include <iostream>

#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>

#include "MandelbrotData.h"

#include <string.h>
#include <unistd.h>
#include <netinet/in.h>
#include <cstring>
#include <cassert>

#define lengthof(a) (sizeof(a) / sizeof(a[0]))

using namespace std;

void error(const char *msg) {
  perror(msg);
  exit(0);
}

int main(int argc, char *argv[]) {
  int sockfd, portno, n;
  struct sockaddr_in serv_addr;
  struct hostent *server;

  char buffer[256];

  if (argc < 3) {
    fprintf(stderr, "usage %s hostname port\n", argv[0]);
    exit(0);
  }

  portno = atoi("1000");

  sockfd = socket(AF_INET, SOCK_STREAM, 0);
  if (sockfd < 0) {
    error("ERROR OPENING SOCKET");
  }

  // Convert ip address to binary form
  if (inet_pton(AF_INET, "127.0.0.1", &serv_addr.sin_addr) <= 0) { error("could not canverrt"); }

  serv_addr.sin_port = htons(portno);
  serv_addr.sin_family = AF_INET;

  cout << "Connecting to server on port " << portno << "\n";
  if (connect(sockfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0) {
    error("error connecting");
  }

  // Serliaze a MandelbrotRequest to json and sent it to the server
  auto mbRequest = new MandelbrotRequest(0, 0, 640, 480, 640, 480, 255);

  string toSend = mbRequest->toJson();

  bzero(buffer, 256);

  strcpy(buffer, toSend.c_str());

  cout << "Sending data " << toSend << "\n";
  
  n = send(sockfd, buffer, strlen(buffer), 0);

  if (n < 0) { error("error writing to socket"); }

  bzero(buffer, 256);

  // First read a 4 byte message containing the length
  unsigned char lengthBuffer[4] = {0};
  
  n = read(sockfd, lengthBuffer, sizeof(lengthBuffer));

  if (n < 0) { error("error reading from socket"); }

  // Convert from 4 bytes to int
  int total = 0;
  for (int i = 0; i < sizeof(lengthBuffer); i++) {
    //    int value = static_cast<int>(lengthBuffer[i]);

    int temp = (lengthBuffer[3 - i] << (i * 8));
    total += temp;
  }

  cout << "Reading " << total << " bytes from the server\n";

  // Read packets of 60k bytes and store in the totalChars
  // array until we have read it all
  char totalChars[total];
  cout << "Allocated " << sizeof(totalChars) << " char array\n";
  int readBytes = 0;
  int start = 0;

  while (readBytes < total) {
    char cstr[60000];
    int iRead = read(sockfd, cstr, sizeof(cstr));
    cout << "Read " << iRead << " [" << readBytes << "] out of " << total << "\n";
    if (iRead < 1) {
      cout << "Server sent no bytes, breaking\n" ;
      break;
    } else {
      // Copy cstr into totalChars
      cout << "Starting copying from " << start << " to " << start+iRead << "\n";
      /*       for (int i = 0; i < iRead; i++) {
      	cout << "at " << i << " out of " << iRead << "\n";
      	totalChars[start + i] = cstr[i];
	} */

      // Problem site: when filling totalChars with
      // the temporary cstr, count goes up and down
      // between 33589-33771 and thus never exiting the loop.
      // Same problem in the for loop above.
      int count = 0;
      while (count < iRead) {
	totalChars[start + count] = cstr[count];
	cout << "at " << count << " out of " << iRead << "\n";
	count++;
      }
      
      cout << "Done copying\n";
      
    }
    
    start += iRead;
    readBytes += iRead;
  }

  string responseAsString(totalChars);

  cout << responseAsString.length() << "\n";

  close(sockfd);

  return 0;
  
}
