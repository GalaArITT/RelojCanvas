package com.example.oliver.relojcanvas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private int[] numeros={1,2,3,4,5,6,7,8,9,10,11,12};
    private Rect rect = new Rect();
    private int radio=0;
    public double [][] coordenadas = new double[60][2];
    public MainActivity puntero;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        puntero = this;
        //setContentView(R.layout.activity_main);

        PapelView papel = new PapelView(this,0);
        setContentView(papel);
        papel.invalidate();
        AsyncTarea asyncTarea = new AsyncTarea();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            asyncTarea.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else {
            asyncTarea.execute();
        }
    }

    private void coordenadas(int valor,int a, int b, int xx, int yy) {
        double angler = valor*6*0.0174533;//Math.PI / 6 * (valor - 3);
        coordenadas[0][0]=(xx-a)*Math.cos(angler)-(yy-b)*Math.sin(angler)+a;
        coordenadas[0][1]=(xx-a)*Math.sin(angler)+(yy-b)*Math.cos(angler)+b;
    }

    private class PapelView extends View {

        int num;
        public PapelView(Context context, int num) {
            super(context);
            this.num = num;
        }


        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            int ancho = canvas.getWidth();
            int alto = canvas.getHeight();
            int min = Math.min(alto,ancho);
            int medioX = ancho/2-min/2;
            int medioY = alto/2-min/2;
            radio=min/2-55;
            coordenadas(num,ancho/2,alto/2-25,ancho/2,alto/2-radio-50);
            // Pintar el canvas
            canvas.drawPaint(paint);
            paint.setColor(Color.WHITE);
            canvas.drawCircle(ancho/2,alto/2-25,200,paint);

            // Obtener dimensiones
            paint.setColor(Color.BLACK);
            canvas.drawRect(new Rect(0,0,ancho,alto),paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(30);
            paint.setAntiAlias(true);
            canvas.drawText("ancho: " + ancho + ", altura: " + alto, 30, 30, paint);
            paint.setColor(Color.DKGRAY);
            canvas.drawCircle(ancho/2,alto/2-25,radio+30,paint);
            // Lineas
            paint.setColor(Color.WHITE);
            int dis = 25;
            canvas.drawLine(0, dis, ancho, dis, paint);
            canvas.drawLine(dis, 0, dis, alto, paint);
            canvas.drawLine(0, alto-dis, ancho, alto-dis, paint);
            canvas.drawLine(ancho-dis,0 , ancho-dis, alto, paint);
            paint.setTextSize(50);

            for (int n : numeros) {
                String tmp = String.valueOf(n);
                paint.getTextBounds(tmp, 0, tmp.length(), rect);
                double angle = Math.PI / 6 * (n - 3);
                int x = (int) (ancho / 2 + Math.cos(angle) * radio - rect.width() / 2);
                int y = (int) (ancho / 2 + Math.sin(angle) * radio - rect.width() / 2);
                canvas.drawText(tmp, x, y+medioY, paint);
            }
            canvas.drawLine(ancho/2,alto/2-25,(int)coordenadas[0][0],(int)coordenadas[0][1],paint);
        }
    }

    private void UnSegundo() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class  AsyncTarea extends AsyncTask<Void, Integer,Boolean> {
        public AsyncTarea(){
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            int i = 0;
            while(true){
                UnSegundo();
                publishProgress(i);
                if(i == 60){
                    i = 0;
                }
                if (isCancelled()){
                    break;
                }
                i++;
            }
            return true;
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            PapelView papel2 = new PapelView(puntero,values[0]);
            puntero.setContentView(papel2);
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            //super.onPostExecute(aVoid);
            if (aVoid){
                Toast.makeText(getApplicationContext(),"Tarea Terminada",Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(getApplicationContext(),"Tarea NO finaliza AsyncTask",Toast.LENGTH_SHORT).show();
        }
    }
}
