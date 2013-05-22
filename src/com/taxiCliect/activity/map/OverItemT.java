package com.taxiCliect.activity.map;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.OverlayItem;
import com.baidu.mapapi.Projection;

/**
 *  ͼ�ϸ�㻭l��
 * @author zeng
 *
 */
public class OverItemT extends ItemizedOverlay<OverlayItem> {
	protected List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
	protected Drawable marker;
	protected Drawable roughMarker;
	protected Context mContext;
	protected MapView mapView;
	private ArrayList<GeoPoint> geoPoints;
	private float[] pts;

	public OverItemT(Context context, Drawable marker, ArrayList<GeoPoint> point) {
		// TODO Auto-generated constructor stub
		super(boundCenter(marker));
		this.mContext = context;
		this.marker = marker;
		this.geoPoints = point;
		for (int i = 0; i < point.size(); i++) {
			mGeoList.add(new OverlayItem(point.get(i), "", ""));
		}
		
		pts = new float[point.size() * 2];
		populate();
	}

	private ArrayList<Point> pt;

	public void draw(Canvas canvas, MapView mapView, boolean arg2) {
		super.draw(canvas, mapView, arg2);
		Projection projection = mapView.getProjection();
		pt = new ArrayList<Point>();
		for (int index = 0; index < size(); index++) {
			OverlayItem overLayItem = getItem(index);
			String title = overLayItem.getTitle();
			Point point = projection.toPixels(overLayItem.getPoint(), null);
			Paint paintText = new Paint();
			paintText.setColor(Color.BLUE);
			float width = paintText.measureText(title);
			canvas.drawText(title, point.x - width / 2, point.y + 30, paintText);
			// for( int k = 0; k<size(); k++){
			// projection.toPixels(, arg1)
			// }
			// for(int j = 0; j<size()-1;j++){
			// canvas.drawLines(pts, paint)
			// canvas.drawLines(pts, offset, count, paint)
			// }

			// projection.toPixels(geoPoints.get(index), pt.get(index));
				pt.add(point);
		}
		int pts_index = 0;
			for (int i = 0; i < pt.size(); i++) {
				pts[pts_index] = pt.get(i).x;
				pts_index++;
				pts[pts_index] = pt.get(i).y;
				pts_index++;
			}
		Paint linePaint = new Paint();
//		canvas.drawLines(pts, linePaint);
		for( int i = 0; i<pt.size();i++){
			if(i+1< pt.size())
			drawLine(canvas, pt.get(i), pt.get(i+1), linePaint);
		}
	}
	private void drawLine(Canvas canvas,Point p1,Point p2,Paint p){
		canvas.drawLine(p1.x, p1.y, p2.x, p2.y, p);
	}
	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return mGeoList.get(i);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return mGeoList.size();
	}
}
