package simulador.persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import simulador.modelo.RegistroBatalha;


public class RepositorioHistorico {

    private static final String SEPARADOR = ";";

    private final File arquivo;
    private final List<String> avisos = new ArrayList<>();

    public RepositorioHistorico(String caminho) {
        this.arquivo = new File(caminho);
    }

    public void registrar(RegistroBatalha registro) throws IOException {
        File pasta = arquivo.getParentFile();
        if (pasta != null && !pasta.exists()) {
            pasta.mkdirs();
        }
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(arquivo, StandardCharsets.UTF_8, true))) {
            escritor.write(converterParaLinha(registro));
            escritor.newLine();
        }
    }

    public List<RegistroBatalha> carregar() throws IOException {
        List<RegistroBatalha> registros = new ArrayList<>();
        avisos.clear();
        if (!arquivo.exists()) {
            return registros;
        }
        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivo, StandardCharsets.UTF_8))) {
            String linha;
            int numeroLinha = 0;
            while ((linha = leitor.readLine()) != null) {
                numeroLinha++;
                if (linha.isBlank()) {
                    continue;
                }
                try {
                    registros.add(converterLinha(linha));
                } catch (IllegalArgumentException e) {
                    avisos.add("Linha " + numeroLinha + " de " + arquivo.getName() + " ignorada: " + e.getMessage());
                }
            }
        }
        return registros;
    }

    private String converterParaLinha(RegistroBatalha r) {
        return String.join(SEPARADOR,
                r.getDataHora().format(RegistroBatalha.FORMATO_DATA),
                r.getEquipeJogador(),
                r.getEquipeInimigos(),
                r.getResultado(),
                String.valueOf(r.getInimigosDerrotados()),
                String.valueOf(r.getRodadas()));
    }

    private RegistroBatalha converterLinha(String linha) {
        String[] partes = linha.split(SEPARADOR);
        if (partes.length != 6) {
            throw new IllegalArgumentException("esperados 6 campos separados por ';'");
        }
        LocalDateTime dataHora;
        try {
            dataHora = LocalDateTime.parse(partes[0], RegistroBatalha.FORMATO_DATA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("data inválida: " + partes[0]);
        }
        int inimigosDerrotados = converterNumero(partes[4], "inimigosDerrotados");
        int rodadas = converterNumero(partes[5], "rodadas");
        return new RegistroBatalha(dataHora, partes[1], partes[2], partes[3], inimigosDerrotados, rodadas);
    }

    private int converterNumero(String texto, String campo) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("o campo " + campo + " não é um número: " + texto);
        }
    }

    public List<String> getAvisos() {
        return new ArrayList<>(avisos);
    }
}
