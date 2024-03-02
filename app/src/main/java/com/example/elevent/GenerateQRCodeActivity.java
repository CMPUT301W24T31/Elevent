package com.example.elevent;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

// All of this is from chatgpt lol
// OpenAI, 2024, ChatGPT, How to use QR code

public class GenerateQRCodeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_qr_layout);

        String text = "You have checked in";  // this is a placeholder; TODO: change this to what we want to QR code to actually encode
        try{
            Bitmap qrCode = generateQRCode(text);
            ImageView qrImage = findViewById(R.id.qr_image);
            qrImage.setImageBitmap(qrCode);
        } catch (WriterException e){
            Toast.makeText(this, "Cannot generate QR code", Toast.LENGTH_SHORT).show();  // TODO: maybe change this idk what we want to do to handle this
        }
    }

    private Bitmap generateQRCode(String text) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 512, 512);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                bitmap.setPixel(x, y, bitMatrix.get(x,y) ? ContextCompat.getColor(this, R.color.black) : ContextCompat.getColor(this, R.color.white));
            }
        }
        return bitmap;
    }
}
