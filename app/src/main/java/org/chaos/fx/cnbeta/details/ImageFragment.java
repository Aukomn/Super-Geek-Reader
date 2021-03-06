/*
 * Copyright 2017 Chaos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.chaos.fx.cnbeta.details;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewCompat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.chrisbanes.photoview.OnOutsidePhotoTapListener;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.chaos.fx.cnbeta.BuildConfig;
import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.util.CryptUtil;
import org.chaos.fx.cnbeta.wxapi.WXApiProvider;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Chaos
 *         16/02/2017
 */

public class ImageFragment extends Fragment {

    private static final String TAG = "ImageFragment";

    private static final String KEY_IMAGE_URL = "image_url";

    public static ImageFragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString(KEY_IMAGE_URL, url);
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private static final String FILE_SUFFIX = ".png";

    @BindView(R.id.image) PhotoView mPhotoView;
    @BindView(R.id.progress) ProgressBar mProgressBar;

    private String mUrl;
    private ImageStore mImageStore;

    private OnPhotoTapListener mImageTapListener = new OnPhotoTapListener() {
        @Override
        public void onPhotoTap(ImageView view, float x, float y) {
            loadImage();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        mUrl = getArguments().getString(KEY_IMAGE_URL);
        ViewCompat.setTransitionName(mPhotoView, mUrl);

        loadImage();
        mPhotoView.setOnOutsidePhotoTapListener(new OnOutsidePhotoTapListener() {
            @Override
            public void onOutsidePhotoTap(ImageView imageView) {
                if (getActivity() != null) {
                    getActivity().supportFinishAfterTransition();
                }
            }
        });
        mPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                registerForContextMenu(v);
                v.showContextMenu();
                unregisterForContextMenu(v);
                return true;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelStoreRequest();
        Picasso.get().cancelRequest(mPhotoView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.image_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                final String path = getImageCachePath();
                storeImageToLocalIfNeed(path, new Callback() {
                    @Override
                    public void onSuccess() {
                        String message = getString(R.string.format_saved_to_path, path);
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Exception e) {
                        failedToStoreImage(e);
                    }
                });
                return true;
            case R.id.wechat_friends:
                shareImageToWeChat(false);
                return true;
            case R.id.wechat_timeline:
                shareImageToWeChat(true);
                return true;
            case R.id.share_more:
                shareToMore();
                return true;
        }
        return false;
    }

    private String getImageCachePath() {
        String fileName = CryptUtil.encryptToMD5(mUrl) + FILE_SUFFIX;
        return getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + fileName;
    }

    private void storeImageToLocalIfNeed(String path, Callback callback) {
        File f = new File(path);
        if (f.exists()) {
            if (callback != null) {
                callback.onSuccess();
            }
        } else {
            cancelStoreRequest();
            mImageStore = new ImageStore(path, callback);
            Picasso.get().load(mUrl).into(mImageStore);
        }
    }

    private void cancelStoreRequest() {
        if (mImageStore != null) {
            Picasso.get().cancelRequest(mImageStore);
        }
    }

    private void shareImageToWeChat(final boolean toTimeline) {
        final String path = getImageCachePath();
        storeImageToLocalIfNeed(path, new Callback() {
            @Override
            public void onSuccess() {
                WXApiProvider.sharePicture(((BitmapDrawable) mPhotoView.getDrawable()).getBitmap(), path, toTimeline);
            }

            @Override
            public void onError(Exception e) {
                failedToStoreImage(e);
            }
        });
    }

    private void shareToMore() {
        final String path = getImageCachePath();
        storeImageToLocalIfNeed(path, new Callback() {
            @Override
            public void onSuccess() {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/jpg");
                if (Build.VERSION.SDK_INT >= 24) {
                    shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(getActivity().getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", new File(path)));
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));
                }
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_to)));
            }

            @Override
            public void onError(Exception e) {
                failedToStoreImage(e);
            }
        });
    }

    private void failedToStoreImage(Exception e) {
        String message = e.getMessage();
        if (e instanceof NoSpaceLeftException) {
            message = getString(R.string.no_space_left);
        }
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void loadImage() {
        showLoading(true);
        Picasso.get()
                .load(mUrl)
                .into(mPhotoView, new Callback() {
                    @Override
                    public void onSuccess() {
                        showLoading(false);
                        mPhotoView.setOnPhotoTapListener(null);
                        if (getActivity() != null) {
                            getActivity().supportStartPostponedEnterTransition();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        showLoading(false);
                        mPhotoView.setImageResource(R.drawable.default_content_image_failed);
                        mPhotoView.setOnPhotoTapListener(mImageTapListener);
                        if (getActivity() != null) {
                            getActivity().supportStartPostponedEnterTransition();
                        }
                    }
                });
    }

    private void showLoading(boolean loading) {
        mProgressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        mPhotoView.setVisibility(!loading ? View.VISIBLE : View.GONE);
    }
}
