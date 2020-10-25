import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) throws IOException {

        // Nacitavam zaciatocny stav zo suboru zaciatok.txt
        // Zistim pocet cisel v subore, a podla toho prekopirujem cisla do dvojrozmerneho pola
        // Funguje len pre 3x3 a 4x4
        BufferedReader br;
        int[] cisla = new int[17];
        br = new BufferedReader(new FileReader("txt/zaciatok.txt"));
        int hodnota;
        int pocetCisel = 0;
        while ((hodnota = br.read()) != -1) {
            if (hodnota != ' ')
            {
                int cislo = Character.getNumericValue(hodnota);
                cisla[pocetCisel++] = cislo;
            }
        }
        br.close();

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
        BufferedReader br2;
        int[] cisla2 = new int[17];
        br2 = new BufferedReader(new FileReader("txt/ciel.txt"));
        int hodnota2;
        int pocetCisel2 = 0;
        while ((hodnota2 = br2.read()) != -1) {
            if (hodnota2 != ' ')
            {
                int cislo2 = Character.getNumericValue(hodnota2);
                cisla2[pocetCisel2++] = cislo2;
            }
        }
        br2.close();

        int odmocninaCisla2 = (int) Math.sqrt(pocetCisel2);
        int pocetCiselPom2 = 0;

        int[][] cielovaTabulka = new int[odmocninaCisla2][odmocninaCisla2];
        for (int i = 0; i < odmocninaCisla2; i++) {
            for (int j = 0; j < odmocninaCisla2; j++)
                cielovaTabulka[i][j] = cisla2[pocetCiselPom2++];
        }
        Stav ciel = new Stav(cielovaTabulka);

        if (pocetCisel == pocetCisel2)
            System.out.println("GG\n");

        System.out.println("Pocet cisel: "+pocetCisel+"\nPocet cisel 2: "+pocetCisel2);


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
