package flowbank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Modelo que representa um Lançamento (transação) do FlowBank.
 */
public class Lancamento {

    public enum Tipo { ENTRADA, SAIDA }

    private int id;
    private int usuarioId;
    private String descricao;
    private Tipo tipo;
    private BigDecimal valor;
    private LocalDateTime dataLancamento;

    // ── Construtores ───────────────────────────────────────────────────────────

    public Lancamento() {}

    public Lancamento(int id, int usuarioId, String descricao, Tipo tipo,
                      BigDecimal valor, LocalDateTime dataLancamento) {
        this.id              = id;
        this.usuarioId       = usuarioId;
        this.descricao       = descricao;
        this.tipo            = tipo;
        this.valor           = valor;
        this.dataLancamento  = dataLancamento;
    }

    // ── Getters e Setters ──────────────────────────────────────────────────────

    public int getId()                              { return id; }
    public void setId(int id)                      { this.id = id; }

    public int getUsuarioId()                       { return usuarioId; }
    public void setUsuarioId(int usuarioId)        { this.usuarioId = usuarioId; }

    public String getDescricao()                    { return descricao; }
    public void setDescricao(String descricao)     { this.descricao = descricao; }

    public Tipo getTipo()                           { return tipo; }
    public void setTipo(Tipo tipo)                 { this.tipo = tipo; }

    public BigDecimal getValor()                    { return valor; }
    public void setValor(BigDecimal valor)         { this.valor = valor; }

    public LocalDateTime getDataLancamento()                          { return dataLancamento; }
    public void setDataLancamento(LocalDateTime dataLancamento)      { this.dataLancamento = dataLancamento; }
}
