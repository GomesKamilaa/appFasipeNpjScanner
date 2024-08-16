package com.example.fasipemobilej;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fasipemobilej.util.PdfAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainAdd extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private File photoFile;
    private RecyclerView pdfRecyclerView;
    private PdfAdapter pdfAdapter;
    private List<File> pdfFiles = new ArrayList<>();

    // Tamanho da página A4 em pixels a 300 dpi (A4: 210mm x 297mm)
    private static final int PAGE_WIDTH = (int) (210 * 300 / 25.4); // Convertendo mm para pixels
    private static final int PAGE_HEIGHT = (int) (297 * 300 / 25.4); // Convertendo mm para pixels

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_add);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupCaptureButton();

        pdfRecyclerView = findViewById(R.id.pdfRecyclerView);
        pdfRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pdfAdapter = new PdfAdapter(this, pdfFiles);
        pdfRecyclerView.setAdapter(pdfAdapter);

        loadPdfFiles();
    }

    private void setupCaptureButton() {
        Button btnCam = findViewById(R.id.NovoDocumento);
        btnCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Verifique se há uma atividade de câmera para lidar com a intenção
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Erro ao criar arquivo de imagem: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }

            // Continue apenas se o arquivo foi criado com sucesso
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.fasipemobilej.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                // Inicie a atividade da câmera usando o contrato ActivityResultLauncher
                startForResult.launch(takePictureIntent);
            }
        } else {
            Toast.makeText(this, "Nenhuma aplicação de câmera disponível", Toast.LENGTH_SHORT).show();
        }
    }

    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), bmOptions);
                    createPdfFromBitmap(bitmap);
                } else {
                    Toast.makeText(this, "Captura de imagem cancelada", Toast.LENGTH_SHORT).show();
                }
            });

    private File createImageFile() throws IOException {
        // Crie um nome de arquivo de imagem
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }

    private void createPdfFromBitmap(Bitmap bitmap) {
        PdfDocument document = new PdfDocument();

        // Configura a página do PDF para orientação retrato (vertical)
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(PAGE_HEIGHT, PAGE_WIDTH, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        // Desenha a imagem no centro da página do PDF
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        float pageWidth = pageInfo.getPageWidth();
        float pageHeight = pageInfo.getPageHeight();

        // Calcula a escala para ajustar a imagem na página
        float scaleX = pageWidth / bitmapWidth;
        float scaleY = pageHeight / bitmapHeight;
        float scale = Math.min(scaleX, scaleY);

        float scaledWidth = bitmapWidth * scale;
        float scaledHeight = bitmapHeight * scale;

        // Calcula as coordenadas para centralizar a imagem na página
        float left = (pageWidth - scaledWidth) / 2;
        float top = (pageHeight - scaledHeight) / 2;

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        matrix.postTranslate(left, top);

        canvas.drawBitmap(bitmap, matrix, null);

        document.finishPage(page);

        savePdfDocument(document);

        document.close();
    }

    private void savePdfDocument(PdfDocument document) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String pdfFileName = "PDF_" + timeStamp + ".pdf";

        File pdfDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (!pdfDir.exists()) {
            pdfDir.mkdirs();
        }

        File pdfFile = new File(pdfDir, pdfFileName);

        try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
            document.writeTo(fos);
            Toast.makeText(this, "PDF salvo com sucesso!", Toast.LENGTH_SHORT).show();
            addPdfToMediaStore(pdfFile); // Adiciona o PDF ao MediaStore para ser visível no explorador de arquivos
            pdfFiles.add(pdfFile);
            pdfAdapter.notifyItemInserted(pdfFiles.size() - 1);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao salvar PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void addPdfToMediaStore(File pdfFile) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(pdfFile));
        sendBroadcast(mediaScanIntent);
    }

    private void loadPdfFiles() {
        File pdfDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (pdfDir.exists() && pdfDir.isDirectory()) {
            File[] files = pdfDir.listFiles((dir, name) -> name.endsWith(".pdf"));
            if (files != null) {
                for (File file : files) {
                    pdfFiles.add(file);
                }
                pdfAdapter.notifyDataSetChanged();
            }
        }
    }
}
