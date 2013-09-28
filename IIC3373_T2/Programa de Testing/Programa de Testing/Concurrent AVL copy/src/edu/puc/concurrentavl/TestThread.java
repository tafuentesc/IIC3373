package edu.puc.concurrentavl;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class TestThread extends Thread {
    public enum TesterOp { Finished, Aborted }

    public static Object[] mLocks;
    private static ConcurrentHashMap<Integer, Boolean> mFlags;

    private Queue<Command> mCommands;
    private ISearchTree mTree;

    static {
        mLocks = new Object[1000];
        for (int i = 0; i < mLocks.length; i++) {
            mLocks[i] = new Object();
        }

        mFlags = new ConcurrentHashMap<Integer, Boolean>();
    }

    public TestThread(ISearchTree tree, Queue<Command> commands) {
        mCommands = commands;
        mTree = tree;
    }

    @Override
    public void run() {
        while (!mCommands.isEmpty()) {

        	//mTree.printTree();
        	
            Command c = mCommands.poll();
            if (c.getOperation() == Operation.Insert) {
                mTree.insert(c.getValue());
            } else if (c.getOperation() == Operation.Delete) {
                mTree.delete(c.getValue());
            } else if (c.getOperation() == Operation.Find) {
                boolean result = mTree.find(c.getValue());
                if (result != c.isExpected()) {
                    System.out.println("Assert failed for value " + c.getValue() + ". Expected " + c.isExpected() + ", obtained " + result + ".");
                    System.exit(0);
                }
            } else if (c.getOperation() == Operation.SynchroInsert) {
                synchronized (mLocks[c.getValue() / 1000]) {
                    mTree.insert(c.getValue());
                    mFlags.put(c.getValue(), true);
                }
            } else if (c.getOperation() == Operation.SynchroFind) {
                synchronized (mLocks[c.getValue() / 1000]) {
                    boolean result = mTree.find(c.getValue());
                    Boolean value = mFlags.get(c.getValue());
                    boolean expected = value == null ? false : value;

                    if (result != expected) {
                        System.out.println("Assert failed for value " + c.getValue() + ". Expected " + expected + ", obtained " + result + ".");
                        System.exit(0);
                    }
                }
            } else if (c.getOperation() == Operation.SynchroDelete) {
                synchronized (mLocks[c.getValue() / 1000]) {
                    mTree.delete(c.getValue());
                    mFlags.put(c.getValue(), false);
                }
            }

        }
    }

    class TesterCommand {
        private TesterOp mOperation;

        public TesterCommand(TesterOp operation) {
            mOperation = operation;
        }

        public TesterOp getOperation() {
            return mOperation;
        }
    }

}
