Kepler-GJK
==========

*Autor:* **Dalimil Hájek**

**Android aplikace pro Gymnázium Jana Keplera.**

<img src="https://github.com/Dalimil/Kepler-GJK/blob/master/Docs/Screenshots/screenshot_suplovani.png" align="left" height="400" width="240" alt="Suplování"/>
&nbsp;&nbsp;
<img src="https://github.com/Dalimil/Kepler-GJK/blob/master/Docs/Screenshots/screenshot_jidelna.png" align="left" height="400" width="240" alt="Jídelna"/>
&nbsp;&nbsp;
<img src="https://github.com/Dalimil/Kepler-GJK/blob/master/Docs/Screenshots/screenshot_settings.png" align="left" height="400" width="240" alt="Nastavení"/>
<br />

**Maturitní projekt z informatiky.** [Specifikace projektu](https://docs.google.com/document/d/1zhgz2ZLsTh7DuwjyZ96LdD6Nas5F6uHO7drYmZspkiM/edit?usp=sharing)

Cílem projektu je aplikace, která umí uživateli ukazovat aktuální změny v jeho rozvrhu a denní menu školní jídelny.

- [x] Základní Android aplikace s navigací a menu
- [x] Funkční HTTP GET požadavek ze strany Android aplikace
- [x] Ikony v Android aplikaci + vytvořen launcher
- [x] Nastavení preferencí v aplikaci (třída, zobrazování alergenů, ...)
- [x] Specifikace UI aplikace (+ screenshots ve složce Docs) -> Material Design 
- [x] [Návrh API](https://github.com/Dalimil/Kepler-GJK/blob/master/GJK_API/Specifikace_API) se specifikací dat jako odpověď na dané GET parametry 
- [x] Vytvořit API endpoint v PHP, se kterým bude aplikace komunikovat
- [x] Naprogramovat službu v pozadí, která se bude pravidelně spouštět i při vypnuté aplikaci
- [ ] Vytvářet v pozadí notifikace o novém suplování
- [ ] Závěrečné publikování aplikace na Google Play

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
	* API na gjk serveru nejlépe v PHP (PHP tam už je). Android aplikace provede HTTP GET požadavek.
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
