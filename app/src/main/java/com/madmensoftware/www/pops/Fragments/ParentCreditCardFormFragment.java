package com.madmensoftware.www.pops.Fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.braintreepayments.cardform.OnCardFormSubmitListener;
import com.braintreepayments.cardform.view.CardForm;
import com.madmensoftware.www.pops.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ParentCreditCardFormFragment extends Fragment implements OnCardFormSubmitListener {

    @BindView(R.id.parent_bt_card_form) CardForm cardForm;

    private ParentCreditCardFormCallbacks mCallbacks;

    public interface ParentCreditCardFormCallbacks {
        void onParentCreditCardFormSubmit(String cardNumber, String expirationMonth, String expirationYear, String ccv, String postalCode);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
        if (activity instanceof ParentCreditCardFormCallbacks) {
            mCallbacks = (ParentCreditCardFormCallbacks) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ParentCreditCardFormCallbacks) {
            mCallbacks = (ParentCreditCardFormCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onCreditCardFormSubmit");
        }
    }

    public ParentCreditCardFormFragment() {
        // Required empty public constructor
    }

    public static ParentCreditCardFormFragment newInstance() {
        ParentCreditCardFormFragment fragment = new ParentCreditCardFormFragment();
        Log.i("Neighbor:", " ParentCreditCarmForm created.");
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parent_credit_card_form, container, false);
        ButterKnife.bind(this, view);

        cardForm.setRequiredFields(getActivity(), true, true, true, true, "Purchase");
        cardForm.setOnCardFormSubmitListener(this);

        return view;
    }

    @Override
    public void onCardFormSubmit() {
        if (cardForm.isValid()) {
            Toast.makeText(getActivity(), "Awesome!", Toast.LENGTH_SHORT).show();
            String cardNumber = cardForm.getCardNumber();
            String expirationMonth = cardForm.getExpirationMonth();
            String expirationYear = cardForm.getExpirationYear();
            String ccv = cardForm.getCvv();
            String postalCode = cardForm.getPostalCode();
            mCallbacks.onParentCreditCardFormSubmit(cardNumber, expirationMonth, expirationYear, ccv, postalCode);
        }
        else {
            Toast.makeText(getActivity(), "Invalid Payment Info", Toast.LENGTH_SHORT).show();
        }
    }

}
