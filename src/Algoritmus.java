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

        final int dopredu = 0;
        final int dozadu = 1;
        int U = Integer.MAX_VALUE;

        int[] smery = {dopredu, dozadu};

        // minimalna halda na odstranenie uzla z otvoreneho setu s najmensou prioritou
        List<Queue<Uzol>> prioritaOtvorenaHalda = new ArrayList<>(2);
        List<Queue<Uzol>> hlbkaOtvorenaHalda = new ArrayList<>(2);
        List<Queue<Uzol>> celkovaPrioritaOtvorenaHalda = new ArrayList<>(2);

        // Porovnavanie uzlov pomocou Comparatora.
        // Pre prioritu nie je potrebne implementovat vlastnu funkciu, lebo java poskytuje tuto funkciu
        Comparator<Uzol> PrioritaNaZakladeHlbky = Comparator.comparingInt(Uzol::getHlbka);
        Comparator<Uzol> CelkovaPriorita = (a, b) -> {
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
        List<Map<Stav, Uzol>> otvorenyHash = new ArrayList<>(2);
        List<Map<Stav, Uzol>> zatvorenyHash = new ArrayList<>(2);

        Stav[] zaciatocny = new Stav[] {zaciatok, ciel};
        Stav[] cielovy = new Stav[] {ciel, zaciatok};

        for (int i : smery)
        {
            // vytvorime prazdne haldy
            prioritaOtvorenaHalda.add(new PriorityQueue<>());
            hlbkaOtvorenaHalda.add(new PriorityQueue<>(PrioritaNaZakladeHlbky));
            celkovaPrioritaOtvorenaHalda.add(new PriorityQueue<>(CelkovaPriorita));
            // a hash mapy
            otvorenyHash.add(new HashMap<>());
            zatvorenyHash.add(new HashMap<>());

            // vlozime prvy uzol do otvoreneho setu
            Uzol prvy = new Uzol(zaciatocny[i], null, null, zaciatocny[i].linearConflict(cielovy[i],linearConflict));
            otvorenyHash.get(i).put(zaciatocny[i], prvy);
            prioritaOtvorenaHalda.get(i).add(prvy);
            hlbkaOtvorenaHalda.get(i).add(prvy);
            celkovaPrioritaOtvorenaHalda.get(i).add(prvy);
        }

        // kym su prvky v otvorenom setu
        while(!prioritaOtvorenaHalda.get(dopredu).isEmpty() && !prioritaOtvorenaHalda.get(dozadu).isEmpty())
        {
            // uzol s najnizsou prioritou
            int fwdPriority = celkovaPrioritaOtvorenaHalda.get(dopredu).peek().getPriorita();
            int C = Math.min(fwdPriority, celkovaPrioritaOtvorenaHalda.get(dozadu).peek().getPriorita());

            // stop condition: test U
            if (U <= Math.max(Math.max(C, prioritaOtvorenaHalda.get(dopredu).peek().getFScore()),Math.max(prioritaOtvorenaHalda.get(dozadu).peek().getFScore(), hlbkaOtvorenaHalda.get(dopredu).peek().getHlbka()+ hlbkaOtvorenaHalda.get(dozadu).peek().getHlbka()+1)))
            {
                int pocetUzlovOtv = otvorenyHash.get(dopredu).size() + otvorenyHash.get(dozadu).size() + 1;
                int pocetUzlovZatv = zatvorenyHash.get(dopredu).size() + zatvorenyHash.get(dozadu).size();

                System.out.print("Vytvorene uzli: " + (pocetUzlovOtv + pocetUzlovZatv));
                System.out.print(" (" + pocetUzlovOtv + " otvorene/");
                System.out.println(pocetUzlovZatv + " zatvorene)");
                System.out.println("Dlzka cesty: " + U);

                return new Uzol[]{};
            }
            else if (U <= C) {
                System.out.println("U >= C, but not meeting stop condition!!");
                return null;
            }

            // decide direction to expand
            int smer = (C==fwdPriority) ? dopredu : dozadu;

            // choose n ∈ OpenF for which prF (n) = prminF and gF (n) is
            // minimum
            Uzol n = celkovaPrioritaOtvorenaHalda.get(smer).poll();

            assert n != null;
            Stav s = n.getStav();

            // premiestnime uzol z otvoreneho setu, vlozimo ho to zatvoreneho, a odstranime ho z haldy
            otvorenyHash.get(smer).remove(s);
            zatvorenyHash.get(smer).put(s, n);
            prioritaOtvorenaHalda.get(smer).remove(n);
            hlbkaOtvorenaHalda.get(smer).remove(n);
            celkovaPrioritaOtvorenaHalda.get(smer).remove(n);

            // pre kazdy operator vytvorime stav, ktory je vysledkom posunu
            for (Stav.Operator op : Stav.Operator.values()) {
                Stav novyStav = s.posun(op);

                if (novyStav == null)
                    continue;

                Uzol novyUzol;
                // if c ∈ OpenF ∪ ClosedF and  gF (c) ≤ gF (n) + cost(n, c) then continue
                {
                    novyUzol = otvorenyHash.get(smer).get(novyStav);
                    if (novyUzol == null) {
                        novyUzol = zatvorenyHash.get(smer).get(novyStav);
                    }
                    // the child is in the open or closed list
                    if (novyUzol != null) {
                        // test if cost is lower now
                        if (novyUzol.getHlbka() <= n.getHlbka() + 1) {
                            continue;
                        }
                        otvorenyHash.get(smer).remove(novyStav);
                        prioritaOtvorenaHalda.get(smer).remove(novyUzol);
                        hlbkaOtvorenaHalda.get(smer).remove(novyUzol);
                        celkovaPrioritaOtvorenaHalda.get(smer).remove(novyUzol);
                        zatvorenyHash.get(smer).remove(novyStav);
                        novyUzol.setHlbka((short) (n.getHlbka()+1));
                        novyUzol.setPredchadzajuci(n);
                        novyUzol.setOperator(op);
                    }
                }

                // create new node for this state, if not already found in open/closed lists
                if (novyUzol == null)
                    novyUzol = new Uzol(novyStav, n, op, novyStav.linearConflict(cielovy[smer], linearConflict));

                // add c to OpenF
                otvorenyHash.get(smer).put(novyStav, novyUzol);
                prioritaOtvorenaHalda.get(smer).add(novyUzol);
                hlbkaOtvorenaHalda.get(smer).add(novyUzol);
                celkovaPrioritaOtvorenaHalda.get(smer).add(novyUzol);

                // if c ∈ OpenB then U :=min(U,gF(c)+gB(c))
                Uzol matchedUzol = otvorenyHash.get(1-smer).get(novyStav);
                if (matchedUzol != null)
                {
                    U = Math.min(U, matchedUzol.getHlbka() + novyUzol.getHlbka());
                    if (smer == dopredu)
                        System.out.println("Found path: Forward depth:" + novyUzol.getHlbka() + " backward depth: " + matchedUzol.getHlbka());
                    else
                        System.out.println("Found path: Forward depth:" + matchedUzol.getHlbka() + " backward depth: " + novyUzol.getHlbka());

                    System.out.println(novyUzol.cestaZoStartuKaktualnej());
                    System.out.println("Uzli sa stretli tu:");
                    System.out.println(matchedUzol.cestaNaspat());

                }
            }
        }
        return null; // Nema riesenie
    }

}
