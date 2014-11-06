package gjk.kepler;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class NavigationAdapter extends ArrayAdapter<NavigationItem>{

    private Context context;
    private ArrayList<NavigationItem> navigationItems;

    public NavigationAdapter(Context context, int resource, int textViewID, ArrayList<NavigationItem> navigationItems){
        super(context, resource, textViewID, navigationItems);
        this.context = context;
        this.navigationItems = navigationItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.drawer_list_item, null);
        }

        ImageView navIcon = (ImageView) convertView.findViewById(R.id.navigationIcon);
        TextView navTitle = (TextView) convertView.findViewById(R.id.navigationTitle);

        navIcon.setImageResource(navigationItems.get(position).getIcon());
        if(navigationItems.get(position).getActivated()){
            navTitle.setTypeface(Typeface.DEFAULT_BOLD);
        }else{
            navTitle.setTypeface(Typeface.DEFAULT);
        }
        navTitle.setText(navigationItems.get(position).getTitle());

        return convertView;
    }

}
