package com.example.agendapp.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.agendapp.Clases.Usuario;
import com.example.agendapp.MainActivity;
import com.example.agendapp.R;
import com.example.agendapp.Registro.Register;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {
    //Variables para la consulta json
    RequestQueue rq;
    JsonRequest jrq;

    //variables de las vistas
    EditText userTxt;
    EditText contraseñaTxt;
    TextView usuarioMsg;

    String usuario;
    int cont=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);


        //inicializando las vistas
        iniciar();
    }

    //la funcion "iniciar()", inicializa las vistas declaradas con anterioridad
    public void iniciar(){
        contraseñaTxt=findViewById(R.id.contraseñaTxt);
        userTxt= findViewById(R.id.usuarioTxt);

        usuarioMsg=findViewById(R.id.usuarioMsg);
    }


    /**
     * ZONA DE LISTENERS
     * las siguiente funciones seran los listeners de los botones y demas objetos declarados en el xml correspondiente
     */
    public void IniciarOnClick(View view) {
        //validando datos
        boolean validado=true;
        usuario=userTxt.getText().toString();
        cont=0;
        try{
            cont=Integer.parseInt(contraseñaTxt.getText().toString());
        }catch(Exception e){
            validado=false;
        }
        if(usuario.length()==0){
            validado=false;
            usuarioMsg.setText("Este campo es obligatorio");

        }

        if(Integer.toString(cont).length()!=4){
            validado=false;
        }

        if(validado){
            IniciarSesión(usuario, cont);
        }else{
            Toast.makeText(getApplicationContext(),"No se pudo realizar el inicio de sesión, vuelve a intentarlo",Toast.LENGTH_SHORT).show();
        }
    }

    public void crearOnClick(View view) {
        Intent Registro = new Intent ( this, Register.class );
        Login.this.startActivity(Registro);
    }


    private void IniciarSesión(String user,int pass){
        String url="http://agendapp.atwebpages.com/iniciodeSesion.php?usuario="+user+"&clave="+pass;
        url=url.replace(" ","%20");
        jrq=new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        rq = Volley.newRequestQueue (getApplicationContext ());
        rq.add(jrq);
    }

    @Override
    public void onErrorResponse ( VolleyError error ) {
        Toast.makeText ( getApplicationContext (),"No se pudo conectar con la base de datos"+error.toString(),Toast.LENGTH_SHORT).show ();
    }
    @Override
    public void onResponse ( JSONObject response ) {
        boolean v;

        Usuario user=new Usuario(usuario,cont);

        String nombre="";
        String apellido="";
        String carrera="";
        String ciudad="";
        int edad=0;

        try {
            JSONArray json=response.optJSONArray("Usuario");




            JSONObject jsonObject=json.getJSONObject(0);
            v=jsonObject.optBoolean("Acceso");

            if(v){
                nombre=jsonObject.optString("nombre");
                apellido=jsonObject.optString("apellido");
                carrera=jsonObject.optString("carrera");
                ciudad=jsonObject.optString("ciudad");
                edad=jsonObject.optInt("edad");

                //igual se le asignaran los valores al usuario
                user.setNombre(nombre);
                user.setApellido(apellido);
                user.setCarrera(carrera);
                user.setCiudad(ciudad);
                user.setEdad(edad);

                Toast.makeText(getApplicationContext(),"Se a realizado la verificación con exito",Toast.LENGTH_SHORT ).show();

                //se le asigna a un valor estatico el usuario
                SesionActual.usuarioActual=user;


                //Zona de Intents y nuevas vistas
                /* Gracias a que se declaran justo en el hilo donde se ejecuta la busqueda, la nueva vista vendra crgada y sincronizada con los datos del valor estatico*/

                Intent intent=new Intent(this, MainActivity.class);
                this.startActivity(intent);
                this.finish();
            }else{

                Toast.makeText(getApplicationContext(),"Usuario o contraseña no validos", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }


}
