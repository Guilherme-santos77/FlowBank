package flowbank;

import flowbank.view.TelaAutenticacao;

import javax.swing.*;

/**
 * Ponto de entrada da aplicação FlowBank.
 */
public class Main {

    public static void main(String[] args) {
        // Usa o look-and-feel do sistema operacional para uma aparência mais nativa
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Mantém o look-and-feel padrão (Metal) em caso de falha
        }

        // Inicializa a interface gráfica na Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> new TelaAutenticacao().setVisible(true));
    }
}
