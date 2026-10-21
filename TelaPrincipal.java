package flowbank.view;

import flowbank.dao.LancamentoDAO;
import flowbank.model.Lancamento;
import flowbank.model.Usuario;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TelaPrincipal extends JFrame {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Usuario usuarioLogado;
    private final LancamentoDAO lancamentoDAO = new LancamentoDAO();
    private List<Lancamento> todosLancamentos;

    private final JLabel lblNome  = new JLabel();
    private final JLabel lblSaldo = new JLabel();
    private final JButton btnDados = new JButton("Meus Dados");
    private final JButton btnNovo  = new JButton("+ Novo Lançamento");
    private final JComboBox<String> cbFiltro = new JComboBox<>(new String[]{"Todos", "Entradas", "Saídas"});
    private final DefaultTableModel modelo;
    private final JTable tabela;

    public TelaPrincipal(Usuario usuario) {
        this.usuarioLogado = usuario;
        modelo = new DefaultTableModel(new String[]{"#", "Descrição", "Tipo", "Valor (R$)", "Data / Hora"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modelo);
        configurarJanela();
        construirLayout();
        configurarTabela();
        configurarEventos();
        carregarLancamentos();
    }

    private void configurarJanela() {
        setTitle("FlowBank – Extrato");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 580);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Cores.BG);
    }

    private void construirLayout() {
        setLayout(new BorderLayout(0, 0));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Cores.SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, Cores.INPUT_BDR),
            new EmptyBorder(18, 28, 18, 28)));

        JPanel esq = new JPanel();
        esq.setLayout(new BoxLayout(esq, BoxLayout.Y_AXIS));
        esq.setOpaque(false);
        JLabel logo = new JLabel("flowbank");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logo.setForeground(Cores.ACCENT_LT);
        lblNome.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblNome.setForeground(Cores.SUBTEXT);
        lblNome.setText("Olá, " + usuarioLogado.getNome() + "!");
        esq.add(logo);
        esq.add(Box.createVerticalStrut(3));
        esq.add(lblNome);

        JPanel dir = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        dir.setOpaque(false);
        lblSaldo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnDados.setBackground(Cores.INPUT_BG);
        btnDados.setForeground(Cores.ACCENT_LT);
        btnDados.setFocusPainted(false);
        btnDados.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDados.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDados.setBorder(BorderFactory.createLineBorder(Cores.ACCENT, 1, true));
        btnDados.setPreferredSize(new Dimension(130, 36));
        dir.add(lblSaldo);
        dir.add(btnDados);

        header.add(esq, BorderLayout.WEST);
        header.add(dir, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Corpo
        JPanel corpo = new JPanel(new BorderLayout(0, 0));
        corpo.setBackground(Cores.BG);

        JPanel barraAcoes = new JPanel(new BorderLayout());
        barraAcoes.setBackground(Cores.SURFACE);
        barraAcoes.setBorder(new EmptyBorder(14, 28, 14, 28));

        JPanel pnlFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlFiltro.setOpaque(false);
        JLabel lblFiltro = new JLabel("Filtrar por:");
        lblFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFiltro.setForeground(Cores.SUBTEXT);
        cbFiltro.setBackground(Cores.INPUT_BG);
        cbFiltro.setForeground(Cores.TEXT);
        cbFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbFiltro.setPreferredSize(new Dimension(150, 34));
        cbFiltro.setUI(new javax.swing.plaf.basic.BasicComboBoxUI());
        cbFiltro.setOpaque(true);
        cbFiltro.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? Cores.ACCENT : Cores.INPUT_BG);
                setForeground(Cores.TEXT);
                setBorder(new javax.swing.border.EmptyBorder(4, 10, 4, 10));
                return this;
            }
        });
        pnlFiltro.add(lblFiltro);
        pnlFiltro.add(cbFiltro);

        btnNovo.setBackground(Cores.ACCENT);
        btnNovo.setForeground(Color.WHITE);
        btnNovo.setFocusPainted(false);
        btnNovo.setBorderPainted(false);
        btnNovo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnNovo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnNovo.setPreferredSize(new Dimension(190, 36));

        barraAcoes.add(pnlFiltro, BorderLayout.WEST);
        barraAcoes.add(btnNovo, BorderLayout.EAST);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(0, 28, 0, 28),
            BorderFactory.createLineBorder(Cores.INPUT_BDR, 1, true)));
        scroll.getViewport().setBackground(Cores.CARD);

        JLabel footer = new JLabel("FlowBank © 2026 – Todos os direitos reservados", SwingConstants.CENTER);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(new Color(80, 65, 110));
        footer.setBorder(new EmptyBorder(10, 0, 12, 0));

        corpo.add(barraAcoes, BorderLayout.NORTH);
        corpo.add(scroll, BorderLayout.CENTER);
        corpo.add(footer, BorderLayout.SOUTH);
        add(corpo, BorderLayout.CENTER);
    }

    private void configurarTabela() {
        tabela.setRowHeight(36);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setBackground(Cores.CARD);
        tabela.setForeground(Cores.TEXT);
        tabela.setGridColor(new Color(55, 42, 85));
        tabela.setSelectionBackground(new Color(100, 50, 180));
        tabela.setSelectionForeground(Color.WHITE);
        tabela.setShowVerticalLines(false);
        tabela.setIntercellSpacing(new Dimension(0, 1));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setBackground(Cores.SURFACE);
        tabela.getTableHeader().setForeground(Cores.ACCENT_LT);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabela.getTableHeader().setBorder(new MatteBorder(0, 0, 1, 0, Cores.INPUT_BDR));
        tabela.getColumnModel().getColumn(0).setMaxWidth(50);

        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                if (!sel) {
                    setBackground(row % 2 == 0 ? Cores.CARD : new Color(32, 40, 75));
                    String tipo = (String) t.getModel().getValueAt(row, 2);
                    if (col == 2 || col == 3) {
                        setForeground("ENTRADA".equals(tipo) ? Cores.SUCCESS : Cores.DANGER);
                        setFont(new Font("Segoe UI", Font.BOLD, 13));
                    } else {
                        setForeground(Cores.TEXT);
                        setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    }
                }
                setHorizontalAlignment(col == 3 ? RIGHT : LEFT);
                return this;
            }
        });
    }

    private void configurarEventos() {
        btnDados.addActionListener(e -> {
            new TelaCadastroUsuario(this, usuarioLogado).setVisible(true);
            setVisible(false);
        });
        btnNovo.addActionListener(e -> abrirDialogoNovoLancamento());
        cbFiltro.addActionListener(e -> aplicarFiltro());
    }

    private void abrirDialogoNovoLancamento() {
        JDialog dlg = new JDialog(this, "Novo Lançamento", true);
        dlg.setSize(440, 360);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(false);
        dlg.getContentPane().setBackground(Cores.CARD);
        dlg.setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Novo Lançamento", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(Cores.ACCENT_LT);
        titulo.setBorder(new EmptyBorder(22, 0, 10, 0));
        dlg.add(titulo, BorderLayout.NORTH);

        JTextField txtDesc  = new JTextField();
        JTextField txtValor = new JTextField();
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"ENTRADA", "SAIDA"});
        cbTipo.setBackground(Cores.INPUT_BG);
        cbTipo.setForeground(Cores.TEXT);
        cbTipo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbTipo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? Cores.ACCENT : Cores.INPUT_BG);
                setForeground(Cores.TEXT);
                setBorder(new javax.swing.border.EmptyBorder(6, 10, 6, 10));
                return this;
            }
        });

        JPanel campos = new JPanel(new GridBagLayout());
        campos.setBackground(Cores.CARD);
        campos.setBorder(new EmptyBorder(0, 32, 0, 32));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL; g.gridx = 0; g.weightx = 1;

        String[] labs = {"Descrição:", "Tipo:", "Valor (R$):"};
        JComponent[] flds = {txtDesc, cbTipo, txtValor};
        for (int i = 0; i < labs.length; i++) {
            JLabel l = new JLabel(labs[i]);
            l.setForeground(Cores.SUBTEXT);
            l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g.gridy = i * 2; g.insets = new Insets(6, 0, 2, 0);
            campos.add(l, g);
            estilizarCampo(flds[i]);
            g.gridy = i * 2 + 1; g.insets = new Insets(0, 0, 2, 0);
            campos.add(flds[i], g);
        }
        dlg.add(campos, BorderLayout.CENTER);

        JButton btnSalvar = new JButton("Salvar");
        JButton btnCanc   = new JButton("Cancelar");
        btnSalvar.setBackground(Cores.ACCENT); btnSalvar.setForeground(Color.WHITE);
        btnCanc.setBackground(Cores.INPUT_BG); btnCanc.setForeground(Cores.SUBTEXT);
        for (JButton b : new JButton[]{btnSalvar, btnCanc}) {
            b.setFocusPainted(false); b.setBorderPainted(false);
            b.setFont(new Font("Segoe UI", Font.BOLD, 13));
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setPreferredSize(new Dimension(120, 38));
        }

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 16));
        btns.setBackground(Cores.CARD);
        btns.add(btnCanc); btns.add(btnSalvar);
        dlg.add(btns, BorderLayout.SOUTH);

        btnCanc.addActionListener(ev -> dlg.dispose());
        btnSalvar.addActionListener(ev -> {
            String desc = txtDesc.getText().trim();
            String valStr = txtValor.getText().trim().replace(",", ".");
            if (desc.isEmpty() || valStr.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Preencha todos os campos.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
            BigDecimal valor;
            try {
                valor = new BigDecimal(valStr);
                if (valor.compareTo(BigDecimal.ZERO) <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg, "Valor inválido. Ex: 150.00", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Lancamento l = new Lancamento();
            l.setUsuarioId(usuarioLogado.getId());
            l.setDescricao(desc);
            l.setTipo(Lancamento.Tipo.valueOf((String) cbTipo.getSelectedItem()));
            l.setValor(valor);
            try {
                lancamentoDAO.inserir(l);
                dlg.dispose();
                carregarLancamentos();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dlg, "Erro ao salvar:\n" + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        dlg.setVisible(true);
    }

    private void estilizarCampo(JComponent c) {
        c.setBackground(Cores.INPUT_BG);
        c.setForeground(Cores.TEXT);
        if (c instanceof JTextField) ((JTextField) c).setCaretColor(Cores.ACCENT_LT);
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Cores.INPUT_BDR, 1, true),
            new EmptyBorder(8, 12, 8, 12)));
        c.setPreferredSize(new Dimension(0, 38));
    }

    private void carregarLancamentos() {
        try {
            todosLancamentos = lancamentoDAO.listarPorUsuario(usuarioLogado.getId());
            aplicarFiltro();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar:\n" + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aplicarFiltro() {
        if (todosLancamentos == null) return;
        modelo.setRowCount(0);
        String filtro = (String) cbFiltro.getSelectedItem();
        BigDecimal saldo = BigDecimal.ZERO;
        for (Lancamento l : todosLancamentos) {
            boolean show = "Todos".equals(filtro)
                || ("Entradas".equals(filtro) && l.getTipo() == Lancamento.Tipo.ENTRADA)
                || ("Saídas".equals(filtro)   && l.getTipo() == Lancamento.Tipo.SAIDA);
            if (show) {
                String data = l.getDataLancamento() != null ? l.getDataLancamento().format(FMT) : "-";
                modelo.addRow(new Object[]{l.getId(), l.getDescricao(), l.getTipo().name(),
                        String.format("%.2f", l.getValor()), data});
            }
            saldo = l.getTipo() == Lancamento.Tipo.ENTRADA
                  ? saldo.add(l.getValor()) : saldo.subtract(l.getValor());
        }
        boolean pos = saldo.compareTo(BigDecimal.ZERO) >= 0;
        lblSaldo.setText("R$ " + String.format("%.2f", saldo));
        lblSaldo.setForeground(pos ? Cores.SUCCESS : Cores.DANGER);
    }

    public void atualizarDadosUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
        lblNome.setText("Olá, " + usuario.getNome() + "!");
        carregarLancamentos();
    }
}