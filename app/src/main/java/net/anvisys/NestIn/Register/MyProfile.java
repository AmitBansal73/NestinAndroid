package net.anvisys.NestIn.Register;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import net.anvisys.NestIn.Common.DataAccess;
import net.anvisys.NestIn.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyProfile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyProfile extends android.support.v4.app.DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private DataAccess _databaseAccess;
    private static Context mContext;
    EditText firstName, middleName, lastName, mobileNumber, societyName, password, userLogin, eMail;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
       * @return A new instance of fragment MyProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static MyProfile newInstance( Context context) {
        mContext = context;
        MyProfile fragment = new MyProfile();
        Bundle args = new Bundle();
       args.putString(ARG_PARAM1, "Test");
       // args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MyProfile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
       // dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
      //  dialog.getWindow().setLayout(350, 500);
      return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);

        firstName = (EditText)view.findViewById(R.id.editFirstName);
        middleName = (EditText)view.findViewById(R.id.editMiddleName);
        lastName = (EditText)view.findViewById(R.id.editLastName);
        mobileNumber = (EditText)view.findViewById(R.id.editMobileNo);
        societyName = (EditText)view.findViewById(R.id.editSocietyName);
       // password = (EditText)view.findViewById(R.id.editUserPassword);
        userLogin = (EditText)view.findViewById(R.id.editUserLogin);
        eMail = (EditText)view.findViewById(R.id.editEmail);


        _databaseAccess = new DataAccess(mContext);
        _databaseAccess.open();
        Cursor c = _databaseAccess.fetchMember();
        if (c.getCount() > 0) {
            try {
                if (c != null) {
                    int indexFirstName = c.getColumnIndex("firstname");
                    int indexLastName = c.getColumnIndex("lastname");
                    int indexEmail = c.getColumnIndex("email");
                    int indexSocietyName = c.getColumnIndex("societyName");
                    int indexLogin = c.getColumnIndex("userLogin");
                    int indexMobileNo = c.getColumnIndex("mobile_no");
                    int indexPassword = c.getColumnIndex("password");

                    c.moveToFirst();
                    do {
                        firstName.setText(c.getString(indexFirstName));
                        middleName.setText("");
                        lastName.setText(c.getString(indexLastName));
                        societyName.setText(c.getString(indexSocietyName));
                        mobileNumber.setText(c.getString(indexMobileNo));
                     //   password.setText(c.getString(indexPassword));
                        eMail.setText(c.getString(indexEmail));
                        userLogin.setText(c.getString(indexLogin));
                    } while (c.moveToNext());


                }
            } catch (Exception ex) {

            }
        }

        return view;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
            mListener.onFragmentInteraction();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction();
    }


}
