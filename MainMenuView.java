package com.example.myfirstandroidapp;

import com.bn.screen.auto.ScreenScaleResult;
import com.bn.screen.auto.ScreenScaleUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainMenuView extends SurfaceView implements SurfaceHolder.Callback
{
	private static final Rect BACK_XOFFSET = null;
	private static final RectF BACK_YOFFSET = null;
	private static final Rect BUTTON_START_XOFFSET = null;
	private static final RectF BUTTON_START_YOFFSET = null;
	private static final Rect BUTTON_HELP_XOFFSET = null;
	private static final RectF BUTTON_HELP_YOFFSET = null;
	private static final Rect BUTTON_ABOUT_XOFFSET = null;
	private static final RectF BUTTON_ABOUT_YOFFSET = null;
	private static final float BUTTON_OUT_XOFFSET = (Float) null;
	private static final Rect BUTTON_START_WIDTH = null;
	private static final RectF BUTTON_START_HEIGHT = null;
	private static final Rect BUTTON_HELP_WIDTH = null;
	private static final RectF BUTTON_HELP_HEIGHT = null;
	private static final Rect BUTTON_ABOUT_WIDTH = null;
	private static final RectF BUTTON_ABOUT_HEIGHT = null;
	private static final Rect BUTTON_OUT_WIDTH = null;
	private static final int BUTTON_OUT_YOFFSET = 0;
	private static final int BUTTON_OUT_HEIGHT = 0;
	FightagainstlandlordsActivity activity;
	Paint paint;
	Bitmap bitmapStart;
	Bitmap bitmapHelp;
	Bitmap bitmapAbout;
	Bitmap bitmapBack;
	Bitmap bitmapOut;

	public MainMenuView(FightagainstlandlordsActivity activity) {
		super(activity);
		this.activity=activity;
		this.getHolder().addCallback(this);
		paint=new Paint();
		paint.setAntiAlias(true);
		initBitmap();
	}
	public void initBitmap()
	{
		//加载开始按钮的图片
		bitmapStart=BitmapFactory.decodeResource(getResources(), R.drawable.start);
		//加载帮助按钮的图片
		bitmapHelp=BitmapFactory.decodeResource(getResources(), R.drawable.help);
		//加载关于按钮的图片
		bitmapAbout=BitmapFactory.decodeResource(getResources(), R.drawable.about);
		//加载退出按钮的图片
		bitmapOut=BitmapFactory.decodeResource(getResources(), R.drawable.out);
		//加载背景的图片
		bitmapBack=BitmapFactory.decodeResource(getResources(), R.drawable.back);
	}
	@Override
	public void onDraw(Canvas canvas)
	{
		ScreenScaleResult ssr=FightagainstlandlordsActivity.ssr;
		canvas.save();
		canvas.translate(ssr.lucX,ssr.lucY);
		canvas.scale(ssr.ratio, ssr.ratio);
		canvas.drawBitmap(bitmapBack, BACK_XOFFSET,BACK_YOFFSET, paint);
		//start按钮
		canvas.drawBitmap(bitmapStart, BUTTON_START_XOFFSET, BUTTON_START_YOFFSET, null);
		//start按钮
		canvas.drawBitmap(bitmapHelp, BUTTON_HELP_XOFFSET, BUTTON_HELP_YOFFSET, null);
		//start按钮
		canvas.drawBitmap(bitmapAbout, BUTTON_ABOUT_XOFFSET, BUTTON_ABOUT_YOFFSET, null);
		//out按钮
		canvas.drawBitmap(bitmapOut, BUTTON_OUT_XOFFSET, BUTTON_OUT_YOFFSET, null);
		canvas.restore();
	}
	@Override
	public boolean onTouchEvent(MotionEvent e)
	{//发生触摸事件
		int x=(int) (e.getX());
		int y=(int) (e.getY());
		ScreenScaleResult ssr=FightagainstlandlordsActivity.ssr;
		int[] ca=ScreenScaleUtil.touchFromTargetToOrigin(x, y, ssr);
		x=ca[0];
		y=ca[1];
		System.out.println(x + " ... " + y);
		switch(e.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				if(x>BUTTON_START_XOFFSET&&x<BUTTON_START_WIDTH+BUTTON_START_XOFFSET
						&&y>BUTTON_START_YOFFSET&&y<BUTTON_START_YOFFSET+BUTTON_START_HEIGHT)
				{//对开始按钮的监听  点击可是按钮跳到IpView
					activity.playSound(2, 0);
					activity.gotoIpView();

				}
				if(x>BUTTON_HELP_XOFFSET&&x<BUTTON_HELP_XOFFSET+BUTTON_HELP_WIDTH
						&&y>BUTTON_HELP_YOFFSET&&y<BUTTON_HELP_YOFFSET+BUTTON_HELP_HEIGHT)
				{//对帮助按钮的监听
					activity.playSound(2, 0);
					activity.hd.sendEmptyMessage(6);

				}
				if(x>BUTTON_ABOUT_XOFFSET&&x<BUTTON_ABOUT_XOFFSET+BUTTON_ABOUT_WIDTH
						&&y>BUTTON_ABOUT_YOFFSET&&y<BUTTON_ABOUT_YOFFSET+BUTTON_ABOUT_HEIGHT)
				{//对关于按钮的监听
					activity.playSound(2, 0);
					activity.hd.sendEmptyMessage(7);

				}
				if(x>BUTTON_OUT_XOFFSET&&x<BUTTON_OUT_XOFFSET+BUTTON_OUT_WIDTH
						&&y>BUTTON_OUT_YOFFSET&&y<BUTTON_OUT_YOFFSET+BUTTON_OUT_HEIGHT)
				{//对关于按钮的监听
					activity.playSound(2, 0);
					System.exit(0);

				}

				break;
		}
		return true;
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height)
	{

	}
	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		this.repaint();

	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{

	}
	public void repaint()
	{
		SurfaceHolder holder=this.getHolder();
		Canvas canvas=holder.lockCanvas();
		try{
			synchronized(holder){
				onDraw(canvas);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(canvas!=null){
				holder.unlockCanvasAndPost(canvas);
			}
		}
	}
}