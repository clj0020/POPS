package com.madmensoftware.www.pops.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.madmensoftware.www.pops.R;
import com.orhanobut.logger.Logger;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddDetailsPopperFragment extends Fragment implements View.OnClickListener {

    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;

    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.name) EditText mNameEditText;
    @BindView(R.id.age) EditText mAgeEditText;
    @BindView(R.id.zip_code) EditText mZipCodeEditText;
    @BindView(R.id.organization_code) EditText mOrganizationCode;
    @BindView(R.id.goal) EditText mGoal;
    @BindView(R.id.parent_code) EditText mParentCode;
    @BindView(R.id.transportation_spinner) Spinner mTransportationSpinner;
    @BindView(R.id.radius_seekbar) DiscreteSeekBar mRadiusSeekbar;
    @BindView(R.id.nextBtn) Button mNextButton;
    @BindView(R.id.goal_due_date) Button mDateButton;

    public String transportationType;
    private int mYear, mMonth, mDay;
    public Date goalDate;
    private long goalDateLong;

    private AccessCodeChecker accessCodeChecker = new AccessCodeChecker();

    private SignUpPopperCallbacks mCallbacks;

    public interface SignUpPopperCallbacks {
        void onPopperSubmit(String name, int age, int zip_code, String transportation, int radius, double goal, long goalDateLong, int parentCode, int organizationCode);
    }

    public AddDetailsPopperFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
        if (activity instanceof SignUpPopperCallbacks) {
            mCallbacks = (SignUpPopperCallbacks) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SignUpPopperCallbacks) {
            mCallbacks = (SignUpPopperCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    public static AddDetailsPopperFragment newInstance() {
        AddDetailsPopperFragment fragment = new AddDetailsPopperFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_details_popper, container, false);
        ButterKnife.bind(this, view);

        Logger.d("onCreateView");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.transportation_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTransportationSpinner.setAdapter(adapter);

        mTransportationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                transportationType = mTransportationSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        mRadiusSeekbar.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
            @Override
            public int transform(int value) {
                return value;
            }

            @Override
            public String transformToString(int value) {
                return value + " miles";
            }

            @Override
            public boolean useStringTransform() {
                return true;
            }
        });

        mGoal.addTextChangedListener(new CurrencyTextWatcher(mGoal, "#,###"));

        mDateButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.nextBtn:
                if(isEmpty(mNameEditText) || isEmpty(mAgeEditText) || isEmpty(mZipCodeEditText) || isEmpty(mGoal)) {
                    Toast.makeText(getActivity(), "Please enter all fields!", Toast.LENGTH_LONG).show();
                }
                else if (mZipCodeEditText.getText().toString().length() != 5) {
                    Toast.makeText(getActivity(), "Zip code must be 5 digits", Toast.LENGTH_LONG).show();
                }
                else {
                    final String name = mNameEditText.getText().toString();
                    final int age = Integer.parseInt(mAgeEditText.getText().toString());
                    final int zip_code = Integer.parseInt(mZipCodeEditText.getText().toString());
                    final String transportation = transportationType;
                    final int radius = mRadiusSeekbar.getProgress();
                    int parentCode = Integer.parseInt(mParentCode.getText().toString());
                    int organizationCode = Integer.parseInt(mOrganizationCode.getText().toString());


                    double goal = convertDollarToDouble(mGoal.getText().toString());

                    if (radius == 0) {
                        Toast.makeText(getActivity(), "Please set your radius.", Toast.LENGTH_LONG).show();
                    }
                    else if (goalDateLong == 0) {
                        Toast.makeText(getActivity(), "Please choose a goal due date.", Toast.LENGTH_LONG).show();
                    }
                    else if (goal == 0) {
                        Toast.makeText(getActivity(), "Please enter a goal.", Toast.LENGTH_LONG).show();
                    }
                    else {
                        mCallbacks.onPopperSubmit(name, age, zip_code, transportation, radius, goal, goalDateLong, parentCode, organizationCode);
                    }
                }

                    Log.i("AddDetailsPopper", goalDateLong + "");
                break;
            case R.id.goal_due_date:
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                final  SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                goalDate = new Date(year - 1900, monthOfYear, dayOfMonth);
                                goalDateLong = goalDate.getTime();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;
            default:
                break;
        }

    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    public double convertDollarToDouble(String dollar) {
        double dbl = 0;
        NumberFormat nf = new DecimalFormat("$#,###.00");

        try {
            dbl = nf.parse(dollar).doubleValue();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        return dbl;
    }

    public String convertDoubleToDollar(double dbl) {
        String dollar = "";
        NumberFormat nf = new DecimalFormat("$#,###.00");
        return nf.format(dbl);
    }

    public class CurrencyTextWatcher implements TextWatcher {

        private final DecimalFormat df;
        private final DecimalFormat dfnd;
        private final EditText et;
        private boolean hasFractionalPart;
        private int trailingZeroCount;

        public CurrencyTextWatcher(EditText editText, String pattern) {
            df = new DecimalFormat(pattern);
            df.setDecimalSeparatorAlwaysShown(true);
            dfnd = new DecimalFormat("#,###.00");
            this.et = editText;
            hasFractionalPart = false;
        }

        @Override
        public void afterTextChanged(Editable s) {
            et.removeTextChangedListener(this);

            if (s != null && !s.toString().isEmpty()) {
                try {
                    int inilen, endlen;
                    inilen = et.getText().length();
                    String v = s.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "").replace("$","");
                    Number n = df.parse(v);
                    int cp = et.getSelectionStart();
                    if (hasFractionalPart) {
                        StringBuilder trailingZeros = new StringBuilder();
                        while (trailingZeroCount-- > 0)
                            trailingZeros.append('0');
                        et.setText(df.format(n) + trailingZeros.toString());
                    } else {
                        et.setText(dfnd.format(n));
                    }
                    et.setText("$".concat(et.getText().toString()));
                    endlen = et.getText().length();
                    int sel = (cp + (endlen - inilen));
                    if (sel > 0 && sel < et.getText().length()) {
                        et.setSelection(sel);
                    } else if (trailingZeroCount > -1) {
                        et.setSelection(et.getText().length() - 3);
                    } else {
                        et.setSelection(et.getText().length());
                    }
                } catch (NumberFormatException | ParseException e) {
                    e.printStackTrace();
                }
            }

            et.addTextChangedListener(this);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int index = s.toString().indexOf(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator()));
            trailingZeroCount = 0;
            if (index > -1) {
                for (index++; index < s.length(); index++) {
                    if (s.charAt(index) == '0')
                        trailingZeroCount++;
                    else {
                        trailingZeroCount = 0;
                    }
                }
                hasFractionalPart = true;
            } else {
                hasFractionalPart = false;
            }
        }
    }

    public class AccessCodeChecker {
        private DataSnapshot parentCodeMatch;
        private DataSnapshot organizationCodeMatch;

        public AccessCodeChecker() {

        }


        public DataSnapshot getParentCodeMatch() {
            return parentCodeMatch;
        }

        public void setParentCodeMatch(DataSnapshot parentCodeMatch) {
            this.parentCodeMatch = parentCodeMatch;
        }

        public DataSnapshot getOrganizationCodeMatch() {
            return organizationCodeMatch;
        }

        public void setOrganizationCodeMatch(DataSnapshot organizationCodeMatch) {
            this.organizationCodeMatch = organizationCodeMatch;
        }


    }
}

