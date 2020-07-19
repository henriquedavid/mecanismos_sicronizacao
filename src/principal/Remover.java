package principal;

/**
 * 
 * @author Abraão Vitor e Henrique David
 * 
 * Thread responsável por realizar a operação de remoção na 
 * lista.
 * 
 * */
public class Remover extends Thread{
	// Lista a possuir valor removido
	private List list;
	
	/**
	 * Construtor da classe Remover
	 * 
	 * @param name nome da thread
	 * @param l_ lista a ser removido valores
	 */
	public Remover(String name, List l_) {
		super(name);
		this.list = l_;
	}
	
	/**
	 * Método para iniciar a thread e remover valor
	 * da lista.
	 */
	@Override
	public void run() {
		int position = (int) (Math.random() * list.size());
		list.remove(position);
	}
}
