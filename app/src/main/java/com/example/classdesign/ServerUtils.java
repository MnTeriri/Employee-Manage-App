package com.example.classdesign;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.entity.UrlEncodedFormEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.protocol.HTTP;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

public class ServerUtils {
    private static final String url = "http://172.20.10.3/server";

    public static String executeUrl(String servletPath, List<NameValuePair> list) throws IOException {
        HttpPost httpPost = new HttpPost(url + servletPath);
        httpPost.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
        HttpResponse response = new DefaultHttpClient().execute(httpPost);
        return EntityUtils.toString(response.getEntity(), "UTF-8");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Bitmap decodeImageString(String imageString) {
        byte[] bytes = Base64.getMimeDecoder().decode(imageString);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encodeImageBitmap(Bitmap bitmap) {
        String imageString = "";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        imageString = Base64.getEncoder().encodeToString(out.toByteArray());
        return imageString;
    }

}
