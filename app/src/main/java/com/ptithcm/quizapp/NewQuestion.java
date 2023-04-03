package com.ptithcm.quizapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.ptithcm.quizapp.adapter.CustomAdapterQuestion;
import com.ptithcm.quizapp.database.DBHelper;
import com.ptithcm.quizapp.model.Answer;
import com.ptithcm.quizapp.model.Question;

import java.util.ArrayList;

public class NewQuestion extends AppCompatActivity {
    TextInputEditText edtContentDialog;
    Button btnSubmitDialog;
    EditText edtContent;
    Spinner spnLever, spnType;
    ImageView imgImage, imgAddImage, imgDeleteImage, imgAddRowAnswer, imgDeleteRowAnswer;
    LinearLayout llListAnswer;
    FloatingActionButton fabtbSave;
    AlertDialog dialog;
    RadioGroup radioGroup;
    RadioButton rbtnCorrect1, rbtnCorrect2, rbtnCorrect3, rbtnCorrect4, rbtnCorrect5, rbtnCorrect6;
    RadioButton[] rbtn = new RadioButton[7];
    ProgressBar proLoading;
    ArrayList<Answer> ans = new ArrayList<>();

    int checked_position = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.add_update_question);

        setControl();
        setEvent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_new_detail_qs, menu);
        return true;
    }

    private void setEvent() {
        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 1) {
                    rbtn[1].setChecked(true);
                    for (int i = 2; i <= llListAnswer.getChildCount(); i++) {
                        rbtn[i].setVisibility(View.INVISIBLE);
                    }
                    llListAnswer.removeViewsInLayout(1, llListAnswer.getChildCount() - 1);
                    imgAddRowAnswer.setVisibility(View.INVISIBLE);
                }
                else imgAddRowAnswer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        imgAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery, 1000);
            }
        });
        imgDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgDeleteImage.setVisibility(View.GONE);
                imgImage.setVisibility(View.GONE);
                imgImage.setImageURI(null);
            }
        });
        imgAddRowAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llListAnswer.getChildCount() >= 6) {
                    Toast.makeText(NewQuestion.this, "The maximum number of answers is 6", Toast.LENGTH_SHORT).show();
                    return;
                }
                View newAnswer = getLayoutInflater().inflate(R.layout.item_answer, null, false);
                EditText edtAnswer = newAnswer.findViewById(R.id.edtAnswer);
                ImageView imgDeleteRowAnswer = newAnswer.findViewById(R.id.imgDeleteRowAnswer);
                imgDeleteRowAnswer.setVisibility(View.VISIBLE);
                llListAnswer.addView(newAnswer);
                rbtn[llListAnswer.getChildCount()].setVisibility(View.VISIBLE);
                imgDeleteRowAnswer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = llListAnswer.indexOfChild(newAnswer) + 1;
                        switch (radioGroup.getCheckedRadioButtonId()) {
                            case R.id.rbtnCorrect1:
                                checked_position = 1;
                                break;
                            case R.id.rbtnCorrect2:
                                checked_position = 2;
                                break;
                            case R.id.rbtnCorrect3:
                                checked_position = 3;
                                break;
                            case R.id.rbtnCorrect4:
                                checked_position = 4;
                                break;
                            case R.id.rbtnCorrect5:
                                checked_position = 5;
                                break;
                            case R.id.rbtnCorrect6:
                                checked_position = 6;
                        }
                        if (rbtn[position].isChecked()) {
                            Toast.makeText(NewQuestion.this, "Can not delete!", Toast.LENGTH_SHORT).show();
                        } else {
                            if (position < checked_position) {
                                rbtn[checked_position - 1].setChecked(true);
                            }
                            rbtn[llListAnswer.getChildCount()].setVisibility(View.GONE);
                            llListAnswer.removeView(newAnswer);
                        }

                    }
                });
            }
        });
        fabtbSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomAdapterQuestion adapterQuestion = new CustomAdapterQuestion(NewQuestion.this, null);

                getAnswers();
                if (edtContent.getText().toString().equals("")) {
                    edtContent.setError("");
                }
                for (Answer answer : ans) {
                    if(answer.getAnswerContent().equals("")  ) {
                        adapterQuestion.showErrorDialog("Content cannot be left blank!");
                        return;
                    }
                }
                String qsType, qsLevel, qsContent, qsAnswer;
                Bitmap qsImage;
                qsContent = edtContent.getText().toString().trim();
                qsType = String.valueOf(spnType.getSelectedItemPosition());
                qsLevel = String.valueOf(spnLever.getSelectedItemPosition() + 1) ;
                qsAnswer = String.valueOf(checked_position);
                Question qs = new Question();
                try {
                    qsImage = ((BitmapDrawable) imgImage.getDrawable()).getBitmap();
                    qs = new Question(null, qsContent, qsType, qsLevel, qsAnswer, qsImage);
                } catch (Exception e) {
                    qs = new Question(null, qsContent, qsType, qsLevel, qsAnswer, null);
                }

                DBHelper dbHelper = new DBHelper(NewQuestion.this);
                if (dbHelper.addQuestion_Answer(qs,ans)) {
                    Intent intent = new Intent(NewQuestion.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    Toast.makeText(NewQuestion.this, "Created successfully", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                } else {
                    adapterQuestion.showErrorDialog("Error!");
                }

            }
        });
        edtContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtContentDialog.setText(edtContent.getText());
                dialog.show();
            }
        });
    }

    private void getAnswers() {
        ans.clear();
        for (int i = 0; i < llListAnswer.getChildCount(); i++) {
            View v = llListAnswer.getChildAt(i);
            EditText editText = v.findViewById(R.id.edtAnswer);
            if(editText.getText().toString().equals("")) {
                editText.setError("");
            }
            ans.add(new Answer(null,editText.getText().toString()));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imgImage.setImageURI(data.getData());
        imgDeleteImage.setVisibility(View.VISIBLE);
        imgImage.setVisibility(View.VISIBLE);
    }

    private void setControl() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        spnLever = findViewById(R.id.spnLever);
        spnType = findViewById(R.id.spnType);
        imgImage = findViewById(R.id.imgImage);
        imgAddImage = findViewById(R.id.imgAddImage);
        imgDeleteImage = findViewById(R.id.imgDeleteImage);
        imgAddRowAnswer = findViewById(R.id.imgAddRowAnswer);
        imgDeleteRowAnswer = findViewById(R.id.imgDeleteRowAnswer);
        llListAnswer = findViewById(R.id.llListAnswer);
        fabtbSave = findViewById(R.id.fabtbSave);
        edtContent = findViewById(R.id.edtContent);
        rbtnCorrect1 = findViewById(R.id.rbtnCorrect1);
        rbtnCorrect1.setChecked(true);
        rbtnCorrect2 = findViewById(R.id.rbtnCorrect2);
        rbtnCorrect3 = findViewById(R.id.rbtnCorrect3);
        rbtnCorrect4 = findViewById(R.id.rbtnCorrect4);
        rbtnCorrect5 = findViewById(R.id.rbtnCorrect5);
        rbtnCorrect6 = findViewById(R.id.rbtnCorrect6);
        radioGroup = findViewById(R.id.radioGroup);
        proLoading = findViewById(R.id.proLoading);
        rbtn[1] = rbtnCorrect1;
        rbtn[2] = rbtnCorrect2;
        rbtn[3] = rbtnCorrect3;
        rbtn[4] = rbtnCorrect4;
        rbtn[5] = rbtnCorrect5;
        rbtn[6] = rbtnCorrect6;

        AlertDialog.Builder builder = new AlertDialog.Builder(NewQuestion.this);
        View view = getLayoutInflater().inflate(R.layout.content_qs_dialog, null);
        edtContentDialog = view.findViewById(R.id.edtContentDialog);
        btnSubmitDialog = view.findViewById(R.id.btnSubmitDialog);
        btnSubmitDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtContent.setText(edtContentDialog.getText());
                dialog.dismiss();
            }
        });
        builder.setView(view);
        dialog = builder.create();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnReload_tb:
                proLoading.setVisibility(View.VISIBLE);
                proLoading.setProgress(0);
                CountDownTimer countDownTimer = new CountDownTimer(1000,500) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        int val = proLoading.getProgress() + 10;
                        proLoading.setProgress(val);
                    }

                    @Override
                    public void onFinish() {
                        edtContent.setText("");
                        spnLever.setSelection(0);
                        spnType.setSelection(0);
                        for (int i = 2; i <= llListAnswer.getChildCount(); i++) {
                            rbtn[i].setVisibility(View.INVISIBLE);
                        }
                        llListAnswer.removeViewsInLayout(1, llListAnswer.getChildCount() - 1);
                        imgDeleteImage.setVisibility(View.GONE);
                        imgImage.setVisibility(View.GONE);
                        imgImage.setImageURI(null);
                        proLoading.setVisibility(View.GONE);
                    }
                }.start();
        }
        return super.onOptionsItemSelected(item);
    }
}