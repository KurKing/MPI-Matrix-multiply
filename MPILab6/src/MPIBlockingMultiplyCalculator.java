import mpi.MPI;

public class MPIBlockingMultiplyCalculator extends MPIMultiplyCalculator {

    private int id;

    protected MPIBlockingMultiplyCalculator(String[] args, int matrixSize) {
        super(args, matrixSize);
    }

    @Override
    void multiply() {

        id = MPI.COMM_WORLD.Rank();

        System.out.println("Node id: "+id);

        if (id != 0) { return; }

        var m = generateMatrix(matrixSize, false);
        print(m);
        var a = arrayFromMatrix(m);
        printArray(a);
        print(matrixFromArray(a, matrixSize));
    }
}
