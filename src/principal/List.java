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
 * Classe responsável por controlar as operações de escrita, remoção e
 * recuperação de um valor em uma determinada posição, além de realizar
 * os tratamentos para evitar ocorrências de deadlock, livelock e starvation.
 * 
 * */

public class List {
	// Capacidade da lista
	private int capacity;
	/*
	 *  Lista simplesmente encadeada responsável por armazenar os valores inteiros.
	 */
	private LinkedList<Integer> buffer;
	// Lock responsável por controlar a região crítica.
	private Lock lock;
	/*
	 * Condição para verificar se pode realizar sua respectiva ação
	 */
	private Condition canSearch;
	private Condition canInsert;
	private Condition canRemove;
	
	/*
	 * Validador de status do buffer.
	 */
	private Condition isFull;
	private Condition isEmpty;
	
	/* Controlar a quantidade de leitores, escritores e removedores que estão
	 * sendo executados
	 * */
	private int searchers;
	private int inserters;
	private int removers;
	
	// Contabilizar quantas threads estão aguardando, para cada tipo de ação
	private int searchers_waiting;
	private int inserters_waiting;
	private int removers_waiting;
	
	/**
	 * Construtor da classe List
	 * 
	 * @param capacity capacidade da lista
	 */
	public List(int capacity){
		this.capacity = capacity;
		// Inicializar lista simplesmente encadeada de inteiros 
		buffer = new LinkedList<Integer>();
		// Inicializa o Lock
		lock = new ReentrantLock(true);
		// criar as variáveis de condições
		canSearch = lock.newCondition();
		canInsert = lock.newCondition();
		canRemove = lock.newCondition();
		
		isFull = lock.newCondition();
		isEmpty = lock.newCondition();
		
		// Inicializar contabilizadores
		searchers = 0;
		inserters = 0;	
		removers = 0;
		
		searchers_waiting = 0;
		inserters_waiting = 0;
		removers_waiting = 0;
	}
	
	/**
	 * Retornar o tamanho da lista 
	 */
	public int size() {
		return buffer.size();
	}
	
	/**
	 * Inserir elemento na lista
	 * 
	 * @param i Valor a ser inserido 
	 */
	public void insert(Integer i) {
		lock.lock();
		try {
			/*
			 * Verificar se no buffer já existe uma thread inserindo ou algum removedor
			 * está realizando alguma operação, condições essas que não são permitidas
			 * no problema, logo colocando a thread para esperar até que será possível.
			 */
			if(inserters == 1 || removers == 1) {
				System.out.println(Thread.currentThread().getName() + ": Já há um processo de inserção ou remoção rodando!");
				// Incrementar escritores esperando
				++inserters_waiting;
				// Coloca o processo para esperar
				canInsert.await();
				// Incrementar escritores esperando
				--inserters_waiting;
			}
			
			/*
			 * Verifica se o buffer não está na capacidade
			 */
			while(buffer.size() == capacity) {
				System.out.println("A lista está cheia");
				// Faz o processo esperar
				isFull.await();
			}
			
			// Informa que um thread de inserção entrou na região crítica
			inserters = 1;
			// Adiciona o valor na lista
			buffer.add(i);
			System.out.println("Thread " + Thread.currentThread().getName() + " inseriu valor = " + i);
			// Infoma que já foi inserido
			inserters = 0;
			
			/*
			 * Aplica uma prioridade de que as threads responsáveis pela remoção
			 * possuem prioridade, por isso verifica se não há nenhuma thread
			 * com essa finalidade esperando.
			 */
			if(removers_waiting > 0 && inserters == 0 && searchers == 0) {
				// Informa que pode realizar a remoção
				canRemove.signal();
			} else {
				// Verifica se existe alguma thread de inserção esperando
				if (inserters_waiting > 0) {
					// Informa que pode escrever
					canInsert.signal();
				}
				// Verifica se existe alguma thread de busca esperando
				if (searchers_waiting > 0) {
					// Informa que pode buscar
					canSearch.signal();
				}
			}
			
			isEmpty.signal();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			// Libera o espaço
			lock.unlock();
		}
		
	}
	
