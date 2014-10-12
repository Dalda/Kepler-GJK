Kepler-GJK
==========

Autor: Dalimil Hájek

Maturitní projekt z informatiky.

Android aplikace pro Gymnázium Jana Keplera. 

Cílem je aplikace, která umí uživateli ukazovat aktuální změny v jeho rozvrhu a týdenní jídelníček školní jídelny.

==========
Návrhy rozšíření aplikace

Kromě suplování a obědů by to chtělo později přidat
	
	Rozvrh (problém s PDF)
	Aktuality (testovací RSS kanál ?)
	Odkaz na Study/Docházku
	Knihovna
	Suplování musí být i pro učitele

==========
Changelog

Skutečné změny v projektu jsou v git historii commitů
Zde je popsána práce v hodině

30.9. 

Zjišťování toho, v čem budu aplikaci vytvářet a jaké nástroje budu potřebovat

2.10.

Zjištění stavu Bakalářů, výstupním souborem je vždy HTML tabulka

7.10.

Učení se vytváření aplikace pro operační systém Android (Activities, UIs, XML, resources)

8.10.

Zvažování podporování jiných platforem (iOS a Windows Phone).

	Prozatím jsem nápad zavrhl, neexistují vhodné nástroje a převodníky (není divu Java a ObjC je obrovský rozdíl)

API na gjk serveru nejlépe v PHP (PHP tam už je). Android aplikace provede HTTP GET požadavek

12.10

Dokončen základ Android aplikace

V aplikaci funguje navigace mezi jednotlivými položkami (další položky postupně přibudou)

HTTP GET požadavek je funkční a aplikace umí tento text zobrazit uživateli

Chybí však položka nastavení a je nutné dodělat ikony v navigaci


