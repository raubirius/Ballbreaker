
import knižnica.*;
import static knižnica.Svet.*;

// Táto trieda implementuje rozhranie Comparable, ktoré umožňuje použiť
// metódu Collections.sort na rýchle zotriedenie zoznamu kolíznych úsečiek.

public class KolíznaÚsečka implements Comparable<KolíznaÚsečka>
{
	// TEST:
	private final static GRobot kreslič = new GRobot()
	{
		@Override public void kresliTvar()
		{
			for (KolíznaÚsečka úsečka : úsečky)
			{
				if (null != úsečka.b1 && null != úsečka.b2)
				{
					if (úsečka.x1c > 0)
					{
						hrúbkaČiary(1.5);
						--úsečka.x1c;
						farba(tyrkysová);
					}
					else if (úsečka.x1c < 0)
					{
						hrúbkaČiary(2);
						++úsečka.x1c;
						farba(červená);
					}
					else
					{
						hrúbkaČiary(1);
						farba(papierová);
					}

					skočNa(úsečka.b1);
					choďNa(úsečka.b2);

					if (úsečka.x1c < 0)
					{
						hrúbkaČiary(1.5);
						farba(tyrkysová);
						skočNa(úsečka.r1t);
						choďNa(úsečka.r2t);
						skočNa(úsečka.x1t);
						choďNa(úsečka.x2t);
					}
				}
			}
		}
	};
	private final static Zoznam<KolíznaÚsečka> úsečky = new Zoznam<>();


	public Bod b1, b2;
	public KolíznaAkcia akcia = null;

	private double vzdialenosť = 0;

	public KolíznaÚsečka(double x1, double y1,
		double x2, double y2)
	{
		this(new Bod(x1, y1), new Bod(x2, y2));
	}

	public KolíznaÚsečka(Bod b1, Bod b2)
	{
		this.b1 = b1;
		this.b2 = b2;

		// TEST:
		úsečky.pridaj(this);
	}

	public KolíznaÚsečka()
	{
		this(null, null);
	}


	// TODO:
	Bod r1 = null;
	Bod r2 = null;
	Bod x1 = null;

	// TEST:
	int x1c = 0;
	Bod r1t = null;
	Bod r2t = null;
	Bod x1t = null;
	Bod x2t = null;

	public void pripravKolíziu(Loptička l)
	{
		r1 = l.poslednáPoloha();
		r2 = l.poloha();
		x1 = priesečníkÚsečiek(b1, b2, r1, r2);

		// vzdialenosť = vzdialenosťBoduOdÚsečky(l, b1, b2); // nope
		// l.zoznamKolíznychÚsečiek.pridaj(this);

		if (null != x1)
		{
			vzdialenosť = vzdialenosť(l, x1);
			l.zoznamKolíznychÚsečiek.pridaj(this);

			// TEST:
			x1c = 10;
		}
	}

	public boolean spracujKolíziu(Loptička l)
	{
		if (null != x1)
		{
			Bod x2 = najbližšíBodNaPriamke(r2, b1, b2);
			double v2 = 2 * l.vzdialenosťK(x2);
			l.otočNa(x2); l.skoč(v2);
			l.otočNa(x1); l.vľavo(180);

			x2 = l.poloha();
			l.skočNa(x1);
			l.dopredu(3);
			l.skočNa(x2);

			if (null != akcia) akcia.vykonaj();

			// TEST:
			x1c = -10;
			r1t = r1;
			r2t = r2;
			x1t = x1;
			x2t = l.poloha();

			return true;
		}
		return false;
	}


	// Implementácia rozhrania Comparable. Porovnáva vzdialenosti úsečiek od
	// poslednej loptičky, ktorá použila metódu Tehla.pripravKolíziu.
	// Vzdialenosť je potom použitá na zotriedenie zoznamu pred jeho
	// vyhodnotením.

	public int compareTo(KolíznaÚsečka iná)
	{
		return (int)(100 * (vzdialenosť - iná.vzdialenosť));
	}
}
