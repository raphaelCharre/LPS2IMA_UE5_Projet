package Scénario;

public class EtatDefaut extends EtatScenario{
	private EtatScenario precedent;
	
	public EtatDefaut(EtatScenario precedent) {
		this.precedent = precedent;
	}
	
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
		return precedent;
		
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
