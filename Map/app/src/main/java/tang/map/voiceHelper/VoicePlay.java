package tang.map.voiceHelper;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by ximing on 2014/12/12.
 */
public class VoicePlay
{
    private MediaPlayer mediaPlayer;
    private String path;
    public VoicePlay()
    {
        this.mediaPlayer = new MediaPlayer();
    }

    public MediaPlayer getMediaPlayer()
    {
        return this.mediaPlayer;
    }
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void startPlaying()
    {
        try {
            this.mediaPlayer.setDataSource(this.path);
            this.mediaPlayer.prepare();
            this.mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopPlaying()
    {
        this.mediaPlayer.stop();
        this.mediaPlayer.release();
    }
}
