package concurrency;
/*
 * Click `Run` to execute the snippet below!
 */

// Consider a mini-football practice field, where players need to scan their card to enter or exit the field. Scanners are installed at various locations across the field and players can use any of them to scan their card. Card contains information of the player and the football club they are part of (say club A and club B).

// You are to develop the card scanner system following certain rules to ensure only permitted players play in the field at a given time.

// Rules:
// Consider all the players scan their card on arrival to request permit to enter the field

// At a given time only 10 players from the same team can enter the football field.

// A player from a specific team can only enter if the field is empty or the existing players in the field belong to the same team.

// Players from other teams must wait until the field is empty before they can enter.

// The order that waiting players are given clearance to enter the field doesn't need to be based on the time they scanned their ID, they can be permitted to enter in any order.

// There is no restriction on the exit of players, the players can exit in any order.
 

 // A B A A' A' 
 // A B A A A A A A A A A' 
 // A A A

 // practiceTeamA()

 // praticeTeamB()


// pratice(int team) {}

// if(team == 1) {

//  }

/*
* 
 * pratice(int team) {}
 */


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
 
 class Solution {

  private static void startScan(String p, int t, Field field) {
    Thread th = new Thread(()->{
      field.scan(p, t);
    });
    th.start();
  }

  private static void startExit(String p, int t, Field field) {
    Thread th = new Thread(()->{
      field.exit(p, t);
    });
    th.start();
  }

  private static void sleep(int t) {
    try {
      Thread.sleep(t);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
   public static void main(String[] args) {
     // ArrayList<String> strings = new ArrayList<String>();
     // strings.add("Hello, World!");
     // strings.add("Welcome to CoderPad.");
     // strings.add("This pad is running Java " + Runtime.version().feature());
 
     // for (String string : strings) {
     //   System.out.println(string);
     // }
 
     Field field = new Field(10);

     startScan("0-0", 0, field);
     startScan("0-1", 0, field);
     startScan("0-2", 0, field);
 
     startScan("1-0", 1, field);
     startScan("0-3", 0, field);

     sleep(1000);
 
     startExit("0-0", 0, field);
     startExit("0-1", 0, field);
     startExit("0-2", 0, field);
     startExit("0-3", 0, field);
     startExit("1-0", 1, field);
 
    //  field.scan("0-0", 0);
    //  field.scan("0-1", 0);
    //  field.scan("0-2", 0);
 
    //  field.scan("1-0", 1);
    //  field.scan("0-3", 0);
 
    //  field.exit("0-0", 0);
    //  field.exit("0-1", 0);
    //  field.exit("0-2", 0);
    //  field.exit("0-3", 0);
    //  field.exit("1-0", 1);
 
 
 
 
    //  for(int i=0; i<20; i++) {
    //    int playerNo = i;
    //    Thread teamA = new Thread(() -> {
    //      field.scan("Player" + playerNo, 0);
    //      try {
    //         Thread.sleep(5000);
    //     } catch (InterruptedException e) {
    //         // TODO Auto-generated catch block
    //         e.printStackTrace();
    //     }
    //      field.exit("Player" + playerNo, 0);
    //    });
 
    //    teamA.start();
    //  }

    //  for(int i=0; i<20; i++) {
    //     int playerNo = i;
    //     Thread teamA = new Thread(() -> {
    //       field.scan("Player" + playerNo, 1);
    //       try {
    //          Thread.sleep(5000);
    //      } catch (InterruptedException e) {
    //          // TODO Auto-generated catch block
    //          e.printStackTrace();
    //      }
    //       field.exit("Player" + playerNo, 1);
    //     });
  
    //     teamA.start();
    //   }
 
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
    //  System.out.println("Player " + player + " has trying.. from team : " + team);
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