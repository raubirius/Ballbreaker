
import knižnica.*;

public class Loptička extends GRobot
{
	public Loptička()
	{
		veľkosť(18);
		zdvihniPero();
		najväčšiaRýchlosť(20);
	}

	@Override public void kresliTvar()
	{
		krúžok();
	}
}
