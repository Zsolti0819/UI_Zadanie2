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
    public static Uzol[] start(Stav zaciatok, Stav ciel) {

        final int dopredu = 0;
        final int dozadu = 1;
        int U = Integer.MAX_VALUE;

        int[] smery = {dopredu, dozadu};

        // minimalna halda na odstranenie uzla z otvoreneho setu s najmensou prioritou
        List<Queue<Uzol>> fOpenHeap = new ArrayList<Queue<Uzol>>(2);
        List<Queue<Uzol>> gOpenHeap = new ArrayList<Queue<Uzol>>(2);
        List<Queue<Uzol>> prOpenHeap = new ArrayList<Queue<Uzol>>(2);

        // set comparators. by f is built in the Uzol object
        Comparator<Uzol> PrioritaNaZakladeHlbky = new Comparator<Uzol>() {
            @Override
            public int compare(Uzol a, Uzol b) {
                return Integer.compare(a.getDepth(), b.getDepth());
            }
        };
        Comparator<Uzol> CelkovaPriorita = new Comparator<Uzol>() {
            @Override
            public int compare(Uzol a, Uzol b) {
                if (a.getPriority() < b.getPriority())
                    return -1;
                else if (a.getPriority() == b.getPriority()){
                    if (a.getDepth() < b.getDepth()) {
                        return -1;
                    } else if (a.getDepth() == b.getDepth()) {
                        return 0;
                    } else {
                        return 1;
                    }
                } else
                    return 1;
            }
        };

        // Hash tables with States as keys and Nodes as data for
        // checking if a state is in the open or closed set.
        List<Map<Stav, Uzol>> openHash = new ArrayList<Map<Stav, Uzol>>(2);
        List<Map<Stav, Uzol>> closedHash = new ArrayList<Map<Stav, Uzol>>(2);

        // Initial and cielovy states
        Stav[] zaciatocny = new Stav[] {zaciatok, ciel};
        Stav[] cielovy = new Stav[] {ciel, zaciatok};

        // For both forward and backward smery
        for (int i : smery) {
            // Create empty heap and hash maps
            fOpenHeap.add(new PriorityQueue<Uzol>());
            gOpenHeap.add(new PriorityQueue<Uzol>(PrioritaNaZakladeHlbky));
            prOpenHeap.add(new PriorityQueue<Uzol>(CelkovaPriorita));
            openHash.add(new HashMap<Stav, Uzol>());
            closedHash.add(new HashMap<Stav, Uzol>());

            // Add zaciatocny node to the open set
            Uzol n = new Uzol(zaciatocny[i], null, null, (short)(zaciatocny[i].linearConflict(cielovy[i])));
            openHash.get(i).put(zaciatocny[i], n);
            fOpenHeap.get(i).add(n);
            gOpenHeap.get(i).add(n);
            prOpenHeap.get(i).add(n);
        }


        // While there are still elements in the open set
        while(!fOpenHeap.get(dopredu).isEmpty() && !fOpenHeap.get(dozadu).isEmpty()) {
            // get minimum priority
            int fwdPriority = prOpenHeap.get(dopredu).peek().getPriority();
            int C = Math.min(fwdPriority, prOpenHeap.get(dozadu).peek().getPriority());

            // stop condition: test U
            if (U<=Math.max(Math.max(
                    C,
                    fOpenHeap.get(dopredu).peek().getFScore()
            ),Math.max(
                    fOpenHeap.get(dozadu).peek().getFScore(),
                    gOpenHeap.get(dopredu).peek().getDepth()+
                            gOpenHeap.get(dozadu).peek().getDepth()+1
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
                        if (newUzol.getDepth() <= n.getDepth() + 1) {
                            continue;
                        }
                        openHash.get(dir).remove(newStav);
                        fOpenHeap.get(dir).remove(newUzol);
                        gOpenHeap.get(dir).remove(newUzol);
                        prOpenHeap.get(dir).remove(newUzol);
                        closedHash.get(dir).remove(newStav);
                        newUzol.setDepth((short) (n.getDepth()+1));
                        newUzol.setPredchadzajuci(n);
                        newUzol.setOp(op);
                    }
                }

                // create new node for this state, if not already found in open/closed lists
                if (newUzol == null) {
                    newUzol = new Uzol(newStav, n, op, (short)(newStav.linearConflict(cielovy[dir]) ));
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
                            matchedUzol.getDepth() +
                                    newUzol.getDepth()
                    );
                    if (dir==dopredu) {
                        System.out.println("Found path: Forward depth:" + newUzol.getDepth() + " backward depth: " + matchedUzol.getDepth());


                    } else {
                        System.out.println("Found path: Forward depth:" + matchedUzol.getDepth() + " backward depth: " + newUzol.getDepth());
                    }
                    System.out.println(newUzol.pathToString());
                    System.out.println(matchedUzol.revPathToStringSkipFirst());

                }
            }


        }
        return null;    // No solution found
    }

}
