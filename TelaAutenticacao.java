package flowbank.view;

import flowbank.dao.UsuarioDAO;
import flowbank.model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class TelaAutenticacao extends JFrame {

    private final JTextField     txtEmail     = new JTextField(25);
    private final JPasswordField txtSenha     = new JPasswordField(25);
    private final JButton        btnLogin     = new JButton("Entrar");
    private final JButton        btnCadastrar = new JButton("Criar conta");
    private final UsuarioDAO     usuarioDAO   = new UsuarioDAO();

    public TelaAutenticacao() {
        configurarJanela();
        construirLayout();
        configurarEventos();
    }

    private void configurarJanela() {
        setTitle("FlowBank");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(440, 420);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Cores.BG);
    }

    private void construirLayout() {
        setLayout(new GridBagLayout());

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Cores.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Cores.INPUT_BDR, 1, true),
            new EmptyBorder(40, 44, 36, 44)));
        card.setPreferredSize(new Dimension(370, 360));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;

        JLabel logo = new JLabel("flowbank", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        logo.setForeground(Cores.ACCENT_LT);
        g.gridy = 0; g.insets = new Insets(0, 0, 4, 0);
        card.add(logo, g);

        JLabel sub = new JLabel("Sua vida financeira simplificada", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(Cores.SUBTEXT);
        g.gridy = 1; g.insets = new Insets(0, 0, 28, 0);
        card.add(sub, g);

        g.insets = new Insets(4, 0, 4, 0);
        g.gridy = 2; card.add(makeLabel("E-mail"), g);
        g.gridy = 3; card.add(estilizarTextField(txtEmail), g);
        g.gridy = 4; g.insets = new Insets(10, 0, 4, 0);
        card.add(makeLabel("Senha"), g);
        g.gridy = 5; g.insets = new Insets(4, 0, 4, 0);
        card.add(estilizarPasswordField(txtSenha), g);

        g.gridy = 6; g.insets = new Insets(22, 0, 6, 0);
        card.add(makeBtnPrimary(btnLogin), g);
        g.gridy = 7; g.insets = new Insets(4, 0, 0, 0);
        card.add(makeBtnSecondary(btnCadastrar), g);

        add(card);
    }

    private JLabel makeLabel(String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(Cores.SUBTEXT);
        return l;
    }

    private JTextField estilizarTextField(JTextField c) {
        c.setBackground(Cores.INPUT_BG);
        c.setForeground(Cores.TEXT);
        c.setCaretColor(Cores.ACCENT_LT);
        c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Cores.INPUT_BDR, 1, true),
            new EmptyBorder(9, 14, 9, 14)));
        c.setPreferredSize(new Dimension(0, 42));
        return c;
    }

    private JPasswordField estilizarPasswordField(JPasswordField c) {
        c.setBackground(Cores.INPUT_BG);
        c.setForeground(Cores.TEXT);
        c.setCaretColor(Cores.ACCENT_LT);
        c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Cores.INPUT_BDR, 1, true),
            new EmptyBorder(9, 14, 9, 14)));
        c.setPreferredSize(new Dimension(0, 42));
        return c;
    }

    private JButton makeBtnPrimary(JButton btn) {
        btn.setBackground(Cores.ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(0, 44));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton makeBtnSecondary(JButton btn) {
        btn.setBackground(Cores.INPUT_BG);
        btn.setForeground(Cores.SUBTEXT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setPreferredSize(new Dimension(0, 40));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void configurarEventos() {
        btnLogin.addActionListener((ActionEvent e) -> {
            String email = txtEmail.getText().trim();
            String senha = new String(txtSenha.getPassword()).trim();
            if (email.isEmpty() || senha.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha e-mail e senha.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                Usuario u = usuarioDAO.autenticar(email, senha);
                if (u == null) {
                    JOptionPane.showMessageDialog(this, "E-mail ou senha incorretos.", "Erro", JOptionPane.ERROR_MESSAGE);
                } else {
                    new TelaPrincipal(u).setVisible(true);
                    dispose();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao conectar:\n" + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCadastrar.addActionListener((ActionEvent e) -> {
            new TelaCadastroUsuario(this, null).setVisible(true);
            setVisible(false);
        });
    }
}
