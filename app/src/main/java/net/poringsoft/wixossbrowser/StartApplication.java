package net.poringsoft.wixossbrowser;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import net.poringsoft.wixossbrowser.data.EnvPath;
import net.poringsoft.wixossbrowser.utils.PSDebug;

import java.io.File;

/**
 * アプリケーション起動時の処理
 * Created by mry on 2014/05/03.
 */
public class StartApplication extends Application {
    /**
     * 起動時処理
     */
    @Override
    public void onCreate() {
        super.onCreate();

        PSDebug.d("call");

        //デバッグ状態セット
        PSDebug.initDebugFlag(getApplicationContext());

        //初期化
        EnvPath.init();

        //https://github.com/nostra13/Android-Universal-Image-Loader
        File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
        ImageLoaderConfiguration  config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(3)  //非同期処理数
                .memoryCacheSize(2 * 1024 * 1024)                   //メモリキャッシュの制限サイズ
                .discCacheSize(50 * 1024 * 1024)                    //キャッシュファイルサイズ
                //.memoryCache(new LruMemoryCache(2 * 1024 * 1024))   //メモリキャッシュ設定
                //.discCache(new UnlimitedDiscCache(cacheDir))        //ディクスキャッシュ設定
                //.discCacheFileCount(300)                            //キャッシュファイル数
                .build();
        ImageLoader.getInstance().init(config);
    }
}
