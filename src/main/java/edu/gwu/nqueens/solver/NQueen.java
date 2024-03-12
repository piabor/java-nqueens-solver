package main.java.edu.gwu.nqueens.solver;

import java.util.*;

public class NQueen {

    private final int boardSize; // Number of Queens
    private List<List<Integer>> domains;
    private final int[] board;
    private int placedQueensCount = 0;

    public NQueen(int boardSize, int[] initialPositions) {
        this.boardSize = boardSize;
        board = initialPositions;
        initializeDomains();
    }

    private void initializeDomains() {
        domains = new ArrayList<>();
        for (int i = 0; i < boardSize; i++) {
            List<Integer> domain = new ArrayList<>();
            for (int j = 0; j < boardSize; j++) {
                domain.add(j);
            }
            domains.add(domain);
            board[i] = -1; // Initialize board
        }
    }

    public boolean solve() {
        if (placedQueensCount == boardSize) {
            printBoard(); // All queens successfully placed
            return true;
        }

        int nextRow = selectRowWithMRV();
        List<Integer> sortedValues = sortValuesByLeastConstraining(nextRow);

        for (int col : sortedValues) {
            board[nextRow] = col;
            placedQueensCount++;
            List<List<Integer>> oldDomains = copyDomains(domains);
            if (ac3(nextRow, col) && solve()) {
                return true;
            }
            placedQueensCount--;
            board[nextRow] = -1; // Backtrack
            domains = oldDomains; // Restore domains after backtracking
        }

        return false;
    }

    int selectRowWithMRV() {
        int minDomainSize = Integer.MAX_VALUE;
        int rowIndexWithMinDomain = -1;
        int maxConstrainingScore = -1; // Track the score of the most constraining row.

        for (int i = 0; i < boardSize; i++) {
            if (board[i] != -1) continue; // Skip if a queen is already placed in this row.

            int domainSize = domains.get(i).size();

            if (domainSize < minDomainSize) {
                minDomainSize = domainSize;
                rowIndexWithMinDomain = i;
            } else if (domainSize == minDomainSize) { // Apply more constraining variable as a tie-breaking rule
                int constrainingScore = calculateConstrainingScore(i); // Calculate how constraining the row is.
                if (constrainingScore > maxConstrainingScore) {
                    maxConstrainingScore = constrainingScore;
                }
            }
        }
        return rowIndexWithMinDomain;
    }

    int calculateConstrainingScore(int row) {
        int score = 0;
        // For each column in the row, calculate how placing a queen there would affect other rows.
        for (int col : domains.get(row)) {
            // Simulate placing a queen in each possible column and count how many positions
            // in other rows would be attacked by a queen in this position.
            for (int otherRow = 0; otherRow < boardSize; otherRow++) {
                if (otherRow == row) continue; // Skip the current row.
                for (int otherCol = 0; otherCol < boardSize; otherCol++) {
                    if (otherCol == col || Math.abs(otherRow - row) == Math.abs(otherCol - col)) {
                        score++; // Increment score for each position that would be attacked.
                    }
                }
            }
        }
        return score;
    }

    List<Integer> sortValuesByLeastConstraining(int row) {
        Map<Integer, Integer> valueConstraints = new HashMap<>();
        for (int value : domains.get(row)) {
            int constraints = 0;
            for (int i = 0; i < boardSize; i++) {
                if (i != row && isConstraining(row, value, i)) {
                    constraints += domains.get(i).size() - 1; // Assume each value potentially removes one option from the domain of row i
                }
            }
            valueConstraints.put(value, constraints);
        }

        List<Integer> sortedValues = new ArrayList<>(domains.get(row));
        sortedValues.sort(Comparator.comparingInt(valueConstraints::get));
        return sortedValues;
    }

    boolean isConstraining(int currentRow, int currentValue, int otherRow) {
        for (int value : domains.get(otherRow)) {
            if (!isConsistent(otherRow, value, currentRow, currentValue)) {
                return true;
            }
        }
        return false;
    }

    boolean ac3(int assignedRow, int assignedCol) {
        Queue<int[]> queue = new LinkedList<>();

        // Initialize the queue with all arcs affected by the assignment
        for (int i = 0; i < boardSize; i++) {
            if (i != assignedRow) {
                queue.add(new int[]{i, assignedRow});
            }
        }

        while (!queue.isEmpty()) {
            int[] arc = queue.poll();
            int xi = arc[0];
            int xj = arc[1];

            if (removeInconsistentValues(xi, xj, assignedRow, assignedCol)) {
                if (domains.get(xi).isEmpty()) {
                    return false; // No value can satisfy the constraint, thus failure
                }

                // Add back arcs to the queue
                for (int xk = 0; xk < boardSize; xk++) {
                    if (xk != xi && xk != xj) {
                        queue.add(new int[]{xk, xi});
                    }
                }
            }
        }

        return true;
    }

    boolean removeInconsistentValues(int xi, int xj, int assignedRow, int assignedCol) {
        boolean removed = false;

        Iterator<Integer> it = domains.get(xi).iterator();
        while (it.hasNext()) {
            int x = it.next();
            if (!isConsistent(xi, x, assignedRow, assignedCol)) {
                it.remove();
                removed = true;
            }
        }

        return removed;
    }

    boolean isConsistent(int xi, int valueXi, int assignedRow, int assignedCol) {
        // Checks if placing a queen at (xi, valueXi) is consistent with a queen at (assignedRow, assignedCol)
        return valueXi != assignedCol && Math.abs(xi - assignedRow) != Math.abs(valueXi - assignedCol);
    }

    List<List<Integer>> copyDomains(List<List<Integer>> original) {
        List<List<Integer>> copy = new ArrayList<>();
        for (List<Integer> domain : original) {
            copy.add(new ArrayList<>(domain));
        }
        return copy;
    }

    private void printBoard() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                System.out.print(board[i] == j ? "Q " : ". ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public int getBoardSize() {
        return boardSize;
    }

    public List<List<Integer>> getDomains() {
        return domains;
    }

    public int[] getBoard() {
        return board;
    }

    public int getPlacedQueensCount() {
        return placedQueensCount;
    }

}