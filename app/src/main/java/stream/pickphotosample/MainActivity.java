package stream.pickphotosample;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import stream.custombutton.CustomButton;
import stream.custompermissionsdialogue.PermissionsDialogue;
import stream.custompermissionsdialogue.utils.PermissionUtils;
import stream.pickphotoview.PickPhotoView;
import stream.pickphotoview.adapter.SpaceItemDecoration;
import stream.pickphotoview.util.PickConfig;
import stream.pickphotoview.util.PickUtils;

public class MainActivity extends AppCompatActivity {

    private SampleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!PermissionUtils.IsPermissionsEnabled(getApplicationContext(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}))
        {
            PermissionsDialogue.Builder permissions = new PermissionsDialogue.Builder(this)
                    .setMessage(getString(R.string.app_name) + " is a photo selector and requires the following permissions: ")
                    .setIcon(R.mipmap.ic_launcher)
                    .setCancelable(false)
                    .setRequireStorage(PermissionsDialogue.REQUIRED)
                    .setRequireCamera(PermissionsDialogue.REQUIRED)
                    .setOnContinueClicked(new PermissionsDialogue.OnContinueClicked() {
                        @Override
                        public void OnClick(View view, Dialog dialog) {
                            dialog.dismiss();
                        }
                    })
                    .build();
            permissions.show();
        }

        //Select Single Image - When image is selected, gallery immediately closes and returns image.
        CustomButton btn1 = findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PickPhotoView.Builder(MainActivity.this)
                        .setPickPhotoSize(1)
                        .setShowCamera(true)
                        .setSpanCount(3)
                        .setLightStatusBar(true)
                        .setStatusBarColor("#ffffff")
                        .setToolbarColor("#ffffff")
                        .setToolbarIconColor("#000000")
                        .setClickSelectable(true)
                        .setShowGif(true)
                        .start();
            }
        });

        //Select Multiple Images - User can select multiple images and click Select to confirm.
        CustomButton btn2 = findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PickPhotoView.Builder(MainActivity.this)
                        .setPickPhotoSize(3)
                        .setShowCamera(true)
                        .setSpanCount(4)
                        .setLightStatusBar(true)
                        .setStatusBarColor('#' + Integer.toHexString(ContextCompat.getColor(getApplicationContext(), R.color.white)))
                        .setToolbarColor('#' + Integer.toHexString(ContextCompat.getColor(getApplicationContext(), R.color.white)))
                        .setToolbarIconColor('#' + Integer.toHexString(ContextCompat.getColor(getApplicationContext(), R.color.black)))
                        .setSelectIconColor('#' + Integer.toHexString(ContextCompat.getColor(getApplicationContext(), R.color.pick_green)))
                        .setClickSelectable(true)
                        .start();
            }
        });

        //Image Preview Select - Clicking on image opens Image Preview. Must click select icon to select image.
        CustomButton btn3 = findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PickPhotoView.Builder(MainActivity.this)
                        .setPickPhotoSize(6)
                        .setShowCamera(false)
                        .setSpanCount(4)
                        .setLightStatusBar(true)
                        .setStatusBarColor('#' + Integer.toHexString(ContextCompat.getColor(getApplicationContext(), R.color.white)))
                        .setToolbarColor('#' + Integer.toHexString(ContextCompat.getColor(getApplicationContext(), R.color.white)))
                        .setToolbarIconColor('#' + Integer.toHexString(ContextCompat.getColor(getApplicationContext(), R.color.black)))
                        .setClickSelectable(false)
                        .start();
            }
        });

        RecyclerView photoList = findViewById(R.id.photo_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        photoList.setLayoutManager(layoutManager);
        photoList.addItemDecoration(new SpaceItemDecoration(PickUtils.getInstance(MainActivity.this).dp2px(PickConfig.ITEM_SPACE), 3));
        adapter = new SampleAdapter(this, null);
        photoList.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("RequestCode", String.valueOf(requestCode));
        Log.d("ResultCode", String.valueOf(resultCode));
        Log.d("Data", String.valueOf(data));
        if(resultCode == 0){
            return;
        }
        if(data == null){
            return;
        }
        if (requestCode == PickConfig.PICK_PHOTO_DATA) {
            ArrayList<String> selectPaths = (ArrayList<String>) data.getSerializableExtra(PickConfig.INTENT_IMG_LIST_SELECT);
            adapter.updateData(selectPaths);
        }
    }
}
