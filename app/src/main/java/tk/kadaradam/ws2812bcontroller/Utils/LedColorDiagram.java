package tk.kadaradam.ws2812bcontroller.Utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Adam on 10/8/2017.
 */

public class LedColorDiagram {

    private int padding;
    private int width;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;

    public LedColorDiagram(int width, int height, int padding, int padding_color) {

        this.padding    = padding;
        this.width      = width;

        this.bitmap = Bitmap.createBitmap(
                width + padding, // Width
                height, // Height
                Bitmap.Config.ARGB_8888 // Config
        );

        this.canvas = new Canvas(bitmap);

        if(padding_color != -1)
            this.canvas.drawColor(padding_color);

        this.paint = new Paint();
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setAntiAlias(true);


    }

    public void addItem(int color, int x, int y) {
        Rect rectangle = new Rect(
                x + this.padding, // Left
                this.padding, // Top
                y, // Right
                this.canvas.getHeight() - this.padding // Bottom
        );
        RectF rectF = new RectF(rectangle);
        this.paint.setColor(color);
        this.canvas.drawRoundRect(rectF, this.width / 2, this.width / 2, this.paint);
    }

    public void showOnImage(ImageView view) {
        view.setImageBitmap(this.bitmap);
    }
}
