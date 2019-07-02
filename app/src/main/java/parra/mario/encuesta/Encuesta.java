package parra.mario.encuesta;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Encuesta extends AppCompatActivity {

    private int encuesta;
    String fecha_str, nombre_archivo;
    private int CODIGO_GUARDAR = 223;

    int[] IMAGES = {R.drawable.apple,  R.drawable.carrot, R.drawable.corn, R.drawable.soja, R.drawable.steak, R.drawable.fish, R.drawable.icon_papitas, R.drawable.icon_soda};

    String[] TYPES = {"Frutas", "Verduras", "Cereales", "Leguminosas", "Carnes", "Mariscos", "Ultraprocesados", "Bebidas azucaradas"};

    int[]   CANT = {0, 0, 0, 0, 0, 0, 0, 0};

    CustomAdapter customAdapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encuesta);

        listView = (ListView) findViewById(R.id.lista);
        customAdapter = new CustomAdapter();

        listView.setAdapter(customAdapter);

        Bundle bundle = getIntent().getExtras();

        estableceFecha(new Date());

        if (bundle != null){
            fecha_str = bundle.getString("fecha");
            nombre_archivo = bundle.getString("nombre");
        }
    }

    private void estableceFecha(Date fecha){
        SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
        fecha_str = formatoFecha.format(fecha);

        SimpleDateFormat formatoNombre = new SimpleDateFormat("HH-mm-ss");
        nombre_archivo = "enc"+formatoFecha.format(fecha);


    }

    public void enviar(View v){

//        Date c = Calendar.getInstance().getTime();
//        System.out.println("Current time => " + c);

//        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
//        String formattedDate = df.format(c);

        Map<String,String> map = new HashMap<>();
        map.put("frutas", String.valueOf(CANT[0]));
        map.put("verduras",String.valueOf(CANT[1]));
        map.put("cerelaes",String.valueOf(CANT[2]));
        map.put("leguminosas",String.valueOf(CANT[3]));
        map.put("carnes",String.valueOf(CANT[4]));
        map.put("mariscos",String.valueOf(CANT[5]));
        map.put("ultraprocesados",String.valueOf(CANT[6]));
        map.put("azucaradas",String.valueOf(CANT[7]));





    }

    public void permisos_guardar(View v){

        //Verifica los permisos
        //A partir de la versión de Android de Marshmallow
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                //no tiene los permisos
                String[] permisos = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

                //muestra ventana para pedir los permisos al usuario
                requestPermissions(permisos, CODIGO_GUARDAR);
            }else{
                //tiene los permisos
                guardar();
            }
        }else{
            //No es necesario pedir los permisos directamente
            guardar();
        }


    }

    private void guardar(){
            try{
                File archivo = new File(ubicacionCarpeta(), nombre_archivo+".txt");
                FileOutputStream fos = new FileOutputStream(archivo);
                String msj = fecha_str + "\n";
                msj += "frutas," + CANT[0] + "\n";
                msj += "verduras," + CANT[1] + "\n";
                msj += "cereales," + CANT[2] + "\n";
                msj += "leguminosas," + CANT[3] + "\n";
                msj += "carnes," + CANT[4] + "\n";
                msj += "mariscos," + CANT[5] + "\n";
                msj += "ultraprocesados," + CANT[6] + "\n";
                msj += "azucaradas," + CANT[7];

                fos.write(msj.getBytes());

                fos.close();
                Toast.makeText(this,"Se guardó el archivo", Toast.LENGTH_SHORT).show();
            }catch(Exception e){
                Toast.makeText(this,"Error al guardar la descripción", Toast.LENGTH_SHORT).show();
            }

            finish();

    }

    private String ubicacionCarpeta(){
        File carpeta = new File(Environment.getExternalStorageDirectory(),"PotroSaludable");
        if (!carpeta.exists()){
            carpeta.mkdir();
        }
        File dia = new File(carpeta, fecha_str);
        if(!dia.exists()){
            dia.mkdir();
        }
        return dia.getAbsolutePath();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

//        switch (requestCode){
//            case CODIGO_GUARDAR:
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            guardar();
        } else {
            Toast.makeText(this, "Permisos denegados", Toast.LENGTH_SHORT).show();
        }
        ;
//            break;
//        }
    }

    public void cancelar(View v){
        finish();
    }




    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            int count = 0;
            if (encuesta == 0){
                count = IMAGES.length;
            }

            return count;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.adapter_view_layout, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.image_icon);
            TextView txt_type = (TextView) view.findViewById(R.id.txt_tipo);
            final TextView txt_quant = (TextView) view.findViewById(R.id.txt_quantity);
            SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBar);
            final int x = i;
            if (encuesta == 0){
                imageView.setImageResource(IMAGES[i]);
                txt_type.setText(TYPES[i]);
                seekBar.setProgress(CANT[i]);
                txt_quant.setText(String.valueOf(CANT[i]));
            }

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    // TODO Auto-generated method stub
                    txt_quant.setText(String.valueOf(progress));
                    if (progress == seekBar.getMax()){
                        txt_quant.setText(String.valueOf(progress) + "+");
                    }
                    if (encuesta == 0){
                        CANT[x]= seekBar.getProgress();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub
                }
            });

            return view;
        }


    }
}
