package flowbank.view;

import flowbank.dao.UsuarioDAO;
import flowbank.model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TelaCadastroUsuario extends JFrame {

    private final Usuario    usuarioLogado;
    private final JFrame     telaOrigem;
    private final boolean    modoAtualizacao;
    private final UsuarioDAO dao = new UsuarioDAO();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final JTextField     txtId      = new JTextField(10);
    private final JTextField     txtNome    = new JTextField(30);
    private final JTextField     txtEmail   = new JTextField(30);
    private final JTextField     txtCelular = new JTextField(20);
    private final JTextField     txtData    = new JTextField(15);
    private final JPasswordField txtSenha   = new JPasswordField(20);
    private final JPasswordField txtConf    = new JPasswordField(20);

    private final JButton btnAcao    = new JButton("Cadastrar");
    private final JButton btnExcluir = new JButton("Excluir");
    private final JButton btnCancel  = new JButton("Cancelar");

    public TelaCadastroUsuario(JFrame telaOrigem, Usuario usuarioLogado) {
        this.telaOrigem      = telaOrigem;
        this.usuarioLogado   = usuarioLogado;
        this.modoAtualizacao = (usuarioLogado != null);
        configurarJanela();
        construirLayout();
        configurarModo();
        configurarEventos();
    }

    private void configurarJanela() {
        setTitle("FlowBank – Cadastro");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(500, 520);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Cores.BG);
    }

    private void construirLayout() {
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Cores.SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, Cores.INPUT_BDR),
            new EmptyBorder(18, 28, 18, 28)));
        JLabel logo = new JLabel("flowbank");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logo.setForeground(Cores.ACCENT_LT);
        JLabel subtitulo = new JLabel(modoAtualizacao ? "Atualizar Dados" : "Criar Conta");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(Cores.SUBTEXT);
        JPanel esq = new JPanel();
        esq.setLayout(new BoxLayout(esq, BoxLayout.Y_AXIS));
        esq.setOpaque(false);
        esq.add(logo);
        esq.add(Box.createVerticalStrut(2));
        esq.add(subtitulo);
        header.add(esq, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Campos
        JPanel corpo = new JPanel(new GridBagLayout());
        corpo.setBackground(Cores.BG);
        corpo.setBorder(new EmptyBorder(16, 36, 10, 36));
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;

        String[] labs = {"ID:", "Nome:", "E-mail:", "Celular:", "Nascimento (dd/MM/yyyy):", "Senha:", "Confirmar Senha:"};
        JComponent[] flds = {txtId, txtNome, txtEmail, txtCelular, txtData, txtSenha, txtConf};

        for (int i = 0; i < labs.length; i++) {
            JLabel l = new JLabel(labs[i]);
            l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            l.setForeground(Cores.SUBTEXT);
            g.gridy = i * 2; g.insets = new Insets(5, 0, 2, 0);
            corpo.add(l, g);
            estilizarCampo(flds[i]);
            g.gridy = i * 2 + 1; g.insets = new Insets(0, 0, 0, 0);
            corpo.add(flds[i], g);
        }

        txtId.setEditable(false);
        txtId.setBackground(new Color(30, 22, 50));
        txtId.setForeground(Cores.SUBTEXT);

        JScrollPane scroll = new JScrollPane(corpo);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Cores.BG);
        add(scroll, BorderLayout.CENTER);

        // Botões
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 16));
        pnlBtns.setBackground(Cores.SURFACE);
        pnlBtns.setBorder(new MatteBorder(1, 0, 0, 0, Cores.INPUT_BDR));

        btnAcao.setBackground(Cores.ACCENT);       btnAcao.setForeground(Color.WHITE);
        btnExcluir.setBackground(new Color(120, 20, 40)); btnExcluir.setForeground(Cores.DANGER);
        btnCancel.setBackground(Cores.INPUT_BG);   btnCancel.setForeground(Cores.SUBTEXT);

        for (JButton b : new JButton[]{btnAcao, btnExcluir, btnCancel}) {
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setFont(new Font("Segoe UI", Font.BOLD, 13));
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setPreferredSize(new Dimension(130, 38));
        }

        pnlBtns.add(btnAcao);
        pnlBtns.add(btnExcluir);
        pnlBtns.add(btnCancel);
        add(pnlBtns, BorderLayout.SOUTH);
    }

    private void estilizarCampo(JComponent c) {
        c.setBackground(Cores.INPUT_BG);
        c.setForeground(Cores.TEXT);
        if (c instanceof JTextField) ((JTextField) c).setCaretColor(Cores.ACCENT_LT);
        if (c instanceof JPasswordField) ((JPasswordField) c).setCaretColor(Cores.ACCENT_LT);
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Cores.INPUT_BDR, 1, true),
            new EmptyBorder(8, 12, 8, 12)));
        c.setPreferredSize(new Dimension(0, 38));
    }

    private void configurarModo() {
        if (modoAtualizacao) {
            btnAcao.setText("Atualizar");
            txtId.setText(String.valueOf(usuarioLogado.getId()));
            txtNome.setText(usuarioLogado.getNome());
            txtEmail.setText(usuarioLogado.getEmail());
            txtCelular.setText(usuarioLogado.getCelular());
            if (usuarioLogado.getDataNascimento() != null)
                txtData.setText(usuarioLogado.getDataNascimento().format(FMT));
            txtSenha.setText(usuarioLogado.getSenha());
            txtConf.setText(usuarioLogado.getSenha());
        } else {
            btnExcluir.setEnabled(false);
            btnExcluir.setBackground(new Color(50, 40, 70));
            btnExcluir.setForeground(new Color(90, 75, 115));
        }
    }

    private void configurarEventos() {
        btnAcao.addActionListener((ActionEvent e) -> {
            if (!validar()) return;
            Usuario u = montarUsuario();
            if (u == null) return;
            try {
                if (modoAtualizacao) {
                    u.setId(usuarioLogado.getId());
                    dao.atualizar(u);
                    JOptionPane.showMessageDialog(this, "Dados atualizados com sucesso!");
                    usuarioLogado.setNome(u.getNome());
                    usuarioLogado.setEmail(u.getEmail());
                    usuarioLogado.setCelular(u.getCelular());
                    usuarioLogado.setDataNascimento(u.getDataNascimento());
                    usuarioLogado.setSenha(u.getSenha());
                    irParaPrincipal();
                } else {
                    dao.cadastrar(u);
                    JOptionPane.showMessageDialog(this, "Conta criada! Faça o login.");
                    irParaAutenticacao();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro:\n" + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnExcluir.addActionListener((ActionEvent e) -> {
            int r = JOptionPane.showConfirmDialog(this,
                "Deseja excluir sua conta? Esta ação não pode ser desfeita.",
                "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (r == JOptionPane.YES_OPTION) {
                try {
                    dao.excluir(usuarioLogado.getId());
                    JOptionPane.showMessageDialog(this, "Conta excluída.");
                    irParaAutenticacao();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erro:\n" + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnCancel.addActionListener((ActionEvent e) -> {
            if (modoAtualizacao) irParaPrincipal();
            else irParaAutenticacao();
        });
    }

    private boolean validar() {
        if (txtNome.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty()
                || txtCelular.getText().trim().isEmpty()
                || new String(txtSenha.getPassword()).trim().isEmpty()
                || new String(txtConf.getPassword()).trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos obrigatórios.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!new String(txtSenha.getPassword()).equals(new String(txtConf.getPassword()))) {
            JOptionPane.showMessageDialog(this, "Senha e confirmação não conferem.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private Usuario montarUsuario() {
        Usuario u = new Usuario();
        u.setNome(txtNome.getText().trim());
        u.setEmail(txtEmail.getText().trim());
        u.setCelular(txtCelular.getText().trim());
        u.setSenha(new String(txtSenha.getPassword()).trim());
        String d = txtData.getText().trim();
        if (!d.isEmpty()) {
            try { u.setDataNascimento(LocalDate.parse(d, FMT)); }
            catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Data inválida. Use dd/MM/yyyy.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return null;
            }
        }
        return u;
    }

    private void irParaAutenticacao() {
        dispose();
        if (telaOrigem instanceof TelaAutenticacao) telaOrigem.setVisible(true);
        else new TelaAutenticacao().setVisible(true);
    }

    private void irParaPrincipal() {
        dispose();
        if (telaOrigem instanceof TelaPrincipal) {
            TelaPrincipal tp = (TelaPrincipal) telaOrigem;
            tp.atualizarDadosUsuario(usuarioLogado);
            tp.setVisible(true);
        } else if (telaOrigem != null) {
            telaOrigem.setVisible(true);
        }
    }
}
