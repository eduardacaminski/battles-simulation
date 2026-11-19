package simuladorr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import simulador.controle.GerenciadorPersonagens;
import simulador.modelo.Personagem;
import simulador.persistencia.RepositorioHistorico;
import simulador.persistencia.RepositorioPersonagens;
import simulador.visao.Menu;


public class Main {

    private static final String ARQUIVO_PERSONAGENS = "dados/personagens.txt";
    private static final String ARQUIVO_HISTORICO = "dados/historico.txt";

    public static void main(String[] args) {
        RepositorioPersonagens repositorioPersonagens = new RepositorioPersonagens(ARQUIVO_PERSONAGENS);
        RepositorioHistorico repositorioHistorico = new RepositorioHistorico(ARQUIVO_HISTORICO);

        List<Personagem> carregados = new ArrayList<>();
        try {
            carregados = repositorioPersonagens.carregar();
        } catch (IOException e) {
            System.out.println("Não foi possível ler " + ARQUIVO_PERSONAGENS + ": " + e.getMessage());
        }
        for (String aviso : repositorioPersonagens.getAvisos()) {
            System.out.println("Aviso: " + aviso);
        }

        GerenciadorPersonagens gerenciador = new GerenciadorPersonagens(carregados);
        try (Scanner entrada = new Scanner(System.in)) {
            Menu menu = new Menu(entrada, gerenciador, repositorioPersonagens, repositorioHistorico);
            menu.iniciar();
        }
    }
}