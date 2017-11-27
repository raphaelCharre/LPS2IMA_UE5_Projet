package Scénario;

public class EtatValide extends EtatScenario{
	@Override
	public EtatScenario defaut() {
		return new EtatDefaut(this);
	}

	@Override
	public EtatScenario sortie() {
		return new EtatEteint();
	}
	
	@Override
	public int[] getSorties() {
		int[] sorties = {0, 1, 0, 1, 0, remplissage};
		return sorties;
	}

}
