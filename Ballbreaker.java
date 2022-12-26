
import java.util.Collections;
import knižnica.*;
import static knižnica.Kláves.*;
import static knižnica.Svet.*;
import static knižnica.ÚdajeUdalostí.*;

// Prvá fáza vývoja:
// ✓ návrh a výroba základných objektov (tehly, loptičky) a tried (hlavná
//   trieda, rodičovská trieda kolíznych objektov…)
// ~ kolízie objektov (stále nastávajú úniky – všimol som si, že podobné sa
//   diali aj v jednej implementácii, ktorú som hrával mnoho rokov dozadu;
//   tým to nechcem ospravedlňovať; len, že to nie je také ľahké)
// ✓ zoznamy objektov
// • (neustála revízia a testovanie)
// ———
// Druhá fáza vývoja:
// ✓ rozšírenie o ďalšie objekty a triedy (plošina, strela, bonus…)
// ✓ škálovateľnosť grafiky podľa okna
// ✓ penetračná loptička
// ✓ delo (prídavok plošiny)
// ✓ zmena veľkosti plošiny
// ✓ zmena veľkosti loptičiek
// ✓ zmena rýchlosti loptičiek
// ✓ bonusy (pridanie loptičiek, zmena veľkosti plošiny, zmena veľkosti
//   loptičiek, zmena rýchlosti loptičiek, penetračné loptičky, delo)
// ———
// TODO – na dokončenie:
// • steny
// • skóre
// • výhra/prehra
// • levely

public class Ballbreaker extends GRobot
{
	// Aktívne objekty hry:
	public final static Zoznam<Tehla> tehly = new Zoznam<>();
	private final Zoznam<Loptička> loptičky = new Zoznam<>();
	private final Zoznam<Strela> strely = new Zoznam<>();
	private final Zoznam<Bonus> bonusy = new Zoznam<>();
	private final Plošina plošina = new Plošina();

	// Výpočtové rozmery hracej plochy:
	public final static double šš = 800; // šírka
	public final static double vv = 500; // výška

	// Výpočtové hranice hracej plochy:
	public final static double x1 =  -šš / 2;      // najmenšie x
	public final static double x2 =  (šš / 2) - 1; // najväčšie x
	public final static double y1 = (-vv / 2) + 1; // najmenšie y
	public final static double y2 =   vv / 2;      // najväčšie y

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
	private Akcia spodnýOkraj = () ->
	{
		if (null != testovanáLoptička)
			testovanáLoptička.deaktivuj(false);
	};{
		kú4.akcia = spodnýOkraj;
	}

	// Hlavná inštancia hry a globálna mierka:
	public static Ballbreaker ballbreaker; // (keby mohla byť final, bola by
		// final, ale nedá sa to urobiť, pretože túto inštanciu je nevyhnutné
		// inicializovať čo najskôr (v podstate je to druhý príkaz
		// konštruktora), inak by vznikali chyby počas inicializácie;
		// keďže zároveň musí byť statická, kompilátor nedokáže rozpoznať,
		// že priradenie by sa vykonalo len raz (nemá tú garanciu), tak
		// vyhlasuje pri príznaku final chybu)
	public static double mierka = 1.0;


	// Súvisiace s inicializáciou…

	private Ballbreaker()
	{
		super(šírkaZariadenia(), výškaZariadenia());
		ballbreaker = this;

		farbaPozadia(antracitová);
		farba(tmavomodrá);
		reset();
	}

	public void reset()
	{
		plošina.reset();

		Strela.reset();
		strely.vymaž();

		Bonus.reset();
		bonusy.vymaž();

		Loptička.reset();
		loptičky.vymaž();
		for (int i = 0; i < 3; ++i)
			nováLoptička();

		Tehla.reset();
		tehly.vymaž();

		for (int y = 0; y < 5; ++y)
		{
			int yy = Math.abs(y % 2);
			for (int x = -5 + yy; x <= 6; ++x)
			{
				Tehla tehla = Tehla.dajTehlu(4 - y);

				double tš = tehla.šírka(), tv = tehla.výška();
				double dx = -(tš / 2), dy = y2 - tv;
				double mx = 6 + tš, my = 6 + tv;

				tehla.skočNa(
					dx + x * mx - (tš * yy) / 2,
					dy - y * my);
				tehly.pridaj(tehla);
			}
		}
	}


