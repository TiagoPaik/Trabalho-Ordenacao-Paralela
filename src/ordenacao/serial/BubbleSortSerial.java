package ordenacao.serial;

import ordenacao.Ordenador;

/** Bubble Sort com parada antecipada. O(n^2) no pior caso, O(n) se ja ordenado. */
public class BubbleSortSerial implements Ordenador {

    @Override public String nome() { return "BubbleSort"; }
    @Override public String versao() { return "serial"; }

    @Override
    public void ordenar(int[] a) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            boolean trocou = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (a[j] > a[j + 1]) {
                    int temp = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = temp;
                    trocou = true;
                }
            }
            if (!trocou) break; // ja esta ordenado
        }
    }
}
