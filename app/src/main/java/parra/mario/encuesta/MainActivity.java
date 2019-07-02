package parra.mario.encuesta;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Date fecha;
    String fecha_str, nombre_archivo, fecha_view;
    HorizontalBarChart horizontalBarChart;
    TextView tv_fecha;
    int datos[] = {0,0,0,0,0,0,0,0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Encuesta.class);
//                intent.putExtra("action","nuevo");
                intent.putExtra("fecha",fecha_str);
                intent.putExtra("nombre", nombre_archivo);
                startActivity(intent);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        tv_fecha = findViewById(R.id.tv_fecha);
        fecha = new Date();
        estableceFecha(fecha);

        horizontalBarChart = findViewById(R.id.horizontal_chart);

        leerDatos();
        mostrarGrafica();

    }

    private void mostrarGrafica(){
        horizontalBarChart.setDrawBarShadow(false);

        Description descripcion = new Description();
        descripcion.setText("");
        horizontalBarChart.setDescription(descripcion);
        horizontalBarChart.getLegend().setEnabled(false);
        horizontalBarChart.setPinchZoom(false);
        horizontalBarChart.setDrawValueAboveBar(false);


        XAxis xAxis = horizontalBarChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setEnabled(true);
        xAxis.setDrawAxisLine(false);

        YAxis yAxis = horizontalBarChart.getAxisLeft();
        yAxis.setAxisMaximum(5);
        yAxis.setAxisMinimum(0);
        yAxis.setEnabled(false);

//        xAxis.setLabelCount(8);
//
//        ArrayList<String> valores = new ArrayList<String>();
//        valores.add("Frutas");
//        valores.add("Verduras");
//        valores.add("Cereales");
//        valores.add("Leguminosas");
//        valores.add("Carnes");
//        valores.add("Mariscos");
//        valores.add("Ultraprocesados");
//        valores.add("B. Azucaradas");
        String[] nombres = {"B. Azucaradas","Ultraprocesados","Mariscos","Carnes","Leguminosas","Cereales","Verduras","Frutas"};

        xAxis.setValueFormatter(new XAxisValueFormatter(nombres));

        YAxis yAxisRight = horizontalBarChart.getAxisRight();
        yAxisRight.setDrawAxisLine(true);
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setEnabled(false);

        setDatosGrafica();

        horizontalBarChart.animateY(2000);


    }

    private void setDatosGrafica() {

        ArrayList<BarEntry> entradas = new ArrayList<BarEntry>();
        entradas.add(new BarEntry(0, datos[7]));
        entradas.add(new BarEntry(1, datos[6]));
        entradas.add(new BarEntry(2, datos[5]));
        entradas.add(new BarEntry(3, datos[4]));
        entradas.add(new BarEntry(4, datos[3]));
        entradas.add(new BarEntry(5, datos[2]));
        entradas.add(new BarEntry(6, datos[1]));
        entradas.add(new BarEntry(7, datos[0]));

        BarDataSet barDataSet = new BarDataSet(entradas,"Datos");

        barDataSet.setValueFormatter(new MyValueFormatter());

//        barDataSet.setColors(R.color.primary, R.color.primary_dark,
//                R.color.primary_light, R.color..accent, R.color.colorPrimaryDark,
//                R.color.colorPrimary, R.color.primaryTextColor, R.color.primaryLightColor);
        horizontalBarChart.setDrawBarShadow(true);
        barDataSet.setBarShadowColor(Color.argb(40,150,150,150));
//        horizontalBarChart.setDrawBarShadowColor(Color.argb(40,150,150,150));
        BarData barData = new BarData(barDataSet);

//        horizontalBarChart.setVa


        horizontalBarChart.setData(barData);
        horizontalBarChart.invalidate();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    class XAxisValueFormatter implements IAxisValueFormatter{

        private String[] values;

        public XAxisValueFormatter(String[] values){
            this.values = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {

            return this.values[(int) value];
        }
    }

    public class MyValueFormatter implements IValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,##0"); // use one decimal
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            // write your logic here
            return mFormat.format(value); // e.g. append a dollar-sign
        }
    }

    public void fecha_izq(View v){
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        cal.add(Calendar.DATE, -1); //minus number would decrement the days
        fecha = cal.getTime();

        estableceFecha(fecha);
        leerDatos();
        mostrarGrafica();
    }

    public void fecha_der(View v){
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        cal.add(Calendar.DATE, 1); //minus number would decrement the days
        fecha = cal.getTime();

        estableceFecha(fecha);
        leerDatos();
        mostrarGrafica();
    }

    private void estableceFecha(Date fecha){
        SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
        fecha_str = formatoFecha.format(fecha);

        SimpleDateFormat formatoNombre = new SimpleDateFormat("HH-mm-ss");
        nombre_archivo = "enc"+formatoFecha.format(fecha);

        SimpleDateFormat formatoFechaView = new SimpleDateFormat("dd/MM/yyyy");
        fecha_view = formatoFechaView.format(fecha);

        tv_fecha.setText(fecha_view);


    }

    private void leerDatos(){
        limpiarDatos();
        try{
            File texto = new File(ubicacionCarpeta(),nombre_archivo + ".txt");
            if (texto.exists()){
                leerArchivo(texto);
            }

        }catch (Exception e ){
            Toast.makeText(this,"No se encontró el texto", Toast.LENGTH_LONG).show();
        }
    }

    private void limpiarDatos(){
        datos[0] = 0;
        datos[1] = 0;
        datos[2] = 0;
        datos[3] = 0;
        datos[4] = 0;
        datos[5] = 0;
        datos[6] = 0;
        datos[7] = 0;
    }

    private void leerArchivo(File archivo){
//        limpiarDatos();
        int cont = 0;
        try{
            FileInputStream fis = new FileInputStream(archivo);
            DataInputStream dis = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(dis));

            String linea = br.readLine();
            String texto = "";
            while(linea != null){

                texto = texto + linea;
                linea = br.readLine();
                String dato = linea.substring(linea.indexOf(",")+1);
                datos[cont] = Integer.parseInt(dato);
                cont++;
            }
            br.close();
            dis.close();
            fis.close();


//            et_desc.setText(texto);

        }catch (Exception e){

        }





    }

    private String ubicacionCarpeta(){
        File carpeta = new File(Environment.getExternalStorageDirectory(),"PotroSaludable");
        if (!carpeta.exists()){
            carpeta.mkdir();
        }
        File dia = new File(carpeta, fecha_str);
        if(!dia.exists()){
            //dia.mkdir();
            return "";
        }
        return dia.getAbsolutePath();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        leerDatos();
        mostrarGrafica();
    }
}
