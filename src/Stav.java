public class Stav {

    // 2D array representing game board where each element is a number
    // between 0 and 15 (0 is used for the blank tile)
    public final int[][] Tabulka;

    // Correct position of each tile to achieve this state
    private Pozicia[] AktualnaPozicia;

    enum Operator {
        Hore, Dole, Vlavo, Vpravo;

        public Operator reverse() {
            if (this == Hore)
                return Dole;
            else if (this == Dole)
                return Hore;
            else if (this == Vlavo)
                return Vpravo;
            else
                return Vlavo;
        }
    }


    // parameter je tabulka, ktoru sme nacitali zo suboru
    public Stav(int[][] Tabulka) {
        this.Tabulka = Tabulka;
    }


    // prechadzame celu tabulku, hladame 0,
    // a ked ju najdeme nahradime zatvorkami []
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int[] riadok : Tabulka) {
            for (int dlazdica : riadok) {
                if (dlazdica == 0)
                    sb.append("[] ");
                else
                    sb.append(String.format("%2d ", dlazdica));
            }
            sb.append("\n");
        }
        return sb.toString();
    }


    // parameter je operator
    // funkcia vrati stav, alebo 0, ak nie je mozne pouzit operatora
    public Stav posun(Operator op) {
        // Create a new empty game board
        int[][] novaTabulka = new int[3][3];

        // inicializacia novej tabulky na aktualnu
        for (int riadok = 0; riadok < 3; riadok++)
            System.arraycopy(Tabulka[riadok], 0, novaTabulka[riadok], 0, 3);

        // hladanie dlazdici
        for (int riadok = 0; riadok < 3; riadok++) {
            for (int stlpec = 0; stlpec < 3; stlpec++) {
                if (Tabulka[riadok][stlpec] == 0) {

                    // skusime aplikovat operatora
                    // posunutim dlazdici na urcene miesto
                    switch (op) {
                        case Hore:
                            if (riadok > 0) {
                                novaTabulka[riadok][stlpec] = Tabulka[riadok-1][stlpec];
                                novaTabulka[riadok-1][stlpec] = 0;
                            } else {
                                return null;
                            }
                            break;
                        case Dole:
                            if (riadok < 2) {
                                novaTabulka[riadok][stlpec] = Tabulka[riadok+1][stlpec];
                                novaTabulka[riadok+1][stlpec] = 0;
                            } else {
                                return null;
                            }
                            break;
                        case Vlavo:
                            if (stlpec > 0) {
                                novaTabulka[riadok][stlpec] = Tabulka[riadok][stlpec-1];
                                novaTabulka[riadok][stlpec-1] = 0;
                            } else {
                                return null;
                            }
                            break;
                        case Vpravo:
                            if (stlpec < 2) {
                                novaTabulka[riadok][stlpec] = Tabulka[riadok][stlpec+1];
                                novaTabulka[riadok][stlpec+1] = 0;
                            } else {
                                return null;
                            }
                            break;
                    }
                }
            }
        }
        return new Stav(novaTabulka);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (obj == null || obj.getClass() != this.getClass())
            return false;

        Stav other = (Stav) obj;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (Tabulka[i][j] != other.Tabulka[i][j])
                    return false;

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        for (int[] riadok : Tabulka)
            for (int dlazdica : riadok)
                hash = 7*hash+dlazdica;

        return hash;
    }

    /**
     * @param ciel  Goal state
     * @return Array of positions for each of the 15 tiles
     */

    // parameter je cielovy stav
    // vrati pole pozicii pre kazdu dlazdicu
    public Pozicia[] vratitSpravnuPoziciu(Stav ciel) {
        if (ciel.AktualnaPozicia == null) {
            ciel.AktualnaPozicia = new Pozicia[9];

            // najde spravnu poziciu kazdej dlazdice
            for (int riadok = 0; riadok < 3; riadok++) {
                for (int stlpec = 0; stlpec < 3; stlpec++) {
                    if (ciel.Tabulka[riadok][stlpec] != 0) {
                        ciel.AktualnaPozicia[ciel.Tabulka[riadok][stlpec]] = new Pozicia(riadok, stlpec);
                    }
                }
            }
        }
        return ciel.AktualnaPozicia;
    }

    // parameter je cielovy stav od ktoreho vypocitame manhattansku vzdialenost
    public int manhattanDistance(Stav ciel)
    {
        int manhattan = 0;
        Pozicia[] spravnaPozicia = vratitSpravnuPoziciu(ciel);

        // Compare each tile's actual row and column to the correct row
        // and column, compute Manhattan distance, and add to sum.
        for (int riadok = 0; riadok < 3; riadok++) {
            for (int stlpec = 0; stlpec < 3; stlpec++) {
                int dlazdica = Tabulka[riadok][stlpec];

                if (dlazdica != 0) {
                    manhattan += Math.abs(spravnaPozicia[dlazdica].riadok -riadok);
                    manhattan += Math.abs(spravnaPozicia[dlazdica].stlpec -stlpec);
                }
            }
        }

        return manhattan;
    }

    public int linearConflict(Stav goal)
    {
        if (!Config.LinearConflict) {
            return manhattanDistance(goal);
        }
        // Required number moves to remove all linear conflicts
        int reqMoves = 0;

        Pozicia[] spravnaPozicia = vratitSpravnuPoziciu(goal);

        // Number or horizontal and vertical conflicts a particular
        // tile is involved in
        int[][] horizontalneKonflikty = new int[3][3];
        int[][] vertikalneKonflikty = new int[3][3];

        // pocetKonfliktov[i] is the number of tiles in a row or column
        // that have i conflicts with other tiles in the same row/column
        int[] pocetKonfliktov;

        int i = 0;
        int j = 0;
        int k;

        while (i < 3) {
            while (j < 3) {
                if (Tabulka[i][j] != 0) {
                    if (spravnaPozicia[Tabulka[i][j]].riadok == i) {                                                    // ak dlazdica je v spravnom riadku
                        k = j + 1;
                        while (k < 3) {                                                                                 // tak pre kazdu dlazdicu v tom riadku
                            //System.out.println("spravnaPozicia[Tabulka[i][k]].riadok:" + spravnaPozicia[Tabulka[i][k]].riadok);
                            if (Tabulka[i][k] != 0 &&                                                                   // dlazdica nie je prazdne miesto
                                    spravnaPozicia[Tabulka[i][k]].riadok == i &&                                        // nasledujuca dlazdica napravo je tiez v spravnom riadku
                                    spravnaPozicia[Tabulka[i][k]].stlpec < spravnaPozicia[Tabulka[i][j]].stlpec) {      // ale dlazdice su v nespravnom poradi
                                horizontalneKonflikty[i][k]++;                                                          // zvysujeme pocet konfilktov pre obe dlazdice
                                horizontalneKonflikty[i][j]++;
                            }
                            k++;
                        }
                    }
                    if (spravnaPozicia[Tabulka[i][j]].stlpec == j) {                                                    // ak dlazdica je v spravnom stlpci
                        k = i + 1;
                        while (k < 3) {                                                                                 // tak pre kazdu dlazdicu v tom stlpci
                            if (Tabulka[k][j] != 0 &&                                                                   // dlazdica nie je prazdne miesto
                                    spravnaPozicia[Tabulka[k][j]].stlpec == j &&                                        // nasledujuca dlazdica dole je tiez v spravnom stlpci
                                    spravnaPozicia[Tabulka[k][j]].riadok < spravnaPozicia[Tabulka[i][j]].riadok) {      // ale dlazdice sa v nespravnom poradi
                                vertikalneKonflikty[k][j]++;                                                            // zvysujeme pocet konfilktov pre obe dlazdice
                                vertikalneKonflikty[i][j]++;
                            }
                            k++;
                        }
                    }
                }
                j++;
            }
            i++;
        }

        // For each row, add number of moves to eliminate conflicts to required moves
        for (i = 0; i < 3; i++) {
            pocetKonfliktov = new int[3];
            for (j = 0; j < 3; j++) {
                pocetKonfliktov[horizontalneKonflikty[i][j]]++;
            }
            reqMoves += movesForConflicts(pocetKonfliktov);
        }

        // For each column, add number of moves to eliminate conflicts to required moves
        for (j = 0; j < 3; j++) {
            pocetKonfliktov = new int[3];
            for (i = 0; i < 3; i++) {
                pocetKonfliktov[vertikalneKonflikty[i][j]]++;
            }
            reqMoves += movesForConflicts(pocetKonfliktov);
        }

        // Return the sum of the Manhattan distance and the additional
        // required moves to resolve conflicts
        return  reqMoves + manhattanDistance(goal);
    }

    /**
     * @param conflictCount  conflictCount[i] is the number of tiles with i conflicts
     * @return  Number of moves required to resolve linear conflicts
     */
    private int movesForConflicts(int[] conflictCount)
    {
        // ak dlazdice su v spravnom poradi
        if (conflictCount[0] == 3)
            return 0;

            // If every tile has 3 conflicts
            // Matches 4321
        else if (conflictCount[2] == 3)
            return 5; // 6 additional moves required


            // If 2 tiles have 1 conflict each
            // or 2 tiles have 1 conflict each and 1 tile has 2 conflicts
            // or 3 tiles have 1 conflict each and 1 tiles has 3 conflicts
            // Matches 1243,1324,1342,1423,2134,2314,2341,3124,4123
        else if (conflictCount[1] == 2 && conflictCount[2] != 2)
            return 2; // 2 additional moves required

            // Otherwise
            // Matches 1432,2143,2413,2431,3142,3214,3241,3412,3421,4132,4213,4231,4312
        else
            return 3; // 4 additional moves required
    }

}
