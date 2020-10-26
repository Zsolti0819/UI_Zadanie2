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

        // Vytovríme dve stavy pre uzly.
        // Začiatočný stav má začiatok a cieľ normálne
        // Cieľový stav začne z cieľa, a jeho cieľ je začiatok, takže má to presne naopak
        Stav[] zaciatocny = new Stav[] {zaciatok, ciel};
        Stav[] cielovy = new Stav[] {ciel, zaciatok};

        // pre oba smery (DOZADU a DOPREDU)
        for (int i = 0; i < smery.length; i++)
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

            if (stop)
            {
                int pocetUzlovOtv = oHashT.get(DOPREDU).size() + oHashT.get(DOZADU).size() + 1;
                int pocetUzlovZatv = zHashT.get(DOPREDU).size() + zHashT.get(DOZADU).size();

                System.out.print("Vytvorene uzly: " + (pocetUzlovOtv + pocetUzlovZatv));
                System.out.print(" (" + pocetUzlovOtv + " otvorene/");
                System.out.println(pocetUzlovZatv + " zatvorene)");
                System.out.println("Dlzka cesty: " + dlzkaCesty);

                return new Uzol[]{};
            }

            int smer;
            if (minimumPr == prUzlaDopredu)
                smer = DOPREDU;

            else
                smer = DOZADU;

            // Odstránime uzol z otvorenej haldy
            Uzol novy = cprOhalda.get(smer).poll();

            assert novy != null;
            Stav s = novy.getStav();

            // Premiestníme uzol z otvoreného setu, vložíme ho do zatvoreného, a odstránime ho z haldy
            oHashT.get(smer).remove(s);
            zHashT.get(smer).put(s, novy);
            prOhalda.get(smer).remove(novy);
            hlOhalda.get(smer).remove(novy);
            cprOhalda.get(smer).remove(novy);

            // Pre každý operator vytvoríme stav, ktorý je výsledkom posunu
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
                    // Ak uzol je v otvorenom alebo v zatvorenom sete
                    if (novyUzol != null) {
                        // Kontrolujeme, či hodnota je už nižšia
                        if (novyUzol.getHlbka() <= novy.getHlbka() + 1) {
                            continue;
                        }
                        oHashT.get(smer).remove(novyStav);
                        prOhalda.get(smer).remove(novyUzol);
                        hlOhalda.get(smer).remove(novyUzol);
                        cprOhalda.get(smer).remove(novyUzol);
                        zHashT.get(smer).remove(novyStav);
                        novyUzol.setHlbka((novy.getHlbka()+1));
                        novyUzol.setPredchadzajuci(novy);
                        novyUzol.setOperator(op);
                    }
                }

                // Vytvoríme novyUzol, ak este nie je v otvorenom alebo v zatvorenom sete
                if (novyUzol == null)
                    novyUzol = new Uzol(novyStav, novy, op, novyStav.linearConflict(cielovy[smer], linearConflict));

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
                    {
                        Main.netrebaRiesit = true;
                        return null;
                    }

                    else {
                        if (smer == DOPREDU)
                        {
                            System.out.println("Smer: Dopredu");
                            System.out.println("Hlbka dopredu:" + novyUzol.getHlbka() + "\nHlbka dozadu: " + matchedUzol.getHlbka());
                            System.out.println(novyUzol.cestaZoStartuKaktualnej());
                            System.out.println("Uzly sa stretli tu. Cielovy stav prveho uzla je zaciatocny stav druheho uzla.");
                            if (matchedUzol.getHlbka() == 0)
                                System.out.println("Druhy uzol nevykonal ziadne kroky.");
                            else
                                System.out.println(matchedUzol.cestaNaspatVynechajPrvu(false));
                        }

                        else
                        {
                            System.out.println("Smer: Dozadu");
                            System.out.println("Hlbka dopredu:" + matchedUzol.getHlbka() + "\nHlbka dozadu: " + novyUzol.getHlbka());
                            System.out.println(matchedUzol.cestaZoStartuKaktualnej());
                            System.out.println("Uzly sa stretli tu. Cielovy stav prveho uzla je zaciatocny stav druheho uzla.");
                            System.out.println(novyUzol.cestaNaspatVynechajPrvu(false));
                        }
                    }

                }
            }
        }
        return null; // Nemá riešenie
    }
}
