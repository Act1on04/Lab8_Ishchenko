package ua.opnu;

import com.aparapi.Kernel;
import com.aparapi.Range;

import java.util.Random;

public class Lab8_2 {
    public static void main(String[] _args) {

        final int size = 1024 * 5; // размер массива А
        final int[] A = createRandomArray(size); // Заполните массив случайными значениями. Способ заполнения массива не имеет значения
        final int[] indexArrayForGpuComp = new int[1]; // массив для индекса второго четного элемента на GPU
        final int[] indexArrayForCpuComp = new int[1]; // массив для индекса второго четного элемента на CPU

        // GPU
        Kernel kernel = new Kernel() {
            @Override
            public void run() {
                int gid = getGlobalId();
                // Проверка на четность
                if ((int) A[gid] % 2 == 0) {
                    synchronized (indexArrayForGpuComp) {
                        if (indexArrayForGpuComp[0] == 0) {
                            indexArrayForGpuComp[0] = gid + 1; // сохраняем индекс первого четного элемента (добавляем 1, чтобы избежать нуля)
                        } else if (indexArrayForGpuComp[0] > 0 && indexArrayForGpuComp[0] < gid + 1) {
                            indexArrayForGpuComp[0] = -(gid + 1); // сохраняем индекс второго четного элемента в отрицательном виде
                        }
                    }
                }
            }
        };

        // Выполнение на GPU
        kernel.execute(Range.create(size));
        // Получение индекса второго четного элемента
        int secondEvenIndexGpu = -indexArrayForGpuComp[0] - 1;
        // Вывод результатов на GPU
        System.out.println("Device: " + kernel.getTargetDevice().getShortDescription());
        System.out.println("Gpu execution time: " + kernel.getExecutionTime() + " ms");
        System.out.println("Второй четный элемент на GPU находится на индексе: " + secondEvenIndexGpu + " и имеет значение: " + A[secondEvenIndexGpu]);
        // Освобождение ресурсов GPU
        kernel.dispose();

        // CPU
        long cpuStartTime = System.nanoTime();
        int secondEvenIndexCpu = findSecondEvenElement(A);
        long cpuEndTime = System.nanoTime();

        // Вывод результатов на CPU
        System.out.println("Java Vendor: " + System.getProperty("java.vendor"));
        System.out.println("Cpu execution time: " + (cpuEndTime - cpuStartTime) / 1000000 + " ms");
        System.out.println("Второй четный элемент на CPU находится на индексе: " + secondEvenIndexCpu + " и имеет значение: " + A[secondEvenIndexCpu]);
    }

    // Функция для создания и заполнения массива случайными значениями
    public static int[] createRandomArray(int size) {
        int[] array = new int[size];
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt() * 100; // Генерация случайного значения от 0.0 до 100.0
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
}
