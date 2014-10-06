package gjk.kepler;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class Home extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
    public void showOK(View w){
        Toast.makeText(this, "Aktualizuji...", Toast.LENGTH_LONG).show();
        //tohle presunout do action_refresh v menu
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
                //refreshPage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
