package edu.puc.concurrentavl;

import java.util.concurrent.Semaphore;

public class BinaryTree implements ISearchTree {
	private Node root;
	private treegraph W;
	private final Semaphore rw = new Semaphore(1, true);
	
	public BinaryTree(){
		root = null;
	}
	
	@Override
	public boolean find(int value) {
		Node node = root;
		
		while(node != null)
		{
			if(value < node.value || (value == node.value && node.isRoutingNode))
				node = node.left;
			else if(value > node.value)
				node = node.right;
			else
				return true;
		}
		return false;
	}

	@Override
	public void insert(int value) {
		
		if(root == null){
			root = new Node(value, null);
			return;
		}
		
		Node node = root;		
		boolean inserted = false;
		
		// nodo a insertar. Declaramos el padre como null (lo actualizaremos al insertarlo)
		Node newNode = new Node(value, null);

		while(!inserted)
		{
			if(value < node.value || (value == node.value && node.isRoutingNode))
			{
				if(node.left != null)
					node = node.left;
				else
				{
					newNode.parent = node; // = new RBNode(value, color.RED, node);
					node.left = newNode;
					inserted = true;
				}
			}
			else if(value > node.value)
			{
				if(node.right != null)
					node = node.right;
				else
				{
					newNode.parent = node; // = new RBNode(value, color.RED, node);
					node.right = newNode;
					inserted = true;
				}
			}
			else
				return;
		}
	}

	@Override
	public void delete(int value) {

		Node node = root;		

		while(node != null)
		{
			if(value < node.value)
				node = node.left;
			
			else if(value > node.value)
				node = node.right;
			else{
				// Si el valor es el mismo, significa que lo encontramos.
				// Luego, lo marcamos como nodo de ruteo y retornamos:
				node.isRoutingNode = true;
				return;
			}
		}
	}

	@Override
	public void printTree() {
		if(W == null)
			W = new treegraph(800,600);
	    W.drawtree(root);

	    //W.display.drawString("Do you like my tree?",20,W.YDIM-50);
	    try{Thread.sleep(3000);} catch(Exception e) {} // 3 sec delay
	}

}
