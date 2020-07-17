package principal;

/**
 * 
 * @author Abraão Vitor e Henrique David
 * 
 * Classe principal responsável por realizar os testes.
 * 
 * */
public class ListaEncadeada {
	
	private static final int CAPACITY = 100;
	private static final int NUM_THREADS = 10000;
	
	public static void main(String[] args) {
		for(int o = 0; o < 20; o++) {
		List lista = new List(CAPACITY);
		
		Reader reader[] = new Reader[NUM_THREADS];
		Remover remover[] = new Remover[NUM_THREADS];
		Writer writer[] = new Writer[NUM_THREADS];
		
		for(int i = 0; i < NUM_THREADS; i++) {
			reader[i] = new Reader("Reader " + (i+1), CAPACITY, lista);
			remover[i] = new Remover("Remover " + (i+1), lista);
			writer[i] = new Writer("Writer " + (i+1), lista);
		}
		
		for(int i = 0; i < NUM_THREADS; i++) {
			reader[i].start();
			remover[i].start();
			writer[i].start();
		}
		
		try {
			for(int i = 0; i < NUM_THREADS; i++) {
				reader[i].join();
				remover[i].join();
				writer[i].join();
			}
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		}
		
		
		System.out.println("\nFINALIZADO");
	}
}
