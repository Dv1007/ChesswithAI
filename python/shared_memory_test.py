import os
import time
import sys

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

for i in range(transfers):

    while not os.path.exists("java_ready.flag"):
        time.sleep(0.001)

    with open("shared_memory.bin", "rb") as file:
        data = file.read(64)

    if len(data) == 64 and data == expect:
        success += 1

    open("python_ready.flag", "w").close()
    
print("Total Transfers:", transfers)
print("Successful Transfers:", success)
print("Boards Verified:", success == transfers)
