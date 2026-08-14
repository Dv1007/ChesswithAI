import socket

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

server.bind((HOST, PORT))
server.listen(1)
print("Waiting for Java")

connection, address = server.accept()
print("Java connected")

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
transfers = 100
board_size = 64

for i in range(transfers):

    data = exact(connection, board_size)

    print("Transfer: ", i + 1, "Board length:", len(data), "Check Board:", data == expect)

    connection.sendall(b"OK")

connection.close()
server.close()
