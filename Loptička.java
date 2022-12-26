
import knižnica.*;

public class Loptička extends GRobot
{
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


	public final Zoznam<KolíznaÚsečka> zoznamKolíznychÚsečiek = new Zoznam<>();

	public Loptička()
	{
		veľkosť(18);

		ohranič();

		// Toto bolo upravené, aby to spolupracovalo s metódou mimoHraníc:
		/*ohranič(Svet.šírka() / 2 - veľkosť(),
			Svet.výška() / 2 - veľkosť(), ODRAZ); */

		// Poznámka: Spoľahlivejšie by bolo, keby aj kolízie so stenami sveta
		// (hracieho plátna) boli implementované prostredníctvom kolíznych
		// úsečiek (a nie cez ohraničenie), lebo pri tejto implementácii je
		// opäť riziko vzniku „bugov“. Totiž: mechanizmus ohraničenia funguje
		// samostatne a neberie do úvahy opravu, ktorá bola vykonaná.

		zdvihniPero();
		najväčšiaRýchlosť(20);
		rýchlosť(8, false);
		aktivuj(false);
	}

	@Override public void kresliTvar()
	{
		krúžok();
	}

	// Táto metóda bola prevzatá z dokumentácie
	// (http://localhost/horvath/GRobot/GRobot#mimoHran%C3%ADc-Bod:A-double-):
	/* @Override public boolean mimoHraníc(Bod[] poleBodov, double uhol)
	{
		Bod priesečník = Svet.priesečníkÚsečiek(poleBodov[0],
			poloha(), poleBodov[2], poleBodov[3]);
		if (null != priesečník)
			smer(Svet.smer(priesečník, poleBodov[1]));
		return true;
	} */
}
