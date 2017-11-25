package projet.carte;

import java.security.InvalidParameterException;

import net.sf.libk8055.jk8055.JK8055;
import net.sf.libk8055.jk8055.JK8055.AllAnalog;
import net.sf.libk8055.jk8055.JK8055Exception;

/**
 * <b>Cette classe offre quelques fonctions utiles pour facilement lire et
 * envoyer des valeurs avec la carte.</b>
 * <p>
 * Elle s'occupe elle-même d'ouvrir le dialogue avec la carte via la classe
 * JK8055 et convertit également les valeurs reçues pour être plus faciles
 * d'utilisation. Elle laisse néanmoins la gestion des erreurs aux classes qui
 * s'en serviront.
 * </p>
 * <p>
 * <b>Note</b> : s'agissant d'une classe utilitaire, toutes les méthodes sont
 * statiques, et l'instanciation de la classe a été empêchée.
 * </p>
 * 
 * @see EcritureSorties
 * @see LectureEntrees
 * 
 * @author Merwen CANN et Raphaël CHARRE - Novembre 2017
 * @version 1.0
 */
final class LiaisonCarte {
  /**
   * Ce tableau contient l'état actuel de toutes les sorties de la carte.
   * <p>
   * Le format est le suivant :
   * </p>
   * <ul>
   * <li>valeur actuelle des cinq sorties digitales (de 0 à 255),</li>
   * <li>valeur actuelle des deux sorties analogiques (0 ou 1).</li>
   * </ul>
   * <p>
   * Ce tableau est utilisé par les méthodes d'écriture des sorties car les
   * méthodes de la classe JK8055 ne permettent qu'une écriture sur plusieurs
   * sorties en même-temps. Il faut donc garder en mémoire la valeur des sorties
   * pour les modifier unes à unes, étant donné qu'on ne peut lire la valeur d'une
   * sortie.
   * </p>
   * 
   * @see LiaisonCarte#sortieDigitale(int, boolean)
   * @see LiaisonCarte#sortieAnalogique(int, int)
   */
  private static int[] sortie = new int[7];

  /**
   * Contient le numéro de la carte afin de pouvoir y accéder.
   * <p>
   * Cette variable est utilisée par toutes les méthodes de la classe afin
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
   * Cette classe n'étant pas prévue pour être instanciée, ce constructeur a comme
   * visibilité "privé".
   * </p>
   */
  private LiaisonCarte() {
  }

  /**
   * Permet de lire toutes les entrées de la carte.
   * <p>
   * Après lecture des entrées de la carte, ces dernière sont retournées sous
   * forme de tableau. Ce dernier a comme format :
   * </p>
   * <ul>
   * <li>valeur actuelle des cinq entrées digitales (de 0 à 255),</li>
   * <li>valeur actuelle des deux entrées analogiques (0 ou 1).</li>
   * </ul>
   * 
   * @return toutes les entrées sous forme d'un tableau d'entier
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
   * Permet d'envoyer une valeur à une sortie digitale.
   * <p>
   * Il ne devrait pas y avoir de problème lors de l'utilisation des deux méthodes
   * d'écriture de cette classe à partir de plusieurs tâches.
   * </p>
   * 
   * @param chaine
   *          le numéro de la sortie à laquelle envoyer la valeur (comprise entre
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
      throw new InvalidParameterException("Les sorties digitales vont de 1 à 5.");
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
   * Permet d'envoyer une valeur à une sortie analogique.
   * <p>
   * Il ne devrait pas y avoir de problème lors de l'utilisation des deux méthodes
   * d'écriture de cette classe à partir de plusieurs tâches.
   * </p>
   * 
   * @param chaine
   *          le numéro de la sortie à laquelle envoyer la valeur (comprise entre
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
      throw new InvalidParameterException("La valeur doit être comprise entre 0 et 255.");
    }
    if (chaine < 1 || chaine > 2) {
      throw new InvalidParameterException("Les sorties analogiques vont de 1 à 2.");
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
