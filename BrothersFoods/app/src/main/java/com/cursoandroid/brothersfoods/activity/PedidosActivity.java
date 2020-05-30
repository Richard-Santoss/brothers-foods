package com.cursoandroid.brothersfoods.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.cursoandroid.brothersfoods.R;
import com.cursoandroid.brothersfoods.adapter.AdapterPedido;
import com.cursoandroid.brothersfoods.adapter.AdapterProduto;
import com.cursoandroid.brothersfoods.helper.ConfiguracaoFirebase;
import com.cursoandroid.brothersfoods.helper.UsuarioFirebase;
import com.cursoandroid.brothersfoods.listener.RecyclerItemClickListener;
import com.cursoandroid.brothersfoods.model.Pedido;
import com.cursoandroid.brothersfoods.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class PedidosActivity extends AppCompatActivity {

    private RecyclerView recyclerPedidos;
    private AdapterPedido adapterPedido;
    private List<Pedido> pedidos = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        //Configurações iniciais
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idEmpresa = UsuarioFirebase.getIdUsuario();

        //Configuração Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Pedidos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configura recyclerview
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));
        recyclerPedidos.setHasFixedSize(true);
        adapterPedido = new AdapterPedido(pedidos);
        recyclerPedidos.setAdapter( adapterPedido );

        recuperarPedidos();

        //Adicionando evento de clique no recyclerview
        recyclerPedidos.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerPedidos,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Pedido pedido = pedidos.get(position);
                        pedido.setStatus("finalizado");
                        pedido.atualizarStatus();
                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));

    }

    private void recuperarPedidos() {

        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos")
                .child(idEmpresa);


        Query pedidoPesquisa = pedidoRef.orderByChild("status")
                .equalTo("Confirmado");



        pedidoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                pedidos.clear();
                if( dataSnapshot.getValue() != null ){
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        Pedido pedido = ds.getValue(Pedido.class);
                        pedidos.add(pedido);
                    }
                    adapterPedido.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void inicializarComponentes(){
        recyclerPedidos = findViewById(R.id.recyclerPedidos);
    }

}
