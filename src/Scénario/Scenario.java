package Scénario;

import net.sf.libk8055.jk8055.JK8055Exception;
import projet.carte.EcritureSorties;
import projet.carte.LectureEntrees;

public class Scenario extends Thread{
	private static Scenario instance;
	
	private EtatScenario etat;
	private LectureEntrees le;
	private EcritureSorties es;
	
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
		this.etat = new EtatDepart();
	}
	
	public EtatScenario getEtat() {
		return this.etat;
	}
	
	public void run() {
		boolean continuer = true;
		le.start();
		es.start();
		
		while(continuer) {
			try {
				int[] entrees = le.recupererEntrees();
				
				//Entrees digitales
				if(entrees[0] == 1) etat = etat.start();
				if(entrees[1] == 1) etat = etat.miseEnPlace1();
				if(entrees[2] == 1) etat = etat.miseEnPlace2();
				if(entrees[3] == 1) etat = etat.sortie();
				if(entrees[4] == 1) etat = etat.defaut();
				
				//Entrees analogiques
				etat = etat.remplissage(entrees[5]);
				etat = etat.lecturePoids(entrees[6]);
				
				sleep(50);
			} catch (JK8055Exception e) {
				System.err.println("Erreur lors de la lecture des entrées");
				continuer = false;
			} catch (InterruptedException e) {
				System.err.println("Erreur lors de l'éxecution de l'application");
				continuer = false;
			}
		}
		le.finLecture();
		es.finTache();
		
		
	}
}
