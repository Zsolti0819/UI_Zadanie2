public class Uzol implements Comparable<Uzol> {

    private final Stav stav;
    private Uzol predchadzajuci;
    private Stav.Operator operator;
    private int hlbka;
    private final int heuristika;
    private final int priorita;

    public Uzol(Stav stav, Uzol predchadzajuci, Stav.Operator operator, int heuristika) {
        this.stav = stav;
        this.predchadzajuci = predchadzajuci;
        this.operator = operator;
        this.heuristika = heuristika;

        // nastavime hlbku prve uzla (koren) na 0
        if (predchadzajuci == null) {
            this.hlbka = 0;
        } else { // ostatne uzli maju hlbku hlbka predka + 1
            this.hlbka = (short)(predchadzajuci.hlbka + 1);
        }
        priorita = Math.max(2* hlbka, hlbka + this.heuristika);
    }

    public void setPredchadzajuci(Uzol predchadzajuci) {
        this.predchadzajuci = predchadzajuci;
    }

    public void setOperator(Stav.Operator operator) {
        this.operator = operator;
    }

    public int getFScore() {
        return hlbka + heuristika;
    }

    public void setHlbka(short depth) {
        this.hlbka = depth;
    }

    public int getHlbka() {
        return hlbka;
    }

    public int getPriorita() {
        return priorita;
    }

    public Stav getStav() {
        return stav;
    }

    // zo zaciatocneho uzla k aktualnej
    public String pathToString() {
        if (predchadzajuci != null)
            return predchadzajuci.pathToString() + "\n" + operator + "\n" + stav;
        else
            return "\nInitial Stav:\n" + stav;
    }

    // z aktualnej uzli do cielovej, vynechame prvy
    public String revPathToStringSkipFirst() {
        if (predchadzajuci != null)
            return "\n" + operator.reverse() + "\n" + predchadzajuci.revPathToString();
        else
            return "\n" + operator.reverse() + "\n";
    }

    // z aktualnej uzli do cielovej
    public String revPathToString() {
        if (predchadzajuci != null)
            return stav + "\n" + operator.reverse() + "\n" + predchadzajuci.revPathToString();
        else
            return stav.toString();
    }

    @Override
    public int compareTo(Uzol otherUzol) {
        return Integer.compare(this.getFScore(), otherUzol.getFScore());
    }
}
