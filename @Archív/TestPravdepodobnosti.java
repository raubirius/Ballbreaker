
import knižnica.GRobot;
import static knižnica.Svet.*;

public class TestPravdepodobnosti extends GRobot
{
	private static int dajNáhodnéČíslo(int n)
	{
		double p = Math.abs(((1 + náhodnéReálneČíslo()) *
			(1 + náhodnéReálneČíslo())) - 2) / 2;
		return n - (int)(n * p) - 1;
	}

	private TestPravdepodobnosti()
	{
		super(880, 880);

		int[] štat = new int[400];
		for (int i = 0; i < 101; ++i) štat[i] = 0;

		for (int i = 0; i < 8000; ++i)
		{
			double p1 = náhodnéReálneČíslo();
			double p2 = náhodnéReálneČíslo();
			double pp = Math.abs(((1 + p1) * (1 + p2)) - 2) / 2;
			// System.out.println("> " + p1 + " & " + p2 + " =>\n\t" + pp);
			if (pp > 1) System.err.println(pp);
			++štat[(int)(pp * 100)];
		}

		for (int i = 0; i < 5; ++i)
		{
			skočNa(-400 + 200 * i, -390);
			vzad(20); odskoč(10);
			text(S(i / 4.0));
		}

		skočNa(-400, -400); posuňVpravo(880);
		skočNa(-400, -400); dopredu(880);
		skočNa(-400, -400);

		for (int i = 0; i < 101; ++i)
			choďNa(-400 + 8 * i, -400 + 3 * štat[i]);


		for (int i = 0; i < 10; ++i) štat[i] = 0;
		for (int i = 0; i < 1300; ++i) ++štat[dajNáhodnéČíslo(8)];
		for (int i = 0; i < 10; ++i)
		{
			skočNa(-400 + 80 * i, -390); text(S(i));
			skočNa(-400 + 80 * i, -400 + štat[i]);
			kresliObdĺžnik(8, štat[i]);
		}


		skry();
	}

	public static void main(String[] args)
	{
		nekresli();
		new TestPravdepodobnosti();
		zbaľ(); vystreď(); kresli();
	}
}
