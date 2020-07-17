package principal;

public class Remover extends Thread{

	private List list;
	
	public Remover(String name, List l_) {
		super(name);
		this.list = l_;
	}
	
	@Override
	public void run() {
		System.out.println("Thread " + this.getName() + " vai remover");
		list.remove();
		System.out.println("Thread " + this.getName() + " removeu" );
	}
}
