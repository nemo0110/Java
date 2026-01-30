import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class MapToArray {

	static Map<Integer, Integer> mutableMap = new HashMap<Integer, Integer>(Map.of(1, 100, 2, 200, 3, 300));

	public static void mapToArray() {
		for (Map.Entry<Integer, Integer> entry : mutableMap.entrySet()) {
			System.out.println(entry.getKey() + "/" + entry.getValue());
		}

		// SOLO VALORI
		// Opzione A - Stream (Java 8+)
		int[] values = mutableMap.values().stream().mapToInt(Integer::intValue).toArray();
		// Opzione B - Loop classico
		int[] values2 = new int[mutableMap.size()];
		int i = 0;
		for (Integer value : mutableMap.values()) {
			values2[i++] = value;
		}

		// SOLO CHIAVI
		// Stream
		int[] keys = mutableMap.keySet().stream().mapToInt(Integer::intValue).toArray();
		// Loop
		int[] keys2 = new int[mutableMap.size()];
		int j = 0;
		for (Integer key : mutableMap.keySet()) {
			keys2[j++] = key;
		}

		// CHIAVI VALORI
		int[] keyValuePairs = new int[mutableMap.size() * 2];
		int k = 0;
		for (Map.Entry<Integer, Integer> entry : mutableMap.entrySet()) {
			keyValuePairs[k++] = entry.getKey();
			keyValuePairs[k++] = entry.getValue();
		}

		// DUE ARRAY SEPARATI
		int[] keys0 = new int[mutableMap.size()];
		int[] values0 = new int[mutableMap.size()];

		int l = 0;
		for (Map.Entry<Integer, Integer> entry : mutableMap.entrySet()) {
			keys0[l] = entry.getKey();
			values0[l] = entry.getValue();
			l++;
		}

		System.out.println(Arrays.toString(values));
		System.out.println(Arrays.toString(values2));
		System.out.println(Arrays.toString(keys));
		System.out.println(Arrays.toString(keys2));
		System.out.println(Arrays.toString(keyValuePairs));
		System.out.println("Keys: " + Arrays.toString(keys0));
		System.out.println("Values: " + Arrays.toString(values0));
	}

// Stream flatMap
	public static void mapToArrayAdvanced1() {
		int[] keyValuePairs = mutableMap.entrySet().stream()
				.flatMapToInt(entry -> IntStream.of(entry.getKey(), entry.getValue())).toArray();

		// Più leggibile con metodo separato
		int[] keyValuePairs2 = mutableMap.entrySet().stream().flatMapToInt(Solution::entryToIntPair).toArray();
		System.out.println(Arrays.toString(keyValuePairs));
		System.out.println(Arrays.toString(keyValuePairs2));
	}

	private static IntStream entryToIntPair(Map.Entry<Integer, Integer> entry) {
		return IntStream.of(entry.getKey(), entry.getValue());
	}

	// Collectors custom
	public static void mapToArrayAdvanced2() {
		/*
		 * int[] keyValuePairs = mutableMap.entrySet().stream() .collect( () -> new
		 * int[mutableMap.size() * 2], (array, entry) -> { int idx = AtomicInteger //
		 * vedi sotto array[idx * 2] = entry.getKey(); array[idx * 2 + 1] =
		 * entry.getValue(); }, (arr1, arr2) -> System.arraycopy(arr2, 0, arr1,
		 * arr1.length, arr2.length) );
		 */
		AtomicInteger idx = new AtomicInteger(0);
		int[] keyValuePairs0 = mutableMap.entrySet().stream().collect(() -> new int[mutableMap.size() * 2],
				(array, entry) -> {
					int i = idx.getAndAdd(2); // incrementa di 2 e ritorna valore precedente
					array[i] = entry.getKey();
					array[i + 1] = entry.getValue();
				}, (arr1, arr2) -> {
				} // combiner vuoto per stream sequenziale
		);
		// Versione corretta con indice esterno
		final int[] index = { 0 };
		int[] keyValuePairs1 = mutableMap.entrySet().stream().sequential() // importante!
				.collect(() -> new int[mutableMap.size() * 2], (array, entry) -> {
					array[index[0]++] = entry.getKey();
					array[index[0]++] = entry.getValue();
				}, (a, b) -> {
				});

		/*
		 * In questo esatto contesto sequenziale, AtomicInteger non è strettamente
		 * necessario e potrebbe essere sostituito da un array a un elemento o un
		 * oggetto contatore personalizzato. Tuttavia, il suo utilizzo qui è una buona
		 * pratica difensiva e rende il codice immediatamente "thread-safe" e pronto per
		 * essere parallelizzato. La regola principale quando si lavora con le lambda in
		 * Java (e di conseguenza con gli Stream) è che qualsiasi variabile locale
		 * utilizzata all'interno della lambda deve essere "final" o
		 * "effectively final". Questo significa che non puoi modificare direttamente
		 * una variabile dichiarata all'esterno. Il compilatore ti impedisce di
		 * modificare idx perché, se lo stream venisse eseguito in parallelo, più thread
		 * potrebbero tentare di leggere e scrivere idx contemporaneamente, portando a
		 * una race condition e a risultati imprevedibili e non deterministici.
		 */

		// CODICE ERRATO - NON COMPILA
		int idx0 = 0;
		int[] keyValuePairs2 = mutableMap.entrySet().stream().collect(() -> new int[mutableMap.size() * 2],
				(array, entry) -> {
					array[idx0] = entry.getKey();
					array[idx0 + 1] = entry.getValue();
					// idx0 += 2; // ERRORE DI COMPILAZIONE: idx non è "effectively final"
				}, (arr1, arr2) -> {
				});
		System.out.println(Arrays.toString(keyValuePairs0));
		System.out.println(Arrays.toString(keyValuePairs1));

	}
	
	// ArrayList intermedio
	public static void mapToArrayAdvanced3() {
		List<Integer> list = new ArrayList<Integer>(mutableMap.size() * 2);
		mutableMap.forEach((key, value) -> {
			list.add(key);
			list.add(value);
		});

		int[] keyValuePairs0 = list.stream().mapToInt(Integer::intValue).toArray();

		// Oppure senza stream
		int[] keyValuePairs1 = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			keyValuePairs1[i] = list.get(i);
		}
		System.out.println(Arrays.toString(keyValuePairs0));
		System.out.println(Arrays.toString(keyValuePairs1));
	}

