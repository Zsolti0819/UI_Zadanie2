import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;

public class Main {
    public static int rozmer;
    public static boolean netrebaRiesit;

    public static void main(String[] args) throws IOException {

        // Nacitavam zaciatocny stav zo suboru zaciatok.txt
        // Zistim pocet cisel v subore, a podla toho prekopirujem cisla do dvojrozmerneho pola
        // Funguje len pre 3x3 a 4x4
        int[] cisla = new int[50];
        int pocetCisel = 0;
        String hodnotaTest;
        try (BufferedReader br = new BufferedReader(new FileReader("txt/zaciatok.txt")))
        {
            // Read numbers from the line
            while ((hodnotaTest = br.readLine()) != null) { // Stop reading file when -1 is reached
                Scanner scanner = new Scanner(hodnotaTest);
                while (scanner.hasNextInt())
                {
                    int start = scanner.nextInt();
                    if (start == -1)
                        break;
                    cisla[pocetCisel++] = start;
                }

            }
        } catch (IOException e) {
            throw new IOException("Error processing the file.");
        }

        int odmocninaCisla = (int) Math.sqrt(pocetCisel);
        int pocetCiselPom = 0;

        int[][] zaciatocnaTabulka = new int[odmocninaCisla][odmocninaCisla];
        for (int i = 0; i < odmocninaCisla; i++) {
            for (int j = 0; j < odmocninaCisla; j++)
                zaciatocnaTabulka[i][j] = cisla[pocetCiselPom++];
        }
        Stav zaciatok = new Stav(zaciatocnaTabulka);


        // Nacitavam cielovy stav zo suboru ciel.txt
        // Ostatne su to iste ako pre zaciatok
        int[] cisla2 = new int[50];
        int pocetCisel2 = 0;
        String hodnotaTest2;
        try (BufferedReader br2 = new BufferedReader(new FileReader("txt/3x3_01.txt")))
        {

            // Read numbers from the line
            while ((hodnotaTest2 = br2.readLine()) != null) {
                Scanner scanner2 = new Scanner(hodnotaTest2);
                while (scanner2.hasNextInt())
                {
                    int start = scanner2.nextInt();
                    if (start == -1)
                        break;
                    cisla2[pocetCisel2++] = start;
                }
            }
        } catch (IOException e) {
            throw new IOException("Error processing the file.");
        }

        int odmocninaCisla2 = (int) Math.sqrt(pocetCisel2);
        int pocetCiselPom2 = 0;

        int[][] cielovaTabulka = new int[odmocninaCisla2][odmocninaCisla2];
        for (int i = 0; i < odmocninaCisla2; i++) {
            for (int j = 0; j < odmocninaCisla2; j++)
                cielovaTabulka[i][j] = cisla2[pocetCiselPom2++];
        }
        Stav ciel = new Stav(cielovaTabulka);
        rozmer = (int) Math.sqrt(pocetCisel);

        if (pocetCisel == pocetCisel2)
        {
            System.out.println("Zaciatocny stav: \n" + zaciatok);
            System.out.println("Cielovy stav: \n" + ciel);

            System.out.println("Chcete riesit hlavolam pomocou linear conflict heuristiky? ano/nie");
            Scanner scanner = new Scanner(System.in);
            String choice = scanner.next();

            if (choice.equalsIgnoreCase("ano"))
            {
                System.out.println("Riesenie hlavolamu zacalo.\nPouzite heuristiky: Manhattan distance + Linear conflict\n");
                Instant start = Instant.now();
                Uzol[] riesenie = Algoritmus.start(zaciatok, ciel, true);
                Instant koniec = Instant.now();
                if (riesenie == null)
                    System.out.println("Nema riesenie!");
                else
                    System.out.println("Beh programu: " + Duration.between(start, koniec));

            }
            else {
                System.out.println("Riesenie hlavolamu zacalo.\nPouzita heuristika: Manhattan distance");
                Instant start = Instant.now();
                Uzol[] riesenie = Algoritmus.start(zaciatok, ciel, false);
                Instant koniec = Instant.now();
                if (riesenie == null)
                    System.out.println("Nema riesenie!");
                else
                    System.out.println("Beh programu: " + Duration.between(start, koniec));
            }
        }
    }
}
