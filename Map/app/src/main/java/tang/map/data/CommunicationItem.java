package tang.map.data;

/**
 * Created by wk on 2015/1/12.
 */
public class CommunicationItem {
    private int userid;
    private int sendOrRec;  //send为0,rec为1
    private int type;
    private String content;
    private String date;



    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getSendOrRec() {
        return sendOrRec;
    }

    public void setSendOrRec(int sendOrRec) {
        this.sendOrRec = sendOrRec;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
