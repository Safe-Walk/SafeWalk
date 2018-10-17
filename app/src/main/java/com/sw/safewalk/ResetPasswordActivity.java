package com.sw.safewalk;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();


        // Botão para enviar o e-mail para resetar a senha

        final Button btnResetPsw = findViewById(R.id.btnResetPsw);

        btnResetPsw.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            EditText email = findViewById(R.id.email);

            mAuth.sendPasswordResetEmail(email.getText().toString())
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, "E-mail enviado com sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Não foi possível enviar o e-mail", Toast.LENGTH_SHORT).show();
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
}
