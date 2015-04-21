## Changelog
*Skutečné změny v projektu jsou v git historii commitů*

* 30.9. 
	* Zjišťování toho, v čem budu aplikaci vytvářet a jaké nástroje budu potřebovat
* 2.10.
	* Zjištění stavu Bakalářů, výstupním souborem je vždy HTML tabulka.
* 7.10.
	* Učení se vytváření aplikace pro operační systém Android (Activities, UIs, XML, resources).
* 8.10.
	* Zvažování podporování jiných platforem (iOS a Windows Phone).
	* Nápad jsem zavrhl, neexistují vhodné nástroje a převodníky (není divu Java a ObjC je obrovský rozdíl).
	* API na gjk serveru nejlépe v PHP. Android aplikace provede HTTP GET požadavek.
* 12.10.
	* Dokončen základ Android aplikace
	* V aplikaci funguje navigace mezi jednotlivými položkami (další položky postupně přibudou).
	* HTTP GET požadavek je funkční a aplikace umí tento text zobrazit uživateli.
	* Chybí však položka nastavení a je nutné dodělat ikony v navigaci.
* 14.10.
	* Vytváření API pro php skript na GJK serveru => HTTP GET s parametry pro suplování, třídu, jídelnu apod.
	* Pro parsování je potřeba povolit modul "dom" v GJK konfiguraci PHP.
* 17.10.
	* Dodělal jsem do aplikace všechny potřebné ikony včetně launcher ikony
* 21.10.
	* Zařazení alergenů do návrhu vrácených dat z API endpointu
	* Mastering Markdown syntax
* 24.10.
	* Předělání a dokončení API pro JSON formát
	* Implementace čtení JSON dat a komunikace s PHP endpointem v Android aplikaci
* 30.10.
	* Dokončen PHP API endpoint
* 8.11.
	* Aplikace je funkční a lze běžně používat, zbývá pořádně otestovat a zveřejnit apk
* 21.11.
	* Notifikace i cache jsem otestoval a fungují. 
	* APK je prozatím zveřejněno zde na GitHubu a API skript běží na mé vlastní URL (nikoli na gjk.cz)
* 27.11.2014
	* Publikování aplikace na Google Play
	* Je potřeba dodělat grafiku a popisy na Google Play stránce
	* Zjistit a případně reagovat na feedback od uživatelů
* 6.1.2015
	* Google Play stránka je v pořádku
	* Bylo předěláno parsování jídelny, protože byly změněny GJK stránky školy
