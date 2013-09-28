package edu.puc.concurrentavl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;


public class Tester {

    public static void main(String[] args) {
        
    	//System.out.println("Enter test file path: ");
        Scanner scanner = new Scanner(System.in);
        String path = null;//scanner.nextLine();
        scanner.close();
        
        path ="Test/test1_STP.txt";
        //path ="Test/test0.txt";

        long time = System.currentTimeMillis();

        File file = new File(path);
        try {
            scanner = new Scanner(file);
            int numberOfThreads = Integer.parseInt(scanner.nextLine());

            List<Queue<Command>> commandQueues = new ArrayList<Queue<Command>>();
            for (int i = 0; i < numberOfThreads; i++) {
                commandQueues.add(new LinkedList<Command>());
            }

            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                Command c = new Command(line);
                commandQueues.get(c.getThread()).add(c);
            }
            scanner.close();

            ISearchTree tree = new RBTree();// null; // Reemplazar con la
            // implementacion del alumno

            TestThread[] threads = new TestThread[numberOfThreads];
            for (int i = 0; i < numberOfThreads; i++) {
                threads[i] = new TestThread(tree, commandQueues.get(i));
                threads[i].start();
            }

            for (int i = 0; i < numberOfThreads; i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Test Passed");
            long delta = System.currentTimeMillis() - time;
            System.out.println("The test took " + delta + " ms.");



        } catch (FileNotFoundException e) {
            System.out
            .println("No file found at specified path, test aborted.");
        }



    }

}
