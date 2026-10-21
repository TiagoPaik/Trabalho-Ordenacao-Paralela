package ordenacao.serial;

import ordenacao.Ordenador;

/**
 * Counting Sort com offset (aceita valores negativos). O(n + k), onde
 * k = max - min. O resultado e copiado de volta para o vetor original
 * para manter o mesmo contrato (in-place) dos demais algoritmos.
 */
public class CountingSortSerial implements Ordenador {

    @Override public String nome() { return "CountingSort"; }
    @Override public String versao() { return "serial"; }

    @Override
    public void ordenar(int[] a) {
        if (a.length == 0) return;
        int min = a[0], max = a[0];
        for (int x : a) {
            if (x < min) min = x;
            if (x > max) max = x;
        }
        int shift = -min;
        int k = max - min;
        int[] c = new int[k + 1];
        int[] b = new int[a.length];

        for (int x : a) c[x + shift]++;
        for (int i = 1; i <= k; i++) c[i] += c[i - 1];
        // percorre de tras para frente para garantir estabilidade
        for (int j = a.length - 1; j >= 0; j--) {
            int key = a[j] + shift;
            b[c[key] - 1] = a[j];
            c[key]--;
        }
        System.arraycopy(b, 0, a, 0, a.length);
    }
}
