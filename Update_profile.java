package com.google.firebase.udacity.friendlychat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

public class Update_profile extends AppCompatActivity implements Validator.ValidationListener {

    @NotEmpty
    @Email
    private EditText editEmail;

    @NotEmpty
    private EditText editPwd;

    @NotEmpty
    private EditText editNewPwd;

    private Button btnUpdate;
    private Validator validator;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        initViews();

        validator = new Validator(this);
        validator.setValidationListener(this);

    }

    private void initViews() {
        editEmail = findViewById(R.id.editEmail);
        editEmail.setText(firebaseUser.getEmail());

        editPwd = findViewById(R.id.editPwd);
        editNewPwd = findViewById(R.id.editNewPwd);

        btnUpdate = findViewById(R.id.btnUpdate);
    }

    public void updateProfile(View view) {
        String email = editEmail.getText().toString();
        String pwd = editPwd.getText().toString();

        validator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        AuthCredential credential = EmailAuthProvider
                .getCredential(editEmail.getText().toString(), editPwd.getText().toString());

        firebaseUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            firebaseUser.updatePassword(editNewPwd.getText().toString());
                            Toast.makeText(getApplicationContext(), "Usuário atualizado com sucesso", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Autenticação não autorizada", Toast.LENGTH_LONG).show();
                        }

                    }
                });

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
