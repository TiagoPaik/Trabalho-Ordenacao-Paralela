package ordenacao.serial;

import ordenacao.Ordenador;

/** Selection Sort. Sempre O(n^2) comparacoes, independente da entrada. */
public class SelectionSortSerial implements Ordenador {

    @Override public String nome() { return "SelectionSort"; }
    @Override public String versao() { return "serial"; }

    @Override
    public void ordenar(int[] a) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            int min = i;
            for (int j = i + 1; j < n; j++) {
                if (a[j] < a[min]) min = j;
            }
            int temp = a[min];
            a[min] = a[i];
            a[i] = temp;
        }
    }
}
