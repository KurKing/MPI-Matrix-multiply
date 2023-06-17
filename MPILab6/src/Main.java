import mpi.MPI;

public class Main {
    public static void main(String[] args) {

        MPI.Init(args);

        System.out.println(MPI.COMM_WORLD.Rank());
    }
}