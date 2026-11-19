package simulador.modelo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import simulador.excecoes.AcaoInvalidaException;

public class Inventario {

    private static final int POCOES_INICIAIS = 3;
    private static final int BOMBAS_INICIAIS = 2;

    private final Map<Item, Integer> quantidades = new LinkedHashMap<>();

    public static Inventario criarPadrao() {
        Inventario inventario = new Inventario();
        inventario.adicionar(new PocaoVida(), POCOES_INICIAIS);
        inventario.adicionar(new Bomba(), BOMBAS_INICIAIS);
        return inventario;
    }
    public void adicionar(Item item, int quantidade) {
        if (item == null || quantidade <= 0) {
            throw new IllegalArgumentException("Item ou quantidade inválidos.");
        }
        quantidades.put(item, getQuantidade(item) + quantidade);
    }

    public int getQuantidade(Item item) {
        return quantidades.getOrDefault(item, 0);
    }

    public boolean temDisponivel(Item item) {
        return getQuantidade(item) > 0;
    }

    public String usar(Item item, Personagem alvo) throws AcaoInvalidaException {
        if (item == null) {
            throw new AcaoInvalidaException("Nenhum item foi escolhido.");
        }
        if (!temDisponivel(item)) {
            throw new AcaoInvalidaException("Não há " + item.getNome() + " disponível no inventário.");
        }
        String resultado = item.usar(alvo);
        quantidades.put(item, getQuantidade(item) - 1);
        return resultado + " Restam " + getQuantidade(item) + " unidade(s) de " + item.getNome() + ".";
    }

    public List<Item> getItens() {
        return new ArrayList<>(quantidades.keySet());
    }
}
