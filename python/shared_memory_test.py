import os
import time
import sys
import mmap

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

with open("shared_memory.bin", "r+b") as file:
    memory = mmap.mmap(file.fileno(), 65)

    for i in range(transfers):

        while memory[0] != 1:
            time.sleep(0.0001)

        data = memory[1:65]

        if len(data) == 64 and data == expect:
            success += 1

        memory[0] = 2

print("Total Transfers:", transfers)
print("Successful Transfers:", success)
print("Boards Verified:", success == transfers)
