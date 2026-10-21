package benchmark;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import benchmark.GeradorDados.Tipo;
import ordenacao.Ordenador;
import ordenacao.serial.BubbleSortSerial;
import ordenacao.serial.CountingSortSerial;
import ordenacao.serial.InsertionSortSerial;
import ordenacao.serial.MergeSortSerial;
import ordenacao.serial.QuickSortSerial;
import ordenacao.serial.SelectionSortSerial;

/**
 * Framework de teste: executa cada algoritmo para cada tamanho e tipo de
 * entrada, repete N amostras, confere se o resultado esta ordenado e grava:
 *
 *   resultados/resultados_brutos.csv  -> uma linha por execucao
 *   resultados/resultados_resumo.csv  -> media, desvio padrao, min e max
 *
 * Uso:
 *   java -cp out benchmark.Benchmark            (configuracao completa)
 *   java -cp out benchmark.Benchmark --rapido   (tamanhos pequenos, p/ testar)
 */
public class Benchmark {

    static final int AMOSTRAS = 5;

    // Tamanhos testados para algoritmos O(n log n) / O(n + k)
    static final int[] TAMANHOS = {1_000, 5_000, 10_000, 50_000, 100_000, 500_000, 1_000_000};
    // Algoritmos O(n^2) ficam muito lentos acima disso
    static final int LIMITE_QUADRATICOS = 50_000;

    static final int[] TAMANHOS_RAPIDO = {1_000, 5_000, 10_000, 50_000};
    static final int LIMITE_QUADRATICOS_RAPIDO = 10_000;

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US); // ponto decimal no CSV
        boolean rapido = Arrays.asList(args).contains("--rapido");
        int[] tamanhos = rapido ? TAMANHOS_RAPIDO : TAMANHOS;
        int limiteQuad = rapido ? LIMITE_QUADRATICOS_RAPIDO : LIMITE_QUADRATICOS;

        List<Ordenador> algoritmos = List.of(
                new BubbleSortSerial(),
                new SelectionSortSerial(),
                new InsertionSortSerial(),
                new MergeSortSerial(),
                new QuickSortSerial(),
                new CountingSortSerial()
                // As versoes paralelas entram aqui depois, ex.:
                // new MergeSortParalelo(2), new MergeSortParalelo(4), ...
        );

        int nucleos = Runtime.getRuntime().availableProcessors();
        System.out.printf("Nucleos disponiveis: %d | Amostras por configuracao: %d%n%n", nucleos, AMOSTRAS);

        aquecerJvm(algoritmos);

        Path pasta = Path.of("resultados");
        Files.createDirectories(pasta);

        List<Medicao> medicoes = new ArrayList<>();
        try (PrintWriter bruto = new PrintWriter(
                Files.newBufferedWriter(pasta.resolve("resultados_brutos.csv"), StandardCharsets.UTF_8))) {

            bruto.println("algoritmo,versao,threads,nucleos_disponiveis,tipo_entrada,tamanho,amostra,tempo_ms");

            for (Ordenador alg : algoritmos) {
                boolean quadratico = ehQuadratico(alg);
                for (Tipo tipo : Tipo.values()) {
                    for (int n : tamanhos) {
                        if (quadratico && n > limiteQuad) continue;

                        double[] tempos = new double[AMOSTRAS];
                        for (int s = 1; s <= AMOSTRAS; s++) {
                            int[] dados = GeradorDados.gerar(tipo, n, s);

                            long ini = System.nanoTime();
                            alg.ordenar(dados);
                            long fim = System.nanoTime();

                            if (!GeradorDados.estaOrdenado(dados)) {
                                throw new IllegalStateException(alg.nome() + " nao ordenou corretamente: "
                                        + tipo + " n=" + n);
                            }

                            double ms = (fim - ini) / 1_000_000.0;
                            tempos[s - 1] = ms;
                            bruto.printf("%s,%s,%d,%d,%s,%d,%d,%.4f%n",
                                    alg.nome(), alg.versao(), alg.threads(), nucleos, tipo, n, s, ms);
                        }
                        bruto.flush();

                        Medicao m = new Medicao(alg, tipo, n, tempos);
                        medicoes.add(m);
                        System.out.printf("%-14s %-8s t=%-2d %-15s n=%-9d media=%10.3f ms  dp=%8.3f%n",
                                alg.nome(), alg.versao(), alg.threads(), tipo, n, m.media(), m.desvio());
                    }
                }
            }
        }

        gravarResumo(pasta.resolve("resultados_resumo.csv"), medicoes, nucleos);
        System.out.println("\nCSV gerados em: " + pasta.toAbsolutePath());
    }

    /** Executa cada algoritmo algumas vezes antes de medir, para o JIT compilar o codigo. */
    static void aquecerJvm(List<Ordenador> algoritmos) {
        System.out.print("Aquecendo a JVM...");
        for (Ordenador alg : algoritmos) {
            int n = ehQuadratico(alg) ? 2_000 : 50_000;
            for (int i = 0; i < 10; i++) {
                alg.ordenar(GeradorDados.gerar(Tipo.ALEATORIO, n, 1000 + i));
            }
        }
        System.out.println(" ok\n");
    }

    static boolean ehQuadratico(Ordenador alg) {
        return switch (alg.nome()) {
            case "BubbleSort", "SelectionSort", "InsertionSort" -> true;
            default -> false;
        };
    }

    static void gravarResumo(Path arquivo, List<Medicao> medicoes, int nucleos) throws IOException {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(arquivo, StandardCharsets.UTF_8))) {
            out.println("algoritmo,versao,threads,nucleos_disponiveis,tipo_entrada,tamanho,amostras,"
                    + "media_ms,desvio_padrao_ms,min_ms,max_ms");
            for (Medicao m : medicoes) {
                out.printf("%s,%s,%d,%d,%s,%d,%d,%.4f,%.4f,%.4f,%.4f%n",
                        m.alg.nome(), m.alg.versao(), m.alg.threads(), nucleos, m.tipo, m.n,
                        m.tempos.length, m.media(), m.desvio(), m.min(), m.max());
            }
        }
    }

    record Medicao(Ordenador alg, Tipo tipo, int n, double[] tempos) {
        double media() {
            return Arrays.stream(tempos).average().orElse(0);
        }
        double desvio() { // desvio padrao amostral
            if (tempos.length < 2) return 0;
            double med = media(), soma = 0;
            for (double t : tempos) soma += (t - med) * (t - med);
            return Math.sqrt(soma / (tempos.length - 1));
        }
        double min() { return Arrays.stream(tempos).min().orElse(0); }
        double max() { return Arrays.stream(tempos).max().orElse(0); }
    }
}
