import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class Algoritmus {

    // funkcia vrati pole dvoch uzlov, kde sa stretavaju
    // prvy uzol ma smernik, ktory ukazuje na predchadzajuci uzol, az k zaciatocnej pozicii
    // druhy uzol ma smernik, ktory ukazuje na predchadzajuci uzol, az k cielovej pozicii
    public static Uzol[] start(Stav zaciatok, Stav ciel, boolean linearConflict) {

        final int DOPREDU = 0;
        final int DOZADU = 1;
        int maxHodnota = Integer.MAX_VALUE;

        int[] smery = {DOPREDU, DOZADU};

        // minimalna halda na odstranenie uzla z otvoreneho setu s najmensou prioritou
        List<Queue<Uzol>> prOtvHalda = new ArrayList<>(2);
        List<Queue<Uzol>> hlbkaOtvHalda = new ArrayList<>(2);
        List<Queue<Uzol>> cPrOtvHalda = new ArrayList<>(2);

        // Porovnavanie uzlov pomocou Comparatora.
        // Pre prioritu nie je potrebne implementovat vlastnu funkciu, lebo java poskytuje tuto funkciu
        Comparator<Uzol> HlbkovaHalda = Comparator.comparingInt(Uzol::getHlbka);
        Comparator<Uzol> CelaHalda = (a, b) -> {
            if (a.getPriorita() < b.getPriorita())
                return -1;
            else if (a.getPriorita() == b.getPriorita())
                return Integer.compare(a.getHlbka(), b.getHlbka());
            else
                return 1;
        };


        // hash tabulky
        // kluce su stavy uzlov
        // uzli su ako data, sluzi na to, aby sme zistili, ci dany stav je v otvorenom alebo zatvorenom setu
        List<Map<Stav, Uzol>> otvHashT = new ArrayList<>(2);
        List<Map<Stav, Uzol>> zatvHashT = new ArrayList<>(2);

        Stav[] zaciatocny = new Stav[] {zaciatok, ciel};
        Stav[] cielovy = new Stav[] {ciel, zaciatok};

        // pre oba smery (DOZADU a DOPREDU)
        for (int i : smery)
        {
            // vytvorime prazdne haldy
            prOtvHalda.add(new PriorityQueue<>());
            hlbkaOtvHalda.add(new PriorityQueue<>(HlbkovaHalda));
            cPrOtvHalda.add(new PriorityQueue<>(CelaHalda));
            // a hash mapy
            otvHashT.add(new HashMap<>());
            zatvHashT.add(new HashMap<>());

            // vlozime prvy uzol do otvoreneho setu
            Uzol prvy = new Uzol(zaciatocny[i], null, null, zaciatocny[i].linearConflict(cielovy[i],linearConflict));
            otvHashT.get(i).put(zaciatocny[i], prvy);
            prOtvHalda.get(i).add(prvy);
            hlbkaOtvHalda.get(i).add(prvy);
            cPrOtvHalda.get(i).add(prvy);
        }

        // kym su prvky v otvorenom setu
        while(!prOtvHalda.get(DOPREDU).isEmpty() && !prOtvHalda.get(DOZADU).isEmpty())
        {
            // uzol s najnizsou prioritou
            int prUzlaDopredu = cPrOtvHalda.get(DOPREDU).peek().getPriorita();
            int prUzlaDozadu = cPrOtvHalda.get(DOZADU).peek().getPriorita();
            int minimumPr = Math.min(prUzlaDopredu, prUzlaDozadu);
            int max1 = Math.max(minimumPr, prOtvHalda.get(DOPREDU).peek().getCelkovaPriorita());
            int max2 = Math.max(prOtvHalda.get(DOZADU).peek().getCelkovaPriorita(), hlbkaOtvHalda.get(DOPREDU).peek().getHlbka()+ hlbkaOtvHalda.get(DOZADU).peek().getHlbka()+1);
            int maximum = Math.max(max1, max2);

            // aby sme nevytvorili nekonecne vela uzlov, mame podmienku
            if (maxHodnota <= maximum)
            {
                int pocetUzlovOtv = otvHashT.get(DOPREDU).size() + otvHashT.get(DOZADU).size() + 1;
                int pocetUzlovZatv = zatvHashT.get(DOPREDU).size() + zatvHashT.get(DOZADU).size();

                System.out.print("Vytvorene uzli: " + (pocetUzlovOtv + pocetUzlovZatv));
                System.out.print(" (" + pocetUzlovOtv + " otvorene/");
                System.out.println(pocetUzlovZatv + " zatvorene)");
                System.out.println("Dlzka cesty: " + maxHodnota);

                return new Uzol[]{};
            }
            else if (maxHodnota <= minimumPr)
            {
                System.out.println("maxHodnota >= minimumPr, but not meeting stop condition!!");
                return null;
            }

            int smer;
            if (minimumPr == prUzlaDopredu)
            {
                smer = DOPREDU;
            }

            else
            {
                smer = DOZADU;
            }



            // odstranime uzol z haldy
            Uzol n = cPrOtvHalda.get(smer).poll();

            assert n != null;
            Stav s = n.getStav();

            // premiestnime uzol z otvoreneho setu, vlozimo ho do zatvoreneho, a odstranime ho z haldy
            otvHashT.get(smer).remove(s);
            zatvHashT.get(smer).put(s, n);
            prOtvHalda.get(smer).remove(n);
            hlbkaOtvHalda.get(smer).remove(n);
            cPrOtvHalda.get(smer).remove(n);

            // pre kazdy operator vytvorime stav, ktory je vysledkom posunu
            for (Stav.Operator op : Stav.Operator.values()) {
                Stav novyStav = s.posun(op);

                if (novyStav == null)
                    continue;

                Uzol novyUzol;
                {
                    novyUzol = otvHashT.get(smer).get(novyStav);
                    if (novyUzol == null) {
                        novyUzol = zatvHashT.get(smer).get(novyStav);
                    }
                    // ak uzol je v otvorenom alebo v zatvorenom setu
                    if (novyUzol != null) {
                        // kontrolujeme, ci hodnota je uz nizsia
                        if (novyUzol.getHlbka() <= n.getHlbka() + 1) {
                            continue;
                        }
                        otvHashT.get(smer).remove(novyStav);
                        prOtvHalda.get(smer).remove(novyUzol);
                        hlbkaOtvHalda.get(smer).remove(novyUzol);
                        cPrOtvHalda.get(smer).remove(novyUzol);
                        zatvHashT.get(smer).remove(novyStav);
                        novyUzol.setHlbka((short) (n.getHlbka()+1));
                        novyUzol.setPredchadzajuci(n);
                        novyUzol.setOperator(op);
                    }
                }

                // vytvorime novy uzol, ak este nie je v otvorenom alebo v zatvorenom setu
                if (novyUzol == null)
                    novyUzol = new Uzol(novyStav, n, op, novyStav.linearConflict(cielovy[smer], linearConflict));

                // vlozime ho do hash tabulky a do 3 haldach
                otvHashT.get(smer).put(novyStav, novyUzol);
                prOtvHalda.get(smer).add(novyUzol);
                hlbkaOtvHalda.get(smer).add(novyUzol);
                cPrOtvHalda.get(smer).add(novyUzol);

                Uzol matchedUzol = otvHashT.get(1-smer).get(novyStav);
                if (matchedUzol != null)
                {
                    maxHodnota = Math.min(maxHodnota, matchedUzol.getHlbka() + novyUzol.getHlbka());
                    if (smer == DOPREDU)
                        System.out.println("Found path: Forward depth:" + novyUzol.getHlbka() + " backward depth: " + matchedUzol.getHlbka());
                    else
                        System.out.println("Found path: Forward depth:" + matchedUzol.getHlbka() + " backward depth: " + novyUzol.getHlbka());

                    System.out.println(novyUzol.cestaZoStartuKaktualnej());
                    System.out.println("Uzli sa stretli tu:");
                    System.out.println(matchedUzol.cestaNaspatVynechajPrvu());

                }
            }
        }
        return null; // Nema riesenie
    }
}
