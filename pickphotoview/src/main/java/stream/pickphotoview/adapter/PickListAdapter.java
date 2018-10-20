package stream.pickphotoview.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import stream.pickphotoview.R;
import stream.pickphotoview.model.DirImage;
import stream.pickphotoview.model.GroupImage;
import stream.pickphotoview.util.PickPreferences;

public class PickListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private GroupImage groupImage;
    private DirImage dirImage;
    private View.OnClickListener listener;
    private Context mContext;

    public PickListAdapter(Context c, View.OnClickListener listener) {
        this.mContext = c;
        this.groupImage = PickPreferences.getInstance(mContext).getListImage();
        this.dirImage = PickPreferences.getInstance(mContext).getDirImage();
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        GroupImageViewHolder holder = new GroupImageViewHolder(LayoutInflater.from(mContext).inflate(R.layout.pick_item_list_layout, parent, false));
        holder.itemView.setOnClickListener(listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (dirImage != null) {
            String dirName = dirImage.dirName.get(position);
            ArrayList<String> paths = groupImage.mGroupMap.get(dirName);
            GroupImageViewHolder groupImageViewHolder = (GroupImageViewHolder) holder;
            groupImageViewHolder.bindItem(dirName, paths);
        }

    }

    @Override
    public int getItemCount() {
        if (dirImage != null) {
            return dirImage.dirName.size();
        } else {
            return 0;
        }
    }

    private class GroupImageViewHolder extends RecyclerView.ViewHolder {

        private ImageView cover;
        private TextView dirNameText, photoSizeText;

        GroupImageViewHolder(View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.image);
            dirNameText = itemView.findViewById(R.id.title);
            photoSizeText = itemView.findViewById(R.id.description);
        }

        void bindItem(String dirName, ArrayList<String> paths) {
            dirNameText.setText(dirName);
            photoSizeText.setText(String.format(mContext.getString(R.string.pick_photo_size), paths.size() + ""));
            Glide.with(mContext).load(Uri.parse("file://" + paths.get(0))).into(cover);
            itemView.setTag(R.id.pick_dir_name, dirName);
        }

    }
}
