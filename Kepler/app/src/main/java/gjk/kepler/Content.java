package gjk.kepler;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Content extends Fragment {

    public static final String ARG_CONTENT_NUMBER = "content_number";

    public Content() {
        // Nutně prázdný pro třídy dědící Fragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);
        TextView tv = (TextView) rootView.findViewById(R.id.text_content);

        int i = getArguments().getInt(ARG_CONTENT_NUMBER);
        tv.setText("ARG C NUMBER JE" +i);

        return rootView;
    }
}
/*
<!-- Nastavení po rozkliknutí menu
        -nastaveni bude ve vytahovaci liste vlevo
        -bude tam mit ikonu ozubeneho kola

        -v tomto levem menu po vytazeni budou take polozky:
        -suplovani
        -obedy
        -nastaveni
        -a vse bude s ikonami - ikona priboru pro obedy
        -podobne jako v google aplikacich jako gmail, gdocs apod

        -->

<!-- O aplikaci
        -tato polozka bude primo v aktivite "nastaveni"

        -->
      */
