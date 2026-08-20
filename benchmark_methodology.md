# Benchmark Methodology

## Objective:

The benchmark compares two communication architectures for transferring a
64 byte chess board state between Java and Python:

1. TCP-based Inter-process communication (IPC) using localhost
2. Memory-mapped shared memory

The objective is to evaluate the performance of both architectures under repeated communication workloads and determine how their performance changes as the number of transfers increases.

## Workloads:

Each architecture is tested using:

- 100 transfers
- 1,000 transfers
- 10,000 transfers
- 100,000 transfers
- 1,000,000 transfers

Each workload is repeated for five trials.

The measurements from all trials are stored in the raw CSV dataset. They are later used to calculate statistics for each architecture and workload size.

## Independent Variables:

- Communication architecture: TCP-based IPC vs. memory-mapped shared memory
- Workload size: 100, 1,000, 10,000, 100,000, 1,000,000 transfers

## Controlled Variables:

- Board size: 64 bytes
- Java chessboard
- Python receiver
- Number of trials per workload
- Board state information
- Timing method
- Synchronization protocol
- Verification method

The benchmark is designed to examine the performance differences between each architecture.

## Measured Metrics:

For each trial, the benchmark measures:

- Total transfer cycle time in milliseconds
- Average latency per transfer in milliseconds
- Transfer throughput in transfers per second
- Board size in bytes

## Total Transfer-Cycle time:

Measures the time required to complete the entire requested workload, including the communication and synchronization required for every transfer.

## Average Latency:

Average latency is calculated as:

    average latency = total benchmark time / number of transfers

This represents the average time required to complete one communication cycle. 

## Throughput:

Throughput is calculated as:

    throughput = number of transfers / elapsed time in seconds

This represents the number of completed communication cycles per second.

## Verification:

The Python receiver verifies every received 64 byte board state against the
expected chess board state.

The Java sender waits for the acknowledgment before beginning the next transfer.

A trial is only considered valid if the number of successfully verified
boards equals the requested transfer count.

## IPC Architecture:

The IPC implementation transfers the 64 byte board state between the Java sender and Python receiver using a TCP socket over localhost.

The Python receiver waits for the Java sender, receives each board state, verifies it, and sends an `OK` acknowledgment to the Java sender.

The Java sender measures the complete communication workload using `System.nanoTime()`.

## Shared Memory Architecture:

The shared memory implementation uses a memory-mapped region containing 65 bytes.

The memory layout is:

- Byte 0: synchronization flag
- Bytes 1-64: 64 byte chess board

The Java sender writes the board to the mapped region and sets the synchronization flag to signal that the board is available to the Python receiver. 

The Python receiver reads and verifies the board state and sets the synchronization flag to acknowledge the transfer.

## Synchronization:

Both architectures use a request/response synchronization system
between the Java sender and Python receiver. 

The shared-memory architecture uses active polling during synchronization.

### TCP-based IPC

For each transfer:

1. Java sends the 64 byte board state through the TCP socket.
2. Python receives the complete 64 byte board state.
3. Python verifies the board state.
4. Python sends an `OK` acknowledgment to Java.
5. Java receives the acknowledgment.
6. The next transfer begins.

### Memory-Mapped Shared Memory

The synchronization flag uses:

- `0`: empty
- `1`: available
- `2`: received

For each transfer:

1. Java writes the board state.
2. Java sets the synchronization flag to `1`.
3. Python continuously polls the synchronization flag until it equals `1`.
4. Python reads and verifies the board state.
5. Python sets the synchronization flag to `2`.
6. Java continuously polls the synchronization flag until it equals `2`.
7. Java resets the synchronization flag to `0`.
8. The next transfer begins.

## Timing:

Java measures each benchmark using `System.nanoTime()` from the start to the end of the transfer workload.

The benchmark timer includes the communication and synchronization process for each requested workload.

The benchmark timer does not include the process startup, TCP connection, or memory-map initialization in the measured transfer time. These processes happen before the benchmark timer begins. 

## Experimental Environment:

The benchmark was conducted on the same system for both communication architectures. The configuration was kept constant for the entire benchmark.

### Environment

- CPU: 13th Gen Intel Core i3-1315U
- RAM: 8 GB
- Operating System: Linux (Crostini), Debian GNU/Linux 12
- Java: OpenJDK 17.0.20
- Python: 3.11.2
- Architecture: x86_64

## Data Recording:

Each trial is recorded as one row in the raw benchmark dataset.

The CSV records:

- Architecture
- Transfer count
- Trial number
- Board size
- Total transfer-cycle time
- Average latency
- Throughput

## Scalability:

Scalability is measured by how average latency, throughput, and total transfer cycle time change as the workload increases from 100 to 1,000,000 transfers.

## Interpretation:

The measured latency represents the entire communication cycle latency,
including synchronization and receiver acknowledgment.

Therefore, results should not be interpreted as the raw hardware latency of
the IPC or shared memory alone.
