import mpi.MPI;

public class Main {

    public static void main(String[] args) {

        new MPIBlockingMultiplyCalculator(args, 4).multiply();

    }
}