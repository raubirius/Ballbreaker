
import knižnica.*;
import static knižnica.Svet.*;

public class Plošina extends KolíznyBlok
{
	public boolean máDelo = false;

	public Plošina()
	{
		veľkosť(12);
		zaoblenie(20);
		hrúbkaČiary(2);
		// reset();
	}

	private class Akcia implements KolíznaAkcia
	{
		public void vykonaj()
		{
			Ballbreaker.ballbreaker.odchýľLoptičku(rýchlosťPosunu());
		}
	}

	public void reset()
	{
		máDelo = false;

		skočNa(0, 0);
		pomer(3.8);

		vypĺňajTvary();
		farba(tmavozelená);

		ohranič((Ballbreaker.šš - šírka()) / 2,
			(Ballbreaker.vv - výška()) / 2, PLOT);

		Akcia akcia = new Akcia();
		for (int i = 0; i < 12; ++i)
			kolíznaÚsečka[i].akcia = akcia;

		skočNa(0, Ballbreaker.y1 + 1.4 * veľkosť());
		najväčšiaRýchlosťPosunu(25);
		aktivuj(false);
	}

	@Override public void kresliTvar()
	{
		super.kresliTvar();
		if (máDelo)
		{
			hrúbkaČiary(hrúbkaČiary() * Ballbreaker.mierka);
			farba(čierna);
			kresliObdĺžnik();
			skoč(veľkosť() * Ballbreaker.mierka * 0.75);
			kruh(veľkosť() * Ballbreaker.mierka * 0.5);
		}
	}
}
