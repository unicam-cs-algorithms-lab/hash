# Tabelle hash: liste di collisione e hashing doppio — ASDL

Questa esercitazione utilizza **Java 8, Maven e JUnit 5** per studiare due tecniche di risoluzione delle collisioni. Le due implementazioni sono indipendenti e consentono di confrontare vantaggi, limiti e invarianti di strutture hash diverse.

## Obiettivi formativi

1. Comprendere la distinzione tra codice hash, indice di bucket e uguaglianza degli oggetti (`hashCode()` e `equals()`).
2. Gestire collisioni mediante liste concatenate (*separate chaining*) oppure scansioni all'interno dell'array (*open addressing*).
3. Applicare i contratti di `Set<E>`: assenza di duplicati, valori booleani restituiti dalle operazioni, rifiuto di `null` in questa implementazione.
4. Mantenere coerenti dimensione logica, capacità, fattore di carico e collegamenti delle catene; eseguire correttamente il *rehashing*.
5. Realizzare un iteratore che attraversi un array di liste e rilevi le modifiche strutturali (*fail-fast*).
6. Comprendere perché nelle tabelle a indirizzamento aperto occorrono celle `DELETED` e un passo di scansione coprimo con la capacità.
7. Leggere Javadoc, commenti e test come parte della specifica prima di implementare i TODO.

## Prima di iniziare: hashing e contratti

Due oggetti uguali secondo `equals()` **devono** avere lo stesso `hashCode()`; l'inverso non è richiesto. Una collisione tra elementi diversi è normale. La funzione hash primaria trasforma il codice hash in un indice valido dell'array. Non confondere `size` (elementi presenti) con `capacity` (numero di bucket/celle).

Le prestazioni medie attese sono vicine a `O(1)` quando la distribuzione delle chiavi e il fattore di carico sono favorevoli: **non si tratta di una garanzia nel caso peggiore**.

## Parte A — `CollisionListResizableHashTable<E>`

È una realizzazione di `java.util.Set<E>` mediante un array di **bucket**, ciascuno dei quali punta a una lista semplicemente concatenata di nodi. La capacità iniziale è `16`, la soglia di ridimensionamento è `floor(0.75 * capacity)` e la capacità raddoppia quando, a seguito di un nuovo inserimento, `size` supera la soglia. Il rehashing deve ricalcolare il bucket di **tutti** gli elementi con la nuova capacità, senza alterare `size`.

**TODO da implementare:**

- `contains(Object)`: calcolare l'indice del bucket e cercare nella relativa catena usando `equals`.
- `add(E)`: rifiutare `null`, evitare duplicati, inserire il nuovo nodo, aggiornare `size` e `modCount`, e verificare la soglia di resize.
- `resize()`: creare un array di capacità doppia e reinserire/ricollegare tutti gli elementi nei nuovi bucket, senza modificare il numero logico di elementi.
- `remove(Object)`: scollegare correttamente il nodo cercato, anche se è la testa o l'unico elemento della catena; aggiornare i contatori soltanto se la rimozione riesce.
- `Itr.hasNext()` e `Itr.next()` e lo stato necessario dell'iteratore: attraversare una catena e passare al bucket non vuoto successivo, senza saltare o duplicare elementi. Gestire `NoSuchElementException` a fine iterazione e `ConcurrentModificationException` dopo una modifica strutturale.

**Già implementati e da studiare:** `containsAll`, `addAll`, `removeAll`, `clear`, i metodi di accesso e la struttura dei nodi. Nelle operazioni bulk occorre scorrere **tutti** gli elementi anche quando il risultato booleano è già diventato `true`; per questo viene utilizzato `changed = changed | operazione(...)` invece di affidarsi a un OR che interrompa le valutazioni.

L'ordine di attraversamento del `Set` **non è garantito**. Le operazioni `toArray`, `toArray(T[])` e `retainAll` non fanno parte degli assignment e in questo progetto lanciano `UnsupportedOperationException`.

### Invarianti da controllare

- Ogni elemento compare una sola volta ed è nella catena del bucket calcolato con la capacità corrente.
- `size` conta gli elementi, non le catene; i riferimenti `next` terminano in `null`.
- Una modifica strutturale incrementa `modCount`; l'inserimento di un duplicato e la ricerca non lo incrementano.
- L'iteratore è valido finché non cambia strutturalmente il `Set` e deve funzionare anche con bucket vuoti e catene lunghe.

## Parte B — `MyDoubleHashTable`

