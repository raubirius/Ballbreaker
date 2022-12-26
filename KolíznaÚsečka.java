
import knižnica.*;
import static knižnica.Svet.*;

// Táto trieda implementuje rozhranie Comparable, ktoré umožňuje použiť
// metódu Collections.sort na rýchle zotriedenie zoznamu kolíznych úsečiek.

public class KolíznaÚsečka implements Comparable<KolíznaÚsečka>
{
	public Bod b1, b2;
	public KolíznaAkcia akcia = null;

	private double vzdialenosť = 0;

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


	public void pripravKolíziu(Loptička l)
	{
		vzdialenosť = vzdialenosťBoduOdÚsečky(l, b1, b2);
		l.zoznamKolíznychÚsečiek.pridaj(this);
	}

	public boolean spracujKolíziu(Loptička l)
	{
		Bod r1 = l.poslednáPoloha();
		Bod r2 = l.poloha();
		Bod x1 = priesečníkÚsečiek(b1, b2, r1, r2);
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
