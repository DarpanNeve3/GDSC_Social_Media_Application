package com.example.videoplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class CustomImageAdapter extends BaseAdapter {
    Context context;
    String[] location;
    String[] names;
    String[] iName;
    LayoutInflater inflater;
    StorageReference storageReference;
    Random rand;
    int size;
    int width,height;
    Uri imageUri;
    CustomImageAdapter(Context c,String[] l,String[] n,String[] in,String s)
    {
        this.context=c;
        this.location=l;
        this.names=n;
        this.iName=in;
        this.size=Integer.parseInt(s);
        rand = new Random();
        inflater=LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return location.length;
//        return 4;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
//        if(size>=location.length)
//            size=0;
        view=inflater.inflate(R.layout.image_list,null);
        TextView name = view.findViewById(R.id.listViewName);
        TextView INameText = view.findViewById(R.id.listViewImageImage);
        ImageView b = view.findViewById(R.id.imageView20);
        ImageView image = view.findViewById(R.id.listViewImage);
        ImageView refresh = view.findViewById(R.id.imageView2);
        name.setText(names[i+size]);
        INameText.setText(iName[i+size]);
//        try {
//            Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(url).getContent());
//            image.setImageBitmap(bitmap);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        imageUri=Uri.parse(url);
//        image.setImageURI(imageUri);


        storageReference = FirebaseStorage.getInstance().getReference("images/"+location[i+size]);
        try{
            File localFile = File.createTempFile("tempfile",".png");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    try {

//
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
//                        image.setImageBitmap(bitmap);
                        width = bitmap.getWidth();
                        height = bitmap.getHeight();
                        int pix[][] = new int[width][height];
                        int keys[][] = new int[width][height];
                        int flag = 0;
                        int key = -97, temp = 1;

                        for (int y = 0; y < height; y++) {
                            for (int x = 0; x < width; x++) {
                                //Retrieving contents of a pixel
                                int pixel = bitmap.getPixel(x, y);
                                pix[x][y] = pixel;
                            }
                        }
                        //getting all keys
                        for (int y = 0; y < height; y++) {
                            for (int x = 0; x < width; x++) {
                                //Setting pixel
                                int m = (int) ((Math.pow(2, 32)) - 1);
                                temp = ((key * temp + 7) / 3) % m;
                                keys[x][y] = temp;
                            }
                        }

                        for (int y = 0; y < height; y++) {
                            for (int x = 0; x < width; x++) {
                                //Setting pixel
                                if (x % 2 == 0) {
                                    pix[x][y] = pix[x][y] * -1;
                                }
                                flag++;
                                pix[x][y] = pix[x][y] ^ keys[x][y];
                                bitmap.setPixel(x, y, pix[x][y]);
//                bufferedImage1.setRGB(x,y,pix1[x][y]);
                            }
                        }


                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG,100,bytes);

                        int no = rand.nextInt();
                        String path1 = MediaStore.Images.Media.insertImage(context.getApplicationContext().getContentResolver(),bitmap,"myEncImg2"+no,null);
                        imageUri = Uri.parse(path1);
                        image.setImageURI(imageUri);
//                        image.setImageBitmap(bitmap);
                    }
                    catch (Exception e)
                    {
                        Log.d("EncryptImage:",""+e);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Failure",""+e);
                }
            });
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    String path1 = "https://firebasestorage.googleapis.com/v0/b/cpe-project-ddf59.appspot.com/o/images%2F"+location[i]+"?alt=media";
//                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                    shareIntent.setType("text/plain");
//                    shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Video");
//                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,path1);
//                    context.startActivity(Intent.createChooser(shareIntent, "Share via"));
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("image/*");
                    i.putExtra(Intent.EXTRA_STREAM, imageUri);
                    context.startActivity(Intent.createChooser(i, "Share Image"));
                }
            });
        }
        catch (Exception e)
        {
            Log.d("GettingFileError:",""+e);
        }

//        refresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i1 = new Intent(context,ImageActivity.class);
//                i1.putExtra("Location",location);
//                i1.putExtra("Name",names);
//                i1.putExtra("iName",iName);
//                i1.putExtra("size",size+4);
//                context.startActivity(i1);
//            }
//        });
        return view;
    }
}
