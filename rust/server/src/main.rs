use std::io::prelude::*;
use std::net::{TcpListener, TcpStream};
use std::io::BufReader;
use byteorder::{ByteOrder, LittleEndian};

#[macro_use]
extern crate json;

fn handle_client(stream: TcpStream) -> &'static str {
    // ...
    println!("handle_client");

    // let mut reader = BufReader::new(stream);

    // let mut line = String::new();
    // let len = reader.read_line(&mut line);
    
    // println!("First line is {} bytes long", len);

    // match len {
    //     Ok(data) => println!("wutfacé {:?}", data),
    //     Err(e) => println!("errrror {:?}", e)
    // }

    

    return "heyhey";
}


fn perform_calculations(mb_request: json::JsonValue) -> std::vec::Vec<std::vec::Vec<i32>> {

    println!("{}", mb_request["start_x"]);
    
    let start_x = &mb_request["start_x"].as_number().unwrap().into();
    let start_y = &mb_request["start_y"].as_number().unwrap().into();

    let end_x = &mb_request["end_x"].as_number().unwrap().into();
    let end_y = &mb_request["end_y"].as_number().unwrap().into();

    // println!("{}", start_x.is_number());

    //let start_x_as_number = &mb_request["start_x"].as_number().unwrap();

    println!("{}", start_x);

    /* match start_x_as_number {
        Some(number) => println!("{}", number),
        None => println!("none")
    } */
    // println!("{}", start_x.as_number());

    /* This is what I would like to do,
    grab a value from json and iterate from one to the other
    for x in start_x..end_x {
        for y in start_y..end_y {
            println!("{}, {}", x, y);
        }
    } */
    let mut x_count: i32 = *start_x;
    let mut y_count: i32 = *start_y;
    
    let x_max: i32 = *end_x;
    let y_max: i32 = *end_y;

    let mut x_array: std::vec::Vec<std::vec::Vec<i32>> = Vec::with_capacity(x_max as usize);
    //let mut y_array: std::vec::Vec<i32> = Vec::with_capacity(y_max as usize);

    //let y_array: [i32; y_max];
    //let x_array: [i32; x_max];

    while x_count < x_max {
        
        //println!("{}", x_count);
        let mut y_array: std::vec::Vec<i32> = Vec::with_capacity(y_max as usize);
        
        while y_count < y_max {
            y_array.push(y_count);
            y_count = y_count + 1;
        }

        x_array.push(y_array);
        
        x_count = x_count + 1;
    }

    println!("{}", x_array.len());

    /* let obj = object!{
        "data" => x_array,
    }; */

    return x_array;
    
}

fn write_string(mut stream: TcpStream, str: String) -> Result<bool, &'static str> {
    return Err("no faces done");
}

fn handle_connection(mut stream: TcpStream) {
    let mut buffer = [0; 512];

    stream.read(&mut buffer).unwrap();

    println!("request: {}", String::from_utf8_lossy(&buffer[..]));

//    let mb_request = json::parse(String::from_utf8_lossy(&buffer[..]));

    let parsed = json::parse(r#"
{"start_x": 0,
"start_y": 0,
"end_x": 640,
"end_y": 480,
"total_x": 640,
"total_y": 480,
"max_iterations": 255
}
"#);

    match parsed {
        Ok(object) => {
            println!("{}", object.to_string());
            let calculation_data: std::vec::Vec<std::vec::Vec<i32>> = perform_calculations(object);
            /* Construct a {
            "data" => ·calculation data
            } json object and send it back as a string */
            let instantiated = object!{
    "code" => 200,
    "success" => true,
    "payload" => object!{
        "features" => array![
            "awesome",
            "easyAPI",
            "lowLearningCurve"
        ]
    }
            };

            let wut: String = instantiated.dump();

            /* Write string to tcp stream */
            let res = write_string(stream, wut); 
        },
        Err(e) => println!("Could not parse json {}", e)
    }
    
    //println!("{}", parsed.to_string());
}

fn main() {
    println!("Starting server on port 1000...");

    let listener = TcpListener::bind("127.0.0.1:1000").unwrap();
    
    let running = true;

    for stream in listener.incoming() {
        let stream = stream.unwrap();
        println!("Connection established");
        handle_connection(stream);
            
    }
    
    // accept connections and process them serially
   
    

    //Ok(());
}
