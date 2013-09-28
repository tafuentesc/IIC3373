package edu.puc.concurrentavl;

public interface ISearchTree {
	public boolean find(int value);
	public void insert(int value);
	public void delete(int value);

}