	// Tvorba objektov hry – používané pri inicializácii a bonusoch…

	public Loptička nováLoptička()
	{
		Loptička loptička = Loptička.dajLoptičku();
		loptička.skočNa(plošina.polohaX(), 0);
		loptička.smer(90);
		loptička.odskoč();
		loptička.smer(náhodnéCeléČíslo(260, 280));
		loptičky.pridaj(loptička);
		return loptička;
	}

	public Strela nováStrela()
	{
		Strela strela = Strela.dajStrelu();
		strela.skočNa(plošina);
		strela.smer(plošina);
		strela.skoč();
		strela.vľavo(náhodnéCeléČíslo(-3, 3));
		strely.pridaj(strela);
		return strela;
	}

	public Bonus novýBonus(Poloha p, int n)
	{
		Bonus bonus = Bonus.dajBonus(p, n);
		bonusy.pridaj(bonus);
		return bonus;
	}

	// Akcie pri zobratí bonusov:
	public Akcia akcieBonusov[] = {
		// 0 – delo
		() -> plošina.máDelo = true,

		// 1 – zmeň všetky loptičky na penetračné
		() -> {
			for (Loptička loptička : loptičky)
				loptička.penetračná(true);
		},

		// 2 – zmenši loptičky
		() -> {
			for (Loptička loptička : loptičky)
				loptička.upravVeľkosť(-1);
		},

		// 3 – zrýchli loptičky
		() -> {
			for (Loptička loptička : loptičky)
				loptička.upravRýchlosť(1);
		},

		// 4 – zmenši plošinu
		() -> plošina.upravŠírku(-1),

		// 5 – zväčši loptičky
		() -> {
			for (Loptička loptička : loptičky)
				loptička.upravVeľkosť(1);
		},

		// 6 – spomaľ loptičky
		() -> {
			for (Loptička loptička : loptičky)
				loptička.upravRýchlosť(-1);
		},

		// 7 – zväčši plošinu
		() -> plošina.upravŠírku(1),

		// 8 – nová loptička
		() -> nováLoptička(),
	};

	private void čít()
	{
		zastavČasovač();
		String čít = zadajReťazec("čít");
		if (null != čít)
		{
			čít = čít.trim().toLowerCase();
			boolean ibaJedna = true, pridaj = true;
			{
				boolean opakuj = true;
				while (opakuj)
				{
					opakuj = false;
					if (čít.endsWith("v") || čít.endsWith("a"))
					{
						ibaJedna = false;
						čít = čít.substring(0, čít.length() - 1);
						opakuj = true;
					}
					else if (čít.endsWith("+"))
					{
						pridaj = true;
						čít = čít.substring(0, čít.length() - 1);
						opakuj = true;
					}
					else if (čít.endsWith("-"))
					{
						pridaj = false;
						čít = čít.substring(0, čít.length() - 1);
						opakuj = true;
					}
				}
			}

			switch (čít)
			{
			case "b0": case "b1": case "b2": case "b3": case "b4":
			case "b5": case "b6": case "b7": case "b8":
				int ktorý = Integer.parseInt(čít.substring(1));
				novýBonus(stred, ktorý);
				break;

			case "d":
				plošina.máDelo = pridaj;
				break;

			case "š":
				plošina.upravŠírku(pridaj ? 1 : -1);
				break;

			case "l":
				nováLoptička();
				break;

			case "ľ":
				for (Loptička loptička : loptičky)
				{
					loptička.upravVeľkosť(pridaj ? 1 : -1);
					if (ibaJedna) break;
				}
				break;

			case "r":
				for (Loptička loptička : loptičky)
				{
					loptička.upravRýchlosť(pridaj ? 1 : -1);
					if (ibaJedna) break;
				}
				break;

			case "p":
				for (Loptička loptička : loptičky)
				{
					loptička.penetračná(pridaj);
					if (ibaJedna) break;
				}
				break;

			case "s":
				for (Loptička loptička : loptičky)
				{
					loptička.skočNaMyš();
					if (ibaJedna) break;
				}
				break;

			case "o":
				for (Loptička loptička : loptičky)
				{
					loptička.otočNaMyš();
					if (ibaJedna) break;
				}
				break;

			case "t":
				reset();
				break;
			}
		}
		spustiČasovač();
	}


