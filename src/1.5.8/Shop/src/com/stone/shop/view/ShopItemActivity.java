package com.stone.shop.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobQuery.CachePolicy;
import cn.bmob.v3.listener.FindListener;

import com.stone.shop.R;
import com.stone.shop.adapter.GoodsListAdapter;
import com.stone.shop.adapter.ViewPagerAdapter;
import com.stone.shop.model.Good;
import com.stone.shop.model.Shop;
import com.stone.ui.ViewPagerCompat;

import e.V;

public class ShopItemActivity extends Activity implements OnClickListener {

	private static final String TAG = "ShopItemActivity";

	// ViewPager页
	private View view1, view2; // 需要滑动的页卡
	private ViewPagerCompat viewPager; // viewpager
	private ViewPagerAdapter shopViewPagerAdapter;
	// private PagerTitleStrip pagerTitleStrip; // viewpager的标题
	private PagerTabStrip pagerTabStrip; // 一个viewpager的指示器，效果就是一个横的粗的下划线
	private List<View> viewList; // 把需要滑动的页卡添加到这个list中
	private List<String> titleList; // viewpager的标题

	// 店铺商品列表
	private ListView lvGoodsList;
	private GoodsListAdapter goodsListAdapter;
	private Button btnBuyGood;
	
	//店铺简介页中的控件
	private TextView tvShopName; 	//店铺名
	private TextView tvShopInfo;	//店铺简介
	private TextView tvShopSale;	//店铺促销信息
	private TextView tvShopLoc; 	//店铺地理位置
	private TextView tvShopPhone;   //店铺电话
	private Button btnCommit;
	private EditText etCommit;
	private LinearLayout llCommitParent; // 评论父线性布局
	private LinearLayout llCommitSon; // 评论子线性布局
	private ImageView imgCall; //拨打电话 
	
	// UI测试数据
	private static List<Good> goodsList;
	
