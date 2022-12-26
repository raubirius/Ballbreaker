
import knižnica.*;

public class Tehla extends KolíznyBlok
{
	private final static Farba[] farby = {tmavočervená, červená,
		tmavopurpurová, tmavohnedá, tmavooranžová};

	// Mechanizmus automatickej recyklácie tehiel:

	private final static Zoznam<Tehla> tehly = new Zoznam<>();

	public static void reset()
	{
		for (Tehla tehla : tehly)
			tehla.deaktivuj(false);
	}

	public static Tehla dajTehlu(int úderov)
	{
		for (Tehla tehla : tehly)
		{
			if (tehla.pasívny())
			{
				tehla.aktivuj(false);
				tehla.úderov = úderov;
				tehla.upravParametrePodľaÚderov();
				return tehla;
			}
		}

		Tehla tehla = new Tehla();
		tehla.úderov = úderov;
		tehla.upravParametrePodľaÚderov();
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

	private int úderov = -1;

	public Tehla()
	{
		vypĺňajTvary();

		veľkosť(16);
		pomer(1.8);
		zaoblenie(16);

		Akcia akcia = new Akcia();
		for (int i = 0; i < 12; ++i)
			kolíznaÚsečka[i].akcia = akcia;
	}

	private void upravParametrePodľaÚderov()
	{
		farba(farby[úderov % farby.length]);
		if (úderov >= farby.length)
			hrúbkaČiary(1.5 + (úderov / farby.length));
	}

	public void udri()
	{
		Ballbreaker.ballbreaker.overPenetračnú();

		if (úderov > 0)
		{
			--úderov;
			upravParametrePodľaÚderov();
		}
		else
		{
			Ballbreaker.tehly.odober(this);
			deaktivuj(false);
		}
	}

	@Override public void kresliTvar()
	{
		super.kresliTvar();
		if (úderov >= farby.length)
		{
			farba(čierna);
			kresliObdĺžnik();
		}
	}
}
