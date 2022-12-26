
import knižnica.*;
import static knižnica.Svet.*;

// [*kú*]
// Teraz táto trieda implementuje rozhranie Comparable, ktoré umožňuje použiť
// metódu Collections.sort na rýchle zotriedenie zoznamu kolíznych úsečiek.

public class KolíznaÚsečka implements Comparable<KolíznaÚsečka>
{
	public Bod b1, b2;
	private double v = 0;

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

	// [*kú*]
	// Táto metóda je nová a jej cieľom pripraviť „pôdu“ na detektciu kolízií:
	// Atribút v si bude pamätať aktuálnu vzdialenosť loptičky od tejto
	// kolíznej úsečky, čo bude použité na zotriedenie zoznamu kolíznych
	// úsečiek.

	public void pripravKolíziu(Loptička l)
	{
		v = vzdialenosťBoduOdÚsečky(l, b1, b2);
		l.zkú.pridaj(this);
	}

	public boolean spracujKolíziu(Loptička l)
	{
		Bod r1 = l.poslednáPoloha();
		Bod r2 = l.poloha();
		Bod x1 = priesečníkÚsečiek(b1, b2, r1, r2);
		if (null != x1)
		{
			Bod x2 = najbližšíBodNaPriamke(r2, b1, b2);
			double vv = 2 * l.vzdialenosťK(x2);
			l.otočNa(x2); l.skoč(vv);
			l.otočNa(x1); l.vľavo(180);
			return true;
		}
		return false;
	}

	// [*kú*]
	// Toto je implementácia rozhrania Comparable. Porovnáva vzdialenosti
	// úsečiek od loptičky, čo je použité na zotriedenie zoznamu pred jeho
	// vyhodnotením…

	public int compareTo(KolíznaÚsečka iná)
	{
		return (int)(100 * (v - iná.v));
	}
}
