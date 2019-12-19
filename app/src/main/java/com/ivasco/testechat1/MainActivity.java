package com.ivasco.testechat1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ivasco.testechat1.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private EditText edtNome, edtEmail;
    private ListView listV_dados;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private List<User> listUser = new ArrayList<>();
    private ArrayAdapter<User> arrayAdapterUser;

    private User userSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtNome = findViewById(R.id.edtNome);
        edtEmail = findViewById(R.id.edtEmail);
        listV_dados = findViewById(R.id.listV_dados);

        initFirebase();

        eventoDataBase();

        listV_dados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, listUser.get(position).getEmail(), Toast.LENGTH_LONG).show();
                userSelecionado = (User)parent.getItemAtPosition(position);
                edtNome.setText(userSelecionado.getNome());
                edtEmail.setText(userSelecionado.getEmail());
            }
        });
    }

    private void eventoDataBase() {
        databaseReference.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listUser.clear();

                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    User u = objSnapshot.getValue(User.class);
                    listUser.add(u);
                }
                arrayAdapterUser = new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_list_item_1, listUser);
                listV_dados.setAdapter(arrayAdapterUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(MainActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_novo: {
                if (edtNome.getText().toString().isEmpty() || edtEmail.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Erro", Toast.LENGTH_LONG).show();
                }
                else {
                    User user = new User();
                    user.setUid(UUID.randomUUID().toString());
                    user.setNome(edtNome.getText().toString());
                    user.setEmail(edtEmail.getText().toString());
                    databaseReference.child("User").child(user.getUid()).setValue(user);
                    clearFields();
                }
            }
            break;

            case R.id.menu_editar: {
                if (userSelecionado == null)
                    break;
                Toast.makeText(MainActivity.this, "Editar", Toast.LENGTH_LONG).show();
                User u = new User();
                u.setUid(userSelecionado.getUid());
                u.setNome(edtNome.getText().toString().trim());
                u.setEmail(edtEmail.getText().toString().trim());
                databaseReference.child("User").child(u.getUid()).setValue(u);
                clearFields();
            }
            break;

            case R.id.menu_deleta: {
                if (userSelecionado == null)
                    break;
                Toast.makeText(MainActivity.this, "Deletar", Toast.LENGTH_LONG).show();
                User u = new User();
                u.setUid(userSelecionado.getUid());
                databaseReference.child("User").child(u.getUid()).removeValue();
                clearFields();
            }
            break;
        }
        return true;
    }

    private void clearFields() {
        edtNome.setText("");
        edtEmail.setText("");
    }
}