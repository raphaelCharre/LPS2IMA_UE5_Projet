package Scénario;

public abstract class EtatScenario {
	public static int remplissage = 0;
	public abstract EtatScenario start();
	public abstract EtatScenario miseEnPlace1();
	public abstract EtatScenario remplissage(int remplissage);
	public abstract EtatScenario miseEnPlace2();
	public abstract EtatScenario lecturePoids(int poidsMax);
	public abstract EtatScenario defaut();
	public abstract EtatScenario sortie();
}
