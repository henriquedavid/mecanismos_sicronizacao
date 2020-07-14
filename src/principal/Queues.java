package principal;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * @author Abraão Vitor
 * @author Henrique David
 * 
 * */

public class Queues {
	private int capacity;
	private Queue<Integer> buffer;
	private Lock lock;
	private Condition full;
	private Condition empty;
	
	public Queues(int capacity){
		this.capacity = capacity;
		buffer = new LinkedList<Integer>();
		lock = new ReentrantLock(true);
		full = lock.newCondition();
		empty = lock.newCondition();
	}
	
	public void insert(Integer i) {
		
	}
	
	public Integer find(Integer pos) {
		return 0;
	}
	
	public void remove() {
		
	}
	

}
