import csv
import matplotlib.pyplot as plt

transfers = []
ipc_latency = []
shared_latency = []
ipc_throughput = []
shared_throughput = []

with open("benchmark_summary.csv", newline="") as file:
    reader = csv.DictReader(file)

    for row in reader:
        transfers.append(int(row["transfers"]))
        ipc_latency.append(float(row["ipc_latency_ms"]))
        shared_latency.append(float(row["shared_memory_latency_ms"]))
        ipc_throughput.append(float(row["ipc_throughput"]))
        shared_throughput.append(float(row["shared_memory_throughput"]))

# Figure 1: Latency

plt.figure(figsize=(8, 5))

plt.plot(transfers, ipc_latency, marker="o", label="TCP-based localhost IPC")

plt.plot(transfers, shared_latency, marker="o", label="Memory-mapped shared memory")

plt.xscale("log")
plt.yscale("log")

plt.xlabel("Number of transfers")
plt.ylabel("Average latency (ms)")
plt.title("Average Synchronization Latency vs. Workload")

plt.legend()
plt.grid(True, which="both", alpha=0.25)
plt.tight_layout()

plt.savefig("figure1_latency.png", dpi=300)
plt.close()

# Figure 2: Throughput

plt.figure(figsize=(8, 5))

plt.plot(transfers, ipc_throughput, marker="o", label="TCP-based localhost IPC")

plt.plot(transfers, shared_throughput, marker="o", label="Memory-mapped shared memory")

plt.xscale("log")
plt.yscale("log")

plt.xlabel("Number of transfers")
plt.ylabel("Throughput (transfers/s)")
plt.title("Synchronization Throughput vs. Workload")

plt.legend()
plt.grid(True, which="both", alpha=0.25)
plt.tight_layout()

plt.savefig("figure2_throughput.png", dpi=300)
plt.close()

print("Figures created successfully.")
