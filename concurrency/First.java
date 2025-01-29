
 import java.io.*;
 import java.lang.management.LockInfo;
 import java.util.*;
 import java.util.concurrent.CountDownLatch;
 import java.util.concurrent.locks.Condition;
 import java.util.concurrent.locks.Lock;
 import java.util.concurrent.locks.ReentrantLock;
 
 /*
  * To execute Java, please define "static void main" on a class
  * named Solution.
  *
  * If you need more classes, simply define them inline.
  */
 
 class First {
   public static void main(String[] args) {
     // ArrayList<String> strings = new ArrayList<String>();
     // strings.add("Hello, World!");
     // strings.add("Welcome to CoderPad.");
     // strings.add("This pad is running Java " + Runtime.version().feature());
 
     // for (String string : strings) {
     //   System.out.println(string);
     // }
 
     Field field = new Field(10);
 
     field.scan("0-0", 0);
     field.scan("0-1", 0);
     field.scan("0-2", 0);
 
     field.scan("1-0", 1);
     field.scan("0-3", 0);
 
     field.exit("0-0", 0);
     field.exit("0-1", 0);
     field.exit("0-2", 0);
     field.exit("0-3", 0);
     field.exit("1-0", 1);
 
 
 
 
     // for(int i=0; i<20; i++) {
     //   int playerNo = i;
     //   Thread teamA = new Thread(() -> {
     //     field.scan("Player" + playerNo, 0);
     //   });
 
     //   teamA.start();
     // }
 
     // for(int i=0; i<10; i++) {
     //   int playerNo = i;
     //   Thread teamB = new Thread(() -> {
     //     field.scan("Player" + playerNo, 1);
     //   });
 
     //   teamB.start();
     // }
     // try {
     //   Thread.sleep(1000);
     // } catch (Exception e) {
     //   e.printStackTrace();
     // }
     
 
     // for(int i=0; i<10; i++) {
     //   int playerNo = i;
     //   Thread teamA = new Thread(() -> {
     //     field.exit("Player" + playerNo, 0);
     //   });
 
     //   teamA.start();
     // }
 
     // for(int i=0; i<10; i++) {
     //   int playerNo = i;
     //   Thread teamB = new Thread(() -> {
     //     field.exit("Player" + playerNo, 1);
     //   });
 
     //   teamB.start();
     // }
 
 
   }
 }
 
 class Field {
   int capacity, currPlayers, currTeam;
   Lock lock = new ReentrantLock();
   Condition practice = lock.newCondition();
   // CountDownLatch latch = new CountDownLatch(1);
 
   Field(int capacity) {
     this.capacity = capacity;
     this.currPlayers = 0;
     this.currTeam = -1;
   }
 
   public void scan(String player, int team) {
     lock.lock();
     System.out.println("Player " + player + " has trying.. from team : " + team);
     try {
       while(!((currTeam == team || currTeam == -1) && currPlayers < capacity)) {
         // System.out.println("Player " + player + " has waiting from team : " + team);
         practice.await();
       }
       currTeam = team;
       currPlayers++;
     } catch (InterruptedException e) {
       Thread.currentThread().interrupt();
     } finally {
       lock.unlock();
     }
 
     System.out.println("Player " + player + " has entered from team : " + team);
 
   }
 
   public void exit(String player, int team) {
     lock.lock();
     try {
       currPlayers--;
       if(currPlayers == 0) {
         currTeam = -1;
       }
     } finally {
       practice.signalAll();
       // latch.countDown();
       lock.unlock();
     }
     System.out.println("Player " + player + " has exited from team : " + team);
   } 
 }
 
 
 // Your previous Plain Text content is preserved below:
 
 // Pad for Tanmay Chandekar - Software Engineer - Atlas