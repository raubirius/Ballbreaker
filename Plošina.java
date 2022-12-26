
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

	public void reset()
	{
		skočNa(0, 0);
		pomer(3.8);

		ohranič((Plátno.šírka() - šírka()) / 2,
			(Plátno.výška() - výška()) / 2, PLOT);

		skočNa(0, najmenšieY() + 1.4 * veľkosť());
		aktivuj(false);
	}
}
