package tudolist.studio.dennis.com.tudolist;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button btnAdicionar;
    private ListView listaTarefas;
    private EditText tarefa;
    private SQLiteDatabase bancoDados;
    private ArrayAdapter<String> itensAdaptador;
    private ArrayList<String>itens;
    private ArrayList<Integer> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try
        {
            //Capturando as Views
            btnAdicionar = (Button) findViewById(R.id.adicionarId);
            tarefa = (EditText) findViewById(R.id.tarefaId);
            listaTarefas = (ListView) findViewById(R.id.listaItens);

            //Criando um banco de dados Sqlitle
            bancoDados = openOrCreateDatabase("appTarefas", MODE_PRIVATE,null);
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS tblTarefas (id INTEGER PRIMARY KEY AUTOINCREMENT, tarefa VARCHAR )");

            //Adicionando tarefa ao ListView
            btnAdicionar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String tarefaDigitada = tarefa.getText().toString();
                    salvarTarefa(tarefaDigitada);
                }
            });
            //Deletando Tarefa ao clicar por alguns segundos no item
            listaTarefas.setLongClickable(true);
            listaTarefas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    removerTarefa(ids.get(i));
                    recuperarTarefas();
                    return true;
                }
            });

            recuperarTarefas();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    private void salvarTarefa(String tarefaDigitada)
    {
            try
            {
                if(!tarefaDigitada.isEmpty() || !tarefaDigitada.equals(""))
                {
                    //Inserindo tarefa no banco de dados
                    bancoDados.execSQL("INSERT INTO tblTarefas (tarefa) VALUES ('"+tarefaDigitada+"')");
                    Toast.makeText(getApplicationContext(),"Tarefa adicionada",Toast.LENGTH_SHORT).show();
                    recuperarTarefas();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Digite uma tarefa",Toast.LENGTH_SHORT).show();
                }

            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
    }

    private void recuperarTarefas()
    {
        try
        {
            Cursor cursor = null;
            cursor = bancoDados.rawQuery("SELECT * FROM tblTarefas ORDER BY id DESC",null);

            int indiceColunaId = cursor.getColumnIndex("id");
            int indiceColunaTarefa = cursor.getColumnIndex("tarefa");


            itens = new ArrayList<String>();
            ids = new ArrayList<Integer>();
            itensAdaptador = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_2,android.R.id.text1,itens);
            listaTarefas.setAdapter(itensAdaptador);
            cursor.moveToFirst();

            while(cursor != null)
            {
                itens.add(cursor.getString(indiceColunaTarefa));
                ids.add(Integer.parseInt(cursor.getString(indiceColunaId)));
                //Log.i("Resultado-","Tarefa:"+cursor.getString(indiceColunaTarefa));
                cursor.moveToNext();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void removerTarefa (int id)
    {
        bancoDados.execSQL("DELETE FROM tblTarefas WHERE id="+id);
    }

}
