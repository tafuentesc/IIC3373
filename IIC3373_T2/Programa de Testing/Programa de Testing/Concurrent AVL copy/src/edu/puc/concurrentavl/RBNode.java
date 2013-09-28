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
