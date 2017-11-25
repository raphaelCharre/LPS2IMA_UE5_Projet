package projet.carte;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.sf.libk8055.jk8055.JK8055Exception;

/**
 * <b>Cette classe permet d'écrire à n'importe quel moment la valeur des sorties
 * de la carte.</b>
 * <p>
 * Elle fonctionne en étant une tâche. Une fois lancée, elle va continuellement
 * aller lire le contenu de la file d'écriture et faire ce qui y est demandé. Il
 * est possible d'ajouter des éléments à la file à n'importe quel moment via
 * l'une des deux méthodes d'écriture. Pour l'arrêter, un appel à la méthode
 * finTache() suffit.
 * </p>
 * <p>
 * En cas d'erreur elle va s'arrêter et enregistrer l'erreur. Elle va ensuite
 * l'envoyer à chaque tentative d'écriture. Il vous faudra appeler la méthode
 * reinitialiserErreur() avant de pouvoir relancer la tâche.
 * </p>
 * 
 * @see LiaisonCarte
 * 
 * @author Merwen CANN et Raphaël CHARRE - Novembre 2017
 * @version 1.0
 */
public class EcritureSorties extends Thread {
  /**
   * Sert de verrou pour la tâche.
   * 
   * @see EcritureSorties#ecrireDigitale(int, boolean)
   * @see EcritureSorties#ecrireAnalogique(int, int)
   * @see EcritureSorties#run()
   */
  private Lock verrou = new ReentrantLock();
  /**
   * Contient l'unique instance autorisée de cette classe.
   * 
   * @see EcritureSorties#recupererInstance()
   * @see EcritureSorties#EcritureSorties()
   * @see EcritureSorties#definirInstance(EcritureSorties)
   */
  private static EcritureSorties instance;
  /**
   * Correspond à la file d'écriture.
   * <p>
   * Contient des tableaux d'entier décrivant les écritures successives à
   * effectuer. Ces tableaux ont comme format :
   * </p>
   * <ul>
   * <li>écriture analogique (0) ou digitale (1)</li>
   * <li>la valeur à écrire</li>
   * <li>le numéro de la sortie qu'il faut écrire</li>
   * </ul>
   * 
   * @see EcritureSorties#ecrireDigitale(int, boolean)
   * @see EcritureSorties#ecrireAnalogique(int, int)
   * @see EcritureSorties#run()
   */
  private ArrayList<int[]> ecritures = new ArrayList<int[]>();
  /**
   * Permet de savoir si l'on doit arrêter l'écriture ou que l'on peut continuer.
   * 
   * @see EcritureSorties#finTache()
   * @see EcritureSorties#reinitialiserErreur()
   * @see EcritureSorties#run()
   */
  private boolean continuer = true;
  /**
   * Contient la dernière erreur de dialogue avec la carte, ou est nulle si tout
   * va bien.
   * 
   * @see EcritureSorties#reinitialiserErreur()
   * @see EcritureSorties#ecrireDigitale(int, boolean)
   * @see EcritureSorties#ecrireAnalogique(int, int)
   * @see EcritureSorties#run()
   */
  private JK8055Exception erreur;

  /**
   * Le constructeur de la classe.
   * <p>
   * Il se contente d'initialiser l'instance de cette classe. Une seule instance
   * étant autorisée à la fois, cette méthode est privée.
   * </p>
   * 
   * @see EcritureSorties#definirInstance(EcritureSorties)
   */
  private EcritureSorties() {
    definirInstance(this);
  }

  /**
   * Permet de définir l'instance de la classe.
   * 
   * @param instance
   *          la nouvelle instance de la classe
   * 
   * @see EcritureSorties#instance
   */
  private static void definirInstance(EcritureSorties instance) {
    EcritureSorties.instance = instance;
  }

  /**
   * Permet de récupérer l'unique instance de la classe, ou d'en créer une si elle
   * n'existe pas.
   * 
   * @return l'unique instance de la classe
   * 
   * @see EcritureSorties#instance
   * @see EcritureSorties#EcritureSorties()
   * @see EcritureSorties#erreur
   */
  public static synchronized EcritureSorties recupererInstance() {
    if (instance == null) {
      return new EcritureSorties();
    }

    return instance;
  }

