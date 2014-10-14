<?php

phpinfo();
echo "Init</br>";
$OK = main();
echo $OK;

function getXML($url){
	$html = file_get_contents($url); #allow_url_fopen must be true in the php.ini
	if($html !== FALSE){
		$doc = new DOMDocument();
		$doc->loadHTML($html); //ze string promenne
		$xml = simplexml_import_dom($doc);
		return $xml;
	}
	else return null;
}

function main(){
	if(isset($_GET["type"])){
		$type = htmlspecialchars($_GET["type"]);
		echo "Nastaven type: ".$type ."</br>";
		switch($type){
			case "suplovani":
				if(!isset($_GET["name"])) return false; //tento parametr musi byt uveden

				$name = htmlspecialchars($_GET["name"]);

				$xml = getXML('http://old.gjk.cz/suplovani.php');
				if($xml == null) return false;

				print_r($xml);
				echo $xml->getName() . "<br>";
	    		foreach($xml->children() as $child) {
	      			echo $child->getName() . ": " . $child . "<br>";
	    		}
			break;

			case "jidelna":
				$xml = getXML('http://gjk.cz/?id=4332');
				if($xml == null) return false;
				
				print_r($xml);
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
