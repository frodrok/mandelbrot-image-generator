use std::io::prelude::*;
use std::net::{TcpListener, TcpStream};
use std::io::BufReader;
use byteorder::{ByteOrder, LittleEndian};
use std::str::from_utf8;

#[macro_use]
extern crate json;

fn perform_calculations(mb_request: json::JsonValue) -> std::vec::Vec<std::vec::Vec<i32>> {

    println!("{}", mb_request["start_x"]);
    
    let start_x = &mb_request["start_x"].as_number().unwrap().into();
    let start_y = &mb_request["start_y"].as_number().unwrap().into();

    let end_x = &mb_request["end_x"].as_number().unwrap().into();
    let end_y = &mb_request["end_y"].as_number().unwrap().into();

    println!("{}", start_x);

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

    println!("writing {} to tcp stream", str);

    let mut len = [0u8; 4];
    let bytes = str.as_bytes();
    LittleEndian::write_u32(&mut len, str.len() as u32);

    println!("writing length");
    stream.write(&len);
    //println!("writing bytes");
    stream.write(&bytes);
    
    return Err("no faces done");
}

fn filter_read_string(str: &String) -> String {

    let mut count: i32 = 0;
    let mut result = String::new();

    for charrer in str.chars() {
        println!("{}: {} charrer: {}", count, charrer, charrer.is_whitespace());

        count = count + 1;
    }

    result.push('h');
        
    return result;
}

fn as_u32_le(array: &[u8;4]) -> u32 {
    ((array[0] as u32) << 0) +
    ((array[1] as u32) << 8) +
    ((array[2] as u32) << 16) +
    ((array[3] as u32) << 24)
}

fn handle_connection(mut stream: TcpStream) {
    
    let mut len_buf = [0 as u8; 4];

    match stream.read_exact(&mut len_buf) {
        Ok(_) => {
            let length = as_u32_le(&len_buf);
            /* let mut temp_data = vec![0u8; length as usize];
            stream.read_exact(&mut temp_data).unwrap(); */

            let mut temp_data = vec![0u8; length as usize];
            stream.read_exact(&mut temp_data).unwrap();

            let text: String = from_utf8(&temp_data).unwrap().to_string();
            println!("received {}", text);

            let mb_request = json::parse(&text);
            
            match mb_request {
                Ok(value) => {
                    println!("{} value", value);
                    let calculation_data = perform_calculations(value);
                    
                    let instantiated = object!{
                        "data" => 1
                    };
                    
                    write_string(stream, instantiated.dump());
                }
                Err(e) => println!("could not parse json")
            }
            
        },
        Err(e) => println!("Could not read length bytes")
    }
    
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
}
