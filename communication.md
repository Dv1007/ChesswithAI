# Java and Python Communication

## Board State Transfer:

Java sends the current chess board to Python as a `byte[64]` array.

- **Data sent:** 64 bytes

- **Sender:** Java

- **Receiver:** Python

- **Purpose:** Transfer current chess board state

- **Order:** `board[0]` through `board[63]`

Every byte represents one square of the 8×8 chess board.

## Response:

- After receiving the board data, Python sends a response to Java.

- `OK` — board has been received successfully.

- The response is currently sent as a byte sequence.

## Request/Response Cycle:

```text
Java
  │
  │ 64-byte board
  ▼
Python
  │
  │ "OK"
  ▼
Java
```

**This current implementation uses a TCP socket connection over localhost.**

## Current Cycle:
- **Java connects to Python.**

- **Java retrieves the current `byte[64]` board state.**

- **Java sends the `byte[64]` board.**

- **Python receives the board.**

- **Python verifies the received board data.**

- **Python sends an `OK` response.**

- **Java receives the response.**

- **The connection closes.**
