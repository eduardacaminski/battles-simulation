package simulador.visao;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import simulador.controle.Batalha;
import simulador.controle.GerenciadorPersonagens;
import simulador.excecoes.AcaoInvalidaException;
import simulador.modelo.Equipe;
import simulador.modelo.FabricaPersonagens;
import simulador.modelo.Inventario;
import simulador.modelo.Item;
import simulador.modelo.Personagem;
import simulador.modelo.RegistroBatalha;
import simulador.persistencia.RepositorioHistorico;
import simulador.persistencia.RepositorioPersonagens;


public class Menu {

    private static final int LIMITE_MAXIMO_EQUIPE = 4;

    private final Scanner entrada;
    private final GerenciadorPersonagens gerenciador;
    private final RepositorioPersonagens repositorioPersonagens;
    private final RepositorioHistorico repositorioHistorico;

    public Menu(Scanner entrada, GerenciadorPersonagens gerenciador,
            RepositorioPersonagens repositorioPersonagens, RepositorioHistorico repositorioHistorico) {
        this.entrada = entrada;
        this.gerenciador = gerenciador;
        this.repositorioPersonagens = repositorioPersonagens;
        this.repositorioHistorico = repositorioHistorico;
    }
    // ===================== Menu principal =====================

    public void iniciar() {
        System.out.println("=== SIMULADOR DE BATALHAS ===");
        System.out.println(gerenciador.listar().size() + " personagem(ns) carregado(s).");
        int opcao;
        do {
            exibirMenuPrincipal();
            opcao = lerInteiro("Escolha uma opção: ", 0, 6);
            switch (opcao) {
                case 1:
                    cadastrarPersonagem();
                    break;
                case 2:
                    listarPersonagens();
                    break;
                case 3:
                    consultarPersonagem();
                    break;
                case 4:
                    iniciarBatalha();
                    break;
                case 5:
                    exibirHistorico();
                    break;
                case 6:
                    descansar();
                    break;
                default:
                    salvarPersonagens();
                    System.out.println("Dados salvos. Até a próxima!");
            }
        } while (opcao != 0);
    }

    private void exibirMenuPrincipal() {
        System.out.println("\n----- MENU -----");
        System.out.println("1 - Cadastrar personagem");
        System.out.println("2 - Listar personagens");
        System.out.println("3 - Consultar personagem");
        System.out.println("4 - Iniciar batalha");
        System.out.println("5 - Ver histórico de batalhas");
        System.out.println("6 - Descansar (recuperar a vida de todos)");
        System.out.println("0 - Salvar e sair");
    }

    // ===================== Personagens =====================

    private void listarPersonagens() {
        System.out.println("\n--- Personagens cadastrados ---");
        exibirLista(gerenciador.listar());
    }
    private void cadastrarPersonagem() {
        System.out.println("\n--- Cadastro de personagem ---");
        for (int i = 0; i < FabricaPersonagens.TIPOS.length; i++) {
            System.out.println((i + 1) + " - " + FabricaPersonagens.TIPOS[i]);
        }
        int tipo = lerInteiro("Tipo: ", 1, FabricaPersonagens.TIPOS.length);
        String nome = lerTexto("Nome: ");
        try {
            Personagem novo = gerenciador.cadastrar(FabricaPersonagens.TIPOS[tipo - 1], nome);
            System.out.println("Cadastrado: " + novo);
            salvarPersonagens();
        } catch (AcaoInvalidaException e) {
            System.out.println("Não foi possível cadastrar: " + e.getMessage());
        }
    }

    private void consultarPersonagem() {
        List<Personagem> todos = gerenciador.listar();
        if (todos.isEmpty()) {
            System.out.println("Nenhum personagem cadastrado.");
            return;
        }
        exibirLista(todos);
        Personagem escolhido = todos.get(lerInteiro("Número do personagem: ", 1, todos.size()) - 1);
        System.out.println(escolhido);
        System.out.println("   Habilidade: " + escolhido.getNomeHabilidade() + " - " + escolhido.getDescricaoHabilidade());
    }

    private void descansar() {
        gerenciador.descansarTodos();
        System.out.println("Todos os personagens recuperaram a vida total.");
        salvarPersonagens();
    }
    // ===================== Batalha =====================

