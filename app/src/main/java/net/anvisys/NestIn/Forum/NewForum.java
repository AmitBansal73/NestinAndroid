package net.anvisys.NestIn.Forum;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.anvisys.NestIn.Common.ApplicationConstants;
import net.anvisys.NestIn.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Amit Bansal on 22-02-2016.
 */
public class NewForum extends DialogFragment {

    Context mContext;
    Spinner spinnerTopic;
    EditText threadText;
    Button btnForumPost;
    String selectedTopic;
    String textThread;
    ProgressBar prgBar;
    static String strResID;
    static String strSocietyName;

    static DialogFragment newInstance(String resident, String SocietyName) {

        strResID = resident;
        strSocietyName = SocietyName;
        DialogFragment info = new NewForum();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", 1);

        info.setArguments(args);
        //  info.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme);
        return info;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(350, 500);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity().getApplicationContext();
        View info = inflater.inflate(R.layout.new_forum_thread, container, false);

        spinnerTopic =  info.findViewById(R.id.forum_topic);
        prgBar = info.findViewById(R.id.progressBar);
        prgBar.setVisibility(View.GONE);
        ArrayAdapter<CharSequence> adapterTopic = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                R.array.forumTopic, R.layout.my_spinner_text);
        spinnerTopic.setAdapter(adapterTopic);
        spinnerTopic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTopic = (String)spinnerTopic.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        } );
        threadText =  info.findViewById(R.id.txtForumThread);
        btnForumPost =  info.findViewById(R.id.btnPostThread);
        btnForumPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textThread = threadText.getText().toString();
                AddNewPost();
            }
        });
        return info;
    }

    private void   AddNewPost()
    {
        prgBar.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL+"/api/forum/NewForum";
        String reqBody = "{\"resID\":\""+ strResID +"\",\"Topic\":\""+ selectedTopic +"\",\"SocietyId\":\""+ strResID + "\",\"CurrentThread\":\""+ textThread + "\"}";
        try {
            JSONObject jsRequest = new JSONObject(reqBody);
            //-------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url,jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jObj) {

                    Toast.makeText(getActivity().getApplicationContext(), "Post Submitted Successfully.",
                            Toast.LENGTH_SHORT).show();
                    prgBar.setVisibility(View.GONE);
                    int REQUEST_CODE = 0;
                    Intent intent = new Intent();
                    intent.putExtra("Topic",selectedTopic);
                    intent.putExtra("CurrentThread",textThread);
                    ForumActivity forumActivity = (ForumActivity) getActivity();
                    forumActivity.onActivityResult(getTargetRequestCode(), REQUEST_CODE, intent);
                    getTargetFragment().onActivityResult(getTargetRequestCode(), REQUEST_CODE, intent);
                    NewForum.this.dismiss();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    prgBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity().getApplicationContext(), "Post could not be submitted : Try Again",
                            Toast.LENGTH_LONG).show();
                }
            });

            RetryPolicy policy = new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 8000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 3;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            };

            jsArrayRequest.setRetryPolicy(policy);
            queue.add(jsArrayRequest);

            //*******************************************************************************************************
        }
        catch (JSONException js)
        {
            prgBar.setVisibility(View.GONE);

        }

        finally {

        }


    }


}