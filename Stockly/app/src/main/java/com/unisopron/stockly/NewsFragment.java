package com.unisopron.stockly;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment {

    private static final String TAG = "NewsFragment";

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<RssFeedModel> mFeedModelList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        try {

            recyclerView = view.findViewById(R.id.recyclerView);
            swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

            recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

            // automatically start fetching
            new FetchFeedTask().execute((Void) null);

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new FetchFeedTask().execute((Void) null);
                }
            });

        } catch (Exception e) {
            Toast.makeText(view.getContext(), "Something happened - try connecting to the Internet!", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    // parse rss
    public List<RssFeedModel> parseFeed(InputStream inputStream) throws XmlPullParserException, IOException {
        String title = null;
        String link = null;

        boolean isItem = false;
        List<RssFeedModel> items = new ArrayList<>();

        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);

            xmlPullParser.nextTag();
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();
                if(name == null)
                    continue;

                /*if(eventType == XmlPullParser.END_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }*/

                if (
                        eventType == XmlPullParser.START_TAG
                                && name.equalsIgnoreCase("item")
                ) { isItem = true; }

                if (
                        eventType == XmlPullParser.END_TAG
                                && name.equalsIgnoreCase("item")
                ) { isItem = true; }

                Log.d(TAG, "Parsing name ==> " + name);
                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                if (name.equalsIgnoreCase("title")) {
                    title = result;
                } else if (name.equalsIgnoreCase("link")) {
                    link = result;
                }

                if (title != null && link != null) {
                    if(isItem) {
                        RssFeedModel item = new RssFeedModel(title, link);
                        items.add(item);
                    }

                    title = null;
                    link = null;
                    isItem = false;
                }
            }

            return items;
        } finally {
            inputStream.close();
        }
    }

    // feed logic
    private class FetchFeedTask extends AsyncTask<Void, Void, Boolean> {

        private String urlLink;

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);

            urlLink = "http://feeds.marketwatch.com/marketwatch/topstories/";
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            if (TextUtils.isEmpty(urlLink))
                return false;

            try {
                if(!urlLink.startsWith("http://") && !urlLink.startsWith("https://"))
                    urlLink = "http://" + urlLink;

                URL url = new URL(urlLink);
                InputStream inputStream = url.openConnection().getInputStream();
                mFeedModelList = parseFeed(inputStream);
                return true;
            } catch (Exception e) {
                Log.e(TAG, "Error", e);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            swipeRefreshLayout.setRefreshing(false);

            if (success) {
                // Fill RecyclerView
                recyclerView.setAdapter(new RssFeedListAdapter(mFeedModelList));
            } else {
                Log.d("onPostExecute", "Something happened...");
            }
        }
    }
}


