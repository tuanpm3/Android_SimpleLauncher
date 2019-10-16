package com.example.mylauncher;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class SimpleLauncher extends AppCompatActivity {
    PackageManager mPackageManager;
    public static List<AppInfo> mAppsInfo;
    GridView mGrdView;
    public static ArrayAdapter<AppInfo> mGridAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_applist);

        mAppsInfo = null;
        mGridAdapter = null;
        loadApps();
        loadListView();
        addGridListeners();
    }

    public void addGridListeners() {
        try {
            mGrdView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String packageName = mAppsInfo.get(i).packageName.toString();
                    Intent intent = mPackageManager.getLaunchIntentForPackage(packageName);
                    SimpleLauncher.this.startActivity(intent);
                }
            });
        } catch (Exception ex) {
            Toast.makeText(SimpleLauncher.this, ex.getMessage().toString() + " Grid", Toast.LENGTH_LONG).show();
            Log.e("Error Grid", ex.getMessage().toString() + " Grid");
        }
    }

    private void loadListView() {
        try {
            mGrdView = (GridView) findViewById(R.id.grd_allApps);
            if (mGridAdapter == null) {
                mGridAdapter = new ArrayAdapter<AppInfo>(this, R.layout.grd_items, mAppsInfo) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {

                        ViewHolderItem viewHolder = null;

                        if (convertView == null) {
                            convertView = getLayoutInflater().inflate(
                                R.layout.grd_items, parent, false
                            );
                            viewHolder = new ViewHolderItem();
                            viewHolder.icon = convertView.findViewById(R.id.img_icon);
                            viewHolder.label = convertView.findViewById(R.id.txt_label);

                            convertView.setTag(viewHolder);
                        } else {
                            viewHolder = (ViewHolderItem) convertView.getTag();
                        }

                        AppInfo appInfo = mAppsInfo.get(position);

                        if (appInfo != null) {
                            viewHolder.icon.setImageDrawable(appInfo.icon);
                            viewHolder.label.setText(appInfo.label);
                        }
                        return convertView;

                    }

                    final class ViewHolderItem {
                        ImageView icon;
                        TextView label;
                    }
                };
            }

            mGrdView.setAdapter(mGridAdapter);
        } catch (Exception ex) {
            Toast.makeText(SimpleLauncher.this, ex.getMessage().toString() + " loadListView", Toast.LENGTH_LONG).show();
            Log.e("Error loadListView", ex.getMessage().toString() + " loadListView");
        }
    }

    private void loadApps() {
        try {

            mPackageManager = getPackageManager();
            if (mAppsInfo == null) {
                mAppsInfo = new ArrayList<AppInfo>();

                Intent i = new Intent(Intent.ACTION_MAIN, null);
                i.addCategory(Intent.CATEGORY_LAUNCHER);

                List<ResolveInfo> availableApps = mPackageManager.queryIntentActivities(i, 0);
                for (ResolveInfo ri : availableApps) {
                    AppInfo appinfo = new AppInfo();
                    appinfo.label = ri.loadLabel(mPackageManager);
                    appinfo.packageName = ri.activityInfo.packageName;
                    appinfo.icon = ri.activityInfo.loadIcon(mPackageManager);
                    mAppsInfo.add(appinfo);
                }
            }

        } catch (Exception ex) {
            Toast.makeText(SimpleLauncher.this, ex.getMessage().toString() + " loadApps", Toast.LENGTH_LONG).show();
            Log.e("Error loadApps", ex.getMessage().toString() + " loadApps");
        }
    }
}
