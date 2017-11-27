package Scénario;

public class EtatDefaut extends EtatScenario{
	private EtatScenario precedent;
	
	public EtatDefaut(EtatScenario precedent) {
		this.precedent = precedent;
	}
	
	@Override
	public EtatScenario defaut() {
		return precedent;
		
	}

	@Override
	public int[] getSorties() {
		int[] sorties = {0, 0, 0, 0, 1, remplissage};
		return sorties;
	}

}