    private void iniciarBatalha() {
        List<Personagem> vivos = gerenciador.listarVivos();
        if (vivos.isEmpty()) {
            System.out.println("Nenhum personagem disponível. Cadastre um personagem ou use a opção Descansar.");
            return;
        }
        int limite = lerInteiro("Limite máximo de integrantes da equipe (1 a " + LIMITE_MAXIMO_EQUIPE + "): ",
                1, LIMITE_MAXIMO_EQUIPE);
        Equipe equipe = montarEquipe(vivos, limite);
        try {
            Equipe inimigos = Batalha.gerarInimigos(equipe.getQuantidade());
            Batalha batalha = new Batalha(equipe, inimigos, Inventario.criarPadrao());
            System.out.println("\nSua equipe: " + equipe.getNomesDosMembros());
            System.out.println("Inimigos:   " + inimigos.getNomesDosMembros());
            conduzirBatalha(batalha);
            finalizarBatalha(batalha);
        } catch (AcaoInvalidaException e) {
            System.out.println("Não foi possível iniciar a batalha: " + e.getMessage());
        }
    }
    private Equipe montarEquipe(List<Personagem> candidatos, int limite) {
        Equipe equipe = new Equipe("Sua equipe", limite);
        while (!equipe.estaCheia()) {
            System.out.println("\nEscolha os integrantes (" + equipe.getQuantidade() + "/" + limite + ") - 0 para terminar:");
            exibirLista(candidatos);
            int escolha = lerInteiro("Personagem: ", 0, candidatos.size());
            if (escolha == 0) {
                if (equipe.estaVazia()) {
                    System.out.println("A equipe precisa ter pelo menos 1 personagem.");
                    continue;
                }
                break;
            }
            Personagem escolhido = candidatos.get(escolha - 1);
            try {
                equipe.adicionar(escolhido);
                System.out.println(escolhido.getNome() + " entrou na equipe.");
            } catch (AcaoInvalidaException e) {
                System.out.println("Não foi possível adicionar: " + e.getMessage());
            }
        }
        return equipe;
    }
    private void conduzirBatalha(Batalha batalha) {
        while (!batalha.terminou()) {
            System.out.println("\n========== RODADA " + batalha.getRodada() + " ==========");
            exibirCombatentes(batalha);
            turnoDoJogador(batalha);
            if (!batalha.terminou()) {
                System.out.println("\n--- Turno dos inimigos ---");
                for (String acontecimento : batalha.executarTurnoDosInimigos()) {
                    System.out.println(acontecimento);
                }
            }
        }
    }

    private void turnoDoJogador(Batalha batalha) {
        while (!batalha.turnoDoJogadorAcabou()) {
            List<Personagem> podemAgir = batalha.getDisponiveisParaAgir();
            System.out.println("\nQuem vai agir?");
            exibirLista(podemAgir);
            Personagem escolhido = podemAgir.get(lerInteiro("Personagem: ", 1, podemAgir.size()) - 1);
            escolherAcao(batalha, escolhido);
        }
    }
    private void escolherAcao(Batalha batalha, Personagem personagem) {
        while (true) {
            System.out.println("\nAção de " + personagem.getNome() + ":");
            System.out.println("1 - Ataque normal");
            System.out.println("2 - " + descreverHabilidade(personagem));
            System.out.println("3 - Usar item");
            System.out.println("4 - Consultar atributos dos combatentes");
            System.out.println("0 - Escolher outro personagem");
            int acao = lerInteiro("Ação: ", 0, 4);
            try {
                switch (acao) {
                    case 0:
                        return;
                    case 1:
                        System.out.println(batalha.atacar(personagem, escolherAlvo(batalha.getEquipeInimigos())));
                        return;
                    case 2:
                        System.out.println(batalha.usarHabilidade(personagem, escolherAlvo(batalha.getEquipeInimigos())));
                        return;
                    case 3:
                        if (usarItem(batalha, personagem)) {
                            return;
                        }
                        break;
                    default:
                        exibirCombatentes(batalha);
                }
            } catch (AcaoInvalidaException e) {
                System.out.println("Ação inválida: " + e.getMessage());
            }
        }
    }
    private boolean usarItem(Batalha batalha, Personagem usuario) throws AcaoInvalidaException {
        Inventario inventario = batalha.getInventario();
        List<Item> itens = inventario.getItens();
        System.out.println("\nItens:");
        for (int i = 0; i < itens.size(); i++) {
            Item item = itens.get(i);
            System.out.println((i + 1) + " - " + item + " (x" + inventario.getQuantidade(item) + ")");
        }
        int escolha = lerInteiro("Item (0 para voltar): ", 0, itens.size());
        if (escolha == 0) {
            return false;
        }
        Item item = itens.get(escolha - 1);
        Equipe equipeDoAlvo = item.isUsadoEmAliado() ? batalha.getEquipeJogador() : batalha.getEquipeInimigos();
        Personagem alvo = escolherAlvo(equipeDoAlvo);
        System.out.println(batalha.usarItem(usuario, item, alvo));
        return true;
    }

