package principal;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * @author Abraão Dantas
 * @author Henrique David
 * 
 * */

public class List {
	
	private int capacity;
	private LinkedList<Integer> buffer;
	private Lock lock;
	private Condition canSearch;
	private Condition canInsert;
	private Condition canRemove;
	private Condition isFull;
	private Condition isEmpty;
	
	private int searchers;
	private int inserters;
	private int removers;
	
	private int searchers_waiting;
	private int inserters_waiting;
	private int removers_waiting;
	
	public List(int capacity){
		this.capacity = capacity;
		buffer = new LinkedList<Integer>();
		lock = new ReentrantLock(true);
		canSearch = lock.newCondition();
		canInsert = lock.newCondition();
		canRemove = lock.newCondition();
		
		isFull = lock.newCondition();
		isEmpty = lock.newCondition();
		
		searchers = 0;
		inserters = 0;
		removers = 0;
		
		searchers_waiting = 0;
		inserters_waiting = 0;
		removers_waiting = 0;
	}
	
	public int size() {
		return buffer.size();
	}
	
	public void insert(Integer i) {
		lock.lock();
		try {
			if(inserters == 1 || removers == 1) {
				System.out.println("Já há um processo de inserção ou remoção rodando!");
				++inserters_waiting;
				canInsert.await();
				--inserters_waiting;
			}
			
			while(buffer.size() == capacity) {
				System.out.println("A lista está cheia");
				isFull.await();
			}
			
			inserters = 1;
			buffer.add(i);
			inserters = 0;
			
			if(removers_waiting > 0) {
				canRemove.signal();
			} else {
				if (inserters_waiting > 0) {
					canInsert.signal();
				}
				if (searchers_waiting > 0) {
					canSearch.signal();
				}
			}
			
			isEmpty.signal();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		
	}
	
	public Integer find(Integer pos) {
		lock.lock();
		Integer find = null;
		try {
			if(removers == 1) {
				System.out.println("Já há um processo de remoção rodando!");
				++searchers_waiting;
				canSearch.await();
				--searchers_waiting;
			}
			
			++searchers;
			find = buffer.get(pos);
			--searchers;
			
			if(removers_waiting > 0) {
				canRemove.signal();
			} else if (searchers_waiting > 0) {
				canSearch.signal();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
//			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		return find;
	}
	
	public void remove() {
		lock.lock();
		try {
			if(inserters == 1 || removers == 1 || searchers > 0) {
				System.out.println("Já há um processo de inserção, remoção ou busca rodando!");
				++removers_waiting;
				canRemove.await();
				--removers_waiting;
			}
			
			while(buffer.size() == 0) {
				System.out.println("A lista está vazia!");
				isEmpty.await();
			}
			
			removers = 1;
			buffer.removeLast();
			removers = 0;
			
			if(inserters_waiting > 0) {
				canInsert.signal();
			} else {
				if (searchers_waiting > 0) {
					canSearch.signal();
				} else  if (removers_waiting > 0){
					canRemove.signal();
				}
			}
			
			isFull.signal();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	

}
