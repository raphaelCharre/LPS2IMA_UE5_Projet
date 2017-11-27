package Scénario;

public class EtatDeplacement1 extends EtatScenario{

	@Override
	public EtatScenario miseEnPlace1() {
		return new EtatRemplissage();
		
	}

	@Override
	public EtatScenario defaut() {
		return new EtatDefaut(this);
	}

	@Override
	public int[] getSorties() {
		int[] sorties = {1, 0, 0, 0, 0, remplissage};
		return sorties;
	}
}
