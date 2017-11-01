package tk.kadaradam.ws2812bcontroller.Utils;

/**
 * Created by Adam on 8/29/2017.
 */

public class ColorSequences {

    private long _id;
    private int led_from;
    private int led_to;
    private int color;

    public ColorSequences() {
    }

    public ColorSequences(long id, int ledFrom, int ledTo, int Color) {
        this._id = id;
        this.led_from = ledFrom;
        this.led_to = ledTo;
        this.color = Color;
    }

    public ColorSequences(int ledFrom, int ledTo, int Color) {
        this.led_from = ledFrom;
        this.led_to = ledTo;
        this.color = Color;
    }

    public long getID(){
        return this._id;
    }

    public void setID(int id){
        this._id = id;
    }

    public int getLedFrom(){
        return this.led_from;
    }

    public void setLedFrom(int value){
        this.led_from = value;
    }

    public int getLedTo(){
        return this.led_to;
    }

    public void setLedTo(int value){
        this.led_to = value;
    }

    public int getLedColor(){
        return this.color;
    }

    public void setLedColor(int value){
        this.color = value;
    }


}
