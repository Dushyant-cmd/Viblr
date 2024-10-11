package com.veblr.android.veblrapp.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.veblr.android.veblrapp.R;

public class GalleryFragment extends Fragment {

        private int count;
        private Bitmap[] thumbnails;
        private boolean[] thumbnailsselection;
        private String[] arrPath;
        private int[] typeMedia;
        private ImageAdapter imageAdapter;

        @SuppressLint("NewApi") @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.gallery_gridview, container, false);

            String[] columns = { MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.DATA,
                    MediaStore.Files.FileColumns.DATE_ADDED,
                    MediaStore.Files.FileColumns.MEDIA_TYPE,
                    MediaStore.Files.FileColumns.MIME_TYPE,
                    MediaStore.Files.FileColumns.TITLE,
            };
            String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
            final String orderBy = MediaStore.Files.FileColumns.DATE_ADDED;
            Uri queryUri = MediaStore.Files.getContentUri("external");

            @SuppressWarnings("deprecation")
            Cursor imagecursor = getActivity().managedQuery(queryUri,
                    columns,
                    selection,
                    null, // Selection args (none).
                    MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
            );

            int image_column_index = imagecursor.getColumnIndex(MediaStore.Files.FileColumns._ID);
            this.count = imagecursor.getCount();
            this.thumbnails = new Bitmap[this.count];
            this.arrPath = new String[this.count];
            this.typeMedia = new int[this.count];
            this.thumbnailsselection = new boolean[this.count];
            for (int i = 0; i < this.count; i++) {
                imagecursor.moveToPosition(i);
                int id = imagecursor.getInt(image_column_index);
                int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inSampleSize = 4;
                bmOptions.inPurgeable = true;
                int type = imagecursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);
                int t = imagecursor.getInt(type);
                if(t == 1)
                    thumbnails[i] = MediaStore.Images.Thumbnails.getThumbnail(
                            getActivity().getContentResolver(), id,
                            MediaStore.Images.Thumbnails.MINI_KIND, bmOptions);
                else if(t == 3)
                    thumbnails[i] = MediaStore.Video.Thumbnails.getThumbnail(
                            getActivity().getContentResolver(), id,
                            MediaStore.Video.Thumbnails.MINI_KIND, bmOptions);

                arrPath[i]= imagecursor.getString(dataColumnIndex);
                typeMedia[i] = imagecursor.getInt(type);
            }

            GridView imagegrid = (GridView) v.findViewById(R.id.PhoneImageGrid);
         //   Button reSizeGallery = (Button) v.findViewById(R.id.reSizeGallery);

            imageAdapter = new ImageAdapter();
            imagegrid.setAdapter(imageAdapter);
            imagecursor.close();

          /*  reSizeGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
               //     ChatViewerAdapter.ScreenResize(getActivity());
                }
            });*/

            return v;//super.onCreateView(inflater, container, savedInstanceState);
        }

        public class ImageAdapter extends BaseAdapter {
            private LayoutInflater mInflater;

            public ImageAdapter() {
                mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            public int getCount() {
                return count;
            }

            public Object getItem(int position) {
                return position;
            }

            public long getItemId(int position) {
                return position;
            }

            @SuppressLint("NewApi") public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder;
/*
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int height = size.y;

                if (convertView == null) {
                    holder = new ViewHolder();
                    convertView = mInflater.inflate(
                            R.layout.image_item, null);
                    holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
                    holder.videoICON = (ImageView) convertView.findViewById(R.id.videoICON);
                    convertView.setTag(holder);
                }
                else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.imageview.getLayoutParams().height = height/6;
                holder.imageview.getLayoutParams().width = width/4;

                holder.imageview.setId(position);

                if(typeMedia[position] == 1)
                    holder.videoICON.setVisibility(View.GONE);
                else if(typeMedia[position] == 3)
                    holder.videoICON.setVisibility(View.VISIBLE);

                holder.imageview.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        int id = v.getId();

                        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                        int height = display.getHeight();
                        final int height_half = (int) (height/2.5);

                        RelativeLayout fragment_layout = (RelativeLayout) getActivity()
                                .findViewById(R.id.fragment_gallery);
                        fragment_layout.setVisibility(View.VISIBLE);
                        fragment_layout.getLayoutParams().height = height_half;

                        GalleryImageChooseFragment f_img_choose =new GalleryImageChooseFragment();
                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        Bundle args = new Bundle();
                        args.putString("PATH", arrPath[id]);
                        f_img_choose.setArguments(args);
                        ft.show(f_img_choose);
                        ft.replace(R.id.fragment_tattle, f_img_choose);
                        ft.addToBackStack("f_img_choose");
                        ft.commit();
                    }
                });
                holder.imageview.setImageBitmap(thumbnails[position]);
                holder.id = position;*/
                return convertView;
            }
        }

        class ViewHolder {
            ImageView imageview;
            ImageView videoICON;
            int id;
        }
    }

