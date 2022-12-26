
import java.util.Collections;
import knižnica.*;
import static knižnica.Kláves.*;
import static knižnica.Svet.*;
import static knižnica.ÚdajeUdalostí.*;

// Prvá fáza vývoja:
// ✓ návrh a výroba základných objektov (tehly, loptičky) a tried (hlavná
//   trieda, rodičovská trieda kolíznych objektov…)
// ~ kolízie objektov (stále nastávajú úniky – všimol som si, že podobné sa
//   diali aj v jednej implementácii, ktorú som hrával mnoho rokov dozadu;
//   tým to nechcem ospravedlňovať; len, že to zrejme nie je také ľahké)
// ✓ zoznamy objektov
// • (neustála revízia a testovanie)
// ———
// Druhá fáza vývoja:
// ✓ rozšírenie o ďalšie objekty a triedy (plošina, strela, bonus…)
// ✓ škálovateľnosť grafiky podľa okna
// ✓ penetračná loptička
// ✓ delo (prídavok plošiny)
// ✓ zmena veľkosti plošiny
// ✓ zmena veľkosti loptičiek
// ✓ zmena rýchlosti loptičiek
// ✓ bonusy (pridanie loptičiek, zmena veľkosti plošiny, zmena veľkosti
//   loptičiek, zmena rýchlosti loptičiek, penetračné loptičky, delo)
// ———
// TODO – na dokončenie:
// • steny
// • skóre
// ✓ čakanie loptičky po objavení
// ✓ efekty (postupné zmiznutie tehly po zničení)
// • ďalšie bonusy (život, duplikácia jestvujúcich loptičiek)
// ✓ výhra/prehra
// • levely

public class Ballbreaker extends GRobot
{
	// Hlavná inštancia hry a globálna mierka:
	// public static Ballbreaker ballbreaker; // (keby mohla byť final, bola by
		// final, ale nedá sa to urobiť, pretože túto inštanciu je nevyhnutné
		// inicializovať čo najskôr (v podstate je to druhý príkaz
		// konštruktora), inak by vznikali chyby počas inicializácie;
		// keďže zároveň musí byť statická, kompilátor nedokáže rozpoznať,
		// že priradenie by sa vykonalo len raz (nemá tú garanciu), tak
		// vyhlasuje pri príznaku final chybu) TODO(revízia)
	public static double mierka = 1.0;

	// Aktívne objekty hry:
	public final static Zoznam<Tehla> tehly = new Zoznam<>();
	public final static Zoznam<Loptička> loptičky = new Zoznam<>();
	public final static Zoznam<Strela> strely = new Zoznam<>();
	public final static Zoznam<Bonus> bonusy = new Zoznam<>();
	private static Plošina plošina; // (keby mohla byť final, bola by final,
		// ale ani tu sa to nedá urobiť, lebo žiadny robot (vrátane tejto
		// plošiny) nesmie byť vytvorený skôr, než hlavná trieda
		// (Ballbreaker); zároveň by mala byť plošina dostupná čo najskôr;
		// to sa dá docieliť tak, že bude vytvorená počas inicializácie
		// hlavnej triedy – v jej konštruktore; aspoň, že táto inštancia
		// (na rozdiel od hlavnej triedy) môže byť súkromná) TOOO(revízia)

	/* Poznámky: Pri tejto revízii bolo zhodnotené a rozhodnuté, že väčšina
	súčastí hlavnej triedy môže byť statická. Objektový model sa tým odľahčil.
	Veľa vecí sa zjedonodušilo. */

	// Rôzne herné atribúty:
	private static int životy;
	private static int skóre;
	private static boolean hraJeAktívna;

	// Výpočtové rozmery hracej plochy:
	public final static double šš = 800; // (šírka)
	public final static double vv = 500; // (výška)

	// Výpočtové hranice hracej plochy:
	public final static double x1 =  -šš / 2;      // (najmenšie x)
	public final static double x2 =  (šš / 2) - 1; // (najväčšie x)
	public final static double y1 = (-vv / 2) + 1; // (najmenšie y)
	public final static double y2 =   vv / 2;      // (najväčšie y)

