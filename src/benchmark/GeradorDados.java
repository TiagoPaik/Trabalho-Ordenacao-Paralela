package benchmark;

import java.util.Random;

/**
 * Gera os conjuntos de entrada. A semente e fixa (depende do tamanho, do tipo
 * e do numero da amostra), entao a versao serial e a paralela recebem
 * exatamente os mesmos vetores, o que torna a comparacao justa.
 */
public final class GeradorDados {

    public enum Tipo {
        ALEATORIO,       // valores aleatorios em [0, n)
        ORDENADO,        // ja em ordem crescente (melhor caso de Bubble/Insertion)
        REVERSO,         // ordem decrescente (pior caso de Bubble/Insertion)
        QUASE_ORDENADO   // ordenado com ~5% das posicoes trocadas
    }

    private GeradorDados() {}

    public static int[] gerar(Tipo tipo, int n, int amostra) {
        long semente = 42L * 1_000_003L + n * 31L + tipo.ordinal() * 7L + amostra;
        Random rng = new Random(semente);
        int[] a = new int[n];

        switch (tipo) {
            case ALEATORIO -> {
                for (int i = 0; i < n; i++) a[i] = rng.nextInt(Math.max(n, 1));
            }
            case ORDENADO -> {
                for (int i = 0; i < n; i++) a[i] = i;
            }
            case REVERSO -> {
                for (int i = 0; i < n; i++) a[i] = n - i;
            }
            case QUASE_ORDENADO -> {
                for (int i = 0; i < n; i++) a[i] = i;
                int trocas = Math.max(1, n / 20);
                for (int t = 0; t < trocas && n > 1; t++) {
                    int i = rng.nextInt(n), j = rng.nextInt(n);
                    int tmp = a[i]; a[i] = a[j]; a[j] = tmp;
                }
            }
        }
        return a;
    }

    public static boolean estaOrdenado(int[] a) {
        for (int i = 1; i < a.length; i++) {
            if (a[i - 1] > a[i]) return false;
        }
        return true;
    }
}
