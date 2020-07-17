package principal;

public class Writer extends Thread{
	
	private List list;
	
	public Writer(String name, List l) {
		super(name);
		this.list = l;
	}
	
	@Override
	public void run() {
		int value = (int) (Math.random() * 50) + 1;
		System.out.println("Thread " + this.getName() + " vai inserir");
		list.insert(value);
		System.out.println("Thread " + this.getName() + " inseriu");
	}

}
