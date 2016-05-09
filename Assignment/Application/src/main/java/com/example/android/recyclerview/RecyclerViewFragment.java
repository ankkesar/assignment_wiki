package com.example.android.recyclerview;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.android.recyclerview.modals.UserSearch;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Searches wiki for images
 */
public class RecyclerViewFragment extends Fragment {

    private static final String TAG = "RecyclerViewFragment";
    private static final String SEARCH_URL = "https://en.wikipedia.org/w/api.php?action=query&prop=pageimages&format=json&piprop=thumbnail&pithumbsize=50&pilimit=50&generator=prefixsearch&gpssearch=";
    protected RecyclerView mRecyclerView;
    protected CustomAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    private String searchUrl;
    private String queryText;
    EditText searchEt;
    private SearchHandler mSearchHandler;
    AtomicInteger mSearchCounter;
    private String prev = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view_frag, container, false);
        rootView.setTag(TAG);
        mSearchCounter = new AtomicInteger();
        mSearchHandler = new SearchHandler();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);
        searchEt = (EditText) rootView.findViewById(R.id.search_et);
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                queryText = editable.toString().trim().toLowerCase();
                if (!prev.equals(queryText)) {
                    if (!TextUtils.isEmpty(queryText)) {
                        mSearchHandler.removeMessages(1);
                        mSearchHandler.sendEmptyMessageDelayed(1, 600);
                        prev = queryText;
                    } else {
                        mAdapter.setResult(new ArrayList<UserSearch>());
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        return rootView;
    }

    class FetchAsync extends AsyncTask<String, Void, ArrayList<UserSearch>> {

        @Override
        protected ArrayList<UserSearch> doInBackground(String... query) {
            searchUrl = SEARCH_URL +query[0];
                                try {
                if (Integer.parseInt(query[1]) == mSearchCounter.get())
                    return grabContent(searchUrl);
                else
                    return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<UserSearch> result) {
            super.onPostExecute(result);
            if (result != null) {
                if (mAdapter == null) {
                    mAdapter = new CustomAdapter(result);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    mAdapter.setResult(result);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }
// initiate request
    private ArrayList<UserSearch> grabContent(String searchUrl) throws IOException {

        // Connect to the URL using java's native library
        URL url = new URL(searchUrl);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();

        // Convert to a JSON object
        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
        ArrayList<UserSearch> imageList = null;
        if (root.getAsJsonObject()!=null){
        if (root.getAsJsonObject().getAsJsonObject("query") != null) {
            JsonObject sourceObj = root.getAsJsonObject().getAsJsonObject("query").getAsJsonObject("pages");
            System.out.println("RESPONSE:" + sourceObj.toString());
            Set<Map.Entry<String, JsonElement>> entrySet = sourceObj.entrySet();
            if (entrySet != null && entrySet.size() > 0) {
                imageList = new ArrayList<UserSearch>(entrySet.size());
                for (Map.Entry<String, JsonElement> jsonObj : entrySet) {
                    UserSearch userSearch = new UserSearch();
                    JsonObject obj = jsonObj.getValue().getAsJsonObject();

                    System.out.println(jsonObj.getValue().getAsJsonObject().get("thumbnail"));
                    userSearch.setTitle(obj.get("title").getAsString());
                    if (obj.getAsJsonObject("thumbnail") != null)
                        userSearch.setSource(obj.getAsJsonObject("thumbnail").get("source").getAsString());
                    else {
                        userSearch.setSource(null);
                        continue;
                    }
                    userSearch.setWidth(obj.getAsJsonObject("thumbnail").get("width").getAsString());
                    userSearch.setHeight(obj.getAsJsonObject("thumbnail").get("height").getAsString());
                    imageList.add(userSearch);
                }
            }
        }
        }
        return imageList;
    }
//Accumulates request for 600ms to avoid frequent changes
    class SearchHandler extends android.os.Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            new FetchAsync().execute(queryText, Integer.toString(mSearchCounter.incrementAndGet()));

        }
    }
}
