package com.example.saldoplusv1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.saldoplusv1.databinding.ActivitySplahsScreenBinding;
import com.example.saldoplusv1.repositories.RepositorioUsuario;


public class SplashScreenActivity extends AppCompatActivity {

    private ActivitySplahsScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Inflamos el binding
        binding = ActivitySplahsScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Accedemos directamente a los views a partir del binding
        ProgressBar progressBar = binding.progressBar;
        ImageView imageView = binding.imageView;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                RepositorioUsuario repo = new RepositorioUsuario(SplashScreenActivity.this);
                if (repo.isRegistered()){
                    String nombre = repo.obtenerNombre();
                    intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    Toast.makeText(SplashScreenActivity.this, "Bienvenido de nuevo "  + nombre + " 👋", Toast.LENGTH_SHORT).show();

                }else {
                    intent = new Intent(SplashScreenActivity.this, NameActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 1000);

        new Thread(() -> {
            for (int progress = 0; progress <= 100; progress++) {
                int finalProgress = progress;
                runOnUiThread(() -> progressBar.setProgress(finalProgress));

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            runOnUiThread(() -> progressBar.setVisibility(View.GONE));

        }).start();

    }
}
