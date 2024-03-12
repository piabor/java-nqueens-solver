package main.java.edu.gwu.nqueens.solver;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Scanner;

public class NQueensSolverApplication {

    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("n-queen.txt");
        int n = 0;
        Scanner input = new Scanner(file);

        while (input.hasNextLine()) {
            Scanner colReader = new Scanner(input.nextLine());
            if (colReader.hasNextInt()) {
                n++;
            }
        }
        input.close();

        if (n < 10 || n > 1000) {
            System.out.println("Invalid input. Please enter a value between 10 and 1000.");
            return;
        }

        int[] initialPositions = new int[n];
        Arrays.fill(initialPositions, -1);

        input = new Scanner(file);
        n = 0;
        while (input.hasNextLine()) {
            Scanner colReader = new Scanner(input.nextLine());
            if (colReader.hasNextInt()) {
                // Adjusting for the 0 index
                initialPositions[n] = colReader.nextInt() - 1;
                n++;
            }
        }
        input.close();

        NQueen queenProblem = new NQueen(n, initialPositions);

        Instant start = Instant.now();

        queenProblem.solve();

        Instant end = Instant.now();

        Duration timeElapsed = Duration.between(start, end);
        System.out.println("Time taken: " + timeElapsed.toMillis() + " ms");
    }

}
