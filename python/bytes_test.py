import socket

HOST = "localhost"
PORT = 5000

server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

server.bind((HOST, PORT))
server.listen(1)
print("Waiting for Java")

connection, address = server.accept()
print("Java connected")

data = connection.recv(64)
print("Received:", data)
print("Number of bytes:", len(data))

connection.close()
server.close()
