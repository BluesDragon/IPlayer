# iplayer
Android media player library.
-----------------------------
#### 播放器快速开发框架（播放库）背景：<br>
>* `在日常项目中，经常会有多媒体相关音、视频模块开发需求，介于Android系统默认多媒体播放整体使用流程比较复杂，在此分享一下这个小型的多媒体应用快速开发框架。`<br>
>* `框架封装了Android多媒体播放音、视频的基本流程（默认使用Android原生MediaPlayer，如需扩展其他播放引擎，可以通过播放控制接口（IPlayController）进行设置，详见接口注释），只需要初始化并配置好接口，就可以很方便的开发播放器应用。`<br>
  
#### 优点：<br>
>* 调用方便，快速开发：所有调用入口全部以接口形式开放，内置默认音视频全部处理流程。<br>
>* 功能可配置：内置音频、视频两个Manager，除现有默认功能外，可以通过config配置其他功能，如线控等焦点、锁屏监听等；<br>
>* 高度可扩展：所有模块化功能接口，均可以自定义实现类、或直接继承来自定义功能模块。<br>
    
#### 所需权限：<br>
>`android.permission.WRITE_EXTERNAL_STORAGE`<br>
>`android.permission.WAKE_LOCK`<br>

#### 集成介绍:<br>
>* 音频播放：初始化AudioPlayerManager，调用相关API即可。<br>
>* 视频播放：<br>
  1、初始化VideoPlayerManager；<br>
  2、获取视频显示界面的layout（包含了SurfaceView等）并设置到需要显示的位置，后续会丰富视频现实界面相关的效果。<br>
  3、调用相关API即可。<br>

```视频播放Demo：（音频Demo不赘述，直接调用播放即可）<br>
/**
* 视频播放测试类
*/
public class VideoTestActivity extends Activity {

  private static final int HEIGHT = 608;// 竖屏时窗口高度

  private VideoPlayerManager mVideoPlayerManager;// 视频播放管理者

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_video_test);
    init();
  }

  private void init() {
    initVideoPlayerManager();
    initView();
    startPlay();
  }

  /**
  * 初始化VideoPlayerManager
  */
  private void initVideoPlayerManager() {
    VideoPlayerConfig videoPlayerConfig = new VideoPlayerConfig();
    videoPlayerConfig.setRegisterAudioFocus(true);
    mVideoPlayerManager = new VideoPlayerManager(this, videoPlayerConfig);
  }

  /**
  * 初始化布局，获取并添加播放界面
  */
  private void initView() {
    RelativeLayout main = (RelativeLayout) this
            .findViewById(R.id.main_video);
    RelativeLayout playerView = mVideoPlayerManager
            .getVideoPlayController().getViewController().getIPlayerView()
            .getPlayerView();
    LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
            HEIGHT);
    playerView.setLayoutParams(params);
    main.addView(playerView);
  }

  /**
  * 播放视频
  */
  private void startPlay() {
    //获取播放控制调用接口
    IPlayController videoPlayController = mVideoPlayerManager
            .getVideoPlayController();
    PlayItem playItem = new PlayItem();
    String playUrl = "";// 播放地址 TODO
    playItem.setPlayUrl(playUrl);
    videoPlayController.startPlay(playItem);
  }

  @Override
  protected void onResume() {
    super.onResume();
    // 通知播放库，窗口焦点变化
    if (mVideoPlayerManager != null) {
        mVideoPlayerManager.onResume();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    // 通知播放库，窗口焦点变化
    if (mVideoPlayerManager != null) {
        mVideoPlayerManager.onPause();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    // 退出时，释放播放库
    if (mVideoPlayerManager != null) {
        mVideoPlayerManager.release();
    }
  }
}
```
