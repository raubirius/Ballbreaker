
import knižnica.*;

public class Strela extends GRobot
{
	// Mechanizmus automatickej recyklácie striel:

	private final static Zoznam<Strela> strely = new Zoznam<>();

	public static void reset()
	{
		for (Strela strela : strely)
			strela.deaktivuj(false);
	}

	public static Strela dajStrelu()
	{
		for (Strela strela : strely)
		{
			if (strela.pasívny())
			{
				strela.aktivuj(false);
				return strela;
			}
		}

		Strela strela = new Strela();
		strely.pridaj(strela);
		return strela;
	}


	// Súvisiace s inicializáciou…

	public Strela()
	{
		veľkosť(16);
		ohranič(
			(Ballbreaker.šš + veľkosť()) / 2,
			(Ballbreaker.vv + veľkosť()) / 2);

		hrúbkaČiary(5);
		farba(svetložltá.svetlejšia());

		rýchlosť(12, false);
		zdvihniPero();
		aktivuj(false);
	}


	// Ostatné…

	@Override public void mimoHraníc()
	{
		Ballbreaker.ballbreaker.deaktivujStrelu(this);
	}

	@Override public void kresliTvar()
	{
		skočNa(polohaX() * Ballbreaker.mierka, polohaY() * Ballbreaker.mierka);
		hrúbkaČiary(hrúbkaČiary() * Ballbreaker.mierka);
		vzad(veľkosť() * Ballbreaker.mierka);
	}

	@Override public void deaktivácia() { skry(); }
	@Override public void aktivácia() { zobraz(); }
}
