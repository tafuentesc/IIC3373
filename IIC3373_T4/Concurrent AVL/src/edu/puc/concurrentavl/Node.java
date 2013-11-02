package edu.puc.concurrentavl;

import java.util.concurrent.Semaphore;

public class Node {
	public int value;
	public boolean isRoutingNode;
	public final Semaphore nodeSem = new Semaphore(1, true); 
	
	public Node left;
	public Node right;
	public Node parent;
	
	public int rightHeight;		// Altura aparente derecha
	public int leftHeight;		// Altura aparente izquierda

	public Node(int value, Node parent)
	{
		this.value = value;
		this.isRoutingNode = false;
		
		this.parent = parent;
		this.left = null;
		this.right = null;
		
		// Seteamos las alturas aparentes en 0
		rightHeight = 0;
		leftHeight = 0;
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