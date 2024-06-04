package ua.opnu;

import com.aparapi.Kernel;
import com.aparapi.Range;

import java.util.Arrays;
import java.util.Random;


public class Lab8_2 {
    public static void main(String[] _args) {

        final int size = 1024 * 5; // размер массива А
//        final int size = 10; // размер массива А
        final int[] A = createRandomArray(size); // Заполните массив случайными значениями. Способ заполнения массива не имеет значения
        System.out.println(Arrays.toString(A));
        final int[] indexArrayForGpuComp = new int[size]; // в эти массивы будем помещать индексы элементов, которые удовлетворяют условию.
        final int[] indexArrayForCpuComp = new int[size];
//        final int[] countSize = {0}; // подсчет элементов, удовлетворяющие условию.
//        final int[] secondEvenElement = {0}; // подсчет элементов, удовлетворяющие условию.
        final int secondEvenElement; // подсчет элементов, удовлетворяющие условию.

        final float[] sum = new float[size];
        // GPU
        Kernel kernel = new Kernel() {
            @Override
            public void run() {
                int gid = getGlobalId();
//                sum[gid] = (float) (Math.cos(Math.sin(A[gid])) + Math.sin(Math.cos(A[gid])));
                if (A[gid] % 2 == 0)
                    indexArrayForGpuComp[gid] = gid;
            }
        };

        // Execute Kernel.
        kernel.execute(Range.create(size));
        // Report target execution mode: GPU or JTP (Java Thread Pool).
        System.out.println("Device: " + kernel.getTargetDevice().getShortDescription());
        System.out.println("Gpu execution time: " + kernel.getExecutionTime() + " ms");
        System.out.println(Arrays.toString(indexArrayForGpuComp));

        secondEvenElement = findSecondNonZeroElement(indexArrayForGpuComp);
        if (secondEvenElement != -1 ) {
        System.out.println("Второй четный элемент находится на индексе: " + secondEvenElement + " и имеет значение: " + A[secondEvenElement]);
        } else {
            System.out.println("Второй четный элемент не найден.");
        }

        // Dispose Kernel resources.
        kernel.dispose();


        // CPU
        final float[] sum2 = new float[size];
        long startTime = System.currentTimeMillis();
        // Найти второй четный элемент
        int secondEvenIndex = findSecondEvenElement(A);
        long endTime = System.currentTimeMillis() - startTime;
        System.out.println("Java Vendor: " + System.getProperty("java.vendor"));
        System.out.println("Cpu execution time: " + endTime + " ms");
        if (secondEvenIndex != -1) {
            System.out.println("Второй четный элемент находится на индексе: " + secondEvenIndex + " и имеет значение: " + A[secondEvenIndex]);
        } else {
            System.out.println("Второй четный элемент не найден.");
        }

    }

    // Функция для создания и заполнения массива случайными значениями
    public static int[] createRandomArray(int size) {
        int[] array = new int[size];
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(10000)+1; // Генерация случайного значения от 0.0 до 1.0
        }

        return array;
    }

    // Функция для нахождения второго четного элемента
    public static int findSecondEvenElement(int[] array) {
        int count = 0;
        for (int i = 0; i < array.length; i++) {
            if ((int) array[i] % 2 == 0) {
                count++;
                if (count == 2) {
                    return i;
                }
            }
        }
        return -1; // Возвращаем -1, если второй четный элемент не найден
    }

    public static int findSecondNonZeroElement(int[] array) {
        int count = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] != 0) {
                count++;
                if (count == 2) {
                    return i;
                }
            }
        }
        return -1; // Возвращаем -1, если второй ненулевой элемент не найден
    }

}
