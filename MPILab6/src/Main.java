import mpi.MPI;

public class Main {

    public static void main(String[] args) {

        calculator(true, args, 5000).multiply();
    }

    private static MPIMultiplyCalculator calculator(boolean isBlocking, String[] args, int matrixSize) {

        if (isBlocking)
            return new MPIBlockingMultiplyCalculator(args, matrixSize);

        return new MPINonBlockingMultiplyCalculator(args, matrixSize);
    }
}