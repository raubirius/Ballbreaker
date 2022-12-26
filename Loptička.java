
import knižnica.*;

public class Loptička extends GRobot
{
	// Mechanizmus automatickej recyklácie loptičiek:

	private final static Zoznam<Loptička> loptičky = new Zoznam<>();

	public static void reset()
	{
		for (Loptička loptička : loptičky)
			loptička.deaktivuj(false);
	}

	public static Loptička dajLoptičku()
	{
		for (Loptička loptička : loptičky)
		{
			if (loptička.pasívny())
			{
				loptička.počiatočnéVlastnosti();
				return loptička;
			}
		}

		Loptička loptička = new Loptička();
		loptičky.pridaj(loptička);
		return loptička;
	}


	// Tento verejný zoznam je viacnásobne používaný pri každom tiku časovača.
	// Je čistený pred každým cyklom detekcie kolízií, ktorých môže byť
	// v rámci jedného tiku viac. Potom ho prípravné metódy detekcie kolízií
	// automaticky plnia. Následne je zotriedený a nakoniec sú vykonané testy
	// kolízií.
	public final Zoznam<KolíznaÚsečka> zoznamKolíznychÚsečiek = new Zoznam<>();

	public double poslednýSmer = 90;

	private boolean penetračná;

	public Loptička()
	{
		najväčšiaRýchlosť(20);
		ohranič(
			(Ballbreaker.šš + najväčšiaRýchlosť()) / 2,
			(Ballbreaker.vv + najväčšiaRýchlosť()) / 2);

		zdvihniPero();
		vypĺňajTvary();
		počiatočnéVlastnosti();
	}

	private void počiatočnéVlastnosti()
	{
		rýchlosť(12, false);
		veľkosť(8);
		aktivuj(false);
		penetračná(false);
	}

	public boolean penetračná() { return penetračná; }

	public void penetračná(boolean penetračná)
	{
		this.penetračná = penetračná;
		farba(penetračná ? tmavotyrkysová : tmavožltá);
	}

	public void pripravKolíziu()
	{
		zoznamKolíznychÚsečiek.vymaž();
	}

	@Override public void kresliTvar() { krúžok(); }
	@Override public void deaktivácia() { skry(); }
	@Override public void aktivácia() { zobraz(); }
}
