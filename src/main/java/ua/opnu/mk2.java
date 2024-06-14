package ua.opnu;

import com.aparapi.Kernel;
import com.aparapi.Range;

import java.util.Arrays;
import java.util.Random;

public class mk2 {
    public static void main(String[] _args) {

        final int size = 81; // розмір матриці
        final float[][] A1 = createRandomMatrix(size);
        final float[][] A2 = createRandomMatrix(size);
        final float[][] resultMatrix = new float[size][size];

        // GPU
        Kernel kernel = new Kernel() {
            @Override
            public void run() {
                int gid = getGlobalId();
                int row = gid / size;
                int col = gid % size;
                resultMatrix[row][col] = A1[row][col] + A2[row][col];
            }
        };

        // Виконання Kernel
        kernel.execute(Range.create(size * size));

        // Вивід результату
        System.out.println("\nМатриця A1:");
        printMatrix(A1);
        System.out.println("\nМатриця A2:");
        printMatrix(A2);
        System.out.println("\nРезультуюча матриця:");
        printMatrix(resultMatrix);

        // Звільнення ресурсів Kernel
        kernel.dispose();
    }

    public static float[][] createRandomMatrix(int size) {
        float[][] matrix = new float[size][size];
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = random.nextFloat() * 10000 + 1;
            }
        }
        return matrix;
    }

    public static void printMatrix(float[][] matrix) {
        int limit = 5; // Ліміт на кількість виведених рядків та стовпців

        for (int i = 0; i < Math.min(matrix.length, limit); i++) {
            for (int j = 0; j < Math.min(matrix[i].length, limit); j++) {
                System.out.printf("%.2f ", matrix[i][j]);
            }
            if (matrix[i].length > limit) {
                System.out.print("..."); // Додаємо "..." після останнього стовпця
            }
            System.out.println();
        }
        if (matrix.length > limit) {
            System.out.println("..."); // Додаємо "..." після останнього рядка
        }
    }

}
