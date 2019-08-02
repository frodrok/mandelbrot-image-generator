# Distributed mandelbrot image generator

This is an example on using distributed programming to calculate and generate a mandelbrot image.
https://simple.wikipedia.org/wiki/Mandelbrot_set

### The process:
    * Each server listens on a specified port for a JSON messages containing parameters shown below
    * The consumer uses hard coded values for image properties and calculations properties
      to split the width and height into parts that it sends off to each server
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
		"re_start" - some parameter determining how the picture is generated, int
		"re_end" - some parameter determining how the picture is generated, int
		"im_start" - some parameter determining how the picture is generated, int
		"im_end" - some parameter determining how the picture is generated, int
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
    * Read image properties and calculation properties as arguments to the consumer
    * Read wanted resulting filename as arguments to the consumer
    * Handle connection and data serialization failures
    * Use a queue or other middle man to handle data passing instead of direct TCP
    * Implement an algorithm to choose hosts based on connectivity and usage instead of depending on which part we're generating
    * Implement the project in different languages: Java, Scala, Go, Rust and C++
    
   
### Running it (tested on linux)

```bash
bash start-4-servers.sh	
python3 consumer.py
```
   
    

