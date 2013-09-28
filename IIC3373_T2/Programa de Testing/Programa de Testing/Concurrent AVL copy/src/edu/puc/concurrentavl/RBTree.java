package edu.puc.concurrentavl;

enum color {RED, BLACK};

public class RBTree implements ISearchTree {
	private RBNode root;
	private treegraph W = new treegraph(800,600);
	
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
		
		// nodo a insertar. Declaramos el padre como null (lo actualizaremos al insertarlo)
		RBNode newNode = new RBNode(value, color.RED, null);

		while(!inserted)
		{
			if(value < node.value)
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
		
		// una vez insertado, balanceamos:
		balance(newNode);
	}
	
	public void balance(RBNode node)
	{
		// Si el nodo es la raíz, cambiamos su color a negro
		if(node.parent == null){
			node.color = color.BLACK;
			return;
		}
		
		// Si el padre es negro, no hacemos nada
		if(node.parent.color == color.BLACK)
			return;
		
		// Si el padre es rojo, tenemos 2 casos:
		// (NOTA: el hecho de que el padre sea rojo nos dice que no es raíz -> tiene padre!)
		// (NOTA2: además, el que sea rojo indica que no tiene más hijos
		
		// 1. El hermano del padre es negro (si no existe, lo suponemos negro):
		if(node.parent.sibling() == null || node.parent.sibling().color == color.BLACK)
		{
			RBNode parentNode = node.parent;
			RBNode grandParentNode = parentNode.parent;

			//TODO: refactoring
			// Si node es hijo izquierdo y node.parent es hijo izquierdo => rotación simple!
			if(parentNode.left == node && grandParentNode.left == parentNode)
			{
				grandParentNode.left = parentNode.right;
				if(grandParentNode.left != null)
					grandParentNode.left.parent = grandParentNode;
				
				parentNode.parent = grandParentNode.parent;
				if(parentNode.parent == null)
					root = parentNode;
				else if(parentNode.parent.left == grandParentNode)
					parentNode.parent.left = parentNode;
				else if(parentNode.parent.right == grandParentNode)
					parentNode.parent.right = parentNode;
				
				parentNode.right = grandParentNode;
				grandParentNode.parent = parentNode;
				
				// Finalmente, actualizamos los colores:
				parentNode.color = color.BLACK;
				grandParentNode.color = color.RED;
			}
			// Si node es hijo derecho y node.parent es hijo derecho => rotación simple!
			else if(parentNode.right == node && grandParentNode.right == parentNode)
			{
				grandParentNode.right = parentNode.left;
				if(grandParentNode.right != null)
					grandParentNode.right.parent = grandParentNode;
				
				parentNode.parent = grandParentNode.parent;
				if(parentNode.parent == null)
					root = parentNode;
				else if(parentNode.parent.right == grandParentNode)
					parentNode.parent.right = parentNode;
				else if(parentNode.parent.left == grandParentNode)
					parentNode.parent.left = parentNode;
				
				parentNode.left = grandParentNode;
				grandParentNode.parent = parentNode;
				
				// Finalmente, actualizamos los colores:
				parentNode.color = color.BLACK;
				grandParentNode.color = color.RED;
			}
			// node es hijo derecho, node.parent es hijo izquierdo -> rotación doble:
			else if(parentNode.right == node && grandParentNode.left == parentNode)
			{
				// Intercambiamos parentNode y node:
				parentNode.right = node.left;
				if(parentNode.right != null)
					parentNode.right.parent = parentNode;
				
				node.parent = parentNode.parent;
				grandParentNode.left = node;
				
				node.left = parentNode;
				parentNode.parent = node;
				
				// llamamos a balance para parentNode
				balance(parentNode);
			}
			// node es hijo izquierdo, node.parent es hijo derecho -> rotación doble:
			else if(parentNode.left == node && grandParentNode.right == parentNode)
			{
				// Intercambiamos parentNode y node:
				parentNode.left = node.right;
				if(parentNode.left != null)
					parentNode.left.parent = parentNode;
				
				node.parent = parentNode.parent;
				grandParentNode.right = node;
				
				node.right = parentNode;
				parentNode.parent = node;
				
				// llamamos a balance para parentNode
				balance(parentNode);
			}
		}
		else
		{
			// 2. Tanto su papá como su hermano son negros:
			// En este caso pintamos al abuelo rojo y pintamos al papá y su hermano negros:
			node.parent.color = color.BLACK;
			node.parent.sibling().color = color.BLACK;
			node.parent.parent.color = color.RED;
			
			// luego, rebalanceamos partiendo desde el abuelo
			balance(node.parent.parent);
		}
		// Finalmente, restauramos la propiedad que la raíz sea negra:
		root.color = color.BLACK;
	}
	
	public boolean checkBalance(RBNode node)
	{
		return true;
	}
	
	public void printTree()
	{
		//printTree(root, 0);
	      W.drawtree(root);
	      //W.display.drawString("Do you like my tree?",20,W.YDIM-50);
	      try{Thread.sleep(3000);} catch(Exception e) {} // 5 sec delay
	}
	
	private void printTree(RBNode n, int s)
	{
		if(n == null)
			return;
		
		String tab = "\t";
		int j;
		for(j=0; j < s; j++)
			tab = tab + "\t";
		
		System.out.println(tab + n.value + "\n");
		int i;
		
		printTree(n.left, s+1);
		printTree(n.right, s+1);
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
					if(succesorNode.left != null)
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
}
