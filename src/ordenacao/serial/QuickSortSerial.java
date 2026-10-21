package ordenacao.serial;

import java.util.concurrent.ThreadLocalRandom;

import ordenacao.Ordenador;

/**
 * Quick Sort aleatorizado (particao de Lomuto, pivo sorteado).
 * O pivo aleatorio evita o pior caso O(n^2) e o estouro de pilha
 * em vetores ja ordenados ou em ordem reversa.
 */
public class QuickSortSerial implements Ordenador {

    @Override public String nome() { return "QuickSort"; }
    @Override public String versao() { return "serial"; }

    @Override
    public void ordenar(int[] a) {
        if (a.length > 1) quickSort(a, 0, a.length - 1);
    }

    static void quickSort(int[] a, int p, int r) {
        if (p < r) {
            int q = randomizedPartition(a, p, r);
            quickSort(a, p, q - 1);
            quickSort(a, q + 1, r);
        }
    }

    public static int randomizedPartition(int[] a, int p, int r) {
        swap(a, r, p + ThreadLocalRandom.current().nextInt(r - p + 1));
        return partition(a, p, r);
    }

    // Esquema de Lomuto, pivo = a[r]
    static int partition(int[] a, int p, int r) {
        int x = a[r];
        int i = p - 1;
        for (int j = p; j <= r - 1; j++) {
            if (a[j] <= x) {
                i++;
                swap(a, i, j);
            }
        }
        swap(a, i + 1, r);
        return i + 1;
    }

    static void swap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }
}
