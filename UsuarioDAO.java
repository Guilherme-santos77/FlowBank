package flowbank.dao;

import flowbank.database.Conexao;
import flowbank.model.Usuario;

import java.sql.*;
import java.time.LocalDate;

/**
 * DAO (Data Access Object) responsável pelas operações de banco de dados
 * relacionadas ao {@link Usuario}.
 */
public class UsuarioDAO {

    // ── Autenticar ─────────────────────────────────────────────────────────────

    /**
     * Verifica se existe um usuário com o e-mail e senha informados.
     *
     * @return O {@link Usuario} encontrado ou {@code null} se inválido.
     */
    public Usuario autenticar(String email, String senha) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND senha = ?";
        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, senha);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    // ── Cadastrar ──────────────────────────────────────────────────────────────

    /**
     * Insere um novo usuário no banco de dados.
     */
    public void cadastrar(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuarios (nome, email, celular, data_nascimento, senha) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNome());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getCelular());
            ps.setObject(4, u.getDataNascimento()); // aceita null
            ps.setString(5, u.getSenha());
            ps.executeUpdate();
        }
    }

    // ── Atualizar ──────────────────────────────────────────────────────────────

    /**
     * Atualiza os dados de um usuário existente.
     */
    public void atualizar(Usuario u) throws SQLException {
        String sql = "UPDATE usuarios SET nome=?, email=?, celular=?, data_nascimento=?, senha=? "
                   + "WHERE id=?";
        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNome());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getCelular());
            ps.setObject(4, u.getDataNascimento());
            ps.setString(5, u.getSenha());
            ps.setInt(6, u.getId());
            ps.executeUpdate();
        }
    }

    // ── Excluir ────────────────────────────────────────────────────────────────

    /**
     * Remove um usuário do banco de dados pelo ID.
     */
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ── Buscar por e-mail ──────────────────────────────────────────────────────

    public Usuario buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    // ── Auxiliar ───────────────────────────────────────────────────────────────

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setNome(rs.getString("nome"));
        u.setEmail(rs.getString("email"));
        u.setCelular(rs.getString("celular"));
        Date dn = rs.getDate("data_nascimento");
        if (dn != null) u.setDataNascimento(dn.toLocalDate());
        u.setSenha(rs.getString("senha"));
        return u;
    }
}
