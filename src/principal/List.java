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
	private Condition search;
	private Condition busy;
	
	private int searchers;
	private int inserters;
	private int removers;
	
	public List(int capacity){
		this.capacity = capacity;
		buffer = new LinkedList<Integer>();
		lock = new ReentrantLock(true);
		search = lock.newCondition();
		busy = lock.newCondition();
		searchers = 0;
		inserters = 0;
		removers = 0;
	}
	
	public void insert(Integer i) {
		lock.lock();
		try {
			if(inserters == 1 || removers == 1) {
				busy.await();
			}
			
			inserters = 1;
			buffer.add(i);
			inserters = 0;
			busy.signal();
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
				busy.await();
			}
			
			++searchers;
			find = buffer.get(pos);
			--searchers;
			busy.signal();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		return find;
	}
	
	public void remove() {
		lock.lock();
		try {
			if(inserters == 1 || removers == 1 || searchers > 0) {
				busy.await();
			}
			
			removers = 1;
			buffer.removeLast();
			removers = 0;
			busy.signal();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	

}
