package edu.puc.concurrentavl;

public enum Operation {
    Insert("INSERT"), Find("FIND"), Delete("DELETE"), SynchroInsert(
            "SYNCHROINSERT"), SynchroFind("SYNCHROFIND"), SynchroDelete(
            "SYNCHRODELETE");

    private String mData;

    private Operation(String data) {
        mData = data;
    }

    @Override
    public String toString() {
        return mData;
    };
}
