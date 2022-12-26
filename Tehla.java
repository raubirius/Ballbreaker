
import knižnica.*;

public class Tehla extends GRobot
{
	// Zmena: Body aj kolízne úsečky vytvoríme vopred a prepojíme ich tak,
	// aby sme na nich vzájomnej prepojenosti už nemuseli nič meniť.
	// 
	// Body sa nezmenia, len budú vytvorené vopred a preto nebudeme pri
	// určovaní ich polôh vytvárať nové, ale ich len presunieme na nové
	// pozície metódou poloha.
	// 
	// Kolízne úsečky budú obsahovať body presne v tom poradí ako boli, len
	// ich bude 12 a budú prepojené so svojimi bodmi vopred a natrvalo.

	private final Bod[] kb = new Bod[12]; // kb – kolízny bod
	private final KolíznaÚsečka[] kú = new KolíznaÚsečka[12];

	public Tehla()
	{
		veľkosť(20);
		pomer(2);

		for (int i = 0; i < 12; ++i) kb[i] = new Bod();

		for (int i = 0; i < 12; ++i)
			kú[i] = new KolíznaÚsečka(kb[i], kb[(i + 1) % 12]);
	}

	public boolean jeVKolíznejZóne(Loptička l)
	{
		// Tu boli všetky inštancie bodov nahradené primitívnymi hodnotami
		// double. Je to efektívnejšie…

		double p1x = l.polohaX();
		double p1y = l.polohaY();
		double p2x = l.poslednáPolohaX();
		double p2y = l.poslednáPolohaY();

		double v = l.veľkosť() + l.najväčšiaRýchlosť() +
			(výška() > šírka() ? výška() : šírka()) / 2;

		double p3x = polohaX() - v;
		double p3y = polohaY() - v;
		double p4x = polohaX() + v;
		double p4y = polohaY() + v;

		// Podmienky boli prepísané do jedného výrazu:
		return
			(p1x >= p3x && p1x <= p4x && p1y >= p3y && p1y <= p4y) ||
			(p2x >= p3x && p2x <= p4x && p2y >= p3y && p2y <= p4y);
	}

	// TEST (aktívny – kresli tučnou čiarou tie tehly, ktoré sú v kolíznej
	// 	zóne loptičky):
	private boolean bvkz = false; // (bol v kolíznej zóne)

	public void spracujKolíziu(Loptička l)
	{
		// Nové: Spracujú sa len tie kolízie, ktoré sú v rámci kolíznej zóny:
		// if (!jeVKolíznejZóne(l)) return;
		if (!(bvkz = jeVKolíznejZóne(l))) return; // TEST

		double v = l.veľkosť();
		double šš = šírka() / 2;
		double vv = výška() / 2;
		double s = smer();

		// Do poľa kb teraz už nesmieme priraďovať nové body. Namiesto toho
		// budeme meniť polohy jestvujúcich prvkov (bodov) poľa kb:

		kb[1].poloha(this);
		kb[1].posuňVSmere(s, vv + v);
		kb[2].poloha(kb[1]);

		kb[1].posuňVSmere(s + 90, šš);
		kb[2].posuňVSmere(s - 90, šš);


		kb[8].poloha(this);
		kb[8].posuňVSmere(s + 180, vv + v);
		kb[7].poloha(kb[8]);

		kb[8].posuňVSmere(s + 90, šš);
		kb[7].posuňVSmere(s - 90, šš);


		kb[4].poloha(this);
		kb[4].posuňVSmere(s - 90, šš + v);
		kb[5].poloha(kb[4]);

		kb[4].posuňVSmere(s, vv);
		kb[5].posuňVSmere(s, -vv);


		kb[11].poloha(this);
		kb[11].posuňVSmere(s + 90, šš + v);
		kb[10].poloha(kb[11]);

		kb[11].posuňVSmere(s, vv);
		kb[10].posuňVSmere(s, -vv);


		kb[0].poloha(this);
		kb[0].posuňVSmere(s, vv);
		kb[3].poloha(kb[0]);

		kb[0].posuňVSmere(s + 90, šš);
		kb[3].posuňVSmere(s - 90, šš);

		kb[0].posuňVSmere(s + 45, v);
		kb[3].posuňVSmere(s - 45, v);


		kb[9].poloha(this);
		kb[9].posuňVSmere(s, -vv);
		kb[6].poloha(kb[9]);

		kb[9].posuňVSmere(s + 90, šš);
		kb[6].posuňVSmere(s - 90, šš);

		kb[9].posuňVSmere(s + 135, v);
		kb[6].posuňVSmere(s - 135, v);


		// [*kú*]
		// Na tomto mieste by bolo spracovanie kolízie predčasné. Najprv
		// potrebujeme kolízne úsečky zoradiť podľa vzdialenosti, takže tu je
		// použitá len nová prípravná metóda pripravKolíziu. (Pôvodná,
		// spracujKolíziu, je vypnutá a použitá v tiku hlavnej triedy.)

		// for (int i = 0; i < 12; ++i) kú[i].spracujKolíziu(l);
		for (int i = 0; i < 12; ++i) kú[i].pripravKolíziu(l);
	}


	@Override public void kresliTvar()
	{
		if (bvkz) hrúbkaČiary(3); // TEST
		obdĺžnik();
	}
}
