package tang.map.data;

/**
 * Created by wk on 2014/11/29.
 */
public class Protocol {

    public static final Integer REGISTER_SEND_CMD = new Integer( 101);
    public static final Integer REGISTER_REC_CMD = new Integer( 102);

    public static final Integer LOGIN_SEND_CMD = new Integer( 103);
    public static final Integer LOGIN_REC_CMD = new Integer( 104);

    public static final Integer HEART_SEND_CMD = new Integer(105);
    public static final Integer HEART_REC_CMD = new Integer(106);

    public static final Integer UPDATELOC_SEND_CMD = new Integer( 107);
    public static final Integer UPDATELOC_REC_CMD = new Integer( 108);

    public static final Integer ASKLOC_SEND_CMD = new Integer( 109);
    public static final Integer ASKLOC_REC_CMD = new Integer( 110);

    public static final Integer MESSAGE_SEND_CMD = new Integer( 111);
    public static final Integer MESSAGE_SENDR_CMD = new Integer( 112);
    public static final Integer MESSAGE_REC_CMD = new Integer( 113);
    public static final Integer MESSAGE_RECR_CMD = new Integer( 114);

    public static final Integer USERINFOC_SEND_CMD = new Integer(115);
    public static final Integer USERINFOC_REC_CMD = new Integer(116);

    public static final Integer USERINFOU_SEND_CMD = new Integer(117);
    public static final Integer USERINFOU_REC_CMD = new Integer(118);

    public static final Integer CREATE_REC_CMD = new Integer( 201);
    public static final Integer CREATE_SEND_CMD = new Integer( 200);

    public static final Integer UPDATEINFO_SEND_CMD = new Integer( 202);
    public static final Integer UPDATEINFO_REC_CMD = new Integer( 203);

    public static final Integer JOIN_SEND_CMD = new Integer( 204);
    public static final Integer JOIN_REC_CMD = new Integer( 205);

    public static final Integer REQUEST_REC_CMD = new Integer( 206);
    public static final Integer ACCEPT_REC_SEND = new Integer( 207);

    public static final Integer RESPONSE_REC_CMD = new Integer(208);
    public static final Integer RESPONSE_SEND_CMD = new Integer(209);

    public static final Integer QUIT_SEND_CMD = new Integer( 210);
    public static final Integer QUIT_REC_CMD = new Integer( 211);

    public static final Integer KICKOUT_SEND_CMD = new Integer( 212);
    public static final Integer KICKOUT_REC_CMD = new Integer( 213);

    public static final Integer SHOWTEAM_SEND_CMD = new Integer( 214);
    public static final Integer SHOWTEAM_REC_CMD = new Integer( 215);

    public static final Integer GETINFO_SEND_CMD = new Integer( 216);
    public static final Integer GETINFO_REC_CMD = new Integer( 217);

    public static final Integer QUITR_REC_CMD = new Integer( 218);
    public static final Integer QUITR_SEND_CMD = new Integer( 219);


    public static final Integer HELP_SEND_CMD = new Integer( 222);

    public static final Integer SHARE_SEND_CMD = new Integer(300);
    public static final Integer SHARE_REC_CMD = new Integer(301);

    public static final Integer GETSHARE_SEND_CMD = new Integer(302);
    public static final Integer GETSHARE_REC_CMD = new Integer(303);

    public static Integer getProtocol(int cmd){
        switch (cmd){
            case 101:
                return REGISTER_SEND_CMD;
            case 102:
                return REGISTER_REC_CMD;
            case 103:
                return LOGIN_SEND_CMD;
            case 104:
                return LOGIN_REC_CMD;
            case 105:
                return HEART_SEND_CMD;
            case 106:
                return HEART_REC_CMD;
            case 107:
                return UPDATELOC_SEND_CMD;
            case 108:
                return UPDATELOC_REC_CMD;
            case 109:
                return ASKLOC_SEND_CMD;
            case 110:
                return ASKLOC_REC_CMD;
            case 111:
                return MESSAGE_SEND_CMD;
            case 112:
                return MESSAGE_SENDR_CMD;
            case 113:
                return MESSAGE_REC_CMD;
            case 114:
                return MESSAGE_RECR_CMD;
            case 115:
                return USERINFOC_SEND_CMD;
            case 116:
                return USERINFOC_REC_CMD;
            case 117:
                return USERINFOU_SEND_CMD;
            case 118:
                return USERINFOU_REC_CMD;
            case 200:
                return CREATE_SEND_CMD;
            case 201:
                return CREATE_REC_CMD;
            case 202:
                return UPDATEINFO_SEND_CMD;
            case 203:
                return UPDATEINFO_REC_CMD;
            case 204:
                return JOIN_SEND_CMD;
            case 205:
                return JOIN_REC_CMD;
            case 206:
                return REQUEST_REC_CMD;
            case 207:
                return ACCEPT_REC_SEND;
            case 208:
                return RESPONSE_REC_CMD;
            case 209:
                return RESPONSE_SEND_CMD;
            case 210:
                return QUIT_SEND_CMD;
            case 211:
                return QUIT_REC_CMD;
            case 212:
                return KICKOUT_SEND_CMD;
            case 213:
                return KICKOUT_REC_CMD;
            case 214:
                return SHOWTEAM_SEND_CMD;
            case 215:
                return SHOWTEAM_REC_CMD;
            case 216:
                return GETINFO_SEND_CMD;
            case 217:
                return GETINFO_REC_CMD;
            case 218:
                return QUITR_REC_CMD;
            case 219:
                return QUITR_SEND_CMD;
            case 222:
                return HELP_SEND_CMD;
            case 300:
                return SHARE_SEND_CMD;
            case 301:
                return SHARE_REC_CMD;
            case 302:
                return GETSHARE_SEND_CMD;
            case 303:
                return GETSHARE_REC_CMD;
        }
        return null;
    }
}
