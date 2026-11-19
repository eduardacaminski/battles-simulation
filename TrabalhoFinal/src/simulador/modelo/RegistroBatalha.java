package simulador.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class RegistroBatalha {

    public static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final LocalDateTime dataHora;
    private final String equipeJogador;
    private final String equipeInimigos;
    private final String resultado;
    private final int inimigosDerrotados;
    private final int rodadas;

    public RegistroBatalha(LocalDateTime dataHora, String equipeJogador, String equipeInimigos,
            String resultado, int inimigosDerrotados, int rodadas) {
        this.dataHora = dataHora;
        this.equipeJogador = equipeJogador;
        this.equipeInimigos = equipeInimigos;
        this.resultado = resultado;
        this.inimigosDerrotados = inimigosDerrotados;
        this.rodadas = rodadas;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getEquipeJogador() {
        return equipeJogador;
    }

    public String getEquipeInimigos() {
        return equipeInimigos;
    }

    public String getResultado() {
        return resultado;
    }

    public int getInimigosDerrotados() {
        return inimigosDerrotados;
    }

    public int getRodadas() {
        return rodadas;
    }

    @Override
    public String toString() {
        return String.format("%s | %s em %d rodada(s) | Equipe: %s | Inimigos: %s | Inimigos derrotados: %d",
                dataHora.format(FORMATO_DATA), resultado, rodadas, equipeJogador, equipeInimigos, inimigosDerrotados);
    }
}
