
import knižnica.*;
import static knižnica.Svet.*;

public class Ballbreaker extends GRobot
{
	private Tehla tehla = new Tehla();
	private Loptička loptička = new Loptička();

	/*KolíznaÚsečka ku = new KolíznaÚsečka(-125, 250, 75, -225);
	GRobot r = new GRobot(); // kreslič*/

	private Ballbreaker()
	{
		super(300, 400);
		/* r.skry();
		veľkosť(5);
		skočNa(50, -30);
		uhol(150);
		r.skočNa(ku.b1);
		r.choďNa(ku.b2);
		dopredu(150);

		ku.spracujKolíziu(this);
		r.skočNa(ku.x1);
		r.choďNa(this); */

		loptička.ohranič();
		loptička.rýchlosť(8, false);
		loptička.aktivuj(false);

		tehla.skoč(100);
		tehla.vpravo(20);
		// tehla.spracujKolíziu(loptička);
	}

	@Override public void kresliTvar()
	{
		/* kruh();
		dopredu(15);
		nUholník(5, 3); */
	}

	@Override public void klik()
	{
		if (ÚdajeUdalostí.tlačidloMyši(ĽAVÉ))
		{
			tehla.skoč(náhodnéCeléČíslo(-5, 5), náhodnéCeléČíslo(-5, 5));
			tehla.vľavo(náhodnéCeléČíslo(-5, 5));
			tehla.spracujKolíziu(loptička);
			žiadajPrekreslenie();
		}
		else
		{
			loptička.otočNaMyš();
		}
	}

	@Override public void tik()
	{
		tehla.spracujKolíziu(loptička);
		if (neboloPrekreslené()) prekresli();
	}

	public static void main(String[] args)
	{
		použiKonfiguráciu("Ballbreaker.cfg"); Svet.skry(); nekresli();
		try { new Ballbreaker(); } catch (Throwable t) { t.printStackTrace(); }
		if (prvéSpustenie()) { zbaľ(); vystreď(); }
		Svet.zobraz(); spustiČasovač();
	}
}
