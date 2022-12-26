
import java.util.Collections;
import knižnica.*;
import static knižnica.Svet.*;

// Poznámka: V tejto verzii je opravený mechanizmus kolízií. Riešenie je
// „roztrúsené“ vo viacerých triedach a je označené značkou [*kú*].

public class Ballbreaker extends GRobot
{
	private Tehla tehla1 = new Tehla();
	private Tehla tehla2 = new Tehla();
	private Tehla tehla3 = new Tehla();
	private Loptička loptička = new Loptička();

	private Ballbreaker()
	{
		super(300, 400);

		// (Zmena vlastností loptičky bola presunutá do konštruktora loptičky.)

		// Ide o náhodu, ale v tejto konfigurácii sa do 30 sekúnd prejaví
		// očakávaný bug, keď loptička vletí do a vyletí z vnútra tehly:
		/*
		tehla.skoč(100);
		tehla.vpravo(20);
		*/

		/*
		// Testy:
		loptička.skočNa(-91.79, 83.13);
		loptička.smer(50);

		for (int i = 0; i < 5; ++i)
		{
			loptička.vzad(1);
			vypíšRiadok(
				"x: ", loptička.polohaX(), riadok,
				"y: ", loptička.polohaY(), riadok);
		}
		*/

		// V tejto konfigurácii sa to prejaví okamžite (ale teraz už sa to
		// neprejaví, keďže to bolo opravené):
		loptička.skočNa(-92, 83);
		loptička.smer(50);

		tehla1.skočNa(0, 100);
		tehla1.smer(70);

		tehla2.skočNa(-50, 0);
		tehla2.smer(45);

		tehla3.skočNa(0, -100);
		tehla3.smer(-70);
	}

	@Override public void kresliTvar()
	{
	}

	@Override public void klik()
	{
		// TESTY (vypnuté – zistenie aktuálnej polohy a orientácie, aby sme
		// 	objekty dostali rýchlejšie do konfigurácie, v ktorej sa prejaví
		// 	„bug“ vojdenia lopty do tehly):
		// if (ÚdajeUdalostí.tlačidloMyši(ĽAVÉ))
		// {
		// 	vypíšRiadok(
		// 		"Lopta:", riadok,
		// 			"  x: ", loptička.polohaX(), riadok,
		// 			"  y: ", loptička.polohaY(), riadok,
		// 			"  s: ", loptička.smer(), riadok,
		// 		"Tehla:", riadok,
		// 			"  x: ", tehla.polohaX(), riadok,
		// 			"  y: ", tehla.polohaY(), riadok,
		// 			"  s: ", tehla.smer());
		// 
		// 	/*tehla.skoč(náhodnéCeléČíslo(-5, 5), náhodnéCeléČíslo(-5, 5));
		// 	tehla.vľavo(náhodnéCeléČíslo(-5, 5));
		// 	tehla.spracujKolíziu(loptička);
		// 	žiadajPrekreslenie();*/
		// }
		// else
		// {
		// 	vymažTexty();
		// 	// loptička.otočNaMyš();
		// }
	}

	// TESTY (vypnuté – výpis stavu zásobníka kolíznych úsečiek pred a po
	// 	prvom zotriedení – aby sme overili, či triedenie funguje správne; prvý
	// 	test – zoradenie od najvzdialenejšej po najbližšiu ukázal, že toto
	// 	zoradenie bug nerieši; druhý test – zoradenie od najbližšej po
	// 	najvzdialenejšiu odstránil „bug“ prinajmenšom v tej konfigurácii,
	// 	ktorú sme mali pripravenú, ale na dokonalé overenie to chce viac
	// 	tehiel, viac loptičiek a dlhšie testovanie…):
	// 	
	// private boolean bbb = true;

	@Override public void tik()
	{
		loptička.zkú.vymaž();
		tehla1.spracujKolíziu(loptička);
		tehla2.spracujKolíziu(loptička);
		tehla3.spracujKolíziu(loptička);

		// TESTY:
		// boolean aaa = bbb && !loptička.zkú.isEmpty();
		// if (aaa) { System.out.println("pred"); for (KolíznaÚsečka kú : loptička.zkú) System.out.println(kú.v); }

		// [*kú*]
		// Zotriedenie zoznamu kolízií tejto loptičky:
		Collections.sort(loptička.zkú);

		// TESTY:
		// if (aaa) { System.out.println("\npo"); for (KolíznaÚsečka kú : loptička.zkú) System.out.println(kú.v); bbb = false; }

		// [*kú*]
		// Detekcia kolízií:
		for (KolíznaÚsečka kú : loptička.zkú) kú.spracujKolíziu(loptička);

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