	// Kolízne úsečky okrajov obrazovky:
	private final static KolíznaÚsečka
		kú1 = new KolíznaÚsečka(new Bod(), new Bod()), // (ľavý)
		kú2 = new KolíznaÚsečka(new Bod(), new Bod()), // (pravý)
		kú3 = new KolíznaÚsečka(new Bod(), new Bod()), // (spodný)
		kú4 = new KolíznaÚsečka(new Bod(), new Bod()); // (vrchný)

	// Do tohto atribútu sa počas testovania kolízií vždy uloží aktuálne
	// testovaná loptička. Je to využité hlavne v kolíznej úsečke spodného
	// okraja.
	private static Loptička testovanáLoptička = null;

	// Akcia spodného okraja – deaktivuje loptičku, ktorá je uložená v atribúte
	// testovanáLoptička:
	private static Akcia spodnýOkraj = () ->
	{
		if (null != testovanáLoptička)
			testovanáLoptička.deaktivuj(false);
	}; static {
		kú3.akcia = spodnýOkraj;
	}


	// Súvisiace s inicializáciou a deaktiváciou hry…

	private Ballbreaker()
	{
		super(šírkaZariadenia(), výškaZariadenia());
		// ballbreaker = this;
		plošina = new Plošina();
		hrúbkaČiary(5);
		veľkosť(5);
		vrstva(1);

		farbaPozadia(antracitová);
		farba(tmavomodrá);
		reset();

		Rozhranie.inicializuj();
	}

