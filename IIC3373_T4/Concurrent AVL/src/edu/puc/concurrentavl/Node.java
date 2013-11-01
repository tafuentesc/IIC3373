package edu.puc.concurrentavl;

import java.awt.Color;
import java.util.concurrent.Semaphore;

public class Node {
	public int value;
	public boolean isRoutingNode;
	public final Semaphore nodeSem = new Semaphore(1, false); 
	
	public Node left;
	public Node right;
	public Node parent;
//	
//	public void acquireNode(){
//		try{
//			nodeSem.acquire();
//		}
//		catch(InterruptedException e){
//			e.printStackTrace();
//		}
//	}
//
//	public void releaseNode(){
//		nodeSem.release();
//	}

	public Node(int value, Node parent)
	{
		this.value = value;
		this.isRoutingNode = false;
		
		this.parent = parent;
		this.left = null;
		this.right = null;
	}
	
	public void setLeft(Node newLeft){
		
		if(left != null && left.parent == this)
			left.parent = null;
		
		this.left = newLeft;
		if(newLeft != null)
			left.parent = this;
	}

	public void setRight(Node newRight){
		
		if(right != null && right.parent == this)
			right.parent = null;
		
		this.right = newRight;
		if(newRight != null)
			right.parent = this;
	}
	
	public void changeSon(Node formerSon, Node newSon)
	{
		if(formerSon != null && formerSon.parent == this)
			formerSon.parent = null;
		
		if(this.left == formerSon)
			this.left = newSon;
		else
			this.right = newSon;
		
		// Actualizamos referencia del hijo al padre
		if(newSon != null)
			newSon.parent = this;
	}
	
	public boolean isLeftSon(){
		return (parent!=null && parent.left == this);
	}

	public boolean isRightSon(){
		return (parent!=null && parent.right == this);
	}

	// Método que retorna el hermano de un nodo. Notar que si el nodo no tiene padre
	// este método arroja error => es un comportamiento esperado (por ahora)
	public Node sibling()
	{
		if(this.parent.left == this)
			return parent.right;
		else
			return parent.left;
	}
}