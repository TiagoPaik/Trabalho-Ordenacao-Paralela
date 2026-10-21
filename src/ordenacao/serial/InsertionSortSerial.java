package ordenacao.serial;

import ordenacao.Ordenador;

/** Insertion Sort. O(n^2) no pior caso, O(n) se o vetor ja estiver ordenado. */
public class InsertionSortSerial implements Ordenador {

    @Override public String nome() { return "InsertionSort"; }
    @Override public String versao() { return "serial"; }

    @Override
    public void ordenar(int[] a) {
        for (int j = 1; j < a.length; j++) {
            int key = a[j];
            int i = j - 1;
            while (i >= 0 && a[i] > key) {
                a[i + 1] = a[i];
                i--;
            }
            a[i + 1] = key;
        }
    }
}