	private static void reset()
	{
		životy = 4;
		skóre = 0;
		hraJeAktívna = true;

		strataŽivota();
		Tehla.reset();

		// TOOO: návrhár levelov
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

	private static void strataŽivota()
	{
		// for (int i = 0; i < 2; ++i)
		if (životy > 0)
		{
			plošina.reset();
			Bonus.reset();
			Strela.reset();
			Loptička.reset();
			nováLoptička();
			--životy;
		}
		else deaktivujHru();
	}

	private static void úspechVHre()
	{
		Bonus.reset(); // TODO: zober všetky bonusy (zvýš skóre)
		Loptička.reset();
		deaktivujHru();
	}

	private static void deaktivujHru()
	{
		hraJeAktívna = false;
		plošina.zrýchleniePosunu(0, false);
		plošina.rýchlosťPosunu(0, false);
	}


	// Tvorba objektov hry – používané pri inicializácii a bonusoch…

	public static Loptička nováLoptička()
	{
		Loptička loptička = Loptička.dajLoptičku();
		loptička.skočNa(plošina.polohaX(), 0);
		loptička.smer(90);
		loptička.odskoč();
		loptička.smer(náhodnéCeléČíslo(260, 280));
		loptičky.pridaj(loptička);
		return loptička;
	}

	public static Strela nováStrela()
	{
		Strela strela = Strela.dajStrelu();
		strela.skočNa(plošina);
		strela.smer(plošina);
		strela.skoč();
		strela.vľavo(náhodnéCeléČíslo(-3, 3));
		strely.pridaj(strela);
		return strela;
	}

	public static Bonus novýBonus(Poloha p, int n)
	{
		Bonus bonus = Bonus.dajBonus(p, n);
		bonusy.pridaj(bonus);
		return bonus;
	}

	// Akcie pri zobratí bonusov:
	public static Akcia akcieBonusov[] = {
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

	private static void čít()
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

			if (čít.startsWith("ž") && čít.length() > 1)
			{
				Long ž = reťazecNaCeléČíslo(čít.substring(1));
				if (null != ž) životy = ž.intValue();
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
				if (životy < 1) životy = 1;
				hraJeAktívna = true;
				break;

			case "č":
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


	// Deaktivácia objektov (ich vyradenie z hry) a ďalšie akcie…

	public static void deaktivujStrelu(Strela strela)
	{
		strela.deaktivuj(false);
		strely.odober(strela);
	}

	public static void deaktivujBonus(Bonus bonus)
	{
		bonus.deaktivuj(false);
		bonusy.odober(bonus);
	}

	public static void overPenetračnú()
	{
		if (null != testovanáLoptička) testovanáLoptička.penetruj();
	}

	public static void odchýľLoptičku(double rýchlosťPosunu)
	{
		if (null != testovanáLoptička)
			testovanáLoptička.vpravo(rýchlosťPosunu);
	}


	// Obsluha udalostí…

	@Override public void zmenaVeľkostiOkna()
	{
		if (Svet.zobrazený())
		{
			mierka = Math.min(viditeľnáŠírka() / šš, viditeľnáVýška() / vv);
			vymažGrafiku(); vyplňObdĺžnik(x2 * mierka, y2 * mierka);
			// písmo("Cambria", 50 * mierka);
		}
	}

	@Override public void kresliTvar()
	{
		// TODO: skóre a podobne
		farba(žltá);
		skočNa((x1 + 16) * Ballbreaker.mierka,
			(y2 - 16) * Ballbreaker.mierka);
		veľkosť(veľkosť() * Ballbreaker.mierka);
		for (int i = 0; i < životy; ++i)
		{
			kruh();
			preskočVpravo(14 * Ballbreaker.mierka);
		}

		if (!hraJeAktívna)
		{
			mierka(mierka * 5);
			skočNa(stred);
			hrúbkaČiary(hrúbkaČiary() * Ballbreaker.mierka);

			if (tehly.jePrázdny())
			// if (0 == (++xxx / 10) % 2) // TEST
				// text("👍");
				obkresliOblasť(Rozhranie.úspech);
			else
				// text("👎");
				obkresliOblasť(Rozhranie.neúspech);
		}
	}

	// private static int xxx = 0; // TEST

	@Override public void klik()
	{
		// TESTY
		if (tlačidloMyši(ĽAVÉ)) čít();
	}

	@Override public void stlačenieKlávesu()
	{
		switch (kláves())
		{
		case VĽAVO:
			if (hraJeAktívna) plošina.zrýchleniePosunu(-2.5, false);
			break;

		case VPRAVO:
			if (hraJeAktívna) plošina.zrýchleniePosunu(2.5, false);
			break;

		case ENTER: čít(); break; // TESTY
		}
	}

	@Override public void uvoľnenieKlávesu()
	{
		switch (kláves())
		{
		case VĽAVO: case VPRAVO:
			plošina.zrýchleniePosunu(0, false);
			plošina.rýchlosťPosunu(0, false);
			break;

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
		if (hraJeAktívna)
		{
			if (loptičky.jePrázdny()) strataŽivota();
			else if (tehly.jePrázdny()) úspechVHre();
			else
			{
				for (int i = 0; i < loptičky.veľkosť(); ++i)
				{
					testovanáLoptička = loptičky.daj(i);
					if (testovanáLoptička.štartuje > 0)
					{
						if (0 == --testovanáLoptička.štartuje)
							testovanáLoptička.rýchlosť(12, false);
						continue;
					}

					double v = testovanáLoptička.veľkosť();

					// Výpočet súradníc, ktoré sú považované za vnútrajšok
					// plochy pre túto loptičku – využíva sa to nielen na
					// prípravu úsečiek, ale aj na ich zaradenie do detekcie
					// v ďalšom cykle nižšie:
					double x1v = x1 + v;
					double x2v = x2 - v;
					double y1v = y1 + v;
					double y2v = y2 - v;

					// Príprava kolíznych úsečiek okrajov hracej plochy:
					kú1.b1.poloha(x1v, y1);
					kú1.b2.poloha(x1v, y2);

					kú2.b1.poloha(x2v, y1);
					kú2.b2.poloha(x2v, y2);

					kú3.b1.poloha(x1, y1v);
					kú3.b2.poloha(x2, y1v);

					kú4.b1.poloha(x1, y2v);
					kú4.b2.poloha(x2, y2v);

					boolean opakuj = true;

					for (int j = 0; opakuj && testovanáLoptička.aktívny() &&
						j < 1000; ++j)
					{
						opakuj = false;

						testovanáLoptička.pripravKolíziu();
						double lx = testovanáLoptička.poslednáPolohaX();
						double ly = testovanáLoptička.poslednáPolohaY();

						// Tie to hranice sú do detekcie pridané len
						// v prípade, že je loptička v ich vnútri:
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

						// Triedenie kolíznych úsečiek podľa vzdialenosti –
						// pozri aj KolíznaÚsečka.compareTo:
						Collections.sort(testovanáLoptička.
							zoznamKolíznychÚsečiek);

						for (KolíznaÚsečka kolíznaÚsečka :
							testovanáLoptička.zoznamKolíznychÚsečiek)
							if (kolíznaÚsečka.
								spracujKolíziu(testovanáLoptička))
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
			}
		}

		if (neboloPrekreslené()) prekresli();
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
