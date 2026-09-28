# AI Chess Engine

A fully playable chess application built from scratch in **Java** with **JavaFX**, featuring a custom chess engine, legal move generation, standard chess rules, and an AI opponent powered by **Minimax with Alpha-Beta Pruning**.

I built this project to go beyond simply creating a graphical chessboard. I wanted to understand what it takes to make a program actually play chess: representing the board, enforcing the rules, detecting legal moves, evaluating positions, and searching through possible games.

## About

Chess looks simple from the outside: move a piece, respond to an opponent, and try to checkmate the king.

Programming chess is a very different problem.

A single move can affect king safety, castling rights, en passant, promotion, repetition, draw conditions, and the position's evaluation. I built the underlying chess engine myself so that the application could handle these interactions rather than relying on an external chess library.

After building the game logic, I added an AI player using **Minimax with Alpha-Beta Pruning**. This allowed the same move-generation and board-state system to become the foundation for an AI capable of searching through possible future positions.

## Features

The chess application currently supports:

- Legal move generation
- Captures
- Check detection
- Checkmate detection
- Stalemate detection
- Castling
- En passant
- Pawn promotion
- Threefold repetition
- Fifty-move rule
- Insufficient-material draws
- King-safety checking
- Draw detection
- Player-versus-AI gameplay
- JavaFX graphical interface

The goal was to make the board behave like an actual chess game, not just make the pieces move.

## How the Chess Engine Works

The board is represented internally as a **64-element byte array**, with each element corresponding to one square on the 8×8 chessboard.

This compact representation also makes the board state easy to work with outside of the graphical interface.

When a player attempts a move, the engine:

1. Determines whether the selected piece belongs to the current player.
2. Generates possible moves for that piece.
3. Checks whether the destination square is valid.
4. Handles captures and special rules.
5. Simulates the move.
6. Checks whether the player's king would be left in check.
7. Accepts the move only if it is legal.
8. Updates the board and game state.

The same underlying board and move-generation system is used by both the human player and the AI.

## Special Chess Rules

### Castling

The engine supports both kingside and queenside castling.

It tracks whether the king and rooks have moved and checks the required king-safety conditions before allowing castling.

### En Passant

The game tracks the necessary pawn-move information to determine when an en passant capture is available and verifies that the capture is legal.

### Pawn Promotion

When a pawn reaches the opposite end of the board, it can be promoted to:

- Queen
- Rook
- Bishop
- Knight

The AI also handles promotion when generating and evaluating moves.

### Draw Conditions

The engine checks for several ways a game can end in a draw:

- Stalemate
- Threefold repetition
- Fifty-move rule
- Insufficient material

These conditions are part of the game-state logic rather than being handled only by the graphical interface.

## The AI

The chess AI uses **Minimax with Alpha-Beta Pruning** to search through possible future positions.

At a high level, the AI:

1. Generates the legal moves available to it.
2. Simulates a possible move.
3. Generates the opponent's responses.
4. Continues searching to a specified depth.
5. Evaluates the resulting positions.
6. Uses Minimax to select the highest-scoring move.
7. Uses Alpha-Beta Pruning to skip branches that cannot change the final decision.

The current implementation searches to a depth of **3**.

Alpha-Beta Pruning makes the search more efficient by eliminating parts of the game tree that do not need to be evaluated.

## Position Evaluation

The AI does not simply count pieces.

Its evaluation function combines **material values** with **piece-square tables**, giving the AI some awareness of where pieces are positioned on the board.

The material values used by the evaluation system are:

| **Piece** | **Value** |
| --------- | --------: |
| Pawn      |       100 |
| Knight    |       320 |
| Bishop    |       330 |
| Rook      |       500 |
| Queen     |       900 |
| King      |     20000 |

Piece-square tables add or subtract positional value depending on where a piece is located.

This allows the AI to distinguish between positions with the same material but different piece placement.

## Move Ordering

The AI also uses move ordering to make Alpha-Beta Pruning more effective.

Captures are prioritized using the relative values of the pieces involved. By considering promising moves earlier, the search can find useful bounds sooner and potentially eliminate more unnecessary branches.

## The Graphical Interface

The graphical interface is built with **JavaFX**.

The board is displayed as an 8×8 grid, with each square represented by a JavaFX button. Chess pieces are loaded from image assets in `src/pieces/`.

The interface handles the player's interaction while the chess engine handles the actual game.

### JavaFX

- Displays the board
- Displays pieces
- Handles user interaction
- Updates the visual board

### Chess Engine

