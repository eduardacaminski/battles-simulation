package simulador.controle;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import simulador.excecoes.AcaoInvalidaException;
import simulador.modelo.Arqueiro;
import simulador.modelo.Equipe;
import simulador.modelo.Guerreiro;
import simulador.modelo.Inventario;
import simulador.modelo.Item;
import simulador.modelo.Mago;
import simulador.modelo.Personagem;
import simulador.modelo.RegistroBatalha;


public class Batalha {

    private static final Random SORTEIO = new Random();
    private static final double CHANCE_INIMIGO_USAR_HABILIDADE = 0.4;
    private static final String[] NOMES_INIMIGOS = {"Goblin", "Orc", "Esqueleto", "Bandido", "Lobo Sombrio"};

    private final Equipe equipeJogador;
    private final Equipe equipeInimigos;
    private final Inventario inventario;
    private final List<Personagem> jaAgiramNaRodada = new ArrayList<>();
    private int rodada;
    public Batalha(Equipe equipeJogador, Equipe equipeInimigos, Inventario inventario) throws AcaoInvalidaException {
        if (equipeJogador == null || equipeJogador.estaVazia()) {
            throw new AcaoInvalidaException("A equipe do jogador precisa ter pelo menos 1 personagem.");
        }
        if (equipeJogador.todosDerrotados()) {
            throw new AcaoInvalidaException("Todos os personagens da equipe estão derrotados.");
        }
        if (equipeInimigos == null || equipeInimigos.estaVazia()) {
            throw new AcaoInvalidaException("Não há inimigos para enfrentar.");
        }
        this.equipeJogador = equipeJogador;
        this.equipeInimigos = equipeInimigos;
        this.inventario = inventario;
        this.rodada = 1;
        prepararEquipe(equipeJogador);
        prepararEquipe(equipeInimigos);
    }
    // ===================== Criação dos inimigos =====================

    public static Equipe gerarInimigos(int quantidade) throws AcaoInvalidaException {
        Equipe inimigos = new Equipe("Inimigos", quantidade);
        for (int i = 1; i <= quantidade; i++) {
            String nome = NOMES_INIMIGOS[SORTEIO.nextInt(NOMES_INIMIGOS.length)] + " " + i;
            inimigos.adicionar(criarInimigoAleatorio(nome));
        }
        return inimigos;
    }

    private static Personagem criarInimigoAleatorio(String nome) {
        int tipo = SORTEIO.nextInt(3);
        if (tipo == 0) {
            return new Guerreiro(nome);
        }
        if (tipo == 1) {
            return new Mago(nome);
        }
        return new Arqueiro(nome);
    }
    // ===================== Ações do jogador =====================

    public List<Personagem> getDisponiveisParaAgir() {
        List<Personagem> disponiveis = new ArrayList<>();
        for (Personagem p : equipeJogador.getVivos()) {
            if (!jaAgiramNaRodada.contains(p)) {
                disponiveis.add(p);
            }
        }
        return disponiveis;
    }

    public String atacar(Personagem atacante, Personagem alvo) throws AcaoInvalidaException {
        validarQuemAge(atacante);
        validarAlvoInimigo(alvo);
        String resultado = atacante.atacar(alvo);
        jaAgiramNaRodada.add(atacante);
        return resultado;
    }

