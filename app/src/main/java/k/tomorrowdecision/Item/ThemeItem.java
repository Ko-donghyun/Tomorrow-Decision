package k.tomorrowdecision.Item;

public class ThemeItem {
    private long id;
    private String title;
    private String subTitle;
    private String[] importanceColorCodes = new String[10];
    private int usable;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String[] getImportanceColorCodes() {
        return importanceColorCodes;
    }

    public void setImportanceColorCodes(String[] importanceColorCodes) {
        this.importanceColorCodes = importanceColorCodes;
    }

    public int getUsable() {
        return usable;
    }

    public void setUsable(int usable) {
        this.usable = usable;
    }
}