	/**
	 * Função find responsável por retornar o elemento que está em uma
	 * posição específica na lista.
	 * 
	 * @param pos posição que deseja buscar
	 * 
	 * @return valor que se encontra na posição desejada.
	 */
	public Integer find(Integer pos) {
		lock.lock();
		Integer find = null;
		try {
			/*
			 * Verifica se há threads removedoras no buffer, já que uma das restrições
			 * é que a remoção seja mútuamente excludente para a operação de escrita e
			 * busca.
			 */
			if(removers == 1) {
				System.out.println("Já há um processo de remoção rodando!");
				// Adiciona mais uma thread de busca a espera
				++searchers_waiting;
				// Bloqueia a thread
				canSearch.await();
				// Decrementa o contador de espera da busca
				--searchers_waiting;
			}
			
			// Incrementa que mais uma thread está executando.
			++searchers;
			// Verifica se a posição informada é válida na lista
			if(pos < buffer.size()) {
				find = buffer.get(pos);
				System.out.println("Thread " + Thread.currentThread().getName() + ": lista["+pos + "] = " + find);
			}
			else {
				System.out.println("Thread " + Thread.currentThread().getName() + ": Posição não existe");
			}
			// Decrementa a quantidade de threads de busca executando
			--searchers;
			
			/*
			 * Atribui prioridade aos removedores, de forma que se existir
			 * alguma thread de remoção esperando notifique que pode remover.
			 * Caso contrário, notifique que pode continuar a busca.
			 * Note que, para que haja a notificação das threads de remoção é necessário
			 * que não haja leitores executando
			 */
			if(removers_waiting > 0 && searchers == 0 && inserters == 0) {
				canRemove.signal();
			} else if (searchers_waiting > 0) {
				canSearch.signal();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		// Retorna o valor da posição informada na lista.
		return find;
	}
	
	/**
	 * Função responsável por remover o elemento que se encontra na
	 * cabeça da lista encadeada.
	 */
	public void remove(int pos_) {
		lock.lock();
		try {
			/*
			 * Verifica se existe condições exclusivas, ou seja, a operação de remoção é
			 * mútuamente exclusiva para todas as outras operações e para ela mesma, de forma
			 * que não pode haver nenhum escritor, nem leitor, nem um remover sendo executado.
			 */
			if(inserters == 1 || removers == 1 || searchers > 0) {
				System.out.println("Já há um processo de inserção, remoção ou busca rodando!");
				// Incrementa os removedores esperando.
				++removers_waiting;
				// Coloca a thread para dormir
				canRemove.await();
				// Decrementa os removedores esperando
				--removers_waiting;
			}
			
			// Verifica se a lista não está vázia. Se estiver, bloqueie.
			while(buffer.size() == 0) {
				System.out.println("A lista está vazia!");
				isEmpty.await();
			}
			
			//Informa que a thread de remoção irá executar.
			removers = 1;
			try {
				// Remove o elemento de uma posição da lista
				if(pos_ < buffer.size()) {
					buffer.remove(pos_);
					System.out.println("Thread " + Thread.currentThread().getName() + " removeu valor na posição " + pos_);
				} else {
					System.out.println("Thread " + Thread.currentThread().getName() + ": Posição para remoção não válida.");
				}
			} catch(IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
			
			Thread.sleep(1000);
			// Informa que foi realizada a remoção
			removers = 0;
			
			/*
			 * A prioridade é aplicada aos inscritores, ou seja, se houver 
			 * escritores esperando então execute.
			 */
			if(inserters_waiting > 0) {
				// Acorda as threads de inserção
				canInsert.signal();
			} else {
				/*
				 * Verifica se existe alguma thread esperando para realizar
				 * leitura, se sim, então acorda as threads de leitura; caso contrário,
				 * acorda verifica se há threads de remoção e se houver, então acordar elas.
				 */
				if (searchers_waiting > 0) {
					canSearch.signal();
				} else  if (removers_waiting > 0){
					canRemove.signal();
				}
			}
			
			isFull.signal();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	

}
