package com.example.fasipemobilej;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.fasipemobilej.database.SQLDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public ListView viewListarTodos;

    private ImageView imagem;

    Button btnCam;

    private AppCompatButton telaNovoDocumento;



    private SQLiteDatabase dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);


            try{
                SQLDatabase databaseSQL = new SQLDatabase();
                dataBase = openOrCreateDatabase("appCaptura", MODE_PRIVATE, null);
                dataBase.execSQL(databaseSQL.sqlCriarTabelaPaciente());
                dataBase.execSQL(databaseSQL.sqlCriarTabelaProfissional());
                dataBase.execSQL(databaseSQL.sqlCriarTabelaProcedimento());
                dataBase.execSQL(databaseSQL.sqlCriarTabelaProntuario());
                dataBase.close();
            }catch (Exception e){
                e.printStackTrace();
            }

        viewListarTodos = (ListView) findViewById(R.id.viewListarTodos);

        listarTodos();

        adicionarDocumento();

        telaNovoDocumento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,MainAdd.class);
                startActivity(intent);
            }
        });

    }

    private void adicionarDocumento(){
        telaNovoDocumento = findViewById(R.id.telaNovoDocumento);
    }





    public void salvarArquivo(){

    }

    public void listarTodos(){

        try{
            dataBase = openOrCreateDatabase("appCaptura", MODE_PRIVATE, null);
            Cursor consulta = dataBase.rawQuery("select * from prontuario", null);
            ArrayList<Object> retorno = new ArrayList<>();
            ArrayAdapter<Object> adp = new ArrayAdapter<Object>(this, android.R.layout.simple_list_item_1,android.R.id.text1,retorno);
            viewListarTodos.setAdapter(adp);
            consulta.moveToFirst();
            while (consulta != null){
                retorno.add(consulta.getInt(1));
                retorno.add(consulta.getInt(2));
                retorno.add(consulta.getInt(3));
                retorno.add(consulta.getInt(4));
                retorno.add(consulta.getString(4));
                retorno.add(consulta.getString(5));
                retorno.add(consulta.getString(6));
                retorno.add(consulta.getInt(7));

                consulta.moveToNext();
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }




}