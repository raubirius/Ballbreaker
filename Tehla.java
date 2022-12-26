
import knižnica.*;

public class Tehla extends GRobot
{
	public Tehla()
	{
		veľkosť(20);
		pomer(2);
	}

	public boolean jeVKolíznejZóne(Loptička l)
	{
		Bod p1 = l.poloha();
		Bod p2 = l.poslednáPoloha();
		double v = l.veľkosť() + l.najväčšiaRýchlosť() +
			(výška() > šírka() ? výška() : šírka()) / 2;

		Bod p3 = new Bod(polohaX() - v, polohaY() - v);
		Bod p4 = new Bod(polohaX() + v, polohaY() + v);

		// Z dôvodu prehľadnosti to rozpíšeme (dalo by sa to potom spojiť
		// do jednej dlhej podmienky):
		if (p1.polohaX() >= p3.polohaX() && p1.polohaX() <= p4.polohaX() &&
			p1.polohaY() >= p3.polohaY() && p1.polohaY() <= p4.polohaY())
			return true;

		if (p2.polohaX() >= p3.polohaX() && p2.polohaX() <= p4.polohaX() &&
			p2.polohaY() >= p3.polohaY() && p2.polohaY() <= p4.polohaY())
			return true;

		return false; 
		/*
			výsledok = (podmienka ? výraz1 : výraz2)

			if (podmienka)
				výsledok = výraz1;
			else
				výsledok = výraz2;
		*/
	}

	private Bod[] body = new Bod[12]; 

	public void spracujKolíziu(Loptička l)
	{
		double v = l.veľkosť();
		double šš = šírka() / 2;
		double vv = výška() / 2;
		double s = smer();

		body[1] = poloha();
		body[1].posuňVSmere(s, vv + v);
		body[2] = new Bod(body[1]);

		body[1].posuňVSmere(s + 90, šš);
		body[2].posuňVSmere(s - 90, šš);


		body[8] = poloha();
		body[8].posuňVSmere(s + 180, vv + v);
		body[7] = new Bod(body[8]);

		body[8].posuňVSmere(s + 90, šš);
		body[7].posuňVSmere(s - 90, šš);


		body[4] = poloha();
		body[4].posuňVSmere(s - 90, šš + v);
		body[5] = new Bod(body[4]);

		body[4].posuňVSmere(s, vv);
		body[5].posuňVSmere(s, -vv);


		body[11] = poloha();
		body[11].posuňVSmere(s + 90, šš + v);
		body[10] = new Bod(body[11]);

		body[11].posuňVSmere(s, vv);
		body[10].posuňVSmere(s, -vv);


		body[0] = poloha();
		body[0].posuňVSmere(s, vv);
		body[3] = new Bod(body[0]);

		body[0].posuňVSmere(s + 90, šš);
		body[3].posuňVSmere(s - 90, šš);

		body[0].posuňVSmere(s + 45, v);
		body[3].posuňVSmere(s - 45, v);


		body[9] = poloha();
		body[9].posuňVSmere(s, -vv);
		body[6] = new Bod(body[9]);

		body[9].posuňVSmere(s + 90, šš);
		body[6].posuňVSmere(s - 90, šš);

		body[9].posuňVSmere(s + 135, v);
		body[6].posuňVSmere(s - 135, v);


		naštartované = true;


		for (int i = 0; i < body.length - 1; ++i)
		{
			kú.b1 = body[i];
			kú.b2 = body[i + 1];
			kú.spracujKolíziu(l);
		}

		kú.b1 = body[11];
		kú.b2 = body[0];
		kú.spracujKolíziu(l);
	}

	private KolíznaÚsečka kú = new KolíznaÚsečka();

	private boolean naštartované = false;

	@Override public void kresliTvar()
	{
		obdĺžnik();

		if (naštartované)
		{
			int i = 0;
			for (Bod bod : body)
			{
				skočNa(bod);
				// kruh(6);
				text(S(i));
				++i;
			}
		}
	}
}
