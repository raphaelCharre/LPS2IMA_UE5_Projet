package projet.carte;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.sf.libk8055.jk8055.JK8055Exception;

/**
 * <b>Cette classe permet d'�crire � n'importe quel moment la valeur des sorties
 * de la carte.</b>
 * <p>
 * Elle fonctionne en �tant une t�che. Une fois lanc�e, elle va continuellement
 * aller lire le contenu de la file d'�criture et faire ce qui y est demand�. Il
 * est possible d'ajouter des �l�ments � la file � n'importe quel moment via
 * l'une des deux m�thodes d'�criture. Pour l'arr�ter, un appel � la m�thode
 * finTache() suffit.
 * </p>
 * <p>
 * En cas d'erreur elle va s'arr�ter et enregistrer l'erreur. Elle va ensuite
 * l'envoyer � chaque tentative d'�criture. Il vous faudra appeler la m�thode
 * reinitialiserErreur() avant de pouvoir relancer la t�che.
 * </p>
 * 
 * @see LiaisonCarte
 * 
 * @author Merwen CANN et Rapha�l CHARRE - Novembre 2017
 * @version 1.0
 */
public class EcritureSorties extends Thread {
  /**
   * Sert de verrou pour la t�che.
   * 
   * @see EcritureSorties#ecrireDigitale(int, boolean)
   * @see EcritureSorties#ecrireAnalogique(int, int)
   * @see EcritureSorties#run()
   */
  private Lock verrou = new ReentrantLock();
  /**
   * Contient l'unique instance autoris�e de cette classe.
   * 
   * @see EcritureSorties#recupererInstance()
   * @see EcritureSorties#EcritureSorties()
   * @see EcritureSorties#definirInstance(EcritureSorties)
   */
  private static EcritureSorties instance;
  /**
   * Correspond � la file d'�criture.
   * <p>
   * Contient des tableaux d'entier d�crivant les �critures successives �
   * effectuer. Ces tableaux ont comme format :
   * </p>
   * <ul>
   * <li>�criture analogique (0) ou digitale (1)</li>
   * <li>la valeur � �crire</li>
   * <li>le num�ro de la sortie qu'il faut �crire</li>
   * </ul>
   * 
   * @see EcritureSorties#ecrireDigitale(int, boolean)
   * @see EcritureSorties#ecrireAnalogique(int, int)
   * @see EcritureSorties#run()
   */
  private ArrayList<int[]> ecritures = new ArrayList<int[]>();
  /**
   * Permet de savoir si l'on doit arr�ter l'�criture ou que l'on peut continuer.
   * 
   * @see EcritureSorties#finTache()
   * @see EcritureSorties#reinitialiserErreur()
   * @see EcritureSorties#run()
   */
  private boolean continuer = true;
  /**
   * Contient la derni�re erreur de dialogue avec la carte, ou est nulle si tout
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
   * �tant autoris�e � la fois, cette m�thode est priv�e.
   * </p>
   * 
   * @see EcritureSorties#definirInstance(EcritureSorties)
   */
  private EcritureSorties() {
    definirInstance(this);
  }

  /**
   * Permet de d�finir l'instance de la classe.
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
   * Permet de r�cup�rer l'unique instance de la classe, ou d'en cr�er une si elle
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
   * Permet d'ajouter l'�criture d'une valeur sur une sortie digitale � la file.
   * <p>
   * Elle renverra une erreur si � un moment de l'�criture en continu une erreur
   * est survenue. Il est possible d'utiliser la m�thode reinitialiserErreur()
   * pour pouvoir � nouveau s'en servir.
   * </p>
   * 
   * @param chaine
   *          le num�ro de la sortie � laquelle envoyer la valeur (comprise entre
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
      throw new InvalidParameterException("Les sorties digitales vont de 1 � 5.");
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
   * Permet d'ajouter l'�criture d'une valeur sur une sortie analogique � la file.
   * <p>
   * Elle renverra une erreur si � un moment de l'�criture en continu une erreur
   * est survenue. Il est possible d'utiliser la m�thode reinitialiserErreur()
   * pour pouvoir � nouveau s'en servir.
   * </p>
   * 
   * @param chaine
   *          le num�ro de la sortie � laquelle envoyer la valeur (comprise entre
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
      throw new InvalidParameterException("La valeur doit �tre comprise entre 0 et 255.");
    }
    if (chaine < 1 || chaine > 2) {
      throw new InvalidParameterException("Les sorties analogiques vont de 1 � 2.");
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
   * Permet de supprimer toute erreur enregistr�e, ce qui permet de relancer
   * l'�criture.
   * <p>
   * Cela n'entreprend n�anmoins aucune action correctrice, et il est assum� que
   * l'utilisateur a r�gl� le probl�me ayant caus� l'erreur avant d'appeler cette
   * m�thode.
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
   * Permet d'arr�ter l'�criture.
   * <p>
   * Cette m�thode doit �tre utilis�e lors de la fermeture du programme, car elle
   * est le seul moyen de tuer cette t�che.
   * </p>
   * 
   * @see EcritureSorties#continuer
   */
  public void finTache() {
    continuer = false;
  }

  /**
   * La m�thode permettant de lancer la t�che.
   * <p>
   * Elle va lire en boucle la file d'�criture, puis �crire sur les sorties, et ne
   * s'arr�tera que lorsque une erreur sera survenue, ou qu'on lui aura demand� de
   * s'arr�ter.
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
