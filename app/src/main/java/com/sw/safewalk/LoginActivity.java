package com.sw.safewalk;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();


        // Botão de login de usuários
        final Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                login();
            }
        });

        // Botão para redirecionar para a activity de cadastro
        final TextView btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(intent);
            }
        });

        // Botão para enviar o e-mail para resetar a senha
        final TextView btnResetPsw = findViewById(R.id.btnResetPsw);
        btnResetPsw.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String emailAddress = "erikaaespindola@hotmail.com";

            auth.sendPasswordResetEmail(emailAddress)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "E-mail enviado com sucesso!", Toast.LENGTH_SHORT).show();
                }
                }
            });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void login() {
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);

        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                Toast.makeText(LoginActivity.this, "Bem vindo de volta!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(LoginActivity.this, "Usuário ou senha inválidos", Toast.LENGTH_SHORT).show();
            }
            }
        });
    }
}
