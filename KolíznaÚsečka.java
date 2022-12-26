
import knižnica.*;
import static knižnica.Svet.*;

public class KolíznaÚsečka
{
	public Bod b1, b2;

	public KolíznaÚsečka(double x1, double y1,
		double x2, double y2)
	{
		b1 = new Bod(x1, y1);
		b2 = new Bod(x2, y2);
	}

	public KolíznaÚsečka(Bod b1, Bod b2)
	{
		this.b1 = b1;
		this.b2 = b2;
	}

	public KolíznaÚsečka()
	{
		b1 = b2 = null;
	}

	// Ladenie:
	// public Bod r1, r2, x1, x2;

	public boolean spracujKolíziu(GRobot r)
	{
		Bod r1 = r.poslednáPoloha();
		Bod r2 = r.poloha();
		Bod x1 = priesečníkÚsečiek(b1, b2, r1, r2);
		if (null != x1)
		{
			Bod x2 = najbližšíBodNaPriamke(r2, b1, b2);
			double vv = 2 * r.vzdialenosťK(x2);
			r.otočNa(x2); r.skoč(vv);
			r.otočNa(x1); r.vľavo(180);
			return true;
		}
		return false;
	}
}
