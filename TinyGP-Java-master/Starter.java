public class Starter {
    public static void main(String[] args) {
        String fname = "problem.dat";
        long s = -1;

        TinyGP gp = new TinyGP(fname, s);
        gp.evolve();
    }
}