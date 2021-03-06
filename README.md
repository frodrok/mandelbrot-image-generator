# Distributed mandelbrot image generator

This is an example on using distributed programming to calculate and generate a mandelbrot image.
https://simple.wikipedia.org/wiki/Mandelbrot_set

### The process:
    * Each server listens on a specified port for a JSON messages containing parameters shown below
    * The consumer uses values from the command line for image- and calculation properties to split the width and height into parts that it sends off to each server
    * The server receives the parameters and returns back a JSON message containing a two-dimensional array of [(x=0)[calculations of y values]], (x=1)[calculations of y values]] representing x and y values
    * The consumer keeps track of which part it received and stores that into a list of parts
    * Finally the consumer join the parts and translate the calculation data for a pixel into a color and draw's it on the canvas and saves to a file
    

### JSON message structure:
    json_message = {
	   	"part_nr" - index of the part we're generating, optional int
		"start_x" - which x pixel to start generating from, int
		"start_y" - which y pixel to start generating from, int
		"end_x" - which x pixel to stop generating at, int
		"end_y" - which y pixel to stop generating at, int
		"total_x" - total width of the image, int
		"total_y" - total height of the image, int
		"re_start" - alias for real_start, determines the scale of the picture, int
		"re_end" - alias for real_end, same as above int
		"im_start" - alias for imaginary_start, same as above int
		"im_end" - alias for imaginary_end, same as above, int
		"max_iter" - a parameter specifying how many iterations we will allow before we exit, int
    }
    
### Result
![alt text](https://github.com/frodrok/mandelbrot-image-generator/raw/master/result/output-distributed.png "The result")

### Mandelbrot explanation
The mandelbrot equation can be seen on wikipedia but what it means for us in this project is that for each point in a grid we can calculate the amount of iterations it take us to reach the number 2 for a complex number. The point is that it's computationally expensive and gives us a reason to make the process parallell.

The code for the calculation:
```python
first_number = RE_START + (current_x / total_x) * (RE_END - RE_START)
second_number = IM_START + (current_y / total_y) * (IM_END - IM_START)

c = complex(first_number, second_number)

while abs(z) <= 2 and n < MAX_ITER:
	z = z*z + c
	n += 1
	
return n

```

### Going forward:
    * Read hosts from a file on disk or database
    * Handle connection and data serialization failures
    * Use a queue or other middle man to handle data passing instead of direct TCP
    * Implement an algorithm to choose hosts based on connectivity and usage instead of depending on which part we're generating
    * Implement the project in different languages: Java, Scala, Go, Rust, C++ and erlang
    * Create a front-end to send and receive data with web sockets
    * Add kubernetes and helm charts for deployment
    * Add build scripts and static code analysis

### Progress
- [x] Python
- [x] Java
- [x] Scala
- [X] Go
- [ ] Rust
- [ ] C++
- [ ] Erlang
- [ ] Front-end
- [ ] Deployment

### Performance
Generating a 1920x1080 picture with 255 max iterations
* Python sequential execution: 21.051s
* Python parallel execution: 9.336s
* Java: 1.909s    
* Scala sequential execution: 3.716s
* Scala parallell execution: Between 3.085s and 4.161s
* Golang sequential execution: 0.673s
* Golang parallel execution: 1.222s (unsure why, maybe there's some extra polling in regards to getting results from channels)
   
Each subproject has instructions on how to run it.
