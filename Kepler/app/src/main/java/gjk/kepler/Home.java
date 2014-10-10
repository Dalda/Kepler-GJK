package gjk.kepler;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class Home extends Activity {

    private String url_suplovani = "http://old.gjk.cz/suplovani.php";
    private TextView text_suplovani;
    private HTML_Loader myHTML;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        text_suplovani = (TextView) findViewById(R.id.text_suplovani);
        myHTML = new HTML_Loader(this);
        myHTML.refreshPage(url_suplovani); //načti suplování při prvním spuštění
    }

    public void show(String s){
        text_suplovani.setText(s);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //vytvoří položky v horní liště (action bar)
        getMenuInflater().inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Akce po kliknutí na jednotlivé položky v horní liště (action bar)
        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                myHTML.refreshPage(url_suplovani);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
