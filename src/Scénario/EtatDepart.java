package Scénario;

public class EtatDepart extends EtatScenario{
	@Override
	public EtatScenario start() {
		return new EtatDeplacement1();
		
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
		return this;
	}

	@Override
	public EtatScenario sortie() {
		return this;
	}

}
