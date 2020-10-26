public class Stav {

    public final int[][] Tabulka;

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

    public Stav(int[][] Tabulka) {
        this.Tabulka = Tabulka;
    }

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

    public Stav posun(Operator operator) {
        int[][] novaTabulka = new int[Main.rozmer][Main.rozmer];

        // Inicializácia novej tabuľky na aktuálnu
        for (int riadok = 0; riadok < Main.rozmer; riadok++)
            System.arraycopy(Tabulka[riadok], 0, novaTabulka[riadok], 0, Main.rozmer);

        // hladanie dlazdici
        for (int riadok = 0; riadok < Main.rozmer; riadok++) {
            for (int stlpec = 0; stlpec < Main.rozmer; stlpec++) {
                if (Tabulka[riadok][stlpec] == 0)
                {

                    // Skúsime aplikovať operátora
                    // posunutim dlazdici na urcene miesto
                    switch (operator) {
                        case Hore:
                            if (riadok > 0)
                            {
                                novaTabulka[riadok][stlpec] = Tabulka[riadok-1][stlpec];
                                novaTabulka[riadok-1][stlpec] = 0;
                            }
                            else
                                return null;
                            break;
                        case Dole:
                            if (riadok < Main.rozmer-1)
                            {
                                novaTabulka[riadok][stlpec] = Tabulka[riadok+1][stlpec];
                                novaTabulka[riadok+1][stlpec] = 0;
                            }
                            else
                                return null;

                            break;
                        case Vlavo:
                            if (stlpec > 0)
                            {
                                novaTabulka[riadok][stlpec] = Tabulka[riadok][stlpec-1];
                                novaTabulka[riadok][stlpec-1] = 0;
                            }
                            else
                                return null;

                            break;
                        case Vpravo:
                            if (stlpec < Main.rozmer-1)
                            {
                                novaTabulka[riadok][stlpec] = Tabulka[riadok][stlpec+1];
                                novaTabulka[riadok][stlpec+1] = 0;
                            }
                            else
                                return null;

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
        for (int i = 0; i < Main.rozmer; i++)
            for (int j = 0; j < Main.rozmer; j++)
                if (Tabulka[i][j] != other.Tabulka[i][j])
                    return false;

        return true;
    }

    @Override
    public int hashCode() {
        int hash = Main.rozmer;
        for (int[] riadok : Tabulka)
            for (int dlazdica : riadok)
                hash = 7*hash+dlazdica;

        return hash;
    }

    public Pozicia[] vratitSpravnuPoziciu(Stav ciel) {
        if (ciel.AktualnaPozicia == null)
        {
            ciel.AktualnaPozicia = new Pozicia[(int) Math.pow(Main.rozmer, 2)];

            // najde spravnu poziciu kazdej dlazdice
            for (int riadok = 0; riadok < Main.rozmer; riadok++)
            {
                for (int stlpec = 0; stlpec < Main.rozmer; stlpec++)
                {
                    if (ciel.Tabulka[riadok][stlpec] != 0)
                        ciel.AktualnaPozicia[ciel.Tabulka[riadok][stlpec]] = new Pozicia(riadok, stlpec);
                }
            }
        }
        return ciel.AktualnaPozicia;
    }

    public int vypovitajManhattanDistance(Stav ciel)
    {
        int manhattan = 0;
        Pozicia[] spravnaPozicia = vratitSpravnuPoziciu(ciel);
        for (int riadok = 0; riadok < Main.rozmer; riadok++) {
            for (int stlpec = 0; stlpec < Main.rozmer; stlpec++) {
                int dlazdica = Tabulka[riadok][stlpec];

                if (dlazdica != 0) {
                    manhattan += Math.abs(spravnaPozicia[dlazdica].riadok -riadok);
                    manhattan += Math.abs(spravnaPozicia[dlazdica].stlpec -stlpec);
                }
            }
        }

        return manhattan;
    }

    public int linearConflict(Stav goal, boolean linearConflict)
    {
        if (!linearConflict)
            return vypovitajManhattanDistance(goal);

        int potrebneKroky = 0;

        Pozicia[] spravnaPozicia = vratitSpravnuPoziciu(goal);

        int[][] horizontalneKonflikty = new int[Main.rozmer][Main.rozmer];
        int[][] vertikalneKonflikty = new int[Main.rozmer][Main.rozmer];

        // pocetKonfliktov[i] is the number of tiles in a row or column
        // that have i conflicts with other tiles in the same row/column
        int[] pocetKonfliktov;

        int i = 0;
        int j = 0;
        int k;

        while (i < Main.rozmer)
        {
            while (j < Main.rozmer)
            {
                if (Tabulka[i][j] != 0)
                {
                    if (spravnaPozicia[Tabulka[i][j]].riadok == i)                                                      // ak dlazdica je v spravnom riadku
                    {
                        k = j + 1;
                        while (k < Main.rozmer)
                        {                                                                                               // tak pre kazdu dlazdicu v tom riadku
                            if (Tabulka[i][k] != 0 &&                                                                   // dlazdica nie je prazdne miesto
                                    spravnaPozicia[Tabulka[i][k]].riadok == i &&                                        // nasledujuca dlazdica napravo je tiez v spravnom riadku
                                    spravnaPozicia[Tabulka[i][k]].stlpec < spravnaPozicia[Tabulka[i][j]].stlpec) {      // ale dlazdice su v nespravnom poradi
                                horizontalneKonflikty[i][k]++;                                                          // zvysujeme pocet konfilktov pre obe dlazdice
                                horizontalneKonflikty[i][j]++;
                            }
                            k++;
                        }
                    }
                    if (spravnaPozicia[Tabulka[i][j]].stlpec == j)                                                      // ak dlazdica je v spravnom stlpci
                    {
                        k = i + 1;
                        while (k < Main.rozmer)
                        {                                                                                               // tak pre kazdu dlazdicu v tom stlpci
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

        for (i = 0; i < Main.rozmer; i++)
        {
            pocetKonfliktov = new int[Main.rozmer];
            for (j = 0; j < Main.rozmer; j++)
                pocetKonfliktov[horizontalneKonflikty[i][j]]++;
            potrebneKroky += potrebneKrokyPreRiesenieKonfliktu(pocetKonfliktov);
        }

        for (j = 0; j < Main.rozmer; j++)
        {
            pocetKonfliktov = new int[Main.rozmer];
            for (i = 0; i < Main.rozmer; i++)
                pocetKonfliktov[vertikalneKonflikty[i][j]]++;
            potrebneKroky += potrebneKrokyPreRiesenieKonfliktu(pocetKonfliktov);
        }
        return  potrebneKroky + vypovitajManhattanDistance(goal);
    }

    private int potrebneKrokyPreRiesenieKonfliktu(int[] pocetKonfliktov)
    {
        if (Main.rozmer == 3)
        {
            if (pocetKonfliktov[0] == 3)
                return 0;

            else if (pocetKonfliktov[2] == 3)
                return 4;

            else if (pocetKonfliktov[1] == 2 && pocetKonfliktov[1] == 1)
                return 2;

            else
                return 3;
        }

        else
        {
            if (pocetKonfliktov[0] == 4)
                return 0;

            else if (pocetKonfliktov[3] == 4)
                return 6;

            else if (pocetKonfliktov[1] == 2 && pocetKonfliktov[2] != 2 || pocetKonfliktov[1] == 3)
                return 2;

            else
                return 4;

        }
    }
}
