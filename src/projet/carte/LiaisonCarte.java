package projet.carte;

import java.security.InvalidParameterException;

import net.sf.libk8055.jk8055.JK8055;
import net.sf.libk8055.jk8055.JK8055.AllAnalog;
import net.sf.libk8055.jk8055.JK8055Exception;

/**
 * <b>Cette classe offre quelques fonctions utiles pour facilement lire et
 * envoyer des valeurs avec la carte.</b>
 * <p>
 * Elle s'occupe elle-m�me d'ouvrir le dialogue avec la carte via la classe
 * JK8055 et convertit �galement les valeurs re�ues pour �tre plus faciles
 * d'utilisation. Elle laisse n�anmoins la gestion des erreurs aux classes qui
 * s'en serviront.
 * </p>
 * <p>
 * <b>Note</b> : s'agissant d'une classe utilitaire, toutes les m�thodes sont
 * statiques, et l'instanciation de la classe a �t� emp�ch�e.
 * </p>
 * 
 * @see EcritureSorties
 * @see LectureEntrees
 * 
 * @author Merwen CANN et Rapha�l CHARRE - Novembre 2017
 * @version 1.0
 */
final class LiaisonCarte {
  /**
   * Ce tableau contient l'�tat actuel de toutes les sorties de la carte.
   * <p>
   * Le format est le suivant :
   * </p>
   * <ul>
   * <li>valeur actuelle des cinq sorties digitales (de 0 � 255),</li>
   * <li>valeur actuelle des deux sorties analogiques (0 ou 1).</li>
   * </ul>
   * <p>
   * Ce tableau est utilis� par les m�thodes d'�criture des sorties car les
   * m�thodes de la classe JK8055 ne permettent qu'une �criture sur plusieurs
   * sorties en m�me-temps. Il faut donc garder en m�moire la valeur des sorties
   * pour les modifier unes � unes, �tant donn� qu'on ne peut lire la valeur d'une
   * sortie.
   * </p>
   * 
   * @see LiaisonCarte#sortieDigitale(int, boolean)
   * @see LiaisonCarte#sortieAnalogique(int, int)
   */
  private static int[] sortie = new int[7];

  /**
   * Contient le num�ro de la carte afin de pouvoir y acc�der.
   * <p>
   * Cette variable est utilis�e par toutes les m�thodes de la classe afin
   * d'initier le dialogue avec la carte.
   * </p>
   * 
   * @see LiaisonCarte#lectureComplete()
   * @see LiaisonCarte#sortieDigitale(int, boolean)
   * @see LiaisonCarte#sortieAnalogique(int, int)
   */
  private static final int APPAREIL = 0;

  /**
   * Constructeur de la classe.
   * <p>
   * Cette classe n'�tant pas pr�vue pour �tre instanci�e, ce constructeur a comme
   * visibilit� "priv�".
   * </p>
   */
  private LiaisonCarte() {
  }

  /**
   * Permet de lire toutes les entr�es de la carte.
   * <p>
   * Apr�s lecture des entr�es de la carte, ces derni�re sont retourn�es sous
   * forme de tableau. Ce dernier a comme format :
   * </p>
   * <ul>
   * <li>valeur actuelle des cinq entr�es digitales (de 0 � 255),</li>
   * <li>valeur actuelle des deux entr�es analogiques (0 ou 1).</li>
   * </ul>
   * 
   * @return toutes les entr�es sous forme d'un tableau d'entier
   * 
   * @throws JK8055Exception
   *           quand une erreur intervient lors du dialogue avec la carte
   * 
   * @see LiaisonCarte#APPAREIL
   */
  static int[] lectureComplete() throws JK8055Exception {
    JK8055 jk8055 = JK8055.getInstance();
    jk8055.OpenDevice(APPAREIL);

    int valeursDigitales = jk8055.ReadAllDigital();
    AllAnalog valeursAnalogiques = jk8055.ReadAllAnalog();

    int[] retour = new int[7];
    String conversion = Integer.toBinaryString(valeursDigitales);

    for (int i = 0; i < 5; i++) {
      retour[i] = Integer.parseInt(conversion.substring(i, i + 1));
    }

    retour[5] = valeursAnalogiques.data1;
    retour[6] = valeursAnalogiques.data2;

    return retour;
  }

  /**
   * Permet d'envoyer une valeur � une sortie digitale.
   * <p>
   * Il ne devrait pas y avoir de probl�me lors de l'utilisation des deux m�thodes
   * d'�criture de cette classe � partir de plusieurs t�ches.
   * </p>
   * 
   * @param chaine
   *          le num�ro de la sortie � laquelle envoyer la valeur (comprise entre
   *          1 et 5)
   * @param valeur
   *          la nouvelle valeur de la sortie
   * 
   * @throws JK8055Exception
   *           quand une erreur intervient lors du dialogue avec la carte
   * 
   * @see LiaisonCarte#sortie
   * @see LiaisonCarte#APPAREIL
   */
  static synchronized void sortieDigitale(int chaine, boolean valeur) throws JK8055Exception {
    if (chaine < 1 || chaine > 5) {
      throw new InvalidParameterException("Les sorties digitales vont de 1 � 5.");
    }

    JK8055 jk8055 = JK8055.getInstance();
    jk8055.OpenDevice(APPAREIL);

    int[] tmp = sortie.clone();
    tmp[chaine - 1] = valeur ? 1 : 0;

    StringBuffer tampon = new StringBuffer();
    for (int i = 0; i < 5; i++) {
      tampon.append(tmp[i]);
    }
    String conversion = tampon.toString();

    int valeursDigitales = Integer.parseInt(conversion, 2);

    jk8055.SetAllValues(valeursDigitales, sortie[5], sortie[6]);
    sortie[chaine - 1] = valeur ? 1 : 0;
  }

  /**
   * Permet d'envoyer une valeur � une sortie analogique.
   * <p>
   * Il ne devrait pas y avoir de probl�me lors de l'utilisation des deux m�thodes
   * d'�criture de cette classe � partir de plusieurs t�ches.
   * </p>
   * 
   * @param chaine
   *          le num�ro de la sortie � laquelle envoyer la valeur (comprise entre
   *          1 et 2)
   * @param valeur
   *          la nouvelle valeur de la sortie (comprise entre 0 et 255)
   * 
   * @throws JK8055Exception
   *           quand une erreur intervient lors du dialogue avec la carte
   * 
   * @see LiaisonCarte#sortie
   * @see LiaisonCarte#APPAREIL
   */
  static synchronized void sortieAnalogique(int chaine, int valeur) throws JK8055Exception {
    if (valeur < 0 || valeur > 255) {
      throw new InvalidParameterException("La valeur doit �tre comprise entre 0 et 255.");
    }
    if (chaine < 1 || chaine > 2) {
      throw new InvalidParameterException("Les sorties analogiques vont de 1 � 2.");
    }

    JK8055 jk8055 = JK8055.getInstance();
    jk8055.OpenDevice(APPAREIL);

    StringBuffer tampon = new StringBuffer();
    for (int i = 0; i < 5; i++) {
      tampon.append(sortie[i]);
    }
    String conversion = tampon.toString();

    int valeursDigitales = Integer.parseInt(conversion, 2);

    if (chaine == 1) {
      jk8055.SetAllValues(valeursDigitales, valeur, sortie[6]);
      sortie[5] = valeur;
    } else {
      jk8055.SetAllValues(valeursDigitales, sortie[5], valeur);
      sortie[6] = valeur;
    }
  }
}
