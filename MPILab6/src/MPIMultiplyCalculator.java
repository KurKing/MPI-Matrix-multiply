import mpi.MPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public abstract class MPIMultiplyCalculator {

    protected final int mpiSize;
    protected final int chunkSize;
    protected final int chunkSizeLeft;

    protected final int matrixSize;

    protected MPIMultiplyCalculator(String[] args, int matrixSize) {

        this.matrixSize = matrixSize;

        MPI.Init(args);

        mpiSize = MPI.COMM_WORLD.Size();
        chunkSizeLeft = matrixSize % (mpiSize - 1);
        chunkSize = Math.floorDiv(matrixSize, mpiSize - 1);
    }

    abstract void multiply();

    protected void print(int[][] matrix) {

        System.out.println("Print matrix with size: "+matrix.length+"x"+matrix[0].length+"; "+matrix);

        for (int[] row: matrix) {

            for (int item: row) {

                System.out.print(item + " ");
            }

            System.out.println("");
        }
    }

    protected void printArray(int[] array) {

        System.out.println("Print array with size: "+array.length+"; "+array);

        for (int item: array) {

            System.out.print(item + " ");
        }

        System.out.println("");
    }

    protected int[][] generateMatrix(int size, boolean isOneMatrix) {

        int[][] matrix = new int[size][size];
        Random random = ThreadLocalRandom.current();

        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {

                if (isOneMatrix) {

                    matrix[row][column] = 1;
                } else {

                    matrix[row][column] = random.nextInt(3)+1;
                }
            }
        }

        return matrix;
    }

    protected int[] arrayFromMatrix(int[][] matrix) {

        int resultSize = matrix.length * matrix[0].length;
        List<Integer> result = new ArrayList<>(resultSize);

        for (int[] row: matrix) {

            for (int item: row) {

                result.add(item);
            }
        }

        int[] intArray = new int[resultSize];
        for (int i = 0; i < resultSize; i++) {
            intArray[i] = result.get(i);
        }

        return intArray;
    }

    protected int[][] matrixFromArray(int[] array, int size) {

        int[][] matrix = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = array[i * size + j];
            }
        }

        return matrix;
    }
}
