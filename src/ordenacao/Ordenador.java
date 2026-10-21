package ordenacao;

/**
 * Contrato comum a todos os algoritmos (seriais e, futuramente, paralelos).
 * O framework de teste so conhece esta interface, entao basta criar uma
 * nova implementacao para que ela entre automaticamente nas medicoes.
 */
public interface Ordenador {

    /** Nome do algoritmo, ex.: "MergeSort". Usado no CSV. */
    String nome();

    /** "serial" ou "paralelo". Usado no CSV. */
    String versao();

    /** Quantidade de threads usada (1 nas versoes seriais). */
    default int threads() {
        return 1;
    }

    /** Ordena o vetor em ordem crescente (in-place). */
    void ordenar(int[] a);
}
