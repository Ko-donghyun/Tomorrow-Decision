package k.tomorrowdecision;

public class TodoItem {
    private int id;
    private String time;
    private String todo;
    private String textColorCode;
    private String backgroundColorCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTodo() {
        return todo;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

    public String getTextColorCode() {
        return textColorCode;
    }

    public void setTextColorCode(String textColorCode) {
        this.textColorCode = textColorCode;
    }

    public String getBackgroundColorCode() {
        return backgroundColorCode;
    }

    public void setBackgroundColorCode(String backgroundColorCode) {
        this.backgroundColorCode = backgroundColorCode;
    }
}