package ua.opnu;

import com.aparapi.Kernel;
import com.aparapi.Range;

public class Lab8_1 {

    public static void main(String[] _args) {

        final int size = 512;

        /* Input float array for which square values need to be computed. */
        final float[] values = new float[size];

        /* Initialize input array. */
        for (int i = 0; i < size; i++) {
            values[i] = i;
        }

        /* Output array which will be populated with square values of corresponding input array elements. */
        final float[] squares = new float[size];

        /* Aparapi Kernel which computes squares of input array elements and populates them in corresponding elements of
         * output array.
         **/
        Kernel kernel = new Kernel(){
            @Override public void run() {
                int gid = getGlobalId();
                squares[gid] = values[gid] * values[gid];
            }
        };

        // Execute Kernel.
        kernel.execute(Range.create(512));
        // Dispose Kernel resources.
        kernel.dispose();

        System.out.println("Time = " + kernel.getExecutionTime());

        // Report target execution mode: GPU or JTP (Java Thread Pool).
        System.out.println("Device = " + kernel.getTargetDevice().getShortDescription());

        // Display computed square values.
        for (int i = 0; i < size; i++) {
            System.out.printf("%6.0f %8.0f\n", values[i], squares[i]);
        }

    }

}