- Stores the board state
- Generates legal moves
- Enforces chess rules
- Detects check and game-ending conditions
- Evaluates positions
- Calculates AI moves

Keeping these responsibilities separate makes it possible to use the same chess logic for both the graphical game and the AI.

## Extending the Project

The chess application also became the starting point for a separate **Java/Python systems research project**.

I built a communication layer that allows the compact chess board state to be shared between Java and Python. That work led to a separate investigation comparing TCP communication with memory-mapped shared memory.

The full experiment, methodology, benchmark results, and analysis are documented separately in the repository.

Relevant files include:

- `benchmark_methodology.md`
- `communication.md`
- `benchmark_results.csv`
- `benchmark_summary.csv`
- `python/`

The chess application itself remains the core of this repository.

## Project Structure

```text
.
├── docs/
│   ├── figure1_latency.png
│   ├── figure2_throughput.png
│   └── images/
│
├── python/
│   ├── benchmark_analysis.py
│   ├── benchmark_config.txt
│   ├── benchmark_runner.py
│   ├── bytes_test.py
│   ├── create_graph.py
│   └── shared_memory_test.py
│
├── src/
│   ├── pieces/
│   │   └── chess piece image assets
│   ├── ByteSend.java
│   ├── ChessAI.java
│   ├── ChessBoard.java
│   ├── ChessFX.java
│   ├── Communication.java
│   ├── Evaluation.java
│   ├── Move.java
│   ├── MoveOrder.java
│   └── SharedMemory.java
│
├── test/
│   ├── ChessGame.java
│   └── TestAI.java
│
├── benchmark_methodology.md
├── benchmark_results.csv
├── benchmark_summary.csv
├── communication.md
├── LICENSE
└── README.md
```

### Main Components

**`ChessFX.java`**

The JavaFX application and graphical interface. Handles the board display and player interaction.

**`ChessBoard.java`**

Contains the board state and core chess logic, including move generation, legal-move checking, special moves, and game-state detection.

**`ChessAI.java`**

Contains the AI search algorithm, including Minimax and Alpha-Beta Pruning.

**`Evaluation.java`**

Evaluates chess positions using material values and piece-square tables.

**`Move.java`**

Represents individual chess moves and the information needed to apply and evaluate them.

**`MoveOrder.java`**

Prioritizes candidate moves during AI search.

**`Communication.java`**

Handles communication between the Java chess environment and the external Python process.

**`ByteSend.java`**

Handles transmission of the compact board-state representation.

**`SharedMemory.java`**

Implements the memory-mapped shared-memory communication used by the separate systems investigation.

## Installation

### Requirements

The project was developed and tested in:

- **Debian GNU/Linux 12 (Bookworm)**
- **ChromeOS Linux container (Crostini)**
- **13th Gen Intel Core i3-1315U**
- **8 GB RAM**
- **x86_64**
- **OpenJDK 17.0.20**
- **Python 3.11.2**
- **JavaFX**
- **Git**

A newer JDK may also work, but JDK 17 is the version used during development and testing.

### Clone the Repository

```bash
git clone <repository-url>
cd <repository-name>
```

### Compile the Chess Application

The project currently uses a direct Java compilation setup rather than Maven or Gradle. JavaFX must therefore be installed separately.

From the repository root:

```bash
mkdir -p out

javac \
  --module-path /path/to/javafx-sdk/lib \
  --add-modules javafx.controls \
  -d out \
  src/*.java
```

Replace `/path/to/javafx-sdk/lib` with the location of the JavaFX SDK on your system.

### Copy the Chess Pieces

The application loads its piece images from the `/pieces/` resource path.

Copy the image directory into the compiled output:

```bash
cp -r src/pieces out/pieces
```

### Run the Game

```bash
java \
  --module-path /path/to/javafx-sdk/lib \
  --add-modules javafx.controls \
  -cp out \
  ChessFX
```

The exact JavaFX path will depend on your installation.

## Acknowledgements

I used the **Oracle Java documentation** and **JavaFX documentation** as references while developing the project.

I also used programming tutorials and educational videos on **YouTube** as supplementary learning resources while learning Java, JavaFX, and chess-engine concepts.

These resources helped me understand unfamiliar concepts and work through implementation problems. The application, chess engine, and AI were designed and implemented as my own project.

## Future Development

There are several directions I would like to take the project next:

- Increase the AI search depth
- Improve the evaluation function
- Experiment with stronger move ordering
- Improve AI playing strength
- Add move history
- Add captured-piece displays
- Add game timers
- Add position analysis
- Build an AI coaching mode
- Improve the JavaFX interface
- Explore reinforcement learning for chess