// IntBuffer (NIO, più efficiente per grandi volumi)
	public static void mapToArrayAdvanced4() {
		IntBuffer buffer = IntBuffer.allocate(mutableMap.size() * 2);
		mutableMap.forEach((key, value) -> {
			buffer.put(key);
			buffer.put(value);
		});

		int[] keyValuePairs0 = buffer.array();

		// Oppure con iterator per controllo
		IntBuffer buffer2 = IntBuffer.allocate(mutableMap.size() * 2);
		Iterator<Map.Entry<Integer, Integer>> iter = mutableMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Integer, Integer> entry = iter.next();
			buffer2.put(entry.getKey()).put(entry.getValue());
		}
		int[] keyValuePairs1 = buffer2.array();
		System.out.println(Arrays.toString(keyValuePairs0));
		System.out.println(Arrays.toString(keyValuePairs1));
	}

// Parallel Stream (per map enormi >100k elementi)
	public static void mapToArrayAdvanced5() {
		AtomicInteger idx = new AtomicInteger(0);
		int[] keyValuePairs = new int[mutableMap.size() * 2];

		mutableMap.entrySet().parallelStream().forEach(entry -> {
			int i = idx.getAndAdd(2);
			keyValuePairs[i] = entry.getKey();
			keyValuePairs[i + 1] = entry.getValue();
		});

		// Meglio: usa TreeMap per ordine garantito + parallel
		Map<Integer, Integer> sortedMap = new TreeMap<Integer, Integer>(mutableMap);
		int[] sorted = sortedMap.entrySet().stream().flatMapToInt(e -> IntStream.of(e.getKey(), e.getValue()))
				.toArray();

		System.out.println(Arrays.toString(keyValuePairs));
		System.out.println(Arrays.toString(sorted));
	}
