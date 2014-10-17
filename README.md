Kepler-GJK
==========

Autor: Dalimil Hájek

Maturitní projekt z informatiky.

Android aplikace pro Gymnázium Jana Keplera. 

Cílem je aplikace, která umí uživateli ukazovat aktuální změny v jeho rozvrhu a denní menu školní jídelny.

==========
Dodělat návrh API se specifikací dat jako odpověď na dané GET parametry
Specifikovat UI aplikace, screenshots apod.

==========
Changelog

Skutečné změny v projektu jsou v git historii commitů
Zde je popsána práce v hodině

30.9. 

Zjišťování toho, v čem budu aplikaci vytvářet a jaké nástroje budu potřebovat

2.10.

Zjištění stavu Bakalářů, výstupním souborem je vždy HTML tabulka.

7.10.

Učení se vytváření aplikace pro operační systém Android (Activities, UIs, XML, resources).

8.10.

Zvažování podporování jiných platforem (iOS a Windows Phone).

Nápad jsem zavrhl, neexistují vhodné nástroje a převodníky (není divu Java a ObjC je obrovský rozdíl).

API na gjk serveru nejlépe v PHP (PHP tam už je). Android aplikace provede HTTP GET požadavek.

12.10.

Dokončen základ Android aplikace

V aplikaci funguje navigace mezi jednotlivými položkami (další položky postupně přibudou).

HTTP GET požadavek je funkční a aplikace umí tento text zobrazit uživateli.

Chybí však položka nastavení a je nutné dodělat ikony v navigaci.

14.10.

Vytváření API pro php skript na GJK serveru => HTTP GET s parametry pro suplování, třídu, jídelnu apod.

Pro parsování je potřeba povolit modul "dom" v GJK konfiguraci PHP.


17.10.

Dodělal jsem do aplikace všechny potřebné ikony včetně launcher ikony