    private Personagem escolherAlvo(Equipe equipe) {
        List<Personagem> membros = equipe.getMembros();
        System.out.println("\nEscolha o alvo:");
        exibirLista(membros);
        return membros.get(lerInteiro("Alvo: ", 1, membros.size()) - 1);
    }

    private void exibirCombatentes(Batalha batalha) {
        System.out.println("\n[" + batalha.getEquipeJogador().getNome() + "]");
        for (Personagem p : batalha.getEquipeJogador().getMembros()) {
            System.out.println("  " + p + " | " + descreverHabilidade(p));
        }
        System.out.println("[" + batalha.getEquipeInimigos().getNome() + "]");
        for (Personagem p : batalha.getEquipeInimigos().getMembros()) {
            System.out.println("  " + p + " | " + descreverHabilidade(p));
        }
    }

    private String descreverHabilidade(Personagem p) {
        String estado = p.isHabilidadeDisponivel() ? "disponível" : "recarga: " + p.getRecargaRestante() + " rodada(s)";
        return p.getNomeHabilidade() + " (" + estado + ")";
    }
    private void finalizarBatalha(Batalha batalha) {
        System.out.println(batalha.jogadorVenceu() ? "\n*** VITÓRIA! ***" : "\n*** DERROTA... ***");
        System.out.println("Inimigos derrotados: " + batalha.getInimigosDerrotados() + " | Rodadas: " + batalha.getRodada());
        RegistroBatalha registro = batalha.gerarRegistro();
        try {
            repositorioHistorico.registrar(registro);
            System.out.println("Batalha registrada no histórico.");
        } catch (IOException e) {
            System.out.println("Erro ao gravar o histórico: " + e.getMessage());
        }
        salvarPersonagens();
    }

    // ===================== Histórico =====================

    private void exibirHistorico() {
        System.out.println("\n--- Histórico de batalhas ---");
        try {
            List<RegistroBatalha> registros = repositorioHistorico.carregar();
            for (String aviso : repositorioHistorico.getAvisos()) {
                System.out.println("Aviso: " + aviso);
            }
            if (registros.isEmpty()) {
                System.out.println("Nenhuma batalha registrada ainda.");
                return;
            }
            for (RegistroBatalha r : registros) {
                System.out.println(r);
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o histórico: " + e.getMessage());
        }
    }
    // ===================== Utilitários =====================

    private void salvarPersonagens() {
        try {
            repositorioPersonagens.salvar(gerenciador.listar());
        } catch (IOException e) {
            System.out.println("Erro ao salvar os personagens: " + e.getMessage());
        }
    }

    private void exibirLista(List<Personagem> lista) {
        if (lista.isEmpty()) {
            System.out.println("(nenhum)");
            return;
        }
        for (int i = 0; i < lista.size(); i++) {
            System.out.println((i + 1) + ") " + lista.get(i));
        }
    }

    private int lerInteiro(String mensagem, int minimo, int maximo) {
        while (true) {
            System.out.print(mensagem);
            String texto = entrada.nextLine();
            try {
                int valor = Integer.parseInt(texto.trim());
                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
                System.out.println("Digite um número entre " + minimo + " e " + maximo + ".");
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida: digite apenas números.");
            }
        }
    }

    private String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return entrada.nextLine();
    }
}