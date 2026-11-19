package simulador.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import simulador.excecoes.AcaoInvalidaException;

public class Equipe {

    private final String nome;
    private final int limiteMaximo;
    private final List<Personagem> membros = new ArrayList<>();

    public Equipe(String nome, int limiteMaximo) {
        if (limiteMaximo < 1) {
            throw new IllegalArgumentException("Uma equipe precisa aceitar pelo menos 1 integrante.");
        }
        this.nome = nome;
        this.limiteMaximo = limiteMaximo;
    }
    public void adicionar(Personagem personagem) throws AcaoInvalidaException {
        if (personagem == null) {
            throw new AcaoInvalidaException("Personagem inválido.");
        }
        if (estaCheia()) {
            throw new AcaoInvalidaException("A equipe já tem o máximo de " + limiteMaximo + " integrante(s).");
        }
        if (membros.contains(personagem)) {
            throw new AcaoInvalidaException(personagem.getNome() + " já está na equipe.");
        }
        if (!personagem.estaVivo()) {
            throw new AcaoInvalidaException(personagem.getNome() + " está derrotado(a). Use a opção Descansar antes.");
        }
        membros.add(personagem);
    }
    public List<Personagem> getMembros() {
        return Collections.unmodifiableList(membros);
    }

    public List<Personagem> getVivos() {
        List<Personagem> vivos = new ArrayList<>();
        for (Personagem p : membros) {
            if (p.estaVivo()) {
                vivos.add(p);
            }
        }
        return vivos;
    }

    public boolean todosDerrotados() {
        return getVivos().isEmpty();
    }

    public int contarDerrotados() {
        return membros.size() - getVivos().size();
    }
    public boolean contem(Personagem personagem) {
        return membros.contains(personagem);
    }

    public boolean estaCheia() {
        return membros.size() >= limiteMaximo;
    }

    public boolean estaVazia() {
        return membros.isEmpty();
    }

    public int getQuantidade() {
        return membros.size();
    }

    public String getNomesDosMembros() {
        List<String> nomes = new ArrayList<>();
        for (Personagem p : membros) {
            nomes.add(p.getNome());
        }
        return String.join(", ", nomes);
    }

    public String getNome() {
        return nome;
    }

    public int getLimiteMaximo() {
        return limiteMaximo;
    }
}