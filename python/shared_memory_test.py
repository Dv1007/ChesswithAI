import os
import time

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

for i in range(100):

    while not os.path.exists("java_ready.flag"):
        time.sleep(0.01)

    with open("shared_memory.bin", "rb") as file:
        data = file.read(64)

    print("Number of Transfers:", i + 1, "Board length:", len(data), "Check Board:", data == expect)

    open("python_ready.flag", "w").close()

    while os.path.exists("python_ready.flag"):
        time.sleep(0.01)

print("Successfully completed 100 transfers")