È una tabella di elementi distinti **a capacità fissa** `m`, senza liste e senza resize. Per un elemento `e` si usa `k = e.hashCode()`. La posizione esaminata al tentativo `i`, con `0 <= i < m`, è:

```text
h(k,i) = ( h1(k) + i * h2(k) ) mod m
h1(k)  = floorMod(k,m)
h2 candidato(k) = 1 + floorMod(k,m-1)   (m > 1)
```

La funzione privata **`validStep` è già implementata**: a partire dal passo candidato sceglie, se necessario, un passo coprimo con `m`, affinché tutte le celle possano essere visitate prima di ripetere la sequenza. Per `m=1` il passo è 1. Si usi il metodo fornito, senza cambiarlo.

**Esempio con capacità composta.** Con `m=10` e `k=1`, la posizione iniziale è 1 e il passo candidato è 2. La scansione `1,3,5,7,9,1,...` raggiunge soltanto cinque celle, perché `MCD(2,10)=2`. `validStep` seleziona il passo successivo coprimo, 3: `1,4,7,0,3,6,9,2,5,8`. In questo modo un inserimento non fallisce prematuramente soltanto perché la scansione non raggiunge una cella libera.

Le celle hanno tre stati: **`null`** (mai occupata: una ricerca può terminare), **`DELETED`** (cancellata: la scansione deve proseguire), **occupata** (confrontare con `equals`). Una cancellazione **non** può sostituire il valore con `null`, perché potrebbe rendere irraggiungibili elementi inseriti dopo una collisione.

**TODO da implementare:**

- `contains(Object)`: visitare al massimo `m` posizioni, ignorare `DELETED` e interrompere la ricerca su `null` o quando è stata esaminata tutta la sequenza.
- `insert(Object)`: evitare duplicati con `equals`; ricordare la **prima** cella `DELETED` ma continuare la scansione per cercare eventuali duplicati; riutilizzare la cella disponibile, aumentare `size` solo se l'inserimento riesce; se non c'è spazio, lanciare `FullHashTableException`.
- `delete(Object)`: cercare con la stessa sequenza di scansione e sostituire l'elemento individuato con la sentinella `DELETED`, diminuendo `size` solo se presente.

Non sono ammessi elementi `null`. Tutti gli indici devono essere calcolati modulo `m` e i prodotti `i * step` vanno effettuati in `long` per evitare overflow di `int`.

## Ordine di lavoro consigliato

1. Aprire il progetto Maven in IntelliJ IDEA, identificare la versione del JDK configurata (Java 8) e leggere integralmente le Javadoc delle due classi.
2. Eseguire `mvn test`: nella versione studenti alcuni test devono fallire finché restano i TODO.
3. Completare **Parte A** nell'ordine: `contains`, `add`, `remove`, `resize`, iteratore. Dopo ogni metodo lanciare i test pertinenti.
4. Completare **Parte B** nell'ordine: `contains`, `insert`, `delete`. Ragionare a mano su `null`, `DELETED`, duplicati e tabella piena.
5. Eseguire l'intera suite `mvn test`; correggere anche i casi limite e verificare che la soluzione non dipenda dall'ordine di iterazione delle catene.
6. Se utile, generare la documentazione HTML con `mvn javadoc:javadoc` (in `target/reports/apidocs/index.html` con la configurazione standard).

## Compilazione e verifica

Dalla directory che contiene `pom.xml`:

```bash
mvn test
mvn -DskipTests compile
mvn javadoc:javadoc
```

I test JUnit sono **esempi eseguibili dei contratti** e includono collisioni intenzionali, codici hash negativi, duplicati, ridimensionamento, modifiche durante l'iterazione, celle `DELETED` e capacità composte. Non modificare i test per nascondere un difetto nei metodi richiesti.

## Consegna su Moodle

Consegnare **tutte le classi Java completate o modificate**, mantenendo nomi di classe, package e firme dei metodi forniti. Non consegnare `target/`, `.idea/`, `.class` o altri file generati. La data e l'orario sono quelli riportati nell'attività Moodle.

## Riferimenti per lo studio

Slide «Tabelle Hash» di M. R. Di Berardini ed E. Merelli: funzioni hash e collisioni (17–25), chaining e fattore di carico (26–36), indirizzamento aperto e cancellazione (37–42), tecniche di scansione e hashing doppio (54–69). La condizione `MCD(h2(k),m)=1` è trattata nelle slide finali.