	// Obsluha udalostí…

	@Override public void zmenaVeľkostiOkna()
	{
		if (Svet.zobrazený())
		{
			mierka = Math.min(viditeľnáŠírka() / šš, viditeľnáVýška() / vv);
			vymažGrafiku(); vyplňObdĺžnik(x2 * mierka, y2 * mierka);
		}
	}

	@Override public void kresliTvar()
	{
		// TODO: skóre a podobne
	}

	@Override public void klik()
	{
		// TESTY
		if (tlačidloMyši(ĽAVÉ)) čít();
	}

	@Override public void stlačenieKlávesu()
	{
		switch (kláves())
		{
		case VĽAVO: plošina.zrýchleniePosunu(-2.5); break;
		case VPRAVO: plošina.zrýchleniePosunu(2.5); break;
		case ENTER: čít(); break; // TESTY
		}
	}

	@Override public void uvoľnenieKlávesu()
	{
		switch (kláves())
		{
		case VĽAVO: case VPRAVO:
			plošina.zrýchleniePosunu(0);
			plošina.rýchlosťPosunu(0); break;

		case MEDZERA:
			if (plošina.máDelo)
			{
				// TODO – obmedziť maximálny počet striel(?)
				nováStrela();
			}
			break;
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
					kú1.pripravKolíziu(testovanáLoptička);
					kú2.pripravKolíziu(testovanáLoptička);
					kú3.pripravKolíziu(testovanáLoptička);
					kú4.pripravKolíziu(testovanáLoptička);
				}

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
		}

		for (int i = 0; i < loptičky.veľkosť(); ++i)
		{
			Loptička loptička = loptičky.daj(i);
			if (loptička.neaktívny())
			{
				loptičky.odober(loptička);
				--i;
			}
			else loptička.uložPoslendé();
		}

		testovanáLoptička = null;

		for (int i = 0; i < strely.veľkosť(); ++i)
		{
			Strela strela = strely.daj(i);

			for (Tehla tehla : tehly)
			{
				if (tehla.bodVObdĺžniku(strela))
				{
					tehla.udri();
					deaktivujStrelu(strela);
					--i;
					break;
				}
			}
		}

		for (int i = 0; i < bonusy.veľkosť(); ++i)
		{
			Bonus bonus = bonusy.daj(i);

			if (plošina.bodVObdĺžniku(bonus))
			{
				bonus.akcia();
				deaktivujBonus(bonus);
				--i;
			}
		}

		if (neboloPrekreslené()) prekresli();
	}


	// Deaktivácia objektov (ich vyradenie z hry) a ďalšie akcie…

	public void deaktivujStrelu(Strela strela)
	{
		strela.deaktivuj(false);
		strely.odober(strela);
	}

	public void deaktivujBonus(Bonus bonus)
	{
		bonus.deaktivuj(false);
		bonusy.odober(bonus);
	}

	public void overPenetračnú()
	{
		if (null != testovanáLoptička) testovanáLoptička.penetruj();
	}

	public void odchýľLoptičku(double rýchlosťPosunu)
	{
		if (null != testovanáLoptička)
			testovanáLoptička.vpravo(rýchlosťPosunu);
	}


	// Hlavná metóda…
	public static void main(String[] args)
	{
		použiKonfiguráciu("Ballbreaker.cfg"); Svet.skry(); nekresli();
		try { new Ballbreaker(); } catch (Throwable t) { t.printStackTrace(); }
		if (prvéSpustenie()) { zbaľ(); vystreď(); }
		Svet.zobraz(); spustiČasovač();
	}
}
