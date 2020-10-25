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
        List<Queue<Uzol>> fOpenHeap = new ArrayList<>(2);
        List<Queue<Uzol>> gOpenHeap = new ArrayList<>(2);
        List<Queue<Uzol>> prOpenHeap = new ArrayList<>(2);

        // Porovnavanie uzlov pomocou Comparatora.
        // Na prioritu nie je potrebne implementovat vlastnu funkciu, lebo java poskytuje tuto funkciu
        // Vrati hodnotu -1 ak Uzol a ma mensiu hodnotu, ako Uzol b
        // Vrati hodnotu 0 ak maju rovnaku hodnotu
        // Vrati hodnotu 1 ak Uzol a ma vacsiu hodnotu ako Uzol b
        Comparator<Uzol> PrioritaNaZakladeHlbky = Comparator.comparingInt(Uzol::getHlbka);
        Comparator<Uzol> CelkovaPriorita = (a, b) -> {
            if (a.getPriorita() < b.getPriorita())
                return -1;
            else if (a.getPriorita() == b.getPriorita()) {
                return Integer.compare(a.getHlbka(), b.getHlbka());
            } else
                return 1;
        };

        // Hash tables with States as keys and Nodes as data for
        // checking if a state is in the open or closed set.
        List<Map<Stav, Uzol>> openHash = new ArrayList<>(2);
        List<Map<Stav, Uzol>> closedHash = new ArrayList<>(2);

        Stav[] zaciatocny = new Stav[] {zaciatok, ciel};
        Stav[] cielovy = new Stav[] {ciel, zaciatok};

        // For both forward and backward smery
        for (int i : smery) {
            // Create empty heap and hash maps
            fOpenHeap.add(new PriorityQueue<>());
            gOpenHeap.add(new PriorityQueue<>(PrioritaNaZakladeHlbky));
            prOpenHeap.add(new PriorityQueue<>(CelkovaPriorita));
            openHash.add(new HashMap<>());
            closedHash.add(new HashMap<>());

            // Add zaciatocny node to the open set
            Uzol n = new Uzol(zaciatocny[i], null, null, (zaciatocny[i].linearConflict(cielovy[i],linearConflict)));
            openHash.get(i).put(zaciatocny[i], n);
            fOpenHeap.get(i).add(n);
            gOpenHeap.get(i).add(n);
            prOpenHeap.get(i).add(n);
        }


        // While there are still elements in the open set
        while(!fOpenHeap.get(dopredu).isEmpty() && !fOpenHeap.get(dozadu).isEmpty()) {
            // get minimum priority
            int fwdPriority = prOpenHeap.get(dopredu).peek().getPriorita();
            int C = Math.min(fwdPriority, prOpenHeap.get(dozadu).peek().getPriorita());

            // stop condition: test U
            if (U<=Math.max(Math.max(
                    C,
                    fOpenHeap.get(dopredu).peek().getFScore()
            ),Math.max(
                    fOpenHeap.get(dozadu).peek().getFScore(),
                    gOpenHeap.get(dopredu).peek().getHlbka()+
                            gOpenHeap.get(dozadu).peek().getHlbka()+1
            ))) {

                int openNodeCount = openHash.get(dopredu).size() + openHash.get(dozadu).size() + 1;
                int closedNodeCount = closedHash.get(dopredu).size() + closedHash.get(dozadu).size();

                System.out.print("Vytvorene uzli: " + (openNodeCount + closedNodeCount));
                System.out.print(" (" + openNodeCount + " otvorene/");
                System.out.println(closedNodeCount + " zatvorene)");
                System.out.println("Dlzka cesty: " + U);

                return new Uzol[]{}; // TODO -  retrace the path
            } else if (U <= C) {
                System.out.println("U >= C, but not meeting stop condition!!");
                return null;
            }

            // decide direction to expand
            int dir = (C==fwdPriority) ? dopredu : dozadu;

            // choose n ∈ OpenF for which prF (n) = prminF and gF (n) is
            // minimum
            Uzol n = prOpenHeap.get(dir).poll();


            // get the state for the selected node
            Stav s = n.getStav();

            // Move the node from the open to closed set, remove from heaps
            openHash.get(dir).remove(s);
            closedHash.get(dir).put(s, n);
            fOpenHeap.get(dir).remove(n);
            gOpenHeap.get(dir).remove(n);
            prOpenHeap.get(dir).remove(n);

            // For each of the four possible operators
            for (Stav.Operator op : Stav.Operator.values()) {
                // Create a new state that is the result of the posun
                Stav newStav = s.posun(op);

                // If the posun is invalid
                if (newStav == null) {
                    continue;
                }

                Uzol newUzol = null;
                // if c ∈ OpenF ∪ ClosedF and  gF (c) ≤ gF (n) + cost(n, c) then continue
                {
                    newUzol = openHash.get(dir).get(newStav);
                    if (newUzol == null) {
                        newUzol = closedHash.get(dir).get(newStav);
                    }
                    // the child is in the open or closed list
                    if (newUzol != null) {
                        // test if cost is lower now
                        if (newUzol.getHlbka() <= n.getHlbka() + 1) {
                            continue;
                        }
                        openHash.get(dir).remove(newStav);
                        fOpenHeap.get(dir).remove(newUzol);
                        gOpenHeap.get(dir).remove(newUzol);
                        prOpenHeap.get(dir).remove(newUzol);
                        closedHash.get(dir).remove(newStav);
                        newUzol.setHlbka((short) (n.getHlbka()+1));
                        newUzol.setPredchadzajuci(n);
                        newUzol.setOperator(op);
                    }
                }

                // create new node for this state, if not already found in open/closed lists
                if (newUzol == null) {
                    newUzol = new Uzol(newStav, n, op, (newStav.linearConflict(cielovy[dir], linearConflict)));
                }

                // add c to OpenF
                openHash.get(dir).put(newStav, newUzol);
                fOpenHeap.get(dir).add(newUzol);
                gOpenHeap.get(dir).add(newUzol);
                prOpenHeap.get(dir).add(newUzol);

                // if c ∈ OpenB then U :=min(U,gF(c)+gB(c))
                Uzol matchedUzol = openHash.get(1-dir).get(newStav);
                if (matchedUzol != null) {
                    U = Math.min(U,
                            matchedUzol.getHlbka() +
                                    newUzol.getHlbka()
                    );
                    if (dir==dopredu) {
                        System.out.println("Found path: Forward depth:" + newUzol.getHlbka() + " backward depth: " + matchedUzol.getHlbka());


                    } else {
                        System.out.println("Found path: Forward depth:" + matchedUzol.getHlbka() + " backward depth: " + newUzol.getHlbka());
                    }
                    System.out.println(newUzol.pathToString());
                    System.out.println(matchedUzol.revPathToStringSkipFirst());

                }
            }


        }
        return null;    // No solution found
    }

}
