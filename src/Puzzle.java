import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Puzzle {

    public static void main(String args[]) {

        // Initial states of puzzles to solve
        List<State> initials = new ArrayList<State>();

        if (args.length < 1) {
            System.out.println("Error: no input file given");
            System.exit(1);
        }

        // Read input from file given on command line
        Scanner s = null;
        try {
            s = new Scanner(new FileInputStream(args[0]));
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't open input file '" + args[0] + "'");
            System.exit(1);
        }

        while (s.hasNextLine()) {
            String line = s.nextLine();

            // Skip blank lines
            if (line.isEmpty())
                continue;

            Scanner ss = new Scanner(line);
            byte[][] board = new byte[3][3];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (ss.hasNextInt()) {
                        board[i][j] = (byte) ss.nextInt();
                    } else {
                        System.out.println("Invalid input file");
                        System.exit(1);
                    }
                }
            }
            ss.close();

            initials.add(new State(board));
        }
        s.close();

        // Goal state
        State goal = new State(new byte[][] { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 0 }});

        // Run solver on each test case
        for (State initial : initials) {
            System.out.println("Initial state: \n" + initial + "\n========================\n");

            System.out.println("Running MM manhattanDistance+linearConflict\n--------------------------");
            Config.MMε = 0;
            Config.LinearConflict = true;
            {
                Instant start = Instant.now();
                Node[] solution = MMsearch.MMSolve(initial, goal);
                Instant end = Instant.now();
                if (solution == null) {
                    System.out.println("No solution Found!");
                } else {
                    System.out.println("Run time: " + Duration.between(start, end));
                }
            }
            System.out.print("\n");


            System.out.println("Running MM manhattanDistance\n--------------------------");
            Config.MMε = 0;
            Config.LinearConflict = false;
            {
                Instant start = Instant.now();
                Node[] solution = MMsearch.MMSolve(initial, goal);
                Instant end = Instant.now();
                if (solution == null) {
                    System.out.println("No solution Found!");
                } else {
                    System.out.println("Run time: " + Duration.between(start, end));
                }
            }
            System.out.print("\n");


            System.out.println("Running MMε manhattanDistance+linearConflict\n--------------------------");
            Config.MMε = 1;
            Config.LinearConflict = true;
            {
                Instant start = Instant.now();
                Node[] solution = MMsearch.MMSolve(initial, goal);
                Instant end = Instant.now();
                if (solution == null) {
                    System.out.println("No solution Found!");
                } else {
                    System.out.println("Run time: " + Duration.between(start, end));
                }
            }
            System.out.print("\n");


            System.out.println("Running MMε manhattanDistance\n--------------------------");
            Config.MMε = 1;
            Config.LinearConflict = false;
            {
                Instant start = Instant.now();
                Node[] solution = MMsearch.MMSolve(initial, goal);
                Instant end = Instant.now();
                if (solution == null) {
                    System.out.println("No solution Found!");
                } else {
                    System.out.println("Run time: " + Duration.between(start, end));
                }
            }
            System.out.print("\n");



            System.out.println("\n==========================");

        }
    }

}
