package concurrency;

import java.lang.Thread.State;

public class Demo1 {
    

    public static void main(String[] args) {
        Counter counter = new Counter();

        counter.start();
        try {
            counter.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        while (counter.getState().equals(State.TERMINATED)) {
            if (counter.getState().equals(State.TERMINATED)) {
                System.out.println(Thread.currentThread().getName());
                break;
            }
            // counter.getState();
        }

        System.out.println(counter.getState());
    }
}

// class Counter extends Thread{
//     private int count = 0;
//     @Override
//     public void run() {
//         for(int i=0; i<10; i++) {
//             try{
//                 System.out.println(Thread.currentThread().getName());
//                 Thread.sleep(500);
//             } catch (InterruptedException e) {
//                 System.out.println(e.getMessage());
//             }
//         }
//     }
    
// }
