import subprocess
import json
import csv
import time

def load_config():
    config = {}

    with open("benchmark_config.txt") as file:
        for line in file:
            key, value = line.strip().split("=")
            config[key] = value

    transfers = [int(x) for x in config["transfers"].split(",")]
    trials = int(config["trials"])
    board_size = int(config["board_size"])

    return transfers, trials, board_size

def run_ipc(transfers):
    print(f"\nStarting IPC benchmark: {transfers} transfers")

    python_process = subprocess.Popen(["taskset", "-c", "2", "/usr/bin/python3", "-u","bytes_test.py",str(transfers)], stdout=subprocess.PIPE, stderr=subprocess.PIPE,text=True)

    ready_message = python_process.stdout.readline().strip()

    if ready_message != "Waiting for Java":
        python_stderr = python_process.stderr.read()
        raise RuntimeError(f"Python IPC server failed.\n"f"Output: {ready_message}\n"f"Error: {python_stderr}")

    java_process = subprocess.run(["taskset", "-c", "3", "java", "ByteSend", str(transfers)], cwd ="src", capture_output=True, text=True)

    python_stdout, python_stderr = python_process.communicate()

    print("\n--- Java ---")
    print(java_process.stdout)

    print("--- Python ---")
    print(python_stdout)

    if java_process.returncode != 0:
        print(java_process.stderr)
        raise RuntimeError("Java IPC benchmark failed")

    if python_process.returncode != 0:
        print(python_stderr)
        raise RuntimeError("Python IPC benchmark failed")

    if "Boards Verified: True" not in python_stdout:
        raise RuntimeError(f"IPC trial failed verification.\n" f"Python output:\n{python_stdout}")

    result = json.loads(java_process.stdout)

    return result

def run_shared_memory(transfers):
    print(f"\nStarting Shared Memory benchmark: {transfers} transfers")

    shared_memory_path = "shared_memory.bin"

    python_process = subprocess.Popen(
        ["taskset", "-c", "2", "/usr/bin/python3", "-u", "shared_memory_test.py", str(transfers)],
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True
    )

    time.sleep(0.2)

    java_process = subprocess.run(
        ["taskset", "-c", "3", "java", "SharedMemory", str(transfers), f"../{shared_memory_path}"],
        cwd="src",
        capture_output=True,
        text=True
    )

    python_stdout, python_stderr = python_process.communicate()

    print("\n--- Java ---")
    print(java_process.stdout)

    print("--- Python ---")
    print(python_stdout)

    if java_process.returncode != 0:
        print(java_process.stderr)
        raise RuntimeError("Java Shared Memory benchmark failed")

    if python_process.returncode != 0:
        print(python_stderr)
        raise RuntimeError("Python Shared Memory benchmark failed")

    if "Boards Verified: True" not in python_stdout:
        raise RuntimeError(f"Shared memory trial failed verification.\n" f"Python output:\n{python_stdout}")

    result = json.loads(java_process.stdout)

    return result

def start_csv():
    with open("benchmark_results.csv", "w", newline="") as file:

        writer = csv.writer(file)

        writer.writerow(["architecture","transfers", "trial", "board_size", "total_time_ms", "average_latency_ms", "throughput"])

def save_result(result, trial):
    with open("benchmark_results.csv", "a", newline="") as file:
    
            writer = csv.writer(file)
    
            writer.writerow([result["architecture"], result["transfers"], trial, result["board_size"], result["total_time_ms"], result["average_latency_ms"], result["throughput"]])

def main():
    transfers, trials, board_size = load_config()

    start_csv()

    print("Benchmark Configuration")
    print("=======================")
    print("Transfers:", transfers)
    print("Trials:", trials)
    print("Board size:", board_size)

    for transfer_count in transfers:
        print(f"\n===== {transfer_count} TRANSFERS =====")

        for trial in range(1, trials + 1):
            print(f"\n--- Trial {trial}/{trials} ---")

            result = run_ipc(transfer_count)
            save_result(result, trial)

            result = run_shared_memory(transfer_count)
            save_result(result, trial)

            print("\nParsed JSON result:")
            print(result)

if __name__ == "__main__":
    main()
