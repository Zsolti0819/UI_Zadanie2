public class Uzol implements Comparable<Uzol> {

    private Stav stav;        // The stav for this node
    private Uzol predchadzajuci;       // Back pointer
    private Stav.Operator op;  // Operator used to get to this node
    private int depth;        // Depth of node in search tree
    private int heuristic;    // Computed heuristic for node stav
    private int priority;

    /**
     * @param stav    Main stav associated with this node
     * @param predchadzajuci  Back pointer
     * @param op       Operation that led to this node
     * @param h        Heuristic for node stav
     */
    public Uzol(Stav stav, Uzol predchadzajuci, Stav.Operator op, short h) {
        this.stav = stav;
        this.predchadzajuci = predchadzajuci;
        this.op = op;
        this.heuristic = h;

        // Set root node depth to 0, and child node depth to
        // parent depth + 1
        if (predchadzajuci == null) {
            this.depth = 0;
        } else {
            this.depth = (short)(predchadzajuci.depth + 1);
        }
        // MM / MMε
        priority = Math.max(2*depth, depth+heuristic);
    }

    public void setPredchadzajuci(Uzol predchadzajuci) {
        this.predchadzajuci = predchadzajuci;
    }

    /**
     * @param op  Operator that led to this stav
     */
    public void setOp(Stav.Operator op) {
        this.op = op;
    }

    /**
     * @return  Tree depth plus heuristic
     */
    public int getFScore() {
        return depth+heuristic;
    }

    public void setDepth(short depth) {
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
    }

    public int getPriority() {
        return priority;
    }

    public Stav getStav() {
        return stav;
    }

    // zo zaciatocneho uzla k aktualnej
    public String pathToString() {
        if (predchadzajuci != null)
            return predchadzajuci.pathToString() + "\n" + op + "\n" + stav;
        else
            return "\nInitial Stav:\n" + stav;
    }

    // z aktualnej uzli do cielovej, vynechame prvy
    public String revPathToStringSkipFirst() {
        if (predchadzajuci != null)
            return "\n" + op.reverse() + "\n" + predchadzajuci.revPathToString();
        else
            return "\n" + op.reverse() + "\n";
    }

    // z aktualnej uzli do cielovej
    public String revPathToString() {
        if (predchadzajuci != null)
            return stav + "\n" + op.reverse() + "\n" + predchadzajuci.revPathToString();
        else
            return stav.toString();
    }

    @Override
    public int compareTo(Uzol otherUzol) {
        if (this.getFScore() < otherUzol.getFScore())
            return -1;
        else if (this.getFScore() == otherUzol.getFScore())
            return 0;
        else
            return 1;
    }
}
