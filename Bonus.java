
import knižnica.*;

public class Bonus extends GRobot
{
	// Mechanizmus automatickej recyklácie striel:

	private final static Zoznam<Bonus> bonusy = new Zoznam<>();

	public static void reset()
	{
		Ballbreaker.bonusy.vymaž();
		for (Bonus bonus : bonusy)
			bonus.deaktivuj(false);
	}

	public static Bonus dajBonus(Poloha p) { return dajBonus(p, -1); }
	public static Bonus dajBonus(Poloha p, int n)
	{
		Bonus bonus = null;

		for (Bonus hľadaj : bonusy)
		{
			if (hľadaj.pasívny())
			{
				bonus = hľadaj;

				bonus.skočNa(p);
				bonus.aktivuj(false);
				bonus.akcia = bonus.kresli = null;

				break;
			}
		}

		if (null == bonus)
		{
			bonus = new Bonus(p);
			bonusy.pridaj(bonus);
		}

		if (n >= 0)
		{
			bonus.akcia = Ballbreaker.akcieBonusov[
				n % Ballbreaker.akcieBonusov.length];
			bonus.kresli = bonus.kreslenia[n % bonus.kreslenia.length];
		}

		return bonus;
	}


	// Pomocné atribúty bonusov (animačné, akčné)…
	private Rad pulzy = new Rad(0, 8);
	private int animuj = 0;
	private Akcia akcia = null, kresli = null;

	// Animácie bonusov:
	private Akcia kreslenia[] = {
		// 0 – delo
		() -> {
			veľkosť(veľkosť() * (0.6 + 0.05 * pulzy.dajHodnotu(animuj)));
			pomer(3.8); zaoblenie(20 * Ballbreaker.mierka);

			farba(zelená); vyplňObdĺžnik();
			farba(čierna); kresliObdĺžnik();

			odskoč(veľkosť() * Ballbreaker.mierka * 0.5);
			vyplňElipsu(veľkosť() * Ballbreaker.mierka * 0.5,
				veľkosť() * Ballbreaker.mierka * 0.25);
		},

		// 1 – zmeň všetky loptičky na penetračné
		() -> {
			veľkosť(veľkosť() * (0.6 + 0.05 * pulzy.dajHodnotu(animuj)));
			farba(tyrkysová);
			kruh();
		},

		// 2 – zmenši loptičky
		() -> {
			int fáza = animuj / 2;
			farba(žltá);
			kružnica();
			veľkosť(veľkosť() * (0.6 + 0.05 * (10 - fáza % 10)));
			kruh();
		},

		// 3 – zrýchli loptičky
		() -> {
			farba(žltá);

			preskočVľavo(36 * 0.6 * Ballbreaker.mierka);
			posuňVpravo(72 * 0.6 * Ballbreaker.mierka);
			preskočVľavo(36 * 0.6 * Ballbreaker.mierka);

			preskočVpravo((72 - Math.pow(animuj % 12, 2)) *
				0.4 * Ballbreaker.mierka);

			kruh();
		},

		// 4 – zmenši plošinu
		() -> {
			int fáza = animuj / 2;
			pomer(3.8 - 0.3 * (fáza % 8));

			veľkosť(veľkosť() * 0.6);
			zaoblenie(12 * Ballbreaker.mierka);

			farba(zelená); vyplňObdĺžnik();
		},

		// 5 – zväčši loptičky
		() -> {
			int fáza = animuj / 2;
			farba(žltá);
			kružnica();
			veľkosť(veľkosť() * (0.6 + 0.05 * (1 + fáza % 10)));
			kruh();
		},

		// 6 – spomaľ loptičky
		() -> {
			farba(žltá);

			preskočVľavo(36 * 0.6 * Ballbreaker.mierka);
			posuňVpravo(72 * 0.6 * Ballbreaker.mierka);
			preskočVľavo(36 * 0.6 * Ballbreaker.mierka);

			preskočVpravo((72 - Math.pow(11 - animuj % 12, 2)) *
				0.4 * Ballbreaker.mierka);

			kruh();
		},

		// 7 – zväčši plošinu
		() -> {
			int fáza = animuj / 2;
			pomer(3.8 + 0.3 * (fáza % 8));

			veľkosť(veľkosť() * 0.6);
			zaoblenie(12 * Ballbreaker.mierka);

			farba(zelená); vyplňObdĺžnik();
		},

		// 8 – nová loptička
		() -> {
			veľkosť(veľkosť() * (0.6 + 0.05 * pulzy.dajHodnotu(animuj)));
			farba(žltá);
			kruh();
		},
	};

	// Vykonanie akcie zobratia:
	public void akcia() { if (null != akcia) akcia.vykonaj(); }

	private Bonus(Poloha p) // (konštruktor)
	{
		ohranič(Ballbreaker.šš / 2, Ballbreaker.vv / 2);

		skočNa(p);
		smer(juh);
		hrúbkaČiary(1.5);
		farba(snehová);
		priehľadnosť(0.5);

		rýchlosť(5, false);
		zdvihniPero();
		aktivuj(false);
	}

	@Override public void kresliTvar() // (kreslenie s animáciou)
	{
		skočNa(polohaX() * Ballbreaker.mierka,
			polohaY() * Ballbreaker.mierka);
		veľkosť(veľkosť() * Ballbreaker.mierka);
		hrúbkaČiary(hrúbkaČiary() * Ballbreaker.mierka);
		++animuj;

		if (null != kresli)
			kresli.vykonaj();
		else
			krúžok();
	}


	// Ostatné…

	@Override public void mimoHraníc()
	{ Ballbreaker.deaktivujBonus(this); }

	@Override public void deaktivácia() { skry(); }
	@Override public void aktivácia() { zobraz(); }
}
