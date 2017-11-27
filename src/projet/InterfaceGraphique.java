package projet;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class InterfaceGraphique extends Thread {
  private JButton boutonConnexion = new JButton();
  private JCheckBox[] entreesDigitales = new JCheckBox[5];
  private JLabel[] entreesAnalogiques = new JLabel[2];
  private boolean connecte = false;

  public InterfaceGraphique() {
    JFrame fenetre = new JFrame();
    fenetre.setTitle("Projet Module K8055");
    fenetre.setSize(505, 300);
    fenetre.setLocationRelativeTo(null);
    fenetre.setResizable(false);
    fenetre.setLayout(null);

    JTextField adresse = new JTextField();
    adresse.setText("0");

    boutonConnexion.setIcon(new ImageIcon("images/connexion.png"));
    fenetre.add(boutonConnexion);
    fenetre.add(adresse);
    PlainDocument doc = (PlainDocument) adresse.getDocument();
    doc.setDocumentFilter(new Filtre(2));

    JLabel infosAdresse = new JLabel("Adresse de la carte :");
    fenetre.add(infosAdresse);

    boutonConnexion.setBounds(165, 10, 20, 20);
    adresse.setBounds(135, 10, 20, 20);
    infosAdresse.setBounds(10, 10, 125, 20);

    boutonConnexion.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println(adresse.getText());
      }

    });

    for (int i = 0; i < 5; i++) {
      entreesDigitales[i] = new JCheckBox();
      entreesDigitales[i].setEnabled(false);
      entreesDigitales[i].setDisabledIcon(new ImageIcon("images/cercle_rouge.png"));
      entreesDigitales[i].setDisabledSelectedIcon(new ImageIcon("images/cercle_vert.png"));
      fenetre.add(entreesDigitales[i]);
      entreesDigitales[i].setBounds(60 * (i + 1), 100, 20, 20);
    }
    for (int i = 0; i < 2; i++) {
      entreesAnalogiques[i] = new JLabel("/");
      fenetre.add(entreesAnalogiques[i]);
      entreesAnalogiques[i].setBounds(360 + (60 * i), 100, 25, 20);
    }

    JCheckBox[] sortiesDigitales = new JCheckBox[5];
    for (int i = 0; i < 5; i++) {
      sortiesDigitales[i] = new JCheckBox();
      fenetre.add(sortiesDigitales[i]);
      sortiesDigitales[i].setIcon(new ImageIcon("images/cercle_rouge.png"));
      sortiesDigitales[i].setSelectedIcon(new ImageIcon("images/cercle_vert.png"));
      sortiesDigitales[i].setBounds(60 * (i + 1), 200, 20, 20);
    }
    JTextField sortieAnalogique1 = new JTextField("0");
    fenetre.add(sortieAnalogique1);
    sortieAnalogique1.setBounds(360, 200, 26, 20);
    JTextField sortieAnalogique2 = new JTextField("255");
    fenetre.add(sortieAnalogique2);
    sortieAnalogique2.setBounds(420, 200, 26, 20);

    fenetre.setVisible(true);
  }

  public void miseAJourEntrees(int[] entrees) {

  }

  class Filtre extends DocumentFilter {
    int max;

    Filtre(int max) {
      this.max = max;
    }

    private boolean estValide(String texte) {
      if (texte.length() > max) {
        return false;
      }
      if (texte.isEmpty()) {
        boutonConnexion.setEnabled(false);
        return true;
      }
      int intValue = 0;
      try {
        intValue = Integer.parseInt(texte.trim());
      } catch (NumberFormatException e) {
        return false;
      }
      if (intValue < 0 || intValue > 99) {
        return false;
      }
      boutonConnexion.setEnabled(true);
      return true;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
        throws BadLocationException {
      StringBuilder sb = new StringBuilder();
      sb.append(fb.getDocument().getText(0, fb.getDocument().getLength()));
      sb.insert(offset, text);
      if (estValide(sb.toString())) {
        super.insertString(fb, offset, text, attr);
      }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
        throws BadLocationException {
      StringBuilder sb = new StringBuilder();
      sb.append(fb.getDocument().getText(0, fb.getDocument().getLength()));
      int end = offset + length;
      sb.replace(offset, end, text);
      if (estValide(sb.toString())) {
        super.replace(fb, offset, length, text, attrs);
      }
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
      StringBuilder sb = new StringBuilder();
      sb.append(fb.getDocument().getText(0, fb.getDocument().getLength()));
      int end = offset + length;
      sb.delete(offset, end);
      if (estValide(sb.toString())) {
        super.remove(fb, offset, length);
      }
    }
  }
}
