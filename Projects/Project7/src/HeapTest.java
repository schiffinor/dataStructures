import java.util.Arrays;
import java.util.Random;

public class HeapTest {
    public static void test(int n) {
        PriorityQueue<Double> test = new Heap<>();
        double[] control = new double[n];
        Random rand = new Random();

        for (int i = 0; i < n; i++) {
            control[i] = rand.nextDouble();
            test.offer(control[i]);
        }
        Arrays.sort(control);
        for(int i = 0; i < control.length; i++) {
            int size = test.size();
            Double peek = test.peek();
            Double poll = test.poll();
            //System.out.println("Size: "+size);
            //System.out.println("Peek: "+peek);
            //System.out.println("Poll: "+poll);
            //System.out.println("Control: "+control[i]);
            if (size == 0)
                System.out.println("Size ERROR for n == " + n + " after removing " + (i) + " items. Size: "+size+" Control: "+control.length);
            if (!peek.equals(control[i]))
                System.out.println("Peek ERROR for n == " + n + " after removing " + (i) + " items. Peek: "+peek+" Control: "+control[i]);
            if (!poll.equals(control[i]))
                System.out.println("Poll ERROR for n == " + n + " after removing " + (i) + " items. Poll: "+poll+" Control: "+control[i]);
        }
    }

    public static void main(String[] args){
        for(int n : new int[] {3, 5, 20 , 100000}) test(n);
    }
}
