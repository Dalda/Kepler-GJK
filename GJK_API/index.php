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
	foreach($xml->p as $par){ //naplněnní pole nadpisy (text před každou tabulkou suplování)
		$p = (string) $par;

		if(strpos($p, 'Bakaláři') !== FALSE) continue;
		if(strpos($p, 'datum výpisu') !== FALSE) continue;

		$novyNadpis = trim($p);
		$obsahuje = FALSE;
		for($indCounter = 0; $indCounter <= $counter; $indCounter++){ //kontrola kvuli opakovanemu popisu (nad i pod tabulkou)
			if(in_array($novyNadpis, $nadpisy[$indCounter])){
				$obsahuje = TRUE;
			}
		}
		if(!$obsahuje){
			$nadpisy[$counter][] = $novyNadpis; 
			if (strpos($novyNadpis, 'Suplování:') !== FALSE){
				$counter += 1;
				$nadpisy[$counter] = array();
			}
		}
		
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
			if($pocetTD < 2) continue; //hodně vadný řádek
			if($pocetTD < 5){ //$pocetTD >= 2
				 //vadný řádek, resp. neni normalni zmena suplovani - napr jen avs, je to vyjimka, ale uzivatel o ni musi vedet
				$zmena = "";
				for($td_num = 1; $td_num < $pocetTD; $td_num++){ //načíst řádek
					assert('$supl_tr->td[$td_num]["class"] == "td_supltrid_3"');
					$zmena = $zmena . trim($supl_tr->td[$td_num]->p) . " ";
				}
				$hodina = array("hodina" => -1, 
								"predmet" => "",
								"zmena" => trim($zmena));
				$hodiny[] = $hodina;
				continue;
			}
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

function getAlergeny($str){ //nazev" "A:1, 2 ,3 
	$aIND = strpos($str, "A:");
	if($aIND === FALSE){
		$aIND = strpos($str, "A");
	}
	if(!empty($str) && $aIND !== FALSE){ //bez alergenů nepokračujeme, protože v kolonce může být místo jídla např. "Státní svátek"
		$jidlo = trim(substr($str, 0, $aIND));
		$jidlo = preg_replace('/\s+/', ' ',$jidlo); //odstranění více mezer

		//alergeny
		$alergenyString = "";
		$als = explode(",", substr($str, $aIND+2));
		for($i=0;$i<sizeof($als);$i++){
			$k = trim($als[$i]);
			$k = preg_replace("/[^0-9]/", "", $k);

			$newAlerg = getAlergen($k);
			if($newAlerg != ""){
				if($i != 0){ $alergenyString = $alergenyString . ", "; }
				$alergenyString = $alergenyString . $newAlerg;
			}
			
		}
		return array("nazev" => $jidlo, "alergeny" => $alergenyString);
	}
	return null;
}

function jidelna(){
	$csv = file_get_contents('https://docs.google.com/spreadsheets/d/1JpEUpUJ3slFP1y2PgJV1J_2_sBf5VOek4TUcq90P_Cs/export?format=csv');
	if($csv == null) return false;
	//var_dump($csv);

	$denID = array("Pondělí", "Úterý", "Středa", "Čtvrtek", "Pátek");
	$pointDen = 0;
	$lines = explode("\n", $csv);
	$menu = array();
	for($i=0;$i<count($lines);$i++){
		if(strpos($lines[$i], $denID[$pointDen]) !== FALSE){
			$menu[$pointDen] = array();

			//den a polevka 1radek
			$iA = strpos($lines[$i], "\"");
			$iB = strrpos($lines[$i], "\"");
			$polevka = "";
			if($iA !== FALSE && $iB !== FALSE){
				$polevka = substr($lines[$i], $iA+1, $iB-$iA);
			}
			
			//datum 2radek
			$datum = $lines[$i+1];
			$dA = 0;
			while(!($datum[$dA] >= '0' && $datum[$dA] <= '9')) $dA++;
			$dB = $dA + 1;
			while(!($datum[$dB] == ',')) $dB++;

			//jogurt/zelenina/ovoce apod misto polevky
			$alterPolevka = null;
			if(strpos($lines[$i+1], "nebo") !== FALSE){
				$aA = strpos($lines[$i+1], "nebo")+4;
				$zbytekRadku = substr($lines[$i+1], $aA);
				$aA = 0;
				while($aA < strlen($zbytekRadku) && !ctype_alpha($zbytekRadku[$aA])) $aA++;

				if($aA < strlen($zbytekRadku)){
					$aB = $aA+1;
					while($aB < strlen($zbytekRadku) && $zbytekRadku[$aB] != ",") $aB++;

					$alterPolevka = substr($zbytekRadku, $aA, $aB-$aA);
					$altInd = strpos($alterPolevka, "A:");
					if($altInd !== FALSE){
						$alterPolevka = substr($alterPolevka, 0, $altInd);
					}
				}
			}

			$menu[$pointDen]["den"] = $denID[$pointDen] . " " . substr($datum, $dA, $dB-$dA);

			//jidla
			$jidla = array();
			$k = $i+2;
			while(strpos($lines[$k], "\"") !== FALSE){
				$iA = strpos($lines[$k], "\"");
				$iB = strrpos($lines[$k], "\"");
				$jidla[] = substr($lines[$k], $iA+1, $iB-$iA);
				$k++;
			}

			//prirazeni alergenu k polevce
			$jidlo = getAlergeny($polevka);
			if($jidlo != null){
				if($alterPolevka != null){
					$jidlo["nazev"] = $jidlo["nazev"] . " (nebo ".trim($alterPolevka) .")";
				}
				$menu[$pointDen]["polevka"] = $jidlo;
			}else{ //aplikace pak polevku nezobrazi (uz mame defin. API)
				$menu[$pointDen]["polevka"] = array("nazev" => "", "alergeny" => ""); 
			}
			
			$menu[$pointDen]["jidla"] = array();
			foreach($jidla as $j){
				$jidlo = getAlergeny($j);
				if($jidlo != null){
					$menu[$pointDen]["jidla"][] = $jidlo;
				}
			}
			
			$i = $k-1;
			$pointDen += 1;
		}
	}

	//json print
	$json = array();
	$json["type"] = "jidelna";
	$json["dny"] = $menu;
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
