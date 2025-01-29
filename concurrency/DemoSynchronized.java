package concurrency;

// main
public class DemoSynchronized{
    
    public static void main(String[] args) {
        Counter counter = new Counter();

        Concu concu1 = new Concu(counter);
        Concu concu2 = new Concu(counter);

        concu1.start();
        concu2.start();

        try {
            concu1.join();
            concu2.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        

        System.out.println(counter.getCount());
    } 
}


// thread class
class Concu extends Thread{
    Counter counter;

    Concu(Counter counter) {
        this.counter = counter;
    }

    @Override
    public void run() {
        for(int i=0; i<1000; i++) {
            this.counter.increment();
        }
    }
    
}

// counter
class Counter extends Thread{
    private int count = 0;
    
    public synchronized void increment() {
        this.count++;
    }

    public int getCount() {
        return this.count;
    }
    
}
