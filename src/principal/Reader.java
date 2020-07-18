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
	// Lista a ser buscado valor em uma posição
	private List list;
	
	/**
	 * Construtor da classe Reader
	 * 
	 * @param name nome da thread
	 * @param l_ lista a ser lido valores
	 */
	public Reader(String name, List l_) {
		super(name);
		this.list = l_;
	}

	/**
	 * Método para iniciar a thread e buscar um valor em uma
	 * posição da lista.
	 */
	@Override
	public void run() {
		int position = (int) (Math.random() * list.size());
		Integer v = list.find(position);
	}

}
