
import knižnica.*;
import static knižnica.Svet.*;

public class Plošina extends KolíznyBlok
{
	public Plošina()
	{
		veľkosť(12);
		zaoblenie(20);
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
}
