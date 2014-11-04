<?php
/** 
 * PHP skript zpracovávající HTTP GET
 * Součast GJK aplikace pro Android
 * Výsledek GET požadavku jsou data v JSON formátu, která lze dále použít v aplikaci 
 * @author Dalimil Hájek
 */

assert_options(ASSERT_ACTIVE, 0); //zmenit na false pred oficialnim nasazením
function my_assert_handler($file, $line, $code){ echo "<hr />Assertion Failed: '$code'<br />line: '$line'<hr />";}
assert_options(ASSERT_CALLBACK, 'my_assert_handler');

$OK = main();
assert('$OK == 1');

function getXML($url){
	$html = file_get_contents($url); #allow_url_fopen must be true in the php.ini
	if($html !== FALSE){
		$doc = new DOMDocument();
		$html = iconv('WINDOWS-1250', 'UTF-8', $html); //převod na UTF-8
		
		$html = substr($html, strpos($html, "<body"));
		$html = str_replace("&nbsp;", "", $html);
		$html = '<html><head><meta http-equiv="content-type" content="text/html; charset=utf-8" /></head>' . $html;
		//echo $html;

		$doc->loadHTML('<?xml encoding="UTF-8">' . $html); //ze string promenne
		$xml = simplexml_import_dom($doc);
		return $xml->body;
	}
	else return null;
}

function suplovani($trida){
	$xml = getXML('http://old.gjk.cz/suplovani.php');
	if($xml == null) return false;

	foreach($xml->xpath("//p[not(text())]") as $torm){ //prázdné <p>
		unset($torm[0]);
	}
	foreach($xml->xpath("//table[@class != 'tb_supltrid_3']") as $torm){ //nepotřebné tabulky
		unset($torm[0]);
	}
	
	$nadpisy = array(); //nadpis 1=> p0, p1, p2,; 2=> p3, p4; 3=> ...
	$counter = 1;
	$nadpisy[$counter] = array();
	$first = true;
	foreach($xml->p as $par){ //naplněnní pole nadpisy (text před každou tabulkou suplování)
		$p = (string) $par;

		if(strpos($p, 'Bakaláři') !== FALSE) continue;
		if (strpos($p, 'Suplování:') !== FALSE){
			if($first){
				$first = false;
			} else{
				$counter += 1;
				$nadpisy[$counter] = array();
			}
		}
		if(strpos($p, 'datum výpisu') !== FALSE) continue;

		$nadpisy[$counter][] = trim($p); 
	}

	$json = array();
	$json["type"] = "suplovani";
	$json["trida"] = $trida;
	$json["dny"] = array();

	$tabNum = 0;
	foreach($xml->table as $supl_tab) {
		assert('$supl_tab["class"] == "tb_supltrid_3"');

		//nadpisy nad tabulkama
		$den = "";
		$info = "";
		do {
			$tabNum += 1;
			$den = "";
			$info = "";
			foreach($nadpisy[$tabNum] as $p){
				if (strpos($p, 'Suplování:') !== FALSE){
					$den = substr($p, strpos($p, ":")+1);
				}
				else $info = $info . $p . " \n";
			}
			$info = trim($info);
			$prazdne = (strpos($info, "Změny v rozvrzích tříd - nejsou") !== FALSE);
			if($prazdne){
				$info = str_replace(array("Změny v rozvrzích tříd - nejsou", "Změny v rozvrzích učitelů - nejsou",
										"Místnosti mimo provoz - nejsou", "Nepřítomní učitelé - nejsou",
										"Nepřítomné třídy - nejsou"), "", $info);

				$json["dny"][] = array("den" => $den, "info" => $info, "hodiny" => array());
			}
		} while($prazdne);


		//<table rows>
		$hodiny = array();
		$started = false;
		foreach($supl_tab->tr as $supl_tr){

			if((trim($supl_tr->td[0]->p) == $trida) || (empty($supl_tr->td[0]->p) && $started == true)){
				$started = true;
			}else{
				$started = false;
				continue;
			}
			
			$hodina = array();
			$pocetTD = count($supl_tr->td);
			if($pocetTD < 5) continue; //vadný řádek
			$td_num = 0;
			$zmena = "";
			for($td_num = 4; $td_num < $pocetTD; $td_num++){ //načíst změnu v suplování od čtvrté buňky
				assert('$supl_tr->td[$td_num]["class"] == "td_supltrid_3"');
				$zmena = $zmena . trim($supl_tr->td[$td_num]->p) . " ";
			}

			$hodina = array("hodina" => trim(str_replace(".hod", "", $supl_tr->td[1]->p)), 
							"predmet" => trim(trim($supl_tr->td[2]->p) ." ". trim($supl_tr->td[3]->p)),
							"zmena" => trim($zmena));
			$hodiny[] = $hodina;
		}
		$json["dny"][] = array("den" => $den, "info" => $info, "hodiny" => $hodiny);
	}
	echo json_encode($json);

	return true;
}

