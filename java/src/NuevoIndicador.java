import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class NuevoIndicador {
    private JTextField txtNombre;
    private JButton ingresarButton;
    private JPanel panel;
    private JTextField txtFórmula;
    private JButton volverButton;
    private JDialog dialog;

    private boolean camposVálidos() {
        return txtNombre.getText().length() > 0 && txtFórmula.getText().length() > 0;
    }

    private void cerrar() {
        dialog.setVisible(false);
        dialog.dispose();
    }

    NuevoIndicador(JFrame frame) {
        String operadores = "+-*/()";

        txtNombre.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent evt) {
                char c = evt.getKeyChar();
                if (Character.isLetter(c)) super.keyTyped(evt);
                else evt.consume();
            }
        });

        txtFórmula.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent evt) {
                char c = evt.getKeyChar();
                if (Character.isLetter(c) || operadores.indexOf(c) != -1) super.keyTyped(evt);
                else evt.consume();
            }
        });

        ingresarButton.addActionListener(actionEvent -> {
            if (camposVálidos()) {
                new Indicador(txtNombre.getText(), txtFórmula.getText());
                cerrar();
            }
        });

        volverButton.addActionListener(actionEvent -> cerrar());

        dialog = new JDialog(frame, true);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setVisible(true);
    }
}
