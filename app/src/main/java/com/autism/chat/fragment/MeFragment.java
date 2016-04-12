package com.autism.chat.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.autism.chat.ChatApplication;
import com.autism.chat.R;
import com.autism.chat.base.BaseFragment;
import com.autism.chat.bean.User;
import com.autism.chat.bean.UserModel;
import com.autism.chat.bean.listener.QueryUserListener;
import com.autism.chat.bean.listener.UpdateCacheListener;
import com.autism.chat.receiver.AddFriendEvent;
import com.autism.chat.receiver.ChatEvent;
import com.autism.chat.receiver.UpDataEvent;
import com.autism.chat.utils.CommonUtil;
import com.autism.chat.utils.DirUtil;
import com.autism.chat.utils.ImageLoadOptions;
import com.autism.chat.utils.ViewUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orhanobut.logger.Logger;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.UUID;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by AutismPerson on 4/5 0005.
 */
public class MeFragment extends BaseFragment implements View.OnClickListener {

    /**
     * 相册获取
     **/
    private static final int REQUEST_CODE_GALLERY = 0x111;
    private static final int REQUEST_CODE_CAMERA = 0x222;
    private static final int REQUEST_CODE_CROP = 0x333;
    private File sdcardTempFile;
    private ImageView iv;
    private User currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, null);
        //头像
        iv = (ImageView) view.findViewById(R.id.me_iv_icon);
        ImageView er = (ImageView) view.findViewById(R.id.er);
        TextView tv = (TextView) view.findViewById(R.id.me_tv_account);
        RelativeLayout setting = (RelativeLayout) view.findViewById(R.id.me_item_setting);

        ImageLoader.getInstance().displayImage(currentUser.getAvatar(), iv, ImageLoadOptions.getOptions());
        ViewUtil.setAvatar(currentUser.getAvatar(), R.drawable.default_icon_user, iv);
        tv.setText(currentUser.getUsername());
        iv.setOnClickListener(this);
        er.setOnClickListener(this);
        setting.setOnClickListener(this);
        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDir();
    }

    /**
     * 初始化文件路劲
     */
    private void initDir() {
        // if (mActivity == null) return;
        //当前用户名
        currentUser = UserModel.getInstance().getCurrentUser();
        String iconDir = DirUtil.getIconDir(getActivity());//头像存储路径
        sdcardTempFile = new File(iconDir, fileName());
        if (!sdcardTempFile.getParentFile().exists()) {
            sdcardTempFile.getParentFile().mkdirs();
        }
    }


    // 随机文件名
    private String fileName() {
        return UUID.randomUUID().toString() + ".png";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.me_iv_icon:
                AlertDialog.Builder ab = new AlertDialog.Builder(mActivity);
                String item[] = {"从相册获取", "拍照获取"};
                ab.setItems(item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                getimageFromGallery();  //相册获取
                                break;
                            case 1:
                                getImageFromCamera();
                                // getImageFromCamera();//拍照获取
                                break;
                        }
                    }
                });
                ab.show();

                break;
            case R.id.er:
                toast("设置二维码");
                break;
            case R.id.me_item_setting:
                toast("设置个人信息");
                break;
        }
    }

    /**
     * 相片获取
     */
    private void getimageFromGallery() {
        Intent intent = new Intent("android.intent.action.PICK");
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                "image/*");
        intent.putExtra("output", Uri.fromFile(sdcardTempFile));
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比? intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);// 输出图片大小
        intent.putExtra("outputY", 300);
        startActivityForResult(intent, REQUEST_CODE_GALLERY);       //获取后返回结果
    }


    /**
     * 拍照获取
     **/
    protected void getImageFromCamera() {
        Uri uri = Uri.fromFile(sdcardTempFile);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra("output", uri);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_GALLERY: {        //相册获取后返回结果
                performImageBack();
                break;
            }
            case REQUEST_CODE_CAMERA: {
                Uri uri = Uri.fromFile(sdcardTempFile);         //相册存储路径
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(uri, "image/*");
                intent.putExtra("output", uri);
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 1);// 裁剪框intent.putExtra("aspectY",
                // 1);
                intent.putExtra("outputX", 300);// 输出图片大小
                intent.putExtra("outputY", 300);
                startActivityForResult(intent, REQUEST_CODE_CROP);
                break;
            }
            case REQUEST_CODE_CROP: {
                performImageBack();
                break;
            }
            default:
                break;
        }
    }

    /**
     * 更新
     */
    private void performImageBack() {
        final String path = sdcardTempFile.getAbsolutePath();
        Log.e("Chat", "path" + path);
        Bitmap bmp = BitmapFactory.decodeFile(path);
        iv.setImageBitmap(bmp);
        uploadAvatar(path);
    }

    /**
     * 更新头像
     *
     * @param url
     */
    private void updateUserAvatar(String url) {
//        //对象传递，跳转界面


//        //私聊
//        BmobIM.getInstance().startPrivateConversation(info, new ConversationListener() {
//            @Override
//            public void done(BmobIMConversation c, BmobException e) {
//                if (e == null) {
//                    Intent intent = new Intent();
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("a", c);
//                    if (bundle != null) {
//                        intent.putExtra(mContext.getPackageName(), bundle);
//                        EventBus.getDefault().post(new AddFriendEvent(intent));
//                    }
//                } else {
//                    Toast.makeText(ChatApplication.getInstance(), e.getMessage() + "(" + e.getErrorCode() + ")", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    /*
     上传图片，一个小bug费了我一个上午
     */
    private void uploadAvatar(String path) {
        final BmobFile file = new BmobFile(new File(path));
        file.uploadblock(ChatApplication.getInstance(), new UploadFileListener() {
            @Override
            public void onSuccess() {
                final String fileUrl = file.getFileUrl(mActivity);
                Log.e("Chat", "file.getUrl()" + file.getUrl());
                Log.e("Chat", "file.getFileUrl(mActivity)" + file.getFileUrl(mActivity));
                currentUser.setAvatar(file.getFileUrl(mActivity));
                currentUser.save(mActivity);
                BmobIMUserInfo info = new BmobIMUserInfo(currentUser.getObjectId(), currentUser.getUsername(), currentUser.getAvatar());
                EventBus.getDefault().post(new ChatEvent(info));
            }

            @Override
            public void onFailure(int i, String s) {
                toast("上传失败" + s);
            }
        });
    }


    @Subscribe
    public void OnEventMainThread(MessageEvent event){}
}
