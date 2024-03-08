package com.example.elevent;

import android.content.Intent;
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

// All of this is from chatgpt lol
// OpenAI, 2024, ChatGPT, How to use QR code

/**
 * This activity encodes information into a Bitmap that is converted to byte[]
 */
public class GenerateQRCodeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String eventID = getIntent().getStringExtra("eventID");  // this is a placeholder; TODO: change this to what we want to QR code to actually encode
        try{
            byte[] qrCodeByteArray = generateQRCode(eventID);
            Intent intent = new Intent();
            intent.putExtra("qrCode", qrCodeByteArray);
            setResult(RESULT_OK, intent);
            finish();
        } catch (WriterException e){
            Toast.makeText(this, "Cannot generate QR code", Toast.LENGTH_SHORT).show();  // TODO: maybe change this idk what we want to do to handle this
        }
    }

    private byte[] generateQRCode(String eventID) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(eventID, BarcodeFormat.QR_CODE, 512, 512);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                bitmap.setPixel(x, y, bitMatrix.get(x,y) ? ContextCompat.getColor(this, R.color.black) : ContextCompat.getColor(this, R.color.white));
            }
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }
}
