package edu.puc.concurrentavl;

public class RBNode {
	public int value;
	public color color;
	
	public RBNode left;
	public RBNode right;
	public RBNode parent;
	
	public void setLeft(RBNode newLeft){
		
		if(left.parent == this)
			left.parent = null;
		
		this.left = newLeft;
		if(newLeft != null)
			left.parent = this;
	}

	public void setRight(RBNode newRight){
		
		if(right.parent == this)
			right.parent = null;
		
		this.right = newRight;
		if(newRight != null)
			right.parent = this;
	}
	
	public void changeSon(RBNode formerSon, RBNode newSon)
	{
		if(formerSon.parent == this)
			formerSon.parent = null;
		
		if(this.left == formerSon)
			this.left = newSon;
		else
			this.right = newSon;
		
		// Actualizamos referencia del hijo al padre
		newSon.parent = this;
	}

	public boolean isLeftBlack(){
		return left == null || (left!=null && left.color == color.BLACK);
	}

	public boolean isRightBlack(){
		return right == null || (right!=null && right.color == color.BLACK);
	}
	
	public boolean isLeftSon(){
		return (parent!=null && parent.left == this);
	}

	public boolean isRightSon(){
		return (parent!=null && parent.right == this);
	}

	public RBNode(int value, color color, RBNode parent)
	{
		this.value = value;
		this.color = color;
		this.parent = parent;
		this.left = null;
		this.right = null;
	}

	// Método que retorna el hermano de un nodo. Notar que si el nodo no tiene padre
	// este método arroja error => es un comportamiento esperado (por ahora)
	public RBNode sibling()
	{
		if(this.parent.left == this)
			return parent.right;
		else
			return parent.left;
	}
}
