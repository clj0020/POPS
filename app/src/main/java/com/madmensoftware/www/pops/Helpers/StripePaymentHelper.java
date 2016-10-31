package com.madmensoftware.www.pops.Helpers;

import android.os.AsyncTask;

import com.madmensoftware.www.pops.Models.User;
import com.orhanobut.logger.Logger;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Charge;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by carsonjones on 9/15/16.
 */
//:TODO Create reusable payment class that takes user objects, job object, and price and does different transactions
public class StripePaymentHelper {

    private static final String PUBLISHABLE_TEST_KEY = "pk_test_9SdGQF1ZibEEnbJ3vYmBaAFj";
    private static final String SECRET_TEST_KEY = "sk_test_I9UFP4mZBd3kq6W7w9zDenGq";
    private static final String PUBLISHABLE_LIVE_KEY = "pk_live_KBJbNT9dIp0W1wXzwqJosZBF";
    private static final String SECRET_LIVE_KEY = "sk_live_Fhk9oHkCywcPIO67SQURx8v3";

    public void createCharge(double amount, User neighbor, User parent, double chargeFee,  String chargeDescription) {
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", amount); // Amount in cents
        chargeParams.put("currency", "usd");
        chargeParams.put("customer", neighbor.getStripeCustomerId());
        chargeParams.put("description", chargeDescription);
        chargeParams.put("destination", parent.getStripeAccountId());
        chargeParams.put("application_fee", chargeFee);

        new CreateChargeTask().execute(chargeParams);
    }

    public void onCreateChargeProcessFinish(String result) {
        Logger.d(result);
    }

    private class CreateChargeTask extends AsyncTask<Map<String, Object>, Integer, String> {

        protected String doInBackground(Map<String, Object>... params) {
            String message = "";

            Map<String, Object> chargeParams = params[0];

            Stripe.apiKey = SECRET_TEST_KEY;

            // Create a charge: this will charge the user's card
            try {
                Charge charge = Charge.create(chargeParams);
                message = "Charge successfull!";
            } catch (CardException e) {
                // The card has been declined
                message = "Card declined: " + e.getMessage();
            } catch (APIException e) {
                message = "Stripe API Exception: " + e.getMessage();
            } catch (InvalidRequestException e) {
                message = "Invalid Request Exception: " + e.getMessage();
            } catch (APIConnectionException e) {
                message = "Stripe API Connection Exception: " + e.getMessage();
            } catch (AuthenticationException e) {
                message = "Stripe Authentication Exception: " + e.getMessage();
            }

            return message;
        }

        // This is called each time you call publishProgress()
        protected void onProgressUpdate(Integer... progress) {
        }

        // This is called when doInBackground() is finished
        protected void onPostExecute(String result) {
            onCreateChargeProcessFinish(result);
            super.onPostExecute(result);
        }
    }
}
