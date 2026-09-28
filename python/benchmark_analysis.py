import csv
import statistics
from collections import defaultdict

data = []

with open("benchmark_results.csv", newline="") as file:
    reader = csv.DictReader(file)

    for row in reader:
        arch_name = row["architecture"].strip()
        if arch_name == "TCP-IPC":
            arch_name = "IPC"

        data.append({"architecture": arch_name, "transfers": int(row["transfers"]), "trial": int(row["trial"]), "board_size": int(row["board_size"]), "total_time_ms": float(row["total_time_ms"]), "average_latency_ms": float(row["average_latency_ms"]), "throughput": float(row["throughput"])})

groups = defaultdict(list)

for row in data:
    key = (row["architecture"], row["transfers"])
    groups[key].append(row)

print("=== Benchmark Dataset ===")
print(f"Observations: {len(data)}")
print()
print("=== Summary Statistics ===")

workloads = sorted(set(row["transfers"] for row in data))
architectures = sorted(set(row["architecture"] for row in data))

for workload in workloads:
    print(f"\nWorkload: {workload:,} transfers")

    for architecture in architectures:
        rows = groups[(architecture, workload)]

        latencies = [row["average_latency_ms"] for row in rows]
        throughputs = [row["throughput"] for row in rows]
        total_times = [row["total_time_ms"] for row in rows]

        print(f"\n{architecture}")
        print(f"  Trials: {len(rows)}")

        print(f"  Latency mean: "f"{statistics.mean(latencies):} ms")

        print(f"  Latency median: "f"{statistics.median(latencies):} ms")

        print(f"  Latency standard deviation: "f"{statistics.stdev(latencies):} ms")

        print(f"  Throughput mean: "f"{statistics.mean(throughputs):} transfers/s")

        print(f"  Throughput median: "f"{statistics.median(throughputs):} transfers/s")

        print(f"  Throughput standard deviation: "f"{statistics.stdev(throughputs):} transfers/s")

        print(f"  Total time mean: "f"{statistics.mean(total_times):} ms")

print("\n=== Dataset Validation ===")

expected_observations = 5 * 5 * 2

if len(data) == expected_observations:
    print(f"PASS: {len(data)} observations found.")

else:
    print(f"Error: Expected {expected_observations} "f"observations, found {len(data)}.")


expected_workloads = {100, 1000, 10000, 100000, 1000000}
actual_workloads = set(row["transfers"] for row in data)

if actual_workloads == expected_workloads:
    print("PASS: Expected workloads present.")

else:
    print(f"Error: Expected workloads {sorted(expected_workloads)}, "
          f"found {sorted(actual_workloads)}.")

if all(row["board_size"] == 64 for row in data):
    print("PASS: All board sizes are 64 bytes.")

else:
    print("Error: Board size must be 64 bytes.")

print("\n=== Trial Verification ===")

for workload in workloads:
    for architecture in architectures:
        rows = groups[(architecture, workload)]
        trial_numbers = [row["trial"] for row in rows]

        if sorted(trial_numbers) == [1, 2, 3, 4, 5]:
            print(f"PASS: {architecture}, "f"{workload:,} transfers → 5 trials, IDs 1-5")

        else:
            print(f"Error: {architecture}, "f"{workload:,} transfers → "f"invalid trial IDs: {trial_numbers}")

print("\n=== Analysis ===")

summary = []

for workload in workloads:
    ipc_rows = groups[("IPC", workload)]
    shared_rows = groups[("SharedMemory", workload)]

    ipc_latency = statistics.mean(
        row["average_latency_ms"] for row in ipc_rows
    )

    shared_latency = statistics.mean(
        row["average_latency_ms"] for row in shared_rows
    )

    ipc_throughput = statistics.mean(
        row["throughput"] for row in ipc_rows
    )

    shared_throughput = statistics.mean(
        row["throughput"] for row in shared_rows
    )

    latency_ratio = ipc_latency / shared_latency

    throughput_ratio = shared_throughput / ipc_throughput

    latency_difference_percent = ((ipc_latency - shared_latency) / ipc_latency) * 100

    throughput_difference_percent = ((shared_throughput - ipc_throughput) / ipc_throughput) * 100

    summary.append({"transfers": workload, "ipc_latency_ms": ipc_latency, "shared_memory_latency_ms": shared_latency, "latency_ratio_ipc_to_shared": latency_ratio, "ipc_throughput": ipc_throughput, "shared_memory_throughput": shared_throughput, "throughput_ratio_shared_to_ipc": throughput_ratio, "latency_difference_percent": latency_difference_percent, "throughput_difference_percent": throughput_difference_percent})

    print(f"\nWorkload: {workload:,} transfers")
    print(f"  IPC latency: {ipc_latency:.9f} ms")
    print(f"  Shared memory latency: {shared_latency:.9f} ms")
    print(f"  IPC / Shared memory latency ratio: {latency_ratio:.2f}x")
    print(f"  IPC throughput: {ipc_throughput:.2f} transfers/s")
    print(f"  Shared memory throughput: {shared_throughput:.2f} transfers/s")
    print(f"  Shared memory / IPC throughput ratio: {throughput_ratio:.2f}x")
    print(f"  Latency difference: {latency_difference_percent:.2f}%")
    print(f"  Throughput difference: {throughput_difference_percent:.2f}%")

with open("benchmark_summary.csv", "w", newline="") as file:
    fieldnames = ["transfers", "ipc_latency_ms", "shared_memory_latency_ms", "latency_ratio_ipc_to_shared", "ipc_throughput", "shared_memory_throughput", "throughput_ratio_shared_to_ipc", "latency_difference_percent", "throughput_difference_percent"]

    writer = csv.DictWriter(file, fieldnames=fieldnames)

    writer.writeheader()
    writer.writerows(summary)

print("PASS: benchmark_summary.csv created.")
