import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class Algoritmus {

    // Funkcia vráti pole dvoch uzlov, kde sa stretávajú
    // Prvý uzol má smerník, ktorý ukazuje na predchádzajúci uzol, a tak ďalej až kým sa nevrátime k začiatočneho uzlu
    // Druhý uzol má smerník, ktorý ukazuje na predchádzajúci uzol, a tak ďalej až kým sa nevrátime k cieľoveho uzlu
    public static Uzol[] start(Stav zaciatok, Stav ciel, boolean linearConflict) {

        final int DOPREDU = 0;
        final int DOZADU = 1;
        int maxHodnota = Integer.MAX_VALUE;
        boolean stop = false;
        int dlzkaCesty = 0;

        int[] smery = {DOPREDU, DOZADU};

        // Vytvoríme 3 minimálne haldy
        List<Queue<Uzol>> prOhalda = new ArrayList<>(2);
        List<Queue<Uzol>> hlOhalda = new ArrayList<>(2);
        List<Queue<Uzol>> cprOhalda = new ArrayList<>(2);

        // Porovnavanie uzlov pomocou Comparatora
        // Pre prioritu nie je potrebné implementovať vlastnú funkciu, lebo Java to poskytuje
        Comparator<Uzol> HlbkovaHalda = Comparator.comparingInt(Uzol::getHlbka);
        Comparator<Uzol> CelaHalda = (a, b) -> {
            if (a.getPriorita() < b.getPriorita())
                return -1;
            else if (a.getPriorita() == b.getPriorita())
                return Integer.compare(a.getHlbka(), b.getHlbka());
            else
                return 1;
        };

        // Hash tabuľky pre otvorené a zatvorené sety
        List<Map<Stav, Uzol>> oHashT = new ArrayList<>(2);
        List<Map<Stav, Uzol>> zHashT = new ArrayList<>(2);

        // Vytovríme dve stavy pre uzli.
        // Začiatočný stav má začiatok a cieľ normálne
        // Cieľový stav začne z cieľa, a jeho cieľ je začiatok, takže má to presne naopak
        Stav[] zaciatocny = new Stav[] {zaciatok, ciel};
        Stav[] cielovy = new Stav[] {ciel, zaciatok};

        // pre oba smery (DOZADU a DOPREDU)
        for (int i : smery)
        {
            // Vytvoríme prázdne haldy
            prOhalda.add(new PriorityQueue<>());
            hlOhalda.add(new PriorityQueue<>(HlbkovaHalda));
            cprOhalda.add(new PriorityQueue<>(CelaHalda));
            // Vytvoríme hash mapy
            oHashT.add(new HashMap<>());
            zHashT.add(new HashMap<>());

            // Pre obe smery vložíme tam začiatočný uzol
            Uzol prvy = new Uzol(zaciatocny[i], null, null, zaciatocny[i].linearConflict(cielovy[i],linearConflict));
            oHashT.get(i).put(zaciatocny[i], prvy);
            prOhalda.get(i).add(prvy);
            hlOhalda.get(i).add(prvy);
            cprOhalda.get(i).add(prvy);
        }

        // kym su prvky v otvorenom setu
        while(!prOhalda.get(DOPREDU).isEmpty() && !prOhalda.get(DOZADU).isEmpty())
        {
            // uzol s najnizsou prioritou
            int prUzlaDopredu = cprOhalda.get(DOPREDU).peek().getPriorita();
            int prUzlaDozadu = cprOhalda.get(DOZADU).peek().getPriorita();
            int minimumPr = Math.min(prUzlaDopredu, prUzlaDozadu);
            int max1 = Math.max(minimumPr, prOhalda.get(DOPREDU).peek().getCelkovaPriorita());
            int max2 = Math.max(prOhalda.get(DOZADU).peek().getCelkovaPriorita(), hlOhalda.get(DOPREDU).peek().getHlbka()+ hlOhalda.get(DOZADU).peek().getHlbka()+1);

            // aby sme nevytvorili nekonecne vela uzlov, mame podmienku
            if (stop)
            {
                int pocetUzlovOtv = oHashT.get(DOPREDU).size() + oHashT.get(DOZADU).size() + 1;
                int pocetUzlovZatv = zHashT.get(DOPREDU).size() + zHashT.get(DOZADU).size();

                System.out.print("Vytvorene uzli: " + (pocetUzlovOtv + pocetUzlovZatv));
                System.out.print(" (" + pocetUzlovOtv + " otvorene/");
                System.out.println(pocetUzlovZatv + " zatvorene)");
                System.out.println("Dlzka cesty: " + dlzkaCesty);

                return new Uzol[]{};
            }
            else if (dlzkaCesty != 0 && dlzkaCesty<= minimumPr)
            {
                System.out.println("maxHodnota >= minimumPr, but not meeting stop condition!!");
                return null;
            }

            int smer;
            if (minimumPr == prUzlaDopredu)
                smer = DOPREDU;

            else
                smer = DOZADU;

            // odstranime uzol z haldy
            Uzol n = cprOhalda.get(smer).poll();

            assert n != null;
            Stav s = n.getStav();

            // premiestnime uzol z otvoreneho setu, vlozimo ho do zatvoreneho, a odstranime ho z haldy
            oHashT.get(smer).remove(s);
            zHashT.get(smer).put(s, n);
            prOhalda.get(smer).remove(n);
            hlOhalda.get(smer).remove(n);
            cprOhalda.get(smer).remove(n);

            // pre kazdy operator vytvorime stav, ktory je vysledkom posunu
            for (Stav.Operator op : Stav.Operator.values()) {
                Stav novyStav = s.posun(op);

                if (novyStav == null)
                    continue;

                Uzol novyUzol;
                {
                    novyUzol = oHashT.get(smer).get(novyStav);
                    if (novyUzol == null) {
                        novyUzol = zHashT.get(smer).get(novyStav);
                    }
                    // ak uzol je v otvorenom alebo v zatvorenom setu
                    if (novyUzol != null) {
                        // kontrolujeme, ci hodnota je uz nizsia
                        if (novyUzol.getHlbka() <= n.getHlbka() + 1) {
                            continue;
                        }
                        oHashT.get(smer).remove(novyStav);
                        prOhalda.get(smer).remove(novyUzol);
                        hlOhalda.get(smer).remove(novyUzol);
                        cprOhalda.get(smer).remove(novyUzol);
                        zHashT.get(smer).remove(novyStav);
                        novyUzol.setHlbka((short) (n.getHlbka()+1));
                        novyUzol.setPredchadzajuci(n);
                        novyUzol.setOperator(op);
                    }
                }

                // vytvorime novy uzol, ak este nie je v otvorenom alebo v zatvorenom setu
                if (novyUzol == null)
                    novyUzol = new Uzol(novyStav, n, op, novyStav.linearConflict(cielovy[smer], linearConflict));

                // Vložíme ho do hash tabulky a do troch háld
                oHashT.get(smer).put(novyStav, novyUzol);
                prOhalda.get(smer).add(novyUzol);
                hlOhalda.get(smer).add(novyUzol);
                cprOhalda.get(smer).add(novyUzol);

                Uzol matchedUzol = oHashT.get(1-smer).get(novyStav);
                if (matchedUzol != null)
                {
                    stop = true;
                    dlzkaCesty = matchedUzol.getHlbka()+novyUzol.getHlbka();
                    if (dlzkaCesty == 2 && novyUzol.getHlbka() == 1 && matchedUzol.getHlbka() == 1)
                        return null;


                    else {
                        if (smer == DOPREDU)
                        {
                            System.out.println("Smer: Dopredu");
                            System.out.println("Hlbka dopredu:" + novyUzol.getHlbka() + "\nHlbka dozadu: " + matchedUzol.getHlbka());
                            System.out.println(novyUzol.cestaZoStartuKaktualnej());
                            System.out.println("Uzly sa stretli tu. Cielovy stav prveho uzla je zaciatocny stav druheho uzla.\n");
                            if (matchedUzol.getHlbka() == 0)
                                System.out.println("Druhy uzol nevykonal ziadne kroky.\n");
                            else
                                System.out.println(matchedUzol.cestaNaspatVynechajPrvu(false));
                        }

                        else
                        {
                            System.out.println("Smer: Dozadu");
                            System.out.println("Hlbka dopredu:" + matchedUzol.getHlbka() + "\nHlbka dozadu: " + novyUzol.getHlbka());
                            System.out.println(matchedUzol.cestaZoStartuKaktualnej());
                            System.out.println("Uzly sa stretli tu. Cielovy stav prveho uzla je zaciatocny stav druheho uzla.");
                            System.out.println(novyUzol.cestaNaspatVynechajPrvu(true));
                        }
                    }

                }
            }
        }
        return null; // Nema riesenie
    }
}
