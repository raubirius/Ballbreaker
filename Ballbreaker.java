
import java.util.Collections;
import knižnica.*;
import static knižnica.Kláves.*;
import static knižnica.Svet.*;
import static knižnica.ÚdajeUdalostí.*;

public class Ballbreaker extends GRobot
{
	public final static Zoznam<Tehla> tehly = new Zoznam<>();

	private final Zoznam<Loptička> loptičky = new Zoznam<>();
	private final Plošina plošina = new Plošina();

	// Okraje obrazovky:
	private double x1, x2, y1, y2;

	// Kolízne úsečky okrajov obrazovky:
	private final KolíznaÚsečka
		kú1 = new KolíznaÚsečka(new Bod(), new Bod()),
		kú2 = new KolíznaÚsečka(new Bod(), new Bod()),
		kú3 = new KolíznaÚsečka(new Bod(), new Bod()),
		kú4 = new KolíznaÚsečka(new Bod(), new Bod());

	// Do tohto atribútu sa počas testovania kolízií vždy uloží aktuálne
	// testovaná loptička. Je to využité hlavne v kolíznej úsečke spodného
	// okraja.
	private Loptička testovanáLoptička = null;

	// Akcia spodného okraja – deaktivuje loptičku, ktorá je uložená v atribúte
	// testovanáLoptička:
	private KolíznaAkcia spodnýOkraj = () ->
	{
		if (null != testovanáLoptička)
			testovanáLoptička.deaktivuj(false);
	};{
		kú4.akcia = spodnýOkraj;
	}

	private Ballbreaker()
	{
		super(500, 400);

		// Hranice hracej plochy:
		x1 = najmenšieX();
		x2 = najväčšieX();
		y1 = najmenšieY();
		y2 = najväčšieY();

		// TEST:
		/*x1 += 10;
		x2 -= 10;
		y1 += 10;
		y2 -= 10;*/

		reset();
	}

	public void reset()
	{
		plošina.reset();

		Loptička.reset();
		loptičky.vymaž();
		for (int i = 0; i < 3; ++i)
		{
			Loptička loptička = Loptička.dajLoptičku();
			loptička.skočNa(stred);
			loptička.odskoč();
			loptička.smer(náhodnéCeléČíslo(260, 280));
			loptičky.pridaj(loptička);
		}

		Tehla.reset();
		tehly.vymaž();
		for (int i = 0; i < 5; i += 1)
		{
			Tehla tehla = Tehla.dajTehlu();
			tehla.skočNa(-170 + i * 85, 140 - (45 * (i % 2)));
			tehly.pridaj(tehla);
		}
	}

	@Override public void kresliTvar()
	{
		// TODO: skóre a podobne
	}

	@Override public void klik()
	{
		// TESTY
		if (tlačidloMyši(ĽAVÉ))
		{
			for (Loptička loptička : loptičky)
			{
				loptička.skočNaMyš();
				break;
			}
		}
		else
		{
			for (Loptička loptička : loptičky)
			{
				loptička.otočNaMyš();
				break;
			}
		}
	}

	@Override public void stlačenieKlávesu()
	{
		switch (kláves())
		{
		case VĽAVO: plošina.rýchlosťPosunu(-15); break;
		case VPRAVO: plošina.rýchlosťPosunu(15); break;
		}
	}

	@Override public void uvoľnenieKlávesu()
	{
		switch (kláves())
		{
		case VĽAVO: case VPRAVO: plošina.rýchlosťPosunu(0); break;
		}
	}

	@Override public void tik()
	{
		for (int i = 0; i < loptičky.veľkosť(); ++i)
		{
			testovanáLoptička = loptičky.daj(i);
			double v = testovanáLoptička.veľkosť();

			// Výpočet súradníc, ktoré sú považované za vnútrajšok plochy pre
			// túto loptičku – využíva sa to nielen na prípravu úsečiek, ale
			// aj na ich zaradenie do detekcie v ďalšom cykle nižšie:
			double x1v = x1 + v;
			double x2v = x2 - v;
			double y1v = y1 + v;
			double y2v = y2 - v;

			// Príprava kolíznych úsečiek okrajov hracej plochy:
			kú1.b1.poloha(x1v, y1);
			kú1.b2.poloha(x1v, y2);

			kú2.b1.poloha(x2v, y1);
			kú2.b2.poloha(x2v, y2);

			kú3.b1.poloha(x1, y2v);
			kú3.b2.poloha(x2, y2v);

			kú4.b1.poloha(x1, y1v);
			kú4.b2.poloha(x2, y1v);

			boolean opakuj = true;

			for (int j = 0; opakuj && testovanáLoptička.aktívny() &&
				j < 1000; ++j)
			{
				opakuj = false;

				testovanáLoptička.pripravKolíziu();
				double lx = testovanáLoptička.poslednáPolohaX();
				double ly = testovanáLoptička.poslednáPolohaY();

				// Tie to hranice sú do detekcie pridané len v prípade,
				// že je loptička v ich vnútri:
				if (lx >= x1v && lx <= x2v && ly >= y1v && ly <= y2v)
				{
					// testovanáLoptička.farba(čierna); // TEST
					kú1.pripravKolíziu(testovanáLoptička);
					kú2.pripravKolíziu(testovanáLoptička);
					kú3.pripravKolíziu(testovanáLoptička);
					kú4.pripravKolíziu(testovanáLoptička);
				}
				// else testovanáLoptička.farba(červená); // TEST

				plošina.pripravKolíziu(testovanáLoptička);

				for (Tehla tehla : tehly)
					tehla.pripravKolíziu(testovanáLoptička);

				// Triedenie kolíznych úsečiek podľa vzdialenosti – pozri aj
				// KolíznaÚsečka.compareTo:
				Collections.sort(testovanáLoptička.zoznamKolíznychÚsečiek);

				for (KolíznaÚsečka kolíznaÚsečka :
					testovanáLoptička.zoznamKolíznychÚsečiek)
					if (kolíznaÚsečka.spracujKolíziu(testovanáLoptička))
					{
						opakuj = true;
						break;
					}
			}

			// DEBUG:
			if (opakuj && testovanáLoptička.aktívny()) System.err.println(
				"Prekročený limit hĺbky detekcie kolízií!");
		}

		for (int i = 0; i < loptičky.veľkosť(); ++i)
		{
			Loptička loptička = loptičky.daj(i);
			if (loptička.neaktívny())
			{
				loptičky.odober(loptička);
				--i;
			}
		}

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
