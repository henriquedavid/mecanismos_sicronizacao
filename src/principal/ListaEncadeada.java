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
	private static final int NUM_THREADS = 2000;
	
	public static void main(String[] args) {
		for(int o = 0; o < 100; o++) {
		// Inicializar lista com capacidade determinada
		List lista = new List(CAPACITY);
		
		// Criar vetores das respectivas threads
		Reader reader[] = new Reader[NUM_THREADS];
		Remover remover[] = new Remover[NUM_THREADS];
		Writer writer[] = new Writer[NUM_THREADS];
		
		// Inicializar cada thread
		for(int i = 0; i < NUM_THREADS; i++) {
			reader[i] = new Reader("Reader " + o + " - " + (i+1), lista);
			remover[i] = new Remover("Remover " + o + " - " + (i+1), lista);
			writer[i] = new Writer("Writer " + o + " - " + (i+1), lista);
		}
		
		// Colocar threads para começar
		for(int i = 0; i < NUM_THREADS; i++) {
			reader[i].start();
			remover[i].start();
			writer[i].start();
		}
		
		try {
			// Esperar todas as threads finalizarem
			for(int i = 0; i < NUM_THREADS; i++) {
				reader[i].join();
				remover[i].join();
				writer[i].join();
			}
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		}
		
		
		// Informar que o programa foi finalizado
		System.out.println("\nFINALIZADO");
	}
}