  /**
   * Permet d'ajouter l'écriture d'une valeur sur une sortie digitale à la file.
   * <p>
   * Elle renverra une erreur si à un moment de l'écriture en continu une erreur
   * est survenue. Il est possible d'utiliser la méthode reinitialiserErreur()
   * pour pouvoir à nouveau s'en servir.
   * </p>
   * 
   * @param chaine
   *          le numéro de la sortie à laquelle envoyer la valeur (comprise entre
   *          1 et 5)
   * @param valeur
   *          la nouvelle valeur de la sortie
   * 
   * @throws JK8055Exception
   *           quand une erreur est survenue lors du dialogue avec la carte
   * 
   * @see EcritureSorties#ecritures
   * @see EcritureSorties#erreur
   * @see EcritureSorties#verrou
   */
  public void ecrireDigitale(int chaine, boolean valeur) throws JK8055Exception {
    if (erreur != null) {
      throw erreur;
    }

    if (chaine < 1 || chaine > 5) {
      throw new InvalidParameterException("Les sorties digitales vont de 1 à 5.");
    }

    int[] ajout = new int[3];

    ajout[0] = 0;
    ajout[1] = chaine;
    ajout[2] = valeur ? 1 : 0;

    verrou.lock();
    try {
      ecritures.add(ajout);

      verrou.notifyAll();
    } finally {
      verrou.unlock();
    }
  }

  /**
   * Permet d'ajouter l'écriture d'une valeur sur une sortie analogique à la file.
   * <p>
   * Elle renverra une erreur si à un moment de l'écriture en continu une erreur
   * est survenue. Il est possible d'utiliser la méthode reinitialiserErreur()
   * pour pouvoir à nouveau s'en servir.
   * </p>
   * 
   * @param chaine
   *          le numéro de la sortie à laquelle envoyer la valeur (comprise entre
   *          1 et 2)
   * @param valeur
   *          la nouvelle valeur de la sortie (comprise entre 0 et 255)
   * 
   * @throws JK8055Exception
   *           quand une erreur est survenue lors du dialogue avec la carte
   * 
   * @see EcritureSorties#ecritures
   * @see EcritureSorties#erreur
   * @see EcritureSorties#verrou
   */
  public void ecrireAnalogique(int chaine, int valeur) throws JK8055Exception {
    if (erreur != null) {
      throw erreur;
    }

    if (valeur < 0 || valeur > 255) {
      throw new InvalidParameterException("La valeur doit être comprise entre 0 et 255.");
    }
    if (chaine < 1 || chaine > 2) {
      throw new InvalidParameterException("Les sorties analogiques vont de 1 à 2.");
    }

    int[] ajout = new int[3];

    ajout[0] = 1;
    ajout[1] = chaine;
    ajout[2] = valeur;

    verrou.lock();
    try {
      ecritures.add(ajout);

      verrou.notifyAll();
    } finally {
      verrou.unlock();
    }
  }

  /**
   * Permet de supprimer toute erreur enregistrée, ce qui permet de relancer
   * l'écriture.
   * <p>
   * Cela n'entreprend néanmoins aucune action correctrice, et il est assumé que
   * l'utilisateur a réglé le problème ayant causé l'erreur avant d'appeler cette
   * méthode.
   * </p>
   * 
   * @see EcritureSorties#erreur
   * @see EcritureSorties#continuer
   */
  public void reinitialiserErreur() {
    erreur = null;
    continuer = true;
  }

  /**
   * Permet d'arrêter l'écriture.
   * <p>
   * Cette méthode doit être utilisée lors de la fermeture du programme, car elle
   * est le seul moyen de tuer cette tâche.
   * </p>
   * 
   * @see EcritureSorties#continuer
   */
  public void finTache() {
    continuer = false;
  }

  /**
   * La méthode permettant de lancer la tâche.
   * <p>
   * Elle va lire en boucle la file d'écriture, puis écrire sur les sorties, et ne
   * s'arrêtera que lorsque une erreur sera survenue, ou qu'on lui aura demandé de
   * s'arrêter.
   * </p>
   * 
   * @see EcritureSorties#ecritures
   * @see EcritureSorties#erreur
   * @see EcritureSorties#continuer
   * @see EcritureSorties#verrou
   * @see LiaisonCarte#sortieDigitale(int, boolean)
   * @see LiaisonCarte#sortieAnalogique(int, int)
   */
  @Override
  public void run() {
    while (continuer) {
      try {
        if (ecritures.size() > 0) {
          int[] ecriture;

          verrou.lock();
          try {
            ecriture = ecritures.get(0);
          } finally {
            verrou.unlock();
          }

          if (ecriture[0] == 0) {
            LiaisonCarte.sortieDigitale(ecriture[1], ecriture[2] == 1);
          } else {
            LiaisonCarte.sortieAnalogique(ecriture[1], ecriture[2]);
          }

          verrou.lock();
          try {
            ecritures.remove(0);
          } finally {
            verrou.unlock();
          }
        } else {
          verrou.lock();
          try {
            verrou.wait();
          } finally {
            verrou.unlock();
          }
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (JK8055Exception e) {
        erreur = e;
        continuer = false;
      }
    }
  }
}
