package Scénario;

public class EtatEteint extends EtatScenario{
	public EtatEteint() {
		remplissage = 0;
		poids = 0;
	}
	
	@Override
	public EtatScenario start() {
		return new EtatDeplacement1();
		
	}
	
	@Override
	public EtatScenario defaut() {
		return new EtatDefaut(this);
	}
	
	@Override
	public int[] getSorties() {
		int[] sorties = {0, 0, 0, 0, 0, remplissage};
		return sorties;
	}
}
