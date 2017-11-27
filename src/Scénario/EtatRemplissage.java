package Scénario;

public class EtatRemplissage extends EtatScenario{
	@Override
	public EtatScenario defaut() {
		return new EtatDefaut(this);
		
	}

	@Override
	public EtatScenario remplissage(int remplissage, int poids) {
		EtatScenario.remplissage = remplissage;
		EtatScenario.poids = poids;
		return remplissage < 255 ? this : new EtatDeplacement2();
	}
	
	@Override
	public int[] getSorties() {
		int[] sorties = {1, 0, 0, 0, 0, remplissage};
		return sorties;
	}
}