    public String usarHabilidade(Personagem atacante, Personagem alvo) throws AcaoInvalidaException {
        validarQuemAge(atacante);
        validarAlvoInimigo(alvo);
        String resultado = atacante.usarHabilidade(alvo);
        jaAgiramNaRodada.add(atacante);
        return resultado;
    }
    public String usarItem(Personagem usuario, Item item, Personagem alvo) throws AcaoInvalidaException {
        validarQuemAge(usuario);
        if (item == null) {
            throw new AcaoInvalidaException("Nenhum item foi escolhido.");
        }
        Equipe equipeCerta = item.isUsadoEmAliado() ? equipeJogador : equipeInimigos;
        if (!equipeCerta.contem(alvo)) {
            String lado = item.isUsadoEmAliado() ? "aliados" : "inimigos";
            throw new AcaoInvalidaException(item.getNome() + " só pode ser usado(a) em " + lado + ".");
        }
        String resultado = inventario.usar(item, alvo);
        jaAgiramNaRodada.add(usuario);
        return resultado;
    }
    private void validarQuemAge(Personagem personagem) throws AcaoInvalidaException {
        if (terminou()) {
            throw new AcaoInvalidaException("A batalha já terminou.");
        }
        if (personagem == null || !equipeJogador.contem(personagem)) {
            throw new AcaoInvalidaException("Escolha um personagem da sua equipe.");
        }
        if (!personagem.podeAgir()) {
            throw new AcaoInvalidaException(personagem.getNome() + " está derrotado(a) e não pode agir.");
        }
        if (jaAgiramNaRodada.contains(personagem)) {
            throw new AcaoInvalidaException(personagem.getNome() + " já agiu nesta rodada.");
        }
    }

    private void validarAlvoInimigo(Personagem alvo) throws AcaoInvalidaException {
        if (alvo == null || !equipeInimigos.contem(alvo)) {
            throw new AcaoInvalidaException("Escolha um inimigo como alvo.");
        }
    }

    public boolean turnoDoJogadorAcabou() {
        return terminou() || getDisponiveisParaAgir().isEmpty();
    }
    // ===================== Turno dos inimigos =====================

    public List<String> executarTurnoDosInimigos() {
        List<String> acontecimentos = new ArrayList<>();
        for (Personagem inimigo : equipeInimigos.getVivos()) {
            if (equipeJogador.todosDerrotados()) {
                break;
            }
            Personagem alvo = escolherAlvoParaInimigo();
            try {
                boolean usarHabilidade = inimigo.isHabilidadeDisponivel()
                        && SORTEIO.nextDouble() < CHANCE_INIMIGO_USAR_HABILIDADE;
                if (usarHabilidade) {
                    acontecimentos.add(inimigo.usarHabilidade(alvo));
                } else {
                    acontecimentos.add(inimigo.atacar(alvo));
                }
            } catch (AcaoInvalidaException e) {
                acontecimentos.add(e.getMessage());
            }
        }
        if (!terminou()) {
            encerrarRodada();
        }
        return acontecimentos;
    }

    private Personagem escolherAlvoParaInimigo() {
        List<Personagem> vivos = equipeJogador.getVivos();
        return vivos.get(SORTEIO.nextInt(vivos.size()));
    }
    private void encerrarRodada() {
        for (Personagem p : equipeJogador.getMembros()) {
            p.passarRodada();
        }
        for (Personagem p : equipeInimigos.getMembros()) {
            p.passarRodada();
        }
        jaAgiramNaRodada.clear();
        rodada++;
    }

    private void prepararEquipe(Equipe equipe) {
        for (Personagem p : equipe.getMembros()) {
            p.prepararParaBatalha();
        }
    }

    // ===================== Resultado =====================

    public boolean terminou() {
        return equipeJogador.todosDerrotados() || equipeInimigos.todosDerrotados();
    }

    public boolean jogadorVenceu() {
        return equipeInimigos.todosDerrotados() && !equipeJogador.todosDerrotados();
    }

    public int getInimigosDerrotados() {
        return equipeInimigos.contarDerrotados();
    }

    public RegistroBatalha gerarRegistro() {
        String resultado = jogadorVenceu() ? "VITÓRIA" : "DERROTA";
        return new RegistroBatalha(LocalDateTime.now(), equipeJogador.getNomesDosMembros(),
                equipeInimigos.getNomesDosMembros(), resultado, getInimigosDerrotados(), rodada);
    }

    public int getRodada() {
        return rodada;
    }

    public Equipe getEquipeJogador() {
        return equipeJogador;
    }

    public Equipe getEquipeInimigos() {
        return equipeInimigos;
    }

    public Inventario getInventario() {
        return inventario;
    }
}