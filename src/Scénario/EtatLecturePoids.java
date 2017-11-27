package Scénario;

public class EtatLecturePoids extends EtatScenario{
	@Override
	public EtatScenario defaut() {
		return new EtatDefaut(this);
		
	}
	@Override
	public EtatScenario lecturePoids(int poidsMax) {
		return remplissage <= poidsMax ? new EtatValide() : new EtatDepassement();
	}
	
	@Override
	public int[] getSorties() {
		int[] sorties = {0, 1, 0, 0, 0, remplissage};
		return sorties;
	}

}
