package main.java.edu.gwu.nqueens.solver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NQueenTest {

    private NQueen nQueen;

    @BeforeEach
    void setUp() {
        this.nQueen = new NQueen(4, new int[]{-1, -1, -1, -1});
    }

    @Test
    void testInitialization() {
        assertEquals(4, nQueen.getBoardSize(), "Board size should be initialized to 4.");
        assertEquals(0, nQueen.getPlacedQueensCount(), "Initially, no queens should be placed.");
        for (int pos : nQueen.getBoard()) {
            assertEquals(-1, pos, "Each position on the board should be initialized to -1.");
        }
        for (List<Integer> domain : nQueen.getDomains()) {
            assertEquals(4, domain.size(), "Each domain should initially contain 4 values.");
        }
    }

    @Test
    void testCopyDomains() {
        List<List<Integer>> originalDomains = nQueen.getDomains();
        List<List<Integer>> copiedDomains = nQueen.copyDomains(originalDomains);

        assertNotSame(originalDomains, copiedDomains, "Copied domains should be a new instance.");
        for (int i = 0; i < originalDomains.size(); i++) {
            assertNotSame(originalDomains.get(i), copiedDomains.get(i), "Each domain list should be a new instance.");
            assertEquals(originalDomains.get(i), copiedDomains.get(i), "Copied domain contents should be equal to the original.");
        }
    }

    @Test
    void testSolveFor4Queens() {
        assertTrue(nQueen.solve(), "Application should find a solution for 4 queens.");
        assertTrue(isSolutionValid(nQueen.getBoard()), "The solution should be a valid N-Queens solution.");
    }

    private boolean isSolutionValid(int[] board) {
        int len = board.length;
        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j < len; j++) {
                // Check for same column or diagonal conflicts
                if (board[i] == board[j] || Math.abs(board[i] - board[j]) == Math.abs(i - j)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Test
    void testSolveSelectsRowWithMRV() {
        // Scenario setup to force a situation where the MRV heuristic should pick a specific row
        NQueen nQueen = new NQueen(4, new int[]{-1, -1, -1, -1});
        // Directly manipulating domains to simulate the board state after some moves
        // Assume we have restricted row 0 to 1 option, row 1 to 2 options, etc.
        nQueen.getDomains().get(0).clear();
        nQueen.getDomains().get(0).add(1); // Only one possible position for row 0
        nQueen.getDomains().get(1).remove(Integer.valueOf(0)); // Two possible positions for row 1
        nQueen.getDomains().get(1).remove(Integer.valueOf(3));
        // The MRV heuristic should favor row 0 next due to it having the fewest legal moves

        // Trigger the selecting process
        int result = nQueen.selectRowWithMRV();

        assertEquals(0, result, "The method should return 0 indexed row.");
    }

    @Test
    void testCalculateConstrainingScore() {
        // Scenario setup to force a situation where tie breaking rule should pick the more constraining row
        NQueen nQueen = new NQueen(4, new int[]{-1, -1, -1, -1});
        // Clear all domains for simplicity in this test scenario.
        for (List<Integer> domain : nQueen.getDomains()) {
            domain.clear();
        }
        nQueen.getDomains().get(2).add(0);
        nQueen.getDomains().get(2).add(1);

        // Calculate the constraining score for row 2.
        // Considering our setup, placing a queen in either column (0 or 1) of row 2
        // would constrain positions in other rows. Each column placement affects all columns
        // in other rows plus the diagonals. For a 4x4 board, this simplifies our calculation.
        int score = nQueen.calculateConstrainingScore(2);

        int expectedScore = 14;
        assertEquals(expectedScore, score, "The constraining score should match the expected value.");
    }

    @Test
    void testSortValuesByLeastConstraining() {
        NQueen nQueen = new NQueen(4, new int[]{-1, -1, -1, -1});

        nQueen.getDomains().clear(); // Clear existing domains
        // Manually set domains to reflect potential moves
        nQueen.getDomains().add(Arrays.asList(0, 1, 2, 3)); // Row 0
        nQueen.getDomains().add(Arrays.asList(2, 3));       // Row 1
        nQueen.getDomains().add(Arrays.asList(1, 2, 3));    // Row 2
        nQueen.getDomains().add(Arrays.asList(0, 1, 3));    // Row 3


        List<Integer> expected = Arrays.asList(2, 0, 1, 3);
        List<Integer> actual = nQueen.sortValuesByLeastConstraining(0);

        // Expected: [0, 3, 1, 2] or [3, 0, 1, 2] because placing a queen in 0 or 3 leaves more options.
        assertEquals(expected, actual);
    }

    @Test
    void testIsConsistent_NotSameColumnOrDiagonal() {
        assertTrue(nQueen.isConsistent(0, 1, 4, 3), "Queens are not in the same column or diagonal.");
    }

    @Test
    void testIsConstraining_WhenTrue() {
        // Setup: Assuming we are evaluating the impact of placing a queen in row 0, column 1 on row 1
        // where domains for row 1 would allow a queen in column 2,
        // which is diagonally attacking the (0, 1) position.
        nQueen.getDomains().get(1).clear(); // Clearing existing domains for clarity in this example.
        nQueen.getDomains().get(1).add(2); // Row 1 can potentially place a queen in column 2, which is constrained by (0, 1).

        assertTrue(nQueen.isConstraining(0, 1, 1), "Placing a queen at (0, 1) should constrain row 1.");
    }

    @Test
    void testAc3_WithRemovingInconsistentValues() {
        // Setup: Placing a queen in a position that leads to removing inconsistent values.
        nQueen.getBoard()[2] = 2; // Place a queen at row 2, column 2, creating conflicts for rows 0, 1, and 3.
        assertTrue(nQueen.ac3(2, 2), "ac3 should return true when inconsistency can be resolved.");

        // Assert that inconsistent values were removed but no domain is empty.
        for (int i = 0; i < nQueen.getBoardSize(); i++) {
            if (i != 2) { // Except for the assigned row
                assertFalse(nQueen.getDomains().get(i).contains(2), "Column 2 should be removed from other rows' domains.");
            }
        }
    }

    @Test
    void testAc3_WithConflictLeadingToEmptyDomain() {
        // A scenario where placing a queen leads to at least one domain being emptied.
        nQueen.getBoard()[0] = 0; // Place a queen in the first row, first column.
        // Manipulate domains to simulate a scenario where enforcing arc consistency will empty a domain.
        nQueen.getDomains().get(1).clear(); // Pretend row 1 can only place a queen in a position conflicting with (0, 0).
        nQueen.getDomains().get(1).add(0);

        assertFalse(nQueen.ac3(0, 0), "ac3 should return false when a domain is emptied, indicating a failure.");

        // Assert that at least one domain is empty, indicating the placement leads to an unsolvable state.
        assertTrue(nQueen.getDomains().get(1).isEmpty(), "The domain for row 1 should be emptied due to conflicts.");
    }

}