
import knižnica.*;
import static knižnica.Svet.*;

public class Ballbreaker extends GRobot
{
	/*private Tehla tehla = new Tehla();
	private Loptička loptička = 	new Loptička();*/

	KolíznaÚsečka ku = new KolíznaÚsečka(-125, 250, 75, -225);
	GRobot r = new GRobot(); // kreslič

	private Ballbreaker()
	{
		r.skry();
		veľkosť(5);
		skočNa(50, -20);
		uhol(150);
		r.skočNa(ku.b1);
		r.choďNa(ku.b2);
		dopredu(150);

		ku.spracujKolíziu(this);
		r.skočNa(ku.x1);
		r.choďNa(this);
	}

	@Override public void kresliTvar()
	{
		kruh();
		dopredu(15);
		nUholník(5, 3);
	}

	@Override public void tik()
	{
		if (neboloPrekreslené()) prekresli();
	}

	public static void main(String[] args)
	{
		// použiKonfiguráciu("Ballbreaker.cfg");
		Svet.skry(); nekresli();
		try { new Ballbreaker(); } catch (Throwable t) { t.printStackTrace(); }
		if (prvéSpustenie()) { zbaľ(); vystreď(); }
		Svet.zobraz(); spustiČasovač();
	}
}
