package edu.puc.concurrentavl;

import java.util.concurrent.Semaphore;

public class BinaryTree implements ISearchTree {
	private Node root;
	private final Semaphore rootSem = new Semaphore(1, false);
	private treegraph W;
	
	public BinaryTree(){
		root = new Node(0, null);
		root.isRoutingNode = true;	// Creamos raiz como nodo de ruteo
	}
	
	@Override
	public boolean find(int value) {
		Node node = root;
		Semaphore currentSem = root.nodeSem;

		// Primero, bloqueamos el semáforo asociado a root:
		try{
			currentSem.acquire();
			
			// Posterior a esto, procedemos a recorrer el árbol buscando el valor pedido:
			Node nextNode = null;
			
			while(node != null){
				
				// Actualizamos el valor de currentSem (en la 1ra iteración será el mismo)
				currentSem = node.nodeSem;							
				
				if(value < node.value || (value == node.value && node.isRoutingNode)){
					nextNode = node.left;
				}
				else if(value > node.value){
					nextNode = node.right;
				}
				else{
					// Si encontramos el nodo, liberamos el semáforo actual
					currentSem.release();	// Liberamos el lock del nodo
					return true;			// Retornamos
				}
				
				// Intentamos adquirir el lock del siguiente semáforo. Una vez
				// obtenido, liberamos el nodo del padre:
				if(nextNode !=  null)
					nextNode.nodeSem.acquire();
				currentSem.release();
				node = nextNode;
			}
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void insert(int value) {
		Node node = root;
		Semaphore currentSem = root.nodeSem;

		// Primero, bloqueamos el semáforo asociado a root:
		try{
			currentSem.acquire();
			
			boolean inserted = false;
			
			// nodo a insertar. Declaramos el padre como null (lo actualizaremos al insertarlo)
			Node newNode = new Node(value, null);
			
			Node nextNode = null;
	
			while(!inserted){
				currentSem = node.nodeSem;
				
				if(value < node.value || (value == node.value && node.isRoutingNode))
				{
					if(node.left != null)
						nextNode = node.left;
					else{
						// Si el hijo es null, entonces debemos insertarlo ahí
						
						node.setLeft(newNode);	// Seteamos el hijo
						currentSem.release();	// Liberamos el lock
						inserted = true;		// Indicamos que ya lo insertamos
					}
				}
				else if(value > node.value)
				{
					if(node.right != null)
						nextNode = node.right;
					else{
						// Si el hijo es null, entonces debemos insertarlo ahí
						
						node.setRight(newNode);	// Seteamos el hijo
						currentSem.release();	// Liberamos el lock
						inserted = true;		// Indicamos que ya lo insertamos
					}
				}
				else{
					currentSem.release();
					return;
				}
				// Intentamos adquirir el lock del siguiente semáforo. Una vez
				// obtenido, liberamos el nodo del padre:
				if(!inserted){
					nextNode.nodeSem.acquire();
					currentSem.release();
					node = nextNode;
				}
			}
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
	}

	@Override
	public void delete(int value) {
		Node node = root;		
		Semaphore currentSem = root.nodeSem;
		
		try{
			currentSem.acquire();

			Node nextNode = null;	// Nodo que usaremos para

			while(node != null){
				currentSem = node.nodeSem;
				
				if(value < node.value || (value == node.value && node.isRoutingNode))
					nextNode = node.left;
				
				else if(value > node.value)
					nextNode = node.right;
				else{
					// Si el valor es el mismo, significa que lo encontramos.
					// Luego, lo marcamos como nodo de ruteo y retornamos:
					node.isRoutingNode = true;
					currentSem.release();
					return;
				}
				if(nextNode !=  null)
					nextNode.nodeSem.acquire();
				currentSem.release();
				node = nextNode;
			}
		}
		catch(InterruptedException e){ e.printStackTrace(); }
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