//Metodo factory ottimizzato con sorting opzionale

	public static int[] toInterleavedArray(Map<Integer, Integer> map, Order order) {
	        Stream<Map.Entry<Integer, Integer>> stream = map.entrySet().stream();
	        
	        // Applica ordinamento se richiesto
	        stream = switch (order) {
	            case KEY_ASC -> stream.sorted(Map.Entry.comparingByKey());
	            case KEY_DESC -> stream.sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()));
	            case VALUE_ASC -> stream.sorted(Map.Entry.comparingByValue());
	            case VALUE_DESC -> stream.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()));
	            case INSERTION -> stream; // nessun sort
	        };
	        
	        return stream
	            .flatMapToInt(e -> IntStream.of(e.getKey(), e.getValue()))
	            .toArray();
	    }
	    
	    // Versione con filtro
	    public static int[] toInterleavedArray(
	        Map<Integer, Integer> map, 
	        Order order,
	        java.util.function.Predicate<Map.Entry<Integer, Integer>> filter
	    ) {
	        return map.entrySet().stream()
	            .filter(filter)
	            .sorted(getComparator(order))
	            .flatMapToInt(e -> IntStream.of(e.getKey(), e.getValue()))
	            .toArray();
	    }
	    
	    private static Comparator<Map.Entry<Integer, Integer>> getComparator(Order order) {
	        return switch (order) {
	            case KEY_ASC -> Map.Entry.comparingByKey();
	            case KEY_DESC -> Map.Entry.comparingByKey(Comparator.reverseOrder());
	            case VALUE_ASC -> Map.Entry.comparingByValue();
	            case VALUE_DESC -> Map.Entry.comparingByValue(Comparator.reverseOrder());
	            case INSERTION -> (a, b) -> 0; // no ordering
	        };
	    }
	    
	    public static void mapToArrayAdvanced6() {
		    System.out.println("Insertion order: " + 
		        Arrays.toString(Solution.toInterleavedArray(mutableMap, Order.INSERTION)));
		    // [3, 300, 1, 100, 2, 200]
		    
		    System.out.println("Key ascending: " + 
		        Arrays.toString(Solution.toInterleavedArray(mutableMap, Order.KEY_ASC)));
		    // [1, 100, 2, 200, 3, 300]
		    
		    System.out.println("Value descending: " + 
		        Arrays.toString(Solution.toInterleavedArray(mutableMap, Order.VALUE_DESC)));
		    // [3, 300, 2, 200, 1, 100]
		    
		    // Con filtro
		    System.out.println("Only values > 150: " + 
		        Arrays.toString(Solution.toInterleavedArray(mutableMap, Order.KEY_ASC, 
		            e -> e.getValue() > 150)));
		    // [2, 200, 3, 300]
	    }
	    
	    // Performance ottimizzata con pre-allocazione:
	    	public static void mapToArrayAdvanced6(Map<Integer, Integer> map) {
	    	    int size = map.size();
	    	    int[] result = new int[size * 2];
	    	    
	    	    // Pre-alloca iterator per evitare boxing
	    	    if (map instanceof TreeMap || map instanceof LinkedHashMap) {
	    	        // Ordine garantito, usa iterator diretto
	    	        Iterator<Map.Entry<Integer, Integer>> it = map.entrySet().iterator();
	    	        for (int i = 0; it.hasNext(); i += 2) {
	    	            Map.Entry<Integer, Integer> entry = it.next();
	    	            result[i] = entry.getKey();
	    	            result[i + 1] = entry.getValue();
	    	        }
	    	    } else {
	    	        // HashMap, no garanzie ordine
	    	        int idx = 0;
	    	        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
	    	            result[idx++] = entry.getKey();
	    	            result[idx++] = entry.getValue();
	    	        }
	    	    }
	    	    System.out.println(Arrays.toString(result));
	    	}
	    	
	    	//parallel stream
	    	public static void testParallel() {
		        final int MAP_SIZE = 1000;
		        //mutableMap = null;
		        mutableMap = IntStream.range(0, MAP_SIZE)
		            .boxed()
		            .collect(Collectors.toMap(Function.identity(), Function.identity()));
	
		        System.out.println("--- Esempio 1: Contatore NON Sicuro (int[]) con Stream Parallelo ---");
		        runNonThreadSafeExample(mutableMap);
	
		        System.out.println("\n--- Esempio 2: Contatore SICURO (AtomicInteger) con Stream Parallelo ---");
		        runThreadSafeExample(mutableMap);
	    	}
	    	
	        public static void runNonThreadSafeExample(Map<Integer, Integer> map) {
	            final int[] idx = {0}; // Il nostro contatore non thread-safe
	            final int arraySize = map.size() * 2;

	            int[] keyValuePairs = map.entrySet().stream()
	                .parallel() // ATTIVIAMO IL PARALLELISMO!
	                .collect(
	                    () -> new int[arraySize],
	                    (array, entry) -> {
	                        // RACE CONDITION QUI!
	                        // Più thread possono leggere lo stesso valore di idx[0],
	                        // scrivere nello stesso punto dell'array e poi
	                        // incrementare il contatore, perdendo alcuni aggiornamenti.
	                        int i = idx[0];
	                        array[i] = entry.getKey();
	                        array[i + 1] = entry.getValue();
	                        idx[0] += 2; // Operazione NON atomica (leggi, somma, scrivi)
	                    },
	                    (arr1, arr2) -> {} // Un combiner non è sufficiente a salvare la situazione
	                );

	            // Verifichiamo il risultato
	            int errori = 0;
	            int valoriMancanti = 0;
	            for (int i = 0; i < arraySize; i += 2) {
	                // Se troviamo una coppia 0,0, significa che una scrittura è stata sovrascritta
	                // o non è mai avvenuta in quella posizione.
	                if (keyValuePairs[i] == 0 && keyValuePairs[i + 1] == 0 && map.get(0) == 0) {
	                     // Escludiamo il caso legittimo della coppia (0,0) che deve essere presente una volta
	                     if (i != 0) errori++;
	                }
	            }
	            
	            // Un controllo ancora più semplice è vedere se il contatore finale è corretto
	            if (idx[0] != arraySize) {
	                System.out.println("ERRORE GRAVE: L'indice finale è " + idx[0] + ", ma dovrebbe essere " + arraySize);
	                System.out.println("Questo significa che molte operazioni di incremento sono andate perse!");
	            } else {
	                 System.out.println("L'indice finale è corretto, ma potrebbero esserci errori di sovrascrittura.");
	            }
	            
	            if (errori > 0) {
	                System.out.println("Trovati " + errori + " 'buchi' (coppie 0,0) nell'array. La concorrenza ha causato sovrascritture!");
	            } else {
	                System.out.println("Nessun 'buco' evidente trovato, ma non garantisce la correttezza.");
	            }
	        }
	        
	        public static void runThreadSafeExample(Map<Integer, Integer> map) {
	            AtomicInteger idx = new AtomicInteger(0); // Il nostro contatore thread-safe
	            final int arraySize = map.size() * 2;

	            int[] keyValuePairs = map.entrySet().stream()
	                .parallel() // ANCORA PARALLELO
	                .collect(
	                    () -> new int[arraySize],
	                    (array, entry) -> {
	                        // NESSUNA RACE CONDITION QUI!
	                        // getAndAdd(2) è un'operazione atomica. Garantisce che
	                        // ogni thread ottenga un valore unico per 'i' prima
	                        // che qualsiasi altro thread possa fare lo stesso.
	                        int i = idx.getAndAdd(2);
	                        array[i] = entry.getKey();
	                        array[i + 1] = entry.getValue();
	                    },
	                    (arr1, arr2) -> {}
	                );

	            // Verifichiamo il risultato
	            int errori = 0;
	            // Creiamo un set di controllo per vedere se tutti i numeri da 0 a 999 sono presenti
	            boolean[] numeriTrovati = new boolean[map.size()];
	            for (int i = 0; i < arraySize; i += 2) {
	                int chiave = keyValuePairs[i];
	                int valore = keyValuePairs[i + 1];
	                if (chiave != valore) {
	                    errori++;
	                }
	                if (chiave >= 0 && chiave < map.size()) {
	                    numeriTrovati[chiave] = true;
	                }
	            }
	            
	            long conteggioNumeriMancanti = IntStream.range(0, map.size()).filter(i -> !numeriTrovati[i]).count();

	            System.out.println("L'indice finale (valore interno di AtomicInteger) è: " + idx.get());
	            if (idx.get() == arraySize) {
	                System.out.println("CORRETTO: L'indice finale è " + arraySize + ", come atteso.");
	            } else {
	                System.out.println("ERRORE: L'indice finale è " + idx.get());
	            }
	            
	            if (conteggioNumeriMancanti == 0 && errori == 0) {
	                System.out.println("CORRETTO: L'array finale è valido. Tutte le coppie chiave/valore sono presenti e corrette.");
	            } else {
	                System.out.println("ERRORE: Trovati " + conteggioNumeriMancanti + " numeri mancanti e " + errori + " coppie non valide.");
	            }
	        }
	    	
}
