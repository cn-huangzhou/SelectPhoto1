package com.hz.selectphoto;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> mList = new ArrayList<>();    //选中的图片
    private GridView mGridView;
    private GridAdapter adapter;
    private String ADDPIC = "add";
    private static int REQUEST_IMAGE = 0x0010;
    private TextView tv_selectP;    //选中的图片地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGridView = (GridView) findViewById(R.id.gridView);
        tv_selectP = (TextView) findViewById(R.id.tv_selectP);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String temp = mList.get(i);
                if (temp.equals(ADDPIC)) {
                    if (mList.size() < 9) {
                        mList.remove(ADDPIC);
                    }
                    if (mList.size() == 9
                            && mList.contains(ADDPIC))
                        mList.remove(ADDPIC);

                    MultiImageSelector.create(MainActivity.this)
                            .showCamera(true) // 是否显示相机. 默认为显示
                            .count(9) // 最大选择图片数量, 默认为9. 只有在选择模式为多选时有效
                            .multi()    //多选模式
                            .origin(mList) // 默认已选择图片. 只有在选择模式为多选时有效
                            .start(MainActivity.this, REQUEST_IMAGE);
                }
            }
        });

        mList.add(ADDPIC);
        adapter = new GridAdapter();
        mGridView.setAdapter(adapter);

        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //发送
                if (mList.size() != 0) {  //如果有图片先传图片
                    if (mList.contains(ADDPIC) && !mList.get(0).equals(ADDPIC))
                        mList.remove(ADDPIC);   //先移除末尾的加号
                    tv_selectP.setText(mList.toString());

                    //上传图片过程



                    //刷新一遍列表
                    mList.clear();
                    mList.add(ADDPIC);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                // 获取返回的图片列表
                mList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                mList.add(ADDPIC);
                if (mList.size() > 9) {
                    mList.remove(ADDPIC);
                }
            }else{
                mList.add(ADDPIC);
            }
            adapter.notifyDataSetChanged();
            tv_selectP.setText(mList.toString());
        }
    }


    class GridAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout
                    .selectimg_item, null, false);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_image);
            ImageView iv_delimg = (ImageView) convertView.findViewById(R.id.iv_delimg);

            String path = mList.get(position);
            if (!TextUtils.isEmpty(path) && path.equals(ADDPIC)) {
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setImageResource(R.mipmap.icon_addpic_unfocused);
                iv_delimg.setVisibility(View.GONE);
            } else {
                iv_delimg.setVisibility(View.VISIBLE);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(MainActivity.this).load(mList.get(position))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .crossFade()
                        .into(imageView);
            }
            iv_delimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MainActivity.this,"删除！",Toast.LENGTH_SHORT).show();
                    mList.remove(position);
                    if (mList.size() < 9 && !mList.contains(ADDPIC)) {
                        mList.add(ADDPIC);
                    }
//                    if (mList.size() == 1 && mList.contains(ADDPIC)) {  //如果到最后只剩一个 + 号就全部删除
//                        mList.remove(ADDPIC);
//                    }

                    tv_selectP.setText(mList.toString());
                    adapter.notifyDataSetChanged();
                }
            });
            return convertView;
        }
    }
}
