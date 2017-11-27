package Scénario;

public class EtatLecturePoids extends EtatScenario{

	@Override
	public EtatScenario start() {
		return this;
		
	}

	@Override
	public EtatScenario miseEnPlace1() {
		return this;
		
	}

	@Override
	public EtatScenario miseEnPlace2() {
		return this;		
	}


	@Override
	public EtatScenario defaut() {
		return new EtatDefaut(this);
		
	}

	@Override
	public EtatScenario remplissage(int remplissage) {
		return this;
	}

	@Override
	public EtatScenario lecturePoids(int poidsMax) {
		return remplissage <= poidsMax ? new EtatValide() : new EtatDepassement();
	}

	@Override
	public EtatScenario sortie() {
		return this;
	}

}
