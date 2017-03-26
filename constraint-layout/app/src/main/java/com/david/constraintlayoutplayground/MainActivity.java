package com.david.constraintlayoutplayground;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_image)
    ImageView backgroundImage;
    @BindView(R.id.main_camera_title_text)
    TextView cameraTitleText;
    @BindView(R.id.main_camera_details_text)
    TextView cameraDetailsText;
    @BindView(R.id.main_picture_title_text)
    TextView pictureTitleText;
    @BindView(R.id.main_picture_details_text)
    TextView pictureDetailsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setScreenText();
    }

    private void setScreenText() {
        cameraTitleText.setText("SONY DSC-W570");
        cameraDetailsText.setText("f/8.0 1/250 4.5mm ISO80");

        pictureTitleText.setText("DSC06063.JPG");
        pictureDetailsText.setText("15.9MP 3456x4608 1.8MB");
    }
}
