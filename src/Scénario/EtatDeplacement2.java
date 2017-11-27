package Scénario;

public class EtatDeplacement2 extends EtatScenario{

	@Override
	public EtatScenario miseEnPlace2() {
		return new EtatLecturePoids();
		
	}

	@Override
	public EtatScenario defaut() {
		return new EtatDefaut(this);
	}

	@Override
	public int[] getSorties() {
		int[] sorties = {1, 0, 0, 1, 0, remplissage};
		return sorties;
	}
}