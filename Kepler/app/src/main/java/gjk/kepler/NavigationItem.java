package gjk.kepler;

public class NavigationItem {
    private String title;
    private int icon;
    private boolean activated;

    public NavigationItem(String title, int icon){
        this.title = title;
        this.icon = icon;
        this.activated = false;
    }

    public int getIcon(){
        return this.icon;
    }
    public String getTitle(){
        return this.title;
    }
    public boolean getActivated() { return this.activated; }

    public void setActivated(boolean state) { this.activated = state; }
}
