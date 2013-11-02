package edu.puc.concurrentavl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.Random;

enum ApparentBalanceMode { RELAXED, EXACT }

public class BinaryTree implements ISearchTree {
	private static final int START_VALUE = 10000;
	private static final Random rand = new Random();
	
	private Node root;
	private treegraph W;
	private final Semaphore countLock = new Semaphore(1, true);
	private Thread maintenanceThread; 
	private boolean isMaintaining;
	
	private int totalCount;		// Cant. nodos inc. nodos de ruteo
	private int realCount;		// Can.t nodos sin inc. nodos de ruteo
	
	private ApparentBalanceMode bMode;
	private List<Node> nodesArray;
	
	public BinaryTree(){
		root = new Node(START_VALUE, null);
		root.isRoutingNode = true;	// Creamos raiz como nodo de ruteo
		
		totalCount = 1;
		realCount = 0;
	}
	
	public BinaryTree(ApparentBalanceMode mode){
		root = new Node(START_VALUE, null);
		root.isRoutingNode = true;
		
		totalCount = 1;
		realCount = 0;
		bMode = mode;
		isMaintaining = true;
		
		// Si el modo es exacto, creamos la lista para almacenar los punteros a los
		// nodos:
		if(mode == ApparentBalanceMode.EXACT){
			nodesArray = new ArrayList<Node>();
			nodesArray.add(root);
		}
		
		// Finalmente, hacemos correr el thread de mantenimiento:
		maintenanceThread = new Thread(){ 
			public void run(){
				checkBalance();
			}
		};
		maintenanceThread.start();
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
						
						// Actualizamos los contadores:
						this.countLock.acquire();
						if(bMode == ApparentBalanceMode.EXACT)
							nodesArray.add(newNode);
						totalCount++;			
						realCount++;
						this.countLock.release();
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
						
						// Actualizamos los contadores:
						this.countLock.acquire();
						if(bMode == ApparentBalanceMode.EXACT)
							nodesArray.add(newNode);
						totalCount++;			
						realCount++;
						this.countLock.release();
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
					
					// Actualizamos realCount:
					this.countLock.acquire();
					realCount--;
					this.countLock.release();

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

	private void checkBalance(){
		// Seleccionamos nodo al azar. Si el modo es EXACT, tiramos un
		// random en el total de nodos:
		while(isMaintaining){
			int index = rand.nextInt(totalCount);
			
			Node n = nodesArray.get(index);
			updateHeights(n);
		}
	}
	
	private void updateHeights(Node n){
		try{
			// Tomamos lock del nodo a revisar:
			n.nodeSem.acquire();
			
			// Luego, revisamos si tiene hijo izquierdo. En caso de
			// ser así, actualizamos su altura izquierda:
			Node left = n.left;

			if(left != null){
				// Adquirimos el lock de su hijo:
				left.nodeSem.acquire();
				n.leftHeight = 1 + Math.max(left.leftHeight, left.rightHeight);
				left.nodeSem.release();
			}
			// Repetimos para el nodo derecho:
			Node right = n.right;
			if(right != null){
				// Adquirimos el lock de su hijo:
				right.nodeSem.acquire();
				n.rightHeight = 1 + Math.max(right.leftHeight, right.rightHeight);
				right.nodeSem.release();
			}
			// Finalmente, revisamos si es necesario balancear el árbol:
			int deltaHeight = n.leftHeight - n.rightHeight;
			
			// Si la altura derecha es mayor, entonces rotamos hacia la izquierda;
			// En caso contrario, rotamos hacia la derecha.
			if(deltaHeight <= -2)
				rotateRight(n);
			else if(deltaHeight >= 2)
				rotateLeft(n);
			
			// Finalmente, liberamos el lock del nodo:
			n.nodeSem.release();	
		}
		catch(InterruptedException e){ e.printStackTrace(); }
	}
	
	private void rotateLeft(Node n){
		// Ya tenemos el lock de n, por lo tanto debemos tomar el lock de los
		// nodos que cambiarán => P, L y L.R:
		Node parent = n.parent;
		Node left = n.left;	// no puede ser null, de lo cotrario no podríamos tener deltaH >= 2
		Node lRight = (left != null) ? left.right : null;
		
		try{
			if(parent != null) parent.nodeSem.acquire();
			left.nodeSem.acquire();
			if(lRight != null) lRight.nodeSem.acquire();
			
			// Ahora, procedemos a actualizar los punteros:
			left.setRight(n);
			n.setLeft(lRight);

			if(parent != null)
				parent.changeSon(n, left);
			else
				root = left;
			
			// Actualizamos la altura de n:
			n.leftHeight--;
			
			// Finalmente, liberamos los locks:
			if(parent != null) parent.nodeSem.release();
			left.nodeSem.release();
			if(lRight != null) lRight.nodeSem.release();
		}
		catch(InterruptedException e) { e.printStackTrace();}
	}

	private void rotateRight(Node n){
		// Ya tenemos el lock de n, por lo tanto debemos tomar el lock de los
		// nodos que cambiarán => P, R y R.L:
		Node parent = n.parent;
		Node right = n.right;	// no puede ser null, de lo cotrario no podríamos tener deltaH <= -2
		Node rLeft = (right != null) ? right.left : null;
		
		try{
			if(parent != null) parent.nodeSem.acquire();
			right.nodeSem.acquire();
			if(rLeft != null) rLeft.nodeSem.acquire();
			
			// Ahora, procedemos a actualizar los punteros:
			right.setLeft(n);
			n.setRight(rLeft);
			if(parent != null)
				parent.changeSon(n, right);
			else
				root = right;
			
			// Actualizamos la altura de n:
			n.rightHeight--;
			
			// Finalmente, liberamos los locks:
			if(parent != null) parent.nodeSem.release();
			right.nodeSem.release();
			if(rLeft != null) rLeft.nodeSem.release();
		}
		catch(InterruptedException e) { e.printStackTrace();}		
	}

	public void stopMaintenance(int milis){
		try{
			if(milis > 0)
				Thread.sleep(milis);
			isMaintaining = false;
		}
		catch(Exception e){}
	}
}