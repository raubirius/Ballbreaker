
import knižnica.*;
import static knižnica.Svet.*;

public class Plošina extends KolíznyBlok
{
	public boolean máDelo = false; // (príznak dela)

	// Akcia odchýlenia loptičky podľa rýchlosti posunu plošiny:
	private Akcia akcia = () -> Ballbreaker.odchýľLoptičku(rýchlosťPosunu());


	// Súvisiace s inicializáciou…

	public Plošina()
	{
		veľkosť(12);
		zaoblenie(20);
		hrúbkaČiary(2);
		vypĺňajTvary();
		farba(tmavozelená);

		for (int i = 0; i < 12; ++i)
			kolíznaÚsečka[i].akcia = akcia;

		najväčšiaRýchlosťPosunu(25);
	}

	public void reset()
	{
		máDelo = false;

		skočNa(0, 0);
		pomer(3.8);

		ohranič((Ballbreaker.šš - šírka()) / 2,
			(Ballbreaker.vv - výška()) / 2, PLOT);

		skočNa(0, Ballbreaker.y1 + 1.4 * veľkosť());
	}


	// Súvisiace s úpravami pri zobratí bonusov…

	public void upravŠírku(int zmena)
	{
		//> 2.0 | 2.6 | 3.2 | ›3.8‹ | 4.4 | 5.0 | 5.6 | 6.2 | 6.8 | 7.4
		double pomer = pomer() + 0.6 * zmena;
		if (pomer < 2) pomer = 2; else
		if (pomer > 7.4) pomer = 7.4;
		Poloha poloha = poloha();
		poloha(stred);
		pomer(pomer);
		ohranič((Ballbreaker.šš - šírka()) / 2,
			(Ballbreaker.vv - výška()) / 2, PLOT);
		poloha(poloha);
	}


	// Ostatné…

	@Override public void kresliTvar()
	{
		super.kresliTvar();
		if (máDelo)
		{
			hrúbkaČiary(hrúbkaČiary() * Ballbreaker.mierka);
			farba(čierna);
			kresliObdĺžnik();
			skoč(veľkosť() * 0.5);
			vyplňElipsu(veľkosť() * 0.5, veľkosť() * 0.25);
		}
	}
}
