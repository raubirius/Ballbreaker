
import java.util.Collections;
import knižnica.*;
import static knižnica.Svet.*;

public class Ballbreaker extends GRobot
{
	private final Zoznam<Tehla> tehly = new Zoznam<>();
	private final Zoznam<Loptička> loptičky = new Zoznam<>();

	// Okraje obrazovky:
	private double x1, x2, y1, y2;

	// Kolízne úsečky okrajov obrazovky:
	private final KolíznaÚsečka
		kú1 = new KolíznaÚsečka(new Bod(), new Bod()),
		kú2 = new KolíznaÚsečka(new Bod(), new Bod()),
		kú3 = new KolíznaÚsečka(new Bod(), new Bod()),
		kú4 = new KolíznaÚsečka(new Bod(), new Bod());

	// TEST
	// private KolíznaAkcia ka = () -> pípni(); { kú4.akcia = ka; }

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
		if (ÚdajeUdalostí.tlačidloMyši(ĽAVÉ))
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

	@Override public void tik()
	{
		for (Loptička loptička : loptičky)
		{
			double v = loptička.veľkosť();

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

			for (int i = 0; opakuj && i < 1000; ++i)
			{
				opakuj = false;

				loptička.pripravKolíziu();
				double lx = loptička.poslednáPolohaX();
				double ly = loptička.poslednáPolohaY();

				// Tie to hranice sú do detekcie pridané len v prípade,
				// že je loptička v ich vnútri:
				if (lx >= x1v && lx <= x2v && ly >= y1v && ly <= y2v)
				{
					loptička.farba(čierna);
					kú1.pripravKolíziu(loptička);
					kú2.pripravKolíziu(loptička);
					kú3.pripravKolíziu(loptička);
					kú4.pripravKolíziu(loptička);
				}
				else loptička.farba(červená);

				for (Tehla tehla : tehly)
					tehla.pripravKolíziu(loptička);

				// Triedenie kolíznych úsečiek podľa vzdialenosti – pozri aj
				// KolíznaÚsečka.compareTo:
				Collections.sort(loptička.zoznamKolíznychÚsečiek);

				for (KolíznaÚsečka kolíznaÚsečka :
					loptička.zoznamKolíznychÚsečiek)
					if (kolíznaÚsečka.spracujKolíziu(loptička))
					{
						opakuj = true;
						break;
					}
			}

			// DEBUG:
			if (opakuj) System.err.println(
				"Prekročený limit hĺbky detekcie kolízií!");
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
