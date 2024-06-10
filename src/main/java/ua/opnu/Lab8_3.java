package ua.opnu;

import com.aparapi.Kernel;
import com.aparapi.Range;

import java.util.Arrays;
import java.util.Random;

public class Lab8_3 {
    public static void main(String[] _args) {

        final int size = 512; // размер массива А
        // Заполните массив случайными значениями. Способ заполнения массива не имеет значения
        final float[] A = createRandomArray(size);
        // в эти массивы будем помещать индексы элементов, которые удовлетворяют условию.
        final float[] xi = new float[1];
        xi[0] = 0f;
        final float[] sums = new float[size];
        // GPU
        Kernel kernel = new Kernel() {
            @Override
            public void run() {
                int gid = getGlobalId();
//                xi[0] = xi[0] + A[gid];
                sums[gid] = A[gid];
            }
        };

        // Execute Kernel.
        kernel.execute(Range.create(size));
        for (int i = 0; i < size; i++) {
            xi[0] += sums[i];
        }
        float mean = xi[0] / size;
        System.out.println("\n Масив А: ");
        System.out.println(Arrays.toString(A));
        System.out.println("Середне значення за допомогою GPU: " + mean);
        // Dispose Kernel resources.
        kernel.dispose();

        // CPU
        xi[0] = 0f;
        for (int i = 0; i < size; i++) {
            xi[0] = xi[0] + A[i];
        }
        mean = xi[0] / size;
        System.out.println("Середне значення за допомогою СPU: " + mean);

    }

    public static float[] createRandomArray(int size) {
        float[] array = new float[size];
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            array[i] = random.nextFloat(10000)+1;
        }

        return array;
    }

}
