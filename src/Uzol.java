public class Uzol implements Comparable<Uzol> {

    private final Stav stav;
    private Uzol predchadzajuci;
    private Stav.Operator operator;
    private int hlbka;
    private final int heuristika;
    private final int priorita;

    public Uzol(Stav stav, Uzol predchadzajuci, Stav.Operator operator, int heuristika)
    {
        this.stav = stav;
        this.predchadzajuci = predchadzajuci;
        this.operator = operator;
        this.heuristika = heuristika;

        if (predchadzajuci == null)
            this.hlbka = 0;
        else  // ostatne uzli maju hlbku hlbka predka + 1
            this.hlbka = (short)(predchadzajuci.hlbka + 1);

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

    public String cestaZoStartuKaktualnej()
    {
        if (predchadzajuci != null)
            return predchadzajuci.cestaZoStartuKaktualnej() + "\n" + operator + "\n" + stav;
        else
            return "\nZaciatocny  stav:\n" + stav;
    }

    public String cestaNaspatVynechajPrvu()
    {
        if (predchadzajuci != null)
            return "\n" + operator.reverse() + "\n" + predchadzajuci.cestaNaspat();
        else
            return "\n" + operator.reverse() + "\n";
    }

    public String cestaNaspat()
    {
        if (predchadzajuci != null)
            return stav + "\n" + operator.reverse() + "\n" + predchadzajuci.cestaNaspat();
        else
            return stav.toString();
    }

    @Override
    public int compareTo(Uzol otherUzol) {
        return Integer.compare(this.getFScore(), otherUzol.getFScore());
    }
}