	//从上级页面中传入的数据
	private Shop shop ; //当期选择的Shop
	private String shopID; //当前选择的Shop的ID

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_item);
		
		//获取到从ShopAllActivity中传递过来的Shop对象
		shop = (Shop) getIntent().getSerializableExtra("shop");
		shopID = getIntent().getStringExtra("shopID");
		Log.i(TAG, "<<收到<<" + "shopID: "+shop.getObjectId()+" shopName: "+shop.getName());
		Log.i(TAG, "<<收到<<" + "shopID: "+ shopID +" shopName: "+shop.getName());

		initView();

		// 初始化商品页面以及适配数据
		initGoodsDate();
		initContentView();
		

	}

	public void initView() {

		viewPager = (ViewPagerCompat) findViewById(R.id.viewpager);
		// pagerTitleStrip = (PagerTitleStrip) findViewById(R.id.pagertitle);
		pagerTabStrip = (PagerTabStrip) findViewById(R.id.pagertab);
		pagerTabStrip.setTabIndicatorColor(Color.YELLOW);
		pagerTabStrip.setDrawFullUnderline(false);
		pagerTabStrip.setBackgroundColor(Color.WHITE);
		pagerTabStrip.setTextSpacing(50);

		// view1 = findViewById(R.layout.viewpager_menu);
		// view2 = findViewById(R.layout.viewpager_shopinfo);

		view1 = LayoutInflater.from(this).inflate(R.layout.viewpager_menu, null);
		view2 = LayoutInflater.from(this).inflate(R.layout.viewpager_shopinfo, null);
		
		viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
		viewList.add(view1);
		viewList.add(view2);

		titleList = new ArrayList<String>();// 每个页面的Title数据
		titleList.add("商品");
		titleList.add("店铺简介");
		shopViewPagerAdapter = new ViewPagerAdapter(viewList, titleList);

		viewPager.setAdapter(shopViewPagerAdapter);

	}

	/**
	 * 获取某一商店的所有商品
	 * @date 2014-5-1
	 * @autor Stone
	 */
	public void initGoodsDate() {
		goodsList = new ArrayList<Good>();
		goodsListAdapter = new GoodsListAdapter(this, goodsList);
		BmobQuery<Good> query = new BmobQuery<Good>();
		query.addWhereEqualTo("shopID", shopID);
		//query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);	// 先从缓存取数据，如果没有，再从网络取。
		query.setLimit(15);		// 限制最多15个结果
		query.findObjects(this, new FindListener<Good>() {
			
			@Override
			public void onSuccess(List<Good> goods) {
				//toast("查询商品成功, 共" + goods.size());
				if(goods.size()==0)
					toast("亲, 该店还没有添加商品哦");
				goodsList = goods;
				goodsListAdapter.refresh(goodsList);
				goodsListAdapter.notifyDataSetChanged();
			}
			
			@Override
			public void onError(String arg0) {
				toast("查询失败");
			}
		});
		
//		goodsList.add(new Good("原味石锅", "￥9.0"));
//		goodsList.add(new Good("酱香鸭石锅", "￥10.0"));
//		goodsList.add(new Good("鸡排石锅", "￥10.0"));
//		goodsList.add(new Good("牛肉石锅", "￥13.0"));
//		goodsList.add(new Good("香肠石锅", "￥11.0"));
		
	}

	public void initContentView() {
		// 商品列表页
		lvGoodsList = (ListView) view1.findViewById(R.id.lv_goods_list);
		lvGoodsList.setAdapter(goodsListAdapter);
		
		//店铺简介页
		tvShopName = (TextView) view2.findViewById(R.id.tv_shop_title);
		tvShopInfo = (TextView) view2.findViewById(R.id.tv_shop_introduce);
		tvShopSale = (TextView) view2.findViewById(R.id.tv_shop_promotion);
		tvShopLoc = (TextView) view2.findViewById(R.id.tv_shop_location);
		tvShopPhone = (TextView) view2.findViewById(R.id.tv_shop_phone);
		tvShopName.setText(shop.getName());  	//设置店铺名
		tvShopInfo.setText(shop.getInfo());		//设置店铺简介
		tvShopSale.setText(shop.getSale());		//设置店铺公告
		tvShopLoc.setText("位置：" + shop.getLocation());	//设置店铺位置
		tvShopPhone.setText("电话：" + shop.getPhone());	//设置店铺联系电话
		
		btnCommit = (Button) view2.findViewById(R.id.btn_commit);
		btnCommit.setOnClickListener(this);

		// 获取到评论的布局
		etCommit = (EditText) view2.findViewById(R.id.et_commit);
		llCommitParent = (LinearLayout) view2
				.findViewById(R.id.ll_commit_parent_view);
		llCommitSon = (LinearLayout) findViewById(R.id.ll_commit_son_view);
		
		imgCall = (ImageView) view2.findViewById(R.id.img_call);
		imgCall.setOnClickListener(this);

	}

	/**
	 * 添加一条评论
	 * @param user
	 * @param content
	 */
	public void insertCommit(String user, String content) {
		View view = LayoutInflater.from(this).inflate(R.layout.commit, null);
		TextView tvUser = (TextView) view.findViewById(R.id.tv_commit_user);
		TextView tvContent = (TextView) view
				.findViewById(R.id.tv_commit_content);
		tvUser.setText(user);
		tvContent.setText(content);
		llCommitParent.addView(view);
		tvUser = null;
		tvContent = null;
	}
	
	public void clickBuyGood(View v) {
		toast("亲， 记得在弹出的对话框中选择数量哦");
		Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
		lvGoodsList.startAnimation(shake);
	}


	public void toast(String toast) {
		Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_commit:
			if (etCommit.getText().toString().equals("")) {
				toast("亲，先写一句吧");
			} else {
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy年MM月dd日  HH:mm:ss ");
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
				String time = formatter.format(curDate);
				String content = etCommit.getText().toString()+" [ "+time+" ] ";
				insertCommit("admin" + ":", content);
				etCommit.setText("");
			}
			break;
			
		case R.id.img_call:
			toast("亲，店主好懒，木有留下电话，求别戳");
			break;

		default:
			break;
		}

	}

}
