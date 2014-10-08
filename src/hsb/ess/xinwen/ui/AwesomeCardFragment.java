/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hsb.ess.xinwen.ui;

import hsb.ess.fragment.helper.ScrollTabHolderFragment;
import hsb.ess.xinwen.adapter.NewsAdapter;
import hsb.ess.xinwen.entity.RSSFeed;
import hsb.ess.xinwen.parser.RSSItem;
import hsb.ess.xinwen.parser.RSSParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.astuetz.viewpager.extensions.sample.R;

public class AwesomeCardFragment extends ScrollTabHolderFragment implements
		OnScrollListener {

	private static final String ARG_POSITION = "position";

	List<String> LIST_TITLE = new ArrayList<String>();
	List<String> LIST_LINK = new ArrayList<String>();
	List<String> LIST_DESRIPTION = new ArrayList<String>();
	List<String> LIST_PUB_DATE = new ArrayList<String>();

	NewsAdapter adapter;
	ArrayList<HashMap<String, String>> rssItemList = new ArrayList<HashMap<String, String>>();

	RSSParser rssParser = new RSSParser();

	// button add new website
	// ImageButton btnAddSite;

	List<RSSItem> rssItems = new ArrayList<RSSItem>();

	RSSFeed rssFeed;

	private static String TAG_TITLE = "title";
	private static String TAG_LINK = "link";
	private static String TAG_DESRIPTION = "description";
	private static String TAG_PUB_DATE = "pubDate";
	private static String TAG_GUID = "guid";

	// private int position;
	ListView mListView;
	private int mPosition;

	// Button productButton;
	// static String ARG_POSITION = "position";
	private int position;

	private ArrayList<String> mListItems = new ArrayList<String>();

	// LinearLayout rl;

	public static AwesomeCardFragment newInstance(int position) {
		AwesomeCardFragment f = new AwesomeCardFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPosition = getArguments().getInt(ARG_POSITION);
		position = getArguments().getInt(ARG_POSITION);
		for (int i = 1; i <= 100; i++) {
			mListItems.add(i + ". Quinion " + (mPosition + 1));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// inflater.inflate(R.layout.activity_slide_main, container, false);

		View v = inflater.inflate(R.layout.fragment_list, null);
		final int margin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 8, getResources()
						.getDisplayMetrics());

		mListView = (ListView) v.findViewById(R.id.listView);

		View placeHolderView = inflater.inflate(
				R.layout.view_header_placeholder, mListView, false);

		mListView.addHeaderView(placeHolderView);

		String rss_link = "http://news.google.com/?output=rss";
		// String rss_link = "http://www.thehindu.com/?service=rss";

		new loadRSSFeedItems().execute(rss_link);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent in = new Intent(getActivity(),
						DisPlayWebPageActivity.class);

				// getting page url
				// String page_url = ((TextView)
				// view.findViewById(R.id.page_url))
				// .getText().toString();
				in.putExtra("page_url", LIST_LINK.get(position));
				startActivity(in);
			}
		});

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mListView.setOnScrollListener(this);
		mListView.setAdapter(new ArrayAdapter<String>(getActivity(),
				R.layout.list_item, android.R.id.text1, mListItems));
	}

	@Override
	public void adjustScroll(int scrollHeight) {
		// TODO Auto-generated method stub
		if (scrollHeight == 0 && mListView.getFirstVisiblePosition() >= 1) {
			return;
		}

		mListView.setSelectionFromTop(1, scrollHeight);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		if (mScrollTabHolder != null)
			mScrollTabHolder.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount, mPosition);

	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	/**
	 * Background Async Task to get RSS Feed Items data from URL
	 * */
	class loadRSSFeedItems extends AsyncTask<String, String, String> {

		ProgressDialog pDialog;

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting all recent articles and showing them in listview
		 * */
		@Override
		protected String doInBackground(String... args) {
			// rss link url
			String rss_url = args[0];

			// list of rss items
			rssItems = rssParser.getRSSFeedItems(rss_url);
			int newscounter = 0;
			// looping through each item
			for (RSSItem item : rssItems) {

				// creating new HashMap
				HashMap<String, String> map = new HashMap<String, String>();

				// adding each child node to HashMap key => value
				map.put(TAG_TITLE, item.getTitle());
				map.put(TAG_LINK, item.getLink());
				map.put(TAG_PUB_DATE, item.getPubdate()); // If you want parse
															// the date
				// String description = item.getDescription();
				String htmlpars = item.getDescription();
				// taking only 200 chars from description
				// if (description.length() > 100) {
				// description = description.substring(0, 97) + "..";
				// }
				// String abc = ""+Html.fromHtml(description);

				// Parsing Data

				String newsDescription = "";
				Document doc = Jsoup.parse(htmlpars, "", Parser.xmlParser());
				Elements tr1 = doc.select("td.j");// j is class, td is tag.
				for (Element element : tr1) {
					// will get description
					newsDescription = element.text();
					// System.out.println(element.previousElementSibling().text()
					// + ": " + element.text());
					if (newsDescription.length() > 100) {
						newsDescription = newsDescription.substring(0, 97)
								+ "..";
					}

				}

				map.put(TAG_DESRIPTION, newsDescription);

				// Elements metatbody = metaElems.select("tbody");
				// Elements tr = metatbody.select("tr");
				//
				// Log.i("Awesome", "tr" + tr);
				// Elements tr1 = metatbody.select("tr");
				// Log.i("Awesome", "tr1" + tr1);
				// String name = tr1.attr("b");
				// Log.i("Awesome", "trString" + name);
				// String name = metaElems.attr("b");
				// Log.i("Awesome", "name" + name);

				// String mString = description;

				// adding HashList to ArrayList
				rssItemList.add(map);
				LIST_TITLE.add(item.getTitle());
				LIST_DESRIPTION.add(newsDescription);
				LIST_PUB_DATE.add(item.getPubdate());
				LIST_LINK.add(item.getLink());

			}

			// updating UI from Background Thread
			// getActivity().runOnUiThread(new Runnable() {
			// public void run() {
			// /**
			// * Updating parsed items into listview
			// * */
			// Spanned a=Html.fromHtml(TAG_DESRIPTION);
			// ListAdapter adapter = new SimpleAdapter(getActivity(),
			// rssItemList, R.layout.rss_item_list_row,
			// new String[] { TAG_LINK, TAG_TITLE, TAG_PUB_DATE,
			// TAG_DESRIPTION}, new int[] {
			// R.id.page_url, R.id.title, R.id.pub_date,
			// R.id.link });
			//
			// // updating listview
			// rssList.setAdapter(adapter);
			// }
			// });
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String args) {
			// dismiss the dialog after getting all products
			adapter = new NewsAdapter(getActivity(), LIST_TITLE, LIST_LINK,
					LIST_DESRIPTION, LIST_PUB_DATE);
			mListView.setAdapter(adapter);
			pDialog.dismiss();
		}
	}

}