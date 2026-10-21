package ordenacao.serial;

import java.util.Arrays;

import ordenacao.Ordenador;

/** Merge Sort classico (dividir e conquistar). O(n log n) em todos os casos. */
public class MergeSortSerial implements Ordenador {

    @Override public String nome() { return "MergeSort"; }
    @Override public String versao() { return "serial"; }

    @Override
    public void ordenar(int[] a) {
        if (a.length > 1) mergeSort(a, 0, a.length - 1);
    }

    static void mergeSort(int[] a, int p, int r) {
        if (p < r) {
            int q = (p + r) / 2;
            mergeSort(a, p, q);
            mergeSort(a, q + 1, r);
            merge(a, p, q, r);
        }
    }

    /** Intercala a[p..q] e a[q+1..r]. Publico para ser reutilizado na versao paralela. */
    public static void merge(int[] a, int p, int q, int r) {
        int[] left = Arrays.copyOfRange(a, p, q + 1);
        int[] right = Arrays.copyOfRange(a, q + 1, r + 1);

        int i = 0, j = 0, k = p;
        while (i < left.length && j < right.length) {
            a[k++] = left[i] <= right[j] ? left[i++] : right[j++];
        }
        while (i < left.length) a[k++] = left[i++];
        while (j < right.length) a[k++] = right[j++];
    }
}
