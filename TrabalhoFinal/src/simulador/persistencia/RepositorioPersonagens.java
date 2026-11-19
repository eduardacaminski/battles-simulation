package simulador.persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import simulador.excecoes.AcaoInvalidaException;
import simulador.modelo.FabricaPersonagens;
import simulador.modelo.Personagem;


public class RepositorioPersonagens {

    private static final String SEPARADOR = ";";
    private static final String CABECALHO = "# tipo;nome;vidaAtual";

    private final File arquivo;
    private final List<String> avisos = new ArrayList<>();

    public RepositorioPersonagens(String caminho) {
        this.arquivo = new File(caminho);
    }

    public void salvar(List<Personagem> personagens) throws IOException {
        File pasta = arquivo.getParentFile();
        if (pasta != null && !pasta.exists()) {
            pasta.mkdirs();
        }
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(arquivo, StandardCharsets.UTF_8))) {
            escritor.write(CABECALHO);
            escritor.newLine();
            for (Personagem p : personagens) {
                escritor.write(p.getTipo() + SEPARADOR + p.getNome() + SEPARADOR + p.getVidaAtual());
                escritor.newLine();
            }
        }
    }
    public List<Personagem> carregar() throws IOException {
        List<Personagem> personagens = new ArrayList<>();
        avisos.clear();
        if (!arquivo.exists()) {
            return personagens;
        }
        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivo, StandardCharsets.UTF_8))) {
            String linha;
            int numeroLinha = 0;
            while ((linha = leitor.readLine()) != null) {
                numeroLinha++;
                if (linha.isBlank() || linha.startsWith("#")) {
                    continue;
                }
                try {
                    personagens.add(converterLinha(linha));
                } catch (IllegalArgumentException | AcaoInvalidaException e) {
                    avisos.add("Linha " + numeroLinha + " de " + arquivo.getName() + " ignorada: " + e.getMessage());
                }
            }
        }
        return personagens;
    }

    private Personagem converterLinha(String linha) throws AcaoInvalidaException {
        String[] partes = linha.split(SEPARADOR);
        if (partes.length != 3) {
            throw new IllegalArgumentException("formato esperado tipo;nome;vidaAtual");
        }
        int vidaAtual = converterNumero(partes[2], "vidaAtual");
        return FabricaPersonagens.criar(partes[0], partes[1], vidaAtual);
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