function getAlergen($num){
	$alergeny = array("", "lepek", "korýši", "vejce", "ryby", "arašídy", "sója", "mléko", "ořechy", "celer", "hořčice", "sezam", "měkkýši", "lupina", "siřičitany");
    if($num >= 1 && $num <= 14){
		return $alergeny[$num];
	}else{
		return "";
	}
}

function jidelna(){
	$xml = getXML('http://gjk.cz/?id=4332');
	if($xml == null) return false;
	
	//zpracuj $xml
	$xml = $xml->xpath("//table[@class = 'info']");
	$xml = $xml[0];

	//načti dny v záhlaví tabulky
	$dny = array();
	$poradi = 0;
	foreach($xml->cols->tr[0]->th as $th){
		$dny[] = (string) $th;
		//oddělit datum mezerou
		$last = $dny[$poradi];
		$pos = -1;
		for($i=0;$i<strlen($last);$i++){
			if($last[$i] >= '0' && $last[$i] <= '9'){
				$pos = $i;
				break;
			}
		}
		assert('$pos != -1');
		$dny[$poradi] = trim(substr($last, 0, $pos) . " " . substr($last, $pos));
		$poradi += 1;
	}
	//var_dump($dny);

	//načti denní menu (jídla) z tabulky
	$menu = array();
	for($i=0;$i<5;$i++){ $menu[$i] = array(); }
	$pocetJidel = count($xml->cols->tr);
	for($line=1;$line<$pocetJidel;$line++){ //počet řádků v tabulce ~ počet jídel
		$token = 0;
		foreach($xml->cols->tr[$line]->td as $td){

			$tmp = trim((string) $td->div);
			$aIND = strpos($tmp, "A:");
			if($aIND === FALSE){
				$aIND = strpos($tmp, "A");
			}
			if(!empty($tmp) && $aIND !== FALSE){ //bez alergenů nepokračujeme, protože v kolonce může být místo jídla např. "Státní svátek"
				$jidlo = trim(substr($tmp, 0, $aIND));
				$jidlo = preg_replace('/\s+/', ' ',$jidlo); //někdy kuchařky vloží třeba 10 mezer mezi 2 slova

				//alergeny
				$alergenyString = "";
				$als = explode(",", substr($tmp, $aIND+2));
				for($i=0;$i<sizeof($als);$i++){
					$k = trim($als[$i]);
					$k = preg_replace("/[^0-9]/", "", $k);

					$newAlerg = getAlergen($k);
					if($newAlerg != ""){
						if($i != 0){ $alergenyString = $alergenyString . ", "; }
						$alergenyString = $alergenyString . $newAlerg;
					}
					
				}
				$menu[$token][] = array("nazev" => $jidlo, "alergeny" => $alergenyString);
			}
			$token = ($token + 1);
		}
	}

	//json print
	$json = array();
	$json["type"] = "jidelna";
	$json["dny"] = array();

	for($den=0;$den<5;$den++){
		$polevka = "";
		$jidla = array();
		foreach($menu[$den] as $jidlo){
			if(empty($polevka)){
				$polevka = $jidlo;
			}else{
				$jidla[] = $jidlo;
			}
		}
		if($polevka == ""){
			$polevka = array("nazev" => "", "alergeny" => "");
		}
		$json["dny"][] = array("den" => $dny[$den], "polevka" => $polevka, "jidla" => $jidla);
	}
	echo json_encode($json);

	return true;
}

function main(){
	if(isset($_GET["type"])){
		$type = htmlspecialchars($_GET["type"]);
		switch($type){
			case "suplovani":
				if(empty($_GET["trida"])) return false; //tento parametr musi byt uveden
				if($_GET["trida"] == "") return false; //a nesmí být prázdný, android aplikace tohle ošetřuje
				
				$trida = htmlspecialchars($_GET["trida"]);
				return suplovani($trida);
			break;

			case "jidelna":
				return jidelna();
			break;

			default: #chyba
				return false;
			break;
		}
		return true; //vse OK
	}else{
		return false;
	}
}

?>
