package edu.puc.concurrentavl;

import java.util.concurrent.*;
import java.util.concurrent.locks.*;

enum SyncPolicy { AllExclusive, MultiSearch }

public class SyncSearchTree implements ISearchTree {
	
	private ISearchTree tree;
	private final Semaphore rw = new Semaphore(1, true); 
	private final Semaphore counterMutex; 
	private int readerCount = 0;
	private SyncPolicy syncPolicy;

	private boolean exclusiveSearch(int value) throws InterruptedException
	{
		boolean result = false;
		
		rw.acquire();
			result = tree.find(value);
		rw.release();
		
		return result;
	}
	
	private boolean multipleSearch(int value) throws InterruptedException
	{
		boolean result = false;
		
		// Adquirimos el contador
		counterMutex.acquire();
			// Si no hay lectores, pedimos el semáforo
			if(readerCount == 0) rw.acquire();	
			readerCount++;	// Incrementamos su valor
		counterMutex.release();
		
		// Llamamos a tree.find(value)
		result = tree.find(value);
		
		// Volvemos a editar el contador:
		counterMutex.acquire();
		// Si no quedan lectores, liberamos el semáforo
			readerCount--;
			if(readerCount == 0) rw.release();	
		counterMutex.release();
		
		return result;
	}
	
	@Override
	public boolean find(int value) {
		// Permitimos múltiples búsquedas simultáneas:
		boolean result = false;

		try
		{	
			// Adquirimos el contador
			counterMutex.acquire();
				// Si no hay lectores, pedimos el semáforo
				if(readerCount == 0) rw.acquire();	
				readerCount++;	// Incrementamos su valor
			counterMutex.release();
			
			// Llamamos a tree.find(value)
			result = tree.find(value);
			
			// Volvemos a editar el contador:
			counterMutex.acquire();
			// Si no quedan lectores, liberamos el semáforo
				readerCount--;
				if(readerCount == 0) rw.release();	
			counterMutex.release();
		}
		catch(InterruptedException e)
		{ 
			System.out.println("Find Sync error al procesar value = " + value);
		}

		return result;
	}

	@Override
	public void insert(int value) {
		try
		{
			rw.acquire();
				tree.insert(value);
			rw.release();
		}
		catch(InterruptedException e)
		{
			System.out.println("Insert Sync error al intentar insertar " + value);
		}
	}

	@Override
	public void delete(int value) {
		try
		{
			rw.acquire();
				tree.delete(value);
			rw.release();
		}
		catch(InterruptedException e)
		{
			System.out.println("Delete Sync error al intentar borrar " + value);
		}
	}

	@Override
	public void printTree() {
		//tree.printTree();
	}
	
	public SyncSearchTree(SyncPolicy policy, boolean fair)
	{
		this.tree = new RBTree();
		syncPolicy = policy;
		counterMutex = new Semaphore(1, fair);
	}
	
	public SyncSearchTree(ISearchTree tree, SyncPolicy policy, boolean fair)
	{
		this.tree = tree;
		syncPolicy = policy;
		counterMutex = new Semaphore(1, fair);
	}
}
