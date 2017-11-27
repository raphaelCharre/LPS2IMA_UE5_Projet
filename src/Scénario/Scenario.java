package Sc�nario;

import net.sf.libk8055.jk8055.JK8055Exception;
import projet.carte.EcritureSorties;
import projet.carte.LectureEntrees;
import projet.carte.LiaisonCarte;

public class Scenario extends Thread{
	private static Scenario instance;
	
	private EtatScenario etat;
	private LiaisonCarte lc;
	private LectureEntrees le;
	private EcritureSorties es;
	
	private boolean continuer = true;
	private Scenario() {
		reset();
		this.le = LectureEntrees.recupererInstance();
		this.es = EcritureSorties.recupererInstance();
		instance = this;
	}
	
	public static Scenario recupererInstance() {
		if(instance == null) {
			return new Scenario();
		}
		return instance;
	}
	
	public void reset() {
		this.etat = new EtatEteint();
	}
	
	public EtatScenario getEtat() {
		return this.etat;
	}
	
	public void run() {
		try {
			lc.connexionCarte(0);
			le.start();
			es.start();
		} catch (JK8055Exception e1) {
			System.err.println("Connexion � la carte impossible.");
			continuer = false;
		}

		while(continuer) {
			try {
				int[] entrees = le.recupererEntrees();
				
				//Entrees digitales
				if(entrees[0] == 0) {
					if(!etat.getClass().toString().equals("EtatEteint"))
						reset();
				}else {
					etat = etat.start();
				}
				if(entrees[1] == 1) etat = etat.miseEnPlace1();
				if(entrees[2] == 1) etat = etat.miseEnPlace2();
				if(entrees[3] == 1) etat = etat.sortie();
				if(entrees[4] == 1) etat = etat.defaut();
				
				//Entrees analogiques
				etat = etat.remplissage(entrees[5], entrees[6]);
				etat = etat.lecturePoids(200);
				
				
				
				sleep(50);
			} catch (JK8055Exception e) {
				System.err.println("Erreur lors de la lecture des entr�es");
				continuer = false;
			} catch (InterruptedException e) {
				System.err.println("Erreur lors de l'�xecution de l'application");
				continuer = false;
			}
		}
		extinction();
	}
	
	public void finScenario() {
		continuer = false;
	}
	
	private void extinction() {
		le.finLecture();
		es.finTache();
		try {
			lc.deconnexionCarte();
		} catch (JK8055Exception e) {
			System.err.println("Impossible de d�connecter la carte.");
		}
	}
}
