package gjk.kepler;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class Food extends Activity {
    ////////////////tohle jeste neni vubec hotove - udelej to podobne jako Home
    ////////////////jeste vygooglit, jestli se u Androidu implementuje metoda pro menu pro kazdou aktivitu zvlast...

    /*Intent intent = new Intent(getApplicationContext(), gjk.kepler.Food.class);
                startActivity(intent);
    */
    private String url_obedy = "http://gjk.cz/?id=4332";
    private TextView text_obedy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);
        text_obedy = (TextView) findViewById(R.id.text_obedy);

    }


    //tady by to chtelo jen odkazat na uz hotove metody v tride Home !!!!!!!!!!!!!!!!!!!!!!!!!!!
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //vytvoří položky v horní liště (action bar)
        getMenuInflater().inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }
}
