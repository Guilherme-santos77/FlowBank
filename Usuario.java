package flowbank.model;

import java.time.LocalDate;

/**
 * Modelo que representa um Usuário do FlowBank.
 */
public class Usuario {

    private int id;
    private String nome;
    private String email;
    private String celular;
    private LocalDate dataNascimento;
    private String senha;

    // ── Construtores ───────────────────────────────────────────────────────────

    public Usuario() {}

    public Usuario(int id, String nome, String email, String celular,
                   LocalDate dataNascimento, String senha) {
        this.id             = id;
        this.nome           = nome;
        this.email          = email;
        this.celular        = celular;
        this.dataNascimento = dataNascimento;
        this.senha          = senha;
    }

    // ── Getters e Setters ──────────────────────────────────────────────────────

    public int getId()                       { return id; }
    public void setId(int id)               { this.id = id; }

    public String getNome()                  { return nome; }
    public void setNome(String nome)        { this.nome = nome; }

    public String getEmail()                 { return email; }
    public void setEmail(String email)      { this.email = email; }

    public String getCelular()               { return celular; }
    public void setCelular(String celular)  { this.celular = celular; }

    public LocalDate getDataNascimento()                         { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento)     { this.dataNascimento = dataNascimento; }

    public String getSenha()                 { return senha; }
    public void setSenha(String senha)      { this.senha = senha; }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nome='" + nome + "', email='" + email + "'}";
    }
}
