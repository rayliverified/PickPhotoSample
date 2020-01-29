package stream.pickphotoview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import stream.pickphotoview.adapter.PickGridAdapter;
import stream.pickphotoview.adapter.SpaceItemDecoration;
import stream.pickphotoview.model.GroupImage;
import stream.pickphotoview.model.PickData;
import stream.pickphotoview.model.PickHolder;
import stream.pickphotoview.util.PickConfig;
import stream.pickphotoview.util.PickPhotoHelper;
import stream.pickphotoview.util.PickPhotoListener;
import stream.pickphotoview.util.PickPreferences;
import stream.pickphotoview.util.PickUtils;
import stream.pickphotoview.widget.MyToolbar;

public class PickPhotoActivity extends AppCompatActivity {

    View.OnClickListener imageClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            String imgPath = (String) v.getTag(R.id.pick_image_path);
//            Intent intent = new Intent();
//            intent.setClass(PickPhotoActivity.this, PickPhotoPreviewActivity.class);
//            intent.putExtra(PickConfig.INTENT_IMG_PATH, imgPath);
//            intent.putExtra(PickConfig.INTENT_IMG_LIST, allPhotos);
//            intent.putExtra(PickConfig.INTENT_IMG_LIST_SELECT, pickGridAdapter.getSelectPath());
//            intent.putExtra(PickConfig.INTENT_PICK_DATA, pickData);
//            startActivityForResult(intent, PickConfig.PREVIEW_PHOTO_DATA);
        }
    };
    private PickData pickData;
    private RecyclerView photoList;
    private PickGridAdapter pickGridAdapter;
    private MyToolbar myToolbar;
    private TextView selectText, selectImageSize;
    private ArrayList<String> allPhotos;
    private Context mContext;
    private View.OnClickListener selectClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            select();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_activity_pick_photo);
        mContext = getApplicationContext();
        pickData = (PickData) getIntent().getSerializableExtra(PickConfig.INTENT_PICK_DATA);
        if (pickData != null) {
            PickPreferences.getInstance(PickPhotoActivity.this).savePickData(pickData);
        } else {
            pickData = PickPreferences.getInstance(PickPhotoActivity.this).getPickData();
        }
        initToolbar();
        initRecyclerView();
        initSelectLayout();
    }

    @Override
    public void finish() {
        super.finish();
        PickHolder.newInstance(); //Reset stored selected image paths.
        overridePendingTransition(0, R.anim.pick_finish_slide_out_bottom);
    }

    private void initToolbar() {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(pickData.getStatusBarColor());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (pickData.isLightStatusBar()) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
        selectText = findViewById(R.id.tv_pick_photo);
        selectImageSize = findViewById(R.id.tv_preview_photo);
        selectImageSize.setText(String.valueOf("0"));
        myToolbar = findViewById(R.id.toolbar);
        myToolbar.setBackgroundColor(pickData.getToolbarColor());
        myToolbar.setIconColor(pickData.getToolbarIconColor());
        myToolbar.setLeftIcon(R.mipmap.pick_ic_open);
        myToolbar.setRightIcon(R.mipmap.pick_ic_close);
        myToolbar.setPhotoDirName(getString(R.string.pick_all_photo));
        myToolbar.setLeftLayoutOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickPhotoActivity.this.startPhotoListActivity();
            }
        });
        myToolbar.setRightLayoutOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickPhotoActivity.this.finish();
            }
        });

        selectText.setOnClickListener(selectClick);
    }

    private void initRecyclerView() {
        photoList = findViewById(R.id.photo_list);
        photoList.setItemAnimator(new DefaultItemAnimator());
        GridLayoutManager layoutManager = new GridLayoutManager(this, pickData.getSpanCount());
        photoList.setLayoutManager(layoutManager);
        photoList.addItemDecoration(new SpaceItemDecoration(PickUtils.getInstance(PickPhotoActivity.this).dp2px(PickConfig.ITEM_SPACE), pickData.getSpanCount()));
        PickPhotoHelper helper = new PickPhotoHelper(PickPhotoActivity.this, new PickPhotoListener() {
            @Override
            public void pickSuccess() {
                GroupImage groupImage = PickPreferences.getInstance(PickPhotoActivity.this).getListImage();
                allPhotos = groupImage.mGroupMap.get(PickConfig.ALL_PHOTOS);
                if (allPhotos == null) {
//                    Log.d("PickPhotoView","Image is Empty");
                } else {
//                    Log.d("All photos size:", String.valueOf(allPhotos.size()));
                }
                if (allPhotos != null && !allPhotos.isEmpty()) {
                    pickGridAdapter = new PickGridAdapter(PickPhotoActivity.this, allPhotos, pickData, imageClick);
                    photoList.setAdapter(pickGridAdapter);
                }
            }
        });
        helper.getImages(pickData.isShowGif());
    }

    private void initSelectLayout() {
        LinearLayout selectLayout = findViewById(R.id.select_layout);
        selectLayout.setVisibility(View.VISIBLE);
    }

    public void updateSelectText(String selectSize) {
        if (selectSize.equals("0")) {
            selectImageSize.setText(String.valueOf(0));
            selectText.setTextColor(ContextCompat.getColor(mContext, R.color.pick_gray));
            selectText.setEnabled(false);
        } else {
            selectImageSize.setText(String.valueOf(selectSize));
            selectText.setTextColor(pickData.getSelectIconColor());
            selectText.setEnabled(true);
        }
    }

    private void startPhotoListActivity() {
        Intent intent = new Intent();
        intent.setClass(PickPhotoActivity.this, PickListActivity.class);
        intent.putExtra(PickConfig.INTENT_PICK_DATA, pickData);
        startActivityForResult(intent, PickConfig.LIST_PHOTO_DATA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            return;
        }
        if (requestCode == PickConfig.LIST_PHOTO_DATA) {
            if (data != null) {
                String dirName = data.getStringExtra(PickConfig.INTENT_DIR_NAME);
                GroupImage listImage = PickPreferences.getInstance(PickPhotoActivity.this).getListImage();
                allPhotos = listImage.mGroupMap.get(dirName);
                pickGridAdapter.updateData(allPhotos);
                myToolbar.setPhotoDirName(dirName);
                selectText.setText(getString(R.string.pick_pick));
                selectText.setTextColor(ContextCompat.getColor(mContext, R.color.pick_black));
            }
        } else if (requestCode == PickConfig.CAMERA_PHOTO_DATA) {
            String path;
            if (data != null && data.getData() != null) {
                path = data.getData().getPath();
                if (path.contains("/pick_camera")) {
                    path = path.replace("/pick_camera", "/storage/emulated/0/DCIM/Camera");
                }
            } else {
                path = PickUtils.getInstance(PickPhotoActivity.this).getFilePath(PickPhotoActivity.this);
            }
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
            Intent intent = new Intent();
            ArrayList<String> list = new ArrayList<>();
            list.add(path);
            intent.putExtra(PickConfig.INTENT_IMG_LIST_SELECT, list);
            setResult(PickConfig.PICK_PHOTO_DATA, intent);
            finish();
        } else if (requestCode == PickConfig.PREVIEW_PHOTO_DATA) {
            if (data != null) {
                ArrayList<String> selectPath = (ArrayList<String>) data.getSerializableExtra(PickConfig.INTENT_IMG_LIST_SELECT);
                pickGridAdapter.setSelectPath(selectPath);
                pickGridAdapter.notifyDataSetChanged();
                updateSelectText(String.valueOf(selectPath.size()));
            }
        }
    }

    public void select() {
        if (pickGridAdapter == null) {
            return;
        }

        if (!pickGridAdapter.getSelectPath().isEmpty()) {
            Intent intent = new Intent();
            intent.putExtra(PickConfig.INTENT_IMG_LIST_SELECT, pickGridAdapter.getSelectPath());
            setResult(PickConfig.PICK_PHOTO_DATA, intent);
            finish();
        }
    }
}
