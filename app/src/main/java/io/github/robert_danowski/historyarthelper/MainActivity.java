package io.github.robert_danowski.historyarthelper;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    String subfolder = "HistoryArtHelperData";
//    String fullPath = Environment.getExternalStorageDirectory().getPath().toString()+"/"+subfolder;
    String fullPath = "/mnt/sdcard/HistoryArtHelperData";

    ArrayList<File> allFiles;
    ImageView imageView;
    Button answer1;
    Button answer2;
    Button answer3;
    Button answer4;
    Button.OnClickListener clickNextQuestion;

    public void listf(String directoryName, ArrayList<File> files) {
        File directory = new File(directoryName);

        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                listf(file.getAbsolutePath(), files);
            }
        }
    }

    public void initFilesList() {
        allFiles = new ArrayList<File>(); // create new array;
        listf(fullPath,allFiles);
    }

    private void assignViews() {
        imageView = (ImageView)this.findViewById(R.id.imageView);
        answer1 = (Button)findViewById(R.id.answer1);
        answer2 = (Button)findViewById(R.id.answer2);
        answer3 = (Button)findViewById(R.id.answer3);
        answer4 = (Button)findViewById(R.id.answer4);
    }

    public ArrayList<File> get4Images() {
        ArrayList<File> copyAllFiles=new ArrayList<File>(allFiles);
        int listSize = copyAllFiles.size();
        ArrayList<File> drawedImages = new ArrayList<File>();
        for (int i=0;i<4;i++) {
            Random r = new Random();
            int index = r.nextInt(listSize);

            drawedImages.add(copyAllFiles.get(index));
            copyAllFiles.remove(index);
            listSize--;
        }
        return drawedImages;
    }

    public void setButtonText( Button button, File file) {
        String parent = file.getParent();
                parent = parent.substring(parent.lastIndexOf("/") + 1, parent.length());
        button.setText(parent + ": " + file.getName());
    }

    public void showCorrectAnswer(Button marked, Button shouldBe) {
        if (marked==shouldBe) marked.setBackgroundColor(Color.GREEN);
        else {
            marked.setBackgroundColor(Color.RED);
            shouldBe.setBackgroundColor(Color.GREEN);
        }
        answer1.setOnClickListener(null);
        answer2.setOnClickListener(null);
        answer3.setOnClickListener(null);
        answer4.setOnClickListener(null);

        shouldBe.setOnClickListener(clickNextQuestion);
    }

    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("History Art Helper",fullPath);

        initFilesList();

        assignViews();

        generateQuestion();

        clickNextQuestion = new Button.OnClickListener() {
            public void onClick(View v) {
                restartActivity();
            }
        };
    }

    private void generateQuestion() {
        ArrayList<File> drawedImages = get4Images();
        final File goodAnswer = drawedImages.get(0);
        Collections.shuffle(drawedImages);

        Picasso.with(this).load("file://"+goodAnswer.getPath()).into(imageView);
        setButtonText(answer1,drawedImages.get(0));
        setButtonText(answer2,drawedImages.get(1));
        setButtonText(answer3,drawedImages.get(2));
        setButtonText(answer4,drawedImages.get(3));

        Button goodButton = null;
        if (drawedImages.get(0)==goodAnswer) goodButton=answer1;
        else if (drawedImages.get(1)==goodAnswer) goodButton=answer2;
        else if (drawedImages.get(2)==goodAnswer) goodButton=answer3;
        else if (drawedImages.get(3)==goodAnswer) goodButton=answer4;

        final Button finalGoodButton = goodButton;
        Button.OnClickListener clickCheckAnswer = new Button.OnClickListener() {
            public void onClick(View v) {
                Button clickedButton = (Button)v;
                showCorrectAnswer(clickedButton, finalGoodButton);
            }
        };

        answer1.setOnClickListener(clickCheckAnswer);
        answer2.setOnClickListener(clickCheckAnswer);
        answer3.setOnClickListener(clickCheckAnswer);
        answer4.setOnClickListener(clickCheckAnswer);
    }
}
