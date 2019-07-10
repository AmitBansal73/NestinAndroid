package net.anvisys.NestIn.Poll;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import net.anvisys.NestIn.Common.ApplicationConstants;
import net.anvisys.NestIn.Common.ApplicationVariable;
import net.anvisys.NestIn.Common.DataAccess;
import net.anvisys.NestIn.Common.Profile;
import net.anvisys.NestIn.Common.Session;
import net.anvisys.NestIn.Common.SocietyUser;
import net.anvisys.NestIn.Common.Utility;
import net.anvisys.NestIn.Model.Polling;
import net.anvisys.NestIn.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class OpinionActivityFragment extends Fragment {

    public static final String ARG_PAGE = "page";
    private int mPageNumber;
    private  String mQuestion;
    private  Polling mPoll;
    public static Polling pollData;
    public static int size;

    public static String   strResID="";
    public static String   strSocietyName="";
    TextView pollQuestion,lblEndDate;
    RadioButton btnAnswer1,btnAnswer2,btnAnswer3,btnAnswer4;
    PieChart pieChart;
    Profile myProfile;
    Button SubmitVote;
    RadioGroup opinionRadioGroup;
    ProgressBar prgBar;
    static PollUpdateListener pollListener;
    SocietyUser socUser;

    public OpinionActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup fragmentView = (ViewGroup) inflater.inflate(R.layout.fragment_opinion, container, false);

        try {
        prgBar=fragmentView.findViewById(R.id.progressBar);
        prgBar.setVisibility(View.GONE);

           //getArguments();
            pollQuestion =  fragmentView.findViewById(R.id.opinionQuestion);
            lblEndDate =  fragmentView.findViewById(R.id.lblEndDate);
            btnAnswer1 =  fragmentView.findViewById(R.id.rbAns1);
            btnAnswer2 =  fragmentView.findViewById(R.id.rbAns2);
            btnAnswer3 =  fragmentView.findViewById(R.id.rbAns3);
            btnAnswer4 =  fragmentView.findViewById(R.id.rbAns4);
            SubmitVote = fragmentView.findViewById(R.id.btnSubmitVote);
            opinionRadioGroup = fragmentView.findViewById(R.id.opinionRadioGroup);

            SubmitVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int selectedID = opinionRadioGroup.getCheckedRadioButtonId();

                    int selectedAnswer = -1;

                    switch (selectedID) {
                        case R.id.rbAns1: {
                            selectedAnswer = 1;
                            break;
                        }
                        case R.id.rbAns2: {
                            selectedAnswer = 2;
                            break;
                        }
                        case R.id.rbAns3: {
                            selectedAnswer = 3;
                            break;
                        }
                        case R.id.rbAns4: {
                            selectedAnswer = 4;
                            break;
                        }
                        default: {
                            selectedAnswer = -1;
                            break;
                        }
                    }

                    int id = mPoll.PollID;

                    SubmitVote(id, selectedAnswer, mPoll.previousSelected);

                }
            });

            // Set the title view to show the page number.
            pollQuestion.setText(mPoll.Question);
            Date Poll_End_Date = Utility.DBStringToLocalDate(pollData.End_Date);
            lblEndDate.setText("Vote Before: "+ Utility.DateToDisplayDateTime(Poll_End_Date));
            btnAnswer1.setText(mPoll.Answer1);
            btnAnswer2.setText(mPoll.Answer2);


            pieChart =  fragmentView.findViewById(R.id.opinionChart);
            // creating data values
            ArrayList<Entry> entries = new ArrayList<>();
            entries.add(new Entry((float) mPoll.Answer1Count, 0));
            entries.add(new Entry((float) mPoll.Answer2Count, 1));

            if (mPoll.Answer3 == null||mPoll.Answer3.matches("")) {
                btnAnswer3.setVisibility(View.INVISIBLE);
            } else {
                btnAnswer3.setText(mPoll.Answer3);
                entries.add(new Entry((float) mPoll.Answer3Count, 2));
            }

            if (mPoll.Answer4 == null||mPoll.Answer4.matches("")) {
                btnAnswer4.setVisibility(View.INVISIBLE);
            } else {
                btnAnswer4.setText(mPoll.Answer4);
                entries.add(new Entry((float) mPoll.Answer4Count, 3));
            }

            PieDataSet dataset = new PieDataSet(entries, "# of votes");

            // creating labels
            ArrayList<String> labels = new ArrayList<String>();
            labels.add(mPoll.Answer1);
            labels.add(mPoll.Answer2);

            if (!(mPoll.Answer3 == null) && !mPoll.Answer3.matches("") ) {
                labels.add(mPoll.Answer3);
            }
            if (!(mPoll.Answer4 == null) && !mPoll.Answer4.matches("")) {
                labels.add(mPoll.Answer4);
            }

            dataset.setColors(ColorTemplate.COLORFUL_COLORS); // set the color
            PieData data = new PieData(labels, dataset); // initialize Piedata
            pieChart.setData(data);

            if(mPoll.previousSelected!=0)
            {
                int selectedID=0;
                switch (mPoll.previousSelected)
                {
                    case 1:
                    {
                        selectedID=R.id.rbAns1;
                        break;
                    }
                    case 2:
                    {
                        selectedID=R.id.rbAns2;
                        break;
                    }
                    case 3:
                    {
                        selectedID=R.id.rbAns3;
                        break;
                    }
                    case 4:
                    {
                        selectedID=R.id.rbAns4;
                        break;
                    }
                    default:
                    {
                        selectedID=0;
                        break;
                    }
                }
                opinionRadioGroup.check(selectedID);

                SubmitVote.setVisibility(View.INVISIBLE);
            }
            if(Utility.isPrevious( Utility.GetDateOnly(mPoll.End_Date)))
            {
                opinionRadioGroup.setEnabled(false);

            }
            else {
                opinionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        if (!Utility.IsConnected(getContext())) {
                            Toast.makeText(getContext(), "Working Offline", Toast.LENGTH_LONG);
                        }

                        if (mPoll.previousSelected == 0) {

                        } else {
                            SubmitVote.setVisibility(View.VISIBLE);
                            SubmitVote.setText("Change Vote");
                        }

                    }
                });
            }
            TextView lblPage =  fragmentView.findViewById(R.id.lblPage);

            int TotalPage;
            if(ApplicationConstants.TOTAL_POLL_COUNT==0)
            {
                TotalPage =  size;
            }
            else
            {
                TotalPage = ApplicationConstants.TOTAL_POLL_COUNT;
            }
            lblPage.setText("Poll " + Integer.toString(mPageNumber+1) + " Of " + TotalPage);
        }
        catch (Exception ex)
        {
            Toast.makeText(getContext(),"Error in Creating Pie Chart", Toast.LENGTH_LONG).show();
        }

       // UpdateAnswerCount(pollData.PollID);
         return fragmentView;
    }

    static OpinionActivityFragment newInstance(int position, Polling poll, int count){
        try {

            OpinionActivityFragment swipeFragment = new OpinionActivityFragment();
            pollData = poll;
            size = count;
            Bundle args = new Bundle();
            args.putInt(ARG_PAGE, position);
            Polling TempPoll = ApplicationConstants.pollList.get(position);
            args.putSerializable("poll", TempPoll);
            swipeFragment.setArguments(args);
            return swipeFragment;
        }
        catch (Exception ex)
        {
            return  null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
       // mQuestion = getArguments().getString("poll");
        mPoll = (Polling) getArguments().getSerializable("poll");
    }

    public int getPageNumber() {
        return mPageNumber;
    }

    private void   SubmitVote(int ID,final int selectedVote, int previousSelected)
    {
        socUser = Session.GetCurrentSocietyUser(getContext());


        prgBar.setVisibility(View.VISIBLE);
        String url = ApplicationConstants.APP_SERVER_URL+ "/api/Poll";
        String reqBody = "{\"resID\":\""+ socUser.ResID +"\",\"PollID\":\""+ ID + "\",\"selectedAnswer\":"+ selectedVote + ",\"previousSelected\":"
                + previousSelected + "}";
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
                    switch (pollData.previousSelected)
                    {
                        case 1:
                        {
                            pollData.Answer1Count = pollData.Answer1Count-1;
                            break;
                        }
                        case 2:
                        {
                            pollData.Answer2Count = pollData.Answer2Count-1;
                            break;
                        }
                        case 3:
                        {
                            pollData.Answer3Count = pollData.Answer3Count-1;
                            break;
                        }
                        case 4:
                        {
                            pollData.Answer4Count = pollData.Answer4Count-1;
                            break;
                        }
                    }
                    switch (selectedVote)
                    {
                        case 1:
                        {
                            pollData.Answer1Count = pollData.Answer1Count+1;
                            break;
                        }
                        case 2:
                        {
                            pollData.Answer2Count = pollData.Answer2Count+1;
                            break;
                        }
                        case 3:
                        {
                            pollData.Answer3Count = pollData.Answer3Count+1;
                            break;
                        }
                        case 4:
                        {
                            pollData.Answer4Count = pollData.Answer4Count+1;
                            break;
                        }
                    }
                    pollData.previousSelected = selectedVote;
                    DataAccess da = new DataAccess(getContext());
                    da.open();
                    if(da.checkPollExist(pollData.PollID))
                    {da.updatePollData(pollData);}
                    da.close();
                    pollListener.OnPollUpdate(mPageNumber);
                    pollListener.OnPollUpdate(mPageNumber);


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    prgBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity().getApplicationContext(), "Post could not be submitted : Try Again",
                            Toast.LENGTH_LONG).show();
                }
            });

            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
            jsArrayRequest.setRetryPolicy(rPolicy);
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

    public interface PollUpdateListener
    {
      void  OnPollUpdate(int PageNumber);
    }

    public static void RegisterPollChangeListener(PollUpdateListener listener)
    {
       pollListener = listener;
    }

    public void UpdateAnswerCount(final int PollID)
    {
        try {
            prgBar.setVisibility(View.VISIBLE);

            strSocietyName = socUser.SocietyName;

            String url = ApplicationConstants.APP_SERVER_URL + "/api/Poll/GetAnswer/1";

            String reqBody = "{\"PollID\":\""+ PollID+ "\",\"ResID\":\""+ ApplicationVariable.Res_Id +"\"}";
            JSONObject jsRequest=null;

            try {
                jsRequest = new JSONObject(reqBody);
            }
            catch (JSONException jex)
            {}


            //-------------------------------------------------------------------------------------------------
            RequestQueue queue = Volley.newRequestQueue(getContext());
            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, url,jsRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jObj) {

                    try {
                            DataAccess da = new DataAccess(getContext());
                            da.open();
                            Polling tempPoll = new Polling();
                            tempPoll.PollID = jObj.getInt("PollID");
                            tempPoll.Answer1Count = jObj.getInt("Answer1Count");

                            tempPoll.Answer2Count = jObj.getInt("Answer2Count");

                            tempPoll.Answer3Count = jObj.getInt("Answer3Count");

                            tempPoll.Answer4Count = jObj.getInt("Answer4Count");
                            tempPoll.previousSelected = jObj.getInt("previousSelected");
                        if(tempPoll.previousSelected == pollData.previousSelected &&
                                tempPoll.Answer1Count == pollData.Answer1Count      )
                        {

                        }
                        else
                        {
                            if(da.checkPollExist(tempPoll.PollID))
                            {da.updatePollData(tempPoll);}
                             pollData = tempPoll;
                            pollListener.OnPollUpdate(mPageNumber);
                        }


                        da.close();
                        prgBar.setVisibility(View.GONE);


                    } catch (JSONException e) {
                        prgBar.setVisibility(View.GONE);
                    }
                    catch (Exception ex)
                    {
                        int a=1;
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    String message = error.toString();
                    prgBar.setVisibility(View.GONE);
                }
            });

            RetryPolicy rPolicy = new DefaultRetryPolicy(0,-1,0);
            jsArrayRequest.setRetryPolicy(rPolicy);
            queue.add(jsArrayRequest);

            //*******************************************************************************************************
        }
        catch (Exception ex)
        {
            Toast.makeText(getContext(),"Server Error", Toast.LENGTH_LONG).show();
        }
    }
}
