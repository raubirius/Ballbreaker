
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
				loptička.aktivuj(false);
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

	public Loptička()
	{
		veľkosť(18);
		ohranič();

		zdvihniPero();
		najväčšiaRýchlosť(20);
		rýchlosť(8, false);
		aktivuj(false);
	}

	public void pripravKolíziu()
	{
		zoznamKolíznychÚsečiek.vymaž();
	}

	@Override public void kresliTvar()
	{
		krúžok();
	}
}
