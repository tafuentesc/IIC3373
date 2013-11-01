package edu.puc.concurrentavl;

public class Command {

    private int mThread;
    private int mValue;
    private Operation mOperation;
    private boolean mExpected;

    public Command (int thread, int value, Operation operation) {
        this(thread, value, operation, false);
    }

    public Command (int thread, int value, Operation operation, boolean expected) {
        mThread = thread;
        mValue = value;
        mOperation = operation;
        mExpected = expected;
    }

    public Command(String line) {
        String[] parts = line.split(" ");
        mThread = Integer.parseInt(parts[0]);

        if (parts[1].equals("FIND")) {
            mOperation = Operation.Find;
        } else if (parts[1].equals("INSERT")) {
            mOperation = Operation.Insert;
        } else if (parts[1].equals("DELETE")) {
            mOperation = Operation.Delete;
        } else if (parts[1].equals("SYNCHROINSERT")) {
            mOperation = Operation.SynchroInsert;
        } else if (parts[1].equals("SYNCHROFIND")) {
            mOperation = Operation.SynchroFind;
        } else if (parts[1].equals("SYNCHRODELETE")) {
            mOperation = Operation.SynchroDelete;
        }

        mValue = Integer.parseInt(parts[2]);

        if (parts.length > 3) {
            if (parts[3].equals("true")) {
                mExpected = true;
            } else {
                mExpected = false;
            }
        }

    }

    public int getThread() {
        return mThread;
    }

    public int getValue() {
        return mValue;
    }

    public Operation getOperation() {
        return mOperation;
    }

    public boolean isExpected() {
        return mExpected;
    }

}
