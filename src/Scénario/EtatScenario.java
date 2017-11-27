package Scénario;

public abstract class EtatScenario {
	public static int remplissage = 0;
	public static int poids = 0;
	
	public EtatScenario start() {
		return this;
	}
	public EtatScenario miseEnPlace1() {
		return this;
	}
	public EtatScenario remplissage(int remplissage, int poids) {
		return this;
	}
	public EtatScenario miseEnPlace2() {
		return this;
	}
	public EtatScenario lecturePoids(int poidsMax) {
		return this;
	}
	public EtatScenario defaut() {
		return this;
	}
	public EtatScenario sortie() {
		return this;
	}
	
	public abstract int[] getSorties();
}
