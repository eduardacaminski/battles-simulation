package simulador.controle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import simulador.excecoes.AcaoInvalidaException;
import simulador.modelo.FabricaPersonagens;
import simulador.modelo.Personagem;


public class GerenciadorPersonagens {

    private final List<Personagem> personagens = new ArrayList<>();

    public GerenciadorPersonagens(List<Personagem> personagensCarregados) {
        if (personagensCarregados != null) {
            personagens.addAll(personagensCarregados);
        }
    }

    public Personagem cadastrar(String tipo, String nome) throws AcaoInvalidaException {
        if (nome == null || nome.isBlank()) {
            throw new AcaoInvalidaException("O nome não pode ficar vazio.");
        }
        if (nome.contains(";")) {
            throw new AcaoInvalidaException("O nome não pode conter o caractere ';'.");
        }
        if (buscarPorNome(nome) != null) {
            throw new AcaoInvalidaException("Já existe um personagem chamado " + nome.trim() + ".");
        }
        Personagem novo = FabricaPersonagens.criar(tipo, nome.trim());
        personagens.add(novo);
        return novo;
    }
    public Personagem buscarPorNome(String nome) {
        for (Personagem p : personagens) {
            if (p.getNome().equalsIgnoreCase(nome.trim())) {
                return p;
            }
        }
        return null;
    }

    public List<Personagem> listar() {
        return Collections.unmodifiableList(personagens);
    }

    public List<Personagem> listarVivos() {
        List<Personagem> vivos = new ArrayList<>();
        for (Personagem p : personagens) {
            if (p.estaVivo()) {
                vivos.add(p);
            }
        }
        return vivos;
    }

    public void descansarTodos() {
        for (Personagem p : personagens) {
            p.restaurarVidaTotal();
        }
    }

    public boolean estaVazio() {
        return personagens.isEmpty();
    }
}