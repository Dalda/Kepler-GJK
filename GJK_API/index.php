
SPECIFIKACE API

Dočasná doména (openshift platform): http://kepler-dalimil.rhcloud.com

HTTP GET

parametry:
	type= 
		/* Android aplikace vnitřně nastavuje jednu ze dvou hodnot */
		suplovani
		jidelna


	type=suplovani
		&name=4.A
		(string - povinné, default není)
		/** Pokud je specifikován type "suplovani", musí být uveden parametr "name", což je GJK třída 
		 *  Ta je nastavena uživatelem v nastavení aplikace z nabídky R{1..8}.A {1..4}.{A..C} a je to povinný údaj
		 */

		ODPOVĚĎ PHP skriptu (např. pro ?type=suplovani&name=1.A):

				Středa 22.10.2014 (lichý týden)
				1.A 4.hod Fj FN1 odpadá (Pan)

				Čtvrtek 23.10.2014 (lichý týden)
				1.A 1.hod HSt (H2.3) přesun Kolář Jan z (17.10. 4.hod)

			Respektive jednoduché HTML, které může android aplikace v TextView (jen několik formátovacích tagů) přímo zobrazit.
				Třída: 1.A<br />
				<h5>Středa 22.10.2014  (lichý týden)</h5>
				1.A   4.hod   Fj   FN1      odpadá      (Pan)   <br /><br />
				<h5>Čtvrtek 23.10.2014  (lichý týden)</h5>
				1.A   1.hod   HSt      (H2.3)   přesun   Kolář Jan   z  (17.10. 4.hod)   <br /><br />


	type=jidelna
		&alergeny=0
		(boolean - povinné, default=0)
		/** Pokud je specifikován type "jidelna", musí být uveden parametr "alergeny", což je povinný údaj
		 *  Pokud alergeny=1, spolu s jídelníčkem se vypíšou k jednotlivým jídlům i alergeny
		 *  Tato hodnota je nastavena uživatelem v nastavení aplikace (checkbox ano/ne)
		 */

		 ODPOVĚĎ PHP skriptu (např. pro ?type=jidelna&alergeny=1):

			 	Pondělí 20.10.

				Polévka: Krupicová s vejcem
					Alergeny: lepek, vejce, mléko, celer
				1) Kuřecí maso na paprice, dušená rýže
					Alergeny: lepek, mléko
				2) Zapečené šunkové flíčky, okurka
					Alergeny: lepek, vejce, mléko
				3) Bramborové placičky se špenátem
					Alergeny: lepek, vejce, mléko

				Úterý 21.10.

				Polévka: Dýňová 
				...
				...

			Respektive jednoduché HTML, které může android aplikace v TextView (povoleno jen několik formátovacích tagů) přímo zobrazit.

				<h5>Pondělí 20.10.</h5>
				<i>Polévka:</i> Krupicová s vejcem <br />
				Alergeny: lepek, vejce, mléko, celer<br />
				<i>1)</i> Kuřecí maso na paprice, dušená rýže<br />
				Alergeny: lepek, mléko<br />
				<i>2)</i> Zapečené šunkové flíčky, okurka<br />
				Alergeny: lepek, vejce, mléko<br />
				<i>3)</i> Bramborové placičky se špenátem <br />
				Alergeny: lepek, vejce, mléko<br />
				<br />
				<h5>Úterý 21.10.</h5>
				<i>Polévka:</i> Dýňová
				...
				...

