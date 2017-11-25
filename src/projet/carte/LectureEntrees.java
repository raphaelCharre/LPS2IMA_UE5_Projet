package projet.carte;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.sf.libk8055.jk8055.JK8055Exception;

/**
 * <b>Cette classe permet d'obtenir en continu la valeur des entrées de la
 * carte.</b>
 * <p>
 * Elle fonctionne en étant une tâche. Une fois lancée, elle va continuellement
 * aller lire les entrées de la carte et les stocker en mémoire. Il est ensuite
 * possible d'accéder au résultat de cette lecture via la méthode
 * recupererEntrees(). Pour l'arrêter, un appel à la méthode finLecture()
 * suffit.
 * </p>
 * <p>
 * En cas d'erreur elle va s'arrêter et enregistrer l'erreur. Elle va ensuite
 * l'envoyer à chaque tentative de récupération des résultats. Il vous faudra
 * appeler la méthode reinitialiserErreur() avant de pouvoir relancer la tâche.
 * </p>
 * 
 * @see LiaisonCarte
 * 
 * @author Merwen CANN et Raphaël CHARRE - Novembre 2017
 * @version 1.0
 */
public final class LectureEntrees extends Thread {
  /**
   * Sert de verrou pour la tâche.
   * 
   * @see LectureEntrees#recupererEntrees()
   * @see LectureEntrees#run()
   */
  private Lock verrou = new ReentrantLock();
  /**
   * Contient l'unique instance autorisée de cette classe.
   * 
   * @see LectureEntrees#recupererInstance()
   * @see LectureEntrees#LectureEntrees()
   * @see LectureEntrees#definirInstance(LectureEntrees)
   */
  private static LectureEntrees instance;
  /**
   * Ce tableau contient l'état de toutes les entrées lors de la dernière lecture.
   * <p>
   * Le format est le suivant :
   * </p>
   * <ul>
   * <li>valeur actuelle des cinq entrées digitales (de 0 à 255),</li>
   * <li>valeur actuelle des deux entrées analogiques (0 ou 1).</li>
   * </ul>
   * 
   * @see LectureEntrees#recupererEntrees()
   * @see LectureEntrees#run()
   */
  private int[] entrees = new int[7];
  /**
   * Permet de savoir si l'on doit arrêter la lecture ou que l'on peut continuer.
   * 
   * @see LectureEntrees#finLecture()
   * @see LectureEntrees#reinitialiserErreur()
   * @see LectureEntrees#run()
   */
  private boolean continuer = true;
  /**
   * Contient la dernière erreur de dialogue avec la carte, ou est nulle si tout
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
   * étant autorisée à la fois, cette méthode est privée.
   * </p>
   * 
   * @see LectureEntrees#definirInstance(LectureEntrees)
   */
  private LectureEntrees() {
    definirInstance(this);
  }

  /**
   * Permet de définir l'instance de la classe.
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
   * Permet de récupérer l'unique instance de la classe, ou d'en créer une si elle
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
   * Permet de récupérer l'état de toutes les entrées lors de la dernière lecture.
   * <p>
   * Elle renverra une erreur si à un moment de la lecture en continu une erreur
   * est survenue. Il est possible d'utiliser la méthode reinitialiserErreur()
   * pour pouvoir à nouveau s'en servir.
   * </p>
   * 
   * @return l'état de toutes les entrées lors de la dernière lecture
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
   * Permet de supprimer toute erreur enregistrée, ce qui permet de relancer la
   * lecture.
   * <p>
   * Cela n'entreprend néanmoins aucune action correctrice, et il est assumé que
   * l'utilisateur a réglé le problème ayant causé l'erreur avant d'appeler cette
   * méthode.
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
   * Permet d'arrêter la lecture des entrées.
   * <p>
   * Cette méthode doit être utilisée lors de la fermeture du programme, car elle
   * est le seul moyen de tuer cette tâche.
   * </p>
   * 
   * @see LectureEntrees#continuer
   */
  public void finLecture() {
    continuer = false;
  }

  /**
   * La méthode permettant de lancer la tâche.
   * <p>
   * Elle va lire en boucle le contenu de la carte et ne s'arrêtera que lorsque
   * une erreur sera survenue, ou qu'on lui aura demandé de s'arrêter.
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
