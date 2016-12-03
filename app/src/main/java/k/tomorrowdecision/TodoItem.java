package k.tomorrowdecision;

public class TodoItem {
    private String time;
    private String timeText;
    private String todo;
    private String textColorCode;
    private String backgroundColorCode;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimeText() {
        return timeText;
    }

    public void setTimeText(String timeText) {
        this.timeText = timeText;
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