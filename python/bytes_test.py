import socket
import sys

HOST = "localhost"
PORT = 5000

def exact(connection, size):
    data = bytearray()

    while len(data) < size:
        piece = connection.recv(size - len(data))

        if not piece:
            raise ConnectionError("Connection is closed before receiving all 64 bytes")

        data.extend(piece)
        
    return bytes(data)

server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

server.bind((HOST, PORT))
server.listen(1)
print("Waiting for Java")

connection, address = server.accept()
print("Java connected")

connection.setsockopt(socket.IPPROTO_TCP, socket.TCP_NODELAY, 1)

expect = bytes([
    4, 2, 3, 5, 6, 3, 2, 4,
    1, 1, 1, 1, 1, 1, 1, 1,
    0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0,
    255, 255, 255, 255, 255, 255, 255, 255,
    252, 254, 253, 251, 250, 253, 254, 252
])
transfers = int(sys.argv[1])
board_size = 64

success = 0
for i in range(transfers):

    data = exact(connection, board_size)

    if data == expect:
        success += 1

    connection.sendall(b"OK")

print("Total Transfers:", transfers)
print("Successful Transfers:", success)
print("Boards Verified:", success == transfers)

connection.close()
server.close()
