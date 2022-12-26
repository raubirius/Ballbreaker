
import knižnica.*;

public class KolíznyBlok extends GRobot
{
	// Body aj kolízne úsečky sú vytvorené vopred a sú prepojené tak,
	// aby sa ľahko používali na detekciu kolízií. (Kolízne úsečky obsahujú
	// odkazy na body zoradené presne v tom poradí ako sú v poli kolíznyBod.)

	protected final Bod[] kolíznyBod = new Bod[12];
	protected final KolíznaÚsečka[] kolíznaÚsečka = new KolíznaÚsečka[12];

	public KolíznyBlok()
	{
		zdvihniPero();

		for (int i = 0; i < 12; ++i) kolíznyBod[i] = new Bod();

		for (int i = 0; i < 12; ++i)
			kolíznaÚsečka[i] = new KolíznaÚsečka(
				kolíznyBod[i], kolíznyBod[(i + 1) % 12]);

		aktivuj(false);
	}

	public boolean jeVKolíznejZóne(Loptička l)
	{
		double p1x = l.polohaX();
		double p1y = l.polohaY();
		double p2x = l.poslednáPolohaX();
		double p2y = l.poslednáPolohaY();

		double veľkosť = l.veľkosť() + l.najväčšiaRýchlosť() +
			(výška() > šírka() ? výška() : šírka()) / 2;

		double p3x = polohaX() - veľkosť;
		double p3y = polohaY() - veľkosť;
		double p4x = polohaX() + veľkosť;
		double p4y = polohaY() + veľkosť;

		return
			(p1x >= p3x && p1x <= p4x && p1y >= p3y && p1y <= p4y) ||
			(p2x >= p3x && p2x <= p4x && p2y >= p3y && p2y <= p4y);
	}


	public void pripravKolíziu(Loptička l)
	{
		// Spracujú sa len tie kolízie, ktoré sú v rámci kolíznej zóny:
		if (!jeVKolíznejZóne(l)) return;


		double veľkosť = l.veľkosť();
		double šírka = šírka() / 2;
		double výška = výška() / 2;
		double smer = smer();

		// Optimalizácia (často vyskytujúce sa výpočty):
		double smer1 = smer + 90;
		double smer2 = smer - 90;


		// Príprava kolíznych úsečiek tohto bloku voči zadanej loptičke:

		kolíznyBod[1].poloha(this);
		kolíznyBod[1].posuňVSmere(smer, výška + veľkosť);
		kolíznyBod[2].poloha(kolíznyBod[1]);

		kolíznyBod[1].posuňVSmere(smer1, šírka);
		kolíznyBod[2].posuňVSmere(smer2, šírka);


		kolíznyBod[8].poloha(this);
		kolíznyBod[8].posuňVSmere(smer + 180, výška + veľkosť);
		kolíznyBod[7].poloha(kolíznyBod[8]);

		kolíznyBod[8].posuňVSmere(smer1, šírka);
		kolíznyBod[7].posuňVSmere(smer2, šírka);


		kolíznyBod[4].poloha(this);
		kolíznyBod[4].posuňVSmere(smer2, šírka + veľkosť);
		kolíznyBod[5].poloha(kolíznyBod[4]);

		kolíznyBod[4].posuňVSmere(smer, výška);
		kolíznyBod[5].posuňVSmere(smer, -výška);


		kolíznyBod[11].poloha(this);
		kolíznyBod[11].posuňVSmere(smer1, šírka + veľkosť);
		kolíznyBod[10].poloha(kolíznyBod[11]);

		kolíznyBod[11].posuňVSmere(smer, výška);
		kolíznyBod[10].posuňVSmere(smer, -výška);


		kolíznyBod[0].poloha(this);
		kolíznyBod[0].posuňVSmere(smer, výška);
		kolíznyBod[3].poloha(kolíznyBod[0]);

		kolíznyBod[0].posuňVSmere(smer1, šírka);
		kolíznyBod[3].posuňVSmere(smer2, šírka);

		kolíznyBod[0].posuňVSmere(smer + 45, veľkosť);
		kolíznyBod[3].posuňVSmere(smer - 45, veľkosť);


		kolíznyBod[9].poloha(this);
		kolíznyBod[9].posuňVSmere(smer, -výška);
		kolíznyBod[6].poloha(kolíznyBod[9]);

		kolíznyBod[9].posuňVSmere(smer1, šírka);
		kolíznyBod[6].posuňVSmere(smer2, šírka);

		kolíznyBod[9].posuňVSmere(smer + 135, veľkosť);
		kolíznyBod[6].posuňVSmere(smer - 135, veľkosť);


		double lx = l.poslednáPolohaX();
		double ly = l.poslednáPolohaY();
		double tx = polohaX();
		double ty = polohaY();

		for (int i = 0; i < 12; ++i)
			// Kolíziu s úsečkou budeme detegovať len v prípade, že jestvuje
			// priesečník medzi priamkou, ktorá ňou prechádza a spojnicou
			// medzi aktuálnou polohou tohto bloku (ktorá by mala byť v jeho
			// strede – záleží od grafiky, prípadne iných okolností, ale plán
			// je, že pre tehly a plošinu bude táto poloha presne v strede
			// bloku) a poslednou polohou loptičky. Prečo? Tým chceme
			// dosiahnuť detegovanie kolízií len tých loptičiek, ktoré sa
			// nachádzajú zvonka kolízneho bloku. Keby sme to isté robili tak,
			// že by sme namiesto poslednej polohy loptičky použili jej
			// aktuálnu polohu, tak by sa efekt obrátil – loptička by vedela
			// vojsť do vnútra bloku, ale mala by problém z neho vyjsť.
			if (null != Svet.priesečníkPriamkyAÚsečky(
				kolíznaÚsečka[i].b1.polohaX(), kolíznaÚsečka[i].b1.polohaY(),
				kolíznaÚsečka[i].b2.polohaX(), kolíznaÚsečka[i].b2.polohaY(),
				lx, ly, tx, ty)) kolíznaÚsečka[i].pripravKolíziu(l);
	}


	@Override public void kresliTvar()
	{
		zaoblenie(zaoblenieX() * Ballbreaker.mierka,
			zaoblenieY() * Ballbreaker.mierka);
		skočNa(polohaX() * Ballbreaker.mierka,
			polohaY() * Ballbreaker.mierka);
		veľkosť(veľkosť() * Ballbreaker.mierka);
		obdĺžnik();
	}

	@Override public void deaktivácia() { skry(); }
	@Override public void aktivácia() { zobraz(); }
}
