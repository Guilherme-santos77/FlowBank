package flowbank.dao;

import flowbank.database.Conexao;
import flowbank.model.Lancamento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsável pelas operações de banco de dados relacionadas a {@link Lancamento}.
 */
public class LancamentoDAO {

    // ── Listar por usuário ─────────────────────────────────────────────────────

    /**
     * Retorna todos os lançamentos de um usuário, ordenados do mais recente ao mais antigo.
     */
    public List<Lancamento> listarPorUsuario(int usuarioId) throws SQLException {
        List<Lancamento> lista = new ArrayList<>();
        String sql = "SELECT * FROM lancamentos WHERE usuario_id = ? ORDER BY data_lancamento DESC";
        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    // ── Inserir ────────────────────────────────────────────────────────────────

    /**
     * Insere um novo lançamento no banco de dados.
     * O ID gerado automaticamente é atribuído ao objeto {@code l}.
     *
     * @param l Lançamento a ser persistido (campos usuarioId, descricao, tipo e valor obrigatórios).
     */
    public void inserir(Lancamento l) throws SQLException {
        String sql = "INSERT INTO lancamentos (usuario_id, descricao, tipo, valor) "
                   + "VALUES (?, ?, ?::tipo_lancamento, ?)";
        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, l.getUsuarioId());
            ps.setString(2, l.getDescricao());
            ps.setString(3, l.getTipo().name());      // "ENTRADA" ou "SAIDA"
            ps.setBigDecimal(4, l.getValor());
            ps.executeUpdate();

            // Recupera o ID gerado e atribui ao objeto
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    l.setId(rs.getInt(1));
                }
            }
        }
    }

    // ── Excluir ────────────────────────────────────────────────────────────────

    /**
     * Remove um lançamento do banco de dados pelo ID.
     *
     * @param id        ID do lançamento a excluir.
     * @param usuarioId ID do usuário dono do lançamento (garante que ninguém
     *                  exclua lançamento de outro usuário).
     */
    public void excluir(int id, int usuarioId) throws SQLException {
        String sql = "DELETE FROM lancamentos WHERE id = ? AND usuario_id = ?";
        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, usuarioId);
            ps.executeUpdate();
        }
    }

    // ── Auxiliar ───────────────────────────────────────────────────────────────

    private Lancamento mapear(ResultSet rs) throws SQLException {
        Lancamento l = new Lancamento();
        l.setId(rs.getInt("id"));
        l.setUsuarioId(rs.getInt("usuario_id"));
        l.setDescricao(rs.getString("descricao"));
        l.setTipo(Lancamento.Tipo.valueOf(rs.getString("tipo")));
        l.setValor(rs.getBigDecimal("valor"));
        Timestamp ts = rs.getTimestamp("data_lancamento");
        if (ts != null) l.setDataLancamento(ts.toInstant()
            .atZone(java.time.ZoneId.of("America/Sao_Paulo"))
            .toLocalDateTime());
        return l;
    }
}