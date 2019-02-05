package net.anvisys.NestIn.Poll;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import net.anvisys.NestIn.Object.Polling;
import net.anvisys.NestIn.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScreenSlidePageFragment extends Fragment {

    public static final String ARG_PAGE = "page";
    private int mPageNumber;
    public static Polling pollData;
    TextView pollQuestion;
    RadioButton btnAnswer1;
    RadioButton btnAnswer2;
    RadioButton btnAnswer3;
    RadioButton btnAnswer4;
    PieChart pieChart;


    public ScreenSlidePageFragment() {
        // Required empty public constructor
    }

    public static ScreenSlidePageFragment create(int pageNumber, Polling poll) {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        pollData = poll;
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {    // Inflate the layout containing a title and body text.
            ViewGroup rootView = (ViewGroup) inflater
                    .inflate(R.layout.fragment_screen_slide_page_1, container, false);

            pollQuestion = (TextView) rootView.findViewById(R.id.txtQuestion);
            btnAnswer1 = (RadioButton) rootView.findViewById(R.id.rbOption1);
            btnAnswer2 = (RadioButton) rootView.findViewById(R.id.rbOption2);
            btnAnswer3 = (RadioButton) rootView.findViewById(R.id.rbOption3);
            btnAnswer4 = (RadioButton) rootView.findViewById(R.id.rbOption4);
            // Set the title view to show the page number.
            pollQuestion.setText(pollData.Question);
            btnAnswer1.setText(pollData.Answer1);
            btnAnswer2.setText(pollData.Answer2);
            btnAnswer3.setText(pollData.Answer3);
            btnAnswer4.setText(pollData.Answer4);

            pieChart = (PieChart) rootView.findViewById(R.id.chart);

            // creating data values
            ArrayList<Entry> entries = new ArrayList<>();
            entries.add(new Entry((float) pollData.Answer1Count, 0));
            entries.add(new Entry((float) pollData.Answer2Count, 1));
            entries.add(new Entry((float) pollData.Answer3Count, 2));
            entries.add(new Entry((float) pollData.Answer4Count, 2));

            PieDataSet dataset = new PieDataSet(entries, "# of votes");

            // creating labels
            ArrayList<String> labels = new ArrayList<String>();
            labels.add(pollData.Answer1);
            labels.add(pollData.Answer2);
            labels.add(pollData.Answer3);
            dataset.setColors(ColorTemplate.COLORFUL_COLORS); // set the color
            PieData data = new PieData(labels, dataset); // initialize Piedata
            pieChart.setData(data);
            return rootView;
        }
        catch ( Exception ex)
        {
            return null;
        }
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }

}
