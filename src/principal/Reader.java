package principal;

/**
 * 
 * @author Abraão Vitor e Henrique David
 * 
 * Thread responsável por realizar a leitura de uma
 * determinada posição na lista.
 * 
 * */
public class Reader extends Thread{
	
	private int capacity;
	private List list;
	
	public Reader(String name, int cap, List l_) {
		super(name);
		this.capacity = cap;
		this.list = l_;
	}
	
	@Override
	public void run() {
		int position = (int) (Math.random() * list.size());
		System.out.println("Thread " + this.getName() + " vai ler");
		Integer v = list.find(position);
		System.out.println("Thread " + this.getName() + " - Lista[" + position + "] = " + v);
	}

}
