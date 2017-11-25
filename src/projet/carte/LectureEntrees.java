package projet.carte;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.sf.libk8055.jk8055.JK8055Exception;

/**
 * <b>Cette classe permet d'obtenir en continu la valeur des entr�es de la
 * carte.</b>
 * <p>
 * Elle fonctionne en �tant une t�che. Une fois lanc�e, elle va continuellement
 * aller lire les entr�es de la carte et les stocker en m�moire. Il est ensuite
 * possible d'acc�der au r�sultat de cette lecture via la m�thode
 * recupererEntrees(). Pour l'arr�ter, un appel � la m�thode finLecture()
 * suffit.
 * </p>
 * <p>
 * En cas d'erreur elle va s'arr�ter et enregistrer l'erreur. Elle va ensuite
 * l'envoyer � chaque tentative de r�cup�ration des r�sultats. Il vous faudra
 * appeler la m�thode reinitialiserErreur() avant de pouvoir relancer la t�che.
 * </p>
 * 
 * @see LiaisonCarte
 * 
 * @author Merwen CANN et Rapha�l CHARRE - Novembre 2017
 * @version 1.0
 */
public final class LectureEntrees extends Thread {
  /**
   * Sert de verrou pour la t�che.
   * 
   * @see LectureEntrees#recupererEntrees()
   * @see LectureEntrees#run()
   */
  private Lock verrou = new ReentrantLock();
  /**
   * Contient l'unique instance autoris�e de cette classe.
   * 
   * @see LectureEntrees#recupererInstance()
   * @see LectureEntrees#LectureEntrees()
   * @see LectureEntrees#definirInstance(LectureEntrees)
   */
  private static LectureEntrees instance;
  /**
   * Ce tableau contient l'�tat de toutes les entr�es lors de la derni�re lecture.
   * <p>
   * Le format est le suivant :
   * </p>
   * <ul>
   * <li>valeur actuelle des cinq entr�es digitales (de 0 � 255),</li>
   * <li>valeur actuelle des deux entr�es analogiques (0 ou 1).</li>
   * </ul>
   * 
   * @see LectureEntrees#recupererEntrees()
   * @see LectureEntrees#run()
   */
  private int[] entrees = new int[7];
  /**
   * Permet de savoir si l'on doit arr�ter la lecture ou que l'on peut continuer.
   * 
   * @see LectureEntrees#finLecture()
   * @see LectureEntrees#reinitialiserErreur()
   * @see LectureEntrees#run()
   */
  private boolean continuer = true;
  /**
   * Contient la derni�re erreur de dialogue avec la carte, ou est nulle si tout
   * va bien.
   * 
   * @see LectureEntrees#reinitialiserErreur()
   * @see LectureEntrees#recupererEntrees()
   * @see LectureEntrees#run()
   */
  private JK8055Exception erreur;

  /**
   * Le constructeur de la classe.
   * <p>
   * Il se contente d'initialiser l'instance de cette classe. Une seule instance
   * �tant autoris�e � la fois, cette m�thode est priv�e.
   * </p>
   * 
   * @see LectureEntrees#definirInstance(LectureEntrees)
   */
  private LectureEntrees() {
    definirInstance(this);
  }

  /**
   * Permet de d�finir l'instance de la classe.
   * 
   * @param instance
   *          la nouvelle instance de la classe
   * 
   * @see LectureEntrees#instance
   */
  private static void definirInstance(LectureEntrees instance) {
    LectureEntrees.instance = instance;
  }

  /**
   * Permet de r�cup�rer l'unique instance de la classe, ou d'en cr�er une si elle
   * n'existe pas.
   * 
   * @return l'unique instance de la classe
   * 
   * @see LectureEntrees#instance
   * @see LectureEntrees#LectureEntrees()
   * @see LectureEntrees#erreur
   */
  public static synchronized LectureEntrees recupererInstance() {
    if (instance == null) {
      return new LectureEntrees();
    }

    return instance;
  }

  /**
   * Permet de r�cup�rer l'�tat de toutes les entr�es lors de la derni�re lecture.
   * <p>
   * Elle renverra une erreur si � un moment de la lecture en continu une erreur
   * est survenue. Il est possible d'utiliser la m�thode reinitialiserErreur()
   * pour pouvoir � nouveau s'en servir.
   * </p>
   * 
   * @return l'�tat de toutes les entr�es lors de la derni�re lecture
   * 
   * @throws JK8055Exception
   *           quand une erreur est survenue lors du dialogue avec la carte
   * 
   * @see LectureEntrees#entrees
   * @see LectureEntrees#erreur
   * @see LectureEntrees#verrou
   */
  public int[] recupererEntrees() throws JK8055Exception {
    if (erreur != null) {
      throw erreur;
    }

    int[] retour;

    verrou.lock();
    try {
      retour = entrees.clone();
    } finally {
      verrou.unlock();
    }

    return retour;
  }

  /**
   * Permet de supprimer toute erreur enregistr�e, ce qui permet de relancer la
   * lecture.
   * <p>
   * Cela n'entreprend n�anmoins aucune action correctrice, et il est assum� que
   * l'utilisateur a r�gl� le probl�me ayant caus� l'erreur avant d'appeler cette
   * m�thode.
   * </p>
   * 
   * @see LectureEntrees#erreur
   * @see LectureEntrees#continuer
   */
  public void reinitialiserErreur() {
    erreur = null;
    continuer = true;
  }

  /**
   * Permet d'arr�ter la lecture des entr�es.
   * <p>
   * Cette m�thode doit �tre utilis�e lors de la fermeture du programme, car elle
   * est le seul moyen de tuer cette t�che.
   * </p>
   * 
   * @see LectureEntrees#continuer
   */
  public void finLecture() {
    continuer = false;
  }

  /**
   * La m�thode permettant de lancer la t�che.
   * <p>
   * Elle va lire en boucle le contenu de la carte et ne s'arr�tera que lorsque
   * une erreur sera survenue, ou qu'on lui aura demand� de s'arr�ter.
   * </p>
   * 
   * @see LectureEntrees#entrees
   * @see LectureEntrees#erreur
   * @see LectureEntrees#continuer
   * @see LectureEntrees#verrou
   * @see LiaisonCarte#lectureComplete()
   */
  @Override
  public void run() {
    while (continuer) {
      try {
        verrou.lock();
        try {
          entrees = LiaisonCarte.lectureComplete();
        } finally {
          verrou.unlock();
        }

        sleep(50);
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (JK8055Exception e) {
        erreur = e;
        continuer = false;
      }
    }
  }
}
