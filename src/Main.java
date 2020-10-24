import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;

public class Main {

    public static void main(String args[]) throws FileNotFoundException {

        // nacitanie zaciatocneho stavu zo suboru
        File zaciatocnyStav = new File("txt/zaciatok.txt");
        int[][] zaciatocnaTabulka = new int[3][3];
        Scanner zaciatocnyStavScanner = new Scanner(zaciatocnyStav);
        while (zaciatocnyStavScanner.hasNextLine()) {
            String line = zaciatocnyStavScanner.nextLine();

            if (line.isEmpty())
                continue;

            Scanner s1 = new Scanner(line);
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (s1.hasNextInt()) {
                        zaciatocnaTabulka[i][j] = s1.nextInt();
                    }
                }
            }
            s1.close();
        }
        zaciatocnyStavScanner.close();
        Stav zaciatok = new Stav(zaciatocnaTabulka);

        // nacitania cieloveho stavu zo suboru
        File cielovyStav = new File("txt/ciel.txt");
        int[][] cielovaTabulka = new int[3][3];
        Scanner cielovyStavStavScanner = new Scanner(cielovyStav);
        while (cielovyStavStavScanner.hasNextLine()) {
            String line = cielovyStavStavScanner.nextLine();

            if (line.isEmpty())
                continue;

            Scanner s2 = new Scanner(line);
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (s2.hasNextInt()) {
                        cielovaTabulka[i][j] = s2.nextInt();
                    }
                }
            }
            s2.close();
        }
        cielovyStavStavScanner.close();
        Stav ciel = new Stav(cielovaTabulka);


        System.out.println("Initial state: \n" + zaciatok + "\n========================\n");

        System.out.println("Running MM manhattanDistance+linearConflict\n--------------------------");
        Config.LinearConflict = true;
        {
            Instant start = Instant.now();
            Uzol[] riesenie = Algoritmus.start(zaciatok, ciel);
            Instant koniec = Instant.now();
            if (riesenie == null) {
                System.out.println("Nema riesenie!");
            } else {
                System.out.println("Run time: " + Duration.between(start, koniec));
            }
        }
        System.out.print("\n");

        System.out.println("Running MM manhattanDistance\n--------------------------");
        Config.LinearConflict = false;
        {
            Instant start = Instant.now();
            Uzol[] riesenie = Algoritmus.start(zaciatok, ciel);
            Instant koniec = Instant.now();
            if (riesenie == null) {
                System.out.println("Nema riesenie!");
            } else {
                System.out.println("Run time: " + Duration.between(start, koniec));
            }
        }
        System.out.print("\n");
        System.out.println("\n==========================");

    }
}
