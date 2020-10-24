public class State extends Heuristiky {

    // 2D array representing game board where each element is a number
    // between 0 and 15 (0 is used for the blank tile)
    public final byte[][] Tabulka;

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
    public State(byte[][] Tabulka) {
        this.Tabulka = Tabulka;
    }


    // prechadzame celu tabulku, hladame 0,
    // a ked ju najdeme nahradime zatvorkami []
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (byte[] riadok : Tabulka) {
            for (byte dlazdica : riadok) {
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
    public State posun(Operator op) {
        // Create a new empty game board
        byte[][] novaTabulka = new byte[3][3];

        // Initialize the new board to the same as the current board
        for (int riadok = 0; riadok < 3; riadok++)
            for (int stlpec = 0; stlpec < 3; stlpec++)
                novaTabulka[riadok][stlpec] = Tabulka[riadok][stlpec];

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
        return new State(novaTabulka);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (obj == null || obj.getClass() != this.getClass())
            return false;

        State other = (State) obj;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (Tabulka[i][j] != other.Tabulka[i][j])
                    return false;

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        for (byte[] riadok : Tabulka)
            for (byte dlazdica : riadok)
                hash = 7*hash+dlazdica;

        return hash;
    }

    /**
     * @param ciel  Goal state
     * @return Array of positions for each of the 15 tiles
     */

    // parameter je cielovy stav
    // vrati pole pozicii pre kazdu dlazdicu
    public Pozicia[] vratitSpravnuPoziciu(State ciel) {
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
    public int manhattanDistance(State goal)
    {
        short manhattan = 0;


        Pozicia[] spravnaPozicia = vratitSpravnuPoziciu(goal);

        // Compare each tile's actual row and column to the correct row
        // and column, compute Manhattan distance, and add to sum.
        for (int riadok = 0; riadok < 3; riadok++) {
            for (int stlpec = 0; stlpec < 3; stlpec++) {
                byte dlazdica = Tabulka[riadok][stlpec];

                if (dlazdica != 0) {
                    manhattan += Math.abs(spravnaPozicia[dlazdica].riadok -riadok);
                    manhattan += Math.abs(spravnaPozicia[dlazdica].stlpec -stlpec);
                }
            }
        }

        return manhattan;
    }

    /**
     * Linear conflict heuristic.
     * Returns the sum of the Manhattan distance and the additional
     * moves required to eliminate conflicts between tiles that are in
     * their goal row or column but in the wrong order.
     *
     * @param goal  Goal state to calculate linear conflict heuristic distance from
     * @return  Linear conflict heuristic distance from goal state
     */
    public int h(State goal)
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

        // For each non-blank tile on the board
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (Tabulka[i][j] != 0) {
                    // If the tile is in its goal row
                    if (spravnaPozicia[Tabulka[i][j]].riadok == i) {
                        // For each of the following tiles in the row
                        for (int k = j + 1; k < 3; k++) {
                            // If the second tile is also in its goal row
                            // and the two tiles are in the wrong relative order
                            // then increase the conflict count for both tiles
                            if (Tabulka[i][k] != 0 &&
                                    spravnaPozicia[Tabulka[i][k]].riadok == i &&
                                    spravnaPozicia[Tabulka[i][k]].stlpec < spravnaPozicia[Tabulka[i][j]].stlpec) {
                                horizontalneKonflikty[i][k]++;
                                horizontalneKonflikty[i][j]++;
                            }
                        }
                    }
                    // If the tile is in its goal column
                    if (spravnaPozicia[Tabulka[i][j]].stlpec == j) {
                        // For each of the following tiles in the column
                        for (int k = i + 1; k < 3; k++) {
                            // If the second tile is also in its goal column
                            // and the two tiles are in the wrong relative order
                            // then increase the conflict count for both tiles
                            if (Tabulka[k][j] != 0 &&
                                    spravnaPozicia[Tabulka[k][j]].stlpec == j &&
                                    spravnaPozicia[Tabulka[k][j]].riadok < spravnaPozicia[Tabulka[i][j]].riadok) {
                                vertikalneKonflikty[k][j]++;
                                vertikalneKonflikty[i][j]++;
                            }
                        }
                    }
                }
            }
        }

        // For each row, add number of moves to eliminate conflicts to required moves
        for (int i = 0; i < 3; i++) {
            pocetKonfliktov = new int[3];
            for (int j = 0; j < 3; j++) {
                pocetKonfliktov[horizontalneKonflikty[i][j]]++;
            }
            reqMoves += movesForConflicts(pocetKonfliktov);
        }

        // For each column, add number of moves to eliminate conflicts to required moves
        for (int j = 0; j < 3; j++) {
            pocetKonfliktov = new int[3];
            for (int i = 0; i < 3; i++) {
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
            return 0; // No additional moves required

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
