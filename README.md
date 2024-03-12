# N Queens Solver Application

This Java application is designed to solve N Queens problem using the CSP algorithm. The program takes the initial locations of the queens in a file as an input, represented by row and column.
## Requirements

**Java (JDK):** https://www.oracle.com/java/technologies/downloads/


## Run Locally

Clone the project

```bash
  git clone https://github.com/piabor/java-nqueens-solver.git
```

Go to the project directory

```bash
  cd ./java-nqueens-solver
```

Generate runnable .class files from .java files

```bash
  javac -sourcepath ./src/ -d ./out/ ./src/main/java/edu/gwu/nqueens/solver/NQueensSolverApplication.java
```

Run the program:

```bash
  java -cp ./out/ main/java/edu/gwu/nqueens/solver/NQueensSolverApplication
```