package edu.puc.concurrentavl;

enum color {RED, BLACK};

public class RBTree implements ISearchTree {
	private RBNode root;
	
	public RBTree()
	{
		root = null;
	}
	
	public RBTree(int value)
	{
		root = new RBNode(value, color.BLACK, null);
	}
	
	@Override
	public boolean find(int value) {
		RBNode node = root;
		
		while(node != null)
		{
			if(value < node.value)
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
			root = new RBNode(value, color.BLACK, null);
			return;
		}
		
		RBNode node = root;		
		boolean inserted = false;
		
		while(!inserted)
		{
			if(value < node.value)
			{
				if(node.left != null)
					node = node.left;
				else
				{
					RBNode newNode = new RBNode(value, color.RED, node);
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
					RBNode newNode = new RBNode(value, color.RED, node);
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
		
		RBNode node = root;
		
		if(value == 81)
		{
			int a = 0;
		}
		
		while(node!=null)
		{
			if(node.value > value)
				node = node.left;
			else if(node.value < value)
				node = node.right;
			else	// si es igual => lo encontramos!
			{
				// procedemos a chequear los distintos casos:
				
				// 1. no posee hijos:
				if(node.left == null && node.right == null)
				{
					// Si no tiene papá, entonces es la raiz => la borramos
					if(node.parent == null)
						root = null;
					else if(node.parent.left == node)
						node.parent.left = null;
					else if(node.parent.right == node)
						node.parent.right = null;
					
					return;
				}
				// 2. posee 1 hijo
				else if(node.left == null && node.right != null)
				{
					if(node.parent == null)
					{
						root = node.right;
						node.right.parent = root;
					}else if(node.parent.left == node)
					{
						node.parent.left = node.right;
						node.right.parent = node.parent;
					}else if(node.parent.right == node)
					{
						node.parent.right = node.right;						
						node.right.parent = node.parent;
					}
					
					return;
				}
				else if(node.right == null && node.left != null)
				{
					if(node.parent == null)
					{
						root = node.left;
						node.left.parent = root;
					}
					else if(node.parent.left == node){
						node.parent.left = node.left;
						node.left.parent = node.parent;
					}
					else if(node.parent.right == node){
						node.parent.right = node.left;
						node.left.parent = node.parent;
					}
					return;
				}
				// 3. Posee ambos hijos: en este caso lo vamos desplazando por la derecha
				else
				{
					// buscamos el sucesor de node (es decir, el nodo más a la iquierda
					// de su hijo derecho):
					RBNode succesorNode = node.right;
					
					while(succesorNode.left != null)
						succesorNode = succesorNode.left;
					
					// Una vez encontrado, procedemos a cambiar los punteros:
					
					if(succesorNode.right == null)
					{
						int a = 0;
					}
					
					// Cambiamos punteros de la iquierda:
					succesorNode.left = node.left;
					succesorNode.left.parent = succesorNode;
					node.left = null; // borramos referencia (sabemo
					
					// Cambiamos punteros de la derecha:
					// Usamos puntero auxiliar para mantener la derecha
					RBNode auxNode = node.right;
					
					node.right = succesorNode.right;
					
					if(node.right !=null)
						node.right.parent = node;
					
					// debemos chequear el caso en que el sucesor sea el nodo de la derecha:
					if(succesorNode == auxNode)
					{
						succesorNode.right = node;
						succesorNode.parent = node.parent;
						node.parent = succesorNode;
						
						//TODO: Refactoring (3:24 AM = no puedo pensar como optimizarlo ahora)

						// Si el papá original de node es null significa que era la raíz
						// => actualizamos el puntero:
						if(succesorNode.parent == null)
							root = succesorNode;
						else if(succesorNode.parent.left == node)
							succesorNode.parent.left = succesorNode;
						else //if(succesorNode.parent.right == node)
							succesorNode.parent.right = succesorNode;
					}
					else // Esto sive simpre y cuando el sucesor no sea el nodo derecho:
					{
						succesorNode.right = auxNode;
						succesorNode.right.parent = succesorNode;
						
						// Cambiamos punteros de los padres:
						auxNode = succesorNode.parent;
						
						succesorNode.parent = node.parent;
						
						// Si el papá original de node es null significa que era la raíz
						// => actualizamos el puntero:
						if(succesorNode.parent == null)
							root = succesorNode;
						else if(succesorNode.parent.left == node)
							succesorNode.parent.left = succesorNode;
						else //if(succesorNode.parent.right == node)
							succesorNode.parent.right = succesorNode;
						
						// sabemos que succesorNode era el hijo izquierdo
						node.parent = auxNode;
						node.parent.left = node;
					}
				}
			}
		}
	}
	
	public void balance(RBNode node)
	{
		
	}
}
