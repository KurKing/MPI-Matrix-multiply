import mpi.MPI;
import mpi.Request;

public class MPINonBlockingMultiplyCalculator extends MPIMultiplyCalculator {

    protected MPINonBlockingMultiplyCalculator(String[] args, int matrixSize) {
        super(args, matrixSize);
    }

    @Override
    void masterLogic() {

        final int[][] lhs = generateMatrix(matrixSize, true);
        final int[][] rhs = generateMatrix(matrixSize, true);

//        print(lhs);
//        print(rhs);

        final long timerStart = System.currentTimeMillis();

        int[] result = new int[matrixSize*matrixSize];
        Request[] results = new Request[mpiSize-1];

        for (int i = 1; i < mpiSize; i++) {

            final int startIndex = (i-1)*chunkSize;
            int endIndex = i * chunkSize;
            if (i == mpiSize-1)
                endIndex += chunkSizeLeft;

            int[] lhsArray = arrayFromMatrix(partOfMatrix(lhs, startIndex, endIndex));
            int[] rhsArray = arrayFromMatrix(rhs);

            MPI.COMM_WORLD.Isend(lhsArray, 0, lhsArray.length, MPI.INT, i, 403);
            MPI.COMM_WORLD.Isend(rhsArray, 0, rhsArray.length, MPI.INT, i, 404);

            results[i-1] = MPI.COMM_WORLD.Irecv(result,
                    startIndex*matrixSize,
                    matrixSize*(endIndex-startIndex),
                    MPI.INT,
                    i,
                    200);
        }

        Request.Waitall(results);

        final int[][] matrix = matrixFromArray(result, matrixSize);
        final long time = System.currentTimeMillis() - timerStart;

        print(matrix);

        System.out.println("Execution time: " + time + "ms;");
    }

    @Override
    void workerLogic() {

        int rowNumber = chunkSize;
        if (id == mpiSize - 1)
            rowNumber += chunkSizeLeft;

        int[] lhsArray = new int[rowNumber * matrixSize];
        int[] rhsArray = new int[matrixSize * matrixSize];

        Request lhsReceive = MPI.COMM_WORLD.Irecv(lhsArray, 0, lhsArray.length, MPI.INT, 0, 403);
        Request rhsReceive = MPI.COMM_WORLD.Irecv(rhsArray, 0, rhsArray.length, MPI.INT, 0, 404);

        Request.Waitall(new Request[]{lhsReceive, rhsReceive});

        int[] arrayResult = arrayFromMatrix(multiplyChunks(
                matrixFromArray(lhsArray, matrixSize),
                matrixFromArray(rhsArray, matrixSize))
        );

        MPI.COMM_WORLD.Send(arrayResult, 0, arrayResult.length, MPI.INT, 0, 200);
    }
}
