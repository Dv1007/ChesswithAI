import sys
import mmap
import os

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
success = 0

project_root = os.path.dirname(os.path.abspath(__file__))
shared_memory_path = os.path.join(project_root, "shared_memory.bin")

with open(shared_memory_path, "r+b") as file:
    memory = mmap.mmap(file.fileno(), 65)

    board_view = memoryview(memory)[1:65]

    for i in range(transfers):

        while memory[0:1] != b'\x01':
            pass

        if board_view == expect:
            success += 1

        memory[0] = 2

print("Total Transfers:", transfers)
print("Successful Transfers:", success)
print("Boards Verified:", success == transfers)
