import mpi.MPI;

public class Main {

    public static void main(String[] args) {

        new MPIBlockingMultiplyCalculator(args, 1000).multiply();

    }
}