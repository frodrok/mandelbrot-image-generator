use std::net::{TcpStream};
use std::io::{Read, Write};
use std::str::from_utf8;
use byteorder::{ByteOrder, LittleEndian};

#[macro_use]
extern crate json;

fn as_u32_le(array: &[u8;4]) -> u32 {
    ((array[0] as u32) << 0) +
    ((array[1] as u32) << 8) +
    ((array[2] as u32) << 16) +
    ((array[3] as u32) << 24)
        
}

fn write_string(mut stream: &TcpStream, str: String) -> Result<bool, &'static str> {
    let mut len = [0u8; 4];
    let bytes = str.as_bytes();
    LittleEndian::write_u32(&mut len, str.len() as u32);
    stream.write(&len);
    stream.write(&bytes);

    return Err("no faces done");
}


fn main() {

    println!("Starting client..");

    let mut stream = TcpStream::connect("localhost:1000").unwrap();

    println!("Client connected");

    let mbRequest = object!{
        "start_x" => 0,
        "start_y" => 0,
        "end_x" => 640,
        "end_y" => 480,
        "total_x" => 640,
        "total_y" => 480,
        "max_iter" => 255,
    };
    
    let mbRequest_as_string = mbRequest.dump();
    
    let mbRequest_as_bytes = mbRequest_as_string.as_bytes();
    
    write_string(&stream, mbRequest_as_string);

    let mut data = [0 as u8; 4];

    stream.read_exact(&mut data).unwrap();

    let length = as_u32_le(&data);
    
    let mut temp_data = vec![0u8; length as usize];
    stream.read_exact(&mut temp_data).unwrap();

    let text: String = from_utf8(&temp_data).unwrap().to_string();
    println!("{} text", text);

}
