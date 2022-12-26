
import knižnica.*;

public class Tehla extends KolíznyBlok
{
	// Mechanizmus automatickej recyklácie tehiel:

	private final static Zoznam<Tehla> tehly = new Zoznam<>();

	public static void reset()
	{
		for (Tehla tehla : tehly)
			tehla.deaktivuj(false);
	}

	public static Tehla dajTehlu()
	{
		for (Tehla tehla : tehly)
		{
			if (tehla.pasívny())
			{
				tehla.aktivuj(false);
				return tehla;
			}
		}

		Tehla tehla = new Tehla();
		tehly.pridaj(tehla);
		return tehla;
	}


	private class Akcia implements KolíznaAkcia
	{
		public void vykonaj()
		{
			udri();
		}
	}

	public Tehla()
	{
		veľkosť(16);
		pomer(1.8);
		zaoblenie(16);

		for (int i = 0; i < 12; ++i)
			kolíznaÚsečka[i].akcia = new Akcia();
	}

	public void udri()
	{
		Ballbreaker.tehly.odober(this);
		deaktivuj(false);
	}
}
