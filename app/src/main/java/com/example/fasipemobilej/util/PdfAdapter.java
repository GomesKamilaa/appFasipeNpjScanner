package com.example.fasipemobilej.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fasipemobilej.R;

import java.io.File;
import java.util.List;

public class PdfAdapter extends RecyclerView.Adapter<PdfAdapter.PdfViewHolder> {

    private final List<File> pdfFiles;
    private final Context context;

    public PdfAdapter(Context context, List<File> pdfFiles) {
        this.context = context;
        this.pdfFiles = pdfFiles;
    }

    @NonNull
    @Override
    public PdfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pdf_item, parent, false);
        return new PdfViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PdfViewHolder holder, int position) {
        File pdfFile = pdfFiles.get(position);
        holder.pdfNameTextView.setText(pdfFile.getName());

        holder.itemView.setOnClickListener(v -> {
            Uri pdfUri = FileProvider.getUriForFile(context, "com.example.fasipemobilej.fileprovider", pdfFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(pdfUri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Verifica se há um aplicativo disponível para abrir o PDF em um explorador de arquivos
            Intent chooserIntent = Intent.createChooser(intent, "Abrir PDF com...");
            if (chooserIntent.resolveActivity(context.getPackageManager()) != null) {
                try {
                    context.startActivity(chooserIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "Nenhum aplicativo disponível para abrir o PDF", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Nenhum aplicativo disponível para abrir o PDF", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return pdfFiles.size();
    }

    static class PdfViewHolder extends RecyclerView.ViewHolder {
        TextView pdfNameTextView;

        PdfViewHolder(@NonNull View itemView) {
            super(itemView);
            pdfNameTextView = itemView.findViewById(R.id.pdfNameTextView);
        }
    }
}
