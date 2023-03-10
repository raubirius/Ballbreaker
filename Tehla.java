
import knižnica.*;
import static knižnica.Svet.náhodnéReálneČíslo;

public class Tehla extends KolíznyBlok
{
	// Farby tehiel podľa počtu úderov, ktoré vydržia:
	private final static Farba[] farby = {tmavočervená, červená,
		tmavopurpurová, tmavohnedá, tmavooranžová};


	// Mechanizmus automatickej recyklácie tehiel:

	private final static Zoznam<Tehla> tehly = new Zoznam<>();

	public static void reset()
	{
		Ballbreaker.tehly.vymaž();
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


	private Akcia akcia = () -> udri(); // (kolízna akcia)
	private int úderov = -1; // (koľko vydrží úderov)


	// Súvisiace s inicializáciou…

	private Tehla()
	{
		vypĺňajTvary();

		veľkosť(16);
		pomer(1.8);
		zaoblenie(16);

		for (int i = 0; i < 12; ++i)
			kolíznaÚsečka[i].akcia = akcia;
	}


	// Súvisiace s údermi do tehly…

	private void upravParametrePodľaÚderov()
	{
		if (úderov >= 0)
		{
			farba(farby[úderov % farby.length]);
			hrúbkaČiary(1.5 + (úderov / farby.length));
		}
		else
		{
			farba(farby[0]);
			hrúbkaČiary(0.5);
		}
	}

	// Táto metóda generuje nerovnomerné rozloženie a používa sa na
	// generovanie bonusov – 0 je najvzácnejší, 8 najbežnejší.
	private static int dajNáhodnéČíslo(int n)
	{
		double p = Math.abs(((1 + náhodnéReálneČíslo()) *
			(1 + náhodnéReálneČíslo())) - 2) / 2;
		return n - (int)(n * p) - 1;
	}

	public void udri()
	{
		Ballbreaker.overPenetračnú();

		if (úderov > 0)
		{
			--úderov;
			upravParametrePodľaÚderov();
		}
		else
		{
			if (náhodnéReálneČíslo() > 0.5)
				Ballbreaker.novýBonus(this, dajNáhodnéČíslo(9));

			Ballbreaker.tehly.odober(this);
			deaktivuj(false);

			Efekt.vytvorEfekt(this);
		}
	}


	// Ostatné…

	@Override public void kresliTvar()
	{
		super.kresliTvar();
		if (úderov >= farby.length)
		{
			hrúbkaČiary(hrúbkaČiary() * Ballbreaker.mierka);
			farba(čierna);
			kresliObdĺžnik();
		}
	}
}
