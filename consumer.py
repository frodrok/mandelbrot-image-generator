#!/bin/python3

import socket
import json
from PIL import Image, ImageDraw

HOST = "127.0.0.1"
PORT = 1000

#hosts = [ (HOST, PORT), (HOST, PORT+1), (HOST, PORT+2), (HOST, PORT+3) ]
hosts = [ (HOST, PORT+i) for i in range(0, 4) ]

image_properties = {
	"width": 640,
	"height": 480,
	"parts": 2,
	"max_iter": 160,
	"re_start": -2,
	"re_end": 1,
	"im_start": -1,
	"im_end": 1,
}

im = Image.new('HSV', (image_properties["width"], image_properties["height"]), (0, 0, 0))
draw = ImageDraw.Draw(im)

def send_and_receive(data, host):

	assert isinstance(data, bytes)

	# @TODO: Handle connection failure
	with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:

		s.connect((host[0], host[1]))

		# Send the parameters
		s.sendall(data)

		# Byte buffer to fill with bytes
		full_data = b''

		# First package is how many bytes will be sent for us to expect
		length_bytes = s.recv(5)

		expecting = int.from_bytes(length_bytes, byteorder="big")

		received = 0

		# While we have received less bytes than we're expecting, keep fetching
		while received < expecting:

			data = s.recv(60000)

			print("receiving %s bytes, have %s out of %s" % (len(data), received, expecting))

			received += len(data)
			
			full_data += data

		return full_data

	return None


def split_and_generate(parts_amount, width, height, hosts):

	parts = []

	# @TODO: If parts_amount is not divisible by 2, only split along one axis
	if parts_amount % 2 != 0:
		pass

	one_part_x = width / parts_amount
	one_part_y = height / parts_amount

	# For each part create a json message and send and receive to get data
	generated_parts = 0

	# Explanation:
	# The loop will be multi-dimensional to generate the split image
	# | i=0, j=0     | i=0, j=1    |
	# | i=1, j=0     | i=1, j=1    |
	# 
	# `one_part_x` will be how many pixels of the X axis one part represents,
	# if the total width is 640 and we want 2 parts, `one_part_x` will be 320
	# `start_x` is which X position to start at, i * one_part_x = i * 320 = 0 for the first and so on
	# `end_x` is which X position to end at, (i+1) * one_part_x = (1) * 320 = 320, giving start_x = 0 and end_x = 320
	# for the next part, start_x will be 320 and end_x will be 640
	# same thing for the y-values
	for i in range(0, parts_amount):
		for j in range(0, parts_amount):
				
				start_x = int(i * one_part_x)
				start_y = int(j * one_part_y)

				end_x = int((i + 1) * one_part_x)
				end_y = int((j + 1) * one_part_y)

				json_message = {
						"part_nr": generated_parts,
						"start_x": start_x,
						"start_y": start_y,
						"end_x": end_x,
						"end_y": end_y,
						"total_x": width,
						"total_y": height,
						"re_start": image_properties["re_start"],
						"re_end": image_properties["re_end"],
						"im_start": image_properties["im_start"],
						"im_end": image_properties["im_end"],
						"max_iter": image_properties["max_iter"]
				}

				to_send = json.dumps(json_message).encode('utf-8')

				# Grab a host from our hosts array with index of the part we
				# are generating
				data_received = send_and_receive(to_send, hosts[generated_parts])

				# Store the offset and the data so that we can draw it
				part_data = {}
				part_data["offset"] = [start_x, start_y]
				part_data["data"] = json.loads(data_received.decode('utf-8'))

				parts.append(part_data)

	return parts

parts = split_and_generate(image_properties["parts"], image_properties["width"], image_properties["height"], hosts)

for part in enumerate(parts):

		offset = part["offset"]

		for x_index, xes in enumerate(part["data"]):
				for y_index, calculations in enumerate(xes):

						hue = int(255 * calculations / image_properties["max_iter"])
						saturation = 255
						value = 255 if calculations < image_properties["max_iter"] else 0

						colors = (hue, saturation, value)

						draw.point([x_index + offset[0], y_index + offset[1]], colors)				

# Persist the image buffer to disk
im.convert('RGB').save('output-distributed.png', 'PNG')
