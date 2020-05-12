package com.example.lanchat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lanchat.Bean.Msg;
import com.example.lanchat.R;
import com.example.lanchat.Util.OtherUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 消息体适配器
 * 作者：方荣福
 * 时间：2020.5.7
 */
public class MsgAdapter extends ArrayAdapter<Msg> {
    private int resourceId;
    private Context mContext;

    public MsgAdapter(Context context, int textViewresourceId, List<Msg> objects) {
        super(context, textViewresourceId, objects);
        resourceId = textViewresourceId;
        mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Msg msg = getItem(position);
        View view;
        ViewHolder viewHolder;

        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.leftLayout = view.findViewById(R.id.left_layout);
            viewHolder.rightLayout = view.findViewById(R.id.right_Layout);
            viewHolder.leftMsg = view.findViewById(R.id.left_msg);
            viewHolder.rightMsg = view.findViewById(R.id.right_msg);
            viewHolder.rightImage = view.findViewById(R.id.right_image);
            viewHolder.leftImage = view.findViewById(R.id.left_image);
            viewHolder.riRightImage = view.findViewById(R.id.ri_image_right);
            viewHolder.riLeftImage = view.findViewById(R.id.ri_image_left);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        if(msg.getType()==Msg.RECEIVED){

            //加载头像
            viewHolder.riLeftImage.setImageBitmap(OtherUtil.initHeadImage(msg.getImageId(),mContext));

            //如果是收到的消息，则显示左边消息布局，将右边消息布局隐藏
            if(msg.getContentType()==Msg.TEXT){
                viewHolder.leftLayout.setVisibility(View.VISIBLE);
                viewHolder.rightLayout.setVisibility(View.GONE);
                viewHolder.leftImage.setVisibility(View.GONE);
                viewHolder.rightImage.setVisibility(View.GONE);
                viewHolder.riLeftImage.setVisibility(View.VISIBLE);
                viewHolder.riRightImage.setVisibility(View.GONE);
                viewHolder.leftMsg.setVisibility(View.VISIBLE);
                viewHolder.rightMsg.setVisibility(View.GONE);
                viewHolder.leftMsg.setText(msg.getContent());
            }else if(msg.getContentType()==Msg.IMAGE){
                viewHolder.leftLayout.setVisibility(View.GONE);
                viewHolder.rightLayout.setVisibility(View.GONE);
                viewHolder.leftImage.setVisibility(View.VISIBLE);
                viewHolder.rightImage.setVisibility(View.GONE);
                viewHolder.leftMsg.setVisibility(View.GONE);
                viewHolder.rightMsg.setVisibility(View.GONE);
                viewHolder.riLeftImage.setVisibility(View.VISIBLE);
                viewHolder.riRightImage.setVisibility(View.GONE);
                viewHolder.leftImage.setImageBitmap(msg.getImageBitmap());
            }
            return view;
        }else if(msg.getType()==Msg.SENT){

            //加载头像
            viewHolder.riRightImage.setImageBitmap(OtherUtil.initHeadImage(msg.getImageId(),mContext));

            //如果是发出去的消息，显示右边布局的消息布局，将左边的消息布局隐藏
            if(msg.getContentType()==Msg.TEXT && msg.getImageUri()==null){
                viewHolder.rightLayout.setVisibility(View.VISIBLE);
                viewHolder.leftLayout.setVisibility(View.GONE);
                viewHolder.rightImage.setVisibility(View.GONE);
                viewHolder.leftImage.setVisibility(View.GONE);
                viewHolder.riLeftImage.setVisibility(View.GONE);
                viewHolder.riRightImage.setVisibility(View.VISIBLE);
                viewHolder.rightMsg.setVisibility(View.VISIBLE);
                viewHolder.leftMsg.setVisibility(View.GONE);
                viewHolder.rightMsg.setText(msg.getContent());
            }else if(msg.getContentType()==Msg.IMAGE && msg.getContent()==null){
                viewHolder.rightLayout.setVisibility(View.GONE);
                viewHolder.leftLayout.setVisibility(View.GONE);
                viewHolder.rightImage.setVisibility(View.VISIBLE);
                viewHolder.leftImage.setVisibility(View.GONE);
                viewHolder.rightMsg.setVisibility(View.GONE);
                viewHolder.leftMsg.setVisibility(View.GONE);
                viewHolder.riLeftImage.setVisibility(View.GONE);
                viewHolder.riRightImage.setVisibility(View.VISIBLE);
                viewHolder.rightImage.setImageURI(msg.getImageUri());
            }
            return view;
        }

        return view;
    }

    /**
     * ViewHolder内部类，避免UI组件重复加载
     */
    class ViewHolder{
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
        ImageView rightImage;
        ImageView leftImage;
        CircleImageView riRightImage;
        CircleImageView riLeftImage;
    }
}

