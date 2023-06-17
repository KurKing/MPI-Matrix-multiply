import mpi.MPI;

public class MPIBlockingMultiplyCalculator extends MPIMultiplyCalculator {

    protected MPIBlockingMultiplyCalculator(String[] args, int matrixSize) {
        super(args, matrixSize);
    }

    @Override
    void masterLogic() {

        final int[][] lhs = generateMatrix(matrixSize, true);
        final int[][] rhs = generateMatrix(matrixSize, true);

//        print(lhs);
//        print(rhs);

        final long timerStart = System.currentTimeMillis();
        int[][] result = new int[matrixSize][matrixSize];

        for (int i = 1; i < mpiSize; i++) {

            int endIndex = i * chunkSize;
            if (i == mpiSize-1)
                endIndex += chunkSizeLeft;

            final int[] lhsArray = arrayFromMatrix(partOfMatrix(lhs, (i-1)*chunkSize, endIndex));
            final int[] rhsArray = arrayFromMatrix(rhs);

            MPI.COMM_WORLD.Send(lhsArray, 0, lhsArray.length, MPI.INT, i, 403);
            MPI.COMM_WORLD.Send(rhsArray, 0, rhsArray.length, MPI.INT, i, 404);
        }

        for (int i = 1; i < mpiSize; i++) {

            int rows = chunkSize;
            if (i == mpiSize-1)
                rows += chunkSizeLeft;

            int[] resultChunk = new int[rows * matrixSize];
            MPI.COMM_WORLD.Recv(resultChunk, 0, resultChunk.length, MPI.INT, i, 200);

            final int[][] resultChunkMatrix = matrixFromArray(resultChunk, matrixSize);

            for (int row = 0; row < resultChunkMatrix.length; row++) {
                for (int j = 0; j < resultChunkMatrix[0].length; j++) {

                    result[(i-1)*chunkSize+row][j] = resultChunkMatrix[row][j];
                }
            }
        }

        final long time = System.currentTimeMillis() - timerStart;

        print(result);

        System.out.println("Execution time: " + time + "ms;");
    }

    @Override
    void workerLogic() {

        int rowNumber = chunkSize;
        if (id == mpiSize - 1)
            rowNumber += chunkSizeLeft;

        int[] lhsArray = new int[rowNumber * matrixSize];
        MPI.COMM_WORLD.Recv(lhsArray, 0, lhsArray.length, MPI.INT, 0, 403);

        int[] rhsArray = new int[matrixSize * matrixSize];
        MPI.COMM_WORLD.Recv(rhsArray, 0, rhsArray.length, MPI.INT, 0, 404);

        final int[] arrayResult = arrayFromMatrix(
                multiplyChunks(matrixFromArray(lhsArray, matrixSize),
                        matrixFromArray(rhsArray, matrixSize))
        );

        MPI.COMM_WORLD.Send(arrayResult, 0, arrayResult.length, MPI.INT, 0, 200);
    }
}
