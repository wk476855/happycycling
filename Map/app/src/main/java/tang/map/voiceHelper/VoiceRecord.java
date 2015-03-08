package tang.map.voiceHelper;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;

import java.io.IOException;

/**
 * Created by ximing on 2014/12/12.
 */
public class VoiceRecord
{
    private MediaRecorder mediaRecorder;
    private String path;
    private Context context;
    private Handler handler;
    public VoiceRecord(Context context)
    {
        this.context = context;
        this.mediaRecorder = new MediaRecorder();
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public boolean startRecord()
    {
        this.mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        this.mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        this.mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        this.mediaRecorder.setOutputFile(path);
        try
        {
            this.mediaRecorder.prepare();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        try {
            mediaRecorder.start();
        }catch (IllegalStateException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean stopRecord()
    {
        boolean result = true;
        try {
            this.mediaRecorder.stop();
        }catch (IllegalStateException e)
        {
            result = false;
        }finally {
            this.mediaRecorder.release();
        }
        return result;
    }
}
