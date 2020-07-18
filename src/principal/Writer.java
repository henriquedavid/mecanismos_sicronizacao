package principal;

/**
 * 
 * @author Abraão Vitor e Henrique David
 * 
 * Thread responsável por realizar as operações de
 * escrita na lista.
 * 
 * */
public class Writer extends Thread{
	
	// Lista a ser inserido valor
	private List list;
	
	/**
	 * Construtor da classe Writer
	 * 
	 * @param name nome da thread
	 * @param l lista a ser inserido valores
	 */
	public Writer(String name, List l) {
		super(name);
		this.list = l;
	}
	
	/**
	 * Método para iniciar a thread e inserir valor aleatório
	 * na lista.
	 */
	@Override
	public void run() {
		int value = (int) (Math.random() * 50) + 1;
		System.out.println("Thread " + this.getName() + " vai inserir");
		list.insert(value);
		System.out.println("Thread " + this.getName() + " inseriu");
	}

}
