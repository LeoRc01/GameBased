
# Guida allo sviluppo

  

Qui vengono spiegate tutte le possibile guide, potete proporne e possiamo aggiungerne altre senza alcun tipo di problema.

Per evitare confusione si metterà l'iniziale del nickname per sapere chi ha aggiunto cosa, in modo tale che se qualcuno non sappia abbia qualche dubbio o domanda possa chiedere.

 
 
Divisioni per macro argomenti, in ogni sezioni ci sarà un solo argomento, evitando sezioni troppo grandi

- Zuppa: Z
- Fede : F
- Leo: L
- Roxas: R

## git best practices 
- **git atomici**: questo ci permette di fare una code review migliore e capire meglio i cambiamenti solo dai git (Z)
- **ogni feature ha il suo branch**: questo ci aiuta per lavorare, in quanto sappiamo che in un determinato branch si lavorerà su quel task, bisogna ricordarsi di creare un nuovo branch per ogni feature, anche se sembra inutile, in alcuni casi può essere molto utile, meglio farlo sempre per evitare di averne bisogno quando non c'è più la possibilità di farlo (Z)
- **fare piccole modifiche ogni commit**, tranne in casi particolari, tipo fix che coinvolgano l'intero progetto, in quel caso forse è meglio creare un branch solo per il fix (Z)
## Log best practices 
- Il tag su kotlin non bisogna metterlo a caso, il tag sarà il nome della classe che sta scrivendo il log (Z)
- si loggano tutti gli errori, tutte le volte che viene autorizzato o meno qualcosa, lo si logga.
si logga anche quando si chiama un servizio esterno alla classe, tipo quando si chiama il repository. Anche quando si esegue del codice "critico"  (Z)

## Comment best practices
- commentare in caso di qualche operazione non auto-esplicativa (Z)
- in caso di spiegazione troppo lunga, riportare in questo file una breve spiegazione con magari link che spieghi qualcosa a riguardo (Z)
- Evitare commenti inutili o troppo lunghi (Z)

## Coding best practices
- nomi variabili autoesplicativi, anche se sono lunghi vanno bene, tanto abbiamo l'autocompiler (Z)
- mantenere il più possibile diviso il modello MVVM, ovvero il View model, non deve modificare direttamente a mano la VIEW, ma è la view che si deve occupare di questa modifica, il massimo che può fare il viewmodel è notificare in qualche modo la view. lo scopo del viewmodel è collegare i model con la nostra view (Z)
- cercare di rendere il più possibile atomiche le funzioni, ogni funziona fa una sola cosa (Z)
- cercare di rendere i flussi leggibili, senza bisogno di obbligare chi legge a capire precisamente cosa sta succedendo, questo è fattibile tramite dei nomi scelti bene, delle funzioni o dei commenti ausiliari in caso di operazioni complesse (Z)