package stream.pickphotoview;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import stream.pickphotoview.adapter.PickListAdapter;
import stream.pickphotoview.model.PickData;
import stream.pickphotoview.util.PickConfig;
import stream.pickphotoview.widget.MyToolbar;

public class PickListActivity extends AppCompatActivity {

    private PickData pickData;
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String dirName = (String) v.getTag(R.id.pick_dir_name);
            Intent intent = new Intent();
            intent.setClass(PickListActivity.this, PickPhotoActivity.class);
            intent.putExtra(PickConfig.INTENT_DIR_NAME, dirName);
            PickListActivity.this.setResult(PickConfig.LIST_PHOTO_DATA, intent);
            PickListActivity.this.finish();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_activity_pick_photo);
        pickData = (PickData) getIntent().getSerializableExtra(PickConfig.INTENT_PICK_DATA);
        initToolbar();
        initRecyclerView();
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
        MyToolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setBackgroundColor(pickData.getToolbarColor());
        myToolbar.setIconColor(pickData.getToolbarIconColor());
        myToolbar.setPhotoDirName(getString(R.string.pick_photos));
        myToolbar.setLeftIcon(R.mipmap.pick_ic_back);
        myToolbar.setLeftLayoutOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickListActivity.this.finish();
            }
        });
    }

    private void initRecyclerView() {
        RecyclerView listPhoto = findViewById(R.id.photo_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listPhoto.setLayoutManager(layoutManager);
        PickListAdapter listAdapter = new PickListAdapter(PickListActivity.this, listener);
        listPhoto.setAdapter(listAdapter);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.pick_finish_slide_out_left);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        if (intent.getComponent().getClassName().equals(PickPhotoActivity.class.getName())) {
            overridePendingTransition(R.anim.pick_start_slide_in_left, 0);
        }
    }
}
