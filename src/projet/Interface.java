package projet;

import javax.swing.JCheckBox;
import javax.swing.JFrame;

public class Interface extends Thread {
  public Interface() {
    JFrame fenetre = new JFrame();
    fenetre.setTitle("Super Interface");
    fenetre.setSize(800, 600);
    fenetre.setResizable(false);

    JCheckBox test = new JCheckBox();
    test.setEnabled(false);

    fenetre.add(test);
    fenetre.setVisible(true);
  }
}
