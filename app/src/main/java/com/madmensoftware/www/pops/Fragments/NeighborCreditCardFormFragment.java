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

/**
 * A simple {@link Fragment} subclass.
 */
public class NeighborCreditCardFormFragment extends Fragment implements OnCardFormSubmitListener {

    private CardForm cardForm;

    private NeighborCreditCardFormCallbacks mCallbacks;

    public interface NeighborCreditCardFormCallbacks {
        void onCreditCardFormSubmit(String cardNumber, String expirationMonth, String expirationYear, String ccv, String postalCode);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
        if (activity instanceof NeighborCreditCardFormFragment.NeighborCreditCardFormCallbacks) {
            mCallbacks = (NeighborCreditCardFormFragment.NeighborCreditCardFormCallbacks) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NeighborCreditCardFormFragment.NeighborCreditCardFormCallbacks) {
            mCallbacks = (NeighborCreditCardFormFragment.NeighborCreditCardFormCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onCreditCardFormSubmit");
        }
    }


    public NeighborCreditCardFormFragment() {
        // Required empty public constructor
    }

    public static NeighborCreditCardFormFragment newInstance() {
        NeighborCreditCardFormFragment fragment = new NeighborCreditCardFormFragment();
        Log.i("Neighbor:", " NeighborCreditCarmForm created.");
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_neighbor_credit_card_form, container, false);

        cardForm = (CardForm) view.findViewById(R.id.bt_card_form);
        cardForm.setRequiredFields(getActivity(), true, true, false, false, "Purchase");

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
            mCallbacks.onCreditCardFormSubmit(cardNumber, expirationMonth, expirationYear, ccv, postalCode);
        }
        else {
            Toast.makeText(getActivity(), "Invalid Payment Info", Toast.LENGTH_SHORT).show();
        }
    }

}
