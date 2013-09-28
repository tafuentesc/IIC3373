package edu.puc.concurrentavl;

public class RBNode {
	public int value;
	public color color;
	
	public RBNode left;
	public RBNode right;
	public RBNode parent;
	
	public RBNode(int value, color color, RBNode parent)
	{
		this.value = value;
		this.color = color;
		this.parent = parent;
		this.left = null;
		this.right = null;
	}
}
