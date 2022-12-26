
import knižnica.*;

public class Efekt extends GRobot
{
	// Mechanizmus automatickej recyklácie efektov:

	private final static Zoznam<Efekt> efekty = new Zoznam<>();

	public static void reset()
	{
		for (Efekt efekt : efekty)
			efekt.deaktivuj(false);
	}

	public static Efekt vytvorEfekt(GRobot r)
	{
		for (Efekt efekt : efekty)
		{
			if (efekt.pasívny())
			{
				efekt.naštartuj(r);
				return efekt;
			}
		}

		Efekt efekt = new Efekt(r);
		efekty.pridaj(efekt);
		return efekt;
	}


	// Pomocný príznak kreslenia vlastného tvaru:
	private Farba farbaČiary = null;


	// Súvisiace s inicializáciou…

	private Efekt(GRobot r)
	{
		vypĺňajTvary();
		naštartuj(r);
	}

	private void naštartuj(GRobot r)
	{
		zaoblenie(r.zaoblenieX(), r.zaoblenieY());
		poloha(r);
		smer(r);
		rozmery(r);
		farba(r);
		aktivuj(false);
	}


	// Ostatné…

	@Override public void kresliTvar()
	{
		zaoblenie(zaoblenieX() * Ballbreaker.mierka,
			zaoblenieY() * Ballbreaker.mierka);
		skočNa(polohaX() * Ballbreaker.mierka,
			polohaY() * Ballbreaker.mierka);
		veľkosť(veľkosť() * Ballbreaker.mierka);
		obdĺžnik();

		if (null != farbaČiary)
		{
			hrúbkaČiary(hrúbkaČiary() * Ballbreaker.mierka);
			farba(farbaČiary);
			kresliObdĺžnik();
		}
	}

	@Override public void aktivita()
	{
		if (priehľadnosť() > 0.1)
			priehľadnosť(priehľadnosť() - 0.1);
		else
			deaktivuj();
	}

	@Override public void deaktivácia() { skry(); }
	@Override public void aktivácia() { zobraz(); priehľadnosť(1); }
}
